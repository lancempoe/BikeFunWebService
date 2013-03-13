package com.tools;

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
			bikeRide.setDistanceFromClient(GeoLocationHelper.distFrom(bikeRide.getLocation().getGeoLoc(), geoLoc)); //TODO REPLACE ONCE JONGO .4 IS USED.

			if (bikeRide.isTrackingAllowed()) {
				Iterable<Tracking> trackings = trackingCollection
						.find("{BikeRideId:#}", bikeRide.getId())
						.sort("{TrackingTime: -1}")
						//.limit(1)
						.as(Tracking.class);
				List<Tracking> trackingList = Lists.newArrayList(trackings);
				if (trackingList != null && trackingList.size() > 0) {
					bikeRide.setCurrentlyTracking(trackingList.get(0).getTrackingTime() > clientHeartBeat);
				}

				Iterable<String> usersTracking = trackingCollection
						.distinct("UserId")
						.query("{BikeRideId:#}", bikeRide.getId())
						.as(String.class);
				List<String> usersTrackingList = Lists.newArrayList(usersTracking);
				if (usersTrackingList != null) {
					bikeRide.setTotalPeopleTrackingCount(usersTrackingList.size());
				}
			}
		}
	}
}
