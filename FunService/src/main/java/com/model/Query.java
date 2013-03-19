package com.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Only fields that are populated will be used during the query.
 * @author lancepoehler
 *
 */
@XmlRootElement
public class Query {

	public String query;
	public String cityLocationId; //If not provided then use current location
	public Long date;
	public String targetAudience;

}
