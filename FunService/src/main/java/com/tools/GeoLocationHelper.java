package com.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderStatus;
import com.model.GeoLoc;
import com.model.Location;

/**
 * I am currently calling the Google Geolocation API.  
 * The limit for this api is 2400 per day.
 * 
 * @author lancepoehler
 *
 */
public class GeoLocationHelper {

	private static final Logger LOG = Logger.getLogger(GeoLocationHelper.class.getCanonicalName());

	public static boolean setGeoLocation(Location location) {
		LOG.log(Level.FINEST, "Calling Google GeoLocation API");

		final Geocoder geocoder = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(
				((location.getStreetAddress() != null && !location.getStreetAddress().isEmpty()) ? location.getStreetAddress()+", " : " ") +
				((location.getCity() != null && !location.getCity().isEmpty()) ? location.getCity()+", " : " ") +
				location.getState())
				.setLanguage("en").getGeocoderRequest();
		GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
		
		if (geocoderResponse.getStatus() != GeocoderStatus.OK || geocoderResponse.getResults().get(0).isPartialMatch())
		{ 
			String fullAddress = geocoderResponse.getResults().get(0).getFormattedAddress();
			if (!fullAddress.toLowerCase().contains(location.getCity().toLowerCase()) || 
					!fullAddress.toLowerCase().contains(", "+location.getState().toLowerCase())) {
				return false;
			}
		}

		GeoLoc geoLoc = new GeoLoc();
		geoLoc.latitude = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLat();
		geoLoc.longitude = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLng();
		location.setGeoLoc(geoLoc);
		location.setFormattedAddress(geocoderResponse.getResults().get(0).getFormattedAddress());
		
		return true;
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

}
