/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.impl;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.db.RadiologyReportDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.openmrs.module.radiology.db.hibernate.HibernateRadiologyReportDAO;
import org.openmrs.module.radiology.db.hibernate.HibernateStudyDAO;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link RadiologyServiceImpl}
 */
public class RadiologyServiceImplComponentTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceComponentTestDataset.xml";
	
	private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER_AND_NO_ACTIVE_VISIT = 70011;
	
	private static final int PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT = 70033;
	
	private static final int RADIOLOGY_ORDER_ID_WITHOUT_STUDY = 2004;
	
	private static final int EXISTING_STUDY_ID = 1;
	
	private static final int NON_EXISTING_STUDY_ID = 99999;
	
	private static final String RADIOLOGY_ORDER_PROVIDER_UUID = "c2299800-cca9-11e0-9572-0800200c9a66";
	
	private PatientService patientService = null;
	
	private AdministrationService administrationService = null;
	
	private EncounterService encounterService = null;
	
	private VisitService visitService = null;
	
	private EmrEncounterService emrEncounterService = null;
	
	private ProviderService providerService = null;
	
	private OrderService orderService = null;
	
	private RadiologyServiceImpl radiologyServiceImpl = null;
	
	private RadiologyService radiologyService = null;
	
	private RadiologyProperties radiologyProperties = null;
	
	@Autowired
	private StudyDAO studyDAO;
	
	@Autowired
	private RadiologyReportDAO radiologyReportDAO;
	
	private Method saveRadiologyOrderEncounterMethod = null;
	
	private Method saveStudyMethod = null;
	
	private Method updateStudyMwlStatusMethod;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void runBeforeAllTests() throws Exception {
		
		if (patientService == null) {
			patientService = Context.getPatientService();
		}
		
		if (administrationService == null) {
			administrationService = Context.getAdministrationService();
		}
		
		if (radiologyService == null) {
			radiologyService = Context.getService(RadiologyService.class);
		}
		
		if (providerService == null) {
			providerService = Context.getProviderService();
		}
		
		if (orderService == null) {
			orderService = Context.getOrderService();
		}
		
		if (visitService == null) {
			visitService = Context.getVisitService();
		}
		
		if (encounterService == null) {
			encounterService = Context.getEncounterService();
		}
		
		if (emrEncounterService == null) {
			emrEncounterService = Context.getService(EmrEncounterService.class);
		}
		
		if (radiologyProperties == null) {
			radiologyProperties = Context.getRegisteredComponent("radiologyProperties", RadiologyProperties.class);
		}
		
		if (radiologyServiceImpl == null) {
			radiologyServiceImpl = new RadiologyServiceImpl();
			radiologyServiceImpl.setOrderService(orderService);
			radiologyServiceImpl.setEncounterService(encounterService);
			radiologyServiceImpl.setEmrEncounterService(emrEncounterService);
			radiologyServiceImpl.setStudyDAO(studyDAO);
			radiologyServiceImpl.setRadiologyReportDAO(radiologyReportDAO);
			radiologyServiceImpl.setRadiologyProperties(radiologyProperties);
		}
		
		saveStudyMethod = RadiologyServiceImpl.class.getDeclaredMethod("saveStudy",
			new Class[] { org.openmrs.module.radiology.Study.class });
		saveStudyMethod.setAccessible(true);
		
		updateStudyMwlStatusMethod = RadiologyServiceImpl.class.getDeclaredMethod("updateStudyMwlStatus", new Class[] {
				RadiologyOrder.class, boolean.class });
		updateStudyMwlStatusMethod.setAccessible(true);
		
		saveRadiologyOrderEncounterMethod = RadiologyServiceImpl.class.getDeclaredMethod("saveRadiologyOrderEncounter",
			new Class[] { Patient.class, Provider.class, Date.class });
		saveRadiologyOrderEncounterMethod.setAccessible(true);
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * @see RadiologyServiceImpl#saveStudy(Study)
	 * @verifies create new study from given study object
	 */
	@Test
	public void saveStudy_shouldCreateNewStudyFromGivenStudyObject() throws Exception {
		
		Study radiologyStudy = getUnsavedStudy();
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_ID_WITHOUT_STUDY);
		radiologyOrder.setStudy(radiologyStudy);
		
		Study createdStudy = (Study) saveStudyMethod.invoke(radiologyServiceImpl, new Object[] { radiologyStudy });
		
		assertNotNull(createdStudy);
		assertThat(createdStudy, is(radiologyStudy));
		assertThat(createdStudy.getStudyId(), is(radiologyStudy.getStudyId()));
		assertNotNull(createdStudy.getStudyInstanceUid());
		assertThat(createdStudy.getStudyInstanceUid(), is(radiologyProperties.getStudyPrefix() + createdStudy.getStudyId()));
		assertThat(createdStudy.getModality(), is(radiologyStudy.getModality()));
		assertThat(createdStudy.getRadiologyOrder(), is(radiologyStudy.getRadiologyOrder()));
	}
	
	/**
	 * Convenience method to get a Study object with all required values filled (except
	 * radiologyOrder) in but which is not yet saved in the database
	 * 
	 * @return Study object that can be saved to the database
	 */
	public Study getUnsavedStudy() {
		
		Study study = new Study();
		study.setModality(Modality.CT);
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		return study;
	}
	
	/**
	 * @see RadiologyServiceImpl#saveStudy(Study)
	 * @verifies update existing study
	 */
	@Test
	public void saveStudy_shouldUpdateExistingStudy() throws Exception {
		
		Study existingStudy = radiologyServiceImpl.getStudyByStudyId(EXISTING_STUDY_ID);
		Modality modalityPreUpdate = existingStudy.getModality();
		Modality modalityPostUpdate = Modality.XA;
		existingStudy.setModality(modalityPostUpdate);
		
		Study updatedStudy = (Study) saveStudyMethod.invoke(radiologyServiceImpl, new Object[] { existingStudy });
		
		assertNotNull(updatedStudy);
		assertThat(updatedStudy, is(existingStudy));
		assertThat(modalityPreUpdate, is(not(modalityPostUpdate)));
		assertThat(updatedStudy.getModality(), is(modalityPostUpdate));
	}
	
	/**
	 * @see RadiologyServiceImpl#updateStudyMwlStatus(RadiologyOrder,boolean)
	 * @verifies set the study mwlstatus of given radiology order to in sync given is in sync true
	 */
	@Test
	public void updateStudyMwlStatus_shouldSetTheStudyMwlstatusOfGivenRadiologyOrderToInSyncGivenIsInSyncTrue()
			throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		Study study = new Study();
		study.setStudyId(NON_EXISTING_STUDY_ID);
		study.setMwlStatus(MwlStatus.OUT_OF_SYNC);
		radiologyOrder.setStudy(study);
		updateStudyMwlStatusMethod.invoke(radiologyServiceImpl, new Object[] { radiologyOrder, true });
		
		assertThat(radiologyOrder.getStudy()
				.getMwlStatus(), is(MwlStatus.IN_SYNC));
		
		Study updatedStudy = radiologyServiceImpl.getStudyByStudyId(radiologyOrder.getStudy()
				.getStudyId());
		assertThat(updatedStudy.getMwlStatus(), is(radiologyOrder.getStudy()
				.getMwlStatus()));
	}
	
	/**
	 * @see RadiologyServiceImpl#updateStudyMwlStatus(RadiologyOrder,boolean)
	 * @verifies set the study mwlstatus of given radiology order to out of sync given is in sync false
	 */
	@Test
	public void updateStudyMwlStatus_shouldSetTheStudyMwlstatusOfGivenRadiologyOrderToOutOfSyncGivenIsInSyncFalse()
			throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		Study study = new Study();
		study.setStudyId(NON_EXISTING_STUDY_ID);
		study.setMwlStatus(MwlStatus.IN_SYNC);
		radiologyOrder.setStudy(study);
		updateStudyMwlStatusMethod.invoke(radiologyServiceImpl, new Object[] { radiologyOrder, false });
		
		assertThat(radiologyOrder.getStudy()
				.getMwlStatus(), is(MwlStatus.OUT_OF_SYNC));
		
		Study updatedStudy = radiologyServiceImpl.getStudyByStudyId(radiologyOrder.getStudy()
				.getStudyId());
		assertThat(updatedStudy.getMwlStatus(), is(radiologyOrder.getStudy()
				.getMwlStatus()));
	}
	
	/**
	 * @see RadiologyServiceImpl#saveRadiologyOrderEncounter(Patient,Provider,Date)
	 * @verifies create radiology order encounter attached to existing active visit given patient with active visit
	 */
	@Test
	public void saveRadiologyOrderEncounter_shouldCreateRadiologyOrderEncounterAttachedToExistingActiveVisitGivenPatientWithActiveVisit()
			throws Exception {
		// given
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT);
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		
		List<Visit> preExistingVisits = visitService.getActiveVisitsByPatient(patient);
		assertThat(encounterService.getEncountersByPatient(patient), is(empty()));
		assertThat(visitService.getActiveVisitsByPatient(patient), is(not(empty())));
		
		Encounter encounter = (Encounter) saveRadiologyOrderEncounterMethod.invoke(radiologyServiceImpl, new Object[] {
				patient, provider, encounterDatetime });
		
		assertNotNull(encounter);
		assertThat(encounter.getPatient(), is(patient));
		assertThat(encounter.getProvidersByRole(radiologyProperties.getRadiologyOrderingProviderEncounterRole())
				.size(), is(1));
		assertThat(encounter.getProvidersByRole(radiologyProperties.getRadiologyOrderingProviderEncounterRole())
				.contains(provider), is(true));
		assertThat(encounter.getEncounterDatetime(), is(encounterDatetime));
		assertThat(encounter.getVisit()
				.getVisitType(), is(radiologyProperties.getRadiologyVisitType()));
		assertThat(encounter.getEncounterType(), is(radiologyProperties.getRadiologyOrderEncounterType()));
		assertThat(encounterService.getEncountersByPatient(patient), is(Arrays.asList(encounter)));
		assertThat(visitService.getActiveVisitsByPatient(patient), is(preExistingVisits));
	}
	
	/**
	 * @see RadiologyServiceImpl#saveRadiologyOrderEncounter(Patient,Provider,Date)
	 * @verifies create radiology order encounter attached to new active visit given patient without active visit
	 */
	@Test
	public void saveRadiologyOrderEncounter_shouldCreateRadiologyOrderEncounterAttachedToNewActiveVisitGivenPatientWithoutActiveVisit()
			throws Exception {
		// given
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER_AND_NO_ACTIVE_VISIT);
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		
		assertThat(encounterService.getEncountersByPatient(patient), is(empty()));
		assertThat(visitService.getActiveVisitsByPatient(patient), is(empty()));
		
		Encounter encounter = (Encounter) saveRadiologyOrderEncounterMethod.invoke(radiologyServiceImpl, new Object[] {
				patient, provider, encounterDatetime });
		
		assertNotNull(encounter);
		assertThat(encounter.getPatient(), is(patient));
		assertThat(encounter.getProvidersByRole(radiologyProperties.getRadiologyOrderingProviderEncounterRole())
				.size(), is(1));
		assertThat(encounter.getProvidersByRole(radiologyProperties.getRadiologyOrderingProviderEncounterRole())
				.contains(provider), is(true));
		assertThat(encounter.getEncounterDatetime(), is(encounterDatetime));
		assertThat(encounter.getVisit()
				.getVisitType(), is(radiologyProperties.getRadiologyVisitType()));
		assertThat(encounter.getEncounterType(), is(radiologyProperties.getRadiologyOrderEncounterType()));
		assertThat(encounterService.getEncountersByPatient(patient), is(Arrays.asList(encounter)));
		assertThat(visitService.getVisitsByPatient(patient), is(not(empty())));
	}
}
