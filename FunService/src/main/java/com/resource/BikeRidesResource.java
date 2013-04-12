package com.resource;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.model.AnonymousUser;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Location;
import com.model.User;
import com.tools.GeoLocationHelper;
import com.tools.SecurityTools;
import com.tools.TrackingHelper;

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
@Consumes (MediaType.APPLICATION_JSON)
public class BikeRidesResource {

	private static final Logger LOG = Logger.getLogger(BikeRidesResource.class.getCanonicalName());

	@GET
	@Path("{id}/geoloc={latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}")
	public BikeRide getBikeRide(@PathParam("id") String id, @PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude) {
		if (!GeoLocationHelper.isValidGeoLoc(latitude, longitude)) { return null; }

		GeoLoc geoLoc = new GeoLoc();
		geoLoc.latitude = latitude;
		geoLoc.longitude = longitude;
		return getRide(id, geoLoc);
	}

	/**
	 * Will be used to display the bike ride details page.  This includes all tracking details
	 * @param id
	 * @param geoLoc
	 * @return
	 * @throws Exception
	 */
	private BikeRide getRide(String id, GeoLoc geoLoc) {
		BikeRide bikeRide = null;
		try 
		{			
			//Get the object using Jongo
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			bikeRide = collection.findOne(new ObjectId(id)).as(BikeRide.class);

			//build tracking items.
			if (bikeRide != null) {
				//Get the distance from the client, if the ride is 
				//currently tracking, and total people that have at one time tracked this ride.
				TrackingHelper.setTracking(bikeRide, geoLoc);

				//Get leader tracking
				bikeRide.rideLeaderTracking = TrackingHelper.getRideLeaderTracking(bikeRide);

				//Get all current tracks
				bikeRide.currentTrackings = TrackingHelper.getAllCurrentTrackings(bikeRide);

				//Set ride leader name.
				String leader = "";
				MongoCollection auCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.ANONYMOUS_USERS);
				AnonymousUser rideLeaderAsAnonymousUser = auCollection.findOne(new ObjectId(bikeRide.rideLeaderId)).as(AnonymousUser.class);
				if (rideLeaderAsAnonymousUser != null) {
					leader = rideLeaderAsAnonymousUser.userName;
				} else {
					MongoCollection userCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.USERS);
					User rideLeaderAsUser = userCollection.findOne(new ObjectId(bikeRide.rideLeaderId)).as(User.class);
					leader = rideLeaderAsUser.userName;
				}
				bikeRide.rideLeaderName = leader;
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.INFO, "Exception Error when getting user: " + e.getMessage());
			e.printStackTrace();
		}
		return bikeRide;
	}

	@POST
	@Path("new")
	public BikeRide newBikeRide(BikeRide bikeRide) {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. New BikeRide request");

			//Validate real address:
			if (GeoLocationHelper.setGeoLocation(bikeRide.location) && //Call API for ride geoCodes
					GeoLocationHelper.setBikeRideLocationId(bikeRide)) {

                if (StringUtils.isEmpty(bikeRide.imagePath)) {
                    bikeRide.imagePath = ImageResource.BikeRideImageLocation + "defaultBikeRide.jpg";
                } else {
                    int i = bikeRide.imagePath.lastIndexOf('.');
                    String fileName = UUID.randomUUID() + bikeRide.imagePath.substring(i);
                    bikeRide.imagePath = ImageResource.BikeRideImageLocation + fileName;
                }

				//save the object using Jongo
				MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
				collection.save(bikeRide);

				response = Response.status(Response.Status.OK).build();
                //Send back the bikeRide so the ID can be obtained

			} else { 
				//Invalid address
				response = Response.status(Response.Status.CONFLICT).build();
			}

		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
            //TODO NEED TO SEND BACK SOMETHING ELSE.
		}
		return bikeRide;
	}

	/*
	 * Allows the owner to update their rides.  Only updates if there are changes to the bike ride.
	 * TODO: This security should be improved.  Maybe we could have a new key generated each and every time the app starts. 
	 * @param bikeRide
	 * @return
	 */
	@POST
	@Path("update/{userId}/{key}/{deviceUUID}/")
	public Response updateBikeRide(BikeRide updatedBikeRide, @PathParam("userId") String userId, @PathParam("key") String key, @PathParam("deviceUUID") String deviceUUID)  {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. Update BikeRide request");

			//Get the object and validate that the client has access to the ride.
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			BikeRide currentBikeRide = collection.findOne(new ObjectId(updatedBikeRide.id)).as(BikeRide.class);

			//Validate that the client has access
			if (currentBikeRide != null &&
					SecurityTools.isValidUser(userId, key, deviceUUID) &&
					SecurityTools.isValidOwnerOfRide(userId, currentBikeRide.rideLeaderId)) {

				//Do now allow user to update rideLeaderId or cityLocationId; for now.
				updatedBikeRide.rideLeaderId = currentBikeRide.rideLeaderId;
				Location updatedLocation = updatedBikeRide.location;
				Location currentLocation = currentBikeRide.location;

				if(
						((updatedLocation.streetAddress == null) ? (currentLocation.streetAddress != null) : !updatedLocation.streetAddress.equals(currentLocation.streetAddress)) ||
						((updatedLocation.city == null) ? (currentLocation.city != null) : !updatedLocation.city.equals(currentLocation.city)) ||
						((updatedLocation.state == null) ? (currentLocation.state != null) : !updatedLocation.state.equals(currentLocation.state)) ||
						((updatedLocation.zip == null) ? (currentLocation.zip != null) : !updatedLocation.zip.equals(currentLocation.zip)) ||
						((updatedLocation.country == null) ? (currentLocation.country != null) : !updatedLocation.country.equals(currentLocation.country))
						) {

					//Validate real address:
					if (!GeoLocationHelper.setGeoLocation(updatedBikeRide.location) || //Call API for ride geoCodes
							!GeoLocationHelper.setBikeRideLocationId(updatedBikeRide)) { //Set the location id
						return Response.status(Response.Status.BAD_REQUEST).build();
					}
				}

				//update the object
				collection.save(updatedBikeRide);

				response = Response.status(Response.Status.OK).build();
			} else {
				//Invalid user for this ride.
				response = Response.status(Response.Status.FORBIDDEN).build();
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}

	@POST
	@Path("delete/{userId}/{key}/{deviceUUID}/")
	public Response deleteBikeRide(BikeRide updatedBikeRide, @PathParam("userId") String userId, @PathParam("key") String key, @PathParam("deviceUUID") String deviceUUID) throws Exception {
		Response response;
		try {
			LOG.log(Level.FINEST, "Received POST XML/JSON Request. Delete BikeRide request");

			//Get the object and validate that the client has access to the ride.
			MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);
			BikeRide currentBikeRide = collection.findOne(new ObjectId(updatedBikeRide.id)).as(BikeRide.class);

			//Validate that the client has access
			if (currentBikeRide != null &&
					SecurityTools.isValidUser(userId, key, deviceUUID) &&
					SecurityTools.isValidOwnerOfRide(userId, currentBikeRide.rideLeaderId)) {

				collection.remove(new ObjectId(updatedBikeRide.id));

				response = Response.status(Response.Status.OK).build();
				LOG.log(Level.FINEST, "Delete BikeRide: " + updatedBikeRide.id);

			} else {
				//Client is not allowed to delete the selected role.
				response = Response.status(Response.Status.FORBIDDEN).build();
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,  e.getMessage());
			e.printStackTrace();
			response = Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return response;
	}
}
