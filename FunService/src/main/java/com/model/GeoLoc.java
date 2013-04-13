package com.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

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
