package com.model;

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
	public List<BikeRide> BikeRides = new ArrayList<BikeRide>();
	public Location ClosestLocation = new Location();
    public User User = new User();
    public AnonymousUser AnonymousUser = new AnonymousUser();

}
