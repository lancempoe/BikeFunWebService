//package com.resource.test;
//
//import junit.framework.TestCase;
//
//import org.junit.Test;
//
//import com.model.Location;
//import com.model.Root;
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
//		Client client = Client.create(getDefaultClientConfig());
//		client.addFilter(new LoggingFilter());
//		WebResource webResource = client.resource(REST_URI);
//
//		//		//CLEAR THE DB WARNING.....
//		//		webResource
//		//		.path("/WARNING/CLEAR_AND_RESET_DB")
//		//		.type("application/json")
//		//		.post(ClientResponse.class);
//
//		Location location = new Location();
//		location.streetAddress = ("1000 SE Main St.");
//		location.city = ("Portland");
//		location.state = ("OR");
//		GeoLocationHelper.setGeoLocation(location);
//
//		Root root = webResource
//				.path("display/by_time_of_day/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
//				.type("application/json")
//				.get(Root.class);
//
//		assertTrue(root.BikeRides.size() == 8); //1 is in salem and one is more than 1 day old
//		assertTrue(root.ClosestLocation.city.equals("Portland"));
//
//		location = new Location();
//		location.streetAddress = ("1224 2nd Street Northwest");
//		location.city = ("SALEM");
//		location.state = ("OR");
//		GeoLocationHelper.setGeoLocation(location);
//
//		root = webResource
//				.path("display/by_time_of_day/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
//				.type("application/json")
//				.get(Root.class);
//
//		assertTrue(root.BikeRides.size() == 4);
//		assertTrue(root.ClosestLocation.city.equals("Salem"));
//
//		location = new Location();
//		location.streetAddress = ("SE Ash & SE 22nd");
//		location.city = ("Portland");
//		location.state = ("OR");
//		GeoLocationHelper.setGeoLocation(location);
//
//		root = webResource
//				.path("display/by_time_of_day/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
//				.type("application/json")
//				.get(Root.class);
//
//		assertTrue(root.BikeRides.size() == 8);
//		assertTrue(root.ClosestLocation.city.equals("Portland"));
//
//		boolean validPath = true;
//		try {
//			root = webResource
//					.path("display/by_time_of_day/geoloc=765x43456,345.676543")
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
