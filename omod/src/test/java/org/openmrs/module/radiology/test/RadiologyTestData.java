/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.RequestedProcedurePriority;
import org.openmrs.module.radiology.Roles;
import org.openmrs.module.radiology.Study;

public class RadiologyTestData {
	
	static OrderType radiologyOrderType = new OrderType("Radiology", "Order for radiology procedures");
	
	/**
	 * Convenience method constructing a study order for the tests
	 */
	public static Study getMockStudy1PreSave() {
		
		Study mockStudy = new Study();
		mockStudy.setOrderId(getMockRadiologyOrder1().getId());
		mockStudy.setModality(Modality.CT);
		mockStudy.setPriority(RequestedProcedurePriority.STAT);
		
		return mockStudy;
	}
	
	/**
	 * Convenience method constructing a study order for the tests
	 */
	public static Study getMockStudy1PostSave() {
		
		Study mockStudy = getMockStudy1PreSave();
		
		int studyId = 1;
		mockStudy.setStudyId(studyId);
		mockStudy.setStudyInstanceUid(getStudyPrefix() + studyId);
		
		return mockStudy;
	}
	
	/**
	 * Convenience method constructing a study order for the tests
	 */
	public static Study getMockStudy2PreSave() {
		
		Study mockStudy = new Study();
		mockStudy.setOrderId(getMockRadiologyOrder2().getId());
		mockStudy.setModality(Modality.CT);
		mockStudy.setPriority(RequestedProcedurePriority.STAT);
		
		return mockStudy;
	}
	
	/**
	 * Convenience method constructing a study order for the tests
	 */
	public static Study getMockStudy2PostSave() {
		
		Study mockStudy = getMockStudy1PreSave();
		
		int studyId = 2;
		mockStudy.setStudyId(studyId);
		mockStudy.setStudyInstanceUid(getStudyPrefix() + studyId);
		
		return mockStudy;
	}
	
	/**
	 * Convenience method to get the StudyPrefix needed for StudyInstanceUid construction in the
	 * tests
	 */
	public static String getStudyPrefix() {
		
		return "1.2.826.0.1.3680043.8.2186.1.";
	}
	
	/**
	 * Convenience method constructing an encounter for the tests
	 */
	public static Encounter getMockEncounter() {
		
		Encounter mockEncounter = new Encounter();
		mockEncounter.setId(1);
		mockEncounter.setEncounterType(new EncounterType(1));
		mockEncounter.setEncounterDatetime(new GregorianCalendar(2015, 0, 01).getTime());
		mockEncounter.setLocation(new Location(1));
		
		EncounterProvider encounterProvider = new EncounterProvider();
		encounterProvider.setId(1);
		Set providerSet = new HashSet();
		providerSet.add(encounterProvider);
		mockEncounter.setEncounterProviders(providerSet);
		
		return mockEncounter;
	}
	
	/**
	 * Convenience method constructing a mock order for the tests
	 */
	public static Order getMockRadiologyOrder1() {
		
		Order mockOrder = new Order();
		mockOrder.setOrderId(1);
		mockOrder.setOrderType(getMockRadiologyOrderType());
		mockOrder.setPatient(getMockPatient1());
		Calendar cal = Calendar.getInstance();
		cal.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		mockOrder.setStartDate(cal.getTime());
		mockOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		mockOrder.setDiscontinued(false);
		mockOrder.setVoided(false);
		
		return mockOrder;
	}
	
	/**
	 * Convenience method constructing a mock order for the tests
	 */
	public static Order getMockRadiologyOrder2() {
		
		Calendar cal = Calendar.getInstance();
		Order mockOrder = new Order();
		mockOrder.setOrderId(2);
		mockOrder.setOrderType(getMockRadiologyOrderType());
		mockOrder.setPatient(getMockPatient2());
		cal.set(2015, Calendar.MARCH, 4, 14, 35, 0);
		mockOrder.setStartDate(cal.getTime());
		mockOrder.setInstructions("CT ABDOMEN PANCREAS WITHOUT IV CONTRAST");
		mockOrder.setDiscontinued(true);
		mockOrder.setVoided(false);
		
		return mockOrder;
	}
	
	/**
	 * Convenience method constructing a mock obs for the tests
	 */
	public static Obs getMockObs() {
		
		Obs mockObs = new Obs();
		mockObs.setId(1);
		mockObs.setEncounter(getMockEncounter());
		mockObs.setOrder(getMockRadiologyOrder1());
		mockObs.setPerson(getMockRadiologyOrder1().getPatient());
		return mockObs;
	}
	
