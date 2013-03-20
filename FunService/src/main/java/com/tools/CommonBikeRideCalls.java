package com.tools;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.GeoLoc;
import com.model.Location;

/**
 * Shared classes for bike ride calls.
 * @author lancepoehler
 *
 */
public class CommonBikeRideCalls {

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
		//coll.ensureIndex("{geoLoc: '2d'}") is set when getting the collection
		closestLocation = locationCollection
				.findOne("{geoLoc: {$near: [#, #]}, _id: {$in:#}}", 
						geoLoc.longitude,
						geoLoc.latitude,
						locationIds)
						.as(Location.class);
		return closestLocation;
	}
}
