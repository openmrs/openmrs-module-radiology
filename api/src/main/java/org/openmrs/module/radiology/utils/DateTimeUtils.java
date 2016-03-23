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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateTimeUtils is a utility class converting Date into String using SimpleDateFormat such as
 * 'yyyyMMdd' and 'yyyyMddHHmmss'
 */
public class DateTimeUtils {
	
	private DateTimeUtils() {
		// This class is a utility class which should not be instantiated
	};
	
	/**
	 * Get the date portion of a date as string in format yyyymmdd
	 * 
	 * @param date the date to extract the date portion from
	 * @return date string in the format yyyymmdd
	 * @should return date string in plain format for given date
	 * @should return empty string given null
	 */
	public static String getPlainDateFrom(Date date) {
		if (date == null) {
			return "";
		} else {
			final SimpleDateFormat plainDateFormat = new SimpleDateFormat("yyyyMMdd");
			return plainDateFormat.format(date);
		}
	}
	
	/**
	 * Get current date as string in format yyyymmdd
	 * 
	 * @return current date string in the format yyyymmdd
	 * @should return current date string in plain format
	 */
	public static String getCurrentPlainDate() {
		return getPlainDateFrom(new Date());
	}
	
	/**
	 * Get the time portion of a date as string in format HHmmss
	 * 
	 * @param date the date to extract the time portion from
	 * @return time string in the format HHmmss
	 * @should return time string in plain format for given date
	 * @should return empty string given null
	 */
	public static String getPlainTimeFrom(Date date) {
		if (date == null) {
			return "";
		} else {
			final SimpleDateFormat plainTimeFormat = new SimpleDateFormat("HHmmss");
			return plainTimeFormat.format(date);
		}
	}
	
	/**
	 * Get current datetime as string in format yyyyMddHHmmss
	 * 
	 * @return current datetime string in the format yyyyMddHHmmss
	 * @should return current datetime string in plain format
	 */
	public static String getCurrentPlainTime() {
		return getPlainTimeFrom(new Date());
	}
	
	/**
	 * Get the datetime portion of a date as string in format yyyyMddHHmmss
	 * 
	 * @param date the date to extract the datetime portion from
	 * @return datetime string in the format yyyyMddHHmmss
	 * @should return datetime string in plain format for given date
	 * @should return empty string given null
	 */
	public static String getPlainDateTimeFrom(Date date) {
		if (date == null) {
			return "";
		} else {
			final SimpleDateFormat plainDateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			return plainDateTimeFormat.format(date);
		}
	}
	
	/**
	 * Get current time as string in format HHmmss
	 * 
	 * @return current time string in the format HHmmss
	 * @should return current time string in plain format
	 */
	public static String getCurrentPlainDateTime() {
		return getPlainDateTimeFrom(new Date());
	}
}
