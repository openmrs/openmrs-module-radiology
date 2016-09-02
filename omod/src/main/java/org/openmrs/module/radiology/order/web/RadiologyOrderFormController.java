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

import static org.openmrs.module.radiology.RadiologyPrivileges.ADD_RADIOLOGY_REPORTS;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.dicom.DicomWebViewer;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.module.radiology.order.RadiologyOrderValidator;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.radiology.study.RadiologyStudy;
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
 * Controller for the form handling entry, display, discontinuation of {@code RadiologyOrder's}.
 */
@Controller
@RequestMapping(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_REQUEST_MAPPING)
public class RadiologyOrderFormController {
    
    
    public static final String RADIOLOGY_ORDER_FORM_REQUEST_MAPPING = "/module/radiology/radiologyOrder.form";
    
    static final String RADIOLOGY_ORDER_CREATION_FORM_VIEW = "/module/radiology/orders/radiologyOrderCreationForm";
    
    static final String RADIOLOGY_ORDER_FORM_VIEW = "/module/radiology/orders/radiologyOrderForm";
    
    @Autowired
    private RadiologyOrderService radiologyOrderService;
    
    @Autowired
    private RadiologyReportService radiologyReportService;
    
    @Autowired
    private RadiologyProperties radiologyProperties;
    
    @Autowired
    private DicomWebViewer dicomWebViewer;
    
    @Autowired
    private RadiologyOrderValidator radiologyOrderValidator;
    
    @Autowired
    private DiscontinuationOrderRequestValidator discontinuationOrderRequestValidator;
    
    @InitBinder("discontinuationOrderRequest")
    protected void initBinderDiscontinuationOrderRequest(WebDataBinder webDataBinder) {
        webDataBinder.setValidator(discontinuationOrderRequestValidator);
    }
    
    /**
     * Handles requests for a new {@code RadiologyOrder}.
     * 
     * @return model and view containing new radiology order
     * @should populate model and view with new radiology order
     */
    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView getRadiologyOrderFormWithNewRadiologyOrder() {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDER_CREATION_FORM_VIEW);
        modelAndView.addObject("order", new Order());
        modelAndView.addObject("radiologyReport", null);
        final RadiologyOrder radiologyOrder = new RadiologyOrder();
        radiologyOrder.setStudy(new RadiologyStudy());
        modelAndView.addObject("radiologyOrder", radiologyOrder);
        return modelAndView;
    }
    
    /**
     * Handles requests for a new {@code RadiologyOrder} for a specific patient.
     * 
     * @param patient the existing patient which should be associated with a new radiology order
     *        returned in the model and view
     * @return model and view containing new radiology order
     * @should populate model and view with new radiology order prefilled with given patient
     */
    @RequestMapping(method = RequestMethod.GET, params = "patientId")
    protected ModelAndView
            getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(@RequestParam("patientId") Patient patient) {
        
        final ModelAndView modelAndView = getRadiologyOrderFormWithNewRadiologyOrder();
        final Order order = (Order) modelAndView.getModel()
                .get("radiologyOrder");
        order.setPatient(patient);
        modelAndView.addObject("patientId", patient.getPatientId());
        return modelAndView;
    }
    
    /**
     * Handles requests for getting existing {@code RadiologyOrder's}.
     * 
     * @param order the order of an existing radiology order which should be returned
     * @return model and view containing radiology order
     * @should populate model and view with existing radiology order if given order id matches a
     *         radiology order and no dicom viewer url if order is not completed
     * @should populate model and view with existing radiology order if given order id matches a
     *         radiology order and dicom viewer url if order completed
     * @should populate model and view with existing order if given order id only matches an order
     *         and not a radiology order
     */
    @RequestMapping(method = RequestMethod.GET, params = "orderId")
    protected ModelAndView getRadiologyOrderFormWithExistingRadiologyOrder(@RequestParam("orderId") Order order) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDER_FORM_VIEW);
        modelAndView.addObject("order", order);
        modelAndView.addObject("discontinuationOrderRequest", new DiscontinuationOrderRequest());
        
        if (order instanceof RadiologyOrder) {
            final RadiologyOrder radiologyOrder = (RadiologyOrder) order;
            modelAndView.addObject("radiologyOrder", radiologyOrder);
            if (radiologyOrder.isCompleted()) {
                modelAndView.addObject("dicomViewerUrl", dicomWebViewer.getDicomViewerUrl(radiologyOrder.getStudy()));
            }
        }
        
        if (Context.getAuthenticatedUser()
                .hasPrivilege(ADD_RADIOLOGY_REPORTS)) {
            radiologyReportNeedsToBeCreated(modelAndView, order);
        }
        
        return modelAndView;
    }
    
    /**
     * Handles requests for saving a new {@code RadiologyOrder}.
     *
     * @param request the http servlet request issued to save the radiology order
     * @param radiologyOrder the radiology order to be saved
     * @param resultRadiologyOrder the binding result for given radiology order
     * @return the model and view for the radiology order form containing binding result errors if given radiology order is
     *         not valid
     * @should save given radiology order if valid and set http session attribute openmrs message to order saved and redirect
     *         to the new radiology order
     * @should not save given radiology order if it is not valid and not redirect
     * @should not redirect and set session attribute with openmrs error if api exception is thrown by place radiology
     *         order
     */
    @RequestMapping(method = RequestMethod.POST, params = "saveRadiologyOrder")
    protected ModelAndView saveRadiologyOrder(HttpServletRequest request, @ModelAttribute RadiologyOrder radiologyOrder,
            BindingResult resultRadiologyOrder) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDER_CREATION_FORM_VIEW);
        
        radiologyOrderValidator.validate(radiologyOrder, resultRadiologyOrder);
        if (resultRadiologyOrder.hasErrors()) {
            modelAndView.addObject("order", (Order) radiologyOrder);
            modelAndView.addObject("radiologyOrder", radiologyOrder);
            return modelAndView;
        }
        
        try {
            radiologyOrderService.placeRadiologyOrder(radiologyOrder);
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.saved");
            modelAndView.setViewName(
                "redirect:" + RADIOLOGY_ORDER_FORM_REQUEST_MAPPING + "?orderId=" + radiologyOrder.getOrderId());
            return modelAndView;
        }
        catch (APIException apiException) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, apiException.getMessage());
        }
        
        modelAndView.addObject("order", (Order) radiologyOrder);
        modelAndView.addObject("radiologyOrder", radiologyOrder);
        return modelAndView;
    }
    
    /**
     * Handles requests to discontinue a {@code RadiologyOrder}.
     * 
     * @param request the http servlet request issued to discontinue the radiology order
     * @param radiologyOrderToDiscontinue the radiology order to discontinue
     * @param discontinuationOrderRequest the discontinuation order request containing provider and reason
     * @param resultDiscontinuationOrderRequest the binding result for given discontinuation order request
     * @return the model and view populated with discontinuation order
     * @throws Exception
     * @should discontinue non discontinued radiology order and redirect to discontinuation order
     * @should not discontinue given radiology order and not redirect if discontinuation order request is not valid
     * @should not redirect and set session attribute with openmrs error if api exception is thrown by discontinue radiology
     *         order
     */
    @RequestMapping(method = RequestMethod.POST, params = "discontinueOrder")
    protected ModelAndView discontinueRadiologyOrder(HttpServletRequest request,
            @RequestParam("orderId") RadiologyOrder radiologyOrderToDiscontinue,
            @Valid @ModelAttribute DiscontinuationOrderRequest discontinuationOrderRequest,
            BindingResult resultDiscontinuationOrderRequest) throws Exception {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDER_FORM_VIEW);
        
        if (resultDiscontinuationOrderRequest.hasErrors()) {
            modelAndView.addObject("order", radiologyOrderToDiscontinue);
            modelAndView.addObject("radiologyOrder", radiologyOrderToDiscontinue);
            return modelAndView;
        }
        
        try {
            final Order discontinuationOrder = radiologyOrderService.discontinueRadiologyOrder(radiologyOrderToDiscontinue,
                discontinuationOrderRequest.getOrderer(), discontinuationOrderRequest.getReasonNonCoded());
            
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.discontinuedSuccessfully");
            modelAndView.setViewName(
                "redirect:" + RADIOLOGY_ORDER_FORM_REQUEST_MAPPING + "?orderId=" + discontinuationOrder.getOrderId());
            return modelAndView;
        }
        catch (APIException apiException) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, apiException.getMessage());
        }
        
        modelAndView.addObject("order", radiologyOrderToDiscontinue);
        modelAndView.addObject("radiologyOrder", radiologyOrderToDiscontinue);
        return modelAndView;
    }
    
    /**
     * Convenient method to check if a {@code RadiologyReport} needs to be created for a {@code RadiologyOrder}.
     *
     * @param modelAndView the model and view to which an object indicating if a report needs to be created is added
     * @param order the order to be checked for the need of a radiology report
     * @return true if a radiology report needs to be created and false otherwise
     * @should return false if order is not a radiology order
     * @should return false if radiology order is not completed
     * @should return false if radiology order is completed but has a claimed report
     * @should return false if radiology order is completed but has a completed report
     * @should return true if radiology order is completed and has no claimed report
     */
    private boolean radiologyReportNeedsToBeCreated(ModelAndView modelAndView, Order order) {
        
        final RadiologyOrder radiologyOrder;
        if (order instanceof RadiologyOrder) {
            radiologyOrder = (RadiologyOrder) order;
        } else {
            modelAndView.addObject("radiologyReportNeedsToBeCreated", false);
            return false;
        }
        
        if (radiologyOrder.isNotCompleted()) {
            modelAndView.addObject("radiologyReportNeedsToBeCreated", false);
            return false;
        }
        
        final RadiologyReport radiologyReport =
                radiologyReportService.getActiveRadiologyReportByRadiologyOrder(radiologyOrder);
        if (radiologyReport == null) {
            modelAndView.addObject("radiologyReportNeedsToBeCreated", true);
            return true;
        } else {
            modelAndView.addObject("radiologyReportNeedsToBeCreated", false);
            modelAndView.addObject("radiologyReport", radiologyReport);
            return false;
        }
    }
    
    @ModelAttribute("urgencies")
    private List<String> getUrgenciesList() {
        
        final List<String> urgencies = new LinkedList<String>();
        
        for (final Order.Urgency urgency : Order.Urgency.values()) {
            urgencies.add(urgency.name());
        }
        
        return urgencies;
    }
    
    @ModelAttribute("performedStatuses")
    private Map<String, String> getPerformedStatusList() {
        
        final Map<String, String> performedStatuses = new HashMap<String, String>();
        performedStatuses.put("", "Select");
        
        for (final PerformedProcedureStepStatus performedStatus : PerformedProcedureStepStatus.values()) {
            performedStatuses.put(performedStatus.name(), performedStatus.name());
        }
        
        return performedStatuses;
    }
    
    /**
     * Gets the names of the concept classes that should be filtered
     *
     * @return names of concept classes
     */
    @ModelAttribute("radiologyConceptClassNames")
    private String getRadiologyConceptClassNames() {
        return radiologyProperties.getRadiologyConceptClassNames();
    }
    
    /**
     * Gets the names of the concept classes that should be filtered for the order reason field
     *
     * @return names of concept classes containing concepts for the order reason field
     */
    @ModelAttribute("radiologyOrderReasonConceptClassNames")
    private String getRadiologyOrderReasonConceptClassNames() {
        return radiologyProperties.getRadiologyOrderReasonConceptClassNames();
    }
}
