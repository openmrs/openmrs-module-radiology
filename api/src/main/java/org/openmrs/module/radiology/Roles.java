package org.openmrs.module.radiology;

import java.lang.reflect.Field;

import org.openmrs.api.UserService;

public class Roles {
	public static final String Scheduler="Radiology: Scheduler";
	public static final String ReferringPhysician="Radiology: Referring physician";
	public static final String PerformingPhysician="Radiology: Performing physician";
	public static final String ReadingPhysician="Radiology: Reading physician";

	public static String value(Field f){
		try {
			return f.get(new Roles()).toString();
		} catch (IllegalArgumentException e) {
			// TODO handle
		} catch (IllegalAccessException e) {
			// NOOP
		}
		return "";
	}
	
	public static boolean created(UserService us){
		boolean c=true;
		Field[] fields = Roles.class.getDeclaredFields();
		for (Field field : fields) {
				c=c && us.getRole(value(field))!=null;
		}
		return c;
	}
}
