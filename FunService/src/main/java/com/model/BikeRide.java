package com.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jongo.marshall.jackson.id.Id;

import com.tools.DateAdapter;

@XmlRootElement
public class BikeRide {

	@Id
	private String Id;
	private String BikeRideName;
	private String Details;
	private String RideLeaderId;
	private String TargetAudience;
	@XmlJavaTypeAdapter( DateAdapter.class)
	private Date StartTime;
	private Location Location;
	private String CityLocationId;
	private String ImagePath = "Images/BikeRides/defaultBikeRide.jpg"; //In the event that no image is provided.

	//Generated and send back.  not in DB
	private Double DistanceFromClient;
	@XmlJavaTypeAdapter( DateAdapter.class)
	private Date MostRecentTracking;
	private long TotalPeopleTrackingCount;

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
	public Date getStartTime() {
		return this.StartTime;
	}
	public void setStartTime(Date _startTime) {
		this.StartTime = _startTime;
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
	public Date getMostRecentTracking() {
		return MostRecentTracking;
	}
	public void setMostRecentTracking(Date mostRecentTracking) {
		MostRecentTracking = mostRecentTracking;
	}
	public long getTotalPeopleTrackingCount() {
		return TotalPeopleTrackingCount;
	}
	public void setTotalPeopleTrackingCount(long l) {
		TotalPeopleTrackingCount = l;
	}
}
