package com.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author lancepoehler
 *
 */
@XmlRootElement
public class GeoLoc {

	public BigDecimal longitude;
	public BigDecimal latitude;

}
