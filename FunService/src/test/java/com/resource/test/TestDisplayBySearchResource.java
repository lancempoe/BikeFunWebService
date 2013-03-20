//package com.resource.test;
//
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import junit.framework.TestCase;
//
//import org.apache.commons.lang.StringUtils;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.jongo.MongoCollection;
//import org.junit.Test;
//
//import com.db.MongoDatabase;
//import com.db.MongoDatabase.MONGO_COLLECTIONS;
//import com.google.common.collect.Lists;
//import com.model.BikeRide;
//import com.model.GeoLoc;
//import com.model.Location;
//import com.model.Query;
//import com.model.Root;
//import com.shared.PopulateDB;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.LoggingFilter;
//import com.tools.CommonBikeRideCalls;
//import com.tools.GeoLocationHelper;
//import com.tools.TrackingHelper;
//
///**
//* Web Service must be turned on: glassfish3/bin/asadmin start-domain or tomcat
//* Start the DB as well: mongod
//* @author lancepoehler
//*
//*/
//public class TestDisplayBySearchResource extends TestCase { //extends JerseyTest {
//
//	private static final Logger LOG = Logger.getLogger(TestDisplayBySearchResource.class.getCanonicalName());
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
//	public void testDisplay_By_Search() throws Exception {
//
//		Client client = Client.create(getDefaultClientConfig());
//		client.addFilter(new LoggingFilter());
//		WebResource webResource = client.resource(REST_URI);
//
//		//CLEAR THE DB WARNING.....
////		webResource
////		.path("/WARNING/CLEAR_AND_RESET_DB")
////		.type("application/json")
////		.post(ClientResponse.class);
//
//		PopulateDB populate = new PopulateDB();
//		populate.populateDB(webResource);
//
//		Location location = new Location();
//		location.streetAddress = ("1000 SE Main St.");
//		location.city = ("Portland");
//		location.state = ("OR");
//		GeoLocationHelper.setGeoLocation(location);
//
//		Query query = new Query();
//		query.query = "tacos";
//
//		Root root  = webResource
//				.path("display/by_search/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
//				.type("application/json")
//				.post(Root.class, query);
//
//		assertTrue(root.BikeRides.size() == 1); //1 in portland, 1 in salem (should not include)
//		assertTrue(root.ClosestLocation.city.equals("Portland"));
//
//		query = new Query();
//		query.query = "apple";
//
//		root  = webResource
//				.path("display/by_search/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
//				.type("application/json")
//				.post(Root.class, query);
//
//		assertTrue(root.BikeRides.size() == 2); //1 in name, 2 in details (1 of which is in salem).
//		assertTrue(root.ClosestLocation.city.equals("Portland"));
//
//		query = new Query();
//		query.targetAudience = "21+";
//
//		root  = webResource
//				.path("display/by_search/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
//				.type("application/json")
//				.post(Root.class, query);
//
//		assertTrue(root.BikeRides.size() == 1); //1 under 2 over (1 in salem);
//		assertTrue(root.ClosestLocation.city.equals("Portland"));
//
//	}
//}
