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
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.CommonOrderPriority;
import org.openmrs.module.radiology.utils.DateTimeUtils;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.segment.ORC;

public class RadiologyORC {
	
	/**
	 * Fill HL7 (version 2.3.1) Common Order Segment (ORC) with data from given OpenMRS Study and
	 * Order
	 * 
	 * @param commonOrderSegment Common Order Segment to populate
	 * @param study to map to commonOrderSegment segment
	 * @param order to map to commonOrderSegment segment
	 * @param commonOrderOrderControl Order Control element of Common Order (ORC)
	 * @param commonOrderPriority Priority component of Common Order (ORC) segment attribute
	 *            Quantity/Timing
	 * @return populated commonOrderSegment segment
	 * @throws DataTypeException
	 * @should return populated common order segment given all params
	 * @should fail given null as common order segment
	 * @should fail given null as study
	 * @should fail given null as order
	 */
	public static ORC populateCommonOrder(ORC commonOrderSegment, Study study, Order order,
	        CommonOrderOrderControl commonOrderOrderControl, CommonOrderPriority commonOrderPriority)
	        throws DataTypeException {
		
		if (commonOrderSegment == null) {
			throw new IllegalArgumentException("commonOrderSegment cannot be null.");
		} else if (study == null) {
			throw new IllegalArgumentException("study cannot be null.");
		} else if (order == null) {
			throw new IllegalArgumentException("order cannot be null.");
		}
		
		commonOrderSegment.getOrderControl().setValue(commonOrderOrderControl.getValue());
		commonOrderSegment.getPlacerOrderNumber().getEntityIdentifier().setValue(String.valueOf(study.getId()));
		commonOrderSegment.getQuantityTiming().getStartDateTime().getTimeOfAnEvent().setValue(
		    DateTimeUtils.getPlainDateTimeFrom(order.getStartDate()));
		commonOrderSegment.getQuantityTiming().getPriority().setValue(commonOrderPriority.getValue());
		
		return commonOrderSegment;
	}
}
