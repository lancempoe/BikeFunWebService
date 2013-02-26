package com.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.tools.DateAdapter;


@XmlRootElement
public class User {

	@XmlElement
	public int Id;
	@XmlElement
	public String UserName;
	@XmlElement
	public String Password;
	@XmlElement
	public String Email;
	@XmlJavaTypeAdapter( DateAdapter.class)
	@XmlElement
	public Date JoinedTimeStamp;
	@XmlElement
	public int RidesAddedCount;
	@XmlElement
	public  int RidesJoinedCount;
	@XmlElement
	public boolean ReadTipsForRideLeaders;
	@XmlElement
	public boolean ReadTermsOfAgreement;

	
}
