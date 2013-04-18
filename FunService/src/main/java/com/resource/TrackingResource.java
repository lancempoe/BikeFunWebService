package com.resource;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.Tracking;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jongo.MongoCollection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author lancepoehler
 *
 */
@Path("tracking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class TrackingResource {

    private static final Log LOG = LogFactory.getLog(TrackingResource.class);

	@POST
	@Path("new")
	public Response newObject(Tracking tracking) {
		Response response;
		try {
			LOG.info("Received POST XML/JSON Request. New Tracking Object");

			//Get the object using Jongo
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);
			collection.save(tracking);

			response = Response.status(Response.Status.OK).entity(tracking).build();
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
		}
		return response;
	}
}
