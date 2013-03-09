package com.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {

	public List<User> Users = new ArrayList<User>();
	public List<BikeRide> BikeRides = new ArrayList<BikeRide>();
	public List<Location> Locations = new ArrayList<Location>();
	public Location ClosestLocation = new Location();
	public Location SelectedLocation = new Location();

}
