package com.resource;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.code.geocoder.Geocoder;
import com.google.common.collect.Lists;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Location;
import com.model.Root;
import com.tools.CommonBikeRideCalls;
import com.tools.GoogleGeocoderApiHelper;
import com.tools.TrackingHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.MongoCollection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 
 * @author lancepoehler
 *
 */
@Path("display/by_time_of_day")
@Produces(MediaType.APPLICATION_JSON)
public class DisplayByTimeResource {

    private static final Log LOG = LogFactory.getLog(Geocoder.class);

	/**
	 * Returns all items owned by client.
	 * Return all items that are in the future and back 1 week. Limit to 200 for now.
	 * Ordered by: Reverse chronology
	 * @param rideLeaderId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("geoloc={latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}/rideLeaderId={rideLeaderId}")
	public Response getDisplay(@PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude, @PathParam("rideLeaderId") String rideLeaderId)  {
        Response response;
        if (GoogleGeocoderApiHelper.isValidGeoLoc(latitude, longitude)) {
            GeoLoc geoLoc = new GeoLoc();
            geoLoc.latitude = latitude;
            geoLoc.longitude = longitude;
            response = getDisplayForClient(geoLoc, rideLeaderId);
        } else {
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: Invalid GeoLocation").build();
        }
        return response;
	}

	@GET
	@Path("geoloc={latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}")
	public Response getDisplay(@PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude) {
        Response response;
        if (GoogleGeocoderApiHelper.isValidGeoLoc(latitude, longitude)) {
            GeoLoc geoLoc = new GeoLoc();
            geoLoc.latitude = latitude;
            geoLoc.longitude = longitude;
            response = getDisplay(geoLoc, null);
        } else {
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: Invalid GeoLocation").build();
        }
        return response;
	}

	/**
	 * Main Display Sorted by Time
	 * - Cons: many calls to the DB
	 * - Pros: Returns everything in one JSON
	 *   
	 *   TODO: I need to investigate utilizing additional indexes
	 *   - http://docs.mongodb.org/manual/applications/indexes/
	 *   
	 *   TODO: 	might want to create a ttl index to remove expired data... or keep and backup:
	 *   - http://docs.mongodb.org/manual/tutorial/expire-data/
	 *   
	 * - Currently the Data Modeling Decision is to use References instead of embedding them.
	 *   The main reason for this is that Location and user would be replicate all over the place.
	 *   If we find that the response time is an issue we can migrate to an embedded approach.
	 *   See: http://docs.mongodb.org/manual/core/data-modeling/
     * @param geoLoc
     * @param selectedLocationId
     * @return
     */
	private Response getDisplay(GeoLoc geoLoc, String selectedLocationId) {
        Response response;
		try 
		{
            Root root = new Root();

			DateTime todayDateTime = new DateTime().withZone(DateTimeZone.UTC).toDateMidnight().toDateTime(); // Joda time
			Long yesterday = todayDateTime.minusDays(1).getMillis();  //
			MongoCollection bikeCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);

			Location closestLocation = CommonBikeRideCalls.getClosestActiveLocation(geoLoc, bikeCollection, yesterday);
			root.ClosestLocation = closestLocation;
            root.BikeRides = new ArrayList<BikeRide>();

            if(closestLocation==null) {
                LOG.error("ClosestLocation is null!! yesterday="+yesterday);
                root.ClosestLocation = new Location();
                root.ClosestLocation.geoLoc = new GeoLoc();
                Iterable<BikeRide> bikeRides = getRidesFromDB(yesterday, bikeCollection);

                root.BikeRides.addAll(Lists.newArrayList(bikeRides));
                LOG.error("root.BikeRides.="+root.BikeRides.size());
            } else {
                //**(Identify the upcoming bike rides for the selected city: 1 DB Call)**
                //Find all bike rides for the selected city (if user has default it may not be in the list of locations available.  different ways to display on the UI)
                Iterable<BikeRide> bikeRides = getRidesFromDB(root.ClosestLocation.id, yesterday, bikeCollection);
                root.BikeRides.addAll(Lists.newArrayList(bikeRides));
            }

			//**(Set tracking on bike rides: 2 DB call)
			TrackingHelper.setTracking(root.BikeRides, geoLoc);
            response = Response.status(Response.Status.OK).entity(root).build();
		}
		catch (Exception e)
		{
			LOG.error("Exception Error: ", e);
			e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
		}

		return response;
	}

    private Iterable<BikeRide> getRidesFromDB(String closetsLocationId, Long yesterday, MongoCollection bikeCollection) {
        return bikeCollection
                            .find("{rideStartTime: {$gt: #}, cityLocation: #}",
                                    yesterday,
                                    closetsLocationId)
                            .sort("{rideStartTime : 1}")
                            .limit(200)
                            .fields("{cityLocation: 0, rideLeaderId: 0, details: 0}") //TODO once we narrow down the UI we can cut down data further.
                            .as(BikeRide.class);
    }

    private Iterable<BikeRide> getRidesFromDB(Long yesterday, MongoCollection bikeCollection) {
        return bikeCollection
                .find("{rideStartTime: {$gt: #}}",
                        yesterday)
                .sort("{rideStartTime : 1}")
                .limit(200)
                .fields("{cityLocation: 0, rideLeaderId: 0, details: 0}") //TODO once we narrow down the UI we can cut down data further.
                .as(BikeRide.class);
    }

    /**
	 * 
	 * @param geoLoc
	 * @param rideLeaderId
	 * @return
	 * @throws Exception
	 */
	private Response getDisplayForClient(GeoLoc geoLoc, String rideLeaderId) {
        Response response;
		try 
		{
            Root root = new Root();
			MongoCollection bikeCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);

			///Find all bike rides for the client
			Iterable<BikeRide> bikeRides = bikeCollection
					.find("{rideLeaderId: #}", 
							rideLeaderId)
					.sort("{rideStartTime : -1}")
					.limit(200)
					.fields("{cityLocation: 0, rideLeaderId: 0, details: 0}") //TODO once we narrow down the UI we can cut down data further.
					.as(BikeRide.class);
			root.BikeRides = Lists.newArrayList(bikeRides);

			//**(Set tracking on bike rides: 2 DB call)
			TrackingHelper.setTracking(root.BikeRides, geoLoc);
            response = Response.status(Response.Status.OK).entity(root).build();
		}
		catch (Exception e)
		{
			LOG.error("Exception Error: ", e);
			e.printStackTrace();
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: " + e).build();
		}

		return response;
	}
}
