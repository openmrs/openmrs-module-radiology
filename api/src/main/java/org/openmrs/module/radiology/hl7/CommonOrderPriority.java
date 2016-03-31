/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7;

/**
 * <p>
 * Represents the Priority component (ST) of HL7 Common Order (ORC) Segment's attribute QUANTITY/TIMING (TQ) defined in HL7
 * version 2.3.1
 * </p>
 * Note: This enum does not include every priority defined by the HL7 standard. Only the priorities
 * used so far by the radiology module have been implemented here.
 */
public enum CommonOrderPriority {
	STAT(0, "S", "With highest priority"),
	ASAP(1, "A", "Fill after Stat orders"),
	ROUTINE(2, "R", "Default"),
	TIMING_CRITICAL(5, "T", "critical to come as close as possible to the requested time");
	
	private final int priority;
	
	private final String value;
	
	private final String description;
	
	private CommonOrderPriority(int priority, String value, String description) {
		this.priority = priority;
		this.value = value;
		this.description = description;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String getDescription() {
		return this.description;
	}
	
}
