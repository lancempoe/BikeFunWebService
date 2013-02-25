package com.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.tools.DateAdapter;


@XmlRootElement
public class Users {

	@XmlElement
	private int Id;
	@XmlElement
	private String UserName;
	@XmlElement
	private String Password;
	@XmlElement
	private String Email;
	@XmlJavaTypeAdapter( DateAdapter.class)
	@XmlElement
	private Date JoinedTimeStamp;
	@XmlElement
	private int RidesAddedCount;
	@XmlElement
	private int RidesJoinedCount;
	@XmlElement
	private boolean ReadTipsForRideLeaders;
	@XmlElement
	private boolean ReadTermsOfAgreement;

	public int getId() {
		return Id;
	}
	public void setId(int id) {
		this.Id = id;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public Date getJoinedTimeStamp() {
		return JoinedTimeStamp;
	}
	public void setJoinedTimeStamp(Date joinedTimeStamp) {
		JoinedTimeStamp = joinedTimeStamp;
	}
	public int getRidesAddedCount() {
		return RidesAddedCount;
	}
	public void setRidesAddedCount(int ridesAddedCount) {
		RidesAddedCount = ridesAddedCount;
	}
	public int getRidesJoinedCount() {
		return RidesJoinedCount;
	}
	public void setRidesJoinedCount(int ridesJoinedCount) {
		RidesJoinedCount = ridesJoinedCount;
	}
	public boolean isReadTipsForRideLeaders() {
		return ReadTipsForRideLeaders;
	}
	public void setReadTipsForRideLeaders(boolean readTipsForRideLeaders) {
		ReadTipsForRideLeaders = readTipsForRideLeaders;
	}
	public boolean isReadTermsOfAgreement() {
		return ReadTermsOfAgreement;
	}
	public void setReadTermsOfAgreement(boolean readTermsOfAgreement) {
		ReadTermsOfAgreement = readTermsOfAgreement;
	}
}
