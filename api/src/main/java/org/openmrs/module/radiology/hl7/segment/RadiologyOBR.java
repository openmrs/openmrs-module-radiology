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

import org.openmrs.Order;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Study.Modality;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.segment.OBR;

public class RadiologyOBR {
	
	/**
	 * Fill HL7 Observation Request Segment (OBR) with data from given OpenMRS Study and Order
	 * 
	 * @param observationRequestSegment segment to populate
	 * @param study to map to observationRequestSegment segment
	 * @param order to map to observationRequestSegment segment
	 * @param orderControlCode Order Control element of Common Order (OBR)
	 * @param orderControlPriority Priority component of Common Order (OBR) segment attribute
	 *            Quantity/Timing
	 * @return populated observationRequestSegment segment
	 * @throws DataTypeException
	 * @should return populated observation request segment for given study and order
	 * @should fail given null as observationRequestSegment
	 * @should fail given null as study
	 * @should fail given null as order
	 */
	public static OBR populateObservationRequest(OBR observationRequestSegment, Study study, Order order)
	        throws DataTypeException {
		
		if (observationRequestSegment == null) {
			throw new IllegalArgumentException("observationRequestSegment cannot be null.");
		} else if (study == null) {
			throw new IllegalArgumentException("study cannot be null.");
		} else if (order == null) {
			throw new IllegalArgumentException("order cannot be null.");
		}
		
		observationRequestSegment.getUniversalServiceID().getAlternateText().setValue(order.getInstructions());
		observationRequestSegment.getPlacerField2().setValue(String.valueOf(study.getId()));
		observationRequestSegment.getFillerField1().setValue(String.valueOf(study.getId()));
		observationRequestSegment.getDiagnosticServSectID().setValue(Modality.values()[study.getModality()].toString());
		observationRequestSegment.getProcedureCode().getText().setValue(order.getInstructions());
		
		return observationRequestSegment;
	}
}
