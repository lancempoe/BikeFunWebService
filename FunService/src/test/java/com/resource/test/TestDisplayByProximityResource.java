//package com.resource.test;
//
//import com.db.MongoDatabase;
//import com.google.common.collect.Lists;
//import com.model.BikeRide;
//import com.model.GeoLoc;
//import com.settings.SharedStaticValues;
//import com.tools.CommonBikeRideCalls;
//import com.tools.GoogleGeocoderApiHelper;
//import junit.framework.TestCase;
//
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.jongo.MongoCollection;
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
//import javax.ws.rs.core.Response;
//import java.util.List;
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
//        MongoDatabase.ConnectToDb();
//
//        Location geolocation = new Location();
//        geolocation.streetAddress = ("1000 SE Main St.");
//        geolocation.city = ("Portland");
//        geolocation.state = ("OR");
//        GoogleGeocoderApiHelper.setGeoLocation(geolocation);
//        GeoLoc geoLoc = geolocation.geoLoc;
//
//
//        try
//        {
//            Double ONE_DEGREE_IN_MILES = 69.11; //1 degree of latitude = about 69.11 miles.
//            int RADIUS_IN_MILES = 3;
//            int TIME_IN_MINUTES = 60;
//
//            Root root = new Root();
//
//            //**(Identify the closest city to the client: 1 DB Call)**
//            MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.LOCATIONS, "geoLoc");
//            //coll.ensureIndex("{geoLoc: '2d'}") is set when getting the collection
//            Location closestLocation = locationCollection
//                    .findOne("{geoLoc: {$near: [#, #]}}",
//                            geoLoc.longitude,
//                            geoLoc.latitude)
//                    .as(Location.class);
//            root.ClosestLocation = closestLocation;
//
//            //**(Get BikeRide list: 3 calls to the DB)**
//            DateTime nowDateTime = new DateTime(DateTimeZone.UTC); // Joda time
//            DateTime maxStartTime = nowDateTime.plusMinutes(TIME_IN_MINUTES);
//            DateTime minStartTime = nowDateTime.minusDays(1); //This will cut out most old bike rides
//            Long now = nowDateTime.toInstant().getMillis();
//            Long max = maxStartTime.toInstant().getMillis();
//            Long min = minStartTime.toInstant().getMillis();
//
//            //Get the objects using Jongo
//            MongoCollection bikeRidesCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.BIKERIDES, "location.geoLoc");
//            //Currently not placing a limit on this result.  If we find this is an issue we can add later.
//
//            bikeRidesCollection.ensureIndex("{location.geoLoc: '2d', rideStartTime: 1}");
//            Iterable<BikeRide> all = bikeRidesCollection
//                    .find("{location.geoLoc: {$near: [#, #], $maxDistance: #}, rideStartTime: {$lte: #, $gte: #}}",
//                            geoLoc.longitude,
//                            geoLoc.latitude,
//                            RADIUS_IN_MILES/ONE_DEGREE_IN_MILES,
//                            max ,
//                            min )
//                    .fields(SharedStaticValues.MAIN_PAGE_DISPLAY_FIELDS)
//                    .as(BikeRide.class);
//            List<BikeRide> closeBikeRides = Lists.newArrayList(all);
//
//            //**(Set tracking on bike rides: 2 DB call)
//            closeBikeRides = CommonBikeRideCalls.postBikeRideDBUpdates(closeBikeRides, geoLoc);
//
//            for(BikeRide closeBikeRide : closeBikeRides) {
//                //Find all rides that haven't started AND find all bike rides still being tracked.
//                if (closeBikeRide.rideStartTime > now || closeBikeRide.currentlyTracking) {
//                    root.BikeRides.add(closeBikeRide);
//                }
//            }
//            String test = "";
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        MongoDatabase.mongoClient.close();
//        String test = "";
//	}
//}
