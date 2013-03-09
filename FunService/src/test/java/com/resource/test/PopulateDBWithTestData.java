package com.resource.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.BikeRide;
import com.model.Location;
import com.model.Tracking;
import com.model.User;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

/**
 * Web Service must be turned on: glassfish3/bin/asadmin start-domain
 * Start the DB as well: mongod
 * @author lancepoehler
 *
 */
public class PopulateDBWithTestData {

	protected static final String WEB_APP_NAME = "FunService";
	protected static final String BASE_URI = "http://localhost:" + 8080 + "/" + WEB_APP_NAME; //Local
	//protected static final String BASE_URI = "http://24.21.204.4/" + WEB_APP_NAME; //Test
	protected static final String REST_URI = BASE_URI + "/" + "rest";

	protected ClientConfig getDefaultClientConfig() {
		ClientConfig cc = new DefaultClientConfig();
		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		//TO use POJO Json clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		return cc;	
	}

	public void testClearDataAddData() throws Exception {

		//Web Service must be turned on: glassfish3/bin/asadmin start-domain
		//Start the DB as well: mongod
		Client client = Client.create(getDefaultClientConfig());
		client.addFilter(new LoggingFilter());
		WebResource webResource = client.resource(REST_URI);

		MongoDatabase.ConnectToDb();
		MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
		collection.drop();
		collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
		collection.drop();
		collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
		collection.drop();
		collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.TRACKING);
		collection.drop();
		MongoDatabase.mongoClient.close();

		//Add users
		List<String> userIds = new ArrayList<String>();
		for(int i = 0; i < 4; i++) {
			User user = new User();
			user.setUserName("TestUser"+ i);
			user.setPassword("PicklePassword+ i");
			user.setEmail(i+"Pickle@the.cat");
			user.setAccountActivated(true);

			webResource
			.path("users/new")
			.type("application/json")
			.post(ClientResponse.class, user);

			userIds.add(user.getId());
		}

		//Add Bike Ride(2) with userId
		BikeRide bikeRide = new BikeRide();
		bikeRide.setBikeRideName("Taco Ride");
		DateTime todayDateTime = new DateTime().withZone(DateTimeZone.UTC).toDateMidnight().toDateTime(); // Joda time
		Date future = todayDateTime.plusDays(1).toDate();  //
		bikeRide.setStartTime(future);
		bikeRide.setDetails("You need to come and eat these tacos!");
		bikeRide.setRideLeaderId(userIds.get(0));
		Location location = new Location();
		location.setStreetAddress("1500 SE Ash St.");
		location.setCity("Portland");
		location.setState("OR");
		bikeRide.setLocation(location);

		webResource
		.path("bikerides/new")
		.type("application/json")
		.post(ClientResponse.class, bikeRide);

		bikeRide = new BikeRide();
		bikeRide.setBikeRideName("Pop till you puck");
		future = todayDateTime.plusDays(2).toDate();  //
		bikeRide.setStartTime(future);
		bikeRide.setDetails("Great 80's music all night long.  You will not want to miss this ride.");
		bikeRide.setRideLeaderId(userIds.get(0));
		location = new Location();
		location.setStreetAddress("650 NE Holladay St");
		location.setCity("Portland");
		location.setState("OR");
		bikeRide.setLocation(location);

		webResource
		.path("bikerides/new")
		.type("application/json")
		.post(ClientResponse.class, bikeRide);

		bikeRide = new BikeRide();
		bikeRide.setBikeRideName("Salem Ride");
		future = todayDateTime.plusDays(2).toDate();
		bikeRide.setStartTime(future);
		bikeRide.setDetails("Let's ride our bike in salem.  Ya!");
		bikeRide.setRideLeaderId(userIds.get(1));
		location = new Location();
		location.setStreetAddress("1224 2nd Street Northwest");
		location.setCity("Salem");
		location.setState("OR");
		bikeRide.setLocation(location);

		webResource
		.path("bikerides/new")
		.type("application/json")
		.post(ClientResponse.class, bikeRide);

		bikeRide = new BikeRide();
		bikeRide.setBikeRideName("Strawberry Ride");
		future = todayDateTime.minusDays(1).plusMinutes(1).toDate();  //
		bikeRide.setStartTime(future);
		bikeRide.setDetails("Join us in our fun and games while we eat lots of strawberrys");
		bikeRide.setRideLeaderId(userIds.get(2));
		location = new Location();
		location.setStreetAddress("4214 SE 36th");
		location.setCity("Portland");
		location.setState("OR");
		bikeRide.setLocation(location);

		webResource
		.path("bikerides/new")
		.type("application/json")
		.post(ClientResponse.class, bikeRide);

		bikeRide = new BikeRide();
		bikeRide.setBikeRideName("Dive Bar Bike Tour");
		future = todayDateTime.minusDays(1).minusMinutes(1).toDate();  //
		bikeRide.setStartTime(future);
		bikeRide.setDetails("Let's drink until we can't bike.  Last one standing wins.");
		bikeRide.setRideLeaderId(userIds.get(3));
		location = new Location();
		location.setStreetAddress("2000 SE 82nd Ave.");
		location.setCity("Portland");
		location.setState("OR");
		bikeRide.setLocation(location);

		webResource
		.path("bikerides/new")
		.type("application/json")
		.post(ClientResponse.class, bikeRide);

		MongoDatabase.ConnectToDb();
		MongoCollection bikeCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
		Iterable<BikeRide> all = bikeCollection
				.find()
				.as(BikeRide.class);
		ArrayList<BikeRide> bikeRides = Lists.newArrayList(all);
		MongoDatabase.mongoClient.close();

		//Add a few trackers
		Tracking tracking = new Tracking();
		Date tracktime = todayDateTime.toDate();
		tracking.setBikeRideId(bikeRides.get(0).getId());
		tracking.setGeoLoc(bikeRide.getLocation().getGeoLoc());
		tracking.setTrackingTime(tracktime);
		tracking.setUserId(userIds.get(0));

		webResource
		.path("tracking/new")
		.type("application/json")
		.post(ClientResponse.class, tracking);

		tracking = new Tracking();
		tracktime = todayDateTime.minusMinutes(1).toDate();
		tracking.setBikeRideId(bikeRides.get(0).getId());
		tracking.setGeoLoc(bikeRide.getLocation().getGeoLoc());
		tracking.setTrackingTime(tracktime);
		tracking.setUserId("anonymous1");

		webResource
		.path("tracking/new")
		.type("application/json")
		.post(ClientResponse.class, tracking);

	}
}
