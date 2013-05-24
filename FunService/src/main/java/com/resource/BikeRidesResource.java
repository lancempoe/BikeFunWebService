package com.resource;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.*;
import com.settings.SharedStaticValues;
import com.tools.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.MongoCollection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Mongo with Jongo.
 * 
 * See: http://jongo.org
 * 	NOTE: Field selection aka. partial loading is not written as in Mongo shell: 
 * 	Jongo exposes a fields(..) method. A json selector must be provided: 
 * 		{field: 1} to include it, 
 * 		{field: 0} to exclude it.
 * @author lance poehler
 *
 */
@Path("/bikerides")
@Produces(MediaType.APPLICATION_JSON)
public class BikeRidesResource {
    private static final Log LOG = LogFactory.getLog(BikeRidesResource.class);

	@GET
	@Path("{id},{clientId}/geoloc={latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}")
	public Response getBikeRide(@PathParam("clientId") String clientId, @PathParam("id") String id, @PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude) {
        Response response;
        if (GoogleGeocoderApiHelper.isValidGeoLoc(latitude, longitude)) {
            GeoLoc geoLoc = new GeoLoc();
            geoLoc.latitude = latitude;
            geoLoc.longitude = longitude;
            response = getRide(id, geoLoc, clientId);
        } else {
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: Invalid GeoLocation").build();
        }
        return response;
	}

	/**
	 * Will be used to display the bike ride details page.  This includes all tracking details
	 * @param id
	 * @param geoLoc
	 * @return
	 * @throws Exception
	 */
	protected Response getRide(String id, GeoLoc geoLoc, String clientId) {
        Response response = null;
        try
		{
            BikeRide bikeRide = null;

            //Get the object using Jongo
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			bikeRide = collection.findOne(new ObjectId(id)).as(BikeRide.class);

			//build tracking items.
			if (bikeRide != null) {
				bikeRide = CommonBikeRideCalls.postBikeRideDBUpdates(bikeRide, geoLoc);

                //Clear out all clientId tracks... They won't need to see their own tracks.. clutters the screen.
                if (bikeRide.rideLeaderTracking!=null && bikeRide.rideLeaderTracking.trackingUserName.equals(clientId)) {
                    bikeRide.rideLeaderTracking = null;
                }
                List<Tracking> trackings = new ArrayList<Tracking>();

                if (bikeRide.currentTrackings != null) {
                    for (Tracking tracking : bikeRide.currentTrackings) {
                        if (!tracking.trackingUserId.equals(clientId)) {
                            trackings.add(tracking);
                        }
                    }
                }
                bikeRide.currentTrackings = trackings;

                response = Response.status(Response.Status.OK).entity(bikeRide).build();
			}
		}
		catch (Exception e)
		{
			LOG.info("Exception Error when getting user: " + e.getMessage());
			e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
		}
		return response;
	}



    @POST
    @Path("new/geoloc={latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}")
    @Consumes (MediaType.APPLICATION_JSON)
    public Response newBikeRide(BikeRide bikeRide, @PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude) {
        Response response;
        LOG.info("Received POST XML/JSON Request. New BikeRide request");

        try {
            if (bikeRide != null &&
                    StringUtils.isNotBlank(bikeRide.bikeRideName) &&
                    StringUtils.isNotBlank(bikeRide.rideLeaderId) &&
                    bikeRide.rideStartTime != null &&
                    StringUtils.isNotBlank(bikeRide.details)) {

                if (GoogleGeocoderApiHelper.isValidGeoLoc(latitude, longitude)) {
                    GeoLoc geoLoc = new GeoLoc();
                    geoLoc.latitude = latitude;
                    geoLoc.longitude = longitude;

                    //Validate real address:
                    if (GoogleGeocoderApiHelper.setGeoLocation(bikeRide.location) && //Call API for ride geoCodes
                            GoogleGeocoderApiHelper.setBikeRideLocationId(bikeRide)) {

                        bikeRide.imagePath = getImagePath(bikeRide.imagePath);

                        //save the object using Jongo
                        MongoCollection collectionBikeRides = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
                        collectionBikeRides.save(bikeRide);

                        final int totalHostedBikeRideCount = (int) collectionBikeRides.count("{rideLeaderId:#}", bikeRide.rideLeaderId);
                        updateTotalHostedBikeRideCount(bikeRide.rideLeaderId, totalHostedBikeRideCount);

                        bikeRide = CommonBikeRideCalls.postBikeRideDBUpdates(bikeRide, geoLoc);

                        response = Response.status(Response.Status.OK).entity(bikeRide).build();

                    } else {
                        //Invalid address
                        LOG.info("Invalid address, we're not making the ride sucker!");
                        response = Response.status(Response.Status.CONFLICT).entity("Invalid Address").build();
                    }

                } else {
                    response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: Invalid GeoLocation").build();
                }
            } else {
                response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Please complete all fields.").build();
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
        }
        return response;
    }

    /*
	 * Allows the owner to update their rides.  Only updates if there are changes to the bike ride.
	 * @param bikeRide
	 * @return
	 */
    @POST
    @Path("update/geoloc={latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}")
    @Consumes (MediaType.APPLICATION_JSON)
    public Response updateBikeRide(Root root, @PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude)  {
        Response response;
		try {
            if (GoogleGeocoderApiHelper.isValidGeoLoc(latitude, longitude)) {
                GeoLoc geoLoc = new GeoLoc();
                geoLoc.latitude = latitude;
                geoLoc.longitude = longitude;
			    LOG.info("Received POST XML/JSON Request. Update BikeRide request");
                response = changeBikeRide(root, geoLoc, SharedStaticValues.UpdateType.UPDATE_TYPE);
            } else {
                response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: Invalid GeoLocation").build();
            }
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e.getMessage()).build();
		}
		return response;
	}

