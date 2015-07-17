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

import java.io.File;
import java.util.ArrayList;
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
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests {@link RadiologyService}
 */
public class RadiologyServiceTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceTestDataSet.xml";
	
	private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER = 70011;
	
	private static final int PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER = 70021;
	
	private static final int ORDER_ID_WITH_ONE_OBS = 2002;
	
	private static final int ORDER_ID_WITHOUT_OBS = 2001;
	
	private static final int ORDER_ID_WITHOUT_STUDY = 2004;
	
	private static final String EXISTING_STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.2186.1.1";
	
	private static final String NON_EXISTING_STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.2186.1.9999";
	
	private static final int EXISTING_STUDY_ID = 1;
	
	private static final String GLOBAL_PROPERTY_MWL_DIRECTORY = "radiology.mwlDirectory";
	
	private static final String MWL_DIRECTORY = "mwl";
	
	private PatientService patientService = null;
	
	private OrderService orderService = null;
	
	private ConceptService conceptService = null;
	
	private AdministrationService administrationService = null;
	
	private RadiologyService radiologyService = null;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Rule
	public TemporaryFolder temporaryBaseFolder = new TemporaryFolder();
	
	@Before
	public void runBeforeAllTests() throws Exception {
		
		if (patientService == null) {
			patientService = Context.getPatientService();
		}
		
		if (orderService == null) {
			orderService = Context.getOrderService();
		}
		
		if (conceptService == null) {
			conceptService = Context.getConceptService();
		}
		
		if (administrationService == null) {
			administrationService = Context.getAdministrationService();
		}
		
		if (radiologyService == null) {
			radiologyService = Context.getService(RadiologyService.class);
		}
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * Convenience method to have a Study object with all required values filled in
	 * 
	 * @return a mock Study object that can be saved
	 */
	public Study getMockStudy() {
		
		Study study = new Study();
		study.setOrderId(orderService.getOrder(ORDER_ID_WITHOUT_STUDY).getOrderId());
		study.setModality(Modality.CT);
		study.setPriority(RequestedProcedurePriority.LOW);
		study.setMwlStatus(MwlStatus.DEFAULT);
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		
		return study;
	}
	
	/**
	 * @see RadiologyService#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should create new study from given study object", method = "saveStudy(Study)")
	public void saveStudy_shouldCreateNewStudyFromGivenStudyObject() throws Exception {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(GLOBAL_PROPERTY_MWL_DIRECTORY, temporaryMwlFolder
		        .getAbsolutePath()));
		
		Order mockOrder = orderService.getOrder(ORDER_ID_WITHOUT_STUDY);
		Study mockStudy = getMockStudy();
		mockStudy.setOrderId(mockOrder.getOrderId());
		
		radiologyService.saveStudy(mockStudy);
		
		Study createdStudy = radiologyService.getStudy(mockStudy.getId());
		assertNotNull(createdStudy);
		assertThat(createdStudy, is(mockStudy));
		assertThat(createdStudy.getId(), is(mockStudy.getId()));
		assertThat(createdStudy.getModality(), is(mockStudy.getModality()));
		assertThat(createdStudy.getOrderId(), is(mockStudy.getOrderId()));
	}
	
	/**
	 * @see RadiologyService#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should update existing study", method = "saveStudy(Study)")
	public void saveStudy_shouldUpdateExistingStudy() throws Exception {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(GLOBAL_PROPERTY_MWL_DIRECTORY, temporaryMwlFolder
		        .getAbsolutePath()));
		
		Study existingStudy = radiologyService.getStudy(EXISTING_STUDY_ID);
		Modality modalityPreUpdate = existingStudy.getModality();
		Modality modalityPostUpdate = Modality.XA;
		existingStudy.setModality(modalityPostUpdate);
		
		Study updatedStudy = radiologyService.saveStudy(existingStudy);
		
		updatedStudy = radiologyService.getStudy(updatedStudy.getId());
		assertNotNull(updatedStudy);
		assertThat(updatedStudy, is(existingStudy));
		assertThat(modalityPreUpdate, is(not(modalityPostUpdate)));
		assertThat(updatedStudy.getModality(), is(modalityPostUpdate));
	}
	
	/**
	 * @see RadiologyService#saveStudy(Study)
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
	 * @see RadiologyService#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should throw APIException given study with empty order id", method = "saveStudy(Study)")
	public void saveStudy_shouldThrowAPIExceptionGivenStudyWithEmptyOrderId() {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(GLOBAL_PROPERTY_MWL_DIRECTORY, temporaryMwlFolder
		        .getAbsolutePath()));
		
		Study mockStudy = getMockStudy();
		mockStudy.setOrderId(null);
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Study.order.required");
		radiologyService.saveStudy(mockStudy);
	}
	
	/**
	 * @see RadiologyService#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should throw APIException given study with empty modality", method = "saveStudy(Study)")
	public void saveStudy_shouldThrowAPIExceptionGivenStudyWithEmptyModality() {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(GLOBAL_PROPERTY_MWL_DIRECTORY, temporaryMwlFolder
		        .getAbsolutePath()));
		
		Order mockOrder = orderService.getOrder(ORDER_ID_WITHOUT_STUDY);
		Study mockStudy = getMockStudy();
		mockStudy.setOrderId(mockOrder.getOrderId());
		mockStudy.setModality(null);
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Study.modality.required");
		radiologyService.saveStudy(mockStudy);
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 */
	@Test
	@Verifies(value = "should return study matching study instance uid", method = "getStudyByStudyInstanceUid(String)")
	public void getStudyByStudyInstanceUid_shouldReturnStudyMatchingUid() throws Exception {
		Study study = radiologyService.getStudyByStudyInstanceUid(EXISTING_STUDY_INSTANCE_UID);
		
		assertNotNull(study);
		assertThat(study.getUid(), is(EXISTING_STUDY_INSTANCE_UID));
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 */
	@Test
	@Verifies(value = "should return null if no match was found", method = "getStudyByStudyInstanceUid(String)")
	public void getStudyByStudyInstanceUid_shouldReturnNullIfNoMatchIsFound() throws Exception {
		Study study = radiologyService.getStudyByStudyInstanceUid(NON_EXISTING_STUDY_INSTANCE_UID);
		
		assertNull(study);
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException if study instance uid is null", method = "getStudyByStudyInstanceUid(String)")
	public void getStudyByStudyInstanceUid_shouldThrowIllegalArgumentExceptionIfUidIsNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("studyInstanceUid is required");
		radiologyService.getStudyByStudyInstanceUid(null);
	}
	
	/**
	 * @see RadiologyService#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should fetch all studies for given orders", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldFetchAllStudiesForGivenOrders() throws Exception {
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER);
		List<Order> orders = null;
		orders = orderService.getOrdersByPatient(patient);
		
		List<Study> studies = radiologyService.getStudiesByOrders(orders);
		
		assertThat(studies.size(), is(orders.size()));
		assertThat(studies.get(0).getOrderId(), is(orders.get(0).getOrderId()));
		assertThat(studies.get(1).getOrderId(), is(orders.get(1).getOrderId()));
	}
	
	/**
	 * @see RadiologyService#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should return empty list given orders without associated studies", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldReturnEmptyListGivenOrdersWithoutAssociatedStudies() throws Exception {
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
		List<Order> orders = null;
		orders = orderService.getOrdersByPatient(patient);
		
		List<Study> studies = radiologyService.getStudiesByOrders(orders);
		
		assertThat(orders.size(), is(1));
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should return empty list given empty order list", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldReturnEmptyListGivenEmptyOrderList() throws Exception {
		List<Order> orders = new ArrayList<Order>();
		
		List<Study> studies = radiologyService.getStudiesByOrders(orders);
		
		assertThat(orders.size(), is(0));
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException given null", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orders are required");
		radiologyService.getStudiesByOrders(null);
	}
	
	/**
	 * @see RadiologyService#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should fetch all obs for given orderId", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldFetchAllObsForGivenOrderId() throws Exception {
		List<Obs> obs = radiologyService.getObsByOrderId(ORDER_ID_WITH_ONE_OBS);
		
		assertThat(obs.size(), is(1));
		assertThat(obs.get(0).getOrder().getOrderId(), is(ORDER_ID_WITH_ONE_OBS));
	}
	
	/**
	 * @see RadiologyService#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should return empty list given orderId without associated obs", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldReturnEmptyListGivenOrderIdWithoutAssociatedObs() throws Exception {
		List<Obs> obs = radiologyService.getObsByOrderId(ORDER_ID_WITHOUT_OBS);
		
		assertThat(obs.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException given null", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orderId is required");
		radiologyService.getObsByOrderId(null);
	}
}
