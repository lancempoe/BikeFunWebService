package com.tools;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

public class TestTools {

	@Test
	public void testDateAdapter() throws Exception {
		Date now = new Date();
		DateAdapter da = new DateAdapter();
		Long dateAsString = now.getTime();
		Date newNow = da.unmarshal(dateAsString);	

		assertTrue(now.equals(newNow));
	}

}
