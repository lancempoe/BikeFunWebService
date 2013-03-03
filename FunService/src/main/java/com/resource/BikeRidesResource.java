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
import com.model.BikeRide;
import com.model.Root;

/**
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
	public Response neBikeRide(BikeRide bikeRide) {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. New BikeRide request");

			//Get the object using Jongo
			Jongo jongo = new Jongo(MongoDatabase.Get_DB());
			MongoCollection collection = jongo.getCollection(MONGO_COLLECTIONS.BIKERIDES.name());
			collection.save(bikeRide);

			//TODO SEND OUT A EMAIL FOR THE NEW bike ride to the owner?? Do we want this?

			response = Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}

	@POST
	@Path("{id}/update")
	public Response updateUser(BikeRide bikeRide) {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. Update BikeRide request");

			//TODO We need to understand what we will allow the service to update.
			//Then only up the fields we want to update.

			//Get the object using Jongo
			Jongo jongo = new Jongo(MongoDatabase.Get_DB());
			MongoCollection collection = jongo.getCollection(MONGO_COLLECTIONS.BIKERIDES.name());
			collection.save(bikeRide);

			//TODO SEND OUT A EMAIL FOR THE updates.  Do we want to do this?  how about people watching the event??

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
	public Response deleteUser(@PathParam("id") String id) throws Exception {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. Delete BikeRide request: \"" + id + "\"");

			//Delete the user using Jongo
			Jongo jongo = new Jongo(MongoDatabase.Get_DB());
			MongoCollection collection = jongo.getCollection(MONGO_COLLECTIONS.BIKERIDES.name());
			collection.remove(new ObjectId(id));

			//TODO SEND OUT EMAIL TO LET THE leader KNOW... maybe??

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
	public BikeRide getUser(@PathParam("id") String id) throws Exception {
		BikeRide bikeRide = null;
		try 
		{			
			//Get the object using Jongo
			Jongo jongo = new Jongo(MongoDatabase.Get_DB());
			MongoCollection collection = jongo.getCollection(MONGO_COLLECTIONS.BIKERIDES.name());
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
			Jongo jongo = new Jongo(MongoDatabase.Get_DB());
			MongoCollection collection = jongo.getCollection(MONGO_COLLECTIONS.BIKERIDES.name());
			Iterable<BikeRide> all = collection.find().fields("{_id: 1, BikeRideName:1, TargetAudience:1, ImagePath:1").as(BikeRide.class);		
			ArrayList<BikeRide> bikeRideList = Lists.newArrayList(all);
			root = new Root();
			root.BikeRides = bikeRideList;
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception Error: " + e.getMessage());
			e.printStackTrace();
		} 
		return root;
	}
	
	@GET
	@Path("sortBydistance/{longitude},{latitude}/{maxDistance}")
	public Root getBikeRidesSortedByDistance(@PathParam("longitude") Double longitude, 
												@PathParam("latitude") Double latitude, 
												@PathParam("maxDistance") Double maxDistance) throws Exception {
		if (maxDistance <= 0) { throw new Exception("Positive Max Distance only"); }
		
		return SortedByDistance("{address: {$near: ["+longitude+", "+latitude+"], $maxDistance: "+maxDistance+"}}");
	}
	
	@GET
	@Path("sortBydistance/{longitude},{latitude}")
	public Root getBikeRidesSortedByDistance(@PathParam("longitude") Double longitude, @PathParam("latitude") Double latitude) throws Exception {
		return SortedByDistance("{address: {$near: ["+longitude+", "+latitude+"]");
	}
	
	/**
	 * http://docs.mongodb.org/manual/core/geospatial-indexes/
	 * - http://jongo.org
	 * @return
	 * @throws Exception
	 */
	private Root SortedByDistance(String query) {
		Root root = null;
		try 
		{
			//Get the objects using Jongo
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES, "GeoLoc");
			Iterable<BikeRide> all = collection.find(query).as(BikeRide.class);
			ArrayList<BikeRide> bikeRideList = Lists.newArrayList(all);
			root = new Root();
			root.BikeRides = bikeRideList;
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception Error: " + e.getMessage());
			e.printStackTrace();
		} 
		return root;
	}
}
