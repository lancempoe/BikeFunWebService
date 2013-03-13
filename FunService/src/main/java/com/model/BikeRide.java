package com.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.jongo.marshall.jackson.id.Id;

/**
 * 
 * @author lancepoehler
 *
 */
@XmlRootElement
public class BikeRide {

	@Id
	private String Id;
	private String BikeRideName;
	private String Details;
	private String RideLeaderId;
	private String TargetAudience;
	//@XmlJavaTypeAdapter( DateAdapter.class)
	private Long RideStartTime;
	private Location Location;
	private String CityLocationId;
	private String ImagePath = "Images/BikeRides/defaultBikeRide.jpg"; //In the event that no image is provided.
	private boolean TrackingAllowed = true; //Default tracking is turned on.

	//Generated and send back.  not in DB
	private Double DistanceFromClient;
	private boolean CurrentlyTracking = false; //Default value
	private long TotalPeopleTrackingCount = 0; //Default value

	public String getId() {
		return Id;
	}
	public void setId(String _id) {
		this.Id = _id;
	}
	public String getBikeRideName() {
		return this.BikeRideName;
	}
	public void setBikeRideName(String _bikeRideName) {
		this.BikeRideName = _bikeRideName;
	}
	public String getDetails() {
		return this.Details;
	}
	public void setDetails(String _details) {
		this.Details = _details;
	}
	public String getTargetAudience() {
		return this.TargetAudience;
	}
	public void setTargetAudience(String _targetAudience) {
		this.TargetAudience = _targetAudience;
	}
	public Long getRideStartTime() {
		return this.RideStartTime;
	}
	public void setRideStartTime(Long _rideStartTime) {
		this.RideStartTime = _rideStartTime;
	}
	public Location getLocation() {
		return Location;
	}
	public void setLocation(Location location) {
		Location = location;
	}
	public String getCityLocationId() {
		return CityLocationId;
	}
	public void setCityLocationId(String cityLocationId) {
		CityLocationId = cityLocationId;
	}
	public String getImagePath() {
		return this.ImagePath;
	}
	public void setImagePath(String _imagePath) {
		this.ImagePath = _imagePath;
	}
	public boolean isTrackingAllowed() {
		return TrackingAllowed;
	}
	public void setTrackingAllowed(boolean trackingAllowed) {
		TrackingAllowed = trackingAllowed;
	}
	public Double getDistanceFromClient() {
		return DistanceFromClient;
	}
	public void setDistanceFromClient(Double distanceFromClient) {
		this.DistanceFromClient = distanceFromClient;
	}
	public String getRideLeaderId() {
		return this.RideLeaderId;
	}
	public void setRideLeaderId(String rideLeaderId) {
		RideLeaderId = rideLeaderId;
	}
	public boolean isCurrentlyTracking() {
		return CurrentlyTracking;
	}
	public void setCurrentlyTracking(boolean currentlyTracking) {
		CurrentlyTracking = currentlyTracking;
	}
	public long getTotalPeopleTrackingCount() {
		return TotalPeopleTrackingCount;
	}
	public void setTotalPeopleTrackingCount(long l) {
		TotalPeopleTrackingCount = l;
	}
}
