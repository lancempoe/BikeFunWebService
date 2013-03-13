package com.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author lancepoehler
 *
 */
@XmlRootElement
public class Root {

	public List<User> Users = new ArrayList<User>();
	public List<BikeRide> BikeRides = new ArrayList<BikeRide>();
	public Location ClosestLocation = new Location();

}