	/**
	 * Convenience method constructing a list of previous mock obs for the tests
	 */
	public static List<Obs> getPreviousMockObs() {
		
		ArrayList<Obs> previousObs = new ArrayList<Obs>();
		Obs mockObs = new Obs();
		mockObs.setId(2);
		mockObs.setEncounter(getMockEncounter());
		mockObs.setOrder(getMockRadiologyOrder1());
		mockObs.setPerson(getMockRadiologyOrder1().getPatient());
		previousObs.add(mockObs);
		Obs mockObs2 = new Obs();
		mockObs2.setId(3);
		mockObs2.setEncounter(getMockEncounter());
		mockObs2.setOrder(getMockRadiologyOrder1());
		mockObs2.setPerson(getMockRadiologyOrder1().getPatient());
		previousObs.add(mockObs2);
		
		return previousObs;
	}
	
	/**
	 * Convenience method constructing a mock radiology order type for the tests
	 */
	public static OrderType getMockRadiologyOrderType() {
		
		return radiologyOrderType;
	}
	
	/**
	 * Convenience method constructing a mock patient for the tests
	 */
	public static Patient getMockPatient1() {
		
		Patient mockPatient = new Patient();
		mockPatient.setPatientId(1);
		mockPatient.addIdentifiers(getPatientIdentifiers("100"));
		mockPatient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		mockPatient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		mockPatient.setBirthdate(cal.getTime());
		
		return mockPatient;
	}
	
	/**
	 * Convenience method constructing a mock patient for the tests
	 */
	public static Patient getMockPatient2() {
		
		Patient mockPatient = new Patient();
		mockPatient.setPatientId(2);
		mockPatient.addIdentifiers(getPatientIdentifiers("101"));
		mockPatient.setGender("F");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("Jane");
		personName.setMiddleName("Francine");
		personNames.add(personName);
		mockPatient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1955, Calendar.FEBRUARY, 1, 0, 0, 0);
		mockPatient.setBirthdate(cal.getTime());
		
		return mockPatient;
	}
	
	/**
	 * Convenience method constructing PatientIdentifiers
	 */
	public static Set<PatientIdentifier> getPatientIdentifiers(String id) {
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(getPatientIdentifierType());
		patientIdentifier.setIdentifier(id);
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		return patientIdentifiers;
	}
	
	/**
	 * Convenience method constructing a PatientIdentifierType
	 */
	public static PatientIdentifierType getPatientIdentifierType() {
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		
		return patientIdentifierType;
	}
	
	/**
	 * Convenience method constructing a mock user with role RADIOLOGY_REFERRING_PHYSICIAN for the
	 * tests
	 */
	public static User getMockRadiologyReferringPhysician() {
		
		Role role = new Role(Roles.ReferringPhysician);
		Set<Role> roles = new HashSet<Role>();
		roles.add(role);
		
		User radiologyReferringPhysician = new User();
		radiologyReferringPhysician.setRoles(roles);
		radiologyReferringPhysician.setPerson(getMockUserPerson());
		
		return radiologyReferringPhysician;
	}
	
	/**
	 * Convenience method constructing a mock user with role RADIOLOGY_READING_PHYSICIAN for the
	 * tests
	 */
	public static User getMockRadiologyReadingPhysician() {
		Role role = new Role(Roles.ReadingPhysician);
		Set<Role> roles = new HashSet<Role>();
		roles.add(role);
		
		User radiologyReadingPhysician = new User();
		radiologyReadingPhysician.setRoles(roles);
		radiologyReadingPhysician.setPerson(getMockUserPerson());
		
		return radiologyReadingPhysician;
	}
	
	/**
	 * Convenience method constructing a mock user with role RADIOLOGY_SCHEDULER for the tests
	 */
	public static User getMockRadiologyScheduler() {
		
		Role role = new Role(Roles.Scheduler);
		Set<Role> roles = new HashSet<Role>();
		roles.add(role);
		
		User radiologyScheduler = new User();
		radiologyScheduler.setRoles(roles);
		radiologyScheduler.setPerson(getMockUserPerson());
		
		return radiologyScheduler;
	}
	
	/**
	 * Convenience method constructing a mock person for the tests
	 */
	public static Person getMockUserPerson() {
		
		PersonName name = new PersonName();
		name.setFamilyName("Karlsson");
		name.setGivenName("Karl");
		Set<PersonName> names = new HashSet<PersonName>();
		names.add(name);
		
		Person person = new Person();
		person.setNames(names);
		
		return person;
	}
	
}
