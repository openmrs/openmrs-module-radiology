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
 * Represents a DICOM Modality. Elements are a subset of the defined terms listed in DICOM PS3.3 2014b C.7.3.1.1.1 Modality.
 * Enum name represents the defined terms short form and fullName its long form. Enum name maps to the DICOM value
 * representation CS (Code String) see DICOM PS3.5 2014b - Data Structures and Encoding.
 * </p>
 */
public enum Modality {
	
	CR("Computed Radiography"),
	MR("Magnetic Resonance"),
	CT("Computed Tomography"),
	NM("Nuclear Medicine"),
	US("Ultrasound"),
	XA("X-Ray Angiography");
	
	final private String fullName;
	
	Modality(String fullname) {
		this.fullName = fullname;
	}
	
	public String getFullName() {
		return this.fullName;
	}
}
