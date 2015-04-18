/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.Study;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

/**
 * Tests methods on the {@link StudyValidator} class.
 */
public class StudyValidatorTest {
	
	/**
	 * @see StudyValidator#validate(Object, Errors)
	 */
	@Test
	@Verifies(value = "should fail validation if study is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfStudyIsNull() throws Exception {
		Errors errors = new BindException(new Study(), "study");
		new StudyValidator().validate(null, errors);
		
		assertTrue(errors.hasErrors());
		assertEquals("error.general", ((List<ObjectError>) errors.getAllErrors()).get(0).getCode());
	}
	
	/**
	 * @see StudyValidator#validate(Object, Errors)
	 */
	@Test
	@Verifies(value = "should fail validation if order is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfOrderIsNull() throws Exception {
		Study study = new Study();
		study.setId(1);
		study.setUid("1.2.826.0.1.3680043.8.2186.1.1");
		study.setModality(Modality.CT);
		study.setPriority(0);
		
		Errors errors = new BindException(study, "study");
		new StudyValidator().validate(study, errors);
		
		assertTrue(errors.hasErrors());
		assertTrue(errors.hasFieldErrors("order"));
	}
	
	/**
	 * @see StudyValidator#validate(Object, Errors)
	 */
	@Test
	@Verifies(value = "should fail validation if modality is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfModalityIsNull() throws Exception {
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
		cal.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		patient.setBirthdate(cal.getTime());
		
		Order order = new Order();
		order.setOrderId(20);
		order.setPatient(patient);
		cal.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		order.setStartDate(cal.getTime());
		order.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Study study = new Study();
		study.setId(1);
		study.setOrder(order);
		study.setUid("1.2.826.0.1.3680043.8.2186.1.1");
		study.setPriority(0);
		
		Errors errors = new BindException(study, "study");
		new StudyValidator().validate(study, errors);
		
		assertTrue(errors.hasErrors());
		assertTrue(errors.hasFieldErrors("modality"));
	}
	
	/**
	 * @see {@link StudyValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object, Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
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
		cal.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		patient.setBirthdate(cal.getTime());
		
		Concept concept = new Concept();
		concept.setConceptId(1);
		
		Order order = new Order();
		order.setOrderId(20);
		order.setPatient(patient);
		cal.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		order.setStartDate(cal.getTime());
		order.setConcept(concept);
		order.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Study study = new Study();
		study.setId(1);
		study.setOrder(order);
		study.setUid("1.2.826.0.1.3680043.8.2186.1.1");
		study.setModality(Modality.CT);
		study.setPriority(0);
		
		Errors errors = new BindException(study, "study");
		new StudyValidator().validate(study, errors);
		
		assertFalse(errors.hasErrors());
	}
}
