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

import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.custommodel.v231.message.ORM_O01;
import org.openmrs.module.radiology.hl7.segment.RadiologyMSH;
import org.openmrs.module.radiology.hl7.segment.RadiologyOBR;
import org.openmrs.module.radiology.hl7.segment.RadiologyORC;
import org.openmrs.module.radiology.hl7.segment.RadiologyPID;
import org.openmrs.module.radiology.hl7.segment.RadiologyZDS;

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
	
	private final RadiologyOrder radiologyOrder;
	
	private final CommonOrderOrderControl commonOrderControl;
	
	/**
	 * Constructor for <code>RadiologyORMO01</code>
	 * 
	 * @param radiologyOrder radiology order
	 * @param commonOrderOrderControl Order Control Code of Common Order (ORC) segment
	 * @should create new radiology ormo01 object given all params
	 * @should throw illegal argument exception given null as radiologyOrder
	 * @should throw illegal argument exception if given radiology orders study is null
	 * @should throw illegal argument exception given null as orderControlCode
	 */
	public RadiologyORMO01(RadiologyOrder radiologyOrder, CommonOrderOrderControl commonOrderOrderControl) {
		
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null.");
		} else {
			if (radiologyOrder.getStudy() == null) {
				throw new IllegalArgumentException("radiologyOrder.study cannot be null.");
			}
		}
		
		if (commonOrderOrderControl == null) {
			throw new IllegalArgumentException("orderControlCode cannot be null.");
		}
		
		this.radiologyOrder = radiologyOrder;
		this.commonOrderControl = commonOrderOrderControl;
	}
	
	/**
	 * Create an encoded HL7 ORM^O01 message (version 2.3.1) from this <code>RadiologyORMO01</code>
	 * 
	 * @return encoded HL7 ORM^O01 message
	 * @throws HL7Exception
	 * @should create encoded hl7 ormo01 message
	 */
	public String createEncodedRadiologyORMO01Message() throws HL7Exception {
		
		return PipeParser.encode(createRadiologyORMO01Message(), encodingCharacters);
	}
	
	/**
	 * Create <code>ORM_O01</code> message for this <code>RadiologyORMO01</code>
	 * 
	 * @return ORM_O01 message
	 * @throws HL7Exception
	 * @should create ormo01 message
	 */
	private ORM_O01 createRadiologyORMO01Message() throws HL7Exception {
		
		final ORM_O01 result = new ORM_O01();
		
		RadiologyMSH.populateMessageHeader(result.getMSH(), sendingApplication, sendingFacility, new Date(),
			orderMessageType, orderMessageTriggerEvent);
		
		RadiologyPID.populatePatientIdentifier(result.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), radiologyOrder.getPatient());
		
		RadiologyORC.populateCommonOrder(result.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
				.getORC(), radiologyOrder, commonOrderControl);
		
		RadiologyOBR.populateObservationRequest(result.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
				.getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
				.getOBR(), radiologyOrder);
		
		RadiologyZDS.populateZDSSegment(result.getZDS(), radiologyOrder.getStudy());
		
		return result;
	}
}
