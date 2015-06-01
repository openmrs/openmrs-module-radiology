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

/**
 * A class that supports on openmrs's encounters to make the module DICOM
 * compatible, corresponds to the table encounter_dicom_complment
 * 
 * @author Cortex
 */
public class Visit {
	
	// Visit Statuses - Part 3 Annex C.3.3
	public static class Statuses {
		
		public static final int CREATED = 0;
		
		public static final int SCHEDULED = 1;
		
		public static final int ADMITTED = 2;
		
		public static final int DISCHARGED = 3;
		
		public static String string(int x) {
			switch (x) {
				case CREATED:
					return "CREATED";
				case SCHEDULED:
					return "SCHEDULED";
				case ADMITTED:
					return "ADMITTED";
				case DISCHARGED:
					return "DISCHARGED";
				default:
					return "";
			}
		}
	}
	
	private int id;
	
	private int encounterID;
	
	private int status;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getEncounterID() {
		return encounterID;
	}
	
	public void setEncounterID(int encounterID) {
		this.encounterID = encounterID;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
}
