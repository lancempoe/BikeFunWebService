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
	public String id;
	public String bikeRideId;
	public GeoLoc geoLoc;
	public Long trackingTime;
	public String trackingUserId;
	public String trackingUserName;
}
