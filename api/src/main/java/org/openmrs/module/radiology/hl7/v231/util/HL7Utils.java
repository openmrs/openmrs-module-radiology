/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.v231.util;

import org.openmrs.Order;
import org.openmrs.PersonName;
import org.openmrs.module.radiology.hl7.v231.code.PriorityComponent;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.datatype.XPN;

/**
 * HL7Utils is a utility class containing methods for transforming OpenMRS PersonName into an HL7 conform Extended Person
 * Name and mapping Order.Urgency to HL7 Priority codes.
 */
public class HL7Utils {
	
	private HL7Utils() {
		// This class is a utility class which should not be instantiated
	};
	
	/**
	 * Map an OpenMRS PersonName to an HL7 conform Extended Person Name (XPN) as defined in HL7
	 * version 2.3.1
	 * 
	 * @param personName PersonName to be mapped
	 * @return an extended person name
	 * @throws DataTypeException
	 * @should return extended person name for given person name with family given and middlename
	 * @should return extended person name for given person name with familyname
	 * @should return extended person name for given person name with givenname
	 * @should return extended person name for given person name with middlename
	 * @should return extended person name for given person name with family and givenname
	 * @should return extended person name for given empty person name
	 * @should return empty extended person name given null
	 */
	public static XPN getExtendedPersonNameFrom(PersonName personName) throws DataTypeException {
		final XPN result = new XPN(null);
		
		if (personName != null) {
			result.getFamilyLastName()
					.getFamilyName()
					.setValue(personName.getFamilyName());
			result.getGivenName()
					.setValue(personName.getGivenName());
			result.getMiddleInitialOrName()
					.setValue(personName.getMiddleName());
		}
		return result;
	}
	
	/**
	 * Get the HL7 Priority component of Quantity/Timing (ORC-7) field included in an HL7 version
	 * 2.3.1 Common Order segment from given Order.Urgency.
	 * 
	 * @param orderUrgency Order.Urgency to be converted to PriorityComponent
	 * @return PriorityComponent for given Order.Urgency
	 * @should return routine given null
	 * @should return stat given order urgency stat
	 * @should return routine given order urgency routine
	 * @should return timing critical given order urgency on scheduled date
	 */
	public static PriorityComponent convertOrderUrgencyToCommonOrderPriority(Order.Urgency orderUrgency) {
		final PriorityComponent result;
		
		if (orderUrgency == null) {
			result = PriorityComponent.ROUTINE;
		} else {
			switch (orderUrgency) {
				case STAT:
					result = PriorityComponent.STAT;
					break;
				case ROUTINE:
					result = PriorityComponent.ROUTINE;
					break;
				case ON_SCHEDULED_DATE:
					result = PriorityComponent.TIMING_CRITICAL;
					break;
				default:
					result = PriorityComponent.ROUTINE;
					break;
			}
		}
		return result;
	}
}
