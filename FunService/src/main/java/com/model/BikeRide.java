package com.model;

import org.jongo.marshall.jackson.id.Id;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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
    public String rideLeaderName;
	public String targetAudience;
	public Long rideStartTime;
	public Location location;
	public String cityLocationId;
	public String imagePath;
	public boolean trackingAllowed = true; //Default tracking is turned on.

	//Generated and send back.  not in DB
	public Double distanceFromClient;
	public boolean currentlyTracking = false; //Default value
	public long totalPeopleTrackingCount = 0; //Default value
	
	//Generated and sent back to ride page.  Not saved in same collection
	public Tracking rideLeaderTracking;
	public List<Tracking> currentTrackings = new ArrayList<Tracking>();
}
