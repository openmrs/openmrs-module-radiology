/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order.web.search;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Order.Urgency;
import org.openmrs.api.PatientService;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderSearchCriteria;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests {@link RadiologyOrderSearchHandler}.
 */
public class RadiologyOrderSearchHandlerComponentTest extends MainResourceControllerTest {
    
    
    protected static final String TEST_DATASET = "RadiologyOrderSearchHandlerComponentTestDataset.xml";
    
    private static final String ACCESSION_NUMBER_WITH_ORDER = "4";
    
    private static final String ACCESSION_NUMBER_WITH_NO_ORDER = "6";
    
    private static final String UNKNOWN_PATIENT = "99999999-9999-9999-9999-9999999999999";
    
    private static final String PATIENT_WITH_NO_ORDER = "0f1f7d08-076b-4fc6-acac-4bb91515141e7";
    
    private static final String PATIENT_WITH_ONE_ORDER = "72ff0770-fc9e-11e5-9e59-08002719a237";
    
    private static final String PATIENT_WITH_TWO_ORDERS = "5631b434-78aa-102b-91a0-001e378eb67e";
    
    private static final String DATE_AFTER_ORDER_EFFECTIVE_START_DATES = "2015-02-04 13:00:00";
    
    private static final String DATE_BEFORE_ORDER_EFFECTIVE_START_DATES = "2015-02-01 00:00:00";
    
    private static final String DATE_BETWEEN_ORDER_EFFECTIVE_START_DATES = "2015-02-03 12:00:00";
    
    private static final String RADIOLOGY_ORDER_UUID = "1bae735a-fca0-11e5-9e59-08002719a237";
    
    @Autowired
    PatientService patientService;
    
