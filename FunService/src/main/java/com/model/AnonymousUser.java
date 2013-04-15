package com.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jongo.marshall.jackson.id.Id;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author lancepoehler
 *
 */
@XmlRootElement
public class AnonymousUser {

	@Id
	public String id;
	public DeviceAccount deviceAccount = new DeviceAccount();
    public String imagePath;
    public String userName = "Anonymous"; //User should never change.
	public Long joinedTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
    public boolean readTipsForRideLeaders = false;
    public Long latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
    public int totalHostedBikeRideCount;
	
}
