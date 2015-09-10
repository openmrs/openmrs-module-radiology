/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Utils {
	
	static final Log log = LogFactory.getLog(Utils.class);
	
	/**
	 * @param d the date to plain
	 * @return d in the format yyyymmdd as string
	 */
	@SuppressWarnings("static-access")
	static String plain(Date d) {
		if (d == null)
			d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return "" + pad(c.get(c.YEAR), 4) + pad(c.get(c.MONTH) + 1) + pad(c.get(c.DAY_OF_MONTH));
	}
	
	/**
	 * @param d the date to 'time'
	 * @return d in the format hhmmss as string
	 */
	@SuppressWarnings("static-access")
	static String time(Date d) {
		if (d == null)
			d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return "" + pad(c.get(c.HOUR_OF_DAY)) + pad(c.get(c.MINUTE)) + pad(c.get(c.SECOND));
	}
	
	/**
	 * @param x Number to pad
	 * @param min Minimum numbers to be written
	 * @return pad(2,3) returns "002"
	 */
	static String pad(int x, int min) {
		return String.format("%0" + min + "d", x);
	}
	
	static String pad(int x) {
		return pad(x, 2);
	}
	
}
