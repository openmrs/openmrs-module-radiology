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
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.hamcrest.core.IsInstanceOf;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
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
	
	private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER_AND_NO_ACTIVE_VISIT = 70011;
	
	private static final int PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT = 70033;
	
	private static final int RADIOLOGY_ORDER_ID_WITHOUT_STUDY = 2004;
	
	private static final int EXISTING_STUDY_ID = 1;
	
	private static final String RADIOLOGY_ORDER_VISIT_TYPE_UUID = "fe898a34-1ade-11e1-9c71-00248140a5eb";
	
	private static final String RADIOLOGY_ORDER_PROVIDER_UUID = "c2299800-cca9-11e0-9572-0800200c9a66";
	
	private static final String RADIOLOGY_ORDER_ENCOUNTER_ROLE_UUID = "13fc9b4a-49ed-429c-9dde-ca005b387a3d";
	
	private static final String RADIOLOGY_ORDER_ENCOUNTER_TYPE_UUID = "19db8c0d-3520-48f2-babd-77f2d450e5c7";
	
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
	
	private StudyDAO studyDAO = null;
	
	private Method saveRadiologyOrderEncounterMethod = null;
	
	private Method saveStudyMethod = null;
	
	private Method setupEncounterTransactionMethod = null;
	
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
			radiologyServiceImpl.setEmrEncounterService(emrEncounterService);
			radiologyServiceImpl.setStudyDAO(studyDAO);
			radiologyServiceImpl.setRadiologyReportDAO(radiologyReportDAO);
			radiologyServiceImpl.setRadiologyProperties(radiologyProperties);
		}
		
		saveStudyMethod = RadiologyServiceImpl.class.getDeclaredMethod("saveStudy",
		    new Class[] { org.openmrs.module.radiology.Study.class });
		saveStudyMethod.setAccessible(true);
		
		saveRadiologyOrderEncounterMethod = RadiologyServiceImpl.class.getDeclaredMethod("saveRadiologyOrderEncounter",
		    new Class[] { Patient.class, Provider.class, Date.class });
		saveRadiologyOrderEncounterMethod.setAccessible(true);
		
		setupEncounterTransactionMethod = RadiologyServiceImpl.class.getDeclaredMethod("setUpEncounterTransaction",
		    new Class[] { Patient.class, Date.class, VisitType.class, EncounterType.class, Provider.class,
		            EncounterRole.class });
		setupEncounterTransactionMethod.setAccessible(true);
		
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
	
	/**
	 * @see RadiologyServiceImpl#saveRadiologyOrderEncounter(Patient,Provider,Date)
	 * @verifies create encounter for radiology order for given parameters
	 */
	@Test
	public void saveRadiologyOrderEncounter_shouldCreateEncounterForRadiologyOrderForGivenParameters() throws Exception {
		//given
		Patient patient = patientService
		        .getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT);
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		
		Encounter encounter = (Encounter) saveRadiologyOrderEncounterMethod.invoke(radiologyServiceImpl, new Object[] {
		        patient, provider, encounterDatetime });
		
		assertNotNull(encounter);
		assertThat(encounter.getPatient(), is(patient));
		assertThat(encounter.getProvidersByRole(radiologyProperties.getOrderingProviderEncounterRole()).size(), is(1));
		assertThat(encounter.getProvidersByRole(radiologyProperties.getOrderingProviderEncounterRole()).contains(provider),
		    is(true));
		assertThat(encounter.getEncounterDatetime(), is(encounterDatetime));
		assertThat(encounter.getEncounterType(), is(radiologyProperties.getRadiologyOrderEncounterType()));
	}
	
	/**
	 * @see RadiologyServiceImpl#saveRadiologyOrderEncounter(Patient,Provider,Date)
	 * @verifies create encounter for new radiology order for patient with existing visit
	 */
	@Test
	public void saveRadiologyOrderEncounter_shouldCreateEncounterForNewRadiologyOrderForPatientWithExistingVisit()
	        throws Exception {
		//given
		Patient patient = patientService
		        .getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT);
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		
		Visit preExistingVisit = visitService.getVisit(3001);
		assertThat(encounterService.getEncountersByPatient(patient), is(empty()));
		assertThat(visitService.getActiveVisitsByPatient(patient), is(Arrays.asList(preExistingVisit)));
		
		Encounter encounter = (Encounter) saveRadiologyOrderEncounterMethod.invoke(radiologyServiceImpl, new Object[] {
		        patient, provider, encounterDatetime });
		
		assertThat(encounterService.getEncountersByPatient(patient), is(Arrays.asList(encounter)));
		assertThat(visitService.getActiveVisitsByPatient(patient), is(Arrays.asList(preExistingVisit)));
	}
	
	/**
	 * @see RadiologyServiceImpl#saveRadiologyOrderEncounter(Patient,Provider,Date)
	 * @verifies create encounter for new radiology order for patient without existing visit
	 */
	@Test
	public void saveRadiologyOrderEncounter_shouldCreateEncounterForNewRadiologyOrderForPatientWithoutExistingVisit()
	        throws Exception {
		//given
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER_AND_NO_ACTIVE_VISIT);
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		
		assertThat(encounterService.getEncountersByPatient(patient), is(empty()));
		assertThat(visitService.getActiveVisitsByPatient(patient), is(empty()));
		
		Encounter encounter = (Encounter) saveRadiologyOrderEncounterMethod.invoke(radiologyServiceImpl, new Object[] {
		        patient, provider, encounterDatetime });
		
		assertThat(encounterService.getEncountersByPatient(patient), is(Arrays.asList(encounter)));
		assertThat(visitService.getVisitsByPatient(patient), is(not(empty())));
	}
	
	/**
	 * @see RadiologyServiceImpl#saveRadiologyOrderEncounter(Patient,Provider,Date)
	 * @verifies throw illegal state exception if encounter cannot be created
	 */
	@Test
	public void saveRadiologyOrderEncounter_shouldThrowIllegalStateExceptionIfEncounterCannotBeCreated() throws Exception {
		
		expectedException.expect(InvocationTargetException.class);
		expectedException.expectCause(IsInstanceOf.<Throwable> instanceOf(IllegalStateException.class));
		saveRadiologyOrderEncounterMethod.invoke(radiologyServiceImpl, new Object[] { null, null, null });
	}
	
	/**
	 * @see RadiologyServiceImpl#setUpEncounterTransaction(Patient,Date,VisitType,EncounterType)
	 * @verifies create encounter transaction for given parameters
	 */
	@Test
	public void setUpEncounterTransaction_shouldCreateEncounterTransactionForGivenParameters() throws Exception {
		//given
		Patient patient = patientService
		        .getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT);
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		VisitType visitType = visitService.getVisitTypeByUuid(RADIOLOGY_ORDER_VISIT_TYPE_UUID);
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(RADIOLOGY_ORDER_ENCOUNTER_TYPE_UUID);
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		EncounterRole encounterRole = radiologyProperties.getOrderingProviderEncounterRole();
		
		EncounterTransaction encounterTransaction = (EncounterTransaction) setupEncounterTransactionMethod.invoke(
		    radiologyServiceImpl, new Object[] { patient, encounterDatetime, visitType, encounterType, provider,
		            encounterRole });
		
		assertThat(encounterTransaction, is(notNullValue()));
		assertThat(encounterTransaction.getEncounterDateTime(), is(encounterDatetime));
		assertThat(encounterTransaction.getPatientUuid(), is(patient.getUuid()));
		assertThat(encounterTransaction.getVisitTypeUuid(), is(visitType.getUuid()));
		assertThat(encounterTransaction.getEncounterTypeUuid(), is(encounterType.getUuid()));
		assertThat(encounterTransaction.getProviders(), is(notNullValue()));
		assertThat(encounterTransaction.getProviders(), is(not(empty())));
		for (EncounterTransaction.Provider encounterProvider : encounterTransaction.getProviders()) {
			assertThat(encounterProvider.getUuid(), is(provider.getUuid()));
			assertThat(encounterProvider.getEncounterRoleUuid(), is(encounterRole.getUuid()));
		}
	}
	
	/**
	 * @see RadiologyServiceImpl#setUpEncounterTransaction(Patient,Date,VisitType,EncounterType)
	 * @verifies throw illegal state exception if patient is null
	 */
	@Test
	public void setUpEncounterTransaction_shouldThrowIllegalStateExceptionIfPatientIsNull() throws Exception {
		//given
		Patient patient = null;
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		VisitType visitType = visitService.getVisitTypeByUuid(RADIOLOGY_ORDER_VISIT_TYPE_UUID);
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(RADIOLOGY_ORDER_ENCOUNTER_TYPE_UUID);
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		EncounterRole encounterRole = encounterService.getEncounterRoleByUuid(RADIOLOGY_ORDER_ENCOUNTER_ROLE_UUID);
		
		expectedException.expect(InvocationTargetException.class);
		expectedException.expectCause(IsInstanceOf.<Throwable> instanceOf(IllegalArgumentException.class));
		setupEncounterTransactionMethod.invoke(radiologyServiceImpl, new Object[] { patient, encounterDatetime, visitType,
		        encounterType, provider, encounterRole });
	}
	
	/**
	 * @see RadiologyServiceImpl#setUpEncounterTransaction(Patient,Date,VisitType,EncounterType)
	 * @verifies throw illegal state exception if encounter date time is null
	 */
	@Test
	public void setUpEncounterTransaction_shouldThrowIllegalStateExceptionIfEncounterDateTimeIsNull() throws Exception {
		//given
		Patient patient = patientService
		        .getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT);
		Date encounterDatetime = null;
		VisitType visitType = visitService.getVisitTypeByUuid(RADIOLOGY_ORDER_VISIT_TYPE_UUID);
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(RADIOLOGY_ORDER_ENCOUNTER_TYPE_UUID);
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		EncounterRole encounterRole = encounterService.getEncounterRoleByUuid(RADIOLOGY_ORDER_ENCOUNTER_ROLE_UUID);
		
		expectedException.expect(InvocationTargetException.class);
		expectedException.expectCause(IsInstanceOf.<Throwable> instanceOf(IllegalArgumentException.class));
		setupEncounterTransactionMethod.invoke(radiologyServiceImpl, new Object[] { patient, encounterDatetime, visitType,
		        encounterType, provider, encounterRole });
	}
	
	/**
	 * @see RadiologyServiceImpl#setUpEncounterTransaction(Patient,Date,VisitType,EncounterType)
	 * @verifies throw illegal state exception if visit type is null
	 */
	@Test
	public void setUpEncounterTransaction_shouldThrowIllegalStateExceptionIfVisitTypeIsNull() throws Exception {
		//given
		Patient patient = patientService
		        .getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT);
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		VisitType visitType = null;
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(RADIOLOGY_ORDER_ENCOUNTER_TYPE_UUID);
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		EncounterRole encounterRole = encounterService.getEncounterRoleByUuid(RADIOLOGY_ORDER_ENCOUNTER_ROLE_UUID);
		
		expectedException.expect(InvocationTargetException.class);
		expectedException.expectCause(IsInstanceOf.<Throwable> instanceOf(IllegalArgumentException.class));
		setupEncounterTransactionMethod.invoke(radiologyServiceImpl, new Object[] { patient, encounterDatetime, visitType,
		        encounterType, provider, encounterRole });
	}
	
	/**
	 * @see RadiologyServiceImpl#setUpEncounterTransaction(Patient,Date,VisitType,EncounterType)
	 * @verifies throw illegal state exception if encounter type is null
	 */
	@Test
	public void setUpEncounterTransaction_shouldThrowIllegalStateExceptionIfEncounterTypeIsNull() throws Exception {
		//given
		Patient patient = patientService
		        .getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT);
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		VisitType visitType = visitService.getVisitTypeByUuid(RADIOLOGY_ORDER_VISIT_TYPE_UUID);
		EncounterType encounterType = null;
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		EncounterRole encounterRole = encounterService.getEncounterRoleByUuid(RADIOLOGY_ORDER_ENCOUNTER_ROLE_UUID);
		
		expectedException.expect(InvocationTargetException.class);
		expectedException.expectCause(IsInstanceOf.<Throwable> instanceOf(IllegalArgumentException.class));
		setupEncounterTransactionMethod.invoke(radiologyServiceImpl, new Object[] { patient, encounterDatetime, visitType,
		        encounterType, provider, encounterRole });
	}
	
	/**
	 * @see RadiologyServiceImpl#setUpEncounterTransaction(Patient,Date,VisitType,EncounterType,Provider,EncounterRole)
	 * @verifies throw illegal state exception if provider is null
	 */
	@Test
	public void setUpEncounterTransaction_shouldThrowIllegalStateExceptionIfProviderIsNull() throws Exception {
		//given
		Patient patient = patientService
		        .getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT);
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		VisitType visitType = visitService.getVisitTypeByUuid(RADIOLOGY_ORDER_VISIT_TYPE_UUID);
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(RADIOLOGY_ORDER_ENCOUNTER_TYPE_UUID);
		Provider provider = null;
		EncounterRole encounterRole = encounterService.getEncounterRoleByUuid(RADIOLOGY_ORDER_ENCOUNTER_ROLE_UUID);
		
		expectedException.expect(InvocationTargetException.class);
		expectedException.expectCause(IsInstanceOf.<Throwable> instanceOf(IllegalArgumentException.class));
		setupEncounterTransactionMethod.invoke(radiologyServiceImpl, new Object[] { patient, encounterDatetime, visitType,
		        encounterType, provider, encounterRole });
	}
	
	/**
	 * @see RadiologyServiceImpl#setUpEncounterTransaction(Patient,Date,VisitType,EncounterType,Provider,EncounterRole)
	 * @verifies throw illegal state exception if encounter role is null
	 */
	@Test
	public void setUpEncounterTransaction_shouldThrowIllegalStateExceptionIfEncounterRoleIsNull() throws Exception {
		//given
		Patient patient = patientService
		        .getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT);
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		VisitType visitType = visitService.getVisitTypeByUuid(RADIOLOGY_ORDER_VISIT_TYPE_UUID);
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(RADIOLOGY_ORDER_ENCOUNTER_TYPE_UUID);
		Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
		EncounterRole encounterRole = null;
		
		expectedException.expect(InvocationTargetException.class);
		expectedException.expectCause(IsInstanceOf.<Throwable> instanceOf(IllegalArgumentException.class));
		setupEncounterTransactionMethod.invoke(radiologyServiceImpl, new Object[] { patient, encounterDatetime, visitType,
		        encounterType, provider, encounterRole });
	}
}
