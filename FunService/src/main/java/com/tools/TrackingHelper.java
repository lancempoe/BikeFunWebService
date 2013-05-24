package com.tools;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Tracking;
import com.settings.SharedStaticValues;
import org.joda.time.DateTime;
import org.jongo.MongoCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lancepoehler
 *
 */
public class TrackingHelper {

	public static Tracking getRideLeaderTracking(BikeRide bikeRide) throws Exception {
		Tracking bikeRideLeadertracking = null;
		if (bikeRide.trackingAllowed) {
			
			DateTime now = new DateTime(); // Joda time
			Long clientHeartBeat = now.minusMinutes(SharedStaticValues.CLIENT_HEART_BEAT_IN_MINUTES).getMillis();

			MongoCollection trackingCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);

			Iterable<Tracking> trackings = trackingCollection
					.find("{bikeRideId: #, trackingUserId: #, trackingTime: {$gte: #}}",
							bikeRide.id, 
							bikeRide.rideLeaderId,
							clientHeartBeat)
					.sort("{trackingTime: -1}")
					.as(Tracking.class);
			ArrayList<Tracking> trackingList = Lists.newArrayList(trackings);
			if (trackingList != null && trackingList.size() > 0) {
				bikeRideLeadertracking = trackingList.get(0);
			}
		}
		
		return bikeRideLeadertracking;
	}
	
	public static List<Tracking> getAllCurrentTrackings(BikeRide bikeRide) throws Exception {
		List<Tracking> bikeRidetrackings = null;
		if (bikeRide.trackingAllowed) {
			
			DateTime now = new DateTime(); // Joda time
			Long clientHeartBeat = now.minusMinutes(SharedStaticValues.CLIENT_HEART_BEAT_IN_MINUTES).getMillis();

			MongoCollection trackingCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);

			Iterable<Tracking> trackings = trackingCollection


                    .find("{ \"query\": { \"bikeRideId\": #, \"trackingUserId\": {$ne : # }, \"trackingTime\": {$gte: #} }, $orderby:{\"timestamp\" : -1} }",
							bikeRide.id,
							bikeRide.rideLeaderId,
							clientHeartBeat)
					.as(Tracking.class);

            List<String> finalTrackIds = new ArrayList(); //Unique list of trackers... This works because of $orderby
            List<Tracking> finalTrackingList = new ArrayList(); //Most recent of each tracker track
			List<Tracking> trackingList = Lists.newArrayList(trackings); //All tracks in past
			if (trackingList != null && trackingList.size() > 0) {
				for (Tracking track : trackingList) {
                     if (!finalTrackIds.contains(track.trackingUserId)) {
                        finalTrackingList.add(track);
                        finalTrackIds.add(track.trackingUserId);
                     }
                }

                bikeRidetrackings = finalTrackingList;
			}
		}
		return bikeRidetrackings;
	}

	/**
	 * Find and set:
	 * 1) the distance between the client and the ride. 
	 * 2) Most recent Tracking (if turned on) for each user
	 * 3) People that are tracking Count (if turned on)
	 * @author lancepoehler
	 * @throws Exception 
	 */
	public static void setTracking(BikeRide bikeRide, GeoLoc geoLoc) throws Exception {

		DateTime now = new DateTime(); // Joda time
		Long clientHeartBeat = now.minusMinutes(SharedStaticValues.CLIENT_HEART_BEAT_IN_MINUTES).getMillis();

		MongoCollection trackingCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);
		setBikeRideTrackingDetails(bikeRide, geoLoc, trackingCollection, clientHeartBeat);
	}

    /**
     * Find and delete all tracks for a deleted Event
     * @author lancepoehler
     * @throws Exception
     */
    public static void deleteTrackings(BikeRide bikeRide) throws Exception {

        MongoCollection trackingCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);
        trackingCollection.remove("{bikeRideId:#}", bikeRide.id);

    }

	/**
	 * Get the distance from the client, if the ride is currently tracking, and total people that have at one time tracked this ride.
	 * @param bikeRide
	 * @param geoLoc
	 * @param collection
	 * @param clientHeartBeat
	 * @throws Exception
	 */
	private static void setBikeRideTrackingDetails(BikeRide bikeRide, GeoLoc geoLoc, MongoCollection collection, Long clientHeartBeat) throws Exception {
		bikeRide.distanceFromClient = GoogleGeocoderApiHelper.distFrom(bikeRide.location.geoLoc, geoLoc); //TODO REPLACE ONCE JONGO .4 IS USED.

		if (bikeRide.trackingAllowed) {
			Iterable<Tracking> trackings = collection
					.find("{bikeRideId:#}", bikeRide.id)
					.sort("{trackingTime: -1}")
					//.limit(1)
					.as(Tracking.class);
			List<Tracking> trackingList = Lists.newArrayList(trackings);
			if (trackingList != null && trackingList.size() > 0) {
				bikeRide.currentlyTracking = trackingList.get(0).trackingTime > clientHeartBeat;
			}

			Iterable<String> usersTracking = collection
					.distinct("trackingUserId")
					.query("{bikeRideId:#}", bikeRide.id)
					.as(String.class);
			List<String> usersTrackingList = Lists.newArrayList(usersTracking);
			if (usersTrackingList != null) {
				bikeRide.totalPeopleTrackingCount = usersTrackingList.size();
			}
		}
	}
}
