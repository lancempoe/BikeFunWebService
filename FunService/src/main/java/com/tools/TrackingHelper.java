package com.tools;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Tracking;
import com.settings.SharedValues;

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
			Long clientHeartBeat = now.minusMinutes(SharedValues.CLIENT_HEART_BEAT_IN_MINUTES).getMillis();

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
			Long clientHeartBeat = now.minusMinutes(SharedValues.CLIENT_HEART_BEAT_IN_MINUTES).getMillis();

			MongoCollection trackingCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);

			//Because of the gte, it is possible that 1/1000 of the results will be duplicated.
			//If changed to gt, then 1/1000 tracks may not be returned.  Depends on client speeds.
			Iterable<Tracking> trackings = trackingCollection
					.find("{bikeRideId: #, trackingUserId: {$ne: #}, trackingTime: {$gte: #}}", 
							bikeRide.id, 
							bikeRide.rideLeaderId, 
							clientHeartBeat)
					.as(Tracking.class);
					
			List<Tracking> trackingList = Lists.newArrayList(trackings);
			if (trackingList != null && trackingList.size() > 0) {
				bikeRidetrackings = trackingList;
			}
		}
		return bikeRidetrackings;
	}
	
	/**
	 * Find and set:
	 * 1) the distance between the client and the ride. 
	 * 2) Most recent Tracking (if turned on)
	 * 3) People that are tracking Count (if turned on)
	 * @author lancepoehler
	 * @throws Exception 
	 */
	public static void setTracking(List<BikeRide> bikeRides, GeoLoc geoLoc) throws Exception {

		DateTime now = new DateTime(); // Joda time
		Long clientHeartBeat = now.minusMinutes(SharedValues.CLIENT_HEART_BEAT_IN_MINUTES).getMillis();

		MongoCollection trackingCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);
		for(BikeRide bikeRide : bikeRides) {
			setBikeRideTrackingDetails(bikeRide, geoLoc, trackingCollection, clientHeartBeat);
		}
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
		Long clientHeartBeat = now.minusMinutes(SharedValues.CLIENT_HEART_BEAT_IN_MINUTES).getMillis();

		MongoCollection trackingCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);
		setBikeRideTrackingDetails(bikeRide, geoLoc, trackingCollection, clientHeartBeat);
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
		bikeRide.distanceFromClient = GeoLocationHelper.distFrom(bikeRide.location.geoLoc, geoLoc); //TODO REPLACE ONCE JONGO .4 IS USED.

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
