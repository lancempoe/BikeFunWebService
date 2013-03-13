//package com.resource.test;
//
//import java.util.ArrayList;
//
//import junit.framework.TestCase;
//
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.jongo.MongoCollection;
//import org.junit.Test;
//
//import com.db.MongoDatabase;
//import com.db.MongoDatabase.MONGO_COLLECTIONS;
//import com.google.common.collect.Lists;
//import com.model.BikeRide;
//import com.model.Location;
//import com.model.Root;
//import com.model.Tracking;
//import com.model.User;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.LoggingFilter;
//import com.tools.GeoLocationHelper;
//
///**
// * Web Service must be turned on: glassfish3/bin/asadmin start-domain or tomcat
// * Start the DB as well: mongod
// * @author lancepoehler
// *
// */
//public class TestDisplayByProximityResource extends TestCase { //extends JerseyTest {
//
//	protected static final String WEB_APP_NAME = "FunService";
//	protected static final String BASE_URI = "http://localhost:" + 8080 + "/" + WEB_APP_NAME; //Local
//	//protected static final String BASE_URI = "http://24.21.204.4/" + WEB_APP_NAME; //Test
//	protected static final String REST_URI = BASE_URI + "/" + "rest";
//
//	protected ClientConfig getDefaultClientConfig() {
//		ClientConfig cc = new DefaultClientConfig();
//		cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
//		//TO use POJO Json clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//		return cc;	
//	}
//
//	@Test
//	public void testDisplay_By_Proximity() throws Exception {
//
//		Client client = Client.create(getDefaultClientConfig());
//		client.addFilter(new LoggingFilter());
//		WebResource webResource = client.resource(REST_URI);
//
//		//CLEAR THE DB WARNING.....
//		webResource
//		.path("/WARNING/CLEAR_AND_RESET_DB")
//		.type("application/json")
//		.post(ClientResponse.class);
//
//		populateDB(webResource);
//
//		Location location = new Location();
//		location.setStreetAddress("1000 SE Main St.");
//		location.setCity("Portland");
//		location.setState("OR");
//		GeoLocationHelper.setGeoLocation(location);
//
//		Root root = webResource
//				.path("display/by_proximity/geoloc="+ location.getGeoLoc().latitude + "," + location.getGeoLoc().longitude)
//				.type("application/json")
//				.get(Root.class);
//
//		assertTrue(root.BikeRides.size() == 2); //1 close and in 10 mins and 1 10 mins ago but still tracking.
//		assertTrue(root.ClosestLocation.getCity().equals("Portland"));
//
//		location = new Location();
//		location.setStreetAddress("1224 2nd Street Northwest");
//		location.setCity("SALEM");
//		location.setState("OR");
//		GeoLocationHelper.setGeoLocation(location);
//
//		root = webResource
//				.path("display/by_proximity/geoloc="+ location.getGeoLoc().latitude + "," + location.getGeoLoc().longitude)
//				.type("application/json")
//				.get(Root.class);
//
//		assertTrue(root.BikeRides.size() == 2); //1 in past but tracking and 1 in 10 minutes.
//		assertTrue(root.ClosestLocation.getCity().equals("Salem"));
//
//
//	}
//
//	private void populateDB(WebResource webResource) throws Exception {
//
//
//		try {
//			//Add users
//			for(int i = 0; i < 4; i++) {
//				User user = new User();
//				user.setUserName("TestUser"+ i);
//				user.setPassword("PicklePassword+ i");
//				user.setEmail(i+"Pickle@the.cat");
//				user.setAccountActivated(true);
//
//				webResource
//				.path("users/new")
//				.type("application/json")
//				.post(user);
//			}
//
//			MongoDatabase.ConnectToDb();
//			MongoCollection usersCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
//			Iterable<User> all = usersCollection
//					.find()
//					.as(User.class);
//			ArrayList<User> users = Lists.newArrayList(all);
//			MongoDatabase.mongoClient.close();
//
//
//			//setup
//			DateTime now = new DateTime();
//
//			//Add a few Bike Rides with userId
//			BikeRide bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("1: One Day in the Future Ride");
//
//			Long future = now.plusDays(1).getMillis();  //
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("You need to come and eat these tacos!");
//			bikeRide.setRideLeaderId(users.get(0).getId());
//			Location location = new Location();
//			location.setStreetAddress("1500 SE Ash St.");
//			location.setCity("Portland");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("2: Two Days in the future");
//			future = now.plusDays(2).getMillis();  //
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("Great 80's music all night long.  You will not want to miss this ride.");
//			bikeRide.setRideLeaderId(users.get(0).getId());
//			location = new Location();
//			location.setStreetAddress("650 NE Holladay St");
//			location.setCity("Portland");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("3: Two Days in the future, Salem Ride");
//			future = now.plusDays(2).getMillis();
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("Let's ride our bike in salem.  Ya!");
//			bikeRide.setRideLeaderId(users.get(1).getId());
//			location = new Location();
//			location.setStreetAddress("1224 2nd Street Northwest");
//			location.setCity("Salem");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("4: minus 1 day plus 1 minute in the past");
//			future = now.minusDays(1).plusMinutes(1).getMillis();  //
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("Use to track but over 2 hours ago");
//			bikeRide.setRideLeaderId(users.get(2).getId());
//			location = new Location();
//			location.setStreetAddress("2000 SE 36th St.");
//			location.setCity("Portland");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("5: minus 1 day minus 1 minute in the past");
//			future = now.minusDays(1).minusMinutes(1).getMillis();  //
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("Let's drink until we can't bike.  Last one standing wins.");
//			bikeRide.setRideLeaderId(users.get(3).getId());
//			location = new Location();
//			location.setStreetAddress("2000 SE 82nd Ave.");
//			location.setCity("Portland");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			//DATA FOR THE PROXIMITY TEST.
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("6: 59 minutes in the Future Ride");
//			now = new DateTime().withZone(DateTimeZone.UTC); // Joda time
//			future = now.plusMinutes(59).getMillis();  //
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("You need to come and eat these tacos!");
//			bikeRide.setRideLeaderId(users.get(0).getId());
//			location = new Location();
//			location.setStreetAddress("1500 SE Ash St.");
//			location.setCity("Portland");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("7: 61 minutes in the future");
//			future = now.plusMinutes(61).getMillis();  //
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("Great 80's music all night long.  You will not want to miss this ride.");
//			bikeRide.setRideLeaderId(users.get(0).getId());
//			location = new Location();
//			location.setStreetAddress("650 NE Holladay St");
//			location.setCity("Portland");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("8: 10 minutes in the future, Salem Ride");
//			future = now.plusMinutes(10).getMillis();
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("Let's ride our bike in salem.  Ya!");
//			bikeRide.setRideLeaderId(users.get(1).getId());
//			location = new Location();
//			location.setStreetAddress("1224 2nd Street Northwest");
//			location.setCity("Salem");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("9: 10 minutes in the past, Salem Ride");
//			future = now.minusMinutes(10).getMillis();
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("still tracking....Let's ride our bike in salem.  Ya!");
//			bikeRide.setRideLeaderId(users.get(1).getId());
//			location = new Location();
//			location.setStreetAddress("1224 2nd Street Northwest");
//			location.setCity("Salem");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("10: 10 minutes in the past and a Salem Ride");
//			future = now.minusMinutes(10).getMillis();
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("Not tracking....Let's ride our bike in salem.  Ya!");
//			bikeRide.setRideLeaderId(users.get(1).getId());
//			location = new Location();
//			location.setStreetAddress("1224 2nd Street Northwest");
//			location.setCity("Salem");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("11: 10 minutes in the future, not close");
//			future = now.plusMinutes(10).getMillis();  //
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("not close to the test person");
//			bikeRide.setRideLeaderId(users.get(2).getId());
//			location = new Location();
//			location.setStreetAddress("2000 SE 82nd Ave");
//			location.setCity("Portland");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("12: Started 1 min ago");
//			future = now.minusMinutes(1).getMillis();  //
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("Great ride that has a person tracking");
//			bikeRide.setRideLeaderId(users.get(1).getId());
//			location = new Location();
//			location.setStreetAddress("2000 SE 7th Ave.");
//			location.setCity("Portland");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			bikeRide = new BikeRide();
//			bikeRide.setBikeRideName("13: Started 1 min ago");
//			future = now.minusMinutes(1).getMillis();  //
//			bikeRide.setRideStartTime(future);
//			bikeRide.setDetails("Great ride that has no one person tracking");
//			bikeRide.setRideLeaderId(users.get(1).getId());
//			location = new Location();
//			location.setStreetAddress("2000 SE 7th Ave.");
//			location.setCity("Portland");
//			location.setState("OR");
//			bikeRide.setLocation(location);
//
//			webResource
//			.path("bikerides/new")
//			.type("application/json")
//			.post(bikeRide);
//
//			MongoDatabase.ConnectToDb();
//			MongoCollection bikeRidesCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
//			Iterable<BikeRide> allBikes = bikeRidesCollection
//					.find()
//					.as(BikeRide.class);
//			ArrayList<BikeRide> bikeRides = Lists.newArrayList(allBikes);
//			MongoDatabase.mongoClient.close();
//
//			//Add a few trackers TODO NOT WORKING :(
//			Tracking tracking = new Tracking();
//			Long tracktime = now.getMillis();
//			tracking.setBikeRideId(bikeRides.get(5).getId());
//			tracking.setGeoLoc(bikeRides.get(5).getLocation().getGeoLoc());
//			tracking.setTrackingTime(tracktime);
//			tracking.setUserId(users.get(0).getId());
//			tracking.setUserName(users.get(0).getUserName());
//
//			webResource
//			.path("tracking/new")
//			.type("application/json")
//			.post(tracking);
//
//			tracking = new Tracking();
//			DateTime changeDateTime = now.minusHours(200000);
//			tracktime = changeDateTime.getMillis();
//			tracking.setBikeRideId(bikeRides.get(3).getId());
//			tracking.setGeoLoc(bikeRides.get(3).getLocation().getGeoLoc());
//			tracking.setTrackingTime(tracktime);
//			tracking.setUserId("oldtrackinganonymous2");
//			tracking.setUserName("oldtrackinganonymous2");
//
//			webResource
//			.path("tracking/new")
//			.type("application/json")
//			.post(tracking);
//
//			tracking = new Tracking();
//			tracktime = now.minusMinutes(0).getMillis();
//			tracking.setBikeRideId(bikeRides.get(8).getId());
//			tracking.setGeoLoc(bikeRides.get(8).getLocation().getGeoLoc());
//			tracking.setTrackingTime(tracktime);
//			tracking.setUserId("anonymous1");
//			tracking.setUserName("anonymous1");
//
//			webResource
//			.path("tracking/new")
//			.type("application/json")
//			.post(tracking);
//
//			tracking = new Tracking();
//			tracktime = now.minusMinutes(0).getMillis();
//			tracking.setBikeRideId(bikeRides.get(11).getId());
//			tracking.setGeoLoc(bikeRides.get(11).getLocation().getGeoLoc());
//			tracking.setTrackingTime(tracktime);
//			tracking.setUserId("anonymous2");
//			tracking.setUserName("anonymous2");
//
//			webResource
//			.path("tracking/new")
//			.type("application/json")
//			.post(tracking);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
