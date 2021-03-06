package com.tools;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Location;
import com.settings.SharedStaticValues;
import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared classes for bike ride calls.
 * @author lancepoehler
 *
 */
public class CommonBikeRideCalls {

    private static final int RADIUS_IN_MILES = 60; //This is the max distance that we will pull for active rides.

	/**
	 * Identify the closest city to the client with an upcoming ride
	 * Query all BikeRide.LocationId for rides starting yesterday 12am through the future.  
	 * We start with yesterday so that Rides that start at 11PM and end at 1AM can still be seen that night.
	 * Currently there is no end time.  That can change when we need. (To add ending time: dateTime.plusDays(1))
	 * 
	 * @return Location
	 * @throws Exception 
	 */
	public static Location getClosestActiveLocation(GeoLoc geoLoc, MongoCollection bikeCollection, Long yesterday) throws Exception {

        //ADD ensure index if we want to add max distance to query.  That might speed up the query
        Iterable<String> all = bikeCollection
                .distinct("cityLocationId")
                .query("{rideStartTime: {$gt: #}}",
                        yesterday)
                .as(String.class);
        ArrayList<ObjectId> locationIds = new ArrayList<ObjectId>();
        for(String locationId : all) {
            locationIds.add(new ObjectId(locationId));
        }

		Location closestLocation;
		MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS, "geoLoc");

        closestLocation = locationCollection
				.findOne("{geoLoc: {$near: [#, #], $maxDistance: #}, _id: {$in: #}}",
						geoLoc.longitude,
						geoLoc.latitude,
                        RADIUS_IN_MILES/SharedStaticValues.ONE_DEGREE_IN_MILES,
						locationIds)
						.as(Location.class);

        //If there are not active rides near the client return the closest city in our system.
        if (closestLocation == null) {
            closestLocation = getClosestLocation(geoLoc);
        }

		return closestLocation;
	}

    /**
     * Identify the closest city to the client regardless of any rides.
     *
     * @return Location
     * @throws Exception
     */
    public static Location getClosestLocation(GeoLoc geoLoc) throws Exception {
        //**(Identify the closest city to the client: 1 DB Call)**
        MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS, "geoLoc");
        Location closestLocation = locationCollection
                .findOne("{geoLoc: {$near: [#, #]}}",
                        geoLoc.longitude,
                        geoLoc.latitude)
                .as(Location.class);
        return closestLocation;
    }

    public static BikeRide postBikeRideDBUpdates(BikeRide bikeRide, GeoLoc geoLoc) throws Exception {

        //Get the distance from the client, if the ride is
        //currently tracking, and total people that have at one time tracked this ride.
        TrackingHelper.setTracking(bikeRide, geoLoc);

        //Get leader tracking
        bikeRide.rideLeaderTracking = TrackingHelper.getRideLeaderTracking(bikeRide);

        //Set the distance from the leader
        if (bikeRide.rideLeaderTracking != null) {
            bikeRide.distanceTrackFromClient = GoogleGeocoderApiHelper.distFrom(bikeRide.rideLeaderTracking.geoLoc, geoLoc); //TODO REPLACE ONCE JONGO .4 IS USED.
        }

        //Get all current tracks
        bikeRide.currentTrackings = TrackingHelper.getAllCurrentTrackings(bikeRide);

        //Set the distance from the participant if needed
        if (bikeRide.distanceTrackFromClient == null &&
                bikeRide.currentTrackings != null &&
                bikeRide.currentTrackings.size() > 0) {
            bikeRide.distanceTrackFromClient = GoogleGeocoderApiHelper.distFrom(bikeRide.currentTrackings.get(0).geoLoc, geoLoc); //TODO REPLACE ONCE JONGO .4 IS USED.
        }

        return bikeRide;
    }

    public static List<BikeRide> postBikeRideDBUpdates(List<BikeRide> bikeRides, GeoLoc geoLoc) throws Exception {
        List<BikeRide> updatedBikeRides = new ArrayList<BikeRide>();
        for (BikeRide bikeRide : bikeRides) {
            bikeRide = postBikeRideDBUpdates(bikeRide, geoLoc);
            updatedBikeRides.add(bikeRide);
        }
        return updatedBikeRides;
    }
}
