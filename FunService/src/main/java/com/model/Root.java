package com.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lancepoehler
 *
 */
@XmlRootElement
public class Root {

	//Used for display
    @XmlElement(name="bikeRides", required=true, nillable=false, type=ArrayList.class)
	public List<BikeRide> BikeRides = new ArrayList<BikeRide>();
	public Location ClosestLocation = new Location();

}
