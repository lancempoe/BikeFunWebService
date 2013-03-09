package com.tools;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * MongoDB uses UTC dates.
 * @author lancepoehler
 *
 */
public class DateAdapter extends XmlAdapter<String, Date> {

	private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
	private static final TimeZone utc = TimeZone.getTimeZone("UTC");
	private SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_FORMAT);


	@Override
	public String marshal(Date v) throws Exception {
		dateFormat.setTimeZone(utc);
		return dateFormat.format(v);
	}

	@Override
	public Date unmarshal(String v) throws Exception {
		dateFormat.setTimeZone(utc);
		return dateFormat.parse(v);
	}

}