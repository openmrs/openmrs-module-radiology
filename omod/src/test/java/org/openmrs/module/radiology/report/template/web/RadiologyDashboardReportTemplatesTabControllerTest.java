/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template.web;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.APIException;
import org.openmrs.module.radiology.report.template.MrrtReportTemplate;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateService;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateValidationException;
import org.openmrs.module.radiology.report.template.ValidationError;
import org.openmrs.module.radiology.report.template.ValidationResult;
import org.openmrs.module.radiology.web.RadiologyWebConstants;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.web.WebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyDashboardReportTemplatesTabController}.
 */
public class RadiologyDashboardReportTemplatesTabControllerTest extends BaseContextMockTest {
    
    
    private final static String MOCK_TEMPLATE_CONTENT = "<html>my template</html>";
    
    @Mock
    private MrrtReportTemplateService mrrtReportTemplateService;
    
    @InjectMocks
    private RadiologyDashboardReportTemplatesTabController radiologyDashboardReportTemplatesTabController =
            new RadiologyDashboardReportTemplatesTabController();
    
    InputStream inputStream;
    
    MockMultipartFile multipartFile;
    
    MockHttpServletRequest request;
    
    @Before
    public void setUp() throws IOException {
        
        inputStream = IOUtils.toInputStream(MOCK_TEMPLATE_CONTENT);
        multipartFile = new MockMultipartFile("mrrtReportTemplate", "mrrtReportTemplate.html", "html", inputStream);
        request = new MockHttpServletRequest();
    }
    
