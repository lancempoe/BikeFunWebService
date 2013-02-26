package com.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Fun {
	
	public List<User> Users = new ArrayList<User>();
}
