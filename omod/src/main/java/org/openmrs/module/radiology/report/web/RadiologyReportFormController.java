/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.web;

import javax.validation.Valid;

import org.openmrs.Order;
import org.openmrs.module.radiology.dicom.DicomWebViewer;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.web.RadiologyOrderFormController;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.radiology.report.RadiologyReportValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the form handling entry, display, saving, unclaiming of {@code RadiologyReport's}.
 */
@Controller
@RequestMapping(value = RadiologyReportFormController.RADIOLOGY_REPORT_FORM_REQUEST_MAPPING)
public class RadiologyReportFormController {
    
    
    protected static final String RADIOLOGY_REPORT_FORM_REQUEST_MAPPING = "/module/radiology/radiologyReport.form";
    
    static final String RADIOLOGY_REPORT_FORM_VIEW = "/module/radiology/reports/radiologyReportForm";
    
    @Autowired
    private RadiologyReportService radiologyReportService;
    
    @Autowired
    private DicomWebViewer dicomWebViewer;
    
    @Autowired
    private RadiologyReportValidator radiologyReportValidator;
    
    @InitBinder("radiologyReport")
    protected void initBinderRadiologyReport(WebDataBinder webDataBinder) {
        webDataBinder.setValidator(radiologyReportValidator);
    }
    
    /**
     * Handles requests for creating a new {@code RadiologyReport} for a specific {@code RadiologyOrder}.
     * 
     * @param radiologyOrder the radiology order for which a radiology report will be created
     * @return the model and view containing new radiology report for given radiology order
     * @should populate model and view with new radiology report for given radiology order
     */
    @RequestMapping(method = RequestMethod.GET, params = "orderId")
    protected ModelAndView
            getRadiologyReportFormWithNewRadiologyReport(@RequestParam("orderId") RadiologyOrder radiologyOrder) {
        
        final RadiologyReport radiologyReport = radiologyReportService.createAndClaimRadiologyReport(radiologyOrder);
        return new ModelAndView(
                "redirect:" + RADIOLOGY_REPORT_FORM_REQUEST_MAPPING + "?radiologyReportId=" + radiologyReport.getId());
    }
    
    /**
     * Handles requests for getting existing {@code RadiologyReport's}.
     * 
     * @param radiologyReportId the radiology report which is requested
     * @return the model and view containing radiology report for given radiology report id
     * @should populate model and view with given radiology report
     */
    @RequestMapping(method = RequestMethod.GET, params = "radiologyReportId")
    protected ModelAndView getRadiologyReportFormWithExistingRadiologyReport(
            @RequestParam("radiologyReportId") RadiologyReport radiologyReport) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
        addObjectsToModelAndView(modelAndView, radiologyReport);
        return modelAndView;
    }
    
    /**
     * Handles requests for saving a {@code RadiologyReport} as draft.
     *
     * @param radiologyReport radiology report to be saved
     * @return the model and view containing saved radiology report
     * @should save given radiology report and populate model and view with it
     */
    @RequestMapping(method = RequestMethod.POST, params = "saveRadiologyReport")
    protected ModelAndView saveRadiologyReport(@ModelAttribute RadiologyReport radiologyReport) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
        radiologyReportService.saveRadiologyReport(radiologyReport);
        
        addObjectsToModelAndView(modelAndView, radiologyReport);
        return modelAndView;
    }
    
    /**
     * Handles requests for unclaiming a {@code RadiologyReport}.
     *
     * @param radiologyReport the radiology report to be unclaimed
     * @return the model and view with redirect to order form if unclaim was successful, otherwise stay on
     *         report form
     * @should redirect to radiology order form if unclaim was successful
     */
    @RequestMapping(method = RequestMethod.POST, params = "unclaimRadiologyReport")
    protected ModelAndView unclaimRadiologyReport(@ModelAttribute RadiologyReport radiologyReport) {
        
        radiologyReportService.unclaimRadiologyReport(radiologyReport);
        return new ModelAndView("redirect:" + RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_REQUEST_MAPPING + "?orderId="
                + radiologyReport.getRadiologyOrder()
                        .getOrderId());
    }
    
    /**
     * Handles requests for completing a {@code RadiologyReport}.
     *
     * @param radiologyReport the radiology report to be completed
     * @param bindingResult the binding result for the radiology report
     * @return the model and view for the order form if complete was successful, otherwise the
     *         model and view contains binding result errors
     * @should complete given radiology report if it is valid
     * @should not complete given radiology report if it is not valid
     */
    @RequestMapping(method = RequestMethod.POST, params = "completeRadiologyReport")
    protected ModelAndView completeRadiologyReport(@Valid @ModelAttribute RadiologyReport radiologyReport,
            BindingResult bindingResult) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
        
        if (bindingResult.hasErrors()) {
            addObjectsToModelAndView(modelAndView, radiologyReport);
            return modelAndView;
        }
        
        final RadiologyReport completedRadiologyReport = radiologyReportService.completeRadiologyReport(radiologyReport,
            radiologyReport.getPrincipalResultsInterpreter());
        addObjectsToModelAndView(modelAndView, completedRadiologyReport);
        return modelAndView;
    }
    
    /**
     * Convenience method to add objects (Order, RadiologyOrder, RadiologyReport) to given
     * ModelAndView
     *
     * @param modelAndView model and view to which objects should be added
     * @param radiologyReport radiology report from which objects should be added to the model and
     *        view
     */
    private void addObjectsToModelAndView(ModelAndView modelAndView, RadiologyReport radiologyReport) {
        
        modelAndView.addObject("radiologyReport", radiologyReport);
        modelAndView.addObject("order", (Order) radiologyReport.getRadiologyOrder());
        modelAndView.addObject("dicomViewerUrl", dicomWebViewer.getDicomViewerUrl(radiologyReport.getRadiologyOrder()
                .getStudy()));
        modelAndView.addObject("radiologyOrder", radiologyReport.getRadiologyOrder());
    }
}
