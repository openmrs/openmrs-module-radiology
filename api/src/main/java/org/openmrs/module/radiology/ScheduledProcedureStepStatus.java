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
 * Represents the DICOM Scheduled Procedure Step Status (0040,0020) defined in DICOM PS3.3 2014b
 * C.4.10 Scheduled Procedure Step Module.
 * </p>
 */
public enum ScheduledProcedureStepStatus {
	
	SCHEDULED("SCHEDULED"), ARRIVED("ARRIVED"), READY("READY"), STARTED("STARTED"), DEPARTED("DEPARTED");
	
	final private String displayName;
	
	ScheduledProcedureStepStatus(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	/**
	 * Get displayName for given Scheduled Procedure Step Status
	 * 
	 * @param scheduledProcedureStepStatus the ScheduledProcedureStepStatus for which the
	 *            displayName is returned
	 * @return String displayName of given ScheduledProcedureStepStatus
	 * @should return unknown given null as scheduled procedure step status
	 * @should return display name given scheduled procedure step status
	 */
	public static String getDisplayNameOrUnknown(ScheduledProcedureStepStatus scheduledProcedureStepStatus) {
		return (scheduledProcedureStepStatus == null) ? "UNKNOWN" : scheduledProcedureStepStatus.displayName;
	}
}