	@POST
	@Path("delete")
    @Consumes (MediaType.APPLICATION_JSON)
	public Response deleteBikeRide(Root root) throws Exception {
		Response response;
		try {
			LOG.info("Received POST XML/JSON Request. Delete BikeRide request");
            response = changeBikeRide(root, null, SharedStaticValues.UpdateType.DELETE_TYPE);
		} catch (Exception e) {
			LOG.error(e);
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e.getMessage()).build();
		}
		return response;
	}

    private Response changeBikeRide(Root root, GeoLoc geoLoc, SharedStaticValues.UpdateType type) throws  Exception {
        Response response = null;
        String userId = "";
        boolean validUser = false;

        if (root.AnonymousUser != null) {
            userId = root.AnonymousUser.id;
            validUser = SecurityTools.isValidAnonymousUser(userId,
                    root.AnonymousUser.deviceAccount.key,
                    root.AnonymousUser.deviceAccount.deviceUUID);
        } else if (root.User != null && root.User.oAuth != null && root.User.deviceAccount != null) {
            userId = root.User.id;
            validUser = SecurityTools.isLoggedIn(root.User) && SecurityTools.isValidUser(userId, root.User.deviceAccount.deviceUUID);
        }

        //Validate that the client has access
        if (validUser && root.BikeRides != null && root.BikeRides.size() == 1) {

            //Get the object and validate that the client has access to the ride.
            MongoCollection collectionBikeRides = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
            BikeRide updatedBikeRide = root.BikeRides.get(0);
            BikeRide currentBikeRide = collectionBikeRides.findOne(new ObjectId(updatedBikeRide.id)).as(BikeRide.class);

            if (currentBikeRide != null) {
                if (SecurityTools.isValidOwnerOfRide(userId, currentBikeRide.rideLeaderId)) {

                    switch (type) {
                        case UPDATE_TYPE:
                            //Do not allow user to update rideLeaderId or cityLocationId
                            updatedBikeRide.rideLeaderId = currentBikeRide.rideLeaderId;
                            updatedBikeRide.rideLeaderName = currentBikeRide.rideLeaderName;
                            Location updatedLocation = updatedBikeRide.location;
                            Location currentLocation = currentBikeRide.location;

                            LOG.info("Updating.. ");
                            if (
                                    ((updatedLocation.streetAddress == null) ? (currentLocation.streetAddress != null) : !updatedLocation.streetAddress.equals(currentLocation.streetAddress)) ||
                                            ((updatedLocation.city == null) ? (currentLocation.city != null) : !updatedLocation.city.equals(currentLocation.city)) ||
                                            ((updatedLocation.state == null) ? (currentLocation.state != null) : !updatedLocation.state.equals(currentLocation.state)) ||
                                            ((updatedLocation.zip == null) ? (currentLocation.zip != null) : !updatedLocation.zip.equals(currentLocation.zip)) ||
                                            ((updatedLocation.country == null) ? (currentLocation.country != null) : !updatedLocation.country.equals(currentLocation.country))
                                    ) {
                                //Validate real address:
                                if (!GoogleGeocoderApiHelper.setGeoLocation(updatedBikeRide.location) || //Call API for ride geoCodes
                                        !GoogleGeocoderApiHelper.setBikeRideLocationId(updatedBikeRide)) { //Set the location id
                                    return Response.status(Response.Status.BAD_REQUEST).build();
                                }
                            } else {
                                //Pull in the geo details.
                                updatedBikeRide.location = currentBikeRide.location;
                                updatedBikeRide.cityLocationId = currentBikeRide.cityLocationId;
                            }

                            //New image indicator
                            boolean newImage = false;

                            //Delete old image if needed.
                            if (StringUtils.isNotBlank(currentBikeRide.imagePath) &&
                                    !currentBikeRide.imagePath.equals(updatedBikeRide.imagePath)) {
                                ImageHelper imageHelper = new ImageHelper();
                                imageHelper.deleteImage(currentBikeRide.imagePath);
                                newImage = true;
                            }

                            //Add new image if needed
                            if (StringUtils.isNotBlank(updatedBikeRide.imagePath) &&
                                    !updatedBikeRide.imagePath.equals(currentBikeRide.imagePath)) {
                                newImage = true;
                            }

                            if (newImage) {
                                updatedBikeRide.imagePath = getImagePath(updatedBikeRide.imagePath);
                            }

                            //update the object
                            collectionBikeRides.save(updatedBikeRide);

                            updatedBikeRide = CommonBikeRideCalls.postBikeRideDBUpdates(updatedBikeRide, geoLoc);

                            response = Response.status(Response.Status.OK).entity(updatedBikeRide).build();

                            LOG.info("Update BikeRide: " + updatedBikeRide.id);
                            break;
                        case DELETE_TYPE:
                            //Remove ride
                            collectionBikeRides.remove(new ObjectId(currentBikeRide.id));

                            //Remove ride image
                            ImageHelper imageHelper = new ImageHelper();
                            imageHelper.deleteImage(currentBikeRide.imagePath);

                            //Update user TotalHostedBikeRideCount
                            final int totalHostedBikeRideCount = (int) collectionBikeRides.count("{rideLeaderId:#}", currentBikeRide.rideLeaderId);
                            updateTotalHostedBikeRideCount(currentBikeRide.rideLeaderId, totalHostedBikeRideCount);

                            //Delete all tracks
                            TrackingHelper.deleteTrackings(currentBikeRide);

                            response = Response.status(Response.Status.OK).entity("Bike Ride Deleted").build();

                            LOG.info("Delete BikeRide: " + currentBikeRide.id);
                            break;
                    }

                    //Update the user with updated active timestamp
                    updateLatestActiveTimeStamp(updatedBikeRide.rideLeaderId);

                } else {
                    response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: You are not the owner of this ride").build();
                }
            } else {
                response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: Invalid Bike Ride").build();
            }
        } else {
            //Invalid user for this ride.
            response = Response.status(Response.Status.FORBIDDEN).entity("Error: No Access").build();
        }
        return response;
    }

    private void updateTotalHostedBikeRideCount(String rideLeaderId, int totalHostedBikeRideCount) {
        try {
            //Update User that created the ride.
            MongoCollection auCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.ANONYMOUS_USERS);
            AnonymousUser rideLeaderAsAnonymousUser = auCollection.findOne(new ObjectId(rideLeaderId)).as(AnonymousUser.class);
            if (rideLeaderAsAnonymousUser != null) {
                rideLeaderAsAnonymousUser.totalHostedBikeRideCount = totalHostedBikeRideCount;
                rideLeaderAsAnonymousUser.latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
                auCollection.save(rideLeaderAsAnonymousUser);
            } else {
                MongoCollection userCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
                User rideLeaderAsUser = userCollection.findOne(new ObjectId(rideLeaderId)).as(User.class);
                rideLeaderAsUser.totalHostedBikeRideCount = totalHostedBikeRideCount;
                rideLeaderAsUser.latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
                userCollection.save(rideLeaderAsUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLatestActiveTimeStamp(String userId) {
        try {
            //Update User that created the ride.
            MongoCollection auCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.ANONYMOUS_USERS);
            AnonymousUser rideLeaderAsAnonymousUser = auCollection.findOne(new ObjectId(userId)).as(AnonymousUser.class);
            if (rideLeaderAsAnonymousUser != null) {
                rideLeaderAsAnonymousUser.latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
                auCollection.save(rideLeaderAsAnonymousUser);
            } else {
                MongoCollection userCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
                User rideLeaderAsUser = userCollection.findOne(new ObjectId(userId)).as(User.class);
                rideLeaderAsUser.latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
                userCollection.save(rideLeaderAsUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getImagePath(String startingImagePath) {
        String newImagePath = "";
        if (StringUtils.isEmpty(startingImagePath)) {
            newImagePath = ImageResource.BikeRideImageUrl + ImageHelper.defaultBikeRideImage;
        } else {
            int i = startingImagePath.lastIndexOf('.');
            String fileName = UUID.randomUUID() + startingImagePath.substring(i);
            newImagePath = ImageResource.BikeRideImageUrl + fileName;
        }
        return newImagePath;
    }
}
