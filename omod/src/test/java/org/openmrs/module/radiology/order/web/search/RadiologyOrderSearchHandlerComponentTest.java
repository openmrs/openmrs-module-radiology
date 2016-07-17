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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.PatientService;
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
    
    private static final String UNKNOWN_PATIENT = "99999999-9999-9999-9999-9999999999999";
    
    private static final String PATIENT_WITH_NO_ORDER = "0f1f7d08-076b-4fc6-acac-4bb91515141e7";
    
    private static final String PATIENT_WITH_ONE_ORDER = "72ff0770-fc9e-11e5-9e59-08002719a237";
    
    private static final String PATIENT_WITH_TWO_ORDERS = "5631b434-78aa-102b-91a0-001e378eb67e";
    
    private static final String RADIOLOGY_ORDER_UUID = "1bae735a-fca0-11e5-9e59-08002719a237";
    
    @Autowired
    PatientService patientService;
    
    @Autowired
    RadiologyOrderService radiologyOrderService;
    
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
     * @verifies return empty search result if patient cannot be found
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfPatientCannotBeFound() throws Exception {
        
        MockHttpServletRequest requestPatientWithOneOrder = request(RequestMethod.GET, getURI());
        requestPatientWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, UNKNOWN_PATIENT);
        
        SimpleObject resultPatientWithOneOrder = deserialize(handle(requestPatientWithOneOrder));
        
        assertNotNull(resultPatientWithOneOrder);
        List<Object> hits = (List<Object>) resultPatientWithOneOrder.get("results");
        assertThat(hits.size(), is(0));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return empty search result if patient has no radiology orders
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfPatientHasNoRadiologyOrders() throws Exception {
        
        MockHttpServletRequest requestPatientWithOneOrder = request(RequestMethod.GET, getURI());
        requestPatientWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, PATIENT_WITH_NO_ORDER);
        
        SimpleObject resultPatientWithOneOrder = deserialize(handle(requestPatientWithOneOrder));
        
        assertNotNull(resultPatientWithOneOrder);
        List<Object> hits = (List<Object>) resultPatientWithOneOrder.get("results");
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
        assertThat(PropertyUtils.getProperty(hits.get(0), "uuid"),
            is(radiologyOrderService.getRadiologyOrdersByPatient(patientService.getPatientByUuid(PATIENT_WITH_ONE_ORDER))
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
     * @verifies return all radiology orders for given patient and totalCount if requested
     */
    @Test
    public void search_shouldReturnAllRadiologyOrdersForGivenPatientAndTotalCountIfRequested() throws Exception {
        
        MockHttpServletRequest requestPatientWithOneOrder = request(RequestMethod.GET, getURI());
        requestPatientWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, PATIENT_WITH_ONE_ORDER);
        requestPatientWithOneOrder.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_TOTAL_COUNT, "true");
        SimpleObject resultPatientWithOneOrder = deserialize(handle(requestPatientWithOneOrder));
        
        assertNotNull(resultPatientWithOneOrder);
        assertThat(PropertyUtils.getProperty(resultPatientWithOneOrder, "totalCount"), is(1));
        
        MockHttpServletRequest requestPatientWithTwoOrders = request(RequestMethod.GET, getURI());
        requestPatientWithTwoOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, PATIENT_WITH_TWO_ORDERS);
        requestPatientWithTwoOrders.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_TOTAL_COUNT, "true");
        
        SimpleObject resultPatientWithTwoOrders = deserialize(handle(requestPatientWithTwoOrders));
        
        assertNotNull(resultPatientWithTwoOrders);
        assertThat(PropertyUtils.getProperty(resultPatientWithTwoOrders, "totalCount"), is(2));
    }
}
