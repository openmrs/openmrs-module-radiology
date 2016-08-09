/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order.web;

import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.radiology.dicom.DicomWebViewer;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * Tests {@link RadiologyOrderDetailsPortletController}.
 */
public class RadiologyOrderDetailsPortletControllerTest extends BaseContextMockTest {
    
    
    private static final String RADIOLOGY_ORDER_UUID = "44f24d7e-ebbd-4500-bfba-1db19561ca04";
    
    private static final String DICOM_VIEWER_URL =
            "http://localhost:8081/weasis-pacs-connector/viewer?studyUID=1.2.826.0.1.3680043.8.2186.1.1";
    
    @Mock
    private RadiologyOrderService radiologyOrderService;
    
    @Mock
    private DicomWebViewer dicomWebViewer;
    
    @InjectMocks
    private RadiologyOrderDetailsPortletController radiologyOrderDetailsPortletController =
            new RadiologyOrderDetailsPortletController();
    
    /**
     * @see RadiologyOrderDetailsPortletController#populateModel(HttpServletRequest,Map)
     * @verifies populate model with radiology order if given order uuid model entry matches a radiology order and dicom viewer url if radiology order is completed
     */
    @Test
    public void
            populateModel_shouldPopulateModelWithRadiologyOrderIfGivenOrderUuidModelEntryMatchesARadiologyOrderAndDicomViewerUrlIfRadiologyOrderIsCompleted()
                    throws Exception {
        
        RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        when(radiologyOrderService.getRadiologyOrderByUuid(RADIOLOGY_ORDER_UUID)).thenReturn(mockRadiologyOrder);
        when(dicomWebViewer.getDicomViewerUrl(mockRadiologyOrder.getStudy())).thenReturn(DICOM_VIEWER_URL);
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("orderUuid", RADIOLOGY_ORDER_UUID);
        
        radiologyOrderDetailsPortletController.populateModel(mockRequest, model);
        
        assertThat(model, hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) model.get("radiologyOrder");
        assertThat(radiologyOrder, is(mockRadiologyOrder));
        
        assertThat(model, hasKey("dicomViewerUrl"));
        String dicomViewerUrl = (String) model.get("dicomViewerUrl");
        assertThat(dicomViewerUrl, is(DICOM_VIEWER_URL));
    }
    
    /**
     * @see RadiologyOrderDetailsPortletController#populateModel(HttpServletRequest,Map)
     * @verifies populate model with radiology order if given order uuid model entry matches a radiology order and no dicom viewer url if radiology order is not completed
     */
    @Test
    public void
            populateModel_shouldPopulateModelWithRadiologyOrderIfGivenOrderUuidModelEntryMatchesARadiologyOrderAndNoDicomViewerUrlIfRadiologyOrderIsNotCompleted()
                    throws Exception {
        
        RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
        mockRadiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        when(radiologyOrderService.getRadiologyOrderByUuid(RADIOLOGY_ORDER_UUID)).thenReturn(mockRadiologyOrder);
        when(dicomWebViewer.getDicomViewerUrl(mockRadiologyOrder.getStudy())).thenReturn(DICOM_VIEWER_URL);
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("orderUuid", RADIOLOGY_ORDER_UUID);
        
        radiologyOrderDetailsPortletController.populateModel(mockRequest, model);
        
        assertThat(model, hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) model.get("radiologyOrder");
        assertThat(radiologyOrder, is(mockRadiologyOrder));
        
        assertThat(model, not(hasKey("dicomViewerUrl")));
    }
    
    /**
     * @see RadiologyOrderDetailsPortletController#populateModel(HttpServletRequest,Map)
     * @verifies not populate model with radiology order and dicom viewer url if no radiology order was found
     */
    @Test
    public void populateModel_shouldNotPopulateModelWithRadiologyOrderAndDicomViewerUrlIfNoRadiologyOrderWasFound()
            throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        when(radiologyOrderService.getRadiologyOrderByUuid("wrong_uuid")).thenReturn(null);
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("orderUuid", "wrong_uuid");
        
        radiologyOrderDetailsPortletController.populateModel(mockRequest, model);
        
        assertThat(model, not(hasKey("radiologyOrder")));
        assertThat(model, not(hasKey("dicomViewerUrl")));
    }
    
    /**
     * @see RadiologyOrderDetailsPortletController#populateModel(HttpServletRequest,Map)
     * @verifies not populate model with radiology order and dicom viewer url if model has no entry for order uuid
     */
    @Test
    public void populateModel_shouldNotPopulateModelWithRadiologyOrderAndDicomViewerUrlIfModelHasNoEntryForOrderUuid()
            throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        radiologyOrderDetailsPortletController.populateModel(mockRequest, model);
        
        assertThat(model, not(hasKey("radiologyOrder")));
        assertThat(model, not(hasKey("dicomViewerUrl")));
    }
}
