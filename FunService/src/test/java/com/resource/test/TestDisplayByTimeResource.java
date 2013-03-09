//package com.resource.test;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import junit.framework.TestCase;
//
//import org.bson.types.ObjectId;
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
//import com.sun.jersey.api.client.Client;
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
//public class TestDisplayByTimeResource extends TestCase { //extends JerseyTest {
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
//	public void testDisplay_By_time_of_day() throws Exception {
//
//		PopulateDBWithTestData populate = new PopulateDBWithTestData();
//		populate.testClearDataAddData();
//
//		Client client = Client.create(getDefaultClientConfig());
//		client.addFilter(new LoggingFilter());
//		WebResource webResource = client.resource(REST_URI);
//
//		Location location = new Location();
//		location.setStreetAddress("1000 SE Main St.");
//		location.setCity("Portland");
//		location.setState("OR");
//		GeoLocationHelper.setGeoLocation(location);
//
//		Root root = webResource
//				.path("display/by_time_of_day/proximity/"+ location.getGeoLoc().latitude + "," + location.getGeoLoc().longitude)
//				.type("application/json")
//				.get(Root.class);
//
//		assertTrue(root.BikeRides.size() == 3); //1 is in salem and one is more than 1 day old
//		assertTrue(root.Locations.size() == 2);
//		assertTrue(root.ClosestLocation.getCity().equals("Portland"));
//
//		location = new Location();
//		location.setStreetAddress("1224 2nd Street Northwest");
//		location.setCity("SALEM");
//		location.setState("OR");
//		GeoLocationHelper.setGeoLocation(location);
//
//		root = webResource
//				.path("display/by_time_of_day/proximity/"+ location.getGeoLoc().latitude + "," + location.getGeoLoc().longitude)
//				.type("application/json")
//				.get(Root.class);
//
//		assertTrue(root.BikeRides.size() == 1);
//		assertTrue(root.Locations.size() == 2);
//		assertTrue(root.ClosestLocation.getCity().equals("Salem"));
//
//		location = new Location();
//		location.setStreetAddress("SE Ash & SE 22nd");
//		location.setCity("Portland");
//		location.setState("OR");
//		GeoLocationHelper.setGeoLocation(location);
//
//		root = webResource
//				.path("display/by_time_of_day/proximity/"+ location.getGeoLoc().latitude + "," + location.getGeoLoc().longitude)
//				.type("application/json")
//				.get(Root.class);
//
//		assertTrue(root.BikeRides.size() == 3);
//		assertTrue(root.Locations.size() == 2);
//		assertTrue(root.ClosestLocation.getCity().equals("Portland"));
//
//		boolean validPath = true;
//		try {
//			root = webResource
//					.path("display/by_time_of_day/proximity/765x43456,345.676543")
//					.type("application/json")
//					.get(Root.class);
//		} catch (Exception e) {
//			validPath = false;
//		}
//
//		assertFalse(validPath);
//	}
//
//}
