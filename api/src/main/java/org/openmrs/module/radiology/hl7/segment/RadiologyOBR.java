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

import org.openmrs.module.radiology.RadiologyOrder;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.segment.OBR;

/**
 * RadiologyOBR is a utility class populating an HL7 Observation Request Segment with an OpenMRS
 * RadiologyOrder
 */
public class RadiologyOBR {
	
	private RadiologyOBR() {
		// This class is a utility class which should not be instantiated
	};
	
	/**
	 * Fill HL7 (version 2.3.1) Observation Request Segment (OBR) with data from given OpenMRS Study
	 * and Order
	 * 
	 * @param observationRequestSegment segment to populate
	 * @param radiologyOrder to map to observationRequestSegment segment
	 * @return populated observationRequestSegment segment
	 * @throws DataTypeException
	 * @should return populated observation request segment given all params
	 * @should throw illegal argument exception given null as observation request segment
	 * @should throw illegal argument exception given null as radiology order
	 * @should throw illegal argument exception if given radiology orders study is null
	 */
	public static OBR populateObservationRequest(OBR observationRequestSegment, RadiologyOrder radiologyOrder)
			throws DataTypeException {
		
		if (observationRequestSegment == null) {
			throw new IllegalArgumentException("observationRequestSegment cannot be null.");
		}
		
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null.");
		} else {
			if (radiologyOrder.getStudy() == null) {
				throw new IllegalArgumentException("radiologyOrder.study cannot be null.");
			}
		}
		
		observationRequestSegment.getUniversalServiceID()
				.getAlternateText()
				.setValue(radiologyOrder.getInstructions());
		observationRequestSegment.getPlacerField2()
				.setValue(radiologyOrder.getOrderNumber() == null ? "" : String.valueOf(radiologyOrder.getOrderNumber()));
		observationRequestSegment.getFillerField1()
				.setValue(String.valueOf(radiologyOrder.getStudy()
						.getStudyId()));
		observationRequestSegment.getDiagnosticServSectID()
				.setValue(radiologyOrder.getStudy()
						.getModality()
						.toString());
		observationRequestSegment.getProcedureCode()
				.getText()
				.setValue(radiologyOrder.getInstructions());
		
		return observationRequestSegment;
	}
}
