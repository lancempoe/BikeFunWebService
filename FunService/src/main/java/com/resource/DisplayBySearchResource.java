package com.resource;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.*;
import com.settings.SharedStaticValues;
import com.tools.CommonBikeRideCalls;
import com.tools.GoogleGeocoderApiHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.MongoCollection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for querying the db for a particular ride or type of ride.
 * 
 * @author lancepoehler
 *
 */
@Path("display/by_search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class DisplayBySearchResource {
    private static final Log LOG = LogFactory.getLog(DisplayBySearchResource.class);


	@POST
	@Path("geoloc={latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}")
	public Response getDisplay(Query query, @PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude) throws Exception {
		Response response;
        if (GoogleGeocoderApiHelper.isValidGeoLoc(latitude, longitude)) {
            if (query == null ||
                    (StringUtils.isBlank(query.query) &&
                     StringUtils.isBlank(query.targetAudience) &&
                     StringUtils.isBlank(query.city) &&
                     StringUtils.isBlank(query.rideLeaderId))) {
                response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Please make a selection.").build();
            } else {
                GeoLoc geoLoc = new GeoLoc();
                geoLoc.latitude = latitude;
                geoLoc.longitude = longitude;
                response = getDisplay(geoLoc, query);
            }
        } else {
            response = Response.status(Response.Status.PRECONDITION_FAILED).entity("Error: Invalid GeoLocation").build();
        }
        return response;
	}

	/**
	 * 1st: Identify the current city.
	 * 2nd: build the query provided by the client
	 * 3rd: return results.
	 * @param geoLoc
	 * @param query
	 * @return
	 * @throws Exception
	 */
	private Response getDisplay(GeoLoc geoLoc, Query query) throws Exception {
        Response response;
		try
		{
            Root root = new Root();

			MongoCollection bikeCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);

            List<Location> locations = new ArrayList<Location>();
            String locationQuery = "";
			if (StringUtils.isEmpty(query.city)) {
                DateTime todayDateTime = new DateTime().withZone(DateTimeZone.UTC).toDateMidnight().toDateTime(); // Joda time
                Long yesterday = todayDateTime.minusDays(1).getMillis();
                Location closestLocation = CommonBikeRideCalls.getClosestActiveLocation(geoLoc, bikeCollection, yesterday);
                root.ClosestLocation = closestLocation;
                locations.add(closestLocation);
			} else {
                MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);

                ///Find all matching locations
                Iterable<Location> locationsIterable = locationCollection
                        .find("{ city: {$regex: '.*"+query.city +".*', $options: 'i'}}")
                        .limit(200)
                        .as(Location.class);
                locations = Lists.newArrayList(locationsIterable);
            }
            for (Location location : locations) {
                locationQuery += ", \"" + location.id + "\"";
            }
            if (StringUtils.isNotBlank(locationQuery)){
                locationQuery = locationQuery.substring(2);
            }
			
			DateTime filterStartDateTime = null;
			DateTime filterEndDateTime = null;
            Long filterStartDateAsMilliSeconds = null;
            Long filterEndDateAsMilliSeconds = null;
			if (query.date != null) {
				filterStartDateTime = new DateTime(query.date);
				filterStartDateTime = filterStartDateTime.toDateMidnight().toDateTime();
				filterStartDateAsMilliSeconds = filterStartDateTime.toInstant().getMillis();
                filterEndDateAsMilliSeconds = filterStartDateTime.plusDays(1).toInstant().getMillis();
			} else {
                filterStartDateTime = DateTime.now();
                filterStartDateAsMilliSeconds = filterStartDateTime.toInstant().getMillis();
            }

            //Build the query
            String queryAsString = "{$and: [ ";
            if(StringUtils.isNotBlank(query.rideLeaderId)) { queryAsString += "{rideLeaderId: '" + query.rideLeaderId+"'}, "; }
            if(StringUtils.isNotBlank(query.query)) { queryAsString += "{$or: [ { bikeRideName: {$regex: '.*"+query.query+".*', $options: 'i'} }, {details: {$regex: '.*"+query.query+".*', $options: 'i'} } ] }, "; }
            if(StringUtils.isNotBlank(query.city)) { queryAsString += "{cityLocationId: {$all: [ " + locationQuery + " ] } }, "; }
            if(StringUtils.isNotBlank(query.targetAudience)) { queryAsString += "{targetAudience: '" + query.targetAudience+"'}, "; }
            if (filterEndDateTime != null) { queryAsString += "{rideStartTime: {$lte: "+filterEndDateAsMilliSeconds+", $gte: "+filterStartDateAsMilliSeconds+"} }, "; }
            else { queryAsString += "{rideStartTime: {$gte: "+filterStartDateAsMilliSeconds+"} }, "; }
            queryAsString = queryAsString.substring(0, queryAsString.length() - 2) + " ] }";

			Iterable<BikeRide> bikeRides = bikeCollection
					.find(queryAsString)
					.sort("{rideStartTime : 1}")
					.limit(200)
					.fields(SharedStaticValues.MAIN_PAGE_DISPLAY_FIELDS)
					.as(BikeRide.class);
			root.BikeRides = Lists.newArrayList(bikeRides);

			//**(Set tracking on bike rides: 2 DB call)
            root.BikeRides = CommonBikeRideCalls.postBikeRideDBUpdates(root.BikeRides, geoLoc);
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