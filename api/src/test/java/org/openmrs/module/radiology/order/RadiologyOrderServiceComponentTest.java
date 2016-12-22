/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hamcrest.Matchers;
import org.hibernate.cfg.Environment;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Order.Urgency;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
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
    
    private static final int PATIENT_ID_WITH_TWO_RADIOLOGY_ORDERS = 70021;
    
    private static final int PATIENT_ID_WITH_ONE_VOIDED_AND_TWO_NON_VOIDED_RADIOLOGY_ORDERS = 70023;
    
    private static final int EXISTING_RADIOLOGY_ORDER_ID = 2001;
    
    private static final String EXISTING_RADIOLOGY_ORDER_UUID = "44f24d7e-ebbd-4500-bfba-1db19561ca04";
    
    private static final int NON_EXISTING_RADIOLOGY_ORDER_ID = 99999;
    
    private static final String NON_EXISTING_RADIOLOGY_ORDER_UUID = "99999999-ebbd-4500-bfba-1db19561ca04";
    
    private static final String RADIOLOGY_ORDER_UUID_OF_VOIDED = "56816dbe-59aa-4d4d-a943-3016009e9ae1";
    
    private static final int CONCEPT_ID_FOR_FRACTURE = 178;
    
    private static final String EXISTING_RADIOLOGY_ORDER_ACCESSION_NUMBER = "1";
    
    private static final int PROVIDER_ID_WITH_TWO_ASSIGNED_RADIOLOGY_ORDERS = 2;
    
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
    private RadiologyOrderService radiologyOrderService;
    
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
        // We need to commit the global property nextAccessionNumberSeed otherwise it will not be visible in the
        // HibernateRadiologyOrderDAO since the RadiologyOrderServiceImpl.getNextAccessionNumberSeedSequenceValue() opens a
        // new transaction.
        if (!Context.isSessionOpen()) {
            Context.openSession();
        }
        executeDataSet(TEST_DATASET);
        getConnection().commit();
        Context.clearSession();
    }
    
    @After
    public void tearDown() throws Exception {
        // We need to delete all data we committed otherwise this will influence other test classes and break isolation.
        this.deleteAllData();
    }
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies create new radiology order and study from given radiology order
     */
    @Test
    public void placeRadiologyOrder_shouldCreateNewRadiologyOrderAndStudyGivenRadiologyOrder() throws Exception {
        
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        
        radiologyOrder = radiologyOrderService.placeRadiologyOrder(radiologyOrder);
        
        assertNotNull(radiologyOrder);
        assertNotNull(radiologyOrder.getOrderId());
        assertNotNull(radiologyOrder.getAccessionNumber());
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
        
        EncounterSearchCriteriaBuilder encounterSearchCriteria =
                new EncounterSearchCriteriaBuilder().setPatient(radiologyOrder.getPatient())
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
     * @verifies set the radiology order accession number
     */
    @Test
    public void placeRadiologyOrder_shouldSetTheRadiologyOrderAccessionNumber() throws Exception {
        
        RadiologyOrder radiologyOrder = getUnsavedRadiologyOrder();
        radiologyOrder.setAccessionNumber(null);
        
        radiologyOrder = radiologyOrderService.placeRadiologyOrder(radiologyOrder);
        
        assertNotNull(radiologyOrder);
        assertNotNull(radiologyOrder.getAccessionNumber());
    }
    
    /**
     * @see AccessionNumberGenerator#getNewAccessionNumber()
     * @verifies always return a unique accession number when called multiple times
     */
    @Test
    public void getNewAccessionNumber_shouldAlwaysReturnAUniqueAccessionNumberWhenCalledMultipleTimes() throws Exception {
        
        int N = 50;
        final Set<String> uniqueAccessionNumbers = new HashSet<String>(N);
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < N; i++) {
            threads.add(new Thread(new Runnable() {
                
                
                @Override
                public void run() {
                    try {
                        Context.openSession();
                        uniqueAccessionNumbers
                                .add(((AccessionNumberGenerator) radiologyOrderService).getNewAccessionNumber());
                    }
                    finally {
                        Context.closeSession();
                    }
                }
            }));
        }
        for (int i = 0; i < N; ++i) {
            threads.get(i)
                    .start();
        }
        for (int i = 0; i < N; ++i) {
            threads.get(i)
                    .join();
        }
        // since we used a set we should have the size as N indicating that there were no duplicates
        Assert.assertEquals(N, uniqueAccessionNumbers.size());
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
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies return all radiology orders for given patient if patient is specified
     */
    @Test
    public void getRadiologyOrders_shouldReturnAllRadiologyOrdersForGivenPatientIfPatientIsSpecified() throws Exception {
        
        Patient patient = patientService.getPatient(PATIENT_ID_WITH_TWO_RADIOLOGY_ORDERS);
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteria =
                new RadiologyOrderSearchCriteria.Builder().withPatient(patient)
                        .build();
        
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria);
        assertThat(radiologyOrders.size(), is(2));
        for (RadiologyOrder radiologyOrder : radiologyOrders) {
            assertThat(radiologyOrder.getPatient(), is(patient));
        }
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies return all radiology orders (including voided) matching the search query if include voided is set
     */
    @Test
    public void getRadiologyOrders_shouldReturnAllRadiologyOrdersIncludingVoidedMatchingTheSearchQueryIfIncludeVoidedIsSet()
            throws Exception {
        
        Patient patient = patientService.getPatient(PATIENT_ID_WITH_ONE_VOIDED_AND_TWO_NON_VOIDED_RADIOLOGY_ORDERS);
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteria =
                new RadiologyOrderSearchCriteria.Builder().withPatient(patient)
                        .includeVoided()
                        .build();
        
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria);
        assertThat(radiologyOrders.size(), is(3));
        assertThat(radiologyOrders,
            hasItem(Matchers.<RadiologyOrder> hasProperty("uuid", is(RADIOLOGY_ORDER_UUID_OF_VOIDED))));
        assertThat(radiologyOrders, hasItem(Matchers.<RadiologyOrder> hasProperty("voided", is(true))));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies return all radiology orders for given urgency
     */
    @Test
    public void getRadiologyOrders_shouldReturnAllRadiologyOrdersForGivenUrgency() throws Exception {
        
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteria =
                new RadiologyOrderSearchCriteria.Builder().withUrgency(Urgency.STAT)
                        .build();
        
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria);
        assertThat(radiologyOrders.size(), is(5));
        for (RadiologyOrder radiologyOrder : radiologyOrders) {
            assertThat(radiologyOrder.getUrgency(), is(Urgency.STAT));
        }
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies return all radiology orders with effective order start date in given date range if to date and from date are
     *           specified
     */
    @Test
    public void
            getRadiologyOrders_shouldReturnAllRadiologyOrdersWithEffectiveOrderStartDateInGivenDateRangeIfToDateAndFromDateAreSpecified()
                    throws Exception {
        
        Patient patient = patientService.getPatient(70024);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = format.parse("2016-02-02");
        Date toDate = format.parse("2016-04-04");
        
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteriaDateRange =
                new RadiologyOrderSearchCriteria.Builder().includeVoided()
                        .withPatient(patient)
                        .fromEffectiveStartDate(fromDate)
                        .toEffectiveStartDate(toDate)
                        .build();
        List<RadiologyOrder> radiologyOrders =
                radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteriaDateRange);
        assertThat(radiologyOrders.size(), is(3));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(2009)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20012)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20015)));
        for (RadiologyOrder radiologyOrder : radiologyOrders) {
            if (radiologyOrder.getUrgency()
                    .equals(Urgency.ON_SCHEDULED_DATE)) {
                assertTrue(radiologyOrder.getScheduledDate()
                        .compareTo(fromDate) >= 0);
                assertTrue(radiologyOrder.getScheduledDate()
                        .compareTo(toDate) <= 0);
            } else {
                assertTrue(radiologyOrder.getDateActivated()
                        .compareTo(fromDate) >= 0);
                assertTrue(radiologyOrder.getDateActivated()
                        .compareTo(toDate) <= 0);
            }
        }
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies return all radiology orders with effective order start date after or equal to from date if only from date is
     *           specified
     */
    @Test
    public void
            getRadiologyOrders_shouldReturnAllRadiologyOrdersWithEffectiveOrderStartDateAfterOrEqualToFromDateIfOnlyFromDateIsSpecified()
                    throws Exception {
        
        Patient patient = patientService.getPatient(70024);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = format.parse("2016-03-03");
        
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteriaDateRange =
                new RadiologyOrderSearchCriteria.Builder().includeVoided()
                        .withPatient(patient)
                        .fromEffectiveStartDate(fromDate)
                        .build();
        List<RadiologyOrder> radiologyOrders =
                radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteriaDateRange);
        assertThat(radiologyOrders.size(), is(6));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(2009)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20010)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20012)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20013)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20015)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20016)));
        for (RadiologyOrder radiologyOrder : radiologyOrders) {
            if (radiologyOrder.getUrgency()
                    .equals(Urgency.ON_SCHEDULED_DATE)) {
                assertTrue(radiologyOrder.getScheduledDate()
                        .compareTo(fromDate) >= 0);
            } else {
                assertTrue(radiologyOrder.getDateActivated()
                        .compareTo(fromDate) >= 0);
            }
        }
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies return all radiology orders with effective order start date before or equal to to date if only to date is
     *           specified
     */
    @Test
    public void
            getRadiologyOrders_shouldReturnAllRadiologyOrdersWithEffectiveOrderStartDateBeforeOrEqualToToDateIfOnlyToDateIsSpecified()
                    throws Exception {
        
        Patient patient = patientService.getPatient(70024);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date toDate = format.parse("2016-03-03");
        
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteriaDateRange =
                new RadiologyOrderSearchCriteria.Builder().includeVoided()
                        .withPatient(patient)
                        .toEffectiveStartDate(toDate)
                        .build();
        List<RadiologyOrder> radiologyOrders =
                radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteriaDateRange);
        assertThat(radiologyOrders.size(), is(6));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(2008)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(2009)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20011)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20012)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20014)));
        assertThat(radiologyOrders, hasItem(radiologyOrderService.getRadiologyOrder(20015)));
        for (RadiologyOrder radiologyOrder : radiologyOrders) {
            if (radiologyOrder.getUrgency()
                    .equals(Urgency.ON_SCHEDULED_DATE)) {
                assertTrue(radiologyOrder.getScheduledDate()
                        .compareTo(toDate) <= 0);
            } else {
                assertTrue(radiologyOrder.getDateActivated()
                        .compareTo(toDate) <= 0);
            }
        }
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies return empty list if from date after to date
     */
    @Test
    public void getRadiologyOrders_shouldReturnEmptyListIfFromDateAfterToDate() throws Exception {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = format.parse("2016-06-30");
        Date toDate = format.parse("2016-05-29");
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteria =
                new RadiologyOrderSearchCriteria.Builder().fromEffectiveStartDate(fromDate)
                        .toEffectiveStartDate(toDate)
                        .build();
        
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria);
        assertTrue(radiologyOrders.isEmpty());
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies return empty search result if no effective order start is in date range
     */
    @Test
    public void getRadiologyOrders_shouldReturnEmptySearchResultIfNoEffectiveOrderStartIsInDateRange() throws Exception {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteriaDateRange =
                new RadiologyOrderSearchCriteria.Builder().fromEffectiveStartDate(format.parse("2016-06-06"))
                        .toEffectiveStartDate(format.parse("2016-07-07"))
                        .build();
        
        List<RadiologyOrder> radiologyOrdersWithDateRange =
                radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteriaDateRange);
        assertTrue(radiologyOrdersWithDateRange.isEmpty());
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies return all radiology orders for given accession number if accession number is specified
     */
    @Test
    public void getRadiologyOrders_shouldReturnAllRadiologyOrdersForGivenAccessionNumberIfAccessionNumberIsSpecified()
            throws Exception {
        
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteria =
                new RadiologyOrderSearchCriteria.Builder().withAccessionNumber(EXISTING_RADIOLOGY_ORDER_ACCESSION_NUMBER)
                        .build();
        
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria);
        assertThat(radiologyOrders.size(), is(1));
        assertThat(radiologyOrders.get(0)
                .getAccessionNumber(),
            is(EXISTING_RADIOLOGY_ORDER_ACCESSION_NUMBER));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies return all radiology orders for given orderer
     */
    @Test
    public void getRadiologyOrders_shouldReturnAllRadiologyOrdersForGivenOrderer() throws Exception {
        
        Provider orderer = providerService.getProvider(PROVIDER_ID_WITH_TWO_ASSIGNED_RADIOLOGY_ORDERS);
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteria =
                new RadiologyOrderSearchCriteria.Builder().withOrderer(orderer)
                        .build();
        
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria);
        assertThat(radiologyOrders.size(), is(2));
        for (RadiologyOrder radiologyOrder : radiologyOrders) {
            assertThat(radiologyOrder.getOrderer(), is(orderer));
        }
    }
    
    /**
    * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
    * @verifies return all radiology orders for given urgency and orderer
    */
    @Test
    public void getRadiologyOrders_shouldReturnAllRadiologyOrdersForGivenUrgencyAndOrderer() throws Exception {
        Provider orderer = providerService.getProvider(PROVIDER_ID_WITH_TWO_ASSIGNED_RADIOLOGY_ORDERS);
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteria =
                new RadiologyOrderSearchCriteria.Builder().withOrderer(orderer)
                        .withUrgency(Urgency.STAT)
                        .build();
        
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria);
        assertThat(radiologyOrders.size(), is(1));
        assertThat(radiologyOrders.get(0)
                .getOrderer(),
            is(orderer));
        assertThat(radiologyOrders.get(0)
                .getUrgency(),
            is(Urgency.STAT));
        assertThat(radiologyOrders.get(0)
                .getOrderId(),
            is(2006));
    }
}
