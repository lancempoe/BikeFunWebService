package com.resource;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Location;
import com.model.Root;
import com.tools.GoogleGeocoderApiHelper;
import com.tools.TrackingHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.MongoCollection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @author lancepoehler
 *
 */
@Path("display/by_proximity")
@Produces(MediaType.APPLICATION_JSON)
public class DisplayByProximityResource {

    private static final Log LOG = LogFactory.getLog(DisplayByProximityResource.class);

	private static final Double ONE_DEGREE_IN_MILES = 69.11; //1� of latitude = about 69.11 miles.
	private static final int RADIUS_IN_MILES = 3;
	private static final int TIME_IN_MINUTES = 60;

	@GET
	@Path("geoloc={latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}")
	public Root getDisplay(@PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude) throws Exception {
		if (!GoogleGeocoderApiHelper.isValidGeoLoc(latitude, longitude)) { return null; }

		GeoLoc geoLoc = new GeoLoc();
		geoLoc.latitude = latitude;
		geoLoc.longitude = longitude;
		return getDisplay(geoLoc);
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
	 *   If we find that the response time is an issue we can migrate to an embedded approach.
	 *   See: http://docs.mongodb.org/manual/core/data-modeling/
	 * @param geoLoc
	 * @param selectedLocation
	 * @return
	 * @throws Exception 
	 */
	private Root getDisplay(GeoLoc geoLoc) throws Exception {

		Root root = new Root();
		try 
		{
			//**(Identify the closest city to the client: 1 DB Call)**
			MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS, "geoLoc");
			//coll.ensureIndex("{geoLoc: '2d'}") is set when getting the collection
			Location closestLocation = locationCollection
					.findOne("{geoLoc: {$near: [#, #]}}", 
							geoLoc.longitude, 
							geoLoc.latitude)
					.as(Location.class);
			root.ClosestLocation = closestLocation;

			//**(Get BikeRide list: 3 calls to the DB)**
			DateTime nowDateTime = new DateTime(DateTimeZone.UTC); // Joda time
			DateTime maxStartTime = nowDateTime.plusMinutes(TIME_IN_MINUTES);
			DateTime minStartTime = nowDateTime.minusDays(1); //This will cut out most old bike rides
			Long now = nowDateTime.toInstant().getMillis();
			Long max = maxStartTime.toInstant().getMillis();
			Long min = minStartTime.toInstant().getMillis();

			//Get the objects using Jongo
			MongoCollection bikeRidesCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES, "location.geoLoc");
			//Currently not placing a limit on this result.  If we find this is an issue we can add later.

			bikeRidesCollection.ensureIndex("{location.geoLoc: '2d', rideStartTime: 1}");
			Iterable<BikeRide> all = bikeRidesCollection
					.find("{location.geoLoc: {$near: [#, #], $maxDistance: #}, rideStartTime: {$lte: #, $gte: #}}",
							geoLoc.longitude,
							geoLoc.latitude,
							RADIUS_IN_MILES/ONE_DEGREE_IN_MILES,
							max ,
							min )
					.fields("{cityLocationId: 0, rideLeaderId: 0, details: 0}") //TODO once we narrow down the UI we can cut down data further.		
					.as(BikeRide.class);
			List<BikeRide> closeBikeRides = Lists.newArrayList(all);

			//Two calls to the DB
			TrackingHelper.setTracking(closeBikeRides, geoLoc);

			for(BikeRide closeBikeRide : closeBikeRides) {
				//Find all rides that haven't started AND find all bike rides still being tracked.
				if (closeBikeRide.rideStartTime > now || closeBikeRide.currentlyTracking) {
					root.BikeRides.add(closeBikeRide);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LOG.error("DisplayByProximity Failed", e);
		}

		//**(Return Root)**
		return root;
	}
}
