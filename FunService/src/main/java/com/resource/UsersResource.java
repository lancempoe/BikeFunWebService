package com.resource;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.AnonymousUser;
import com.model.DeviceAccount;
import com.model.OAuth;
import com.model.User;
import com.settings.SharedStaticValues;
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
import javax.ws.rs.core.Response;
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
public class UsersResource {

    private static final Log LOG = LogFactory.getLog(UsersResource.class);

	/**
	 * Client sends over the device UUID and a 4 digit random number
	 * @param deviceUUID
	 * @return
	 * @throws Exception 
	 */
	@GET
    @OPTIONS
	@Path("/anonymous/{key}/{deviceUUID}")
	public Response getAnonymousUser(@PathParam("key") String key, @PathParam("deviceUUID") String deviceUUID) throws Exception {
        Response response;

        try {
			LOG.info("Received POST XML/JSON Request. AnonymousUser request");
            AnonymousUser au = null;

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

            response = Response.status(Response.Status.OK).entity(au).build();
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
		}
		return response;
	}

    /**
     * Client must log in prior to call.
     * @param submittedUser
     * @return
     */
    @POST
    @Consumes (MediaType.APPLICATION_JSON)
    public Response getUser(User submittedUser) {
        Response response;

        try {
            LOG.info("Received POST XML/JSON Request. User request");
            User myUser = null;

            if (SecurityTools.isLoggedIn(submittedUser)) {

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

                    response = Response.status(Response.Status.OK).entity(myUser).build();
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

                    response = Response.status(Response.Status.OK).entity(myUser).build();
                }
            } else {
                response = Response.status(Response.Status.FORBIDDEN).entity("Error: Not currently logged in.").build();
            }
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
        }
        return response;
    }

    @POST
    @Path("update")
    @Consumes (MediaType.APPLICATION_JSON)
    public Response updateUser(User user)  {
        Response response;
        try {
            LOG.info("Received POST XML/JSON update Request. User request");
            response = changeUser(user, SharedStaticValues.UpdateType.UPDATE_TYPE);
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
        }
        return response;
    }

    private Response changeUser(User submittedUser, SharedStaticValues.UpdateType type) {
        Response response = null;

        try {
            User myUser = null;
            boolean validUser = false;

            if (submittedUser.oAuth != null && submittedUser.deviceAccount != null) {
                validUser = SecurityTools.isLoggedIn(submittedUser) && SecurityTools.isValidUser(submittedUser.id, submittedUser.deviceAccount.deviceUUID);
            }

            //Validate that the client has access
            if (validUser) {

                //Validate User exist.
                MongoCollection userCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
                if (userCollection != null && userCollection.count() > 0) {
                    myUser = userCollection.findOne("{foreignId:#, foreignIdType:#}",submittedUser.oAuth.foreignId, submittedUser.oAuth.foreignIdType).as(User.class);
                }
                if (myUser != null) {

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

                } else {
                    response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: Invalid User").build();
                }

            }  else {
                response = Response.status(Response.Status.FORBIDDEN).entity("Error: Not currently logged in.").build();
            }
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
        }
        return response;
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