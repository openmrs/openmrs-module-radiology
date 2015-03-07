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
