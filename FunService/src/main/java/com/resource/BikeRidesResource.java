package com.resource;

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
import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Location;
import com.model.Root;
import com.tools.GeoLocationHelper;

/**
 * Mongo with Jongo!
 * 
 * See: http://jongo.org
 * 	NOTE: Field selection aka. partial loading is not written as in Mongo shell: 
 * 	Jongo exposes a fields(..) method. A json selector must be provided: 
 * 		{field: 1} to include it, 
 * 		{field: 0} to exclude it.
 * @author lance poehler
 *
 */
@Path("/bikerides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class BikeRidesResource {

	private static final Logger LOG = Logger.getLogger(BikeRidesResource.class.getCanonicalName());

	@POST
	@Path("new")
	public Response newBikeRide(BikeRide bikeRide) throws Exception {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. New BikeRide request");

			//Validate real address:
			if (GeoLocationHelper.setGeoLocation(bikeRide.getLocation())) { //Call API for ride geoCodes

				//Check is current city exist
				MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
				Location location = locationCollection
						.findOne("{City:#, State:#, Country:#}", 
								bikeRide.getLocation().getCity(), bikeRide.getLocation().getState(), bikeRide.getLocation().getCountry())
						.as(Location.class);
				if (location == null) {
					//Add new location to the DB
					location = new Location();
					location.setCity(bikeRide.getLocation().getCity());
					location.setState(bikeRide.getLocation().getState());
					location.setCountry(bikeRide.getLocation().getCountry());
					GeoLocationHelper.setGeoLocation(location); //Call API for city center geoCode
					MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
					collection.save(location);
				}
				bikeRide.setCityLocationId(location.getId());

				//Get the object using Jongo
				MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
				collection.save(bikeRide);

				//TODO SEND OUT A EMAIL FOR THE NEW bike ride to the owner?? 

				response = Response.status(Response.Status.OK).build();
			} else { 
				//Invalid address
				response = Response.status(Response.Status.CONFLICT).build();
			}

		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
			throw e;
		}
		return response;
	}

	@POST
	@Path("{id}/update")
	public Response updateBikeRide(BikeRide bikeRide) {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. Update BikeRide request");

			//TODO We need to understand what we will allow the service to update.
			//Then only up the fields we want to update.

			//Get the object using Jongo
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			collection.save(bikeRide);

			response = Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}

	@DELETE
	@Path("{id}/delete")
	public Response deleteBikeRide(@PathParam("id") String id) throws Exception {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. Delete BikeRide request: \"" + id + "\"");

			//Delete the user using Jongo
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			collection.remove(new ObjectId(id));

			response = Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}

	@GET
	@Path("{id}")
	public BikeRide getBikeRide(@PathParam("id") String id) throws Exception {
		BikeRide bikeRide = null;
		try 
		{			
			//Get the object using Jongo
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			bikeRide = collection.findOne(new ObjectId(id)).as(BikeRide.class);
		}
		catch (Exception e)
		{
			LOG.log(Level.INFO, "Exception Error when getting user: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} 
		return bikeRide;
	}

	@GET
	public Root getBikeRides() throws Exception {
		Root root = null;
		try 
		{
			//Get the objects using Jongo
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			Iterable<BikeRide> all = collection.find().fields("{_id: 1, BikeRideName: 1, TargetAudience: 1, ImagePath: 1}").as(BikeRide.class);
			root = new Root();
			root.BikeRides = Lists.newArrayList(all);
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception Error: " + e.getMessage());
			e.printStackTrace();
			throw e;
		} 
		return root;
	}

	@POST
	@Path("sortBydistance/{maxDistance}")
	public Root getBikeRidesSortedByDistance(GeoLoc geoLoc, @PathParam("maxDistance") Double maxDistance) throws Exception {
		if (maxDistance <= 0) { throw new Exception("Positive Max Distance only"); }
		Root root = new Root();
		//Get the objects using Jongo
		MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES, "Location.GeoLoc");
		Iterable<BikeRide> all = collection
				.find("{Location.GeoLoc: {$near: [#, #], $maxDistance: #}}",
						geoLoc.longitude,
						geoLoc.latitude,
						maxDistance)
						.as(BikeRide.class);
		root.BikeRides = Lists.newArrayList(all);
		return root;
	}

	@POST
	@Path("sortBydistance")
	public Root getBikeRidesSortedByDistance(GeoLoc geoLoc) throws Exception {
		Root root = new Root();
		//Get the objects using Jongo
		MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES, "Location.GeoLoc");
		Iterable<BikeRide> all = collection
				.find("{Location.GeoLoc: {$near: [#, #]}}",
						geoLoc.longitude,
						geoLoc.latitude)
						.as(BikeRide.class);
		root.BikeRides = Lists.newArrayList(all);
		return root;
	}

}
