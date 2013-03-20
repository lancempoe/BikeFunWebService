package com.resource;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.AnonymousUser;
import com.model.DeviceAccounts;

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

	private static final Logger LOG = Logger.getLogger(UsersResource.class.getCanonicalName());

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
			LOG.log(Level.FINE, "Received POST XML/JSON Request. AnonymousUser request");

			//check if already an anonymousUser
			MongoCollection auCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.ANONYMOUS_USERS);
			if (auCollection != null && auCollection.count() > 0) {
				au = auCollection.findOne("{deviceAccounts.deviceUUID:#}",deviceUUID).as(AnonymousUser.class);
			}

			//Create new if it doesn't exist.
			if (au == null || (!au.deviceAccounts.key.equals(key))) {
				au = new AnonymousUser();
				au.deviceAccounts = new DeviceAccounts();
				au.deviceAccounts.deviceUUID = deviceUUID;
				au.deviceAccounts.key = key;

				//Get the object using Jongo
				MongoCollection anonymousUsersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.ANONYMOUS_USERS);
				anonymousUsersCollection.save(au);
				LOG.log(Level.FINE, "AnonymousUser created");
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return au;
	}

	//	@GET
	//	@Path("/users/{id}/{provider}/{key}/{externalId}/{deviceUUID}")
	//	public User getUser(
	//			@PathParam("id") String id, 
	//			@PathParam("provider") String provider, 
	//			@PathParam("key") String key, 
	//			@PathParam("externalId") String externalId,
	//			@PathParam("deviceUUID") String deviceUUID) {
	//		User myUser = null;
	//		try {
	//			LOG.log(Level.FINEST, "Received POST XML/JSON Request. User request");
	//
	//			//TODO
	//			//CHECK THE KEY WITH THE provider
	//			//if invalid then return null
	//			
	//			//check if already an user
	//			MongoCollection auCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
	//			myUser = auCollection.findOne(new ObjectId(id)).as(User.class);
	//			
	//			if (myUser != null) {
	//				//Check if deviceUUID is new.
	//				//add if needed
	//			} else {
	//				
	//				myUser = new User();
	//				myUser.deviceUUIDs = Arrays.asList( deviceUUID );
	//				
	//				//Get the object using Jongo
	//				MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
	//				usersCollection.save(myUser);
	//				myUser = user;
	//				LOG.log(Level.FINE, "User created");
	//			}
	//		} catch (Exception e) {
	//			LOG.log(Level.SEVERE,  e.getMessage());
	//			e.printStackTrace();
	//		}
	//		return myUser;
	//	}
	//
	//	@POST
	//	@Path("/users/update/{key}/{deviceUUID}")
	//	public User updateUser(@PathParam("key") String key, @PathParam("deviceUUID") String deviceUUID) {
	//		Response response;
	//		try {
	//			LOG.log(Level.FINEST, "Received POST XML/JSON Request. Update User request");
	//			
	//			//Get the object using Jongo
	//			MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
	//			usersCollection.save(user);
	//
	//			response = Response.status(Response.Status.OK).build();
	//		} catch (Exception e) {
	//			LOG.log(Level.SEVERE,  e.getMessage());
	//			e.printStackTrace();
	//			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
	//		}
	//		return response;
	//	}
	//
	//	@DELETE
	//	@Path("{userId}/delete")
	//	public Response deleteUser(@PathParam("userId") String userId) throws Exception {
	//		Response response;
	//		try {
	//			LOG.log(Level.FINEST, "Received POST XML/JSON Request. Delete User request: \"" + userId + "\"");
	//
	//			//Delete the user using Jongo
	//			MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
	//			usersCollection.remove(new ObjectId(userId));
	//
	//			//TODO SEND OUT EMAIL TO LET THE USER KNOW
	//
	//			response = Response.status(Response.Status.OK).build();
	//		} catch (Exception e) {
	//			LOG.log(Level.SEVERE,  e.getMessage());
	//			e.printStackTrace();
	//			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
	//		}
	//		return response;
	//	}
	//
	//	@GET
	//	@Path("{userId}")
	//	public User getUser(@PathParam("userId") String userId) throws Exception {
	//		User user = null;
	//		try 
	//		{			
	//			//Get the object using Jongo
	//			MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
	//			user = usersCollection.findOne(new ObjectId(userId)).fields("{Password: 0}").as(User.class);
	//		}
	//		catch (Exception e)
	//		{
	//			LOG.log(Level.INFO, "Exception Error when getting user: " + e.getMessage());
	//			e.printStackTrace();
	//			throw e;
	//		} 
	//		return user;
	//	}
}
