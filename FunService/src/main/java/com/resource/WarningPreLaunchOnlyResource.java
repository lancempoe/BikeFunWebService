package com.resource;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;

/**
 * 
 * @author lancepoehler
 *
 */
@Path("/WARNING/CLEAR_AND_RESET_DB")
@Produces(MediaType.APPLICATION_JSON)
public class WarningPreLaunchOnlyResource {

	private static final Logger LOG = Logger.getLogger(WarningPreLaunchOnlyResource.class.getCanonicalName());

	@POST
	public Response getCLEAR_AND_RESET_DB() {

		Response response = null;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. New Location request");

			MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
			MongoCollection bikeRidesCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			MongoCollection locationsCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
			MongoCollection trackingCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);
			MongoCollection anonymousCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.ANONYMOUS_USERS);
			usersCollection.drop();
			bikeRidesCollection.drop();
			locationsCollection.drop();
			trackingCollection.drop();
			anonymousCollection.drop();

			response = Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}
}
