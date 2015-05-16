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
 * <p>
 * Represents the DICOM Performed Procedure Step Status (0040,0252) defined in DICOM PS3.3 2014b
 * C.4.14 Performed Procedure Step Information.
 * </p>
 */
public enum PerformedProcedureStepStatus {
	
	IN_PROGRESS("IN PROGRESS"), DISCONTINUED("DISCONTINUED"), COMPLETED("COMPLETED");
	
	final private String displayName;
	
	PerformedProcedureStepStatus(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	/**
	 * Get displayName for given Performed Procedure Step Status
	 * 
	 * @param performedProcedureStepStatus the PerformedProcedureStepStatus for which the
	 *            displayName is returned
	 * @return String displayName of given PerformedProcedureStepStatus
	 * @should return unknown given null as performed procedure step status
	 * @should return display name given performed procedure step status
	 */
	public static String getDisplayNameOrUnknown(PerformedProcedureStepStatus performedProcedureStepStatus) {
		return (performedProcedureStepStatus == null) ? "UNKNOWN" : performedProcedureStepStatus.displayName;
	}
	
	/**
	 * Get Performed Procedure Step Status for given displayName
	 * 
	 * @param displayName the displayName for which the PerformedProcedureStepStatus is returned
	 * @return PerformedProcedureStepStatus PerformedProcedureStepStatus matching given displayName
	 * @throws IllegalArgumentException
	 * @should return null given undefined display name
	 * @should return performed procedure step status given display name
	 * @should throw IllegalArgumentException if display name is null
	 */
	public static PerformedProcedureStepStatus getMatchForDisplayName(String displayName) throws IllegalArgumentException {
		if (displayName == null) {
			throw new IllegalArgumentException("displayName is required");
		}
		
		if (displayName.toLowerCase().contains("progress")) {
			return IN_PROGRESS;
		} else if (displayName.compareToIgnoreCase("discontinued") == 0) {
			return DISCONTINUED;
		} else if (displayName.compareToIgnoreCase("completed") == 0) {
			return COMPLETED;
		} else {
			return null;
		}
	}
}
