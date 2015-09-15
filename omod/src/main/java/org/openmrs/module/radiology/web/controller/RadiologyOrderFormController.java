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

import static org.openmrs.module.radiology.RadiologyRoles.PERFORMING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.READING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.REFERRRING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.SCHEDULER;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.RequestedProcedurePriority;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.EncounterEditor;
import org.openmrs.propertyeditor.OrderTypeEditor;
import org.openmrs.propertyeditor.PatientEditor;
import org.openmrs.propertyeditor.UserEditor;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.validator.OrderValidator;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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
	
	// private Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private RadiologyService radiologyService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MessageSourceService messageSourceService;
	
	@InitBinder
	void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(OrderType.class, new OrderTypeEditor());
		binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor("t", "f", true));
		binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true));
		binder.registerCustomEditor(User.class, new UserEditor());
		binder.registerCustomEditor(Patient.class, new PatientEditor());
		binder.registerCustomEditor(Encounter.class, new EncounterEditor());
	}
	
	/**
	 * Handles GET requests for the radiologyOrderForm with new order and study
	 * 
	 * @return model and view containing new order and study objects
	 * @should populate model and view with new order and study
	 * @should populate model and view with new order and study with prefilled orderer when
	 *         requested by referring physician
	 */
	@RequestMapping(value = "/module/radiology/radiologyOrder.form", method = RequestMethod.GET)
	protected ModelAndView getRadiologyOrderFormWithNewOrder() {
		ModelAndView modelAndView = new ModelAndView("module/radiology/radiologyOrderForm");
		
		if (Context.isAuthenticated()) {
			Study study = new Study();
			RadiologyOrder order = new RadiologyOrder();
			
			User user = Context.getAuthenticatedUser();
			if (user.hasRole(REFERRRING_PHYSICIAN, true) && order.getOrderer() == null) {
				order.setOrderer(user);
			}
			
			modelAndView.addObject("order", order);
			modelAndView.addObject("study", study);
		}
		
		return modelAndView;
	}
	
	/**
	 * Handles GET requests for the radiologyOrderForm with new order and study with prefilled
	 * patient
	 * 
	 * @param patient existing patient which should be associated with a new order returned in the
	 *            model and view
	 * @return model and view containing new order and study objects
	 * @should populate model and view with new order and study prefilled with given patient
	 * @should populate model and view with new order and study with prefilled orderer when
	 *         requested by referring physician
	 * @should populate model and view with new order and study without prefilled patient if given
	 *         patient is null
	 */
	@RequestMapping(value = "/module/radiology/radiologyOrder.form", method = RequestMethod.GET, params = "patientId")
	protected ModelAndView getRadiologyOrderFormWithNewOrderAndPrefilledPatient(
	        @RequestParam(value = "patientId", required = true) Patient patient) {
		ModelAndView modelAndView = getRadiologyOrderFormWithNewOrder();
		
		if (Context.isAuthenticated() && patient != null) {
			Order order = (Order) modelAndView.getModel().get("order");
			order.setPatient(patient);
			modelAndView.addObject("patientId", patient.getPatientId());
		}
		
		return modelAndView;
	}
	
	/**
	 * Handles GET requests for the radiologyOrderForm with existing order and study
	 * 
	 * @param orderId order id of an existing order which should be put into the model and view
	 * @return model and view containing order and study objects
	 * @should populate model and view with existing order and study matching given order id
	 */
	@RequestMapping(value = "/module/radiology/radiologyOrder.form", method = RequestMethod.GET, params = "orderId")
	protected ModelAndView getRadiologyOrderFormWithExistingOrderByOrderId(
	        @RequestParam(value = "orderId", required = true) Integer orderId) {
		ModelAndView modelAndView = new ModelAndView("module/radiology/radiologyOrderForm");
		
		if (Context.isAuthenticated()) {
			Study study = radiologyService.getStudyByOrderId(orderId);
			RadiologyOrder order = radiologyService.getRadiologyOrderByOrderId(orderId);
			modelAndView.addObject("order", order);
			modelAndView.addObject("study", study);
		}
		
		return modelAndView;
	}
	
	/**
	 * Handles POST requests for the radiologyOrderForm with request parameter attribute saveOrder
	 * 
	 * @param patientId patient id of an existing patient which is used to redirect to the patient
	 *            dashboard
	 * @param study study object
	 * @param sErrors binding result containing study errors for a non valid study
	 * @param order order object
	 * @param oErrors binding result containing order errors for a non valid order
	 * @return model and view
	 * @should set http session attribute openmrs message to order saved and redirect to radiology
	 *         order list when save study was successful
	 * @should set http session attribute openmrs message to order saved and redirect to patient
	 *         dashboard when save study was successful and given patient id
	 * @should set http session attribute openmrs message to saved fail worklist and redirect to
	 *         patient dashboard when save study was not successful and given patient id
	 * @should set http session attribute openmrs message to study performed when study performed
	 *         status is in progress and scheduler is empty and request was issued by radiology
	 *         scheduler
	 * @should not redirect if order is not valid according to order validator
	 */
	@RequestMapping(value = "/module/radiology/radiologyOrder.form", method = RequestMethod.POST, params = "saveOrder")
	protected ModelAndView postSaveOrder(HttpServletRequest request,
	        @RequestParam(value = "patient_id", required = false) Integer patientId, @ModelAttribute("study") Study study,
	        BindingResult sErrors, @ModelAttribute("order") RadiologyOrder order, BindingResult oErrors) throws Exception {
		ModelAndView modelAndView = new ModelAndView("module/radiology/radiologyOrderForm");
		
		order.setOrderType(RadiologyProperties.getRadiologyTestOrderType());
		
		User authenticatedUser = Context.getAuthenticatedUser();
		if (order.getOrderer() == null)
			order.setOrderer(authenticatedUser);
		
		if (authenticatedUser.hasRole(SCHEDULER, true) && study.getScheduler() == null && !study.isScheduleable()) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "radiology.studyPerformed");
			populate(modelAndView, order, study);
		} else {
			new OrderValidator().validate(order, oErrors);
			if (oErrors.hasErrors()) {
				populate(modelAndView, order, study);
				return modelAndView;
			}
			
			try {
				radiologyService.saveRadiologyOrder(order);
				study.setRadiologyOrder(order);
				Study savedStudy = radiologyService.saveStudy(study);
				order.setStudy(study);
				
				radiologyService.sendModalityWorklist(order, OrderRequest.Save_Order);
				
				savedStudy = radiologyService.getStudy(savedStudy.getStudyId());
				if (savedStudy.getMwlStatus() == MwlStatus.SAVE_ERR || savedStudy.getMwlStatus() == MwlStatus.UPDATE_ERR) {
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
				populate(modelAndView, order, study);
				
			}
		}
		
		return modelAndView;
	}
	
	/**
	 * Handles POST requests for the radiologyOrderForm with request parameter attributes
	 * <p>
	 * <ul>
	 * <li>voidOrder
	 * <li>unvoidOrder
	 * <li>discontinueOrder
	 * <li>undiscontinueOrder
	 * </ul>
	 * <p>
	 * 
	 * @param patientId patient id of an existing patient which is used to redirect to the patient
	 *            dashboard
	 * @param study study object
	 * @param sErrors binding result containing study errors for a non valid study
	 * @param order order object
	 * @param oErrors binding result containing order errors for a non valid order
	 * @return model and view
	 * @should set http session attribute openmrs message to voided successfully and redirect to
	 *         patient dashboard when void order was successful and given patient id
	 * @should set http session attribute openmrs message to unvoided successfully and redirect to
	 *         patient dashboard when unvoid order was successful and given patient id
	 * @should set http session attribute openmrs message to discontinued successfully and redirect
	 *         to patient dashboard when discontinue order was successful and given patient id
	 * @should set http session attribute openmrs message to undiscontinued successfully and
	 *         redirect to patient dashboard when undiscontinue order was successful and given
	 *         patient id
	 */
	@RequestMapping(value = "/module/radiology/radiologyOrder.form", method = RequestMethod.POST)
	protected ModelAndView post(HttpServletRequest request,
	        @RequestParam(value = "patient_id", required = false) Integer patientId, @ModelAttribute("study") Study study,
	        BindingResult sErrors, @ModelAttribute("order") Order order, BindingResult oErrors) throws Exception {
		ModelAndView modelAndView = new ModelAndView("module/radiology/radiologyOrderForm");
		
		boolean ok = executeCommand(order, request);
		if (ok) {
			if (patientId == null)
				modelAndView.setViewName("redirect:/module/radiology/radiologyOrder.list");
			else
				modelAndView.setViewName("redirect:/patientDashboard.form?patientId=" + patientId);
		} else {
			populate(modelAndView, order, study);
		}
		
		return modelAndView;
	}
	
	private void populate(ModelAndView mav, Order order, Study study) {
		if (Context.isAuthenticated()) {
			mav.addObject("order", order);
			mav.addObject("study", study);
		}
	}
	
	@ModelAttribute("modalities")
	private Map<String, String> getModalityList() {
		
		Map<String, String> modalities = new HashMap<String, String>();
		
		for (Modality modality : Modality.values()) {
			modalities.put(modality.name(), modality.getFullName());
		}
		
		return modalities;
	}
	
	@ModelAttribute("requestedProcedurePriorities")
	private List<String> getRequestedProcedurePriorityList() {
		
		List<String> requestedProcedurePriorities = new LinkedList<String>();
		
		for (RequestedProcedurePriority requestedProcedurePriority : RequestedProcedurePriority.values()) {
			requestedProcedurePriorities.add(requestedProcedurePriority.name());
		}
		
		return requestedProcedurePriorities;
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
	
	/**
	 * Return true if the current user is authenticated as a referring physician
	 * 
	 * @return true if the current user is authenticated as a referring physician
	 * @should return true if the current user is authenticated as a referring physician
	 * @should return false if the current user is not authenticated as a referring physician
	 * @should throw api authentication exception if the current user is not authenticated
	 */
	@ModelAttribute("isUserReferringPhysician")
	private boolean isUserReferringPhysician() {
		
		if (!Context.isAuthenticated()) {
			String[] args = { OpenmrsConstants.PRIV_EDIT_ORDERS };
			String message = messageSourceService.getMessage("radiology.privilegesRequired", args, Context.getLocale());
			throw new APIAuthenticationException(message);
		}
		return Context.getAuthenticatedUser().hasRole(REFERRRING_PHYSICIAN, true);
	}
	
	/**
	 * Return true if the current user is authenticated as a scheduler
	 * 
	 * @return true if the current user is authenticated as a scheduler
	 * @should return true if the current user is authenticated as a scheduler
	 * @should return false if the current user is not authenticated as a scheduler
	 * @should throw api authentication exception if the current user is not authenticated
	 */
	@ModelAttribute("isUserScheduler")
	private boolean isUserScheduler() {
		
		if (!Context.isAuthenticated()) {
			String[] args = { OpenmrsConstants.PRIV_EDIT_ORDERS };
			String message = messageSourceService.getMessage("radiology.privilegesRequired", args, Context.getLocale());
			throw new APIAuthenticationException(message);
		}
		return Context.getAuthenticatedUser().hasRole(SCHEDULER, true);
	}
	
	/**
	 * Return true if the current user is authenticated as a performing physician
	 * 
	 * @return true if the current user is authenticated as a performing physician
	 * @should return true if the current user is authenticated as a performing physician
	 * @should return false if the current user is not authenticated as a performing physician
	 * @should throw api authentication exception if the current user is not authenticated
	 */
	@ModelAttribute("isUserPerformingPhysician")
	private boolean isUserPerformingPhysician() {
		
		if (!Context.isAuthenticated()) {
			String[] args = { OpenmrsConstants.PRIV_EDIT_ORDERS };
			String message = messageSourceService.getMessage("radiology.privilegesRequired", args, Context.getLocale());
			throw new APIAuthenticationException(message);
		}
		return Context.getAuthenticatedUser().hasRole(PERFORMING_PHYSICIAN, true);
	}
	
	/**
	 * Return true if the current user is authenticated as a reading physician
	 * 
	 * @return true if the current user is authenticated as a reading physician
	 * @should return true if the current user is authenticated as a reading physician
	 * @should return false if the current user is not authenticated as a reading physician
	 * @should throw api authentication exception if the current user is not authenticated
	 */
	@ModelAttribute("isUserReadingPhysician")
	private boolean isUserReadingPhysician() {
		
		if (!Context.isAuthenticated()) {
			String[] args = { OpenmrsConstants.PRIV_EDIT_ORDERS };
			String message = messageSourceService.getMessage("radiology.privilegesRequired", args, Context.getLocale());
			throw new APIAuthenticationException(message);
		}
		return Context.getAuthenticatedUser().hasRole(READING_PHYSICIAN, true);
	}
	
	/**
	 * Return true if the current user is authenticated as a super user
	 * 
	 * @return true if the current user is authenticated as a super user
	 * @should return true if the current user is authenticated as a super user
	 * @should return false if the current user is not authenticated as a super user
	 * @should throw api authentication exception if the current user is not authenticated
	 */
	@ModelAttribute("isUserSuper")
	private boolean isUserSuper() {
		
		if (!Context.isAuthenticated()) {
			String[] args = { OpenmrsConstants.PRIV_EDIT_ORDERS };
			String message = messageSourceService.getMessage("radiology.privilegesRequired", args, Context.getLocale());
			throw new APIAuthenticationException(message);
		}
		return Context.getAuthenticatedUser().isSuperUser();
	}
	
	protected boolean executeCommand(Order order, HttpServletRequest request) {
		if (!Context.isAuthenticated()) {
			return false;
		}
		
		try {
			if (request.getParameter("voidOrder") != null) {
				RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(order.getOrderId());
				radiologyService.sendModalityWorklist(radiologyOrder, OrderRequest.Void_Order);
				if (radiologyService.getStudyByOrderId(radiologyOrder.getOrderId()).getMwlStatus() == MwlStatus.VOID_OK) {
					orderService.voidOrder(radiologyOrder, order.getVoidReason());
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.voidedSuccessfully");
				} else {
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.failWorklist");
				}
			} else if (request.getParameter("unvoidOrder") != null) {
				RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(order.getOrderId());
				radiologyService.sendModalityWorklist(radiologyOrder, OrderRequest.Unvoid_Order);
				if (radiologyService.getStudyByOrderId(radiologyOrder.getOrderId()).getMwlStatus() == MwlStatus.UNVOID_OK) {
					orderService.unvoidOrder(radiologyOrder);
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.unvoidedSuccessfully");
				} else {
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.failWorklist");
				}
			} else if (request.getParameter("discontinueOrder") != null) {
				RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(order.getOrderId());
				radiologyService.sendModalityWorklist(radiologyOrder, OrderRequest.Discontinue_Order);
				if (radiologyService.getStudyByOrderId(radiologyOrder.getOrderId()).getMwlStatus() == MwlStatus.DISCONTINUE_OK) {
					orderService
					        .discontinueOrder(radiologyOrder, order.getDiscontinuedReason(), order.getDiscontinuedDate());
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.discontinuedSuccessfully");
				} else {
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.failWorklist");
				}
			} else if (request.getParameter("undiscontinueOrder") != null) {
				RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(order.getOrderId());
				radiologyService.sendModalityWorklist(radiologyOrder, OrderRequest.Undiscontinue_Order);
				if (radiologyService.getStudyByOrderId(radiologyOrder.getOrderId()).getMwlStatus() == MwlStatus.UNDISCONTINUE_OK) {
					orderService.undiscontinueOrder(radiologyOrder);
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Order.undiscontinuedSuccessfully");
				} else {
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.failWorklist");
				}
			}
		}
		catch (Exception ex) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, ex.getMessage());
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
}
