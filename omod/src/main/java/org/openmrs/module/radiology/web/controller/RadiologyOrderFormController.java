/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.web.controller;

import static org.openmrs.module.radiology.RadiologyRoles.SCHEDULER;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.dicom.DicomWebViewer;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.validator.RadiologyDiscontinuedOrderValidator;
import org.openmrs.module.radiology.validator.RadiologyOrderValidator;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_REQUEST_MAPPING)
public class RadiologyOrderFormController {
	
	protected static final String RADIOLOGY_ORDER_FORM_REQUEST_MAPPING = "/module/radiology/radiologyOrder.form";
	
	static final String RADIOLOGY_ORDER_FORM_VIEW = "/module/radiology/radiologyOrderForm";
	
	@Autowired
	private RadiologyService radiologyService;
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	@Autowired
	private DicomWebViewer dicomWebViewer;
	
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
			radiologyOrder.setStudy(new Study());
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
		ModelAndView modelAndView = getRadiologyOrderFormWithNewRadiologyOrder();
		
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
			modelAndView.addObject("discontinuationOrder", new Order());
			
			if (order instanceof RadiologyOrder) {
				final RadiologyOrder radiologyOrder = (RadiologyOrder) order;
				modelAndView.addObject("radiologyOrder", radiologyOrder);
				if (radiologyOrder.isCompleted()) {
					modelAndView.addObject("dicomViewerUrl", dicomWebViewer.getDicomViewerUrl(radiologyOrder.getStudy()));
				}
			}
			
			radiologyReportNeedsToBeCreated(modelAndView, order);
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
	 * @should set http session attribute openmrs message to saved fail worklist and redirect to
	 *         radiologyOrderForm when save study was not successful and given patient id
	 * @should set http session attribute openmrs message to study performed when study performed
	 *         status is in progress and request was issued by radiology scheduler
	 * @should not redirect if radiology order is not valid according to order validator
	 */
	@RequestMapping(method = RequestMethod.POST, params = "saveRadiologyOrder")
	protected ModelAndView postSaveRadiologyOrder(HttpServletRequest request,
			@RequestParam(value = "patient_id", required = false) Integer patientId, @ModelAttribute("order") Order order,
			@ModelAttribute("radiologyOrder") RadiologyOrder radiologyOrder, BindingResult radiologyOrderErrors)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDER_FORM_VIEW);
		
		User authenticatedUser = Context.getAuthenticatedUser();
		
		if (authenticatedUser.hasRole(SCHEDULER, true) && !radiologyOrder.getStudy()
				.isScheduleable()) {
			request.getSession()
					.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "radiology.studyPerformed");
		} else {
			new RadiologyOrderValidator().validate(radiologyOrder, radiologyOrderErrors);
			if (radiologyOrderErrors.hasErrors()) {
				return modelAndView;
			}
			
			try {
				radiologyService.placeRadiologyOrder(radiologyOrder);
				
				if (radiologyService.placeRadiologyOrderInPacs(radiologyOrder)) {
					request.getSession()
							.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.saved");
				} else {
					request.getSession()
							.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.savedFailWorklist");
				}
				
				modelAndView.setViewName("redirect:" + RADIOLOGY_ORDER_FORM_REQUEST_MAPPING + "?orderId="
						+ radiologyOrder.getOrderId());
			}
			catch (Exception ex) {
				request.getSession()
						.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		return modelAndView;
	}
	
	/**
	 * Handles POST requests for the radiologyOrderForm with request parameter attribute
	 * discontinueOrder
	 *
	 * @param radiologyOrderToDiscontinue order to discontinue
	 * @param nonCodedDiscontinueReason non coded discontinue reason
	 * @return model and view populated with discontinuation order
	 * @throws Exception
	 * @should discontinue non discontinued order and redirect to discontinuation order
	 * @should not redirect if discontinuation failed in pacs
	 */
	@RequestMapping(method = RequestMethod.POST, params = "discontinueOrder")
	protected ModelAndView postDiscontinueRadiologyOrder(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("orderId") RadiologyOrder radiologyOrderToDiscontinue,
			@ModelAttribute("discontinuationOrder") Order discontinuationOrder, BindingResult radiologyOrderErrors)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDER_FORM_VIEW);
		
		try {
			new RadiologyDiscontinuedOrderValidator().validate(discontinuationOrder, radiologyOrderErrors);
			if (radiologyOrderErrors.hasErrors()) {
				modelAndView.addObject("order", radiologyOrderToDiscontinue);
				modelAndView.addObject("radiologyOrder",
					radiologyService.getRadiologyOrderByOrderId(radiologyOrderToDiscontinue.getOrderId()));
				
				return modelAndView;
			}
			discontinuationOrder = radiologyService.discontinueRadiologyOrder(radiologyOrderToDiscontinue,
				discontinuationOrder.getOrderer(), discontinuationOrder.getOrderReasonNonCoded());
			
			if (radiologyService.discontinueRadiologyOrderInPacs(radiologyOrderToDiscontinue)) {
				request.getSession()
						.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.discontinuedSuccessfully");
				modelAndView.setViewName("redirect:" + RADIOLOGY_ORDER_FORM_REQUEST_MAPPING + "?orderId="
						+ discontinuationOrder.getOrderId());
			} else {
				request.getSession()
						.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "radiology.failWorklist");
			}
		}
		catch (APIException apiException) {
			request.getSession()
					.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, apiException.getMessage());
		}
		
		modelAndView.addObject("order", radiologyOrderToDiscontinue);
		modelAndView.addObject("radiologyOrder",
			radiologyService.getRadiologyOrderByOrderId(radiologyOrderToDiscontinue.getOrderId()));
		modelAndView.addObject("discontinuationOrder", discontinuationOrder);
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
		
		final RadiologyReport radiologyReport = radiologyService.getActiveRadiologyReportByRadiologyOrder(radiologyOrder);
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
		
		for (Modality modality : Modality.values()) {
			modalities.put(modality.name(), modality.getFullName());
		}
		
		return modalities;
	}
	
	@ModelAttribute("urgencies")
	private List<String> getUrgenciesList() {
		
		final List<String> urgencies = new LinkedList<String>();
		
		for (Order.Urgency urgency : Order.Urgency.values()) {
			urgencies.add(urgency.name());
		}
		
		return urgencies;
	}
	
	@ModelAttribute("scheduledProcedureStepStatuses")
	private Map<String, String> getScheduledProcedureStepStatusList() {
		
		final Map<String, String> scheduledProcedureStepStatuses = new LinkedHashMap<String, String>();
		scheduledProcedureStepStatuses.put("", "Select");
		
		for (ScheduledProcedureStepStatus scheduledProcedureStepStatus : ScheduledProcedureStepStatus.values()) {
			scheduledProcedureStepStatuses.put(scheduledProcedureStepStatus.name(), scheduledProcedureStepStatus.name());
		}
		
		return scheduledProcedureStepStatuses;
	}
	
	@ModelAttribute("performedStatuses")
	private Map<String, String> getPerformedStatusList() {
		
		final Map<String, String> performedStatuses = new HashMap<String, String>();
		performedStatuses.put("", "Select");
		
		for (PerformedProcedureStepStatus performedStatus : PerformedProcedureStepStatus.values()) {
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
