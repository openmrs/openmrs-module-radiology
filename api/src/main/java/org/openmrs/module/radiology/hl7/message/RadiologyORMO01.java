/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.message;

import java.util.Date;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.CommonOrderPriority;
import org.openmrs.module.radiology.hl7.custommodel.v231.message.ORM_O01;
import org.openmrs.module.radiology.hl7.segment.RadiologyMSH;
import org.openmrs.module.radiology.hl7.segment.RadiologyOBR;
import org.openmrs.module.radiology.hl7.segment.RadiologyORC;
import org.openmrs.module.radiology.hl7.segment.RadiologyPID;
import org.openmrs.module.radiology.hl7.segment.RadiologyZDS;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;

public class RadiologyORMO01 {
	
	private static final String orderMessageType = "ORM";
	
	private static final String orderMessageTriggerEvent = "O01";
	
	private static final String sendingApplication = "OpenMRSRadiologyModule";
	
	private static final String sendingFacility = "OpenMRS";
	
	/**
	 * Get ORM_O01 message for given Study, Order, ORC Order Control Code and Priority
	 * 
	 * @param study Study to create ORMO01 message for
	 * @param order Order corresponding to study
	 * @param commonOrderOrderControl Order Control Code of Common Order (ORC) segment
	 * @param commonOrderPriority Priority component of Common Order (ORC) segment attribute
	 *            Quantity/Timing
	 * @return ORMO01 message
	 * @throws HL7Exception, DataTypeException
	 * @should return ormo01 message given all params
	 * @should fail given null as study
	 * @should fail given null as order
	 * @should fail given null as orderControlCode
	 * @should fail given null as orderControlPriority
	 */
	public static ORM_O01 getRadiologyORMO01Message(Study study, Order order,
	        CommonOrderOrderControl commonOrderOrderControl, CommonOrderPriority commonOrderPriority) throws HL7Exception,
	        DataTypeException {
		
		if (study == null) {
			throw new IllegalArgumentException("study cannot be null.");
		} else if (order == null) {
			throw new IllegalArgumentException("order cannot be null.");
		} else if (commonOrderOrderControl == null) {
			throw new IllegalArgumentException("orderControlCode cannot be null.");
		} else if (commonOrderPriority == null) {
			throw new IllegalArgumentException("orderControlPriority cannot be null.");
		}
		
		ORM_O01 result = new ORM_O01();
		
		Date dateTimeOfMessage = new Date();
		RadiologyMSH.populateMessageHeader(result.getMSH(), sendingApplication, sendingFacility, dateTimeOfMessage,
		    orderMessageType, orderMessageTriggerEvent);
		
		Patient patient = order.getPatient();
		RadiologyPID.populatePatientIdentifier(result.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1().getPID(), patient);
		
		RadiologyORC.populateCommonOrder(result.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC(), study,
		    order, commonOrderOrderControl, commonOrderPriority);
		
		RadiologyOBR.populateObservationRequest(result.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
		        .getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE().getOBR(), study, order);
		
		RadiologyZDS.populateZDSSegment(result.getZDS(), study);
		
		return result;
	}
	
}
