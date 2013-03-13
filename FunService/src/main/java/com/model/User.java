package com.model;

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
	private String Id;
	private String UserName;
	private String Password;
	private String Email;
	private boolean AccountActivated;
	private String ImagePath = "Images/Users/defaultUser.jpg"; //In the event that no image is provided.
	private Long JoinedTimeStamp; //Defaulting with a Date of now.
	private int HostedBikeRideCount;
	private int JoinedBikeRideCount;
	private List<Integer> HostedBikeRides;
	private List<Integer> JoinedBikeRides;
	private boolean ReadTipsForRideLeaders;
	private boolean ReadTermsOfAgreement;

	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
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
	public boolean isAccountActivated() {
		return AccountActivated;
	}
	public void setAccountActivated(boolean accountActivated) {
		AccountActivated = accountActivated;
	}
	public String getImagePath() {
		return ImagePath;
	}
	public void setImagePath(String imagePath) {
		ImagePath = imagePath;
	}
	public Long getJoinedTimeStamp() {
		if (JoinedTimeStamp == null) //Default to now
		{
			JoinedTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
		}
		return JoinedTimeStamp;
	}
	public void setJoinedTimeStamp(Long joinedTimeStamp) {
		JoinedTimeStamp = joinedTimeStamp;
	}
	public DateTime getJoinedTimeStampAsDateTime() {
		if (JoinedTimeStamp == null) //Default to now
		{
			JoinedTimeStamp = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();
		}
		return new DateTime(JoinedTimeStamp);
	}
	public void setJoinedTimeStampAsDateTime(DateTime joinedTimeStamp) {
		JoinedTimeStamp = joinedTimeStamp.getMillis();
	}
	public int getHostedBikeRideCount() {
		return HostedBikeRideCount;
	}
	public int getJoinedBikeRideCount() {
		return JoinedBikeRideCount;
	}
	public List<Integer> getHostedBikeRides() {
		return HostedBikeRides;
	}
	public void setHostedBikeRides(List<Integer> hostedBikeRides) {
		HostedBikeRides = hostedBikeRides;
		if (hostedBikeRides != null) {
			HostedBikeRideCount = hostedBikeRides.size(); 
		} else {
			HostedBikeRideCount = 0;
		}

	}
	public List<Integer> getJoinedBikeRides() {
		return JoinedBikeRides;
	}
	public void setJoinedBikeRides(List<Integer> joinedBikeRides) {
		JoinedBikeRides = joinedBikeRides;
		if (joinedBikeRides != null) { 
			JoinedBikeRideCount = joinedBikeRides.size(); 
		} else {
			JoinedBikeRideCount = 0;
		}
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
