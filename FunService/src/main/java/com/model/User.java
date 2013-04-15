package com.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
public class User {

    @Id
	public String id;
    public OAuth oAuth;
	public String userName;
	public String email;
	public List<DeviceAccount> deviceAccounts = new ArrayList<DeviceAccount>();
    public DeviceAccount deviceAccount = new DeviceAccount();
	public boolean accountActivated = true;
	public String imagePath;
	public Long joinedTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
	public boolean readTipsForRideLeaders = false;
    public Long latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
	public int totalHostedBikeRideCount;

}
