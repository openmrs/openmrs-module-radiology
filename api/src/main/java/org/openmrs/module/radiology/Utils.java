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
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OrderType;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;

public class Utils {
	
	static final Log log = LogFactory.getLog(Utils.class);
	
	/**
	 * @return List of all Order objects with OrderType == "Radiology"
	 */
	public static List<OrderType> getRadiologyOrderType() {
		List<OrderType> radiologyType = new Vector<OrderType>();
		OrderService os = Context.getOrderService();
		List<OrderType> allTypes = os.getAllOrderTypes();
		for (OrderType orderType : allTypes) {
			if (orderType.getName().equals("Radiology"))
				radiologyType.add(orderType);
		}
		return radiologyType;
	}
	
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
	
	/**
	 * Returns true if order type for radiology orders is not in the database
	 */
	public static boolean isRadiologyOrderTypeMissing() {
		return getRadiologyOrderType().isEmpty();
	}
}
