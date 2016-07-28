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

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Order.Urgency;
import org.openmrs.api.PatientService;
import org.openmrs.module.radiology.order.RadiologyOrderSearchCriteria;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
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
    
    private static final String UNKNOWN_PATIENT = "99999999-9999-9999-9999-9999999999999";
    
    private static final String PATIENT_WITH_NO_ORDER = "0f1f7d08-076b-4fc6-acac-4bb91515141e7";
    
    private static final String PATIENT_WITH_ONE_ORDER = "72ff0770-fc9e-11e5-9e59-08002719a237";
    
    private static final String PATIENT_WITH_TWO_ORDERS = "5631b434-78aa-102b-91a0-001e378eb67e";
    
    private static final String RADIOLOGY_ORDER_UUID = "1bae735a-fca0-11e5-9e59-08002719a237";
    
    @Autowired
    PatientService patientService;
    
    @Autowired
    RadiologyOrderService radiologyOrderService;
    
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
     * @verifies return all radiology orders for given urgency
     */
    @Test
    public void search_shouldReturnAllRadiologyOrdersForGivenUrgency() throws Exception {
        
        MockHttpServletRequest requestUrgencyWithOneOrder = request(RequestMethod.GET, getURI());
        requestUrgencyWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_URGENCY, Urgency.STAT.toString());
        requestUrgencyWithOneOrder.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject resultUrgencyWithOneOrder = deserialize(handle(requestUrgencyWithOneOrder));
        
        assertNotNull(resultUrgencyWithOneOrder);
        List<Object> hits = (List<Object>) resultUrgencyWithOneOrder.get("results");
        assertThat(hits.size(), is(1));
        assertThat(PropertyUtils.getProperty(hits.get(0), "urgency"), is(Urgency.STAT.toString()));
        assertNull(PropertyUtils.getProperty(resultUrgencyWithOneOrder, "totalCount"));
        
        MockHttpServletRequest requestUrgencyWithThreeOrders = request(RequestMethod.GET, getURI());
        requestUrgencyWithThreeOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_URGENCY,
            Urgency.ROUTINE.toString());
        requestUrgencyWithThreeOrders.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject resultUrgencyWithThreeOrders = deserialize(handle(requestUrgencyWithThreeOrders));
        
        assertNotNull(resultUrgencyWithThreeOrders);
        hits = (List<Object>) resultUrgencyWithThreeOrders.get("results");
        assertThat(hits.size(), is(3));
        for (int i = 0; i < 3; i++) {
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
        requestUrgencyWithNoOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_URGENCY,
            Urgency.ON_SCHEDULED_DATE.toString());
        
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
