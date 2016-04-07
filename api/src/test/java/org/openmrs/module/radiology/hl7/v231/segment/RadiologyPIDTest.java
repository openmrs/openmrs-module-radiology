/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.v231.segment;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.module.radiology.hl7.HL7Constants;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests {@link RadiologyPID}
 */
public class RadiologyPIDTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * Test RadiologyPID.populatePatientIdentifier
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyPID#populatePatientIdentifier(PID, Patient)}
	 */
	@Test
	@Verifies(value = "should return populated patient identifier segment for given patient", method = "populatePatientIdentifier(PID, Patient)")
	public void populatePatientIdentifier_shouldReturnPopulatedPatientIdentifierSegmentForGivenPatient() throws HL7Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1980, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		patient.setBirthdate(cal.getTime());
		
		ORM_O01 message = new ORM_O01();
		RadiologyPID.populatePatientIdentifier(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), patient);
		
		assertThat(PipeParser.encode(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), HL7Constants.ENCODING_CHARACTERS), is("PID|||100||Doe^John^Francis||19800228222510|M"));
	}
	
	/**
	 * Test RadiologyPID.populatePatientIdentifier
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyPID#populatePatientIdentifier(PID, Patient)}
	 */
	@Test
	@Verifies(value = "should return populated patient identifier segment for given patient with empty personname", method = "populatePatientIdentifier(PID, Patient)")
	public void populatePatientIdentifier_shouldReturnPopulatedPatientIdentifierSegmentForGivenPatientWithEmptyPersonName()
			throws HL7Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1980, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		patient.setBirthdate(cal.getTime());
		
		ORM_O01 message = new ORM_O01();
		RadiologyPID.populatePatientIdentifier(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), patient);
		
		assertThat(PipeParser.encode(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), HL7Constants.ENCODING_CHARACTERS), is("PID|||100||||19800228222510|M"));
	}
	
	/**
	 * Test RadiologyPID.populatePatientIdentifier
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyPID#populatePatientIdentifier(PID, Patient)}
	 */
	@Test
	@Verifies(value = "should return populated patient identifier segment for given patient with non-set personname", method = "populatePatientIdentifier(PID, Patient)")
	public void populatePatientIdentifier_shouldReturnPopulatedPatientIdentifierSegmentForGivenPatientWithNonSetPersonName()
			throws HL7Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		patient.setGender("M");
		
		Calendar cal = Calendar.getInstance();
		cal.set(1980, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		patient.setBirthdate(cal.getTime());
		
		ORM_O01 message = new ORM_O01();
		RadiologyPID.populatePatientIdentifier(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), patient);
		
		assertThat(PipeParser.encode(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), HL7Constants.ENCODING_CHARACTERS), is("PID|||100||||19800228222510|M"));
	}
	
	/**
	 * Test RadiologyPID.populatePatientIdentifier
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyPID#populatePatientIdentifier(PID, Patient)}
	 */
	@Test
	@Verifies(value = "should return populated patient identifier segment for given patient with non-set birthdate", method = "populatePatientIdentifier(PID, Patient)")
	public void populatePatientIdentifier_shouldReturnPopulatedPatientIdentifierSegmentForGivenPatientWithNonSetBirthdate()
			throws HL7Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		ORM_O01 message = new ORM_O01();
		RadiologyPID.populatePatientIdentifier(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), patient);
		assertThat(PipeParser.encode(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), HL7Constants.ENCODING_CHARACTERS), is("PID|||100||Doe^John^Francis|||M"));
	}
	
	/**
	 * Test RadiologyPID.populatePatientIdentifier
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyPID#populatePatientIdentifier(PID, Patient)}
	 */
	@Test
	@Verifies(value = "should return populated patient identifier segment for given patient with non-set gender", method = "populatePatientIdentifier(PID, Patient)")
	public void populatePatientIdentifier_shouldReturnPopulatedPatientIdentifierSegmentForGivenPatientWithNonSetGender()
			throws HL7Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1980, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		patient.setBirthdate(cal.getTime());
		
		ORM_O01 message = new ORM_O01();
		RadiologyPID.populatePatientIdentifier(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), patient);
		
		assertThat(PipeParser.encode(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), HL7Constants.ENCODING_CHARACTERS), is("PID|||100||Doe^John^Francis||19800228222510"));
	}
	
	/**
	 * Test RadiologyPID.populatePatientIdentifier
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyPID#populatePatientIdentifier(PID, Patient)}
	 */
	@Test
	@Verifies(value = "should fail given null as patient", method = "populatePatientIdentifier(PID, Patient)")
	public void populatePatientIdentifier_shouldFailGivenNullAsPatient() throws HL7Exception {
		
		ORM_O01 message = new ORM_O01();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("patient cannot be null."));
		RadiologyPID.populatePatientIdentifier(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), null);
	}
	
	/**
	 * Test RadiologyPID.populatePatientIdentifier
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyPID#populatePatientIdentifier(PID, Patient)}
	 */
	@Test
	@Verifies(value = "should fail given null as patient identifier segment", method = "populatePatientIdentifier(PID, Patient)")
	public void populatePatientIdentifier_shouldFailGivenNullAsPatientIdentifierSegment() throws HL7Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(100);
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1980, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		patient.setBirthdate(cal.getTime());
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("patientIdentifierSegment cannot be null."));
		RadiologyPID.populatePatientIdentifier(null, patient);
	}
	
	/**
	 * Test RadiologyPID.populatePatientIdentifier
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyPID#populatePatientIdentifier(PID, Patient)}
	 */
	@Test
	@Verifies(value = "should fail given patient with no patient identifier", method = "populatePatientIdentifier(PID, Patient)")
	public void populatePatientIdentifier_shouldFailGivenPatientWithNoPatientIdentifier() throws HL7Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(1);
		
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1980, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		patient.setBirthdate(cal.getTime());
		
		ORM_O01 message = new ORM_O01();
		
		expectedException.expect(NullPointerException.class);
		RadiologyPID.populatePatientIdentifier(message.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1()
				.getPID(), patient);
	}
}
