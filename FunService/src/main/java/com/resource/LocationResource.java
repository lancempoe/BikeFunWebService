package com.resource;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.Location;
import com.tools.GeoLocationHelper;

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

	private static final Logger LOG = Logger.getLogger(LocationResource.class.getCanonicalName());

	@POST
	@Path("new")
	public Response newLocation(Location location) {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. New Location request");

			if (GeoLocationHelper.setGeoLocation(location))
			{
				//Save the object using Jongo
				MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
				collection.save(location);
				response = Response.status(Response.Status.OK).build();
			} else {
				response = Response.status(Response.Status.PRECONDITION_FAILED).build();
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}

	@GET
	@Path("{id}")
	public Location getLocation(@PathParam("id") String id) throws Exception {
		Location location = null;
		try 
		{			
			//Get the object using Jongo
			MongoCollection coll = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
			location = coll.findOne(new ObjectId(id)).as(Location.class);
		}
		catch (Exception e)
		{
			LOG.log(Level.INFO, "Exception Error when getting user: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} 
		return location;
	}

	@GET
	public List<Location> getLocations() throws Exception {
		List<Location> locations = null;
		try 
		{
			//Get the objects using Jongo
			MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
			Iterable<Location> all = usersCollection.find().as(Location.class);		
			locations = Lists.newArrayList(all);
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception Error: " + e.getMessage());
			e.printStackTrace();
		} 
		return locations;
	}
}
