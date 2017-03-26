/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.web;

import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.APIException;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.openmrs.module.radiology.report.RadiologyReportValidator;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.web.WebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyReportFormController}.
 */
public class RadiologyReportFormControllerTest extends BaseContextMockTest {
    
    
    @Mock
    private RadiologyReportService radiologyReportService;
    
    @Mock
    private RadiologyReportValidator radiologyReportValidator;
    
    @InjectMocks
    private RadiologyReportFormController radiologyReportFormController = new RadiologyReportFormController();
    
    @Test
    public void shouldCreateANewRadiologyReportForGivenRadiologyOrderAndRedirectToItsRadiologyReportForm() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        RadiologyOrder mockRadiologyOrder = mockRadiologyReport.getRadiologyOrder();
        
        when(radiologyReportService.createRadiologyReport(mockRadiologyOrder)).thenReturn(mockRadiologyReport);
        
        ModelAndView modelAndView = radiologyReportFormController.createRadiologyReport(mockRadiologyOrder);
        
        verify(radiologyReportService, times(1)).createRadiologyReport(mockRadiologyOrder);
        verifyNoMoreInteractions(radiologyReportService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyReport.form?reportId=" + mockRadiologyReport.getId()));
    }
    
    @Test
    public void shouldPopulateModelAndViewWithGivenRadiologyReport() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        
        ModelAndView modelAndView =
                radiologyReportFormController.getRadiologyReportFormWithExistingRadiologyReport(mockRadiologyReport);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyReportFormController.RADIOLOGY_REPORT_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
        
        RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap()
                .get("radiologyReport");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport, is(mockRadiologyReport));
        
        VoidRadiologyReportRequest voidRadiologyReportRequest = (VoidRadiologyReportRequest) modelAndView.getModelMap()
                .get("voidRadiologyReportRequest");
        assertNotNull(voidRadiologyReportRequest);
    }
    
    @Test
    public void
            shouldSaveGivenRadiologyReportAndSetHttpSessionAttributeOpenmrsMessageToReportDraftSavedAndRedirectToItsReportForm() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("saveRadiologyReportDraft", "saveRadiologyReportDraft");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        ModelAndView modelAndView = radiologyReportFormController.saveRadiologyReportDraft(mockRequest, mockRadiologyReport);
        
        verify(radiologyReportService, times(1)).saveRadiologyReportDraft(mockRadiologyReport);
        verifyNoMoreInteractions(radiologyReportService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyReport.form?reportId=" + mockRadiologyReport.getReportId()));
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR),
            is("radiology.RadiologyReport.savedDraft"));
    }
    
    @Test
    public void shouldNotRedirectAndSetSessionAttributeWithOpenmrsErrorIfApiExceptionIsThrownBySaveRadiologyReportDraft()
            throws Exception {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("saveRadiologyReportDraft", "saveRadiologyReportDraft");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        when(radiologyReportService.saveRadiologyReportDraft(mockRadiologyReport))
                .thenThrow(new APIException("RadiologyReport.cannot.saveDraft.already.reported"));
        
        ModelAndView modelAndView = radiologyReportFormController.saveRadiologyReportDraft(mockRequest, mockRadiologyReport);
        
        verify(radiologyReportService, times(1)).saveRadiologyReportDraft(mockRadiologyReport);
        verifyNoMoreInteractions(radiologyReportService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyReportFormController.RADIOLOGY_REPORT_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
        
        RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap()
                .get("radiologyReport");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport, is(mockRadiologyReport));
        
        VoidRadiologyReportRequest voidRadiologyReportRequest = (VoidRadiologyReportRequest) modelAndView.getModelMap()
                .get("voidRadiologyReportRequest");
        assertNotNull(voidRadiologyReportRequest);
        
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR),
            is("RadiologyReport.cannot.saveDraft.already.reported"));
    }
    
    /**
     * @see RadiologyReportFormController#voidRadiologyReport(HttpServletRequest, RadiologyReport, VoidRadiologyReportRequest, BindingResult)
     *         to its report form
     */
    @Test
    public void
            voidRadiologyReport_shouldVoidGivenRadiologyReportAndSetHttpSessionAttributeOpenmrsMessageToReportVoidedAndRedirectToItsReportForm() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        VoidRadiologyReportRequest voidRadiologyReportRequest = new VoidRadiologyReportRequest();
        voidRadiologyReportRequest.setVoidReason("selected wrong order");
        BindingResult bindingResult = mock(BindingResult.class);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("voidRadiologyReport", "voidRadiologyReport");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        ModelAndView modelAndView = radiologyReportFormController.voidRadiologyReport(mockRequest, mockRadiologyReport,
            voidRadiologyReportRequest, bindingResult);
        
        verify(radiologyReportService, times(1)).voidRadiologyReport(mockRadiologyReport,
            voidRadiologyReportRequest.getVoidReason());
        verifyNoMoreInteractions(radiologyReportService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyReport.form?reportId=" + mockRadiologyReport.getReportId()));
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("radiology.RadiologyReport.voided"));
    }
    
    @Test
    public void shouldNotVoidAndNotRedirectGivenInvalidRadiologyReportRequest() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        VoidRadiologyReportRequest voidRadiologyReportRequest = new VoidRadiologyReportRequest();
        voidRadiologyReportRequest.setVoidReason("selected wrong order");
        BindingResult bindingResult = mock(BindingResult.class);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("voidRadiologyReport", "voidRadiologyReport");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        when(bindingResult.hasErrors()).thenReturn(true);
        
        ModelAndView modelAndView = radiologyReportFormController.voidRadiologyReport(mockRequest, mockRadiologyReport,
            voidRadiologyReportRequest, bindingResult);
        
        verifyZeroInteractions(radiologyReportService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyReportFormController.RADIOLOGY_REPORT_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
        
        RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap()
                .get("radiologyReport");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport, is(mockRadiologyReport));
    }
    
    @Test
    public void shouldNotRedirectAndSetHttpSessionAttributeWithOpenmrsErrorIfApiExceptionIsThrownByVoidRadiologyReport() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        VoidRadiologyReportRequest voidRadiologyReportRequest = new VoidRadiologyReportRequest();
        voidRadiologyReportRequest.setVoidReason("selected wrong order");
        BindingResult bindingResult = mock(BindingResult.class);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("voidRadiologyReport", "voidRadiologyReport");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        when(radiologyReportService.voidRadiologyReport(mockRadiologyReport, voidRadiologyReportRequest.getVoidReason()))
                .thenThrow(new APIException("RadiologyReport.cannot.void.completed"));
        
        ModelAndView modelAndView = radiologyReportFormController.voidRadiologyReport(mockRequest, mockRadiologyReport,
            voidRadiologyReportRequest, bindingResult);
        
        verify(radiologyReportService, times(1)).voidRadiologyReport(mockRadiologyReport,
            voidRadiologyReportRequest.getVoidReason());
        verifyNoMoreInteractions(radiologyReportService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyReportFormController.RADIOLOGY_REPORT_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
        
        RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap()
                .get("radiologyReport");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport, is(mockRadiologyReport));
        
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR),
            is("RadiologyReport.cannot.void.completed"));
    }
    
    @Test
    public void
            shouldCompleteGivenRadiologyReportIfValidAndSetHttpSessionAttributeOpenmrsMessageToReportCompletedAndRedirectToItsReportForm() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        mockRadiologyReport.setPrincipalResultsInterpreter(RadiologyTestData.getMockProvider1());
        RadiologyReport mockCompletedRadiologyReport = mockRadiologyReport;
        mockCompletedRadiologyReport.setStatus(RadiologyReportStatus.COMPLETED);
        BindingResult reportErrors = mock(BindingResult.class);
        
        when(radiologyReportService.saveRadiologyReport(mockRadiologyReport)).thenReturn(mockCompletedRadiologyReport);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("completeRadiologyReport", "completeRadiologyReport");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        ModelAndView modelAndView =
                radiologyReportFormController.completeRadiologyReport(mockRequest, mockRadiologyReport, reportErrors);
        
        verify(radiologyReportService, times(1)).saveRadiologyReport(mockRadiologyReport);
        verifyNoMoreInteractions(radiologyReportService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyReport.form?reportId=" + mockCompletedRadiologyReport.getReportId()));
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR),
            is("radiology.RadiologyReport.completed"));
    }
    
    @Test
    public void shouldNotCompleteGivenRadiologyReportIfItIsNotValid() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        mockRadiologyReport.setPrincipalResultsInterpreter(null);
        
        BindingResult reportErrors = mock(BindingResult.class);
        when(reportErrors.hasErrors()).thenReturn(true);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("completeRadiologyReport", "completeRadiologyReport");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        ModelAndView modelAndView =
                radiologyReportFormController.completeRadiologyReport(mockRequest, mockRadiologyReport, reportErrors);
        
        verifyZeroInteractions(radiologyReportService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyReportFormController.RADIOLOGY_REPORT_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertNotNull(radiologyOrder);
        
        assertThat(mockRadiologyReport.getRadiologyOrder(), is(radiologyOrder));
        assertThat(modelAndView.getModelMap(), hasKey("radiologyReport"));
        RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap()
                .get("radiologyReport");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getStatus(), is(RadiologyReportStatus.DRAFT));
        
        VoidRadiologyReportRequest voidRadiologyReportRequest = (VoidRadiologyReportRequest) modelAndView.getModelMap()
                .get("voidRadiologyReportRequest");
        assertNotNull(voidRadiologyReportRequest);
    }
    
    @Test
    public void shouldNotRedirectAndSetSessionAttributeWithOpenmrsErrorIfApiExceptionIsThrownByCompleteRadiologyReport()
            throws Exception {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("completeRadiologyReport", "completeRadiologyReport");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult reportErrors = mock(BindingResult.class);
        
        when(radiologyReportService.saveRadiologyReport(mockRadiologyReport))
                .thenThrow(new APIException("RadiologyReport.cannot.complete.reported"));
        
        ModelAndView modelAndView =
                radiologyReportFormController.completeRadiologyReport(mockRequest, mockRadiologyReport, reportErrors);
        
        verify(radiologyReportService, times(1)).saveRadiologyReport(mockRadiologyReport);
        verifyNoMoreInteractions(radiologyReportService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyReportFormController.RADIOLOGY_REPORT_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
        
        RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap()
                .get("radiologyReport");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport, is(mockRadiologyReport));
        
        VoidRadiologyReportRequest voidRadiologyReportRequest = (VoidRadiologyReportRequest) modelAndView.getModelMap()
                .get("voidRadiologyReportRequest");
        assertNotNull(voidRadiologyReportRequest);
        
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR),
            is("RadiologyReport.cannot.complete.reported"));
    }
}
