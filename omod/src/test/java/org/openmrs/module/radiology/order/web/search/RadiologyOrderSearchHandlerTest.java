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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.PatientResource1_9;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Tests {@link RadiologyOrderSearchHandler}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RestUtil.class, Context.class })
public class RadiologyOrderSearchHandlerTest {
    
    
    private static final String PATIENT_UUID_WITH_ORDERS = "1bae735a-fca0-11e5-9e59-08002719a237";
    
    private static final String PATIENT_UUID_WITHOUT_ORDERS = "88888888-fca0-11e5-9e59-08002719a237";
    
    private static final String PATIENT_UUID_UNKNOWN = "99999999-fca0-11e5-9e59-08002719a237";
    
    @Mock
    RestService restService;
    
    @Mock
    PatientService patientService;
    
    @Mock
    RadiologyOrderService radiologyOrderService;
    
    @Mock
    PatientResource1_9 patientResource = new PatientResource1_9();
    
    @InjectMocks
    RadiologyOrderSearchHandler radiologyOrderSearchHandler = new RadiologyOrderSearchHandler();
    
    Patient patientWithOrders = new Patient();
    
    Patient patientWithoutOrders = new Patient();
    
    RadiologyOrder radiologyOrder1 = new RadiologyOrder();
    
    RadiologyOrder radiologyOrder2 = new RadiologyOrder();
    
    @Before
    public void setUp() throws Exception {
        
        patientWithOrders.setUuid(PATIENT_UUID_WITH_ORDERS);
        radiologyOrder1.setPatient(patientWithOrders);
        radiologyOrder2.setPatient(patientWithOrders);
        List<RadiologyOrder> radiologyOrders = new ArrayList<RadiologyOrder>();
        radiologyOrders.add(radiologyOrder1);
        radiologyOrders.add(radiologyOrder2);
        
        patientWithoutOrders.setUuid(PATIENT_UUID_WITHOUT_ORDERS);
        
        PowerMockito.mockStatic(RestUtil.class);
        PowerMockito.mockStatic(Context.class);
        when(Context.getPatientService()).thenReturn(patientService);
        when(patientResource.getByUniqueId(PATIENT_UUID_WITH_ORDERS)).thenReturn(patientWithOrders);
        when(patientResource.getByUniqueId(PATIENT_UUID_WITHOUT_ORDERS)).thenReturn(patientWithoutOrders);
        when(patientResource.getByUniqueId(PATIENT_UUID_UNKNOWN)).thenReturn(null);
        when(Context.getService(RestService.class)).thenReturn(restService);
        when(restService.getResourceBySupportedClass(Patient.class)).thenReturn(patientResource);
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return empty search result if patient cannot be found
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfPatientCannotBeFound() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, PATIENT_UUID_UNKNOWN);
        RequestContext requestContext = new RequestContext();
        requestContext.setRequest(request);
        
        PageableResult pageableResult = radiologyOrderSearchHandler.search(requestContext);
        assertThat(pageableResult, is(instanceOf(EmptySearchResult.class)));
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return empty search result if patient has no radiology orders
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfPatientHasNoRadiologyOrders() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, PATIENT_UUID_WITHOUT_ORDERS);
        RequestContext requestContext = new RequestContext();
        requestContext.setRequest(request);
        
        PageableResult pageableResult = radiologyOrderSearchHandler.search(requestContext);
        assertThat(pageableResult, is(instanceOf(EmptySearchResult.class)));
    }
}
