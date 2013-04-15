package com.resource;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.AnonymousUser;
import com.model.DeviceAccount;
import com.model.OAuth;
import com.model.User;
import com.settings.SharedValues;
import com.tools.ImageHelper;
import com.tools.SecurityTools;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.MongoCollection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * See: http://jongo.org
 * 	NOTE: Field selection aka. partial loading is not written as in Mongo shell: 
 * 	Jongo exposes a fields(..) method. A json selector must be provided: 
 * 		{field: 1} to include it, 
 * 		{field: 0} to exclude it.
 * @author lance poehler
 *
 */

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class UsersResource {

    private static final Log LOG = LogFactory.getLog(UsersResource.class);

	/**
	 * Client sends over the device UUID and a 4 digit random number
	 * @param deviceUUID
	 * @return
	 * @throws Exception 
	 */
	@GET
	@Path("/anonymous/{key}/{deviceUUID}")
	public AnonymousUser getAnonymousUser(@PathParam("key") String key, @PathParam("deviceUUID") String deviceUUID) throws Exception {
		AnonymousUser au = null;
		try {
			LOG.info("Received POST XML/JSON Request. AnonymousUser request");

			//check if already an anonymousUser
			MongoCollection auCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.ANONYMOUS_USERS);
			if (auCollection != null && auCollection.count() > 0) {
				au = auCollection.findOne("{deviceAccount.deviceUUID:#}",deviceUUID).as(AnonymousUser.class);
			}

			//Create new if it doesn't exist.
			if (au == null || (!au.deviceAccount.key.equals(key))) {
				au = new AnonymousUser();
				au.deviceAccount.deviceUUID = deviceUUID;
				au.deviceAccount.key = key;
                au.imagePath = getImagePath(au.imagePath);

				LOG.info("AnonymousUser created");
			}

            //Note the use.  Helps us identify active users.
            au.latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();

            //Get the object using Jongo
            auCollection.save(au);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
			throw e;
		}
		return au;
	}

    /**
     * Client must log in prior to call.
     * @param submittedUser
     * @return
     */
    @POST
    public User getUser(User submittedUser) {
        User myUser = null;
        try {
            LOG.info("Received POST XML/JSON Request. User request");

            //Check that the foreign key and token are valid. Return null if not logged in.
            if (!SecurityTools.isLoggedIn(submittedUser)) { return null; }

            //check if already an anonymousUser
            MongoCollection userCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
            if (userCollection != null && userCollection.count() > 0) {
                myUser = userCollection.findOne("{foreignId:#, foreignIdType:#}",submittedUser.oAuth.foreignId, submittedUser.oAuth.foreignIdType).as(User.class);
            }

            //Create new if it doesn't exist.
            if (myUser == null) {
                myUser = new User();
                myUser.deviceAccounts.add(new DeviceAccount());
                myUser.deviceAccounts.get(myUser.deviceAccounts.size()-1).deviceUUID = submittedUser.deviceAccount.deviceUUID;
                myUser.deviceAccounts.get(myUser.deviceAccounts.size()-1).key = submittedUser.deviceAccount.key;
                OAuth oAuth = new OAuth();
                oAuth.foreignId = submittedUser.oAuth.foreignId;
                oAuth.foreignId = submittedUser.oAuth.foreignId;
                oAuth.foreignIdType = submittedUser.oAuth.foreignIdType;
                myUser.oAuth = oAuth;
                myUser.userName = submittedUser.oAuth.foreignId;
                myUser.imagePath = getImagePath(submittedUser.imagePath);

                //Get the object using Jongo
                userCollection.save(myUser);

                LOG.info("User created");
            } else {
                //validate that the uuid is part of this account.
                boolean deviceFound = false;
                for(DeviceAccount myDeviceAccount : myUser.deviceAccounts) {
                    if (myDeviceAccount.deviceUUID == submittedUser.deviceAccount.deviceUUID) {
                        deviceFound = true;
                        break;
                    }
                }
                if (!deviceFound) {
                    myUser.deviceAccounts.add(new DeviceAccount());
                    myUser.deviceAccounts.get(myUser.deviceAccounts.size()-1).deviceUUID = submittedUser.deviceAccount.deviceUUID;
                    myUser.deviceAccounts.get(myUser.deviceAccounts.size()-1).key = submittedUser.deviceAccount.key;
                }
                myUser.latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();

                //Update the object using Jongo
                userCollection.save(myUser);
            }
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
            return null;  //TODO NEED TO UPDATE THE CODE SO THAT ERROR OR THE USER CAN BE RETURNED.
        }
        return myUser;
    }

    @POST
    @Path("update")
    public User updateBikeRide(User user)  {
        User myUser = null;
        try {
            LOG.info("Received POST XML/JSON update Request. User request");
            myUser = changeUser(user, SharedValues.UpdateType.UPDATE_TYPE);
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
            //response = Response.status(Response.Status.PRECONDITION_FAILED).build();
        }
        return myUser;
    }

    private User changeUser(User submittedUser, SharedValues.UpdateType type) {
        User myUser = null;
        try {

            String userId = "";
            boolean validUser = false;

            if (submittedUser.oAuth != null && submittedUser.deviceAccount != null) {
                userId = submittedUser.id;
                validUser = SecurityTools.isLoggedIn(submittedUser) && SecurityTools.isValidUser(userId, submittedUser.deviceAccount.deviceUUID);
            }

            //Validate that the client has access
            if (validUser) {

                //Validate User exist.
                MongoCollection userCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
                if (userCollection != null && userCollection.count() > 0) {
                    myUser = userCollection.findOne("{foreignId:#, foreignIdType:#}",submittedUser.oAuth.foreignId, submittedUser.oAuth.foreignIdType).as(User.class);
                }
                if (myUser == null) { return null; }

                switch (type) {
                    case UPDATE_TYPE:
                        //Update user as requested.
                        if (!submittedUser.email.equals(myUser.email))
                            myUser.email = submittedUser.email;
                        if (!submittedUser.userName.equals(myUser.userName)) {
                            //Validate that the new name is available
                            Iterable<User> users = userCollection
                                    .find()
                                    .fields("{userName: 1}")
                                    .as(User.class);

                            boolean validNewUser = true;
                            for(User user : users) {
                                if (submittedUser.userName.equals(user.userName)) {
                                    validNewUser = false;
                                    break;  //Currently not notifying the user.
                                }
                            }
                            if (validNewUser) {
                                myUser.userName = submittedUser.userName;
                            }
                        }
                        if (!myUser.imagePath.equals(submittedUser.imagePath)) {
                            //Delete Old
                            ImageHelper imageHelper = new ImageHelper();
                            imageHelper.deleteImage(myUser.imagePath);

                            //Update to new image path
                            myUser.imagePath = getImagePath(submittedUser.imagePath);
                        }

                        //Update the object using Jongo
                        userCollection.save(myUser);

                        break;
                }

                //Update the user with updated active timestamp
                myUser.latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
            }  else {
                //TODO NEED TO RETURN IN A DIFFERENT WAY SO CLIENT KNOW WHAT'S UP.
                //response = Response.status(Response.Status.PRECONDITION_FAILED).build();
                return null;
            }
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
        return myUser;
    }

    private String getImagePath(String startingImagePath) {
        String newImagePath = "";
        if (StringUtils.isEmpty(startingImagePath)) {
            newImagePath = ImageResource.UserImageUrl + ImageHelper.defaultUserImage;
        } else {
            int i = startingImagePath.lastIndexOf('.');
            String fileName = UUID.randomUUID() + startingImagePath.substring(i);
            newImagePath = ImageResource.UserImageUrl + fileName;
        }
        return newImagePath;
    }
}