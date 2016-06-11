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
@PrepareForTest({ RestService.class, Context.class })
public class RadiologyOrderSearchHandlerTest {
    
    
    private static final String PATIENT_UUID = "1bae735a-fca0-11e5-9e59-08002719a237";
    
    private static final String UNKNOWN_PATIENT_UUID = "99999-fca0-11e5-9e59-08002719a237";
    
    @Mock
    PatientService patientService;
    
    @Mock
    RadiologyOrderService radiologyOrderService;
    
    @Mock
    PatientResource1_9 patientResource = new PatientResource1_9();
    
    @InjectMocks
    RadiologyOrderSearchHandler radiologyOrderSearchHandler = new RadiologyOrderSearchHandler();
    
    Patient patient = new Patient();
    
    RadiologyOrder radiologyOrder1 = new RadiologyOrder();
    
    RadiologyOrder radiologyOrder2 = new RadiologyOrder();
    
    @Before
    public void setUp() throws Exception {
        
        patient.setUuid(PATIENT_UUID);
        radiologyOrder1.setPatient(patient);
        radiologyOrder2.setPatient(patient);
        List<RadiologyOrder> radiologyOrders = new ArrayList<RadiologyOrder>();
        radiologyOrders.add(radiologyOrder1);
        radiologyOrders.add(radiologyOrder2);
        
        when(patientResource.getByUniqueId(PATIENT_UUID)).thenReturn(patient);
        when(patientResource.getByUniqueId(UNKNOWN_PATIENT_UUID)).thenReturn(null);
        
        when(patientService.getPatientByUuid(PATIENT_UUID)).thenReturn(patient);
        when(patientService.getPatientByUuid(UNKNOWN_PATIENT_UUID)).thenReturn(null);
        
        PowerMockito.mockStatic(RestService.class);
        PowerMockito.mockStatic(Context.class);
        when(Context.getPatientService()).thenReturn(patientService);
        
        when(Context.getService(RestService.class)
                .getResourceBySupportedClass(Patient.class)).thenReturn(patientResource);
        
        when(Context.getPatientService()
                .getPatientByUuid(PATIENT_UUID)).thenReturn(patient);
        when(Context.getPatientService()
                .getPatientByUuid(UNKNOWN_PATIENT_UUID)).thenReturn(null);
        
        when(radiologyOrderService.getRadiologyOrdersByPatient(patient)).thenReturn(radiologyOrders);
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return all radiology orders for given patient
     */
    @Test
    public void search_shouldReturnAllRadiologyOrdersForGivenPatient() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, PATIENT_UUID);
        RequestContext requestContext = new RequestContext();
        requestContext.setRequest(request);
        
        PageableResult pageableResult = radiologyOrderSearchHandler.search(requestContext);
        // TODO test
    }
    
    /**
     * @see RadiologyOrderSearchHandler#search(RequestContext)
     * @verifies return empty search result if patient cannot be found
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfPatientCannotBeFound() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(RadiologyOrderSearchHandler.REQUEST_PARAM_PATIENT, UNKNOWN_PATIENT_UUID);
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
        // TODO auto-generated
        // Assert.fail("Not yet implemented");
    }
}
