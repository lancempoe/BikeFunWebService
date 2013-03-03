package com.model;

import java.util.Date;
import java.util.List;

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
	private String TargetAudience;
	@XmlJavaTypeAdapter( DateAdapter.class)
	private Date StartTime;
	private String StartStreetAddress;
	private String StartCity;
	private GeoLoc GeoLoc;
	private String ImagePath; //TODO Need to test putting the actual image in the DB vs external of DB.  http://stackoverflow.com/questions/4245787/how-to-insert-images-in-mongodb-using-java
	private String RideLeader;
	private int ParticipantCount;
	private List<String> Participants;

	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getBikeRideName() {
		return BikeRideName;
	}
	public void setBikeRideName(String bikeRideName) {
		BikeRideName = bikeRideName;
	}
	public String getDetails() {
		return Details;
	}
	public void setDetails(String details) {
		Details = details;
	}
	public String getTargetAudience() {
		return TargetAudience;
	}
	public void setTargetAudience(String targetAudience) {
		TargetAudience = targetAudience;
	}
	public Date getStartTime() {
		return StartTime;
	}
	public void setStartTime(Date startTime) {
		StartTime = startTime;
	}
	public String getStartStreetAddress() {
		return StartStreetAddress;
	}
	public void setStartStreetAddress(String startStreetAddress) {
		StartStreetAddress = startStreetAddress;
	}
	public String getStartCity() {
		return StartCity;
	}
	public void setStartCity(String startCity) {
		StartCity = startCity;
	}
	public GeoLoc getGeoLoc() {
		return GeoLoc;
	}
	public void setGeoLoc(GeoLoc geoLoc) {
		GeoLoc = geoLoc;
	}
	public String getImagePath() {
		return ImagePath;
	}
	public void setImagePath(String imagePath) {
		ImagePath = imagePath;
	}
	public String getRideLeader() {
		return RideLeader;
	}
	public void setRideLeader(String rideLeader) {
		RideLeader = rideLeader;
	}
	public int getParticipantCount() {
		return ParticipantCount;
	}
	public void setParticipantCount(int participantCount) {
		ParticipantCount = participantCount;
	}
	public List<String> getParticipants() {
		return Participants;
	}
	public void setParticipants(List<String> participants) {
		Participants = participants;
		if (participants != null) {
			ParticipantCount = participants.size(); 
		} else {
			ParticipantCount = 0;
		}
	}
}
