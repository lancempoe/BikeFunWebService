package com.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jongo.marshall.jackson.id.Id;

import com.tools.DateAdapter;

@XmlRootElement
public class Tracking {

		@Id
		private String Id;
		private String BikeRideId;
		private GeoLoc GeoLoc;
		@XmlJavaTypeAdapter( DateAdapter.class)
		private Date TrackingTime;
		private String UserId = "Unknown"; //Should be overridden by the client.  Save to the client device until logged in.
		
		public String getId() {
			return Id;
		}
		public void setId(String id) {
			this.Id = id;
		}
		public String getBikeRideId() {
			return BikeRideId;
		}
		public void setBikeRideId(String bikeRideId) {
			this.BikeRideId = bikeRideId;
		}
		public GeoLoc getGeoLoc() {
			return GeoLoc;
		}
		public void setGeoLoc(GeoLoc geoLoc) {
			this.GeoLoc = geoLoc;
		}
		public Date getTrackingTime() {
			return TrackingTime;
		}
		public void setTrackingTime(Date trackingTime) {
			this.TrackingTime = trackingTime;
		}
		public String getUserId() {
			return UserId;
		}
		public void setUserId(String userId) {
			this.UserId = userId;
		}
}
