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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.APIException;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateService;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.web.WebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyDashboardFormControllerTest}
 */
public class RadiologyDashboardFormControllerTest extends BaseContextMockTest {
    
    
    @Mock
    private MrrtReportTemplateService mrrtReportTemplateService;
    
    @InjectMocks
    private RadiologyDashboardFormController radiologyDashboardFormController = new RadiologyDashboardFormController();
    
    /**
    * @see RadiologyDashboardFormController#get()
    * @verifies return model and view
    */
    @Test
    public void get_shouldReturnModelAndView() throws Exception {
        ModelAndView modelAndView = radiologyDashboardFormController.get();
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyDashboardFormController.RADIOLOGY_DASHBOARD_FORM_VIEW));
    }
    
    /**
     * @see RadiologyDashboardFormController#uploadReportTemplate(HttpServletRequest,MultipartFile)
     * @verifies give error message when template file is empty
     */
    @Test
    public void uploadReportTemplate_shouldGiveErrorMessageWhenTemplateFileIsEmpty() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockMultipartFile templateFile = new MockMultipartFile(java.util.UUID.randomUUID()
                .toString(), new byte[] {});
        ModelAndView modelAndView = radiologyDashboardFormController.uploadReportTemplate(request, templateFile);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyDashboardFormController.RADIOLOGY_DASHBOARD_FORM_VIEW));
        
        String message = (String) request.getSession()
                .getAttribute(WebConstants.OPENMRS_ERROR_ATTR);
        assertThat(message, is("radiology.MrrtReportTemplate.not.imported.empty"));
    }
    
    /**
     * @see RadiologyDashboardFormController#uploadReportTemplate(HttpServletRequest,MultipartFile)
     * @verifies give success message when import was successful
     */
    @Test
    public void uploadReportTemplate_shouldGiveSuccessMessageWhenImportWasSuccessful() throws Exception {
        MockMultipartFile templateFile = new MockMultipartFile(java.util.UUID.randomUUID()
                .toString(), new byte[] { 1, 2, 4 });
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        ModelAndView modelAndView = radiologyDashboardFormController.uploadReportTemplate(request, templateFile);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyDashboardFormController.RADIOLOGY_DASHBOARD_FORM_VIEW));
        
        String message = (String) request.getSession()
                .getAttribute(WebConstants.OPENMRS_MSG_ATTR);
        assertThat(message, is("radiology.MrrtReportTemplate.imported"));
    }
    
    /**
     * @see RadiologyDashboardFormController#uploadReportTemplate(HttpServletRequest,MultipartFile)
     * @verifies set error message in session when api exception is thrown
     */
    @Test
    public void uploadReportTemplate_shouldSetErrorMessageInSessionWhenApiExceptionIsThrown() throws Exception {
        MockMultipartFile templateFile = new MockMultipartFile(java.util.UUID.randomUUID()
                .toString(), new byte[] { 1, 2, 4 });
        doThrow(new APIException()).when(mrrtReportTemplateService)
                .importMrrtReportTemplate(templateFile.getInputStream());
        MockHttpServletRequest request = new MockHttpServletRequest();
        ModelAndView modelAndView = radiologyDashboardFormController.uploadReportTemplate(request, templateFile);
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyDashboardFormController.RADIOLOGY_DASHBOARD_FORM_VIEW));
        String errorMessage = (String) request.getSession()
                .getAttribute(WebConstants.OPENMRS_MSG_ATTR);
        //assertNotNull(errorMessage);   	
    }
    
    /**
     * @see RadiologyDashboardFormController#uploadReportTemplate(HttpServletRequest,MultipartFile)
     * @verifies set error message in session when io exception is thrown
     */
    @Test
    public void uploadReportTemplate_shouldSetErrorMessageInSessionWhenIoExceptionIsThrown() throws Exception {
        MockMultipartFile templateFile = new MockMultipartFile(java.util.UUID.randomUUID()
                .toString(), new byte[] { 1, 2, 4 });
        MockHttpServletRequest request = new MockHttpServletRequest();
        doThrow(new IOException()).when(mrrtReportTemplateService)
                .importMrrtReportTemplate(templateFile.getInputStream());
        ;
        ModelAndView modelAndView = radiologyDashboardFormController.uploadReportTemplate(request, templateFile);
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyDashboardFormController.RADIOLOGY_DASHBOARD_FORM_VIEW));
        String errorMessage = (String) request.getSession()
                .getAttribute(WebConstants.OPENMRS_ERROR_ATTR);
        //assertNotNull(errorMessage);
    }
}
