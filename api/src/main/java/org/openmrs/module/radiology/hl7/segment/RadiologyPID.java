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

import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.module.radiology.hl7.HL7Utils;
import org.openmrs.module.radiology.utils.DateTimeUtils;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.datatype.XPN;
import ca.uhn.hl7v2.model.v231.segment.PID;

/**
 * RadiologyPID is a utility class populating an HL7 Patient Identifier Segment with an OpenMRS
 * Patient
 */
public class RadiologyPID {
	
	private RadiologyPID() {
		// This class is a utility class which should not be instantiated
	};
	
	/**
	 * Fill HL7 (version 2.3.1) Patient Identification Segment (PID) with data from given OpenMRS
	 * Patient
	 * 
	 * @param patientIdentifierSegment Patient Identification Segment to populate
	 * @param patient Patient to map to patientIdentifierSegment segment
	 * @return populated patientIdentifierSegment segment
	 * @throws DataTypeException, HL7Exception
	 * @should return populated patient identifier segment for given patient
	 * @should return populated patient identifier segment for given patient with empty personname
	 * @should return populated patient identifier segment for given patient with non-set personname
	 * @should return populated patient identifier segment for given patient with non-set birthdate
	 * @should return populated patient identifier segment for given patient with non-set gender
	 * @should fail given null as patient
	 * @should fail given null as patient identifier segment
	 * @should fail given patient with no patient identifier
	 */
	public static PID populatePatientIdentifier(PID patientIdentifierSegment, Patient patient) throws DataTypeException,
			HL7Exception {
		
		if (patientIdentifierSegment == null) {
			throw new IllegalArgumentException("patientIdentifierSegment cannot be null.");
		} else if (patient == null) {
			throw new IllegalArgumentException("patient cannot be null.");
		}
		
		patientIdentifierSegment.getPatientIdentifierList(0)
				.getID()
				.setValue(patient.getPatientIdentifier()
						.getIdentifier());
		patientIdentifierSegment.getDateTimeOfBirth()
				.getTimeOfAnEvent()
				.setValue(DateTimeUtils.getPlainDateTimeFrom(patient.getBirthdate()));
		patientIdentifierSegment.getSex()
				.setValue(patient.getGender());
		
		final PersonName personName = patient.getPersonName();
		final XPN xpnPatientName = HL7Utils.getExtendedPersonNameFrom(personName);
		patientIdentifierSegment.getPatientName(0)
				.getFamilyLastName()
				.getFamilyName()
				.setValue(xpnPatientName.getFamilyLastName()
						.getFamilyName()
						.getValue());
		patientIdentifierSegment.getPatientName(0)
				.getGivenName()
				.setValue(xpnPatientName.getGivenName()
						.getValue());
		patientIdentifierSegment.getPatientName(0)
				.getMiddleInitialOrName()
				.setValue(xpnPatientName.getMiddleInitialOrName()
						.getValue());
		
		return patientIdentifierSegment;
	}
}
