//package com.resource.test;
//
//import com.tools.GoogleGeocoderApiHelper;
//import junit.framework.TestCase;
//
//import org.joda.time.DateTime;
//import org.junit.Test;
//
//import com.model.BikeRide;
//import com.model.Location;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.LoggingFilter;
//
///**
//* Web Service must be turned on: glassfish3/bin/asadmin start-domain or tomcat
//* Start the DB as well: mongod
//* @author lancepoehler
//*
//*/
//public class TestBikeRideResource extends TestCase { //extends JerseyTest {
//
//	//	private static final Logger LOG = Logger.getLogger(TestBikeRideResource.class.getCanonicalName());
//	protected static final String WEB_APP_NAME = "FunService";
//	protected static final String BASE_URI = "http://localhost:" + 8080 + "/" + WEB_APP_NAME; //Local
//	//	protected static final String BASE_URI = "http://24.21.204.4/" + WEB_APP_NAME; //Test
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
//	public void testDisplayBikeRide() {
//		try {
//			Client client = Client.create(getDefaultClientConfig());
//			client.addFilter(new LoggingFilter());
//			WebResource webResource = client.resource(REST_URI);
//
//			//			//CLEAR THE DB WARNING.....
//			//			webResource
//			//			.path("/WARNING/CLEAR_AND_RESET_DB")
//			//			.type("application/json")
//			//			.post(ClientResponse.class);
//
//			//			PopulateDB populate = new PopulateDB();
//			//			populate.populateDB(webResource);
//
//			String bikeRideID = "51466d5703645990b9d1facf";
//			Location location = new Location();
//			location.streetAddress=("1000 SE Main St.");
//			location.city=("Portland");
//			location.state = ("OR");
//			GoogleGeocoderApiHelper.setGeoLocation(location);
//
//			BikeRide bikeRide = webResource
//					.path("bikerides/" + bikeRideID + "/geoloc="+location.geoLoc.latitude + "," + location.geoLoc.longitude)
//					.type("application/json")
//					.get(BikeRide.class);
//
//			assertNotNull(bikeRide);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void testFunServiceNewBikeRide() {
//		try {
//			Client client = Client.create(getDefaultClientConfig());
//			client.addFilter(new LoggingFilter());
//			WebResource webResource = client.resource(REST_URI);
//
//			//setup
//			DateTime now = new DateTime();
//
//			//Add a few Bike Rides with userId
//			BikeRide bikeRide = new BikeRide();
//			bikeRide.bikeRideName = "1: One Day in the Future Ride: Apple ride";
//
//			Long future = now.plusDays(1).getMillis();  //
//			bikeRide.rideStartTime = future;
//			bikeRide.details = "You need to come and eat these pears?";
//			bikeRide.rideLeaderId = "123456789";
//			Location location = new Location();
//			location.streetAddress = ("1500 SE Ash St.");
//			location.city = ("Portland");
//			location.state = ("OR");
//			bikeRide.location = location;
//            bikeRide.imagePath = "test.jpg";
//
//
//            //Save the resized image
//            int i = bikeRide.imagePath.lastIndexOf('.');
//            String extention = bikeRide.imagePath.substring(i+1);
//
//
//            bikeRide = webResource
//					.path("bikerides/new")
//					.type("application/json")
//					.post(BikeRide.class, bikeRide);
//
//			assertTrue(bikeRide.imagePath == "something different");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
////	@Test
////	public void testFunServiceNewAndUpdateBikeRide() {
////		try {
////			Client client = Client.create(getDefaultClientConfig());
////			client.addFilter(new LoggingFilter());
////			WebResource webResource = client.resource(REST_URI);
////
////			//Create 2 users
////			List<AnonymousUser> users = new ArrayList<AnonymousUser>();
////			for(int i = 0; i < 2; i++) {
////				AnonymousUser au = new AnonymousUser();
////				au.deviceUUID = UUID.randomUUID().toString();
////				au.key = "1234";
////
////				au = webResource
////						.path("users/anonymous/"+au.key+"/"+au.deviceUUID)
////						.type("application/json")
////						.get(AnonymousUser.class);
////				users.add(au);
////			}
////
////			//Create a new bike ride
////			DateTime now = new DateTime();
////
////			//Add a few Bike Rides with userId
////			BikeRide bikeRide = new BikeRide();
////			bikeRide.bikeRideName = "New Test Ride";
////
////			Long future = now.plusDays(1).getMillis();  //
////			bikeRide.rideStartTime = future;
////			bikeRide.details = "You need to come and eat these pears?";
////			bikeRide.rideLeaderId = users.get(0).id;
////			Location location = new Location();
////			location.streetAddress = ("1500 SE Ash St.");
////			location.city = ("Portland");
////			location.state = ("OR");
////			bikeRide.location = location;
////
////			ClientResponse response = webResource
////					.path("bikerides/new")
////					.type("application/json")
////					.post(ClientResponse.class, bikeRide);
////
////			//Get Back the new bike ride
////			MongoDatabase.ConnectToDb();
////			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
////			bikeRide = collection.findOne("{bikeRideName: #, rideLeaderId: #}", bikeRide.bikeRideName, bikeRide.rideLeaderId).as(BikeRide.class);
////			MongoDatabase.mongoClient.close();
////
////			response = webResource
////					.path("bikerides/update/"+users.get(0).id+"/"+users.get(0).key+"/"+users.get(0).deviceUUID)
////					.type("application/json")
////					.post(ClientResponse.class, bikeRide);
////			assertTrue(ClientResponse.Status.OK.getStatusCode() == response.getStatus());
////
////			//update the ride with an incorrect address
////			BikeRide bikeRide2 = bikeRide;
////			bikeRide2.location.streetAddress = "xjosdsfgffx  aa s sd sd hxnrxd55 3lkj r4f ";
////
////			response = webResource
////					.path("bikerides/update/"+users.get(0).id+"/"+users.get(0).key+"/"+users.get(0).deviceUUID)
////					.type("application/json")
////					.post(ClientResponse.class, bikeRide2);
////			assertTrue(ClientResponse.Status.BAD_REQUEST.getStatusCode() == response.getStatus());
////
////			//update the bike ride with incorrect key
////			response = webResource
////					.path("bikerides/update/"+users.get(0).id+"/123456/"+users.get(0).deviceUUID)
////					.type("application/json")
////					.post(ClientResponse.class, bikeRide);
////			assertTrue(ClientResponse.Status.FORBIDDEN.getStatusCode() == response.getStatus());
////
////			//update the bike ride with incorrect uuid
////			response = webResource
////					.path("bikerides/update/"+users.get(0).id+"/"+users.get(0).key+"/abcd")
////					.type("application/json")
////					.post(ClientResponse.class, bikeRide);
////			assertTrue(ClientResponse.Status.FORBIDDEN.getStatusCode() == response.getStatus());
////
////			//TRY TO UPDATE THE BIKE RIDE WITH A DIFFERENT USER
////			response = webResource
////					.path("bikerides/update/"+users.get(1).id+"/"+users.get(1).key+"/"+users.get(1).deviceUUID)
////					.type("application/json")
////					.post(ClientResponse.class, bikeRide);
////			assertTrue(ClientResponse.Status.FORBIDDEN.getStatusCode() == response.getStatus());
////
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////	}
////
////	@Test
////	public void testFunServiceDeleteBikeRide() {
////		try {
////			Client client = Client.create(getDefaultClientConfig());
////			client.addFilter(new LoggingFilter());
////			WebResource webResource = client.resource(REST_URI);
////
////			//Create 2 users
////			List<AnonymousUser> users = new ArrayList<AnonymousUser>();
////			for(int i = 0; i < 2; i++) {
////				AnonymousUser au = new AnonymousUser();
////				au.deviceUUID = UUID.randomUUID().toString();
////				au.key = "1234";
////
////				au = webResource
////						.path("users/anonymous/"+au.key+"/"+au.deviceUUID)
////						.type("application/json")
////						.get(AnonymousUser.class);
////				users.add(au);
////			}
////
////			//Create a new bike ride
////			DateTime now = new DateTime();
////
////			//Add a few Bike Rides with userId
////			BikeRide bikeRide = new BikeRide();
////			bikeRide.bikeRideName = "New Test Ride";
////
////			Long future = now.plusDays(1).getMillis();  //
////			bikeRide.rideStartTime = future;
////			bikeRide.details = "You need to come and eat these pears?";
////			bikeRide.rideLeaderId = users.get(0).id;
////			Location location = new Location();
////			location.streetAddress = ("1500 SE Ash St.");
////			location.city = ("Portland");
////			location.state = ("OR");
////			bikeRide.location = location;
////
////			ClientResponse response = webResource
////					.path("bikerides/new")
////					.type("application/json")
////					.post(ClientResponse.class, bikeRide);
////
////			//Get Back the new bike ride
////			MongoDatabase.ConnectToDb();
////			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
////			bikeRide = collection.findOne("{bikeRideName: #, rideLeaderId: #}", bikeRide.bikeRideName, bikeRide.rideLeaderId).as(BikeRide.class);
////			MongoDatabase.mongoClient.close();
////
////
////			//delete with incorrect user
////			response = webResource
////					.path("bikerides/delete/"+users.get(1).id+"/"+users.get(1).key+"/"+users.get(1).deviceUUID)
////					.type("application/json")
////					.post(ClientResponse.class, bikeRide);
////			assertTrue(ClientResponse.Status.FORBIDDEN.getStatusCode() == response.getStatus());
////
////			//update the bike ride with incorrect key
////			response = webResource
////					.path("bikerides/delete/"+users.get(0).id+"/"+users.get(0).key+"/"+users.get(0).deviceUUID)
////					.type("application/json")
////					.post(ClientResponse.class, bikeRide);
////			assertTrue(ClientResponse.Status.OK.getStatusCode() == response.getStatus());
////
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////	}
//}
