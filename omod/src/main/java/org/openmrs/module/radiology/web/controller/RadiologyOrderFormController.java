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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.propertyeditor.ProviderEditor;
import org.openmrs.module.radiology.validator.RadiologyOrderValidator;
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
public class RadiologyOrderFormController {
	
	private static final Log log = LogFactory.getLog(RadiologyOrderFormController.class);
	
	@Autowired
	private RadiologyService radiologyService;
	
	@Autowired
	private OrderService orderService;
	
	@InitBinder
	void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Provider.class, new ProviderEditor());
	}
	
	/**
	 * Handles GET requests for the radiologyOrderForm with new radiology order
	 * 
	 * @return model and view containing new radiology order
	 * @should populate model and view with new radiology order
	 */
	@RequestMapping(value = "/module/radiology/radiologyOrder.form", method = RequestMethod.GET)
	protected ModelAndView getRadiologyOrderFormWithNewRadiologyOrder() {
		ModelAndView modelAndView = new ModelAndView("module/radiology/radiologyOrderForm");
		
		if (Context.isAuthenticated()) {
			RadiologyOrder radiologyOrder = new RadiologyOrder();
			radiologyOrder.setStudy(new Study());
			modelAndView.addObject("order", new Order());
			modelAndView.addObject("isOrderActive", true);
			modelAndView.addObject("radiologyOrder", radiologyOrder);
		}
		
		return modelAndView;
	}
	
	/**
	 * Handles GET requests for the radiologyOrderForm with new radiology order with prefilled
	 * patient
	 * 
	 * @param patient existing patient which should be associated with a new radiology order
	 *            returned in the model and view
	 * @return model and view containing new radiology order
	 * @should populate model and view with new radiology order prefilled with given patient
	 * @should populate model and view with new radiology order without prefilled patient if given
	 *         patient is null
	 */
	@RequestMapping(value = "/module/radiology/radiologyOrder.form", method = RequestMethod.GET, params = "patientId")
	protected ModelAndView getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(
	        @RequestParam(value = "patientId", required = true) Patient patient) {
		ModelAndView modelAndView = getRadiologyOrderFormWithNewRadiologyOrder();
		
		if (Context.isAuthenticated() && patient != null) {
			Order order = (Order) modelAndView.getModel().get("radiologyOrder");
			order.setPatient(patient);
			modelAndView.addObject("patientId", patient.getPatientId());
		}
		
		return modelAndView;
	}
	
	/**
	 * Handles GET requests for the radiologyOrderForm with existing radiology order
	 * 
	 * @param orderId order id of an existing radiology order which should be put into the model and
	 *            view
	 * @return model and view containing radiology order
	 * @should populate model and view with existing radiology order matching given order id
	 */
	@RequestMapping(value = "/module/radiology/radiologyOrder.form", method = RequestMethod.GET, params = "orderId")
	protected ModelAndView getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(
	        @RequestParam(value = "orderId", required = true) Order order) {
		ModelAndView modelAndView = new ModelAndView("module/radiology/radiologyOrderForm");
		
		if (Context.isAuthenticated()) {
			RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(order.getOrderId());
			modelAndView.addObject("order", order);
			modelAndView.addObject("isOrderActive", order.isActive());
			modelAndView.addObject("radiologyOrder", radiologyOrder);
			modelAndView.addObject("discontinuationOrder", new Order());
		}
		
		return modelAndView;
	}
	
	/**
	 * Handles POST requests for the radiologyOrderForm with request parameter attribute
	 * saveRadiologyOrder
	 * 
	 * @param patientId patient id of an existing patient which is used to redirect to the patient
	 *            dashboard
	 * @param radiologyOrder radiology order object
	 * @param radiologyOrderErrors binding result containing order errors for a non valid order
	 * @return model and view
	 * @should set http session attribute openmrs message to order saved and redirect to radiology
	 *         order list when save study was successful
	 * @should set http session attribute openmrs message to order saved and redirect to patient
	 *         dashboard when save study was successful and given patient id
	 * @should set http session attribute openmrs message to saved fail worklist and redirect to
	 *         patient dashboard when save study was not successful and given patient id
	 * @should set http session attribute openmrs message to study performed when study performed
	 *         status is in progress and request was issued by radiology scheduler
	 * @should not redirect if radiology order is not valid according to order validator
	 */
	@RequestMapping(value = "/module/radiology/radiologyOrder.form", method = RequestMethod.POST, params = "saveRadiologyOrder")
	protected ModelAndView postSaveRadiologyOrder(HttpServletRequest request,
	        @RequestParam(value = "patient_id", required = false) Integer patientId, @ModelAttribute("order") Order order,
	        @ModelAttribute("radiologyOrder") RadiologyOrder radiologyOrder, BindingResult radiologyOrderErrors)
	        throws Exception {
		ModelAndView modelAndView = new ModelAndView("module/radiology/radiologyOrderForm");
		
		User authenticatedUser = Context.getAuthenticatedUser();
		
		if (authenticatedUser.hasRole(SCHEDULER, true) && !radiologyOrder.getStudy().isScheduleable()) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "radiology.studyPerformed");
		} else {
			new RadiologyOrderValidator().validate(radiologyOrder, radiologyOrderErrors);
			if (radiologyOrderErrors.hasErrors()) {
				modelAndView.addObject("isOrderActive", radiologyOrder.isActive());
				return modelAndView;
			}
			
			try {
				radiologyService.placeRadiologyOrder(radiologyOrder);
				
				radiologyService.sendModalityWorklist(radiologyOrder, OrderRequest.Save_Order);
				if (radiologyOrder.getStudy().getMwlStatus() == MwlStatus.SAVE_ERR
				        || radiologyOrder.getStudy().getMwlStatus() == MwlStatus.UPDATE_ERR) {
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.savedFailWorklist");
				} else {
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.saved");
				}
				
				if (patientId == null) {
					modelAndView.setViewName("redirect:/module/radiology/radiologyOrder.list");
				} else {
					modelAndView.setViewName("redirect:/patientDashboard.form?patientId=" + patientId);
				}
			}
			catch (Exception ex) {
				request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, ex.getMessage());
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
	 * @param discontinueDate discontinue date
	 * @return model and view populated with discontinuation order
	 * @throws Exception
	 * @should discontinue non discontinued order and redirect to discontinuation order
	 * @should not redirect if discontinuation failed through date in the future
	 * @should not redirect if discontinuation failed in pacs
	 */
	@RequestMapping(value = "/module/radiology/radiologyOrder.form", method = RequestMethod.POST, params = "discontinueOrder")
	protected ModelAndView postDiscontinueRadiologyOrder(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam("orderId") RadiologyOrder radiologyOrderToDiscontinue,
	        @ModelAttribute("discontinuationOrder") Order discontinuationOrder) throws Exception {
		ModelAndView modelAndView = new ModelAndView("module/radiology/radiologyOrderForm");
		
		try {
			discontinuationOrder = radiologyService.discontinueRadiologyOrder(radiologyOrderToDiscontinue,
			    discontinuationOrder.getOrderer(), discontinuationOrder.getDateActivated(), discontinuationOrder
			            .getOrderReasonNonCoded());
			
			radiologyService.sendModalityWorklist(radiologyOrderToDiscontinue, OrderRequest.Discontinue_Order);
			if (radiologyOrderToDiscontinue.getStudy().getMwlStatus() == MwlStatus.DISCONTINUE_OK) {
				request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.discontinuedSuccessfully");
				modelAndView.setViewName("redirect:/module/radiology/radiologyOrder.form?orderId="
				        + discontinuationOrder.getOrderId());
			} else {
				request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "radiology.failWorklist");
			}
		}
		catch (APIException apiException) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, apiException.getMessage());
		}
		
		modelAndView.addObject("order", radiologyOrderToDiscontinue);
		modelAndView.addObject("isOrderActive", radiologyOrderToDiscontinue.isActive());
		modelAndView.addObject("radiologyOrder", radiologyService.getRadiologyOrderByOrderId(radiologyOrderToDiscontinue
		        .getOrderId()));
		modelAndView.addObject("discontinuationOrder", discontinuationOrder);
		return modelAndView;
	}
	
	@ModelAttribute("modalities")
	private Map<String, String> getModalityList() {
		
		Map<String, String> modalities = new HashMap<String, String>();
		
		for (Modality modality : Modality.values()) {
			modalities.put(modality.name(), modality.getFullName());
		}
		
		return modalities;
	}
	
	@ModelAttribute("urgencies")
	private List<String> getUrgenciesList() {
		
		List<String> urgencies = new LinkedList<String>();
		
		for (Order.Urgency urgency : Order.Urgency.values()) {
			urgencies.add(urgency.name());
		}
		
		return urgencies;
	}
	
	@ModelAttribute("scheduledProcedureStepStatuses")
	private Map<String, String> getScheduledProcedureStepStatusList() {
		
		Map<String, String> scheduledProcedureStepStatuses = new LinkedHashMap<String, String>();
		scheduledProcedureStepStatuses.put("", "Select");
		
		for (ScheduledProcedureStepStatus scheduledProcedureStepStatus : ScheduledProcedureStepStatus.values()) {
			scheduledProcedureStepStatuses.put(scheduledProcedureStepStatus.name(), scheduledProcedureStepStatus.name());
		}
		
		return scheduledProcedureStepStatuses;
	}
	
	@ModelAttribute("performedStatuses")
	private Map<String, String> getPerformedStatusList() {
		
		Map<String, String> performedStatuses = new HashMap<String, String>();
		performedStatuses.put("", "Select");
		
		for (PerformedProcedureStepStatus performedStatus : PerformedProcedureStepStatus.values()) {
			performedStatuses.put(performedStatus.name(), performedStatus.name());
		}
		
		return performedStatuses;
	}
}
