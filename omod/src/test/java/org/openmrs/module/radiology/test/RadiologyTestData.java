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

import static org.openmrs.module.radiology.RadiologyRoles.PERFORMING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.READING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.REFERRING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.SCHEDULER;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.study.RadiologyStudy;
import org.openmrs.util.RoleConstants;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

public class RadiologyTestData {
    
    
    static OrderType radiologyOrderType = new OrderType("Radiology Order", "Order type for radiology exams",
            "org.openmrs.module.radiology.order.RadiologyOrder");
    
    /**
     * Convenience method constructing a study order for the tests
     */
    public static RadiologyStudy getMockStudy1PreSave() {
        
        return new RadiologyStudy();
    }
    
    /**
     * Convenience method constructing a study order for the tests
     */
    public static RadiologyStudy getMockStudy1PostSave() {
        
        RadiologyStudy mockStudy = getMockStudy1PreSave();
        
        int studyId = 1;
        mockStudy.setStudyId(studyId);
        mockStudy.setStudyInstanceUid(getStudyPrefix() + studyId);
        
        return mockStudy;
    }
    
    /**
     * Convenience method constructing a study order for the tests
     */
    public static RadiologyStudy getMockStudy2PreSave() {
        
        return new RadiologyStudy();
    }
    
    /**
     * Convenience method constructing a study order for the tests
     */
    public static RadiologyStudy getMockStudy2PostSave() {
        
        RadiologyStudy mockStudy = getMockStudy2PreSave();
        
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
    public static Encounter getMockEncounter1() {
        
        Encounter mockEncounter = new Encounter();
        mockEncounter.setId(1);
        mockEncounter.setEncounterType(new EncounterType(1));
        mockEncounter.setEncounterDatetime(new GregorianCalendar(2015, 0, 01).getTime());
        mockEncounter.setLocation(new Location(1));
        
        EncounterProvider encounterProvider = new EncounterProvider();
        encounterProvider.setId(1);
        Set<EncounterProvider> providerSet = new HashSet<EncounterProvider>();
        providerSet.add(encounterProvider);
        mockEncounter.setEncounterProviders(providerSet);
        
        return mockEncounter;
    }
    
    /**
     * Convenience method constructing an encounter for the tests
     */
    public static Encounter getMockEncounter2() {
        
        Encounter mockEncounter = new Encounter();
        mockEncounter.setId(2);
        mockEncounter.setEncounterType(new EncounterType(1));
        mockEncounter.setEncounterDatetime(new GregorianCalendar(2015, 1, 02).getTime());
        mockEncounter.setLocation(new Location(1));
        
        EncounterProvider encounterProvider = new EncounterProvider();
        encounterProvider.setId(1);
        Set<EncounterProvider> providerSet = new HashSet<EncounterProvider>();
        providerSet.add(encounterProvider);
        mockEncounter.setEncounterProviders(providerSet);
        
        return mockEncounter;
    }
    
    /**
     * Convenience method constructing an order for the tests
     */
    public static Order getMockOrder1() {
        
        Order mockOrder = new Order();
        mockOrder.setOrderId(1);
        mockOrder.setPatient(getMockPatient1());
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
        mockOrder.setScheduledDate(calendar.getTime());
        mockOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
        mockOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
        mockOrder.setVoided(false);
        return mockOrder;
    }
    
    /**
     * Convenience method constructing a mock RadiologyOrder for the tests
     */
    public static RadiologyOrder getMockRadiologyOrder1() {
        
        RadiologyOrder mockRadiologyOrder = new RadiologyOrder();
        mockRadiologyOrder.setOrderId(1);
        mockRadiologyOrder.setOrderType(getMockRadiologyOrderType());
        mockRadiologyOrder.setPatient(getMockPatient1());
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
        mockRadiologyOrder.setScheduledDate(calendar.getTime());
        mockRadiologyOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
        mockRadiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
        mockRadiologyOrder.setVoided(false);
        RadiologyStudy radiologyStudy = getMockStudy1PostSave();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        mockRadiologyOrder.setStudy(radiologyStudy);
        
        return mockRadiologyOrder;
    }
    
    /**
     * Convenience method constructing a mock RadiologyOrder for the tests
     */
    public static RadiologyOrder getMockRadiologyOrder2() {
        
        RadiologyOrder mockRadiologyOrder = new RadiologyOrder();
        mockRadiologyOrder.setOrderId(2);
        mockRadiologyOrder.setOrderType(getMockRadiologyOrderType());
        mockRadiologyOrder.setPatient(getMockPatient2());
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.MARCH, 4, 14, 35, 0);
        mockRadiologyOrder.setScheduledDate(calendar.getTime());
        mockRadiologyOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
        mockRadiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITHOUT IV CONTRAST");
        mockRadiologyOrder.setVoided(false);
        RadiologyStudy radiologyStudy = getMockStudy2PostSave();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        mockRadiologyOrder.setStudy(radiologyStudy);
        
        return mockRadiologyOrder;
    }
    
