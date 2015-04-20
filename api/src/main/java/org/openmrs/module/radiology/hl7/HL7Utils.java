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

import org.openmrs.PersonName;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.datatype.XPN;

public class HL7Utils {
	
	/**
	 * Map an OpenMRS PersonName to a HL7 conform Extended Person Name (XPN) see HL7 version 2.3.1
	 * 
	 * @param personName to be mapped
	 * @return an extended person name
	 * @throws DataTypeException
	 * @should return extended person name for given person name with family given and middlename
	 *         and degree
	 * @should return extended person name for given person name with familyname
	 * @should return extended person name for given person name with givenname
	 * @should return extended person name for given person name with middlename
	 * @should return extended person name for given person name with family and givenname
	 * @should return extended person name for given initialized but empty person name
	 * @should return extended person name given null
	 */
	public static XPN getExtendedPersonNameFrom(PersonName personName) throws DataTypeException {
		XPN result = new XPN(null);
		
		if (personName != null) {
			result.getFamilyLastName().getFamilyName().setValue(personName.getFamilyName());
			result.getGivenName().setValue(personName.getGivenName());
			result.getMiddleInitialOrName().setValue(personName.getMiddleName());
		}
		return result;
	}
}