    @Test
    public void
            shouldReturnModelAndViewOfTheRadiologyReportTemplatesTabPageAndSetTabSessionAttributeToRadiologyReportsTabPage()
                    throws Exception {
        
        MockHttpSession mockSession = new MockHttpSession();
        request.setSession(mockSession);
        
        ModelAndView modelAndView = radiologyDashboardReportTemplatesTabController.getRadiologyReportTemplatesTab(request);
        
        verifyZeroInteractions(mrrtReportTemplateService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is(RadiologyDashboardReportTemplatesTabController.RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW));
        assertThat(mockSession.getAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE),
            is(RadiologyDashboardReportTemplatesTabController.RADIOLOGY_REPORT_TEMPLATES_TAB_REQUEST_MAPPING));
    }
    
    @Test
    public void shouldGiveSuccessMessageWhenImportWasSuccessful() throws Exception {
        
        ModelAndView modelAndView =
                radiologyDashboardReportTemplatesTabController.uploadReportTemplate(request, multipartFile);
        
        verify(mrrtReportTemplateService).importMrrtReportTemplate(MOCK_TEMPLATE_CONTENT);
        verifyNoMoreInteractions(mrrtReportTemplateService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is(RadiologyDashboardReportTemplatesTabController.RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW));
        String message = (String) request.getSession()
                .getAttribute(WebConstants.OPENMRS_MSG_ATTR);
        assertThat(message, is("radiology.MrrtReportTemplate.imported"));
    }
    
    /**
     * @see RadiologyDashboardReportTemplatesTabController#uploadReportTemplate(HttpServletRequest,MultipartFile)
     */
    @Test
    public void shouldGiveErrorMessageWhenTemplateFileIsEmpty() throws Exception {
        
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);
        
        ModelAndView modelAndView = radiologyDashboardReportTemplatesTabController.uploadReportTemplate(request, emptyFile);
        
        verifyZeroInteractions(mrrtReportTemplateService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is(RadiologyDashboardReportTemplatesTabController.RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW));
        
        String message = (String) request.getSession()
                .getAttribute(WebConstants.OPENMRS_ERROR_ATTR);
        assertThat(message, is("radiology.MrrtReportTemplate.not.imported.empty"));
    }
    
    @Test
    public void shouldSetErrorMessageInSessionWhenMrrtReportTemplateValidationExceptionIsThrown() throws Exception {
        
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError(new ValidationError("Missing header", "err.missing.header"));
        MrrtReportTemplateValidationException mrrtReportTemplateValidationException =
                new MrrtReportTemplateValidationException(validationResult);
        doThrow(mrrtReportTemplateValidationException).when(mrrtReportTemplateService)
                .importMrrtReportTemplate(MOCK_TEMPLATE_CONTENT);
        
        ModelAndView modelAndView =
                radiologyDashboardReportTemplatesTabController.uploadReportTemplate(request, multipartFile);
        
        verify(mrrtReportTemplateService).importMrrtReportTemplate(MOCK_TEMPLATE_CONTENT);
        verifyNoMoreInteractions(mrrtReportTemplateService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is(RadiologyDashboardReportTemplatesTabController.RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW));
        String errorMessage = (String) request.getSession()
                .getAttribute(WebConstants.OPENMRS_ERROR_ATTR);
        assertNotNull(errorMessage);
        assertThat(errorMessage, is("Failed to import mrrtReportTemplate.html"));
        assertThat(modelAndView.getModelMap()
                .get("mrrtReportTemplateValidationErrors"),
            is(validationResult.getErrors()));
    }
    
    @Test
    public void shouldSetErrorMessageInSessionWhenApiExceptionIsThrown() throws Exception {
        
        doThrow(new APIException("Cannot import the same template twice.")).when(mrrtReportTemplateService)
                .importMrrtReportTemplate(MOCK_TEMPLATE_CONTENT);
        
        ModelAndView modelAndView =
                radiologyDashboardReportTemplatesTabController.uploadReportTemplate(request, multipartFile);
        
        verify(mrrtReportTemplateService).importMrrtReportTemplate(MOCK_TEMPLATE_CONTENT);
        verifyNoMoreInteractions(mrrtReportTemplateService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is(RadiologyDashboardReportTemplatesTabController.RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW));
        String errorMessage = (String) request.getSession()
                .getAttribute(WebConstants.OPENMRS_ERROR_ATTR);
        assertNotNull(errorMessage);
        assertThat(errorMessage, is("Failed to import mrrtReportTemplate.html => Cannot import the same template twice."));
    }
    
    @Test
    public void shouldSetErrorMessageInSessionWhenIoExceptionIsThrown() throws Exception {
        
        doThrow(new IOException("File could not be read.")).when(mrrtReportTemplateService)
                .importMrrtReportTemplate(MOCK_TEMPLATE_CONTENT);
        
        ModelAndView modelAndView =
                radiologyDashboardReportTemplatesTabController.uploadReportTemplate(request, multipartFile);
        
        verify(mrrtReportTemplateService).importMrrtReportTemplate(MOCK_TEMPLATE_CONTENT);
        verifyNoMoreInteractions(mrrtReportTemplateService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is(RadiologyDashboardReportTemplatesTabController.RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW));
        String errorMessage = (String) request.getSession()
                .getAttribute(WebConstants.OPENMRS_ERROR_ATTR);
        assertNotNull(errorMessage);
        assertThat(errorMessage, is("Failed to import mrrtReportTemplate.html => File could not be read."));
    }
    
    @Test
    public void
            deleteMrrtReportTemplate_shouldReturnAModelAndViewOfTheRadiologyDashboardReportTemplatesPageWithAStatusMessage() {
        
        MockHttpSession mockSession = new MockHttpSession();
        MrrtReportTemplate mockTemplate = mock(MrrtReportTemplate.class);
        request.setSession(mockSession);
        
        ModelAndView modelAndView =
                radiologyDashboardReportTemplatesTabController.deleteMrrtReportTemplate(request, mockTemplate);
        
        verify(mrrtReportTemplateService).purgeMrrtReportTemplate(mockTemplate);
        verifyNoMoreInteractions(mrrtReportTemplateService);
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is(RadiologyDashboardReportTemplatesTabController.RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW));
        assertThat(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("radiology.MrrtReportTemplate.deleted"));
    }
    
    @Test
    public void shouldCatchApiExceptionAndSetErrorMessageInSession() throws Exception {
        
        MrrtReportTemplate mockTemplate = mock(MrrtReportTemplate.class);
        doThrow(new APIException("File could not be deleted.")).when(mrrtReportTemplateService)
                .purgeMrrtReportTemplate(mockTemplate);
        
        ModelAndView modelAndView =
                radiologyDashboardReportTemplatesTabController.deleteMrrtReportTemplate(request, mockTemplate);
        
        verify(mrrtReportTemplateService).purgeMrrtReportTemplate(mockTemplate);
        verifyNoMoreInteractions(mrrtReportTemplateService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is(RadiologyDashboardReportTemplatesTabController.RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW));
        String errorMessage = (String) request.getSession()
                .getAttribute(WebConstants.OPENMRS_ERROR_ATTR);
        assertNotNull(errorMessage);
        assertThat(errorMessage, is("Failed to delete template file => File could not be deleted."));
    }
}
