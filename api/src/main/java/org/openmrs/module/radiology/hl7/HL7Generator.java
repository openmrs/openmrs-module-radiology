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

import org.openmrs.Order;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.hl7.custommodel.v231.message.ORM_O01;
import org.openmrs.module.radiology.hl7.message.RadiologyORMO01;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

public class HL7Generator {
	
	private static EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	/**
	 * Create an encoded HL7 ORM^O01 message (version 2.3.1) for given Study, Order, Common Order
	 * Control Code and Priority
	 * 
	 * @param study Study to create the order message for
	 * @param order Order to create the order message for
	 * @param commonOrderOrderControl CommonOrderOrderControl of the order message
	 * @param commonOrderPriority CommonOrderPriority of the order message
	 * @return encoded HL7 ORM^O01 message
	 * @throws HL7Exception
	 * @throws DataTypeException
	 * @should should return encoded hl7 ormo01 message given all params including new order control
	 *         code
	 * @should should return encoded hl7 ormo01 message given all params including change order
	 *         control code
	 * @should should return encoded hl7 ormo01 message given all params including cancel order
	 *         control code
	 * @should fail given null as study
	 * @should fail given null as order
	 */
	public static String createEncodedRadiologyORMO01Message(Study study, Order order,
	        CommonOrderOrderControl commonOrderOrderControl, CommonOrderPriority commonOrderPriority) throws HL7Exception,
	        DataTypeException {
		
		String result;
		ORM_O01 ormMessage = null;
		
		ormMessage = RadiologyORMO01.getRadiologyORMO01Message(study, order, commonOrderOrderControl, commonOrderPriority);
		
		result = PipeParser.encode(ormMessage, encodingCharacters);
		return result;
	}
}
