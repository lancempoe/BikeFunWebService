package com.resource;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.Location;
import com.tools.GoogleGeocoderApiHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * See: Jongo: http://jongo.org
 * 
 * See: The Google Geocoding API: https://developers.google.com/maps/documentation/geocoding/
 * 		http://code.google.com/p/geocoder-java/
 * Visual Tool: http://gmaps-samples-v3.googlecode.com/svn/trunk/geocoder/v3-geocoder-tool.html
 * @author lance poehler
 *
 */
@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class LocationResource {
    private static final Log LOG = LogFactory.getLog(LocationResource.class);


	@POST
	@Path("new")
	public Response newLocation(Location location) {
		Response response;
		try {
			LOG.info("Received POST XML/JSON Request. New Location request");

			if (GoogleGeocoderApiHelper.setGeoLocation(location))
			{
				//Save the object using Jongo
				MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
				collection.save(location);
                response = Response.status(Response.Status.OK).entity(location).build();
			} else {
                response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: Invalid Address").build();
			}
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
		}
		return response;
	}

	@GET
	@Path("{id}")
	public Response getLocation(@PathParam("id") String id) throws Exception {
        Response response;
		try {
            Location location = null;

			//Get the object using Jongo
			MongoCollection coll = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
			location = coll.findOne(new ObjectId(id)).as(Location.class);

            response = Response.status(Response.Status.OK).entity(location).build();
		}
		catch (Exception e) {
			LOG.error("Exception Error when getting user: ", e);
			e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
		} 
		return response;
	}

	@GET
	public Response getLocations() throws Exception {
		Response response;
        try
		{
		    List<Location> locations = null;

			//Get the objects using Jongo
			MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
			Iterable<Location> all = usersCollection.find().as(Location.class);		
			locations = Lists.newArrayList(all);

            response = Response.status(Response.Status.OK).entity(locations).build();
		}
		catch (Exception e)
		{
			LOG.error("Exception Error: ", e);
			e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
		} 
		return response;
	}
}
