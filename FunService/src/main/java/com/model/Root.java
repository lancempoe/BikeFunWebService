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

	//Used for display
	public List<BikeRide> BikeRides = new ArrayList<BikeRide>();
	public Location ClosestLocation = new Location();

}