    @Autowired
    RadiologyOrderService radiologyOrderService;
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    DateFormat resultFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        
        return "radiologyorder";
    }
    
    /**
     * @see MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        
        return 0;
    }
    
    /**
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        
        return RADIOLOGY_ORDER_UUID;
    }
    
    /**
     * @see MainResourceControllerTest#shouldGetAll()
     */
    @Override
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        
        deserialize(handle(request(RequestMethod.GET, getURI())));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return all radiology orders for given accession number
     */
    @Test
    public void search_shouldReturnAllRadiologyOrdersForGivenAccessionNumber() throws Exception {
        
        MockHttpServletRequest requestAccessionNumberWithOrder = request(RequestMethod.GET, getURI());
        requestAccessionNumberWithOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_ACCESSION_NUMBER,
            ACCESSION_NUMBER_WITH_ORDER);
        requestAccessionNumberWithOrder.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject resultAccessionNumberWithOrder = deserialize(handle(requestAccessionNumberWithOrder));
        
        assertNotNull(resultAccessionNumberWithOrder);
        List<Object> hits = (List<Object>) resultAccessionNumberWithOrder.get("results");
        assertThat(hits.size(), is(1));
        assertThat(PropertyUtils.getProperty(hits.get(0), "accessionNumber"), is(ACCESSION_NUMBER_WITH_ORDER));
        assertNull(PropertyUtils.getProperty(resultAccessionNumberWithOrder, "totalCount"));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return empty search result if no radiology order exists for given accession number
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfNoRadiologyOrderExistsForGivenAccessionNumber() throws Exception {
        
        MockHttpServletRequest requestAccessionNumberWithNoOrders = request(RequestMethod.GET, getURI());
        requestAccessionNumberWithNoOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_ACCESSION_NUMBER,
            ACCESSION_NUMBER_WITH_NO_ORDER);
        
        SimpleObject resultAccessionNumberWithNoOrders = deserialize(handle(requestAccessionNumberWithNoOrders));
        
        assertNotNull(resultAccessionNumberWithNoOrders);
        List<Object> hits = (List<Object>) resultAccessionNumberWithNoOrders.get("results");
        assertThat(hits.size(), is(0));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return all radiology orders for given patient
     */
    @Test
    public void search_shouldReturnAllRadiologyOrdersForGivenPatient() throws Exception {
        
        MockHttpServletRequest requestPatientWithOneOrder = request(RequestMethod.GET, getURI());
        requestPatientWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, PATIENT_WITH_ONE_ORDER);
        requestPatientWithOneOrder.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject resultPatientWithOneOrder = deserialize(handle(requestPatientWithOneOrder));
        
        assertNotNull(resultPatientWithOneOrder);
        List<Object> hits = (List<Object>) resultPatientWithOneOrder.get("results");
        assertThat(hits.size(), is(1));
        final RadiologyOrderSearchCriteria radiologyOrderSearchCriteria = new RadiologyOrderSearchCriteria.Builder()
                .withPatient(patientService.getPatientByUuid(PATIENT_WITH_ONE_ORDER))
                .build();
        assertThat(PropertyUtils.getProperty(hits.get(0), "uuid"),
            is(radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria)
                    .get(0)
                    .getUuid()));
        assertNull(PropertyUtils.getProperty(resultPatientWithOneOrder, "totalCount"));
        
        MockHttpServletRequest requestPatientWithTwoOrders = request(RequestMethod.GET, getURI());
        requestPatientWithTwoOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, PATIENT_WITH_TWO_ORDERS);
        requestPatientWithTwoOrders.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject resultPatientWithTwoOrders = deserialize(handle(requestPatientWithTwoOrders));
        
        assertNotNull(resultPatientWithTwoOrders);
        assertThat(Util.getResultsSize(resultPatientWithTwoOrders), is(2));
        hits = (List<Object>) resultPatientWithTwoOrders.get("results");
        assertThat(hits.size(), is(2));
        assertNull(PropertyUtils.getProperty(resultPatientWithTwoOrders, "totalCount"));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return empty search result if patient cannot be found
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfPatientCannotBeFound() throws Exception {
        
        MockHttpServletRequest requestUnknownPatient = request(RequestMethod.GET, getURI());
        requestUnknownPatient.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, UNKNOWN_PATIENT);
        
        SimpleObject resultUnknownPatient = deserialize(handle(requestUnknownPatient));
        
        assertNotNull(resultUnknownPatient);
        List<Object> hits = (List<Object>) resultUnknownPatient.get("results");
        assertThat(hits.size(), is(0));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return empty search result if patient has no radiology orders
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfPatientHasNoRadiologyOrders() throws Exception {
        
        MockHttpServletRequest requestPatientWithNoOrder = request(RequestMethod.GET, getURI());
        requestPatientWithNoOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, PATIENT_WITH_NO_ORDER);
        
        SimpleObject resultPatientWithNoOrder = deserialize(handle(requestPatientWithNoOrder));
        
        assertNotNull(resultPatientWithNoOrder);
        List<Object> hits = (List<Object>) resultPatientWithNoOrder.get("results");
        assertThat(hits.size(), is(0));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return all radiology orders with effective order start date after or equal to from date if only from date is
     *           specified
     */
    @Test
    public void
            search_shouldReturnAllRadiologyOrdersWithEffectiveOrderStartDateAfterOrEqualToFromDateIfOnlyFromDateIsSpecified()
                    throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_EFFECTIVE_START_DATE_FROM,
            DATE_BETWEEN_ORDER_EFFECTIVE_START_DATES);
        request.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertThat(hits.size(), is(2));
        assertNull(PropertyUtils.getProperty(result, "totalCount"));
        
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteria = new RadiologyOrderSearchCriteria.Builder()
                .fromEffectiveStartDate(format.parse(DATE_BETWEEN_ORDER_EFFECTIVE_START_DATES))
                .build();
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria);
        
        for (int i = 0; i < hits.size(); i++) {
            if (PropertyUtils.getProperty(hits.get(i), "urgency")
                    .equals("ON_SCHEDULED_DATE")) {
                assertThat(PropertyUtils.getProperty(hits.get(i), "scheduledDate"),
                    is(resultFormat.format(radiologyOrders.get(i)
                            .getScheduledDate())));
            } else {
                assertThat(PropertyUtils.getProperty(hits.get(i), "dateActivated"),
                    is(resultFormat.format(radiologyOrders.get(i)
                            .getDateActivated())));
            }
        }
        
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return all radiology orders with effective order start date before or equal to to date if only to date is
     *           specified
     */
    @Test
    public void
            search_shouldReturnAllRadiologyOrdersWithEffectiveOrderStartDateBeforeOrEqualToToDateIfOnlyToDateIsSpecified()
                    throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_EFFECTIVE_START_DATE_TO,
            DATE_BETWEEN_ORDER_EFFECTIVE_START_DATES);
        request.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertThat(hits.size(), is(2));
        assertNull(PropertyUtils.getProperty(result, "totalCount"));
        
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteria = new RadiologyOrderSearchCriteria.Builder()
                .toEffectiveStartDate(format.parse(DATE_BETWEEN_ORDER_EFFECTIVE_START_DATES))
                .build();
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria);
        
        for (int i = 0; i < hits.size(); i++) {
            if (PropertyUtils.getProperty(hits.get(i), "urgency")
                    .equals("ON_SCHEDULED_DATE")) {
                assertThat(PropertyUtils.getProperty(hits.get(i), "scheduledDate"),
                    is(resultFormat.format(radiologyOrders.get(i)
                            .getScheduledDate())));
            } else {
                assertThat(PropertyUtils.getProperty(hits.get(i), "dateActivated"),
                    is(resultFormat.format(radiologyOrders.get(i)
                            .getDateActivated())));
            }
        }
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return all radiology orders with effective order start date in given date range if to date and from date are
     *           specified
     */
    @Test
    public void
            search_shouldReturnAllRadiologyOrdersWithEffectiveOrderStartDateInGivenDateRangeIfToDateAndFromDateAreSpecified()
                    throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_EFFECTIVE_START_DATE_FROM,
            DATE_BEFORE_ORDER_EFFECTIVE_START_DATES);
        request.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_EFFECTIVE_START_DATE_TO,
            DATE_AFTER_ORDER_EFFECTIVE_START_DATES);
        request.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertThat(hits.size(), is(4));
        assertNull(PropertyUtils.getProperty(result, "totalCount"));
        
        RadiologyOrderSearchCriteria radiologyOrderSearchCriteria = new RadiologyOrderSearchCriteria.Builder()
                .fromEffectiveStartDate(format.parse(DATE_BEFORE_ORDER_EFFECTIVE_START_DATES))
                .toEffectiveStartDate(format.parse(DATE_AFTER_ORDER_EFFECTIVE_START_DATES))
                .build();
        List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrders(radiologyOrderSearchCriteria);
        
        for (int i = 0; i < hits.size(); i++) {
            if (PropertyUtils.getProperty(hits.get(i), "urgency")
                    .equals("ON_SCHEDULED_DATE")) {
                assertThat(PropertyUtils.getProperty(hits.get(i), "scheduledDate"),
                    is(resultFormat.format(radiologyOrders.get(i)
                            .getScheduledDate())));
            } else {
                assertThat(PropertyUtils.getProperty(hits.get(i), "dateActivated"),
                    is(resultFormat.format(radiologyOrders.get(i)
                            .getDateActivated())));
            }
        }
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return empty search result if no effective order start is in date range
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfNoEffectiveOrderStartIsInDateRange() throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_EFFECTIVE_START_DATE_FROM,
            DATE_AFTER_ORDER_EFFECTIVE_START_DATES);
        request.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertThat(hits.size(), is(0));
        assertNull(PropertyUtils.getProperty(result, "totalCount"));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return all radiology orders for given urgency
     */
    @Test
    public void search_shouldReturnAllRadiologyOrdersForGivenUrgency() throws Exception {
        
        MockHttpServletRequest requestUrgencyWithOneOrder = request(RequestMethod.GET, getURI());
        requestUrgencyWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_URGENCY,
            Urgency.ON_SCHEDULED_DATE.toString());
        requestUrgencyWithOneOrder.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject resultUrgencyWithOneOrder = deserialize(handle(requestUrgencyWithOneOrder));
        
        assertNotNull(resultUrgencyWithOneOrder);
        List<Object> hits = (List<Object>) resultUrgencyWithOneOrder.get("results");
        assertThat(hits.size(), is(1));
        assertThat(PropertyUtils.getProperty(hits.get(0), "urgency"), is(Urgency.ON_SCHEDULED_DATE.toString()));
        assertNull(PropertyUtils.getProperty(resultUrgencyWithOneOrder, "totalCount"));
        
        MockHttpServletRequest requestUrgencyWithThreeOrders = request(RequestMethod.GET, getURI());
        requestUrgencyWithThreeOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_URGENCY,
            Urgency.ROUTINE.toString());
        requestUrgencyWithThreeOrders.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject resultUrgencyWithThreeOrders = deserialize(handle(requestUrgencyWithThreeOrders));
        
        assertNotNull(resultUrgencyWithThreeOrders);
        hits = (List<Object>) resultUrgencyWithThreeOrders.get("results");
        assertThat(hits.size(), is(3));
        for (int i = 0; i < hits.size(); i++) {
            assertThat(PropertyUtils.getProperty(hits.get(i), "urgency"), is(Urgency.ROUTINE.toString()));
        }
        assertNull(PropertyUtils.getProperty(resultUrgencyWithThreeOrders, "totalCount"));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return empty search result if no radiology order exists for given urgency
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfNoRadiologyOrderExistsForGivenUrgency() throws Exception {
        
        MockHttpServletRequest requestUrgencyWithNoOrders = request(RequestMethod.GET, getURI());
        requestUrgencyWithNoOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_URGENCY, Urgency.STAT.toString());
        
        SimpleObject resultUrgencyWithNoOrders = deserialize(handle(requestUrgencyWithNoOrders));
        
        assertNotNull(resultUrgencyWithNoOrders);
        List<Object> hits = (List<Object>) resultUrgencyWithNoOrders.get("results");
        assertThat(hits.size(), is(0));
        assertNull(PropertyUtils.getProperty(resultUrgencyWithNoOrders, "totalCount"));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies throw illegal argument exception if urgency doesn't exist
     */
    @Test
    public void search_shouldThrowIllegalArgumentExceptionIfUrgencyDoesntExist() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        
        MockHttpServletRequest requestUrgencyWithNoOrders = request(RequestMethod.GET, getURI());
        requestUrgencyWithNoOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_URGENCY, "wrong_urgency");
        
        deserialize(handle(requestUrgencyWithNoOrders));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return all radiology orders matching the search query and totalCount if requested
     */
    @Test
    public void search_shouldReturnAllRadiologyOrdersMatchingTheSearchQueryAndTotalCountIfRequested() throws Exception {
        
        MockHttpServletRequest requestPatientAndUrgencyWithOneOrder = request(RequestMethod.GET, getURI());
        requestPatientAndUrgencyWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT,
            PATIENT_WITH_ONE_ORDER);
        requestPatientAndUrgencyWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_URGENCY,
            Urgency.ROUTINE.toString());
        requestPatientAndUrgencyWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_TOTAL_COUNT, "true");
        SimpleObject resultPatientAndUrgencyWithOneOrder = deserialize(handle(requestPatientAndUrgencyWithOneOrder));
        
        assertNotNull(resultPatientAndUrgencyWithOneOrder);
        assertThat(PropertyUtils.getProperty(resultPatientAndUrgencyWithOneOrder, "totalCount"), is(1));
        
        MockHttpServletRequest requestPatientAndUrgencyWithTwoOrders = request(RequestMethod.GET, getURI());
        requestPatientAndUrgencyWithTwoOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT,
            PATIENT_WITH_TWO_ORDERS);
        requestPatientAndUrgencyWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_URGENCY,
            Urgency.ROUTINE.toString());
        requestPatientAndUrgencyWithTwoOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_TOTAL_COUNT, "true");
        
        SimpleObject resultPatientAndUrgencyWithTwoOrders = deserialize(handle(requestPatientAndUrgencyWithTwoOrders));
        
        assertNotNull(resultPatientAndUrgencyWithTwoOrders);
        assertThat(PropertyUtils.getProperty(resultPatientAndUrgencyWithTwoOrders, "totalCount"), is(2));
    }
}
