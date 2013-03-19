package com.resource;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.common.collect.Lists;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Location;
import com.model.Query;
import com.model.Root;
import com.tools.CommonBikeRideCalls;
import com.tools.GeoLocationHelper;
import com.tools.TrackingHelper;

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

	private static final Logger LOG = Logger.getLogger(DisplayBySearchResource.class.getCanonicalName());

	@POST
	@Path("geoloc={latitude: ([-]?[0-9]+).([0-9]+)},{longitude: ([-]?[0-9]+).([0-9]+)}")
	public Root getDisplay(Query query, @PathParam("latitude") BigDecimal latitude, @PathParam("longitude") BigDecimal longitude) throws Exception {
		if (!GeoLocationHelper.isValidGeoLoc(latitude, longitude)) { return null; }
		if (query == null || 
				(StringUtils.isBlank(query.query) && 
						StringUtils.isBlank(query.targetAudience) && 
						StringUtils.isBlank(query.cityLocationId))) { return null; }

		GeoLoc geoLoc = new GeoLoc();
		geoLoc.latitude = latitude;
		geoLoc.longitude = longitude;
		return getDisplay(geoLoc, query);
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
	private Root getDisplay(GeoLoc geoLoc, Query query) throws Exception {

		Root root = new Root();
		try 
		{
			DateTime todayDateTime = new DateTime().withZone(DateTimeZone.UTC).toDateMidnight().toDateTime(); // Joda time
			Long yesterday = todayDateTime.minusDays(1).getMillis();  //
			MongoCollection bikeCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.BIKERIDES);

			Location closestLocation = CommonBikeRideCalls.getClosestActiveLocation(geoLoc, bikeCollection, yesterday);
			root.ClosestLocation = closestLocation;

			if (StringUtils.isEmpty(query.cityLocationId)) {
				query.cityLocationId = closestLocation.id;
			}
			
			DateTime filterStartDateTime = null;
			DateTime filterEndDateTime = null;
			if (query.date != null) {
				filterStartDateTime = new DateTime(query.date);
				filterStartDateTime = filterStartDateTime.toDateMidnight().toDateTime();
				filterEndDateTime = filterStartDateTime.plusDays(1);
			}

			//Build the OR query
			String queryOrString = "";
			if(StringUtils.isNotBlank(query.query)) {
				queryOrString += "$or: [{ bikeRideName: {$regex: '.*"+query.query+".*', $options: 'i'}}, { details: {$regex: '.*"+query.query+".*', $options: 'i'}}], "; 
			}

			//Build the remaining query
			String queryAsString = "{" + queryOrString + "cityLocationId: #";
			if(StringUtils.isNotBlank(query.targetAudience)) { queryAsString += ", targetAudience: '"+query.targetAudience+"'"; }
			if(filterStartDateTime != null) { queryAsString += ", rideStartTime: {$lte: "+filterEndDateTime+", $gte: "+filterStartDateTime+"}"; }
			queryAsString += "}";		 

			Iterable<BikeRide> bikeRides = bikeCollection
					.find(queryAsString, query.cityLocationId)
					.sort("{rideStartTime : 1}")
					.limit(200)
					.fields("{cityLocationId: 0, rideLeaderId: 0, details: 0}") //TODO once we narrow down the UI we can cut down data further.
					.as(BikeRide.class);
			root.BikeRides = Lists.newArrayList(bikeRides);

			//**(Set tracking on bike rides: 2 DB call)
			TrackingHelper.setTracking(root.BikeRides, geoLoc);

		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception Error: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		//**(Return Root)**
		return root;
	}

}
