package com.model;

import java.util.ArrayList;
import java.util.List;

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
	public String id;
	public String bikeRideName;
	public String details;
	public String rideLeaderId;
	public String targetAudience;
	public Long rideStartTime;
	public Location location;
	public String cityLocationId;
	public String imagePath = "Images/BikeRides/defaultBikeRide.jpg"; //In the event that no image is provided.
	public boolean trackingAllowed = true; //Default tracking is turned on.

	//Generated and send back.  not in DB
	public String rideLeaderName;
	public Double distanceFromClient;
	public boolean currentlyTracking = false; //Default value
	public long totalPeopleTrackingCount = 0; //Default value
	
	//Generated and sent back to ride page.  Not saved in same collection
	public Tracking rideLeaderTracking;
	public List<Tracking> currentTrackings = new ArrayList<Tracking>();
}
