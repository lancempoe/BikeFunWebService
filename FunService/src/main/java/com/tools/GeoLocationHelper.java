package com.tools;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jongo.MongoCollection;

import com.db.MongoDatabase;
import com.db.MongoDatabase.MONGO_COLLECTIONS;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderStatus;
import com.model.BikeRide;
import com.model.GeoLoc;
import com.model.Location;

/**
 * 
 * @author lancepoehler
 *
 */
public class GeoLocationHelper {

    private static final Log LOG = LogFactory.getLog(Geocoder.class);

	/**
	 * I am currently calling the Google Geolocation API.  
	 * The limit for this api is 2400 per day.
	 * 
	 * @author lancepoehler
	 * @throws InterruptedException 
	 *
	 */
	public static boolean setGeoLocation(Location location) throws InterruptedException {
		LOG.debug("Calling Google GeoLocation API");


		try {
			final Geocoder geocoder = new Geocoder();
			GeocoderRequest geocoderRequest;
			GeocodeResponse geocoderResponse=null;

			//This will fail if it is not synchronized.  The classes are not threadsafe
			synchronized(GeoLocationHelper.class) {
                try {
                    geocoderRequest = new GeocoderRequestBuilder().setAddress(
                                                        (StringUtils.isNotBlank(location.streetAddress) ? location.streetAddress+", " : " ") +
                                                        (StringUtils.isNotBlank(location.city) ? location.city+", " : " ") +
                                                        location.state)
                            .setLanguage("en").getGeocoderRequest();

                    geocoderResponse = geocoder.geocode(geocoderRequest);
                } catch(IllegalArgumentException oops) {
                    LOG.error("Illegal geocoder request", oops);
                    return false;
                }
			}

			if (geocoderResponse==null ||
                    geocoderResponse.getStatus()==null ||
                    geocoderResponse.getStatus() != GeocoderStatus.OK) {
                return false;
            }

            String fullAddress = geocoderResponse.getResults().get(0).getFormattedAddress();
            if (geocoderResponse.getResults().get(0).isPartialMatch())
			{ 
				if (!fullAddress.toLowerCase().contains(location.city.toLowerCase()) ||
						!fullAddress.toLowerCase().contains(", "+location.state.toLowerCase())) {
					return false;
				}
			} else if (StringUtils.isNotBlank(location.streetAddress) && //This will check for crazy addresses that are not returned with an address
					   fullAddress.toLowerCase().startsWith(location.city.toLowerCase() + ", " + location.state.toLowerCase())) {
				return false;
			}

			GeoLoc geoLoc = new GeoLoc();
			geoLoc.latitude = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLat();
			geoLoc.longitude = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLng();
			location.geoLoc = (geoLoc);
			location.formattedAddress = fullAddress;

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean setBikeRideLocationId(BikeRide bikeRide) {
		try {
			//Check is current city exist
			MongoCollection locationCollection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
			Location location = locationCollection
					.findOne("{city:#, state:#, country:#}", 
							bikeRide.location.city, bikeRide.location.state, bikeRide.location.country)
							.as(Location.class);
			if (location == null) {
				//Add new location to the DB
				location = new Location();
				location.city = (bikeRide.location.city);
				location.state = (bikeRide.location.state);
				location.country = (bikeRide.location.country);
				GeoLocationHelper.setGeoLocation(location); //Call API for city center geoCode
				MongoCollection collection = MongoDatabase.Get_DB_Collection(MONGO_COLLECTIONS.LOCATIONS);
				collection.save(location);
			}
			bikeRide.cityLocationId = location.id;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * TODO: This can be replace when Jongo 0.4 is released in April
	 * See: https://github.com/bguerout/jongo/issues/115 
	 * @param event
	 * @param client
	 * @return
	 */
	public static double distFrom(GeoLoc event, GeoLoc client) {
		double lat1, lng1, lat2, lng2;
		lat1 = event.latitude.doubleValue();
		lng1 = event.longitude.doubleValue();
		lat2 = client.latitude.doubleValue();
		lng2 = client.longitude.doubleValue();

		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;
		return dist;
	}

	public static boolean isValidGeoLoc(BigDecimal latitude, BigDecimal longitude) {
		boolean isValid = true;
		if (latitude.compareTo(new BigDecimal(90)) > 0 ||
				latitude.compareTo(new BigDecimal(-90)) < 0 ||
				longitude.compareTo(new BigDecimal(180)) > 0 ||
				longitude.compareTo(new BigDecimal(-180)) < 0) {
			isValid = false;
		}
		return isValid;
	}

}
