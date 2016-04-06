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
 * Represents the Order Control element of HL7 Common Order (ORC) defined in HL7 version 2.3.1
 * </p>
 * Note: This enum does not include every control code defined by the HL7 standard. Only the codes
 * used so far by the radiology module have been implemented here.
 */
public enum CommonOrderOrderControl {
	NEW_ORDER("NW", "New order"),
	CANCEL_ORDER("CA", "Cancel order request"),
	CHANGE_ORDER("XO", "Change order request");
	
	private String value;
	
	private String description;
	
	private CommonOrderOrderControl(String value, String description) {
		this.value = value;
		this.description = description;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String getDescription() {
		return this.description;
	}
}
