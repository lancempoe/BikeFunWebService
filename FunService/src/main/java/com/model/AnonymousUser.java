package com.model;

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
public class AnonymousUser {

	@Id
	public String id;
	public DeviceAccounts deviceAccounts = new DeviceAccounts();
	public String userName = "Anonymous"; //User should never change.
	public Long joinedTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
	
}
