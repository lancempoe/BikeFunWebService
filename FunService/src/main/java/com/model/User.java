package com.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.marshall.jackson.id.Id;

/**
 * 
 * @author lancepoehler
 *
 */
@XmlRootElement
public class User {

	@Id
	public String id;
	public String userName;
	public String email;
	public List<DeviceAccounts> deviceAccounts = new ArrayList<DeviceAccounts>();
	public boolean accountActivated;
	public String imagePath = "Images/Users/defaultUser.jpg"; //In the event that no image is provided.
	public Long joinedTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
	public boolean readTipsForRideLeaders;

	//Generated and send back.  not in DB
	public List<String> hostedBikeRides;
	public int hostedBikeRideCount;

}
