package com.tools;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * MongoDB uses UTC dates.
 * @author lancepoehler
 * 
 * I have disabled this for now.  This can and will work if needed.  Simply saving UTC times for now.
 *
 */
public class DateAdapter extends XmlAdapter<Long, Date> {

	@Override
	public Long marshal(Date v) throws Exception {
		return v.getTime();
	}

	@Override
	public Date unmarshal(Long v) throws Exception {
		Date date = new Date(v);
		return date;
	}

}