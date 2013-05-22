//package com.resource.test;
//
//import com.db.MongoDatabase;
//import com.model.*;
//import com.settings.SharedStaticValues;
//import com.sun.jersey.api.client.ClientResponse;
//import com.tools.CommonBikeRideCalls;
//import com.tools.GoogleGeocoderApiHelper;
//import com.tools.ImageHelper;
//import com.tools.SecurityTools;
//import junit.framework.TestCase;
//
//import org.apache.commons.lang.StringUtils;
//import org.bson.types.ObjectId;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.jongo.MongoCollection;
//import org.junit.Test;
//
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.client.filter.LoggingFilter;
//
//import javax.ws.rs.core.Response;
//import java.util.ArrayList;
//import java.util.List;
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
//    @Test
//    public void testGetBikeRide() {
//
//        try {
////            Client client = Client.create(getDefaultClientConfig());
////            client.addFilter(new LoggingFilter());
////            WebResource webResource = client.resource(REST_URI);
//
//            MongoDatabase.ConnectToDb();
//
//            Location geolocation = new Location();
//            geolocation.streetAddress = ("1000 SE Main St.");
//            geolocation.city = ("Portland");
//            geolocation.state = ("OR");
//            GoogleGeocoderApiHelper.setGeoLocation(geolocation);
//            GeoLoc geoLoc = geolocation.geoLoc;
//
//            BikeRide bikeRide = null;
//
//            //Get the object using Jongo
//            MongoCollection collection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.BIKERIDES);
//            bikeRide = collection.findOne(new ObjectId("519ad9e30364f1fec3ade80e")).as(BikeRide.class);
//
//            //build tracking items.
//            if (bikeRide != null) {
//                bikeRide = CommonBikeRideCalls.postBikeRideDBUpdates(bikeRide, geoLoc);
//            }
//
//            MongoDatabase.mongoClient.close();
//
//            String test = "";
//
//        } catch (Exception e) {
//            String test = "";
//        }
//
//    }
//
////	@Test
////	public void testDisplayBikeRide() {
////		try {
////			Client client = Client.create(getDefaultClientConfig());
////			client.addFilter(new LoggingFilter());
////			WebResource webResource = client.resource(REST_URI);
////
////            Location geolocation = new Location();
////            geolocation.streetAddress = ("1000 SE Main St.");
////            geolocation.city = ("Portland");
////            geolocation.state = ("OR");
////            GoogleGeocoderApiHelper.setGeoLocation(geolocation);
////            GeoLoc geoLoc = geolocation.geoLoc;
////
////            BikeRide bikeRide = new BikeRide();
////            bikeRide.bikeRideName = "lance ride updated";
////            bikeRide.targetAudience = "Family Friendly";
////            Location location = new Location();
////            location.city = "portland";
////            location.state = "or";
////            location.streetAddress = "800 SE Ankeny";
////            bikeRide.location = location;
////            bikeRide.details = "lkj";
////            bikeRide.trackingAllowed = true;
////            bikeRide.rideStartTime = new Long("1368646200000");
////            bikeRide.id = "51947d120364cf253dc145d7";
////
////            AnonymousUser anonymousUser = new AnonymousUser();
////            anonymousUser.id = "5192642a0364eb6f86974361";
////            DeviceAccount deviceAccount = new DeviceAccount();
////            deviceAccount.deviceUUID = "55AF6E1D-F0B9-4F13-9F9F-3B2ACAF0B923";
////            deviceAccount.key = "1367287282251";
////            anonymousUser.deviceAccount = deviceAccount;
////            anonymousUser.imagePath = "www.BikeFunFinder.com/BikeFunFinderImages/Users/defaultUser.jpg";
////            anonymousUser.userName = "Anonymous";
////            anonymousUser.joinedTimeStamp = new Long("1368548394390");
////            anonymousUser.readTipsForRideLeaders = false;
////            anonymousUser.latestActiveTimeStamp = new Long("1368685842936");
////            anonymousUser.totalHostedBikeRideCount = 9;
////
////            Root root = new Root();
////            List<BikeRide> bikeRideList = new ArrayList<BikeRide>();
////            bikeRideList.add(bikeRide);
////            root.BikeRides = bikeRideList;
////            root.AnonymousUser = anonymousUser;
////
////            SharedStaticValues.UpdateType type = SharedStaticValues.UpdateType.UPDATE_TYPE;
////
////            MongoDatabase.ConnectToDb();
////
////            try
////            {
////                //copy in code here.
////                String userId = "";
////                boolean validUser = false;
////
////                if (root.AnonymousUser != null) {
////                    userId = root.AnonymousUser.id;
////                    validUser = SecurityTools.isValidAnonymousUser(userId,
////                            root.AnonymousUser.deviceAccount.key,
////                            root.AnonymousUser.deviceAccount.deviceUUID);
////                } else if (root.User != null && root.User.oAuth != null && root.User.deviceAccount != null) {
////                    userId = root.User.id;
////                    validUser = SecurityTools.isLoggedIn(root.User) && SecurityTools.isValidUser(userId, root.User.deviceAccount.deviceUUID);
////                }
////
////                //Validate that the client has access
////                if (validUser && root.BikeRides != null && root.BikeRides.size() == 1) {
////
////                    //Get the object and validate that the client has access to the ride.
////                    MongoCollection collectionBikeRides = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.BIKERIDES);
////                    BikeRide updatedBikeRide = root.BikeRides.get(0);
////                    BikeRide currentBikeRide = collectionBikeRides.findOne(new ObjectId(updatedBikeRide.id)).as(BikeRide.class);
////
////                    if (currentBikeRide != null) {
////                        if (SecurityTools.isValidOwnerOfRide(userId, currentBikeRide.rideLeaderId)) {
////
////                            switch (type) {
////                                case UPDATE_TYPE:
////                                    //Do not allow user to update rideLeaderId or cityLocationId
////                                    updatedBikeRide.rideLeaderId = currentBikeRide.rideLeaderId;
////                                    Location updatedLocation = updatedBikeRide.location;
////                                    Location currentLocation = currentBikeRide.location;
////
////                                    if (
////                                            ((updatedLocation.streetAddress == null) ? (currentLocation.streetAddress != null) : !updatedLocation.streetAddress.equals(currentLocation.streetAddress)) ||
////                                            ((updatedLocation.city == null) ? (currentLocation.city != null) : !updatedLocation.city.equals(currentLocation.city)) ||
////                                            ((updatedLocation.state == null) ? (currentLocation.state != null) : !updatedLocation.state.equals(currentLocation.state)) ||
////                                            ((updatedLocation.zip == null) ? (currentLocation.zip != null) : !updatedLocation.zip.equals(currentLocation.zip)) ||
////                                            ((updatedLocation.country == null) ? (currentLocation.country != null) : !updatedLocation.country.equals(currentLocation.country))
////                                            ) {
////
////                                        //Validate real address:
////                                        if (!GoogleGeocoderApiHelper.setGeoLocation(updatedBikeRide.location) || //Call API for ride geoCodes
////                                                !GoogleGeocoderApiHelper.setBikeRideLocationId(updatedBikeRide)) { //Set the location id
////                                            String fail = "";
////                                            ///////return Response.status(Response.Status.BAD_REQUEST).build();
////                                        }
////                                    } else {
////                                        updatedLocation = currentLocation;
////                                    }
////
////                                    //Delete old image if needed.
////                                    if (StringUtils.isNotBlank(currentBikeRide.imagePath) &&
////                                        !currentBikeRide.imagePath.equals(updatedBikeRide.imagePath)) {
////                                        //Delete Old
////                                        ImageHelper imageHelper = new ImageHelper();
////                                        imageHelper.deleteImage(currentBikeRide.imagePath);
////
////                                    }
////
////                                    //Add new image if needed
////                                    if (StringUtils.isNotBlank(updatedBikeRide.imagePath) &&
////                                        !updatedBikeRide.imagePath.equals(currentBikeRide.imagePath)) {
////                                        //Update to new image path
////                                        ////////updatedBikeRide.imagePath = getImagePath(updatedBikeRide.imagePath);
////                                    }
////
////                                    //update the object
////                                    collectionBikeRides.save(updatedBikeRide);
////
////                                    updatedBikeRide = CommonBikeRideCalls.postBikeRideDBUpdates(updatedBikeRide, geoLoc);
////
////
////                                    String success = "";
////                                    ///////response = Response.status(Response.Status.OK).entity(updatedBikeRide).build();
////
////                                    /////////LOG.info("Update BikeRide: " + updatedBikeRide.id);
////                                    break;
////                                case DELETE_TYPE:
//////                                    //Remove ride
//////                                    collectionBikeRides.remove(new ObjectId(currentBikeRide.id));
//////
//////                                    //Remove ride image
//////                                    ImageHelper imageHelper = new ImageHelper();
//////                                    imageHelper.deleteImage(currentBikeRide.imagePath);
//////
//////                                    int totalHostedBikeRideCount = (int) collectionBikeRides.count("{rideLeaderId:#}", currentBikeRide.rideLeaderId);
//////                                    updateTotalHostedBikeRideCount(currentBikeRide.rideLeaderId, totalHostedBikeRideCount);
//////
//////                                    response = Response.status(Response.Status.OK).entity("Bike Ride Deleted").build();
//////
//////                                    LOG.info("Delete BikeRide: " + currentBikeRide.id);
//////                                    break;
////                            }
////
////                            //Update the user with updated active timestamp
////                            updateLatestActiveTimeStamp(updatedBikeRide.rideLeaderId);
////
////                        } else {
////                            String fail = "";
////                            ///////response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: You are not the owner of this ride").build();
////                        }
////                    } else {
////                        String fail = "";
////                        ///////response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: Invalid Bike Ride").build();
////                    }
////                } else {
////                    //Invalid user for this ride.
////                    String fail = "";
////                    ///////response = Response.status(Response.Status.FORBIDDEN).entity("Error: No Access").build();
////                }
////            }
////            catch (Exception e)
////            {
////                e.printStackTrace();
////            }
////
////            MongoDatabase.mongoClient.close();
////
////
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////	}
////
////    private void updateLatestActiveTimeStamp(String userId) {
////        try {
////            //Update User that created the ride.
////            MongoCollection auCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.ANONYMOUS_USERS);
////            AnonymousUser rideLeaderAsAnonymousUser = auCollection.findOne(new ObjectId(userId)).as(AnonymousUser.class);
////            if (rideLeaderAsAnonymousUser != null) {
////                rideLeaderAsAnonymousUser.latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
////                auCollection.save(rideLeaderAsAnonymousUser);
////            } else {
////                MongoCollection userCollection = MongoDatabase.Get_DB_Collection(MongoDatabase.MONGO_COLLECTIONS.USERS);
////                User rideLeaderAsUser = userCollection.findOne(new ObjectId(userId)).as(User.class);
////                rideLeaderAsUser.latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
////                userCollection.save(rideLeaderAsUser);
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
////
////	@Test
////	public void testFunServiceNewBikeRide() {
////		try {
////			Client client = Client.create(getDefaultClientConfig());
////			client.addFilter(new LoggingFilter());
////			WebResource webResource = client.resource(REST_URI);
////
////			//setup
////			DateTime now = new DateTime();
////
////			//Add a few Bike Rides with userId
////			BikeRide bikeRide = new BikeRide();
////			bikeRide.bikeRideName = "1: One Day in the Future Ride: Apple ride";
////
////			Long future = now.plusDays(1).getMillis();  //
////			bikeRide.rideStartTime = future;
////			bikeRide.details = "You need to come and eat these pears?";
////			bikeRide.rideLeaderId = "123456789";
////			Location location = new Location();
////			location.streetAddress = ("1500 SE Ash St.");
////			location.city = ("Portland");
////			location.state = ("OR");
////			bikeRide.location = location;
////            bikeRide.imagePath = "test.jpg";
////
////
////            //Save the resized image
////            int i = bikeRide.imagePath.lastIndexOf('.');
////            String extention = bikeRide.imagePath.substring(i+1);
////
////
////            ClientResponse response = webResource
////					.path("bikerides/new")
////					.type("application/json")
////					.post(ClientResponse.class, bikeRide);
////
////            final int statusCode = response.getStatus();
////            if ((statusCode < 200) || (statusCode >= 300)) {
////                String message = "What?!?!";
////            }
////            bikeRide = response.getEntity(BikeRide.class);
////
////			assertTrue(bikeRide.imagePath == "something different");
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////	}
////
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
