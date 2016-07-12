/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.radiology.order;

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
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.dicom.code.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.study.RadiologyStudy;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests {@link RadiologyOrderService}
 */
public class RadiologyOrderServiceComponentTest extends BaseModuleContextSensitiveTest {
    
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/RadiologyOrderServiceComponentTestDataset.xml";
    
    private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER = 70011;
    
    private static final int PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER = 70021;
    
    private static final int PATIENT_ID_WITH_TWO_RADIOLOGY_ORDERS = 70021;
    
    private static final int PATIENT_ID_WITH_FIVE_RADIOLOGY_ORDERS = 70022;
    
    private static final int EXISTING_RADIOLOGY_ORDER_ID = 2001;
    
    private static final String EXISTING_RADIOLOGY_ORDER_UUID = "44f24d7e-ebbd-4500-bfba-1db19561ca04";
    
    private static final int NON_EXISTING_RADIOLOGY_ORDER_ID = 99999;
    
    private static final String NON_EXISTING_RADIOLOGY_ORDER_UUID = "99999999-ebbd-4500-bfba-1db19561ca04";
    
    private static final int CONCEPT_ID_FOR_FRACTURE = 178;
    
    private static final int TOTAL_NUMBER_OF_RADIOLOGY_ORDERS = 3;
    
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
    private RadiologyOrderService radiologyOrderService;
    
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
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies create new radiology order and study from given radiology order object
     */
    @Test
    public void placeRadiologyOrder_shouldCreateNewRadiologyOrderAndStudyGivenRadiologyOrderObject() throws Exception {
        
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        
        radiologyOrder = radiologyOrderService.placeRadiologyOrder(radiologyOrder);
        
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
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setModality(Modality.CT);
        radiologyStudy.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
        radiologyOrder.setStudy(radiologyStudy);
        
        return radiologyOrder;
    }
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies create radiology order encounter
     */
    @Test
    public void placeRadiologyOrder_shouldCreateRadiologyOrderEncounter() throws Exception {
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        
        EncounterSearchCriteriaBuilder encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setPatient(radiologyOrder.getPatient())
                .setProviders(Arrays.asList(radiologyOrder.getOrderer()));
        List<Encounter> matchingEncounters =
                encounterService.getEncounters(encounterSearchCriteria.createEncounterSearchCriteria());
        assertThat(matchingEncounters, is(empty()));
        assertThat(radiologyOrder.getEncounter(), is(nullValue()));
        
        radiologyOrder = radiologyOrderService.placeRadiologyOrder(radiologyOrder);
        
        assertThat(radiologyOrder.getEncounter(), is(not(nullValue())));
        matchingEncounters = encounterService.getEncounters(encounterSearchCriteria.createEncounterSearchCriteria());
        assertThat(matchingEncounters, hasItem(radiologyOrder.getEncounter()));
        assertThat(matchingEncounters.size(), is(1));
    }
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies throw illegal argument exception given null
     */
    @Test
    public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyOrderService.placeRadiologyOrder(null);
    }
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies throw illegal argument exception given existing radiology order
     */
    @Test
    public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenExistingRadiologyOrder() throws Exception {
        
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        
        radiologyOrder = radiologyOrderService.placeRadiologyOrder(radiologyOrder);
        
        assertNotNull(radiologyOrder);
        assertNotNull(radiologyOrder.getOrderId());
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot edit an existing RadiologyOrder");
        radiologyOrderService.placeRadiologyOrder(radiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies throw illegal argument exception if given radiology order has no study
     */
    @Test
    public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderHasNoStudy() throws Exception {
        
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        radiologyOrder.setStudy(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder.study cannot be null");
        radiologyOrderService.placeRadiologyOrder(radiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies throw illegal argument exception if given study modality is null
     */
    @Test
    public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenStudyModalityIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        radiologyOrder.getStudy()
                .setModality(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder.study.modality cannot be null");
        radiologyOrderService.placeRadiologyOrder(radiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder,Provider,String)
     * @verifies create discontinuation order which discontinues given radiology order that is not
     *           in progress or completed
     */
    @Test
    public void
            discontinueRadiologyOrder_shouldCreateDiscontinuationOrderWhichDiscontinuesGivenRadiologyOrderThatIsNotInProgressOrCompleted()
                    throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(null);
        String discontinueReason = "Wrong Procedure";
        
        Order discontinuationOrder = radiologyOrderService.discontinueRadiologyOrder(radiologyOrder,
            radiologyOrder.getOrderer(), discontinueReason);
        
        assertNotNull(discontinuationOrder);
        assertThat(discontinuationOrder.getAction(), is(Order.Action.DISCONTINUE));
        
        assertThat(discontinuationOrder.getPreviousOrder(), is((Order) radiologyOrder));
        assertThat(radiologyOrder.isActive(), is(false));
    }
    
    /**
    * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder,Provider,String)
    * @verifies create radiology order encounter
    */
    @Test
    public void discontinueRadiologyOrder_shouldCreateRadiologyOrderEncounter() throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(null);
        String discontinueReason = "Wrong Procedure";
        EncounterSearchCriteriaBuilder encounterSearchCriteria =
                new EncounterSearchCriteriaBuilder().setPatient(radiologyOrder.getPatient())
                        .setProviders(Arrays.asList(radiologyOrder.getOrderer()));
        List<Encounter> matchingEncounters =
                encounterService.getEncounters(encounterSearchCriteria.createEncounterSearchCriteria());
        assertThat(matchingEncounters, hasItem(radiologyOrder.getEncounter()));
        assertThat(matchingEncounters.size(), is(1));
        
        Order discontinuationOrder = radiologyOrderService.discontinueRadiologyOrder(radiologyOrder,
            radiologyOrder.getOrderer(), discontinueReason);
        
        assertThat(discontinuationOrder.getEncounter(), is(not(nullValue())));
        matchingEncounters = encounterService.getEncounters(encounterSearchCriteria.createEncounterSearchCriteria());
        assertThat(matchingEncounters, hasItem(radiologyOrder.getEncounter()));
        assertThat(matchingEncounters, hasItem(discontinuationOrder.getEncounter()));
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw illegal argument exception if given radiology order is null
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyOrderService.discontinueRadiologyOrder(null, radiologyOrder.getOrderer(), discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw illegal argument exception if given radiology order with orderId null
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderWithOrderIdNull()
            throws Exception {
        
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("can only discontinue existing RadiologyOrder. orderId is null");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder,Provider,String)
     * @verifies throw illegal argument exception if given radiology order is discontinued
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderIsDiscontinued()
            throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(null);
        String discontinueReason = "Wrong Procedure";
        
        Order discontinuationOrder = radiologyOrderService.discontinueRadiologyOrder(radiologyOrder,
            radiologyOrder.getOrderer(), discontinueReason);
        
        assertNotNull(discontinuationOrder);
        assertThat(discontinuationOrder.getAction(), is(Order.Action.DISCONTINUE));
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder is already discontinued");
        radiologyOrderService.discontinueRadiologyOrder((RadiologyOrder) discontinuationOrder.getPreviousOrder(),
            radiologyOrder.getOrderer(), discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw illegal argument exception if given radiology order is in progress
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderIsInProgress()
            throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder is in progress");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw illegal argument exception if given radiology order is completed
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderIsCompleted()
            throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder is completed");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw illegal argument exception if given provider is null
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenProviderIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(null);
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("provider cannot be null");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, null, discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrder(Integer)
     * @verifies return radiology order matching given order id
     */
    @Test
    public void getRadiologyOrder_shouldReturnRadiologyOrderMatchingGivenOrderId() {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        
        assertNotNull(radiologyOrder);
        assertThat(radiologyOrder.getOrderId(), is(EXISTING_RADIOLOGY_ORDER_ID));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrder(Integer)
     * @verifies return null if no match was found
     */
    @Test
    public void getRadiologyOrder_shouldReturnNullIfNoMatchIsFound() {
        
        assertNull(radiologyOrderService.getRadiologyOrder(NON_EXISTING_RADIOLOGY_ORDER_ID));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrder(Integer)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("orderId cannot be null");
        radiologyOrderService.getRadiologyOrder(null);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrderByUuid(String)
     * @verifies return radiology order matching given uuid
     */
    @Test
    public void getRadiologyOrderByUuid_shouldReturnRadiologyOrderMatchingGivenUuid() throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByUuid(EXISTING_RADIOLOGY_ORDER_UUID);
        
        assertNotNull(radiologyOrder);
        assertThat(radiologyOrder.getUuid(), is(EXISTING_RADIOLOGY_ORDER_UUID));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrderByUuid(String)
     * @verifies return null if no match was found
     */
    @Test
    public void getRadiologyOrderByUuid_shouldReturnNullIfNoMatchIsFound() throws Exception {
        
        assertNull(radiologyOrderService.getRadiologyOrderByUuid(NON_EXISTING_RADIOLOGY_ORDER_UUID));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrderByUuid(String)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyOrderByUuid_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("uuid cannot be null");
        radiologyOrderService.getRadiologyOrderByUuid(null);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrdersByPatient(Patient)
     * @verifies should return all radiology orders associated with given patient
     */
    @Test
    public void getRadiologyOrdersByPatient_shouldReturnAllRadiologyOrdersAssociatedWithGivenPatient() {
        
        Patient patientWithTwoRadiologyOrders =
                patientService.getPatient(PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER);
        
        List<RadiologyOrder> radiologyOrders =
                radiologyOrderService.getRadiologyOrdersByPatient(patientWithTwoRadiologyOrders);
        
        assertThat(radiologyOrders.size(), is(2));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrdersByPatient(Patient)
     * @verifies should return empty list given patient without associated radiology orders
     */
    @Test
    public void getRadiologyOrdersByPatient_shouldReturnEmptyListGivenPatientWithoutAssociatedRadiologyOrders() {
        
        Patient patientWithoutRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
        
        List<RadiologyOrder> radiologyOrders =
                radiologyOrderService.getRadiologyOrdersByPatient(patientWithoutRadiologyOrders);
        
        assertThat(radiologyOrders.size(), is(0));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrdersByPatient(Patient)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyOrdersByPatient_shouldThrowIllegalArgumentExceptionIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("patient cannot be null");
        radiologyOrderService.getRadiologyOrdersByPatient(null);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrdersByPatients(List<Patient>)
     * @verifies should return all radiology orders associated with given patients
     */
    @Test
    public void getRadiologyOrdersByPatients_shouldReturnAllRadiologyOrdersAssociatedWithGivenPatients() {
        
        Patient patientWithTwoRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_TWO_RADIOLOGY_ORDERS);
        assertThat(orderService.getAllOrdersByPatient(patientWithTwoRadiologyOrders)
                .size(),
            is(2));
        Patient patientWithFiveRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_FIVE_RADIOLOGY_ORDERS);
        assertThat(orderService.getAllOrdersByPatient(patientWithFiveRadiologyOrders)
                .size(),
            is(1));
        List<Patient> allPatientsWithRadiologyOrders = new ArrayList<Patient>();
        allPatientsWithRadiologyOrders.add(patientWithTwoRadiologyOrders);
        allPatientsWithRadiologyOrders.add(patientWithFiveRadiologyOrders);
        
        List<RadiologyOrder> radiologyOrders =
                radiologyOrderService.getRadiologyOrdersByPatients(allPatientsWithRadiologyOrders);
        
        assertThat(radiologyOrders.size(), is(TOTAL_NUMBER_OF_RADIOLOGY_ORDERS));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrdersByPatients(List<Patient>)
     * @verifies should return all radiology orders given empty patient list
     */
    @Test
    public void getRadiologyOrdersByPatients_shouldReturnAllRadiologyOrdersGivenEmptyPatientList() {
        
        List<Patient> emptyPatientList = new ArrayList<Patient>();
        
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrdersByPatients(emptyPatientList);
        
        assertThat(radiologyOrders.size(), is(TOTAL_NUMBER_OF_RADIOLOGY_ORDERS));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrdersByPatients(List<Patient>)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyOrdersByPatients_shouldThrowIllegalArgumentExceptionIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("patients cannot be null");
        radiologyOrderService.getRadiologyOrdersByPatients(null);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrdersByPatients(List)
     * @verifies return empty list given patient list without associated radiology orders
     */
    @Test
    public void getRadiologyOrdersByPatients_shouldReturnEmptyListGivenPatientListWithoutAssociatedRadiologyOrders()
            throws Exception {
        
        Patient patientWithoutRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
        
        List<RadiologyOrder> radiologyOrders =
                radiologyOrderService.getRadiologyOrdersByPatients(Arrays.asList(patientWithoutRadiologyOrders));
        
        assertThat(radiologyOrders.size(), is(0));
    }
}
