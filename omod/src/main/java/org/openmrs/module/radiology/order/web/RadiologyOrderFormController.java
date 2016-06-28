/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order.web;

import static org.openmrs.module.radiology.RadiologyPrivileges.ADD_RADIOLOGY_REPORTS;
import static org.openmrs.module.radiology.RadiologyRoles.SCHEDULER;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.dicom.DicomWebViewer;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.dicom.code.ScheduledProcedureStepStatus;
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

@Controller
@RequestMapping(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_REQUEST_MAPPING)
public class RadiologyOrderFormController {
    
    
    public static final String RADIOLOGY_ORDER_FORM_REQUEST_MAPPING = "/module/radiology/radiologyOrder.form";
    
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
    private DiscontinuationOrderRequestValidator discontinuationOrderRequestValidator;
    
    @InitBinder("discontinuationOrderRequest")
    protected void initBinderDiscontinuationOrderRequest(WebDataBinder webDataBinder) {
        webDataBinder.setValidator(discontinuationOrderRequestValidator);
    }
    
    /**
     * Handles GET requests for the radiologyOrderForm with new radiology order
     * 
     * @return model and view containing new radiology order
     * @should populate model and view with new radiology order
     */
    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView getRadiologyOrderFormWithNewRadiologyOrder() {
        
        ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDER_FORM_VIEW);
        
        if (Context.isAuthenticated()) {
            modelAndView.addObject("order", new Order());
            modelAndView.addObject("radiologyReport", null);
            final RadiologyOrder radiologyOrder = new RadiologyOrder();
            radiologyOrder.setStudy(new RadiologyStudy());
            modelAndView.addObject("radiologyOrder", radiologyOrder);
        }
        
