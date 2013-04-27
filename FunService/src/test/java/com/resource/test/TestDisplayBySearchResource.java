//package com.resource.test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Logger;
//
//import com.db.MongoDatabase;
//import com.google.common.collect.Lists;
//import com.model.*;
//import com.tools.CommonBikeRideCalls;
//import com.tools.GoogleGeocoderApiHelper;
//import com.tools.TrackingHelper;
//import junit.framework.TestCase;
//
//import org.apache.commons.lang.StringUtils;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.jongo.MongoCollection;
//import org.junit.Test;
//
//import com.shared.PopulateDB;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.LoggingFilter;
//
//import javax.ws.rs.core.Response;
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
////
////		PopulateDB populate = new PopulateDB();
////		populate.populateDB(webResource);
//
//		Location location = new Location();
//		location.streetAddress = ("1000 SE Main St.");
//		location.city = ("Portland");
//		location.state = ("OR");
//		GoogleGeocoderApiHelper.setGeoLocation(location);
//
//
//        Query query = new Query();
//        query.query = "tacos";
//        query.city = "Portland";
//        query.targetAudience = "Boozy";
//
////        Root root  = webResource
////                .path("display/by_search/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
////                .type("application/json")
////                .post(Root.class, query);
//
//
//         ////////////
//
//        GeoLoc geoLoc = new GeoLoc();
//        geoLoc.latitude = location.geoLoc.latitude;
//        geoLoc.longitude = location.geoLoc.longitude;
//
//        Root root = new Root();
//
//        MongoDatabase.ConnectToDb();
//        MongoCollection bikeCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.BIKERIDES);
//
//        List<Location> locations = new ArrayList<Location>();
//        String locationQuery = "";
//        if (StringUtils.isEmpty(query.city)) {
//            DateTime todayDateTime = new DateTime().withZone(DateTimeZone.UTC).toDateMidnight().toDateTime(); // Joda time
//            Long yesterday = todayDateTime.minusDays(1).getMillis();
//            Location closestLocation = CommonBikeRideCalls.getClosestActiveLocation(geoLoc, bikeCollection, yesterday);
//            root.ClosestLocation = closestLocation;
//            locations.add(closestLocation);
//        } else {
//            MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.LOCATIONS);
//
//            ///Find all matching locations
//            Iterable<Location> locationsIterable = locationCollection
//                    .find("{ city: {$regex: '.*"+query.city +".*', $options: 'i'}}")
//                    .limit(200)
//                    .as(Location.class);
//            locations = Lists.newArrayList(locationsIterable);
//        }
//        for (Location location1 : locations) {
//            locationQuery += ", \"" + location1.id + "\"";
//        }
//        locationQuery = locationQuery.substring(2);
//
//        DateTime filterStartDateTime = null;
//        DateTime filterEndDateTime = null;
//        if (query.date != null) {
//            filterStartDateTime = new DateTime(query.date);
//            filterStartDateTime = filterStartDateTime.toDateMidnight().toDateTime();
//            filterEndDateTime = filterStartDateTime.plusDays(1);
//        }
//
//        //Build the query
//        String queryAsString = "{$and: [";
//        if(StringUtils.isNotBlank(query.rideLeaderId)) { queryAsString += "{rideLeaderId: '" + query.rideLeaderId+"'}, "; }
//        if(StringUtils.isNotBlank(query.query)) { queryAsString += "{$or: [{ bikeRideName: {$regex: '.*"+query.query+".*', $options: 'i'}}, { details: {$regex: '.*"+query.query+".*', $options: 'i'}}]}, "; }
//        if(StringUtils.isNotBlank(query.city)) { queryAsString += "{cityLocationId: { $all: [ " + locationQuery + " ]}}, "; }
//        if(StringUtils.isNotBlank(query.targetAudience)) { queryAsString += "{targetAudience: '" + query.targetAudience+"'}, "; }
//        if(filterStartDateTime != null) { queryAsString += "{rideStartTime: {$lte: "+filterEndDateTime+", $gte: "+filterStartDateTime+"}}, "; }
//        queryAsString = queryAsString.substring(0, queryAsString.length() - 2) + "]}";
//
//        Iterable<BikeRide> bikeRides = bikeCollection
//                .find(queryAsString)
//                .sort("{rideStartTime : 1}")
//                .limit(200)
//                .fields("{cityLocationId: 0, rideLeaderId: 0, details: 0}") //TODO once we narrow down the UI we can cut down data further.
//                .as(BikeRide.class);
//        root.BikeRides = Lists.newArrayList(bikeRides);
//
//        //**(Set tracking on bike rides: 2 DB call)
//        TrackingHelper.setTracking(root.BikeRides, geoLoc);
//        MongoDatabase.mongoClient.close();
//
//        /////////////
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//        assertTrue(root.BikeRides.size() == 1); //1 in portland, 1 in salem (should not include)
//
//		query = new Query();
//		query.query = "tacos";
//
//		root  = webResource
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
