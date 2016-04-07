/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.v231.message;

import java.util.Date;

import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.hl7.custommodel.v231.message.ORM_O01;
import org.openmrs.module.radiology.hl7.v231.code.OrderControlElement;
import org.openmrs.module.radiology.hl7.v231.segment.RadiologyMSH;
import org.openmrs.module.radiology.hl7.v231.segment.RadiologyOBR;
import org.openmrs.module.radiology.hl7.v231.segment.RadiologyORC;
import org.openmrs.module.radiology.hl7.v231.segment.RadiologyPID;
import org.openmrs.module.radiology.hl7.v231.segment.RadiologyZDS;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Translates a <code>RadiologyOrder</code> to an HL7 ORM^O01 message
 */
public class RadiologyORMO01 {
	
	private static final EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	private static final String sendingApplication = "OpenMRSRadiologyModule";
	
	private static final String sendingFacility = "OpenMRS";
	
	private static final String orderMessageType = "ORM";
	
	private static final String orderMessageTriggerEvent = "O01";
	
	/**
	 * Create encoded <code>ORM_O01</code> message (version 2.3.1) from a <code>RadiologyOrder</code> and set the Order
	 * Control Code
	 * 
	 * @return encoded ORM_O01 message created from RadiologyOrder with Order Control Code set
	 * @throws HL7Exception
	 * @param radiologyOrder radiology order used to populate ORM_O01 message
	 * @param orderControlElement Order Control Code of Common Order (ORC) segment
	 * @should create new encoded ormo01 object given all params
	 * @should throw illegal argument exception given null as radiologyOrder
	 * @should throw illegal argument exception if given radiology orders study is null
	 * @should throw illegal argument exception given null as orderControlElement
	 */
	public String createEncodedMessage(RadiologyOrder radiologyOrder, OrderControlElement OrderControlelement)
			throws HL7Exception {
		
		return PipeParser.encode(this.createMessage(radiologyOrder, OrderControlelement), encodingCharacters);
	}
	
	/**
	 * Create <code>ORM_O01</code> message (version 2.3.1) from a <code>RadiologyOrder</code> and set the Order Control Code
	 * 
	 * @return ORM_O01 message created from RadiologyOrder with Order Control Code set
	 * @throws HL7Exception
	 * @param radiologyOrder radiology order used to populate ORM_O01 message
	 * @param orderControlElement Order Control Code of Common Order (ORC) segment
	 * @should create new ormo01 object given all params
	 * @should throw illegal argument exception given null as radiologyOrder
	 * @should throw illegal argument exception if given radiology orders study is null
	 * @should throw illegal argument exception given null as orderControlElement
	 */
	public ORM_O01 createMessage(RadiologyOrder radiologyOrder, OrderControlElement orderControlElement) throws HL7Exception {
		
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null.");
		} else {
			if (radiologyOrder.getStudy() == null) {
				throw new IllegalArgumentException("radiologyOrder.study cannot be null.");
			}
		}
		
		if (orderControlElement == null) {
			throw new IllegalArgumentException("orderControlElement cannot be null.");
		}
		
		final ORM_O01 result = new ORM_O01();
		
		RadiologyMSH.populateMessageHeader(result.getMSH(), sendingApplication, sendingFacility, new Date(),
			orderMessageType, orderMessageTriggerEvent);
		
		RadiologyPID.populatePatientIdentifier(result.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), radiologyOrder.getPatient());
		
		RadiologyORC.populateCommonOrder(result.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
				.getORC(), radiologyOrder, orderControlElement);
		
		RadiologyOBR.populateObservationRequest(result.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
				.getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
				.getOBR(), radiologyOrder);
		
		RadiologyZDS.populateZDSSegment(result.getZDS(), radiologyOrder.getStudy());
		
		return result;
	}
}
