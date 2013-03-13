package com.resource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Location;
import com.model.Root;
import com.tools.GeoLocationHelper;
import com.tools.TrackingHelper;

/**
 * 
 * @author lancepoehler
 *
 */
@Path("display/by_time_of_day")
@Produces(MediaType.APPLICATION_JSON)
public class DisplayByTimeResource {

	private static final Logger LOG = Logger.getLogger(DisplayByTimeResource.class.getCanonicalName());

	@GET
	@Path("geoloc={latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}")
	public Root getDisplay(@PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude) throws Exception {
		if (!GeoLocationHelper.isValidGeoLoc(latitude, longitude)) { return null; }

		GeoLoc geoLoc = new GeoLoc();
		geoLoc.latitude = latitude;
		geoLoc.longitude = longitude;
		return getDisplay(geoLoc, null);
	}

	/**
	 * Main Display Sorted by Time
	 * - Cons: many calls to the DB
	 * - Pros: Returns everything in one JSON
	 *   
	 *   TODO: I need to investigate utilizing additional indexes
	 *   - http://docs.mongodb.org/manual/applications/indexes/
	 *   
	 *   TODO: 	might want to create a ttl index to remove expired data... or keep and backup:
	 *   - http://docs.mongodb.org/manual/tutorial/expire-data/
	 *   
	 * - Currently the Data Modeling Decision is to use References instead of embedding them.
	 *   The main reason for this is that Location and user would be replicate all over the place.
	 *   If we find that the response time is an issue we can migrate to an embedded approach.
	 *   See: http://docs.mongodb.org/manual/core/data-modeling/
	 * @param geoLoc
	 * @param selectedLocation
	 * @return
	 * @throws Exception 
	 */
	private Root getDisplay(GeoLoc geoLoc, String selectedLocationId) throws Exception {

		Root root = new Root();
		try 
		{
			//**(Identify the closest city to the client with an upcoming ride: 2 DB Calls)**
			//Query all BikeRide.LocationId for rides starting yesterday through the future.  
			//We start with yesterday so that Rides that start at 11PM and end at 1AM can still be seen that night.
			//Currently there is no end time.  That can change when we need. (To add ending time: dateTime.plusDays(1))
			DateTime todayDateTime = new DateTime().withZone(DateTimeZone.UTC).toDateMidnight().toDateTime(); // Joda time
			Long yesterday = todayDateTime.minusDays(1).getMillis();  //
			MongoCollection bikeCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			Iterable<String> all = bikeCollection
					.distinct("CityLocationId")
					.query("{RideStartTime: {$gt: #}}", 
							yesterday)
							.as(String.class);
			ArrayList<ObjectId> locationIds = new ArrayList<ObjectId>();
			for(String locationId : all) {
				locationIds.add(new ObjectId(locationId));
			}

			Location closestLocation;
			MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS, "GeoLoc");
			//coll.ensureIndex("{GeoLoc: '2d'}") is set when getting the collection
			closestLocation = locationCollection
					.findOne("{GeoLoc: {$near: [#, #]}, _id: {$in:#}}", 
							geoLoc.longitude,
							geoLoc.latitude,
							locationIds)
							.as(Location.class);
			root.ClosestLocation = closestLocation;

			//**(Identify the upcoming bike rides for the selected city: 1 DB Call)**
			//Find all bike rides for the selected city (if user has default it may not be in the list of locations available.  different ways to display on the UI)
			Iterable<BikeRide> bikeRides = bikeCollection
					.find("{RideStartTime: {$gt: #}, CityLocationId: #}", 
							yesterday, 
							root.ClosestLocation.getId())
							.sort("{RideStartTime : 1}")
							.limit(200)
							.as(BikeRide.class);
			root.BikeRides = Lists.newArrayList(bikeRides);

			//**(Set tracking on bike rides: 2 DB call)
			TrackingHelper.setTracking(root.BikeRides, geoLoc);

		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception Error: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		//**(Return Root)**
		return root;
	}
}