package com.resource;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.Tracking;

@Path("tracking")
@Consumes (MediaType.APPLICATION_JSON)
public class TrackingResource {

	private static final Logger LOG = Logger.getLogger(LocationResource.class.getCanonicalName());

	@POST
	@Path("new")
	public Response newObject(Tracking tracking) {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. New Tracking Object");

			//Get the object using Jongo
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);
			collection.save(tracking);

			response = Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}
}
