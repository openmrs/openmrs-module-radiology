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
 * Represents the DICOM Requested Procedure Priority (0040,1003) defined in DICOM PS3.3 2014b C.4.11
 * Requested Procedure Module.
 * </p>
 */
public enum RequestedProcedurePriority {
	
	STAT("STAT"), HIGH("HIGH"), ROUTINE("ROUTINE"), MEDIUM("MEDIUM"), LOW("LOW");
	
	final private String displayName;
	
	RequestedProcedurePriority(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
}
