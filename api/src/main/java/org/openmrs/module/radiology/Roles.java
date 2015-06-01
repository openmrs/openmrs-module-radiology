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

import java.lang.reflect.Field;

import org.openmrs.api.UserService;

public class Roles {
	
	public static final String Scheduler = "Radiology: Scheduler";
	
	public static final String ReferringPhysician = "Radiology: Referring physician";
	
	public static final String PerformingPhysician = "Radiology: Performing Technician";
	
	public static final String ReadingPhysician = "Radiology: Reading physician";
	
	public static String value(Field f) {
		try {
			return f.get(new Roles()).toString();
		}
		catch (IllegalArgumentException e) {
			// TODO handle
		}
		catch (IllegalAccessException e) {
			// NOOP
		}
		return "";
	}
	
	public static boolean created(UserService us) {
		boolean c = true;
		Field[] fields = Roles.class.getDeclaredFields();
		for (Field field : fields) {
			c = c && us.getRole(value(field)) != null;
		}
		return c;
	}
}
