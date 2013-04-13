package com.model;

import org.jongo.marshall.jackson.id.Id;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author lancepoehler
 *
 */
@XmlRootElement
public class Location {

	@Id
	public String id;
	public String streetAddress;
	public String city;
	public String state;
	public String zip;
	public String country;
	public GeoLoc geoLoc;
	public String formattedAddress;
}
