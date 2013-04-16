package com.tools;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class TestTools {

	@Test
	public void testDateAdapter() throws Exception {
		Date now = new Date();
		DateAdapter da = new DateAdapter();
		Long dateAsString = now.getTime();
		Date newNow = da.unmarshal(dateAsString);	

		assertTrue(now.equals(newNow));
	}

    @Test
    public void testDateToLongToStringRoundTrip() throws Exception {

        //Date to long
        Long myLong = new DateTime().withZone(DateTimeZone.UTC).toInstant().getMillis();

        //Long to string
        String myString = myLong.toString();

        //String to a long
        Long myReturnedLong = Long.parseLong(myString);

        assertTrue(myLong.equals(myReturnedLong));
    }

}
