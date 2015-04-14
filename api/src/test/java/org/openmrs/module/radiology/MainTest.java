/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.radiology;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Study.PerformedStatuses;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests the methods in {@link Main}
 */
public class MainTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceTestDataSet.xml";
	
	private static final int PATIENT_ID_WITHOUT_ANY_ORDERS = 70010;
	
	private static final int PATIENT_ID_WITHOUT_STUDIES = 70011;
	
	private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER = 70011;
	
	private static final int PATIENT_ID_WITH_STUDIES = 70021;
	
	private static final int EXISTING_STUDY_ID = 1;
	
	private static final int NON_EXISTING_STUDY_ID = 9999;
	
	private static final int STUDY_ID_WITH_ONE_OBS = 2;
	
	private static final int STUDY_ID_WITHOUT_OBS = 1;
	
	private static final String EXISTING_STUDY_UID = "1.2.826.0.1.3680043.8.2186.1.1";
	
	private static final String NON_EXISTING_STUDY_UID = "1.2.826.0.1.3680043.8.2186.1.9999";
	
	protected static final String GLOBAL_PROPERTY_MWL_DIRECTORY = "radiology.mwlDirectory";
	
	protected static final String MWL_DIRECTORY = "mwl";
	
	private AdministrationService administrationService = null;
	
	private PatientService patientService = null;
	
	private ConceptService conceptService = null;
	
	private OrderService orderService = null;
	
	private Main radiologyService = null;
	
	@Rule
	public TemporaryFolder temporaryBaseFolder = new TemporaryFolder();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void runBeforeAllTests() throws Exception {
		if (administrationService == null) {
			administrationService = Context.getAdministrationService();
		}
		
		if (patientService == null) {
			patientService = Context.getPatientService();
		}
		
		if (conceptService == null) {
			conceptService = Context.getConceptService();
		}
		
		if (orderService == null) {
			orderService = Context.getOrderService();
		}
		
		if (radiologyService == null) {
			radiologyService = Context.getService(Main.class);
		}
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * Convenience method to have a Study object with all required values filled in
	 * 
	 * @return a mock Study object that can be saved
	 */
	public Study getMockStudy() {
		Patient patient = patientService.getPatient(PATIENT_ID_WITHOUT_STUDIES);
		
		Concept concept = conceptService.getConcept(178);
		OrderType radiologyOrderType = orderService.getOrderType(5);
		
		Order order = new Order();
		order.setOrderType(radiologyOrderType);
		order.setPatient(patient);
		order.setConcept(concept);
		Calendar cal = Calendar.getInstance();
		cal.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		order.setStartDate(cal.getTime());
		order.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Study study = new Study();
		study.setOrder(order);
		study.setModality(Modality.CT);
		study.setPriority(0);
		
		return study;
	}
	
	/**
	 * @see Main#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should create new study from given study object", method = "saveStudy(Study)")
	public void saveStudy_shouldCreateNewStudyFromGivenStudyObject() throws Exception {
		Study mockStudy = getMockStudy();
		orderService.saveOrder(mockStudy.getOrder());
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(GLOBAL_PROPERTY_MWL_DIRECTORY, temporaryMwlFolder
		        .getAbsolutePath()));
		radiologyService.saveStudy(mockStudy);
		
		Study createdStudy = radiologyService.getStudy(mockStudy.getId());
		assertNotNull(createdStudy);
		assertThat(createdStudy, is(mockStudy));
		assertThat(createdStudy.getId(), is(mockStudy.getId()));
		assertThat(createdStudy.getModality(), is(mockStudy.getModality()));
		assertNotNull(createdStudy.getOrder());
		assertThat(createdStudy.getOrder(), is(mockStudy.getOrder()));
	}
	
	/**
	 * @see Main#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should create new study and set its uid", method = "saveStudy(Study)")
	public void saveStudy_shouldCreateNewStudyAndSetItsUid() throws Exception {
		Study mockStudy = getMockStudy();
		orderService.saveOrder(mockStudy.getOrder());
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(GLOBAL_PROPERTY_MWL_DIRECTORY, temporaryMwlFolder
		        .getAbsolutePath()));
		radiologyService.saveStudy(mockStudy);
		
		Study createdStudy = radiologyService.getStudy(mockStudy.getId());
		assertNotNull(createdStudy);
		assertThat(createdStudy, is(mockStudy));
		assertNotNull(createdStudy.getUid());
		assertThat(createdStudy.getUid(), is(Utils.studyPrefix() + createdStudy.getId()));
	}
	
	/**
	 * @see Main#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should update existing study", method = "saveStudy(Study)")
	public void saveStudy_shouldUpdateExistingStudy() throws Exception {
		Study existingStudy = radiologyService.getStudy(EXISTING_STUDY_ID);
		Modality modalityPreUpdate = existingStudy.getModality();
		Modality modalityPostUpdate = Modality.XA;
		existingStudy.setModality(modalityPostUpdate);
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(GLOBAL_PROPERTY_MWL_DIRECTORY, temporaryMwlFolder
		        .getAbsolutePath()));
		radiologyService.saveStudy(existingStudy);
		
		Study updatedStudy = radiologyService.getStudy(existingStudy.getId());
		assertNotNull(updatedStudy);
		assertThat(updatedStudy, is(existingStudy));
		assertThat(modalityPreUpdate, is(not(modalityPostUpdate)));
		assertThat(updatedStudy.getModality(), is(modalityPostUpdate));
	}
	
	/**
	 * @see Main#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should not save study given study with empty modality", method = "saveStudy(Study)")
	public void saveStudy_shouldNotSaveStudyGivenStudyWithEmptyModality() {
		Study mockStudy = getMockStudy();
		mockStudy.setModality(null);
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(GLOBAL_PROPERTY_MWL_DIRECTORY, temporaryMwlFolder
		        .getAbsolutePath()));
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Study.modality.required");
		radiologyService.saveStudy(mockStudy);
	}
	
	/**
	 * @see Main#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should not save study given study with empty order", method = "saveStudy(Study)")
	public void saveStudy_shouldNotSaveStudyGivenStudyWithEmptyOrder() {
		Study mockStudy = getMockStudy();
		mockStudy.setOrder(null);
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(GLOBAL_PROPERTY_MWL_DIRECTORY, temporaryMwlFolder
		        .getAbsolutePath()));
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Study.order.required");
		radiologyService.saveStudy(mockStudy);
	}
	
	/**
	 * @see Main#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException if study is null", method = "saveStudy(Study)")
	public void saveStudy_shouldThrowIllegalArgumentExceptionIfStudyIsNull() {
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(GLOBAL_PROPERTY_MWL_DIRECTORY, temporaryMwlFolder
		        .getAbsolutePath()));
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("study is required");
		radiologyService.saveStudy(null);
	}
	
	/**
	 * @see Main#getStudyByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should return study matching order with given orderId", method = "getStudyByOrderId(Integer)")
	public void getStudyByOrderId_shouldReturnStudyMatchingId() throws Exception {
		Integer ORDER_ID_OF_EXISTING_STUDY_ID = 2001;
		Study expectedStudy = radiologyService.getStudy(EXISTING_STUDY_ID);
		Study study = radiologyService.getStudyByOrderId(ORDER_ID_OF_EXISTING_STUDY_ID);
		
		assertNotNull(study);
		assertThat(study.getId(), is(EXISTING_STUDY_ID));
		assertThat(study.getOrder().getOrderId(), is(ORDER_ID_OF_EXISTING_STUDY_ID));
		assertThat(study, is(expectedStudy));
	}
	
	/**
	 * @see Main#getStudyByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should return new study instance if no match is found", method = "getStudyByOrderId(Integer)")
	public void getStudyByOrderId_shouldReturnNewStudyInstanceIfNoMatchIsFound() throws Exception {
		Integer NON_EXISTING_ORDER_ID = 9999;
		Study study = radiologyService.getStudyByOrderId(NON_EXISTING_ORDER_ID);
		
		assertNotNull(study);
		assertThat(study, instanceOf(Study.class));
		assertNull(study.getOrder());
	}
	
	/**
	 * @see Main#updateStudyPerformedStatus(Study, int)
	 */
	@Test
	@Verifies(value = "should update performed status of given study in database to given performed status", method = "updateStudyPerformedStatus(Study, int)")
	public void updateStudyPerformedStatus_shouldUpdatePerformedStatusOfGivenStudyInDatabaseToGivenPerformedStatus()
	        throws Exception {
		Study existingStudy = radiologyService.getStudy(EXISTING_STUDY_ID);
		
		radiologyService.updateStudyPerformedStatus(existingStudy, PerformedStatuses.IN_PROGRESS);
		existingStudy = radiologyService.getStudy(EXISTING_STUDY_ID);
		assertThat(existingStudy.getPerformedStatus(), is(PerformedStatuses.IN_PROGRESS));
		
		radiologyService.updateStudyPerformedStatus(existingStudy, PerformedStatuses.COMPLETED);
		existingStudy = radiologyService.getStudy(EXISTING_STUDY_ID);
		assertThat(existingStudy.getPerformedStatus(), is(PerformedStatuses.COMPLETED));
		
		radiologyService.updateStudyPerformedStatus(existingStudy, PerformedStatuses.DISCONTINUED);
		existingStudy = radiologyService.getStudy(EXISTING_STUDY_ID);
		assertThat(existingStudy.getPerformedStatus(), is(PerformedStatuses.DISCONTINUED));
	}
	
	/**
	 * @see Main#updateStudyPerformedStatus(Study, int)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException if study is null", method = "updateStudyPerformedStatus(Study, int)")
	public void updateStudyPerformedStatus_shouldThrowIllegalArgumentExceptionIfStudyIsNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("study is required");
		radiologyService.updateStudyPerformedStatus(null, PerformedStatuses.IN_PROGRESS);
	}
	
	/**
	 * @see Main#updateStudyPerformedStatus(Study, int)
	 */
	@Test
	@Verifies(value = "should not update non existing study", method = "updateStudyPerformedStatus(Study, int)")
	public void updateStudyPerformedStatus_shouldNotUpdateNonExistingStudy() throws Exception {
		Study nonExistingStudy = getMockStudy();
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Study.cannot.edit.nonexisting");
		radiologyService.updateStudyPerformedStatus(nonExistingStudy, PerformedStatuses.IN_PROGRESS);
	}
	
	/**
	 * @see Main#getStudy(Integer)
	 */
	@Test
	@Verifies(value = "should return study matching id", method = "getStudy(Integer)")
	public void getStudy_shouldReturnStudyMatchingId() throws Exception {
		Study study = radiologyService.getStudy(EXISTING_STUDY_ID);
		
		assertNotNull(study);
		assertThat(study.getId(), is(EXISTING_STUDY_ID));
	}
	
	/**
	 * @see Main#getStudy(Integer)
	 */
	@Test
	@Verifies(value = "should return null if no match was found", method = "getStudy(Integer)")
	public void getStudy_shouldReturnNullIfNoMatchIsFound() throws Exception {
		Study study = radiologyService.getStudy(NON_EXISTING_STUDY_ID);
		
		assertNull(study);
	}
	
	/**
	 * @see Main#getStudy(Integer)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException if id is null", method = "getStudy(Integer)")
	public void getStudy_shouldThrowIllegalArgumentExceptionIfIdIsNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("id is required");
		radiologyService.getStudy(null);
	}
	
	/**
	 * @see Main#getStudyByOrder(Order)
	 */
	@Test
	@Verifies(value = "should return study matching order", method = "getStudyByOrder(Order)")
	public void getStudyByOrder_shouldReturnStudyMatchingOrder() throws Exception {
		int EXISTING_ORDER_ID = 2001;
		Order order = Context.getOrderService().getOrder(EXISTING_ORDER_ID);
		Study study = radiologyService.getStudyByOrder(order);
		
		assertNotNull(study);
		assertThat(study.getOrder(), is(order));
		assertThat(study.getOrder().getOrderId(), is(EXISTING_ORDER_ID));
	}
	
	/**
	 * @see Main#getStudyByOrder(Order)
	 */
	@Test
	@Verifies(value = "should return null if no match was found", method = "getStudyByOrder(Order)")
	public void getStudyByOrder_shouldReturnNullIfNoMatchIsFound() throws Exception {
		int EXISTING_ORDER_ID_OF_NON_RADIOLOGY_ORDER = 3;
		Order order = Context.getOrderService().getOrder(EXISTING_ORDER_ID_OF_NON_RADIOLOGY_ORDER);
		Study study = radiologyService.getStudyByOrder(order);
		
		assertNull(study);
	}
	
	/**
	 * @see Main#getStudyByOrder(Order)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException if order is null", method = "getStudyByOrder(Order)")
	public void getStudyByOrder_ShouldThrowIllegalArgumentExceptionIfOrderIsNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("order is required");
		radiologyService.getStudyByOrder(null);
	}
	
	/**
	 * @see Main#getStudyByUid(String)
	 */
	@Test
	@Verifies(value = "should return study matching uid", method = "getStudyByUid(String)")
	public void getStudyByUid_shouldReturnStudyMatchingUid() throws Exception {
		Study study = radiologyService.getStudyByUid(EXISTING_STUDY_UID);
		
		assertNotNull(study);
		assertThat(study.getUid(), is(EXISTING_STUDY_UID));
	}
	
	/**
	 * @see Main#getStudyByUid(String)
	 */
	@Test
	@Verifies(value = "should return null if no match was found", method = "getStudyByUid(String)")
	public void getStudyByUid_shouldReturnNullIfNoMatchIsFound() throws Exception {
		Study study = radiologyService.getStudyByUid(NON_EXISTING_STUDY_UID);
		
		assertNull(study);
	}
	
	/**
	 * @see Main#getStudyByUid(String)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException if uid is null", method = "getStudyByUid(String)")
	public void getStudyByUid_shouldThrowIllegalArgumentExceptionIfUidIsNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("uid is required");
		radiologyService.getStudyByUid(null);
	}
	
	/**
	 * @see Main#getStudiesByPatient(Patient)
	 */
	@Test
	@Verifies(value = "should fetch all studies for given patient", method = "getStudiesByPatient(Patient)")
	public void getStudiesByPatient_shouldFetchAllStudiesForGivenPatient() throws Exception {
		Patient patient = Context.getPatientService().getPatient(PATIENT_ID_WITH_STUDIES);
		List<Study> studies = radiologyService.getStudiesByPatient(patient);
		
		assertThat(studies.size(), is(2));
		
		assertThat(studies.get(0).getId(), is(1));
		assertThat(studies.get(0).getOrder().getOrderId(), is(2001));
		assertThat(studies.get(0).getOrder().getPatient().getId(), is(PATIENT_ID_WITH_STUDIES));
		assertThat(studies.get(0).getModality(), is(Modality.CT));
		
		assertThat(studies.get(1).getId(), is(2));
		assertThat(studies.get(1).getOrder().getOrderId(), is(2002));
		assertThat(studies.get(0).getOrder().getPatient().getId(), is(PATIENT_ID_WITH_STUDIES));
		assertThat(studies.get(1).getModality(), is(Modality.MR));
	}
	
	/**
	 * @see Main#getStudiesByPatient(Patient)
	 */
	@Test
	@Verifies(value = "should return empty list for given patient without studies", method = "getStudiesByPatient(Patient)")
	public void getStudiesByPatient_shouldReturnEmptyListForGivenPatientWithoutStudies() throws Exception {
		Patient patient = Context.getPatientService().getPatient(PATIENT_ID_WITHOUT_STUDIES);
		List<Study> studies = radiologyService.getStudiesByPatient(patient);
		
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see Main#getStudiesByPatient(Patient)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException if patient is null", method = "getStudiesByPatient(Patient)")
	public void getStudiesByPatient_shouldThrowIllegalArgumentExceptionIfPatientIsNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("patient is required");
		radiologyService.getStudiesByPatient(null);
	}
	
	/**
	 * @see Main#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should fetch all studies for given orders", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldFetchAllStudiesForGivenOrders() throws Exception {
		Patient patient = Context.getPatientService().getPatient(PATIENT_ID_WITH_STUDIES);
		List<Order> orders = null;
		orders = orderService.getOrdersByPatient(patient);
		
		List<Study> studies = radiologyService.getStudiesByOrders(orders);
		
		assertThat(studies.size(), is(2));
		assertThat(studies.get(0).getOrder(), is(orders.get(0)));
		assertThat(studies.get(1).getOrder(), is(orders.get(1)));
	}
	
	/**
	 * @see Main#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should return empty list for given orders without studies", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldReturnEmptyListForGivenOrdersWithoutStudies() throws Exception {
		Patient patient = Context.getPatientService().getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
		List<Order> orders = null;
		orders = orderService.getOrdersByPatient(patient);
		
		List<Study> studies = radiologyService.getStudiesByOrders(orders);
		
		assertThat(orders.size(), is(1));
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see Main#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException if orders is null", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldThrowIllegalArgumentExceptionIfOrdersIsNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orders are required");
		radiologyService.getStudiesByOrders(null);
	}
	
	/**
	 * @see Main#getObservationsByStudy(Study)
	 */
	@Test
	@Verifies(value = "should fetch all obs for given study", method = "getObservationsByStudy(Study)")
	public void getObservationsByStudy_shouldFetchAllObsForGivenStudy() throws Exception {
		Study study = radiologyService.getStudy(STUDY_ID_WITH_ONE_OBS);
		
		List<Obs> obs = radiologyService.getObservationsByStudy(study);
		
		assertThat(obs.size(), is(1));
		assertThat(obs.get(0).getOrder().getOrderId(), is(study.getOrder().getOrderId()));
	}
	
	/**
	 * @see Main#getObservationsByStudy(Study)
	 */
	@Test
	@Verifies(value = "should return empty list for given study without obs", method = "getObservationsByStudy(Study)")
	public void getObservationsByStudy_shouldReturnEmptyListForGivenStudyWithoutObs() throws Exception {
		Study study = radiologyService.getStudy(STUDY_ID_WITHOUT_OBS);
		
		List<Obs> obs = radiologyService.getObservationsByStudy(study);
		
		assertThat(obs.size(), is(0));
	}
	
	/**
	 * @see Main#getObservationsByStudy(Study)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException if study is null", method = "getObservationsByStudy(Study)")
	public void getObservationsByStudy_shouldThrowIllegalArgumentExceptionIfStudyIsNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("study is required");
		radiologyService.getObservationsByStudy(null);
	}
}
