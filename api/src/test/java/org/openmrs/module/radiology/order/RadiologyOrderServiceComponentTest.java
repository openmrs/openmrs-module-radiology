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
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.dicom.code.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.study.RadiologyStudy;
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
    
    private static final int PATIENT_ID_WITH_ONE_RADIOLOGY_ORDER_AND_ACTIVE_VISIT = 70055;
    
    private static final int PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT = 70033;
    
    private static final int EXISTING_RADIOLOGY_ORDER_ID = 2001;
    
    private static final int NON_EXISTING_RADIOLOGY_ORDER_ID = 99999;
    
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
    private RadiologyOrderService radiologyOrderService;
    
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
     * @verifies create radiology order encounter with orderer and attached to existing active visit if patient has active
     *           visit
     */
    @Test
    public
            void
            placeRadiologyOrder_shouldCreateRadiologyOrderEncounterWithOrdererAndAttachedToExistingActiveVisitIfPatientHasActiveVisit()
                    throws Exception {
        
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        radiologyOrder.setPatient(patientService.getPatient(PATIENT_ID_WITH_NO_RADIOLOGY_ORDER_AND_NO_EXISTIG_ENCOUNTER_AND_ACTIVE_VISIT));
        
        assertThat(encounterService.getEncountersByPatient(radiologyOrder.getPatient()), is(empty()));
        assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient()), is(not(empty())));
        Visit preExistingVisit = visitService.getActiveVisitsByPatient(radiologyOrder.getPatient())
                .get(0);
        
        assertThat(radiologyOrder.getEncounter(), is(nullValue()));
        
        radiologyOrder = radiologyOrderService.placeRadiologyOrder(radiologyOrder);
        
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
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies create radiology order encounter with orderer attached to new active visit if patient without active visit
     */
    @Test
    public
            void
            placeRadiologyOrder_shouldCreateRadiologyOrderEncounterWithOrdererAttachedToNewActiveVisitIfPatientWithoutActiveVisit()
                    throws Exception {
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        
        assertThat(encounterService.getEncountersByPatient(radiologyOrder.getPatient()), is(empty()));
        assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient()), is(empty()));
        assertThat(radiologyOrder.getEncounter(), is(nullValue()));
        
        radiologyOrder = radiologyOrderService.placeRadiologyOrder(radiologyOrder);
        
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
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies throw illegal argument exception given null
     */
    @Test
    public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder is required");
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
        expectedException.expectMessage("Cannot edit an existing order!");
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
        expectedException.expectMessage("radiologyOrder.study is required");
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
        expectedException.expectMessage("radiologyOrder.study.modality is required");
        radiologyOrderService.placeRadiologyOrder(radiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder,Provider,String)
     * @verifies create discontinuation order which discontinues given radiology order that is not
     *           in progress or completed
     */
    @Test
    public
            void
            discontinueRadiologyOrder_shouldCreateDiscontinuationOrderWhichDiscontinuesGivenRadiologyOrderThatIsNotInProgressOrCompleted()
                    throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(null);
        String discontinueReason = "Wrong Procedure";
        
        Order discontinuationOrder =
                radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(),
                    discontinueReason);
        
        assertNotNull(discontinuationOrder);
        assertThat(discontinuationOrder.getAction(), is(Order.Action.DISCONTINUE));
        
        assertThat(discontinuationOrder.getPreviousOrder(), is((Order) radiologyOrder));
        assertThat(radiologyOrder.isActive(), is(false));
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder,Provider,String)
     * @verifies create discontinuation order with encounter attached to existing active visit if patient has active visit
     */
    @Test
    public
            void
            discontinueRadiologyOrder_shouldCreateDiscontinuationOrderWithEncounterAttachedToExistingActiveVisitIfPatientHasActiveVisit()
                    throws Exception {
        Patient patient = patientService.getPatient(PATIENT_ID_WITH_ONE_RADIOLOGY_ORDER_AND_ACTIVE_VISIT);
        
        assertThat(visitService.getActiveVisitsByPatient(patient), is(not(empty())));
        assertThat(visitService.getActiveVisitsByPatient(patient)
                .get(0)
                .getEncounters(), is(not(empty())));
        
        RadiologyOrder radiologyOrder =
                radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_ENCOUNTER_AND_ACTIVE_VISIT);
        
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
        Order discontinuationOrder =
                radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(),
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
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder,Provider,String)
     * @verifies create discontinuation order with encounter attached to new active visit if patient without active visit
     */
    @Test
    public
            void
            discontinueRadiologyOrder_shouldCreateDiscontinuationOrderWithEncounterAttachedToNewActiveVisitIfPatientWithoutActiveVisit()
                    throws Exception {
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
        
        assertThat(visitService.getActiveVisitsByPatient(radiologyOrder.getPatient()), is(empty()));
        
        radiologyOrder.getStudy()
                .setPerformedStatus(null);
        String discontinueReason = "Wrong Procedure";
        
        Order discontinuationOrder =
                radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(),
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
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies should throw illegal argument exception given empty radiology order
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenEmptyRadiologyOrder() throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder is required");
        radiologyOrderService.discontinueRadiologyOrder(null, radiologyOrder.getOrderer(), discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies should throw illegal argument exception given radiology order with orderId null
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenRadiologyOrderWithOrderIdNull()
            throws Exception {
        
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("orderId is null");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, Date, String)
     * @verifies should throw illegal argument exception if radiology order is not active
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfRadiologyOrderIsNotActive() throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.setAction(Order.Action.DISCONTINUE);
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("order is not active");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw illegal argument exception if radiology order is completed
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfRadiologyOrderIsCompleted() throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder is in progress");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw illegal argument exception if radiology order is in progress
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfRadiologyOrderIsInProgress() throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder is completed");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, radiologyOrder.getOrderer(), discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies should throw illegal argument exception given empty provider
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenEmptyProvider() throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(null);
        String discontinueReason = "Wrong Procedure";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("provider is required");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, null, discontinueReason);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrderByOrderId(Integer)
     * @verifies should return radiology order matching order id
     */
    @Test
    public void getRadiologyOrderByOrderId_shouldReturnRadiologyOrderMatchingOrderId() {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
        
        assertNotNull(radiologyOrder);
        assertThat(radiologyOrder.getOrderId(), is(EXISTING_RADIOLOGY_ORDER_ID));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrderByOrderId(Integer)
     * @verifies should return null if no match was found
     */
    @Test
    public void getRadiologyOrderByOrderId_shouldReturnNullIfNoMatchIsFound() {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByOrderId(NON_EXISTING_RADIOLOGY_ORDER_ID);
        
        assertNull(radiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrderByOrderId(Integer)
     * @verifies should throw illegal argument exception given null
     */
    @Test
    public void getRadiologyOrderByOrderId_shouldThrowIllegalArgumentExceptionGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("orderId is required");
        radiologyOrderService.getRadiologyOrderByOrderId(null);
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
     * @verifies should throw illegal argument exception given null
     */
    @Test
    public void getRadiologyOrdersByPatient_shouldThrowIllegalArgumentExceptionGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("patient is required");
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
                .size(), is(2));
        Patient patientWithFiveRadiologyOrders = patientService.getPatient(PATIENT_ID_WITH_FIVE_RADIOLOGY_ORDERS);
        assertThat(orderService.getAllOrdersByPatient(patientWithFiveRadiologyOrders)
                .size(), is(1));
        Patient patientWithOneRadiologyOrder =
                patientService.getPatient(PATIENT_ID_WITH_ONE_RADIOLOGY_ORDER_AND_ACTIVE_VISIT);
        assertThat(orderService.getAllOrdersByPatient(patientWithOneRadiologyOrder)
                .size(), is(1));
        List<Patient> allPatientsWithRadiologyOrders = new ArrayList<Patient>();
        allPatientsWithRadiologyOrders.add(patientWithTwoRadiologyOrders);
        allPatientsWithRadiologyOrders.add(patientWithFiveRadiologyOrders);
        allPatientsWithRadiologyOrders.add(patientWithOneRadiologyOrder);
        
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
     * @verifies should throw illegal argument exception given null
     */
    @Test
    public void getRadiologyOrdersByPatients_shouldThrowIllegalArgumentExceptionGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("patients is required");
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