        return modelAndView;
    }
    
    /**
     * Handles GET requests for the radiologyOrderForm with new radiology order with prefilled
     * patient
     * 
     * @param patient existing patient which should be associated with a new radiology order
     *        returned in the model and view
     * @return model and view containing new radiology order
     * @should populate model and view with new radiology order prefilled with given patient
     * @should populate model and view with new radiology order without prefilled patient if given
     *         patient is null
     */
    @RequestMapping(method = RequestMethod.GET, params = "patientId")
    protected ModelAndView getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(
            @RequestParam(value = "patientId", required = true) Patient patient) {
        
        final ModelAndView modelAndView = getRadiologyOrderFormWithNewRadiologyOrder();
        
        if (Context.isAuthenticated() && patient != null) {
            Order order = (Order) modelAndView.getModel()
                    .get("radiologyOrder");
            order.setPatient(patient);
            modelAndView.addObject("patientId", patient.getPatientId());
        }
        
        return modelAndView;
    }
    
    /**
     * Handles GET requests for the radiologyOrderForm with existing radiology order
     * 
     * @param order Order of an existing radiology order which should be put into the model and view
     * @return model and view containing radiology order
     * @should populate model and view with existing radiology order if given order id matches a
     *         radiology order and no dicomViewerUrl if order is not completed
     * @should populate model and view with existing radiology order if given order id matches a
     *         radiology order and dicomViewerUrl if order completed
     * @should populate model and view with existing order if given order id only matches an order
     *         and not a radiology order
     */
    @RequestMapping(method = RequestMethod.GET, params = "orderId")
    protected ModelAndView getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(
            @RequestParam(value = "orderId", required = true) Order order) {
        ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDER_FORM_VIEW);
        
        if (Context.isAuthenticated()) {
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
        }
        
        return modelAndView;
    }
    
    /**
     * Handles POST requests for the radiologyOrderForm with request parameter attribute
     * saveRadiologyOrder
     *
     * @param patientId patient id of an existing patient which is used to redirect to the patient
     *        dashboard
     * @param radiologyOrder radiology order object
     * @param radiologyOrderErrors binding result containing order errors for a non valid order
     * @return model and view
     * @should set http session attribute openmrs message to order saved and redirect to to
     *         radiologyOrderForm when save study was successful
     * @should set http session attribute openmrs message to order saved and redirect to
     *         radiologyOrderForm when save study was successful and given patient id
     * @should set http session attribute openmrs message to study performed when study performed
     *         status is in progress and request was issued by radiology scheduler
     * @should not redirect if radiology order is not valid according to order validator
     */
    @RequestMapping(method = RequestMethod.POST, params = "saveRadiologyOrder")
    protected ModelAndView postSaveRadiologyOrder(HttpServletRequest request,
            @RequestParam(value = "patient_id", required = false) Integer patientId, @ModelAttribute("order") Order order,
            @ModelAttribute("radiologyOrder") RadiologyOrder radiologyOrder, BindingResult radiologyOrderErrors)
            throws Exception {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDER_FORM_VIEW);
        
        if (Context.getAuthenticatedUser()
                .hasRole(SCHEDULER, true)
                && !radiologyOrder.getStudy()
                        .isScheduleable()) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "radiology.studyPerformed");
        } else {
            new RadiologyOrderValidator().validate(radiologyOrder, radiologyOrderErrors);
            if (radiologyOrderErrors.hasErrors()) {
                return modelAndView;
            }
            
            try {
                radiologyOrderService.placeRadiologyOrder(radiologyOrder);
                
                request.getSession()
                        .setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.saved");
                modelAndView.setViewName(
                    "redirect:" + RADIOLOGY_ORDER_FORM_REQUEST_MAPPING + "?orderId=" + radiologyOrder.getOrderId());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                request.getSession()
                        .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, illegalArgumentException.getMessage());
            }
        }
        
        return modelAndView;
    }
    
    /**
     * Handles POST requests to discontinue a {@code RadiologyOrder}.
     * 
     * @param radiologyOrderToDiscontinue the radiology order to discontinue
     * @param discontinuationOrderRequest the discontinuation order request containing provider and reason
     * @return the model and view populated with discontinuation order
     * @throws Exception
     * @should discontinue non discontinued radiology order and redirect to discontinuation order
     * @should not discontinue given radiology order and not redirect if discontinuation order request is not valid
     */
    @RequestMapping(method = RequestMethod.POST, params = "discontinueOrder")
    protected ModelAndView postDiscontinueRadiologyOrder(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("orderId") RadiologyOrder radiologyOrderToDiscontinue,
            @Valid @ModelAttribute("discontinuationOrderRequest") DiscontinuationOrderRequest discontinuationOrderRequest,
            BindingResult resultDiscontinuationOrderRequest) throws Exception {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDER_FORM_VIEW);
        
        try {
            if (resultDiscontinuationOrderRequest.hasErrors()) {
                modelAndView.addObject("order", radiologyOrderToDiscontinue);
                modelAndView.addObject("radiologyOrder",
                    radiologyOrderService.getRadiologyOrder(radiologyOrderToDiscontinue.getOrderId()));
                
                return modelAndView;
            }
            Order discontinuationOrder = radiologyOrderService.discontinueRadiologyOrder(radiologyOrderToDiscontinue,
                discontinuationOrderRequest.getOrderer(), discontinuationOrderRequest.getReasonNonCoded());
            
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.discontinuedSuccessfully");
            modelAndView.setViewName(
                "redirect:" + RADIOLOGY_ORDER_FORM_REQUEST_MAPPING + "?orderId=" + discontinuationOrder.getOrderId());
        }
        catch (APIException apiException) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, apiException.getMessage());
        }
        
        modelAndView.addObject("order", radiologyOrderToDiscontinue);
        modelAndView.addObject("radiologyOrder",
            radiologyOrderService.getRadiologyOrder(radiologyOrderToDiscontinue.getOrderId()));
        return modelAndView;
    }
    
    /**
     * Convenient method to check if a radiologyReport needs to be created. Adds true to the
     * modelAndView, if a RadiologyReport needs to be created, otherwise false.
     *
     * @param modelAndView ModelAndView of the RadiologyOrderForm
     * @param order Order which should be verified if a RadiologyReport needs to be created
     * @return true if a RadiologyReport needs to be created, false otherwise
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
    
    @ModelAttribute("modalities")
    private Map<String, String> getModalityList() {
        
        final Map<String, String> modalities = new HashMap<String, String>();
        
        for (final Modality modality : Modality.values()) {
            modalities.put(modality.name(), modality.getFullName());
        }
        
        return modalities;
    }
    
    @ModelAttribute("urgencies")
    private List<String> getUrgenciesList() {
        
        final List<String> urgencies = new LinkedList<String>();
        
        for (final Order.Urgency urgency : Order.Urgency.values()) {
            urgencies.add(urgency.name());
        }
        
        return urgencies;
    }
    
    @ModelAttribute("scheduledProcedureStepStatuses")
    private Map<String, String> getScheduledProcedureStepStatusList() {
        
        final Map<String, String> scheduledProcedureStepStatuses = new LinkedHashMap<String, String>();
        scheduledProcedureStepStatuses.put("", "Select");
        
        for (final ScheduledProcedureStepStatus scheduledProcedureStepStatus : ScheduledProcedureStepStatus.values()) {
            scheduledProcedureStepStatuses.put(scheduledProcedureStepStatus.name(), scheduledProcedureStepStatus.name());
        }
        
        return scheduledProcedureStepStatuses;
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
     * Gets the Name of the ConceptClasses that should be filtered
     *
     * @return Name of ConceptClasses
     */
    @ModelAttribute("radiologyConceptClassNames")
    private String getRadiologyConceptClassNames() {
        return radiologyProperties.getRadiologyConceptClassNames();
    }
}
