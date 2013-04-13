package com.shared;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.*;
import com.sun.jersey.api.client.WebResource;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.MongoCollection;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This class is used during testing to validate query's to the DB.
 * @author lancepoehler
 *
 */
public class PopulateDB {


	public void populateDB(WebResource webResource) throws Exception {

		try {
			//Add users
			for(int i = 0; i < 4; i++) {
				AnonymousUser au = new AnonymousUser();
				au.deviceAccounts = new DeviceAccounts();
				au.deviceAccounts.deviceUUID = UUID.randomUUID().toString();;
				au.deviceAccounts.key = "1234";

				webResource
				.path("users/anonymous/"+au.deviceAccounts.key+"/"+au.deviceAccounts.deviceUUID)
				.type("application/json")
				.get(AnonymousUser.class);				
			}

			MongoDatabase.ConnectToDb();
			MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.ANONYMOUS_USERS);
			Iterable<AnonymousUser> all = usersCollection
					.find()
					.as(AnonymousUser.class);
			ArrayList<AnonymousUser> users = Lists.newArrayList(all);
			MongoDatabase.mongoClient.close();

			//setup
			DateTime now = new DateTime();

			//Add a few Bike Rides with userId
			BikeRide bikeRide = new BikeRide();
			bikeRide.bikeRideName = "1: One Day in the Future Ride: Apple ride";

			Long future = now.plusDays(10).getMillis();  //
			bikeRide.rideStartTime = future;
			bikeRide.details = "You need to come and eat these pears?";
			bikeRide.rideLeaderId = users.get(0).id;
			Location location = new Location();
			location.streetAddress = ("1500 SE Ash St.");
			location.city = ("Portland");
			location.state = ("OR");
			bikeRide.location = location;

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = "2: Two Days in the future";
			future = now.plusDays(20).getMillis();  //
			bikeRide.rideStartTime = future;
			bikeRide.details = "Great 80's music all night long.  You will not want to miss this ride.";
			bikeRide.rideLeaderId = users.get(0).id;
			location = new Location();
			location.streetAddress = ("650 NE Holladay St");
			location.city = ("Portland");
			location.state = ("OR");
			bikeRide.location = location;

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = "3: Two Days in the future, Salem Ride";
			future = now.plusDays(20).getMillis();
			bikeRide.rideStartTime = future;
			bikeRide.details = "Let's ride our bike in salem with apples.  Ya!";
			bikeRide.rideLeaderId = users.get(1).id;
			location = new Location();
			location.streetAddress = ("1224 2nd Street Northwest");
			location.city = ("Salem");
			location.state = ("OR");
			bikeRide.location = location;

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = "4: minus 1 day plus 1 minute in the past";
			future = now.minusDays(1).plusMinutes(1).getMillis();  //
			bikeRide.rideStartTime = future;
			bikeRide.details = "Use to track but over 2 hours ago.  No more apples.";
			bikeRide.rideLeaderId = users.get(2).id;
			location = new Location();
			location.streetAddress = ("2000 SE 36th St.");
			location.city = ("Portland");
			location.state = ("OR");
			bikeRide.location = location;

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = "5: minus 1 day minus 1 minute in the past";
			future = now.minusDays(1).minusMinutes(1).getMillis();  //
			bikeRide.rideStartTime = future;
			bikeRide.details = "Let's drink until we can't bike.  Last one standing wins.";
			bikeRide.rideLeaderId = users.get(3).id;
			location = new Location();
			location.streetAddress = ("2000 SE 82nd Ave.");
			location.city = ("Portland");
			location.state = ("OR");
			bikeRide.location = location;

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			//DATA FOR THE PROXIMITY TEST.
			bikeRide = new BikeRide();
			bikeRide.bikeRideName = "6: 59 minutes in the Future Ride";
			now = new DateTime().withZone(DateTimeZone.UTC); // Joda time
			future = now.plusMinutes(59).getMillis();  //
			bikeRide.rideStartTime = future;
			bikeRide.details = "You need to come and eat these tacos!";
			bikeRide.rideLeaderId = users.get(0).id;
			location = new Location();
			location.streetAddress = ("1500 SE Ash St.");
			location.city = ("Portland");
			location.state = ("OR");
			bikeRide.location = location;

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = "7: 61 minutes in the future";
			future = now.plusMinutes(61).getMillis();  //
			bikeRide.rideStartTime = future;
			bikeRide.details = "Great 80's music all night long.  You will not want to miss this ride.";
			bikeRide.rideLeaderId = users.get(0).id;
			location = new Location();
			location.streetAddress = ("650 NE Holladay St");
			location.city = ("Portland");
			location.state = ("OR");
			bikeRide.location = location;

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = ("8: 10 minutes in the future, Salem Ride");
			future = now.plusMinutes(10).getMillis();
			bikeRide.rideStartTime = (future);
			bikeRide.details = ("Let's ride our bike in salem.  Ya!");
			bikeRide.rideLeaderId = (users.get(1).id);
			location = new Location();
			location.streetAddress = ("1224 2nd Street Northwest");
			location.city = ("Salem");
			location.state = ("OR");
			bikeRide.location = (location);

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = ("9: 10 minutes in the past, Salem Ride");
			future = now.minusMinutes(10).getMillis();
			bikeRide.rideStartTime = (future);
			bikeRide.details = ("still tracking....Let's ride our bike in salem. There may be taco's.  Ya!");
			bikeRide.rideLeaderId = (users.get(1).id);
			location = new Location();
			location.streetAddress = ("1224 2nd Street Northwest");
			location.city = ("Salem");
			location.state = ("OR");
			bikeRide.location = (location);

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = ("10: 10 minutes in the past and a Salem Ride");
			future = now.minusMinutes(10).getMillis();
			bikeRide.rideStartTime = (future);
			bikeRide.details = ("Not tracking....Let's ride our bike in salem.  Ya!");
			bikeRide.rideLeaderId = (users.get(1).id);
			bikeRide.targetAudience = ("21+");
			location = new Location();
			location.streetAddress = ("1224 2nd Street Northwest");
			location.city = ("Salem");
			location.state = ("OR");
			bikeRide.location = (location);

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = ("11: 10 minutes in the future, not close");
			future = now.plusMinutes(10).getMillis();  //
			bikeRide.rideStartTime = (future);
			bikeRide.details = ("not close to the test person");
			bikeRide.rideLeaderId = (users.get(2).id);
			location = new Location();
			location.streetAddress = ("2000 SE 82nd Ave");
			location.city = ("Portland");
			location.state = ("OR");
			bikeRide.location = (location);

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = ("12: Started 1 min ago");
			future = now.minusMinutes(1).getMillis();  //
			bikeRide.rideStartTime = (future);
			bikeRide.details = ("Great ride that has a person tracking");
			bikeRide.rideLeaderId = (users.get(1).id);
			bikeRide.targetAudience = ("0+");
			location = new Location();
			location.streetAddress = ("2000 SE 7th Ave.");
			location.city = ("Portland");
			location.state = ("OR");
			bikeRide.location = (location);

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			bikeRide = new BikeRide();
			bikeRide.bikeRideName = ("13: Started 1 min ago");
			future = now.minusMinutes(1).getMillis();  //
			bikeRide.rideStartTime = (future);
			bikeRide.details = ("Great ride that has no one person tracking");
			bikeRide.targetAudience = ("21+");
			bikeRide.rideLeaderId = (users.get(1).id);
			location = new Location();
			location.streetAddress = ("2000 SE 7th Ave.");
			location.city = ("Portland");
			location.state = ("OR");
			bikeRide.location = (location);

			webResource
			.path("bikerides/new")
			.type("application/json")
			.post(bikeRide);

			MongoDatabase.ConnectToDb();
			MongoCollection bikeRidesCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			Iterable<BikeRide> allBikes = bikeRidesCollection
					.find()
					.as(BikeRide.class);
			ArrayList<BikeRide> bikeRides = Lists.newArrayList(allBikes);
			MongoDatabase.mongoClient.close();

			//Add a few trackers
			Tracking tracking = new Tracking();
			Long tracktime = now.getMillis();
			tracking.bikeRideId = (bikeRides.get(5).id);
			tracking.geoLoc = (bikeRides.get(5).location.geoLoc);
			tracking.trackingTime = (tracktime);
			tracking.trackingUserId = (users.get(0).id);
			tracking.trackingUserName = (users.get(0).userName);

			webResource
			.path("tracking/new")
			.type("application/json")
			.post(tracking);

			tracking = new Tracking();
			DateTime changeDateTime = now.minusHours(200000);
			tracktime = changeDateTime.getMillis();
			tracking.bikeRideId = (bikeRides.get(3).id);
			tracking.geoLoc = (bikeRides.get(3).location.geoLoc);
			tracking.trackingTime = (tracktime);
			tracking.trackingUserId = (users.get(1).id);
			tracking.trackingUserName = (users.get(1).userName);

			webResource
			.path("tracking/new")
			.type("application/json")
			.post(tracking);

			tracking = new Tracking();
			tracktime = now.minusMinutes(0).getMillis();
			tracking.bikeRideId = (bikeRides.get(8).id);
			tracking.geoLoc = (bikeRides.get(8).location.geoLoc);
			tracking.trackingTime = (tracktime);
			tracking.trackingUserId = (users.get(2).id);
			tracking.trackingUserName = (users.get(2).userName);

			webResource
			.path("tracking/new")
			.type("application/json")
			.post(tracking);

			tracking = new Tracking();
			tracktime = now.minusMinutes(0).getMillis();
			tracking.bikeRideId = (bikeRides.get(11).id);
			tracking.geoLoc = (bikeRides.get(11).location.geoLoc);
			tracking.trackingTime = (tracktime);
			tracking.trackingUserId = (users.get(3).id);
			tracking.trackingUserName = (users.get(3).userName);

			webResource
			.path("tracking/new")
			.type("application/json")
			.post(tracking);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
