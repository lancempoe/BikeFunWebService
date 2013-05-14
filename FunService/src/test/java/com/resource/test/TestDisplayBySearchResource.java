//package com.resource.test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Logger;
//
//import com.db.MongoDatabase;
//import com.google.common.collect.Lists;
//import com.model.*;
//import com.settings.SharedStaticValues;
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
//        GeoLoc geoLoc = location.geoLoc;
//
//        Query query = new Query();
//        query.query = "";
//        query.city = "portland";
//        query.targetAudience = "";
//
////        Root root  = webResource
////                .path("display/by_search/geoloc="+ location.geoLoc.latitude + "," + location.geoLoc.longitude)
////                .type("application/json")
////                .post(Root.class, query);
//
//        MongoDatabase.ConnectToDb();
//
//        try
//        {
//            Root root = new Root();
//
//            BikeRide bikeRide = new BikeRide();
//            Location location1 = new Location();
//            location1.city = "PortlAND";
//            location1.state = "Or";
//            bikeRide.location = location1;
//
//            Location LOCALlocation = new Location();
//            List<Location> LOCALlocations = new ArrayList<Location>();
//
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(bikeRide.location.city).append(", ").append(bikeRide.location.state);
//            if (StringUtils.isNotBlank(bikeRide.location.country)) {
//                stringBuilder.append(", ").append(bikeRide);
//            }
//
//            //Check is current city exist
//            MongoCollection locallocationCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.LOCATIONS);
//            Iterable<Location> locallocationsIterable = locallocationCollection
//                    .find("{formattedAddress: {$regex: '"+ stringBuilder.toString()+".*', $options: 'i'} }")
//                    .limit(1)
//                    .as(Location.class);
//            LOCALlocations = Lists.newArrayList(locallocationsIterable);
//
//            if (LOCALlocations == null || LOCALlocations.size() == 0) {
//                //Add new location to the DB
//                String shit = "";
//            } else {
//                LOCALlocation = LOCALlocations.get(0);
//            }
//            bikeRide.cityLocationId = LOCALlocation.id;
//
//            MongoDatabase.mongoClient.close();
//
//
//
//            MongoCollection bikeCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.BIKERIDES);
//
//            List<Location> locations = new ArrayList<Location>();
//            String locationQuery = "";
//            if (StringUtils.isEmpty(query.city)) {
//                DateTime todayDateTime = new DateTime().withZone(DateTimeZone.UTC).toDateMidnight().toDateTime(); // Joda time
//                Long yesterday = todayDateTime.minusDays(1).getMillis();
//                Location closestLocation = CommonBikeRideCalls.getClosestActiveLocation(geoLoc, bikeCollection, yesterday);
//                root.ClosestLocation = closestLocation;
//                locations.add(closestLocation);
//            } else {
//                MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.LOCATIONS);
//
//                ///Find all matching locations
//                Iterable<Location> locationsIterable = locationCollection
//                        .find("{ city: {$regex: '.*"+query.city +".*', $options: 'i'}}")
//                        .limit(200)
//                        .as(Location.class);
//                locations = Lists.newArrayList(locationsIterable);
//            }
//            for (Location myLocation : locations) {
//                locationQuery += ", \"" + myLocation.id + "\"";
//            }
//            locationQuery = locationQuery.substring(2);
//
//            DateTime filterStartDateTime = null;
//            DateTime filterEndDateTime = null;
//            Long filterStartDateAsMilliSeconds = null;
//            Long filterEndDateAsMilliSeconds = null;
//            if (query.date != null) {
//                filterStartDateTime = new DateTime(query.date);
//                filterStartDateTime = filterStartDateTime.toDateMidnight().toDateTime();
//                filterStartDateAsMilliSeconds = filterStartDateTime.toInstant().getMillis();
//                filterEndDateAsMilliSeconds = filterStartDateTime.plusDays(1).toInstant().getMillis();
//            } else {
//                filterStartDateTime = DateTime.now();
//                filterStartDateAsMilliSeconds = filterStartDateTime.toInstant().getMillis();
//            }
//
//            //Build the query
//            String queryAsString = "{$and: [ ";
//            if(StringUtils.isNotBlank(query.rideLeaderId)) { queryAsString += "{rideLeaderId: '" + query.rideLeaderId+"'}, "; }
//            if(StringUtils.isNotBlank(query.query)) { queryAsString += "{$or: [ { bikeRideName: {$regex: '.*"+query.query+".*', $options: 'i'} }, {details: {$regex: '.*"+query.query+".*', $options: 'i'} } ] }, "; }
//            if(StringUtils.isNotBlank(query.city)) { queryAsString += "{cityLocationId: {$all: [ " + locationQuery + " ] } }, "; }
//            if(StringUtils.isNotBlank(query.targetAudience)) { queryAsString += "{targetAudience: '" + query.targetAudience+"'}, "; }
//            if (filterEndDateTime != null) { queryAsString += "{rideStartTime: {$lte: "+filterEndDateAsMilliSeconds+", $gte: "+filterStartDateAsMilliSeconds+"} }, "; }
//            else { queryAsString += "{rideStartTime: {$gte: "+filterStartDateAsMilliSeconds+"} }, "; }
//            queryAsString = queryAsString.substring(0, queryAsString.length() - 2) + " ] }";
//
//            Iterable<BikeRide> bikeRides = bikeCollection
//                    .find(queryAsString)
//                    .sort("{rideStartTime : 1}")
//                    .limit(200)
//                    .fields(SharedStaticValues.MAIN_PAGE_DISPLAY_FIELDS)
//                    .as(BikeRide.class);
//            root.BikeRides = Lists.newArrayList(bikeRides);
//
//            //**(Set tracking on bike rides: 2 DB call)
//            root.BikeRides = CommonBikeRideCalls.postBikeRideDBUpdates(root.BikeRides, geoLoc);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//
//
//
//
//        MongoDatabase.mongoClient.close();
//
//	}
//}