    /**
     * Convenience method constructing a multipart http servlet request for the tests
     */
    public static MockMultipartHttpServletRequest getMockMultipartHttpServletRequestForMockObsWithComplexConcept() {
        MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
        mockRequest.addFile(getMockMultipartFileForMockObsWithComplexConcept());
        
        return mockRequest;
    }
    
    /**
     * Convenience method constructing a multipart file for the tests
     */
    public static MockMultipartFile getMockMultipartFileForMockObsWithComplexConcept() {
        final String fileName = "test.jpg";
        final byte[] content = "FFD8FFE000104A46".getBytes();
        return new MockMultipartFile("complexDataFile", fileName, "image/jpeg", content);
    }
    
    /**
     * Convenience method constructing an empty multipart file for the tests
     */
    public static MockMultipartFile getEmptyMockMultipartFileForMockObsWithComplexConcept() {
        final String fileName = "test.jpg";
        final byte[] content = "".getBytes();
        return new MockMultipartFile("complexDataFile", fileName, "image/jpeg", content);
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
     * Convenience method constructing a mock patient for the tests
     */
    public static Patient getMockPatient3() {
        
        Patient mockPatient = new Patient();
        mockPatient.setPatientId(3);
        mockPatient.addIdentifiers(getPatientIdentifiers("102"));
        mockPatient.setGender("F");
        
        Set<PersonName> personNames = new HashSet<PersonName>();
        PersonName personName = new PersonName();
        personName.setFamilyName("Diaz");
        personName.setGivenName("Maria");
        personName.setMiddleName("Sophia");
        personNames.add(personName);
        mockPatient.setNames(personNames);
        
        Calendar cal = Calendar.getInstance();
        cal.set(1980, Calendar.FEBRUARY, 1, 0, 0, 0);
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
        
        Role role = new Role(REFERRING_PHYSICIAN);
        Set<Role> roles = new HashSet<Role>();
        roles.add(role);
        
        User radiologyReferringPhysician = new User();
        radiologyReferringPhysician.setRoles(roles);
        radiologyReferringPhysician.setPerson(getMockUserPerson());
        
        return radiologyReferringPhysician;
    }
    
    /**
     * Convenience method constructing a mock user with role RADIOLOGY_PERFORMING_PHYSICIAN for the
     * tests
     */
    public static User getMockRadiologyPerformingPhysician() {
        
        Role role = new Role(PERFORMING_PHYSICIAN);
        Set<Role> roles = new HashSet<Role>();
        roles.add(role);
        
        User radiologyPerformingPhysician = new User();
        radiologyPerformingPhysician.setRoles(roles);
        radiologyPerformingPhysician.setPerson(getMockUserPerson());
        
        return radiologyPerformingPhysician;
    }
    
    /**
     * Convenience method constructing a mock user with role RADIOLOGY_READING_PHYSICIAN for the
     * tests
     */
    public static User getMockRadiologyReadingPhysician() {
        Role role = new Role(READING_PHYSICIAN);
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
        
        Role role = new Role(SCHEDULER);
        Set<Role> roles = new HashSet<Role>();
        roles.add(role);
        
        User radiologyScheduler = new User();
        radiologyScheduler.setRoles(roles);
        radiologyScheduler.setPerson(getMockUserPerson());
        
        return radiologyScheduler;
    }
    
    /**
     * Convenience method constructing a mock user with role RADIOLOGY_SCHEDULER for the tests
     */
    public static User getMockRadiologySuperUser() {
        
        Role role = new Role(RoleConstants.SUPERUSER);
        Set<Role> roles = new HashSet<Role>();
        roles.add(role);
        
        User radiologySuperUser = new User();
        radiologySuperUser.setRoles(roles);
        radiologySuperUser.setPerson(getMockUserPerson());
        
        return radiologySuperUser;
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
    
    public static Provider getMockProvider1() {
        Provider provider = new Provider();
        provider.setId(1);
        provider.setName("doctor");
        return provider;
    }
    
    public static RadiologyReport getMockRadiologyReport1() {
        
        RadiologyReport radiologyReport = new RadiologyReport(getMockRadiologyOrder1());
        radiologyReport.setId(1);
        return radiologyReport;
    }
}
