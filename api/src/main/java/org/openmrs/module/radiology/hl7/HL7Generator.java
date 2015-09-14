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

import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.hl7.custommodel.v231.message.ORM_O01;
import org.openmrs.module.radiology.hl7.message.RadiologyORMO01;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

public class HL7Generator {
	
	private static final EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	/**
	 * Create an encoded HL7 ORM^O01 message (version 2.3.1) for given RadiologyOrder, Common Order
	 * Control Code and Priority
	 * 
	 * @param radiologyOrder radiology order to create the order message for
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
	 * @should throw illegal argument exception given null as radiology order
	 * @should throw illegal argument exception if given radiology orders study is null
	 */
	public static String createEncodedRadiologyORMO01Message(RadiologyOrder radiologyOrder,
	        CommonOrderOrderControl commonOrderOrderControl, CommonOrderPriority commonOrderPriority) throws HL7Exception,
	        DataTypeException {
		
		ORM_O01 ormMessage = RadiologyORMO01.getRadiologyORMO01Message(radiologyOrder, commonOrderOrderControl,
		    commonOrderPriority);
		return PipeParser.encode(ormMessage, encodingCharacters);
	}
}
