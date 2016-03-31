/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.segment;

import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.hl7.custommodel.v231.segment.ZDS;

import ca.uhn.hl7v2.HL7Exception;

/**
 * RadiologyZDS is a utility class populating an HL7 custom ZDS Segment with an OpenMRS
 * Study
 */
public class RadiologyZDS {
	
	private RadiologyZDS() {
		// This class is a utility class which should not be instantiated
	};
	
	/**
	 * Fill custom HL7 segment (ZDS) with data from given OpenMRS Radiology Study. See HL7 ZDS
	 * message segment definition in IHE Radiology Technical Framework Volume 2 (Rev 13.0)
	 * 
	 * @param zds segment to populate
	 * @param study to map to the zds segment
	 * @return populated zds segment
	 * @throws HL7Exception
	 * @should return populated zds segment for given study
	 * @should return zds segment for given study with non-set uid
	 * @should fail given null as zds
	 * @should fail given null as study
	 */
	public static ZDS populateZDSSegment(ZDS zds, Study study) throws HL7Exception {
		
		if (zds == null) {
			throw new IllegalArgumentException("zds cannot be null.");
		} else if (study == null) {
			throw new IllegalArgumentException("study cannot be null.");
		}
		
		zds.getStudyInstanceUID()
				.getPointer()
				.setValue(study.getStudyInstanceUid());
		zds.getStudyInstanceUID()
				.getTypeOfData()
				.setValue("Application");
		zds.getStudyInstanceUID()
				.getSubtype()
				.setValue("DICOM");
		
		return zds;
	}
	
}
