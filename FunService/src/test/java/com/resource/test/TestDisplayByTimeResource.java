//package com.resource.test;
//
//import com.db.MongoDatabase;
//import com.google.common.collect.Lists;
//import com.model.BikeRide;
//import com.model.GeoLoc;
//import com.model.Location;
//import com.model.Root;
//import com.resource.DisplayByTimeResource;
//import com.settings.SharedStaticValues;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.LoggingFilter;
//import com.tools.CommonBikeRideCalls;
//import com.tools.GoogleGeocoderApiHelper;
//import junit.framework.TestCase;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.jongo.MongoCollection;
//import org.junit.Test;
//
//import javax.ws.rs.core.Response;
//import java.util.ArrayList;
//
///**
//* Web Service must be turned on: glassfish3/bin/asadmin start-domain or tomcat
//* Start the DB as well: mongod
//* @author lancepoehler
//*
//*/
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
//		Location location = new Location();
//		location.streetAddress = ("1000 Main St.");
//		location.city = ("seattle");
//		location.state = ("wa");
//		GoogleGeocoderApiHelper.setGeoLocation(location);
//        GeoLoc geoLoc = location.geoLoc;
//
//        MongoDatabase.ConnectToDb();
//        try {
//            DisplayByTimeResource displayByTimeResource = new DisplayByTimeResource();
//            Response response = displayByTimeResource.getDisplay(geoLoc.latitude, geoLoc.longitude);
//            String test = "";
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        MongoDatabase.mongoClient.close();
//
//
//
//	}
//
//    private Iterable<BikeRide> getRidesFromDB(Long yesterday, MongoCollection bikeCollection) {
//        return bikeCollection
//                .find("{rideStartTime: {$gt: #}}",
//                        yesterday)
//                .sort("{rideStartTime : 1}")
//                .limit(200)
//                .fields(SharedStaticValues.MAIN_PAGE_DISPLAY_FIELDS)
//                .as(BikeRide.class);
//    }
//
//    private Iterable<BikeRide> getRidesFromDB(String closetsLocationId, Long yesterday, MongoCollection bikeCollection) {
//        return bikeCollection
//                .find("{rideStartTime: {$gt: #}, cityLocationId: #}",
//                        yesterday,
//                        closetsLocationId)
//                .sort("{rideStartTime : 1}")
//                .limit(200)
//                .fields(SharedStaticValues.MAIN_PAGE_DISPLAY_FIELDS)
//                .as(BikeRide.class);
//    }
//
//}
