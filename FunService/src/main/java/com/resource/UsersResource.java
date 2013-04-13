package com.resource;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.AnonymousUser;
import com.model.DeviceAccounts;
import com.model.ForeignIdType;
import com.model.User;
import com.tools.ImageHelper;
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
	 * TODO: This security should be improved.  Maybe we could have a new key generated each and every time the app starts.
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
				au = auCollection.findOne("{deviceAccounts.deviceUUID:#}",deviceUUID).as(AnonymousUser.class);
			}

			//Create new if it doesn't exist.
			if (au == null || (!au.deviceAccounts.key.equals(key))) {
				au = new AnonymousUser();
				au.deviceAccounts.deviceUUID = deviceUUID;
				au.deviceAccounts.key = key;
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

    @POST
    @Path("users")
    public User getUser(User submittedUser) {
        User myUser = null;
        try {
            LOG.info("Received POST XML/JSON Request. User request");

            //Check that the foreign key and token are valid. Return null if not logged in.
            if (!isLoggedIn(submittedUser)) { return null; }

            //check if already an anonymousUser
            MongoCollection userCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
            if (userCollection != null && userCollection.count() > 0) {
                myUser = userCollection.findOne("{foreignId:#, foreignIdType:#}",submittedUser.foreignId, submittedUser.foreignIdType).as(User.class);
            }

            //Create new if it doesn't exist.
            if (myUser == null) {
                myUser = new User();
                myUser.deviceAccounts.add(new DeviceAccounts());
                myUser.deviceAccounts.get(myUser.deviceAccounts.size()-1).deviceUUID = submittedUser.deviceAccount.deviceUUID;
                myUser.deviceAccounts.get(myUser.deviceAccounts.size()-1).key = submittedUser.deviceAccount.key;
                myUser.foreignId = submittedUser.foreignId;
                myUser.foreignIdType = submittedUser.foreignIdType;
                myUser.userName = submittedUser.foreignId;
                myUser.imagePath = getImagePath(submittedUser.imagePath);

                //Get the object using Jongo
                userCollection.save(myUser);

                LOG.info("User created");
            } else {
                //validate that the uuid is part of this account.
                boolean deviceFound = false;
                for(DeviceAccounts myDeviceAccounts : myUser.deviceAccounts) {
                    if (myDeviceAccounts.deviceUUID == submittedUser.deviceAccount.deviceUUID) {
                        deviceFound = true;
                        break;
                    }
                }
                if (!deviceFound) {
                    myUser.deviceAccounts.add(new DeviceAccounts());
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
        }
        return myUser;
    }

    @POST
    @Path("users/update")
    public User updateUser(User submittedUser) {
        User myUser = null;
        try {
            LOG.info("Received POST XML/JSON update Request. User request");

            //Check that the foreign key and token are valid. Return null if not logged in.
            if (!isLoggedIn(submittedUser)) { return null; }

            //Validate User exist.
            MongoCollection userCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
            if (userCollection != null && userCollection.count() > 0) {
                myUser = userCollection.findOne("{foreignId:#, foreignIdType:#}",submittedUser.foreignId, submittedUser.foreignIdType).as(User.class);
            }
            if (myUser == null) { return null; }

            //Update user as requested.
            if (!submittedUser.email.equals(myUser.email))
                myUser.email = submittedUser.email;
            if (!submittedUser.userName.equals(myUser.userName)) {
                //Validate that the new name is available
                Iterable<User> users = userCollection
                        .find()
                        .fields("{userName: 1}")
                        .as(User.class);

                boolean validUser = true;
                for(User user : users) {
                    if (submittedUser.userName.equals(user.userName)) {
                        validUser = false;
                        break;  //Currently not notifying the user.
                    }
                }
                if (validUser) {
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
            myUser.latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();

            //Update the object using Jongo
            userCollection.save(myUser);

        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
        return myUser;
    }

    private boolean isLoggedIn(User submittedUser) {
        boolean loggedIn = false;
        try {
            if (submittedUser != null ||
                    !StringUtils.isEmpty(submittedUser.foreignId)  &&
                    !StringUtils.isEmpty(submittedUser.foreignIdType) &&
                    !StringUtils.isEmpty(submittedUser.deviceAccount.deviceUUID)) {

                switch (ForeignIdType.fromName(submittedUser.foreignIdType)) {
                    case FaceBook:

                        break;
                    case Google:

                        break;
                }
            }
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
        return loggedIn;
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