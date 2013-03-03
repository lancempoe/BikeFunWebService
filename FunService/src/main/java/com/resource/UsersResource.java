package com.resource;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.Root;
import com.model.User;

/**
 * See: http://jongo.org
 * 	NOTE: Field selection aka. partial loading is not written as in Mongo shell: 
 * 	Jongo exposes a fields(..) method. A json selector must be provided: 
 * 		{field: 1} to include it, 
 * 		{field: 0} to exclude it.
 * @author lance poehler
 *
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class UsersResource {

	private static final Logger LOG = Logger.getLogger(UsersResource.class.getCanonicalName());

	@POST
	@Path("new")
	public Response newUser(User user) {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. New User request");

			//Get the object using Jongo
			Jongo jongo = new Jongo(MongoDatabase.Get_DB());
			MongoCollection usersCollection = jongo.getCollection(MONGO_COLLECTIONS.USERS.name());
			usersCollection.save(user);
			
			//TODO SEND OUT A EMAIL FOR THE NEW USER

			response = Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}

	@POST
	@Path("{userId}/update")
	public Response updateUser(User user) {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. Update User request");

			//TODO We need to understand what we will allow the service to update.
			//Then only up the fields we want to update.

			//Get the object using Jongo
			Jongo jongo = new Jongo(MongoDatabase.Get_DB());
			MongoCollection usersCollection = jongo.getCollection(MONGO_COLLECTIONS.USERS.name());
			usersCollection.save(user);

			//TODO SEND OUT A EMAIL FOR THE updates.  Do we want to do this?  maybe..

			response = Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}

	@DELETE
	@Path("{userId}/delete")
	public Response deleteUser(@PathParam("userId") String userId) throws Exception {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. Delete User request: \"" + userId + "\"");

			//Delete the user using Jongo
			Jongo jongo = new Jongo(MongoDatabase.Get_DB());
			MongoCollection usersCollection = jongo.getCollection(MONGO_COLLECTIONS.USERS.name());
			usersCollection.remove(new ObjectId(userId));

			//TODO SEND OUT EMAIL TO LET THE USER KNOW

			response = Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}

	@GET
	@Path("{userId}")
	public User getUser(@PathParam("userId") String userId) throws Exception {
		User user = null;
		try 
		{			
			//Get the object using Jongo
			Jongo jongo = new Jongo(MongoDatabase.Get_DB());
			MongoCollection usersCollection = jongo.getCollection(MONGO_COLLECTIONS.USERS.name());
			user = usersCollection.findOne(new ObjectId(userId)).fields("{Password: 0}").as(User.class);
		}
		catch (Exception e)
		{
			LOG.log(Level.INFO, "Exception Error when getting user: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} 
		return user;
	}

	@GET
	public Root getUsers() throws Exception {
		Root root = null;
		try 
		{
			//Get the objects using Jongo
			Jongo jongo = new Jongo(MongoDatabase.Get_DB());
			MongoCollection usersCollection = jongo.getCollection(MONGO_COLLECTIONS.USERS.name());
			Iterable<User> all = usersCollection.find().fields("{Password: 0}").as(User.class);		
			ArrayList<User> userList = Lists.newArrayList(all);
			root = new Root();
			root.Users = userList;
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception Error: " + e.getMessage());
			e.printStackTrace();
		} 
		return root;
	}
}
