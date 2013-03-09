package com.resource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.model.Tracking;
import com.tools.GeoLocationHelper;

@Path("display/by_time_of_day")
@Produces(MediaType.APPLICATION_JSON)
public class DisplayByTimeResource {

	private static final Logger LOG = Logger.getLogger(DisplayByTimeResource.class.getCanonicalName());

//	@GET
//	@Path("{selectedLocationId}/")
//	public Root getDisplaySelectedLocation(GeoLoc geoLoc, @PathParam("selectedLocationId") String selectedLocationId) throws Exception {
//		return getDisplay(geoLoc, selectedLocationId);
//	}

	@GET
	@Path("proximity/{latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}")
	public Root getDisplay(@PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude) throws Exception {
		if (longitude.compareTo(new BigDecimal(180)) > 0 ||
			longitude.compareTo(new BigDecimal(-180)) < 0 ||
			latitude.compareTo(new BigDecimal(90)) > 0 ||
			latitude.compareTo(new BigDecimal(-90)) < 0) {
			return null;
		}
		
		GeoLoc geoLoc = new GeoLoc();
		geoLoc.latitude = latitude;
		geoLoc.longitude = longitude;
		return getDisplay(geoLoc, null);
	}

	/**
	 * Main Display Sorted by Time
	 * - Cons: 5 calls to the DB
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
			//**(Results for City drop down field: 2 DB Calls )**
			//Query all BikeRide.LocationId for rides starting yesterday through the future.  
			//We start with yesterday so that Rides that start at 11PM and end at 1AM can still be seen that night.
			//Currently there is no end time.  That can change when we need. (To add ending time: dateTime.plusDays(1))
			DateTime todayDateTime = new DateTime().withZone(DateTimeZone.UTC).toDateMidnight().toDateTime(); // Joda time
			Date yesterday = todayDateTime.minusDays(1).toDate();  //
			MongoCollection bikeCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			Iterable<String> all = bikeCollection
					.distinct("CityLocationId")
					.query("{StartTime: {$gt: #}}", yesterday)
					.as(String.class);
			ArrayList<ObjectId> locationIds = new ArrayList<ObjectId>();
			for(String locationId : all) {
				locationIds.add(new ObjectId(locationId));
			}
			//Query for Locations based on the returned LocationIds
			MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS, true);
			Iterable<Location> locations = locationCollection
					.find("{_id: {$in:#}}", locationIds)
					.sort("{State : 1, City : 1}")
					.as(Location.class);
			root.Locations = Lists.newArrayList(locations);

			//**(Identify the closest city to the client with an upcoming ride: 1 DB Call)**
			Location closestLocation;
			//coll.ensureIndex("{GeoLoc: '2d'}") is set when getting the collection
			closestLocation = locationCollection
					.findOne("{GeoLoc: {$near: ["+geoLoc.longitude+", "+geoLoc.latitude+"]}, _id: {$in:#}}", locationIds)
					.as(Location.class);
			root.ClosestLocation = closestLocation;

			//**(Identify the client selected city: 1 DB Call)**
			Location selectedLocation = null;
			if (selectedLocationId != null) {
				selectedLocation = locationCollection
						.findOne(new ObjectId(selectedLocationId))
						.as(Location.class);
			}
			root.SelectedLocation = selectedLocation; 
			//Set the Selected Location in the event that client has not made a choice
			if (root.SelectedLocation == null) { root.SelectedLocation = root.ClosestLocation; }

			//**(Identify the upcoming bike rides for the selected city: 1 DB Call)**
			//Find all bike rides for the selected city (if user has default it may not be in the list of locations available.  different ways to display on the UI)
			Iterable<BikeRide> bikeRides = bikeCollection
					.find("{StartTime: {$gt: #}, CityLocationId: #}", yesterday, root.SelectedLocation.getId())
					.sort("{StartTime : 1}")
					.limit(200)
					.as(BikeRide.class);
			root.BikeRides = Lists.newArrayList(bikeRides);

			//Find and set:
			//1) the distance between the client and the ride. 
			//2) Most recent Tracking
			//3) Tracking Count
			MongoCollection trackingCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);
			for(BikeRide bikeRide : root.BikeRides) {
				bikeRide.setDistanceFromClient(GeoLocationHelper.distFrom(bikeRide.getLocation().getGeoLoc(), geoLoc)); //TODO REPLACE ONCE JONGO .4 IS USED.
				
				Iterable<Tracking> trackings = trackingCollection.find("{BikeRideId:#}", bikeRide.getId()).sort("{TrackingTime: -1}").limit(1).as(Tracking.class);
				List<Tracking> trackingList = Lists.newArrayList(trackings);
				if (trackingList != null && trackingList.size() == 1) {
					bikeRide.setMostRecentTracking(trackingList.get(0).getTrackingTime());
				}
				
				bikeRide.setTotalPeopleTrackingCount(trackingCollection.count("{BikeRideId:#}", bikeRide.getId()));
			}
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
