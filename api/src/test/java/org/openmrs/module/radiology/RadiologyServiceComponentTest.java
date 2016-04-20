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

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hibernate.cfg.Environment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests {@link RadiologyService}
 */
public class RadiologyServiceComponentTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceComponentTestDataset.xml";
	
	private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER = 70011;
	
	private static final int PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER = 70021;
	
	private static final int PATIENT_ID_WITH_TWO_RADIOLOGY_ORDERS = 70021;
	
	private static final int PATIENT_ID_WITH_FIVE_RADIOLOGY_ORDERS = 70022;
	
	private static final int PATIENT_ID_WITH_ONE_RADIOLOGY_ORDER_AND_ACTIVE_VISIT = 70055;
	
	private static final int PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT = 70033;
	
	private static final int RADIOLOGY_ORDER_ID_WITHOUT_STUDY = 2004;
	
	private static final int EXISTING_RADIOLOGY_ORDER_ID = 2001;
	
	private static final int NON_EXISTING_RADIOLOGY_ORDER_ID = 99999;
	
	private static final String EXISTING_STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.2186.1.1";
	
	private static final String NON_EXISTING_STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.2186.1.9999";
	
	private static final int EXISTING_STUDY_ID = 1;
	
	private static final int NON_EXISTING_STUDY_ID = 99999;
	
	private static final int CONCEPT_ID_FOR_FRACTURE = 178;
	
	private static final int TOTAL_NUMBER_OF_RADIOLOGY_ORDERS = 4;
	
	private static final int RADIOLOGY_ORDER_WITH_ENCOUNTER_AND_ACTIVE_VISIT = 2009;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService administrationService;
	
	@Autowired
	private EncounterService encounterService;
	
	@Autowired
	private ProviderService providerService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private RadiologyService radiologyService;
	
	@Autowired
	private VisitService visitService;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * Overriding following method is necessary to enable MVCC which is disabled by default in DB h2
	 * used for the component tests. This prevents following exception:
	 * org.hibernate.exception.GenericJDBCException: could not load an entity:
	 * [org.openmrs.GlobalProperty#order.nextOrderNumberSeed] due to "Timeout trying to lock table "
	 * GLOBAL_PROPERTY"; SQL statement:" which occurs in all tests touching methods that call
	 * orderService.saveOrder()
	 */
	@Override
	public Properties getRuntimeProperties() {
		Properties result = super.getRuntimeProperties();
		String url = result.getProperty(Environment.URL);
		if (url.contains("jdbc:h2:") && !url.contains(";MVCC=TRUE")) {
			result.setProperty(Environment.URL, url + ";MVCC=TRUE");
		}
		return result;
	}
	
	@Before
	public void runBeforeAllTests() throws Exception {
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrder(RadiologyOrder)
	 * @verifies create new radiology order and study from given radiology order object
	 */
	@Test
	public void placeRadiologyOrder_shouldCreateNewRadiologyOrderAndStudyGivenRadiologyOrderObject() throws Exception {
		
		RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
		
		radiologyOrder = radiologyService.placeRadiologyOrder(radiologyOrder);
		
		assertNotNull(radiologyOrder);
		assertNotNull(radiologyOrder.getOrderId());
		assertNotNull(radiologyOrder.getStudy());
		assertNotNull(radiologyOrder.getStudy()
				.getStudyId());
		assertNotNull(radiologyOrder.getEncounter());
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
		radiologyOrder.setOrderer(providerService.getProviderByIdentifier("1"));
		radiologyOrder.setConcept(conceptService.getConcept(CONCEPT_ID_FOR_FRACTURE));
		radiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		radiologyOrder.setScheduledDate(calendar.getTime());
		radiologyOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
		
		Study study = new Study();
		study.setModality(Modality.CT);
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		radiologyOrder.setStudy(study);
		
		return radiologyOrder;
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrder(RadiologyOrder)
	 * @verifies create radiology order encounter with orderer and attached to existing active visit if patient has active
	 *           visit
	 */
	@Test
	public void placeRadiologyOrder_shouldCreateRadiologyOrderEncounterWithOrdererAndAttachedToExistingActiveVisitIfPatientHasActiveVisit()
			throws Exception {
		
		RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
		radiologyOrder.setPatient(patientService.getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT));
		
		assertThat(encounterService.getEncountersByPatient(radiologyOrder.getPatient()), is(empty()));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient()), is(not(empty())));
		Visit preExistingVisit = visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.get(0);
		
		assertThat(radiologyOrder.getEncounter(), is(nullValue()));
		
		radiologyOrder = radiologyService.placeRadiologyOrder(radiologyOrder);
		
		Encounter encounter = radiologyOrder.getEncounter();
		assertThat(encounter, is(not(nullValue())));
		assertThat(encounter.getEncounterProviders()
				.size(), is(1));
		assertThat(encounter.getEncounterProviders()
				.iterator()
				.next()
				.getProvider(), is(radiologyOrder.getOrderer()));
		assertThat(encounterService.getEncountersByPatient(radiologyOrder.getPatient())
				.size(), is(1));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.size(), is(1));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.get(0), is(preExistingVisit));
		assertThat(orderService.getAllOrdersByPatient(radiologyOrder.getPatient())
				.size(), is(1));
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrder(RadiologyOrder)
	 * @verifies create radiology order encounter with orderer attached to new active visit if patient without active visit
	 */
	@Test
	public void placeRadiologyOrder_shouldCreateRadiologyOrderEncounterWithOrdererAttachedToNewActiveVisitIfPatientWithoutActiveVisit()
			throws Exception {
		RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
		
		assertThat(encounterService.getEncountersByPatient(radiologyOrder.getPatient()), is(empty()));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient()), is(empty()));
		assertThat(radiologyOrder.getEncounter(), is(nullValue()));
		
		radiologyOrder = radiologyService.placeRadiologyOrder(radiologyOrder);
		
		Encounter encounter = radiologyOrder.getEncounter();
		assertThat(encounter, is(not(nullValue())));
		assertThat(encounter.getEncounterProviders()
				.size(), is(1));
		assertThat(encounter.getEncounterProviders()
				.iterator()
				.next()
				.getProvider(), is(radiologyOrder.getOrderer()));
		assertThat(encounterService.getEncountersByPatient(radiologyOrder.getPatient())
				.size(), is(1));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.size(), is(1));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.get(0)
				.getEncounters(), hasItem(encounter));
		assertThat(orderService.getAllOrdersByPatient(radiologyOrder.getPatient())
				.size(), is(1));
		
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrder(RadiologyOrder)
	 * @verifies throw illegal argument exception given null
	 */
	@Test
	public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder is required");
		radiologyService.placeRadiologyOrder(null);
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrder(RadiologyOrder)
	 * @verifies throw illegal argument exception given existing radiology order
	 */
	@Test
	public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenExistingRadiologyOrder() throws Exception {
		
		RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
		
		radiologyOrder = radiologyService.placeRadiologyOrder(radiologyOrder);
		
		assertNotNull(radiologyOrder);
		assertNotNull(radiologyOrder.getOrderId());
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Cannot edit an existing order!");
		radiologyService.placeRadiologyOrder(radiologyOrder);
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrder(RadiologyOrder)
	 * @verifies throw illegal argument exception if given radiology order has no study
	 */
	@Test
	public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderHasNoStudy() throws Exception {
		
		RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
		radiologyOrder.setStudy(null);
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder.study is required");
		radiologyService.placeRadiologyOrder(radiologyOrder);
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrder(RadiologyOrder)
	 * @verifies throw illegal argument exception if given study modality is null
	 */
	@Test
	public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenStudyModalityIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
		radiologyOrder.getStudy()
				.setModality(null);
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder.study.modality is required");
		radiologyService.placeRadiologyOrder(radiologyOrder);
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder,Provider,String)
	 * @verifies create discontinuation order which discontinues given radiology order that is not
	 *           in progress or completed
	 */
	@Test
	public void discontinueRadiologyOrder_shouldCreateDiscontinuationOrderWhichDiscontinuesGivenRadiologyOrderThatIsNotInProgressOrCompleted()
			throws Exception {
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
		radiologyOrder.getStudy()
				.setPerformedStatus(null);
		String discontinueReason = "Wrong Procedure";
		
		Order discontinuationOrder = radiologyService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(),
			discontinueReason);
		
		assertNotNull(discontinuationOrder);
		assertThat(discontinuationOrder.getAction(), is(Order.Action.DISCONTINUE));
		
		assertThat(discontinuationOrder.getPreviousOrder(), is((Order) radiologyOrder));
		assertThat(radiologyOrder.isActive(), is(false));
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder,Provider,String)
	 * @verifies create discontinuation order with encounter attached to existing active visit if patient has active visit
	 */
	@Test
	public void discontinueRadiologyOrder_shouldCreateDiscontinuationOrderWithEncounterAttachedToExistingActiveVisitIfPatientHasActiveVisit()
			throws Exception {
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_ONE_RADIOLOGY_ORDER_AND_ACTIVE_VISIT);
		
		assertThat(visitService.getActiveVisitsByPatient(patient), is(not(empty())));
		assertThat(visitService.getActiveVisitsByPatient(patient)
				.get(0)
				.getEncounters(), is(not(empty())));
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_ENCOUNTER_AND_ACTIVE_VISIT);
		
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.size(), is(1));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.get(0)
				.getEncounters()
				.size(), is(1));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.get(0)
				.getEncounters(), hasItem(radiologyOrder.getEncounter()));
		
		String discontinueReason = "Wrong Procedure";
		
		assertThat(radiologyOrder.getPatient()
				.getUuid(), is(not(nullValue())));
		Order discontinuationOrder = radiologyService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(),
			discontinueReason);
		
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.size(), is(1));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.get(0)
				.getEncounters()
				.size(), is(2));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.get(0)
				.getEncounters(), hasItem(radiologyOrder.getEncounter()));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.get(0)
				.getEncounters(), hasItem(discontinuationOrder.getEncounter()));
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder,Provider,String)
	 * @verifies create discontinuation order with encounter attached to new active visit if patient without active visit
	 */
	@Test
	public void discontinueRadiologyOrder_shouldCreateDiscontinuationOrderWithEncounterAttachedToNewActiveVisitIfPatientWithoutActiveVisit()
			throws Exception {
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
		
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient()), is(empty()));
		
		radiologyOrder.getStudy()
				.setPerformedStatus(null);
		String discontinueReason = "Wrong Procedure";
		
		Order discontinuationOrder = radiologyService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(),
			discontinueReason);
		
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.size(), is(1));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.get(0)
				.getEncounters()
				.size(), is(1));
		assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
				.get(0)
				.getEncounters(), hasItem(discontinuationOrder.getEncounter()));
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
	 * @verifies should throw illegal argument exception given empty radiology order
	 */
	@Test
	public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenEmptyRadiologyOrder() throws Exception {
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
		String discontinueReason = "Wrong Procedure";
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder is required");
		radiologyService.discontinueRadiologyOrder(null, radiologyOrder.getOrderer(), discontinueReason);
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
	 * @verifies should throw illegal argument exception given radiology order with orderId null
	 */
	@Test
	public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenRadiologyOrderWithOrderIdNull()
			throws Exception {
		
		RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
		String discontinueReason = "Wrong Procedure";
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orderId is null");
		radiologyService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder, Provider, Date, String)
	 * @verifies should throw illegal argument exception if radiology order is not active
	 */
	@Test
	public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfRadiologyOrderIsNotActive() throws Exception {
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
		radiologyOrder.setAction(Order.Action.DISCONTINUE);
		String discontinueReason = "Wrong Procedure";
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("order is not active");
		radiologyService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
	 * @verifies throw illegal argument exception if radiology order is completed
	 */
	@Test
	public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfRadiologyOrderIsCompleted() throws Exception {
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
		radiologyOrder.getStudy()
				.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		String discontinueReason = "Wrong Procedure";
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder is in progress");
		radiologyService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
	 * @verifies throw illegal argument exception if radiology order is in progress
	 */
	@Test
	public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfRadiologyOrderIsInProgress() throws Exception {
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
		radiologyOrder.getStudy()
				.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		String discontinueReason = "Wrong Procedure";
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder is completed");
		radiologyService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
	 * @verifies should throw illegal argument exception given empty provider
	 */
	@Test
	public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenEmptyProvider() throws Exception {
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
		radiologyOrder.getStudy()
				.setPerformedStatus(null);
		String discontinueReason = "Wrong Procedure";
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("provider is required");
		radiologyService.discontinueRadiologyOrder(radiologyOrder, null, discontinueReason);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrderByOrderId(Integer)
	 * @verifies should return radiology order matching order id
	 */
	@Test
	public void getRadiologyOrderByOrderId_shouldReturnRadiologyOrderMatchingOrderId() {
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
		
		assertNotNull(radiologyOrder);
		assertThat(radiologyOrder.getOrderId(), is(EXISTING_RADIOLOGY_ORDER_ID));
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrderByOrderId(Integer)
	 * @verifies should return null if no match was found
	 */
	@Test
	public void getRadiologyOrderByOrderId_shouldReturnNullIfNoMatchIsFound() {
		
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(NON_EXISTING_RADIOLOGY_ORDER_ID);
		
		assertNull(radiologyOrder);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrderByOrderId(Integer)
	 * @verifies should throw illegal argument exception given null
	 */
	@Test
	public void getRadiologyOrderByOrderId_shouldThrowIllegalArgumentExceptionGivenNull() {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orderId is required");
		radiologyService.getRadiologyOrderByOrderId(null);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatient(Patient)
	 * @verifies should return all radiology orders associated with given patient
	 */
	@Test
	public void getRadiologyOrdersByPatient_shouldReturnAllRadiologyOrdersAssociatedWithGivenPatient() {
		
		Patient patientWithTwoRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER);
		
		List<RadiologyOrder> radiologyOrders = radiologyService.getRadiologyOrdersByPatient(patientWithTwoRadiologyOrders);
		
		assertThat(radiologyOrders.size(), is(2));
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatient(Patient)
	 * @verifies should return empty list given patient without associated radiology orders
	 */
	@Test
	public void getRadiologyOrdersByPatient_shouldReturnEmptyListGivenPatientWithoutAssociatedRadiologyOrders() {
		
		Patient patientWithoutRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
		
		List<RadiologyOrder> radiologyOrders = radiologyService.getRadiologyOrdersByPatient(patientWithoutRadiologyOrders);
		
		assertThat(radiologyOrders.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatient(Patient)
	 * @verifies should throw illegal argument exception given null
	 */
	@Test
	public void getRadiologyOrdersByPatient_shouldThrowIllegalArgumentExceptionGivenNull() {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("patient is required");
		radiologyService.getRadiologyOrdersByPatient(null);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatients(List<Patient>)
	 * @verifies should return all radiology orders associated with given patients
	 */
	@Test
	public void getRadiologyOrdersByPatients_shouldReturnAllRadiologyOrdersAssociatedWithGivenPatients() {
		
		Patient patientWithTwoRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_TWO_RADIOLOGY_ORDERS);
		assertThat(orderService.getAllOrdersByPatient(patientWithTwoRadiologyOrders)
				.size(), is(2));
		Patient patientWithFiveRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_FIVE_RADIOLOGY_ORDERS);
		assertThat(orderService.getAllOrdersByPatient(patientWithFiveRadiologyOrders)
				.size(), is(1));
		Patient patientWithOneRadiologyOrder = patientService.getPatient(PATIENT_ID_WITH_ONE_RADIOLOGY_ORDER_AND_ACTIVE_VISIT);
		assertThat(orderService.getAllOrdersByPatient(patientWithOneRadiologyOrder)
				.size(), is(1));
		List<Patient> allPatientsWithRadiologyOrders = new ArrayList<Patient>();
		allPatientsWithRadiologyOrders.add(patientWithTwoRadiologyOrders);
		allPatientsWithRadiologyOrders.add(patientWithFiveRadiologyOrders);
		allPatientsWithRadiologyOrders.add(patientWithOneRadiologyOrder);
		
		List<RadiologyOrder> radiologyOrders = radiologyService.getRadiologyOrdersByPatients(allPatientsWithRadiologyOrders);
		
		assertThat(radiologyOrders.size(), is(TOTAL_NUMBER_OF_RADIOLOGY_ORDERS));
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatients(List<Patient>)
	 * @verifies should return all radiology orders given empty patient list
	 */
	@Test
	public void getRadiologyOrdersByPatients_shouldReturnAllRadiologyOrdersGivenEmptyPatientList() {
		
		List<Patient> emptyPatientList = new ArrayList<Patient>();
		
		List<RadiologyOrder> radiologyOrders = radiologyService.getRadiologyOrdersByPatients(emptyPatientList);
		
		assertThat(radiologyOrders.size(), is(TOTAL_NUMBER_OF_RADIOLOGY_ORDERS));
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatients(List<Patient>)
	 * @verifies should throw illegal argument exception given null
	 */
	@Test
	public void getRadiologyOrdersByPatients_shouldThrowIllegalArgumentExceptionGivenNull() {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("patients is required");
		radiologyService.getRadiologyOrdersByPatients(null);
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrderInPacs(RadiologyOrder)
	 * @verifies throw illegal argument exception given null
	 */
	@Test
	public void placeRadiologyOrderInPacs_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder is required");
		radiologyService.placeRadiologyOrderInPacs(null);
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrderInPacs(RadiologyOrder)
	 * @verifies throw illegal argument exception given radiology order with orderId null
	 */
	@Test
	public void placeRadiologyOrderInPacs_shouldThrowIllegalArgumentExceptionGivenRadiologyOrderWithOrderIdNull()
			throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder is not persisted");
		radiologyService.placeRadiologyOrderInPacs(new RadiologyOrder());
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrderInPacs(RadiologyOrder)
	 * @verifies throw illegal argument exception if given radiology order has no study
	 */
	@Test
	public void placeRadiologyOrderInPacs_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderHasNoStudy()
			throws Exception {
		
		RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
		radiologyOrder.setOrderId(1);
		radiologyOrder.setStudy(null);
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder.study is required");
		radiologyService.placeRadiologyOrderInPacs(radiologyOrder);
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrderInPacs(RadiologyOrder)
	 * @verifies throw illegal argument exception given null
	 */
	@Test
	public void discontinueRadiologyOrderInPacs_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder is required");
		radiologyService.discontinueRadiologyOrderInPacs(null);
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrderInPacs(RadiologyOrder)
	 * @verifies throw illegal argument exception given radiology order with orderId null
	 */
	@Test
	public void discontinueRadiologyOrderInPacs_shouldThrowIllegalArgumentExceptionGivenRadiologyOrderWithOrderIdNull()
			throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrder is not persisted");
		radiologyService.discontinueRadiologyOrderInPacs(new RadiologyOrder());
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyId(Integer)
	 * @verifies should return study for given study id
	 */
	@Test
	public void getStudyByStudyId_shouldReturnStudyForGivenStudyId() throws Exception {
		
		Study study = radiologyService.getStudyByStudyId(EXISTING_STUDY_ID);
		
		assertNotNull(study);
		assertThat(study.getRadiologyOrder()
				.getOrderId(), is(EXISTING_RADIOLOGY_ORDER_ID));
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyId(Integer)
	 * @verifies should return null if no match was found
	 */
	@Test
	public void getStudyByStudyId_shouldReturnNullIfNoMatchIsFound() throws Exception {
		
		Study study = radiologyService.getStudyByStudyId(NON_EXISTING_STUDY_ID);
		
		assertNull(study);
	}
	
	/**
	 * @see RadiologyService#getStudyByOrderId(Integer)
	 * @verifies should return study associated with radiology order for which order id is given
	 */
	@Test
	public void getStudyByOrderId_shouldReturnStudyMatching() throws Exception {
		
		Study study = radiologyService.getStudyByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
		
		assertNotNull(study);
		assertThat(study.getRadiologyOrder()
				.getOrderId(), is(EXISTING_RADIOLOGY_ORDER_ID));
	}
	
	/**
	 * @see RadiologyService#getStudyByOrderId(Integer)
	 * @verifies should return null if no match was found
	 */
	@Test
	public void getStudyByOrderId_shouldReturnNullIfNoMatchIsFound() {
		
		Study study = radiologyService.getStudyByOrderId(NON_EXISTING_RADIOLOGY_ORDER_ID);
		
		assertNull(study);
	}
	
	/**
	 * @see RadiologyService#getStudyByOrderId(Integer)
	 * @verifies should throw illegal argument exception given null
	 */
	@Test
	public void getStudyByOrderId_shouldThrowIllegalArgumentExceptionGivenNull() {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orderId is required");
		radiologyService.getStudyByOrderId(null);
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 * @verifies should return study matching study instance uid
	 */
	@Test
	public void getStudyByStudyInstanceUid_shouldReturnStudyMatchingUid() throws Exception {
		Study study = radiologyService.getStudyByStudyInstanceUid(EXISTING_STUDY_INSTANCE_UID);
		
		assertNotNull(study);
		assertThat(study.getStudyInstanceUid(), is(EXISTING_STUDY_INSTANCE_UID));
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 * @verifies should return null if no match was found
	 */
	@Test
	public void getStudyByStudyInstanceUid_shouldReturnNullIfNoMatchIsFound() throws Exception {
		Study study = radiologyService.getStudyByStudyInstanceUid(NON_EXISTING_STUDY_INSTANCE_UID);
		
		assertNull(study);
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 * @verifies should throw IllegalArgumentException if study instance uid is null
	 */
	@Test
	public void getStudyByStudyInstanceUid_shouldThrowIllegalArgumentExceptionIfUidIsNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("studyInstanceUid is required");
		radiologyService.getStudyByStudyInstanceUid(null);
	}
	
	/**
	 * @see RadiologyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 * @verifies should fetch all studies for given radiology orders
	 */
	@Test
	public void getStudiesByRadiologyOrders_shouldFetchAllStudiesForGivenRadiologyOrders() throws Exception {
		
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER);
		List<RadiologyOrder> radiologyOrders = radiologyService.getRadiologyOrdersByPatient(patient);
		
		List<Study> studies = radiologyService.getStudiesByRadiologyOrders(radiologyOrders);
		
		assertThat(studies.size(), is(radiologyOrders.size()));
		assertThat(studies.get(0)
				.getRadiologyOrder(), is(radiologyOrders.get(0)));
		assertThat(studies.get(1)
				.getRadiologyOrder(), is(radiologyOrders.get(1)));
	}
	
	/**
	 * @see RadiologyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 * @verifies should return empty list given radiology orders without associated studies
	 */
	@Test
	public void getStudiesByRadiologyOrders_shouldReturnEmptyListGivenRadiologyOrdersWithoutAssociatedStudies()
			throws Exception {
		
		RadiologyOrder radiologyOrderWithoutStudy = radiologyService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_ID_WITHOUT_STUDY);
		List<RadiologyOrder> radiologyOrders = Arrays.asList(radiologyOrderWithoutStudy);
		
		List<Study> studies = radiologyService.getStudiesByRadiologyOrders(radiologyOrders);
		
		assertThat(radiologyOrders.size(), is(1));
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 * @verifies should return empty list given empty radiology order list
	 */
	@Test
	public void getStudiesByRadiologyOrders_shouldReturnEmptyListGivenEmptyRadiologyOrderList() throws Exception {
		
		List<RadiologyOrder> orders = new ArrayList<RadiologyOrder>();
		
		List<Study> studies = radiologyService.getStudiesByRadiologyOrders(orders);
		
		assertThat(orders.size(), is(0));
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 * @verifies should throw IllegalArgumentException given null
	 */
	@Test
	public void getStudiesByRadiologyOrders_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("radiologyOrders are required");
		radiologyService.getStudiesByRadiologyOrders(null);
	}
	
	/**
	 * @see RadiologyService#updateStudyPerformedStatus(String,PerformedProcedureStepStatus)
	 * @verifies update performed status of study associated with given study instance uid
	 */
	@Test
	public void updateStudyPerformedStatus_shouldUpdatePerformedStatusOfStudyAssociatedWithGivenStudyInstanceUid()
			throws Exception {
		
		Study existingStudy = radiologyService.getStudyByStudyId(EXISTING_STUDY_ID);
		PerformedProcedureStepStatus performedStatusPreUpdate = existingStudy.getPerformedStatus();
		PerformedProcedureStepStatus performedStatusPostUpdate = PerformedProcedureStepStatus.COMPLETED;
		
		Study updatedStudy = radiologyService.updateStudyPerformedStatus(existingStudy.getStudyInstanceUid(),
			performedStatusPostUpdate);
		
		assertNotNull(updatedStudy);
		assertThat(updatedStudy, is(existingStudy));
		assertThat(performedStatusPreUpdate, is(not(performedStatusPostUpdate)));
		assertThat(updatedStudy.getPerformedStatus(), is(performedStatusPostUpdate));
	}
	
	/**
	 * @see RadiologyService#updateStudyPerformedStatus(String,PerformedProcedureStepStatus)
	 * @verifies throw illegal argument exception if study instance uid is null
	 */
	@Test
	public void updateStudyPerformedStatus_shouldThrowIllegalArgumentExceptionIfStudyInstanceUidIsNull() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("studyInstanceUid is required");
		radiologyService.updateStudyPerformedStatus(null, PerformedProcedureStepStatus.COMPLETED);
	}
	
	/**
	 * @see RadiologyService#updateStudyPerformedStatus(String,PerformedProcedureStepStatus)
	 * @verifies throw illegal argument exception if performed status is null
	 */
	@Test
	public void updateStudyPerformedStatus_shouldThrowIllegalArgumentExceptionIfPerformedStatusIsNull() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("performedStatus is required");
		radiologyService.updateStudyPerformedStatus(EXISTING_STUDY_INSTANCE_UID, null);
	}
}
