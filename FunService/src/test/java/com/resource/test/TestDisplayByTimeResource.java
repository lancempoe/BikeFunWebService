//package com.resource.test;
//
//import com.db.MongoDatabase;
//import com.google.common.collect.Lists;
//import com.model.BikeRide;
//import com.model.GeoLoc;
//import com.model.Location;
//import com.model.Root;
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
//		GoogleGeocoderApiHelper.setGeoLocation(location);
//        GeoLoc geoLoc = location.geoLoc;
//
//        MongoDatabase.ConnectToDb();
//
//        try {
//            Root root = new Root();
//
//            DateTime todayDateTime = new DateTime().withZone(DateTimeZone.UTC).toDateMidnight().toDateTime(); // Joda time
//            Long yesterday = todayDateTime.minusDays(1).getMillis();  //
//            MongoCollection bikeCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.BIKERIDES);
//
//            Location closestLocation = CommonBikeRideCalls.getClosestActiveLocation(geoLoc, bikeCollection, yesterday);
//            root.ClosestLocation = closestLocation;
//            root.BikeRides = new ArrayList<BikeRide>();
//
//            if(closestLocation==null) {
//                root.ClosestLocation = new Location();
//                root.ClosestLocation.geoLoc = new GeoLoc();
//                Iterable<BikeRide> bikeRides = getRidesFromDB(yesterday, bikeCollection);
//
//                root.BikeRides.addAll(Lists.newArrayList(bikeRides));
//            } else {
//                //**(Identify the upcoming bike rides for the selected city: 1 DB Call)**
//                //Find all bike rides for the selected city (if user has default it may not be in the list of locations available.  different ways to display on the UI)
//                Iterable<BikeRide> bikeRides = getRidesFromDB(root.ClosestLocation.id, yesterday, bikeCollection);
//                root.BikeRides.addAll(Lists.newArrayList(bikeRides));
//            }
//
//            //**(Set tracking on bike rides: 2 DB call)
//            root.BikeRides = CommonBikeRideCalls.postBikeRideDBUpdates(root.BikeRides, geoLoc);
//            String test = "";
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
////        ClientResponse response = webResource
////				.path("display/by_time_of_day/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
////				.type("application/json")
////				.get(ClientResponse.class);
////
////        final int statusCode = response.getStatus();
////        if ((statusCode < 200) || (statusCode >= 300)) {
////            String message = "What?!?!";
////        }
////        Root root = response.getEntity(Root.class);
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
