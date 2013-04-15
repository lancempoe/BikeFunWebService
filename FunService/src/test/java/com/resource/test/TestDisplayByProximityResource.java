//package com.resource.test;
//
//import com.tools.GoogleGeocoderApiHelper;
//import junit.framework.TestCase;
//
//import org.junit.Test;
//
//import com.model.Location;
//import com.model.Root;
//import com.shared.PopulateDB;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
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
//		PopulateDB populate = new PopulateDB();
//		populate.populateDB(webResource);
//
//		Location location = new Location();
//		location.streetAddress = ("1000 SE Main St.");
//		location.city = ("Portland");
//		location.state = ("OR");
//		GoogleGeocoderApiHelper.setGeoLocation(location);
//
//		Root root = webResource
//				.path("display/by_proximity/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
//				.type("application/json")
//				.get(Root.class);
//
//		assertTrue(root.BikeRides.size() == 2); //1 close and in 10 mins and 1 10 mins ago but still tracking.
//		assertTrue(root.ClosestLocation.city.equals("Portland"));
//
//		location = new Location();
//		location.streetAddress = ("1224 2nd Street Northwest");
//		location.city = ("SALEM");
//		location.state = ("OR");
//		GoogleGeocoderApiHelper.setGeoLocation(location);
//
//		root = webResource
//				.path("display/by_proximity/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
//				.type("application/json")
//				.get(Root.class);
//
//		assertTrue(root.BikeRides.size() == 2); //1 in past but tracking and 1 in 10 minutes.
//		assertTrue(root.ClosestLocation.city.equals("Salem"));
//	}
//}
