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
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Order;
import org.openmrs.module.radiology.dicom.DicomWebViewer;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.openmrs.module.radiology.report.RadiologyReportValidator;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyReportFormController}.
 */
public class RadiologyReportFormControllerTest extends BaseContextMockTest {
    
    
    @Mock
    private RadiologyReportService radiologyReportService;
    
    @Mock
    private DicomWebViewer dicomWebViewer;
    
    @Mock
    private RadiologyReportValidator radiologyReportValidator;
    
    @InjectMocks
    private RadiologyReportFormController radiologyReportFormController = new RadiologyReportFormController();
    
    /**
     * @see RadiologyReportFormController#getRadiologyReportFormWithNewRadiologyReport(RadiologyOrder)
     * @verifies populate model and view with new radiology report for given radiology order
     */
    @Test
    public void
            getRadiologyReportFormWithNewRadiologyReport_shouldPopulateModelAndViewWithNewRadiologyReportForGivenRadiologyOrder() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        RadiologyOrder mockRadiologyOrder = mockRadiologyReport.getRadiologyOrder();
        
        when(radiologyReportService.createAndClaimRadiologyReport(mockRadiologyOrder)).thenReturn(mockRadiologyReport);
        
        ModelAndView modelAndView =
                radiologyReportFormController.getRadiologyReportFormWithNewRadiologyReport(mockRadiologyOrder);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyReport.form?radiologyReportId=" + mockRadiologyReport.getId()));
    }
    
    /**
     * @see RadiologyReportFormController#getRadiologyReportFormWithExistingRadiologyReport(RadiologyReport)
     * @verifies populate model and view with given radiology report
     */
    @Test
    public void getRadiologyReportFormWithExistingRadiologyReport_shouldPopulateModelAndViewWithGivenRadiologyReport() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        
        when(dicomWebViewer.getDicomViewerUrl(mockRadiologyReport.getRadiologyOrder()
                .getStudy())).thenReturn(
                    "http://localhost:8081/weasis-pacs-connector/viewer?studyUID=1.2.826.0.1.3680043.8.2186.1.1");
        
        ModelAndView modelAndView =
                radiologyReportFormController.getRadiologyReportFormWithExistingRadiologyReport(mockRadiologyReport);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyReportFormController.RADIOLOGY_REPORT_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("order"));
        Order order = (Order) modelAndView.getModelMap()
                .get("order");
        assertNotNull(order);
        assertThat(order, is((Order) mockRadiologyReport.getRadiologyOrder()));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
        
        RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap()
                .get("radiologyReport");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport, is(mockRadiologyReport));
        
        assertThat(modelAndView.getModelMap(), hasKey("dicomViewerUrl"));
        String dicomViewerUrl = (String) modelAndView.getModelMap()
                .get("dicomViewerUrl");
        assertThat(dicomViewerUrl,
            is("http://localhost:8081/weasis-pacs-connector/viewer?studyUID=1.2.826.0.1.3680043.8.2186.1.1"));
    }
    
    /**
     * @see RadiologyReportFormController#saveRadiologyReport(RadiologyReport)
     * @verifies save given radiology report and populate model and view with it
     */
    @Test
    public void saveRadiologyReport_shouldSaveGivenRadiologyReportAndPopulateModelAndViewWithIt() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        
        when(dicomWebViewer.getDicomViewerUrl(mockRadiologyReport.getRadiologyOrder()
                .getStudy())).thenReturn(
                    "http://localhost:8081/weasis-pacs-connector/viewer?studyUID=1.2.826.0.1.3680043.8.2186.1.1");
        
        ModelAndView modelAndView = radiologyReportFormController.saveRadiologyReport(mockRadiologyReport);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyReportFormController.RADIOLOGY_REPORT_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("order"));
        Order order = (Order) modelAndView.getModelMap()
                .get("order");
        assertNotNull(order);
        assertThat(order, is((Order) mockRadiologyReport.getRadiologyOrder()));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
        
        RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap()
                .get("radiologyReport");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport, is(mockRadiologyReport));
        
        assertThat(modelAndView.getModelMap(), hasKey("dicomViewerUrl"));
        String dicomViewerUrl = (String) modelAndView.getModelMap()
                .get("dicomViewerUrl");
        assertThat(dicomViewerUrl,
            is("http://localhost:8081/weasis-pacs-connector/viewer?studyUID=1.2.826.0.1.3680043.8.2186.1.1"));
    }
    
    /**
     * @verifies redirect to radiology order form if unclaim was successful
     * @see RadiologyReportFormController#unclaimRadiologyReport(RadiologyReport)
     */
    @Test
    public void unclaimRadiologyReport_shouldRedirectToRadiologyOrderFormIfUnclaimWasSuccessful() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        
        ModelAndView modelAndView = radiologyReportFormController.unclaimRadiologyReport(mockRadiologyReport);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyOrder.form?orderId=" + mockRadiologyReport.getRadiologyOrder()
                    .getOrderId()));
    }
    
    /**
     * @see RadiologyReportFormController#completeRadiologyReport(RadiologyReport,
     *      BindingResult)
     * @verifies complete given radiology report if it is valid
     */
    @Test
    public void completeRadiologyReport_shouldCompleteGivenRadiologyReportIfItIsValid() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        mockRadiologyReport.setPrincipalResultsInterpreter(RadiologyTestData.getMockProvider1());
        RadiologyReport mockCompletedRadiologyReport = mockRadiologyReport;
        mockCompletedRadiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
        BindingResult reportErrors = mock(BindingResult.class);
        
        when(radiologyReportService.completeRadiologyReport(mockRadiologyReport,
            mockRadiologyReport.getPrincipalResultsInterpreter())).thenReturn(mockCompletedRadiologyReport);
        
        ModelAndView modelAndView = radiologyReportFormController.completeRadiologyReport(mockRadiologyReport, reportErrors);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyReportFormController.RADIOLOGY_REPORT_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("order"));
        Order order = (Order) modelAndView.getModelMap()
                .get("order");
        assertNotNull(order);
        assertThat(order, is((Order) mockRadiologyReport.getRadiologyOrder()));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertNotNull(radiologyOrder);
        assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
        
        RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap()
                .get("radiologyReport");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport, is(mockRadiologyReport));
    }
    
    /**
     * @see RadiologyReportFormController#completeRadiologyReport(RadiologyReport,
     *      BindingResult)
     * @verifies not complete given radiology report if it is not valid
     */
    @Test
    public void completeRadiologyReport_shouldNotCompleteGivenRadiologyReportIfItIsNotValid() {
        
        // given
        RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
        mockRadiologyReport.setPrincipalResultsInterpreter(null);
        
        BindingResult reportErrors = mock(BindingResult.class);
        
        when(reportErrors.hasErrors()).thenReturn(true);
        
        ModelAndView modelAndView = radiologyReportFormController.completeRadiologyReport(mockRadiologyReport, reportErrors);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyReportFormController.RADIOLOGY_REPORT_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("order"));
        Order order = (Order) modelAndView.getModelMap()
                .get("order");
        assertNotNull(order);
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertNotNull(radiologyOrder);
        
        assertThat(mockRadiologyReport.getRadiologyOrder(), is(radiologyOrder));
        assertThat(modelAndView.getModelMap(), hasKey("radiologyReport"));
        RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap()
                .get("radiologyReport");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getReportStatus(), is(RadiologyReportStatus.CLAIMED));
    }
}
