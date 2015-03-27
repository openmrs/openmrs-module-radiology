/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.utils;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.Verifies;

/**
 * Tests {@link DateTimeUtils}
 */
public class DateTimeUtilsTest {
	
	static final String PLAIN_DATE_FORMAT = "yyyyMMdd";
	
	static final String PLAIN_TIME_FORMAT = "HHmmss";
	
	static final String PLAIN_DATETIME_FORMAT = "yyyyMMddHHmmss";
	
	static final String FULL_DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * Tests the DateTimeUtils.getPlainDateFrom method mapping date to string
	 * 
	 * @throws ParseException
	 * @see {@link DateTimeUtils#getPlainDateFrom(Date)}
	 */
	@Test
	@Verifies(value = "should return date string in plain format for given date", method = "getPlainDateFrom(Date)")
	public void getPlainDateFrom_shouldReturnDateStringInPlainFormatForGivenDate() throws ParseException {
		
		SimpleDateFormat plainDateFormat = new SimpleDateFormat(PLAIN_DATE_FORMAT);
		assertEquals("19700101", DateTimeUtils.getPlainDateFrom(plainDateFormat.parse("19700101")));
		assertEquals("20000120", DateTimeUtils.getPlainDateFrom(plainDateFormat.parse("20000120")));
		assertEquals("20150101", DateTimeUtils.getPlainDateFrom(plainDateFormat.parse("20150101")));
		
		SimpleDateFormat fullDateTimeFormat = new SimpleDateFormat(FULL_DATETIME_FORMAT);
		assertEquals("19700101", DateTimeUtils.getPlainDateFrom(fullDateTimeFormat.parse("01-1-1970 12:43:11")));
		assertEquals("20000120", DateTimeUtils.getPlainDateFrom(fullDateTimeFormat.parse("20-1-2000 23:59:59")));
		assertEquals("20150120", DateTimeUtils.getPlainDateFrom(fullDateTimeFormat.parse("20-01-2015 12:43:11")));
	}
	
	/**
	 * Tests the DateTimeUtils.getPlainDateFrom method with null
	 * 
	 * @see {@link DateTimeUtils#getPlainDateFrom(Date)}
	 */
	@Test
	@Verifies(value = "should return empty string given null", method = "getPlainDateFrom(Date)")
	public void getPlainDateFrom_shouldReturnEmptyStringGivenNull() {
		
		assertEquals("", DateTimeUtils.getPlainDateFrom(null));
	}
	
	/**
	 * Tests the DateTimeUtils.getCurrentPlainDate
	 * 
	 * @throws ParseException
	 * @see {@link DateTimeUtils#getCurrentPlainDate()}
	 */
	@Test
	@Verifies(value = "should return current date string in plain format", method = "getCurrentPlainDate()")
	public void getCurrentPlainDate_shouldReturnCurrentDateStringInPlainFormat() throws ParseException {
		
		SimpleDateFormat plainDateFormat = new SimpleDateFormat(PLAIN_DATE_FORMAT);
		
		Date now = new Date();
		assertEquals(plainDateFormat.format(now), DateTimeUtils.getCurrentPlainDate());
	}
	
	/**
	 * Tests the DateTimeUtils.getPlainTimeFrom method extracting time string from date
	 * 
	 * @throws ParseException
	 * @see {@link DateTimeUtils#getPlainTimeFrom(Date)}
	 */
	@Test
	@Verifies(value = "should return time string in plain format for given date", method = "getPlainTimeFrom(Date)")
	public void getPlainTimeFrom_shouldReturnTimeStringInPlainFormatForGivenDate() throws ParseException {
		
		SimpleDateFormat fullDateTimeFormat = new SimpleDateFormat(FULL_DATETIME_FORMAT);
		assertEquals("124311", DateTimeUtils.getPlainTimeFrom(fullDateTimeFormat.parse("01-1-1970 12:43:11")));
		assertEquals("235959", DateTimeUtils.getPlainTimeFrom(fullDateTimeFormat.parse("20-1-2000 23:59:59")));
		assertEquals("124311", DateTimeUtils.getPlainTimeFrom(fullDateTimeFormat.parse("20-01-2015 12:43:11")));
	}
	
	/**
	 * Tests the DateTimeUtils.getPlainTimeFrom method with null
	 * 
	 * @see {@link DateTimeUtils#getPlainTimeFrom(Date)}
	 */
	@Test
	@Verifies(value = "should return empty string given null", method = "getPlainTimeFrom(Date)")
	public void getPlainTimeFrom_shouldReturnEmptyStringGivenNull() {
		
		assertEquals("", DateTimeUtils.getPlainTimeFrom(null));
	}
	
	/**
	 * Tests the DateTimeUtils.getCurrentPlainTime
	 * 
	 * @throws ParseException
	 * @see {@link DateTimeUtils#getCurrentPlainTime()}
	 */
	@Test
	@Verifies(value = "should return current time string in plain format", method = "getCurrentPlainTime()")
	public void getCurrentPlainTime_shouldReturnCurrentTimeStringInPlainFormat() throws ParseException {
		
		SimpleDateFormat plainTimeFormat = new SimpleDateFormat(PLAIN_TIME_FORMAT);
		
		Date now = new Date();
		assertEquals(plainTimeFormat.format(now), DateTimeUtils.getCurrentPlainTime());
	}
	
	/**
	 * Tests the DateTimeUtils.getPlainDateTimeFrom method extracting time string from date
	 * 
	 * @throws ParseException
	 * @see {@link DateTimeUtils#getPlainDateTimeFrom(Date)}
	 */
	@Test
	@Verifies(value = "should return datetime string in plain format for given date", method = "getPlainDateTimeFrom(Date)")
	public void getPlainTimeFrom_shouldReturnDateTimeStringInPlainFormatForGivenDate() throws ParseException {
		
		SimpleDateFormat fullDateTimeFormat = new SimpleDateFormat(FULL_DATETIME_FORMAT);
		assertEquals("19700101124311", DateTimeUtils.getPlainDateTimeFrom(fullDateTimeFormat.parse("01-1-1970 12:43:11")));
		assertEquals("20000120235959", DateTimeUtils.getPlainDateTimeFrom(fullDateTimeFormat.parse("20-1-2000 23:59:59")));
		assertEquals("20150120124311", DateTimeUtils.getPlainDateTimeFrom(fullDateTimeFormat.parse("20-01-2015 12:43:11")));
	}
	
	/**
	 * Tests the DateTimeUtils.getPlainDateTimeFrom method with null
	 * 
	 * @see {@link DateTimeUtils#getPlainDateTimeFrom(Date)}
	 */
	@Test
	@Verifies(value = "should return empty string given null", method = "getPlainDateTimeFrom(Date)")
	public void getPlainDateTimeFrom_shouldReturnEmptyStringGivenNull() {
		
		assertEquals("", DateTimeUtils.getPlainDateTimeFrom(null));
	}
	
	/**
	 * Tests the DateTimeUtils.getCurrentPlainDateTime
	 * 
	 * @throws ParseException
	 * @see {@link DateTimeUtils#getCurrentPlainDateTime()}
	 */
	@Test
	@Verifies(value = "should return current datetime string in plain format", method = "getCurrentPlainDateTime()")
	public void getCurrentPlainTime_shouldReturnCurrentDateTimeStringInPlainFormat() throws ParseException {
		
		SimpleDateFormat plainDateTimeFormat = new SimpleDateFormat(PLAIN_DATETIME_FORMAT);
		
		Date now = new Date();
		assertEquals(plainDateTimeFormat.format(now), DateTimeUtils.getCurrentPlainDateTime());
	}
}
