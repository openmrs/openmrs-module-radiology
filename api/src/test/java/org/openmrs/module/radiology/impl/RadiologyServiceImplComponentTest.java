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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.study.RadiologyStudyService;
import org.openmrs.module.radiology.study.Study;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link RadiologyServiceImpl}
 */
public class RadiologyServiceImplComponentTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceComponentTestDataset.xml";
	
	private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER_AND_NO_ACTIVE_VISIT = 70011;
	
	private static final int PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT = 70033;
	
	private static final int NON_EXISTING_STUDY_ID = 99999;
	
	private static final String RADIOLOGY_ORDER_PROVIDER_UUID = "c2299800-cca9-11e0-9572-0800200c9a66";
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private EncounterService encounterService;
	
	@Autowired
	private VisitService visitService;
	
	@Autowired
	private EmrEncounterService emrEncounterService;
	
	@Autowired
	private ProviderService providerService;
	
	@Autowired
	private OrderService orderService;
	
	private RadiologyServiceImpl radiologyServiceImpl = null;
	
	@Autowired
	private RadiologyStudyService radiologyStudyService;
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	private Method saveRadiologyOrderEncounterMethod = null;
	
	private Method updateStudyMwlStatusMethod;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void runBeforeAllTests() throws Exception {
		
		if (radiologyServiceImpl == null) {
			radiologyServiceImpl = new RadiologyServiceImpl();
			Field radiologyStudyServiceField = RadiologyServiceImpl.class.getDeclaredField("radiologyStudyService");
			radiologyStudyServiceField.setAccessible(true);
			radiologyStudyServiceField.set(radiologyServiceImpl, radiologyStudyService);
			Field orderServiceField = RadiologyServiceImpl.class.getDeclaredField("orderService");
			orderServiceField.setAccessible(true);
			orderServiceField.set(radiologyServiceImpl, orderService);
			Field encounterServiceField = RadiologyServiceImpl.class.getDeclaredField("encounterService");
			encounterServiceField.setAccessible(true);
			encounterServiceField.set(radiologyServiceImpl, encounterService);
			Field emrEncounterServiceField = RadiologyServiceImpl.class.getDeclaredField("emrEncounterService");
			emrEncounterServiceField.setAccessible(true);
			emrEncounterServiceField.set(radiologyServiceImpl, emrEncounterService);
			Field radiologyPropertiesField = RadiologyServiceImpl.class.getDeclaredField("radiologyProperties");
			radiologyPropertiesField.setAccessible(true);
			radiologyPropertiesField.set(radiologyServiceImpl, radiologyProperties);
		}
		
		updateStudyMwlStatusMethod = RadiologyServiceImpl.class.getDeclaredMethod("updateStudyMwlStatus", new Class[] {
				RadiologyOrder.class, boolean.class });
		updateStudyMwlStatusMethod.setAccessible(true);
		
		saveRadiologyOrderEncounterMethod = RadiologyServiceImpl.class.getDeclaredMethod("saveRadiologyOrderEncounter",
			new Class[] { Patient.class, Provider.class, Date.class });
		saveRadiologyOrderEncounterMethod.setAccessible(true);
		
		executeDataSet(STUDIES_TEST_DATASET);
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
		
		Study updatedStudy = radiologyStudyService.getStudyByStudyId(radiologyOrder.getStudy()
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
		
		Study updatedStudy = radiologyStudyService.getStudyByStudyId(radiologyOrder.getStudy()
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
