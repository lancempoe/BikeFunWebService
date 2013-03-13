package com.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.jongo.marshall.jackson.id.Id;

/**
 * 
 * @author lancepoehler
 *
 */
@XmlRootElement
public class Tracking {

	@Id
	private String Id;
	private String BikeRideId;
	private GeoLoc GeoLoc;
	private Long TrackingTime;
	private String UserId;
	private String UserName = "Unknown"; //Should be overridden by the client.  Save to the client device until logged in.

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
	public Long getTrackingTime() {
		return TrackingTime;
	}
	public void setTrackingTime(Long trackingTime) {
		this.TrackingTime = trackingTime;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		this.UserId = userId;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
}
