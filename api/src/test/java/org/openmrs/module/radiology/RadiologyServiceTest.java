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
import java.util.Arrays;
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
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
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
	
	private static final int PATIENT_ID_WITH_TWO_RADIOLOGY_ORDERS = 70021;
	
	private static final int PATIENT_ID_WITH_ONE_RADIOLOGY_ORDER = 70022;
	
	private static final int RADIOLOGY_ORDER_ID_WITH_ONE_OBS = 2002;
	
	private static final int RADIOLOGY_ORDER_ID_WITHOUT_OBS = 2001;
	
	private static final int RADIOLOGY_ORDER_ID_WITHOUT_STUDY = 2004;
	
	private static final int EXISTING_RADIOLOGY_ORDER_ID = 2001;
	
	private static final int NON_EXISTING_RADIOLOGY_ORDER_ID = 99999;
	
	private static final String EXISTING_STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.2186.1.1";
	
	private static final String NON_EXISTING_STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.2186.1.9999";
	
	private static final int EXISTING_STUDY_ID = 1;
	
	private static final int CONCEPT_ID_FOR_FRACTURE = 178;
	
	private static final int TOTAL_NUMBER_OF_RADIOLOGY_ORDERS = 3;
	
	private static final String MWL_DIRECTORY = "mwl";
	
	private PatientService patientService = null;
	
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
	 * @see RadiologyService#saveRadiologyOrder(RadiologyOrder)
	 */
	@Test
	@Verifies(value = "should create new radiology order from given radiology order object", method = "saveRadiologyOrder(RadiologyOrder)")
	public void saveRadiologyOrder_shouldCreateNewRadiologyOrderGivenRadiologyOrderObject() {
		
		RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
		
		radiologyOrder = radiologyService.saveRadiologyOrder(radiologyOrder);
		
		assertNotNull(radiologyOrder);
		assertNotNull(radiologyOrder.getOrderId());
	}
	
	/**
	 * Convenience method to get a RadiologyOrder object with all required values filled in but
	 * which is not yet saved in the database
	 * 
	 * @return RadiologyOrder object that can be saved to the database
	 */
	public RadiologyOrder getUnsavedRadiologyOrder() {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setPatient(patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER));
		radiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		radiologyOrder.setStartDate(calendar.getTime());
		
		Concept conceptFracture = conceptService.getConcept(CONCEPT_ID_FOR_FRACTURE);
		radiologyOrder.setConcept(conceptFracture);
		
		radiologyOrder.setOrderType(RadiologyProperties.getRadiologyTestOrderType());
		
		return radiologyOrder;
	}
	
	/**
	 * @see RadiologyService#saveRadiologyOrder(RadiologyOrder)
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null", method = "saveRadiologyOrder(RadiologyOrder)")
	public void saveRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenNull() {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder is required");
		radiologyService.saveRadiologyOrder(null);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrderByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should return radiology order matching order id", method = "getRadiologyOrderByOrderId(Integer)")
	public void getRadiologyOrderByOrderId_shouldReturnRadiologyOrderMatchingOrderId() {
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
		
		assertNotNull(radiologyOrder);
		assertThat(radiologyOrder.getOrderId(), is(EXISTING_RADIOLOGY_ORDER_ID));
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrderByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should return null if no match was found", method = "getRadiologyOrderByOrderId(Integer)")
	public void getRadiologyOrderByOrderId_shouldReturnNullIfNoMatchIsFound() {
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(NON_EXISTING_RADIOLOGY_ORDER_ID);
		
		assertNull(radiologyOrder);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrderByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null", method = "getRadiologyOrderByOrderId(Integer)")
	public void getRadiologyOrderByOrderId_shouldThrowIllegalArgumentExceptionGivenNull() {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orderId is required");
		radiologyService.getRadiologyOrderByOrderId(null);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatient(Patient)
	 */
	@Test
	@Verifies(value = "should return all radiology orders associated with given patient", method = "getRadiologyOrdersByPatient(Patient)")
	public void getRadiologyOrdersByPatient_shouldReturnAllRadiologyOrdersAssociatedWithGivenPatient() {
		
		Patient patientWithTwoRadiologyOrders = patientService
		        .getPatient(PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER);
		
		List<RadiologyOrder> radiologyOrders = radiologyService.getRadiologyOrdersByPatient(patientWithTwoRadiologyOrders);
		
		assertThat(radiologyOrders.size(), is(2));
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatient(Patient)
	 */
	@Test
	@Verifies(value = "should return empty list given patient without associated radiology orders", method = "getRadiologyOrdersByPatient(Patient)")
	public void getRadiologyOrdersByPatient_shouldReturnEmptyListGivenPatientWithoutAssociatedRadiologyOrders() {
		
		Patient patientWithoutRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
		
		List<RadiologyOrder> radiologyOrders = radiologyService.getRadiologyOrdersByPatient(patientWithoutRadiologyOrders);
		
		assertThat(radiologyOrders.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatient(Patient)
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null", method = "getRadiologyOrdersByPatient(Patient)")
	public void getRadiologyOrdersByPatient_shouldThrowIllegalArgumentExceptionGivenNull() {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("patient is required");
		radiologyService.getRadiologyOrdersByPatient(null);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatients(List<Patient>)
	 */
	@Test
	@Verifies(value = "should return all radiology orders associated with given patients", method = "getRadiologyOrdersByPatients(List<Patient>)")
	public void getRadiologyOrdersByPatients_shouldReturnAllRadiologyOrdersAssociatedWithGivenPatients() {
		
		Patient patientWithTwoRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_TWO_RADIOLOGY_ORDERS);
		Patient patientWithOneRadiologyOrder = patientService.getPatient(PATIENT_ID_WITH_ONE_RADIOLOGY_ORDER);
		List<Patient> patientsWithThreeRadiologyOrders = new ArrayList<Patient>();
		patientsWithThreeRadiologyOrders.add(patientWithTwoRadiologyOrders);
		patientsWithThreeRadiologyOrders.add(patientWithOneRadiologyOrder);
		
		List<RadiologyOrder> radiologyOrders = radiologyService
		        .getRadiologyOrdersByPatients(patientsWithThreeRadiologyOrders);
		
		assertThat(radiologyOrders.size(), is(3));
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatients(List<Patient>)
	 */
	@Test
	@Verifies(value = "should return all radiology orders given empty patient list", method = "getRadiologyOrdersByPatients(List<Patient>)")
	public void getRadiologyOrdersByPatients_shouldReturnAllRadiologyOrdersGivenEmptyPatientList() {
		
		List<Patient> emptyPatientList = new ArrayList<Patient>();
		
		List<RadiologyOrder> radiologyOrders = radiologyService.getRadiologyOrdersByPatients(emptyPatientList);
		
		assertThat(radiologyOrders.size(), is(TOTAL_NUMBER_OF_RADIOLOGY_ORDERS));
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatients(List<Patient>)
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null", method = "getRadiologyOrdersByPatients(List<Patient>)")
	public void getRadiologyOrdersByPatients_shouldThrowIllegalArgumentExceptionGivenNull() {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("patients is required");
		radiologyService.getRadiologyOrdersByPatients(null);
	}
	
	/**
	 * @see RadiologyService#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should create new study from given study object", method = "saveStudy(Study)")
	public void saveStudy_shouldCreateNewStudyFromGivenStudyObject() throws Exception {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_ID_WITHOUT_STUDY);
		Study radiologyStudy = getUnsavedStudy();
		radiologyStudy.setOrderId(radiologyOrder.getOrderId());
		
		radiologyService.saveStudy(radiologyStudy);
		
		Study createdStudy = radiologyService.getStudy(radiologyStudy.getStudyId());
		assertNotNull(createdStudy);
		assertThat(createdStudy, is(radiologyStudy));
		assertThat(createdStudy.getStudyId(), is(radiologyStudy.getStudyId()));
		assertNotNull(createdStudy.getStudyInstanceUid());
		assertThat(createdStudy.getStudyInstanceUid(), is(RadiologyProperties.getStudyPrefix() + createdStudy.getStudyId()));
		assertThat(createdStudy.getModality(), is(radiologyStudy.getModality()));
		assertThat(createdStudy.getOrderId(), is(radiologyStudy.getOrderId()));
	}
	
	/**
	 * Convenience method to get a Study object with all required values filled in but which is not
	 * yet saved in the database
	 * 
	 * @return Study object that can be saved to the database
	 */
	public Study getUnsavedStudy() {
		
		Study study = new Study();
		study.setOrderId(radiologyService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_ID_WITHOUT_STUDY).getOrderId());
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
	@Verifies(value = "should update existing study", method = "saveStudy(Study)")
	public void saveStudy_shouldUpdateExistingStudy() throws Exception {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		Study existingStudy = radiologyService.getStudy(EXISTING_STUDY_ID);
		Modality modalityPreUpdate = existingStudy.getModality();
		Modality modalityPostUpdate = Modality.XA;
		existingStudy.setModality(modalityPostUpdate);
		
		Study updatedStudy = radiologyService.saveStudy(existingStudy);
		
		updatedStudy = radiologyService.getStudy(updatedStudy.getStudyId());
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
	public void saveStudy_shouldThrowIllegalArgumentExceptionIfStudyIsNull() throws Exception {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
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
	public void saveStudy_shouldThrowAPIExceptionGivenStudyWithEmptyOrderId() throws Exception {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		Study radiologyStudy = getUnsavedStudy();
		radiologyStudy.setOrderId(null);
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Study.order.required");
		radiologyService.saveStudy(radiologyStudy);
	}
	
	/**
	 * @see RadiologyService#saveStudy(Study)
	 */
	@Test
	@Verifies(value = "should throw APIException given study with empty modality", method = "saveStudy(Study)")
	public void saveStudy_shouldThrowAPIExceptionGivenStudyWithEmptyModality() throws Exception {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_ID_WITHOUT_STUDY);
		Study radiologyStudy = getUnsavedStudy();
		radiologyStudy.setOrderId(radiologyOrder.getOrderId());
		radiologyStudy.setModality(null);
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Study.modality.required");
		radiologyService.saveStudy(radiologyStudy);
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 */
	@Test
	@Verifies(value = "should return study matching study instance uid", method = "getStudyByStudyInstanceUid(String)")
	public void getStudyByStudyInstanceUid_shouldReturnStudyMatchingUid() throws Exception {
		Study study = radiologyService.getStudyByStudyInstanceUid(EXISTING_STUDY_INSTANCE_UID);
		
		assertNotNull(study);
		assertThat(study.getStudyInstanceUid(), is(EXISTING_STUDY_INSTANCE_UID));
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
	 * @see RadiologyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 */
	@Test
	@Verifies(value = "should fetch all studies for given radiology orders", method = "getStudiesByRadiologyOrders(List<RadiologyOrder>)")
	public void getStudiesByRadiologyOrders_shouldFetchAllStudiesForGivenRadiologyOrders() throws Exception {
		
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER);
		List<RadiologyOrder> radiologyOrders = radiologyService.getRadiologyOrdersByPatient(patient);
		
		List<Study> studies = radiologyService.getStudiesByRadiologyOrders(radiologyOrders);
		
		assertThat(studies.size(), is(radiologyOrders.size()));
		assertThat(studies.get(0).getOrderId(), is(radiologyOrders.get(0).getOrderId()));
		assertThat(studies.get(1).getOrderId(), is(radiologyOrders.get(1).getOrderId()));
	}
	
	/**
	 * @see RadiologyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 */
	@Test
	@Verifies(value = "should return empty list given radiology orders without associated studies", method = "getStudiesByRadiologyOrders(List<RadiologyOrder>)")
	public void getStudiesByRadiologyOrders_shouldReturnEmptyListGivenRadiologyOrdersWithoutAssociatedStudies()
	        throws Exception {
		
		RadiologyOrder radiologyOrderWithoutStudy = radiologyService
		        .getRadiologyOrderByOrderId(RADIOLOGY_ORDER_ID_WITHOUT_STUDY);
		List<RadiologyOrder> radiologyOrders = Arrays.asList(radiologyOrderWithoutStudy);
		
		List<Study> studies = radiologyService.getStudiesByRadiologyOrders(radiologyOrders);
		
		assertThat(radiologyOrders.size(), is(1));
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 */
	@Test
	@Verifies(value = "should return empty list given empty radiology order list", method = "getStudiesByRadiologyOrders(List<RadiologyOrder>)")
	public void getStudiesByRadiologyOrders_shouldReturnEmptyListGivenEmptyRadiologyOrderList() throws Exception {
		
		List<RadiologyOrder> orders = new ArrayList<RadiologyOrder>();
		
		List<Study> studies = radiologyService.getStudiesByRadiologyOrders(orders);
		
		assertThat(orders.size(), is(0));
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException given null", method = "getStudiesByRadiologyOrders(List<RadiologyOrder>)")
	public void getStudiesByRadiologyOrders_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrders are required");
		radiologyService.getStudiesByRadiologyOrders(null);
	}
	
	/**
	 * @see RadiologyService#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should fetch all obs for given orderId", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldFetchAllObsForGivenOrderId() throws Exception {
		List<Obs> obs = radiologyService.getObsByOrderId(RADIOLOGY_ORDER_ID_WITH_ONE_OBS);
		
		assertThat(obs.size(), is(1));
		assertThat(obs.get(0).getOrder().getOrderId(), is(RADIOLOGY_ORDER_ID_WITH_ONE_OBS));
	}
	
	/**
	 * @see RadiologyService#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should return empty list given orderId without associated obs", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldReturnEmptyListGivenOrderIdWithoutAssociatedObs() throws Exception {
		List<Obs> obs = radiologyService.getObsByOrderId(RADIOLOGY_ORDER_ID_WITHOUT_OBS);
		
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
