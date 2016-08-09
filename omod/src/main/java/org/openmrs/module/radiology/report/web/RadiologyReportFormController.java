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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.openmrs.api.APIException;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.radiology.report.RadiologyReportValidator;
import org.openmrs.web.WebConstants;
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
    private RadiologyReportValidator radiologyReportValidator;
    
    @Autowired
    private VoidRadiologyReportRequestValidator voidRadiologyReportRequestValidator;
    
    @InitBinder("radiologyReport")
    protected void initBinderRadiologyReport(WebDataBinder webDataBinder) {
        webDataBinder.setValidator(radiologyReportValidator);
    }
    
    @InitBinder("voidRadiologyReportRequest")
    protected void initBinderVoidRadiologyReportRequest(WebDataBinder webDataBinder) {
        webDataBinder.setValidator(voidRadiologyReportRequestValidator);
    }
    
    /**
     * Handles requests for creating a new {@code RadiologyReport} for a {@code RadiologyOrder}.
     * 
     * @param radiologyOrder the radiology order for which a radiology report will be created
     * @return the model and view redirecting to the newly created radiology report
     * @should create a new radiology report for given radiology order and redirect to its radiology report form
     */
    @RequestMapping(method = RequestMethod.GET, params = "orderId")
    protected ModelAndView createRadiologyReport(@RequestParam("orderId") RadiologyOrder radiologyOrder) {
        
        final RadiologyReport radiologyReport = radiologyReportService.createRadiologyReport(radiologyOrder);
        return new ModelAndView(
                "redirect:" + RADIOLOGY_REPORT_FORM_REQUEST_MAPPING + "?reportId=" + radiologyReport.getId());
    }
    
    /**
     * Handles requests for getting existing {@code RadiologyReport's}.
     * 
     * @param radiologyReport the radiology report which is requested
     * @return the model and view containing radiology report for given radiology report id
     * @should populate model and view with given radiology report
     */
    @RequestMapping(method = RequestMethod.GET, params = "reportId")
    protected ModelAndView
            getRadiologyReportFormWithExistingRadiologyReport(@RequestParam("reportId") RadiologyReport radiologyReport) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
        addObjectsToModelAndView(modelAndView, radiologyReport);
        modelAndView.addObject(new VoidRadiologyReportRequest());
        return modelAndView;
    }
    
    /**
     * Handles requests for saving a {@code RadiologyReport} as draft.
     *
     * @param request the http servlet request
     * @param radiologyReport the radiology report to be saved
     * @return the model and view containing saved radiology report draft
     * @should save given radiology report and set http session attribute openmrs message to report draft saved and redirect
     *         to its report form
     * @should not redirect and set session attribute with openmrs error if api exception is thrown by save radiology
     *         report draft
     */
    @RequestMapping(method = RequestMethod.POST, params = "saveRadiologyReportDraft")
    protected ModelAndView saveRadiologyReportDraft(HttpServletRequest request,
            @ModelAttribute RadiologyReport radiologyReport) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
        
        try {
            radiologyReportService.saveRadiologyReportDraft(radiologyReport);
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.RadiologyReport.savedDraft");
            modelAndView.setViewName(
                "redirect:" + RADIOLOGY_REPORT_FORM_REQUEST_MAPPING + "?reportId=" + radiologyReport.getReportId());
            return modelAndView;
        }
        catch (APIException apiException) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, apiException.getMessage());
        }
        
        addObjectsToModelAndView(modelAndView, radiologyReport);
        modelAndView.addObject(new VoidRadiologyReportRequest());
        return modelAndView;
    }
    
    /**
     * Handles requests for voiding a {@code RadiologyReport}.
     *
     * @param request the http servlet request
     * @param radiologyReport the radiology report to be voided
     * @param voidRadiologyReportRequest the void radiology report request holding the void reason
     * @param bindingResult the binding result for the void radiology report request
     * @return the model and view with redirect to report form if voiding was successful, otherwise the
     *         model and view contains binding result errors
     * @should void given radiology report and set http session attribute openmrs message to report voided and redirect
     *         to its report form
     * @should not void and not redirect given invalid void radiology report request
     * @should not redirect and set session attribute with openmrs error if api exception is thrown by void radiology
     *         report
     */
    @RequestMapping(method = RequestMethod.POST, params = "voidRadiologyReport")
    protected ModelAndView voidRadiologyReport(HttpServletRequest request,
            @RequestParam("reportId") RadiologyReport radiologyReport,
            @Valid @ModelAttribute VoidRadiologyReportRequest voidRadiologyReportRequest, BindingResult bindingResult) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
        
        if (bindingResult.hasErrors()) {
            addObjectsToModelAndView(modelAndView, radiologyReport);
            return modelAndView;
        }
        
        try {
            radiologyReportService.voidRadiologyReport(radiologyReport, voidRadiologyReportRequest.getVoidReason());
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.RadiologyReport.voided");
            modelAndView.setViewName(
                "redirect:" + RADIOLOGY_REPORT_FORM_REQUEST_MAPPING + "?reportId=" + radiologyReport.getReportId());
            return modelAndView;
        }
        catch (APIException apiException) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, apiException.getMessage());
        }
        
        addObjectsToModelAndView(modelAndView, radiologyReport);
        return modelAndView;
    }
    
    /**
     * Handles requests for completing a {@code RadiologyReport}.
     *
     * @param request the http servlet request
     * @param radiologyReport the radiology report to be completed
     * @param bindingResult the binding result for the radiology report
     * @return the model and view with redirect to report form if complete was successful, otherwise the
     *         model and view contains binding result errors
     * @should complete given radiology report if valid and set http session attribute openmrs message to report completed and redirect
     *         to its report form
     * @should not complete and redirect given invalid radiology report
     * @should not redirect and set session attribute with openmrs error if api exception is thrown by complete radiology
     *         report
     */
    @RequestMapping(method = RequestMethod.POST, params = "completeRadiologyReport")
    protected ModelAndView completeRadiologyReport(HttpServletRequest request,
            @Valid @ModelAttribute RadiologyReport radiologyReport, BindingResult bindingResult) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
        
        if (bindingResult.hasErrors()) {
            addObjectsToModelAndView(modelAndView, radiologyReport);
            modelAndView.addObject(new VoidRadiologyReportRequest());
            return modelAndView;
        }
        
        try {
            radiologyReportService.saveRadiologyReport(radiologyReport);
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.RadiologyReport.completed");
            modelAndView.setViewName(
                "redirect:" + RADIOLOGY_REPORT_FORM_REQUEST_MAPPING + "?reportId=" + radiologyReport.getReportId());
            return modelAndView;
        }
        catch (APIException apiException) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, apiException.getMessage());
        }
        addObjectsToModelAndView(modelAndView, radiologyReport);
        modelAndView.addObject(new VoidRadiologyReportRequest());
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
        modelAndView.addObject("radiologyOrder", radiologyReport.getRadiologyOrder());
    }
}
