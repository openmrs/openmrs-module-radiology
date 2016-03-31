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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.db.RadiologyReportDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.openmrs.module.radiology.db.hibernate.RadiologyReportDAOImpl;
import org.openmrs.module.radiology.db.hibernate.StudyDAOImpl;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests {@link RadiologyServiceImpl}
 */
public class RadiologyServiceImplComponentTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceComponentTestDataset.xml";
	
	private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER = 70011;
	
	private static final int RADIOLOGY_ORDER_ID_WITHOUT_STUDY = 2004;
	
	private static final int EXISTING_STUDY_ID = 1;
	
	private PatientService patientService = null;
	
	private AdministrationService administrationService = null;
	
	private EncounterService encounterService = null;
	
	private ProviderService providerService = null;
	
	private OrderService orderService = null;
	
	private RadiologyServiceImpl radiologyServiceImpl = null;
	
	private RadiologyService radiologyService = null;
	
	private RadiologyProperties radiologyProperties = null;
	
	private StudyDAO studyDAO = null;
	
	private Method saveRadiologyOrderEncounterMethod = null;
	
	private Method saveStudyMethod = null;
	
	private RadiologyReportDAO radiologyReportDAO;
	
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
		if (encounterService == null) {
			encounterService = Context.getEncounterService();
		}
		
		if (radiologyProperties == null) {
			radiologyProperties = Context.getRegisteredComponent("radiologyProperties", RadiologyProperties.class);
		}
		
		if (studyDAO == null) {
			StudyDAOImpl studyDAOImpl = new StudyDAOImpl();
			studyDAOImpl.setSessionFactory(Context.getRegisteredComponent("sessionFactory", SessionFactory.class));
			studyDAO = studyDAOImpl;
		}
		
		if (radiologyReportDAO == null) {
			RadiologyReportDAOImpl reportDAOImpl = new RadiologyReportDAOImpl();
			reportDAOImpl.setSessionFactory(Context.getRegisteredComponent("sessionFactory", SessionFactory.class));
			radiologyReportDAO = reportDAOImpl;
		}
		
		if (radiologyServiceImpl == null) {
			radiologyServiceImpl = new RadiologyServiceImpl();
			radiologyServiceImpl.setOrderService(orderService);
			radiologyServiceImpl.setEncounterService(encounterService);
			radiologyServiceImpl.setStudyDAO(studyDAO);
			radiologyServiceImpl.setRadiologyReportDAO(radiologyReportDAO);
			radiologyServiceImpl.setRadiologyProperties(radiologyProperties);
		}
		
		saveRadiologyOrderEncounterMethod = RadiologyServiceImpl.class.getDeclaredMethod("saveRadiologyOrderEncounter",
			new Class[] { org.openmrs.Patient.class, org.openmrs.Provider.class, java.util.Date.class });
		saveRadiologyOrderEncounterMethod.setAccessible(true);
		
		saveStudyMethod = RadiologyServiceImpl.class.getDeclaredMethod("saveStudy",
			new Class[] { org.openmrs.module.radiology.Study.class });
		saveStudyMethod.setAccessible(true);
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * @see RadiologyServiceImpl#saveRadiologyOrderEncounter(Patient,Provider,Date)
	 * @verifies save radiology order encounter for given parameters
	 */
	@Test
	public void saveRadiologyOrderEncounter_shouldSaveRadiologyOrderEncounterForGivenParameters() throws Exception {
		
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
		Provider provider = providerService.getProviderByIdentifier("1");
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		
		Encounter encounter = (Encounter) saveRadiologyOrderEncounterMethod.invoke(radiologyServiceImpl, new Object[] {
				patient, provider, encounterDatetime });
		
		assertNotNull(encounter);
		assertThat(encounter.getPatient(), is(patient));
		assertThat(encounter.getProvidersByRole(radiologyProperties.getOrderingProviderEncounterRole())
				.size(), is(1));
		assertThat(encounter.getProvidersByRole(radiologyProperties.getOrderingProviderEncounterRole())
				.contains(provider), is(true));
		assertThat(encounter.getEncounterDatetime(), is(encounterDatetime));
		assertThat(encounter.getEncounterType(), is(radiologyProperties.getRadiologyEncounterType()));
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
		study.setMwlStatus(MwlStatus.DEFAULT);
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
}
