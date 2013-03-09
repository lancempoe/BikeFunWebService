package com.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.jongo.marshall.jackson.id.Id;

@XmlRootElement
public class Location {

	@Id
	private String Id;
	private String StreetAddress;
	private String City;
	private String State;
	private String Zip;
	private String Country;
	private GeoLoc GeoLoc;
	private String FormattedAddress;

	public String getCity() {
		return City;
	}
	public String getCountry() {
		return Country;
	}
	public String getFormattedAddress() {
		return FormattedAddress;
	}
	public GeoLoc getGeoLoc() {
		return GeoLoc;
	}
	public String getId() {
		return Id;
	}
	public String getState() {
		return State;
	}
	public String getStreetAddress() {
		return StreetAddress;
	}
	public String getZip() {
		return Zip;
	}
	public void setCity(String city) {
		this.City = city;
	}
	public void setCountry(String country) {
		Country = country;
	}
	public void setFormattedAddress(String formattedAddress) {
		FormattedAddress = formattedAddress;
	}
	public void setGeoLoc(GeoLoc geoLoc) {
		this.GeoLoc = geoLoc;
	}
	public void setId(String id) {
		Id = id;
	}
	public void setState(String state) {
		this.State = state;
	}
	public void setStreetAddress(String streetAddress) {
		StreetAddress = streetAddress;
	}
	public void setZip(String zip) {
		Zip = zip;
	}

}
