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
    public String foreignId;
    public String foreignIdType;
	public String userName;
	public String email;
	public List<DeviceAccounts> deviceAccounts = new ArrayList<DeviceAccounts>();
    public DeviceAccounts deviceAccount = new DeviceAccounts();
	public boolean accountActivated = true;
	public String imagePath;
	public Long joinedTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
	public boolean readTipsForRideLeaders = false;
    public Long latestActiveTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
	public int totalHostedBikeRideCount;

}
