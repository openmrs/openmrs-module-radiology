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

import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.validator.RadiologyReportValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/module/radiology/radiologyReport.form")
public class RadiologyReportFormController {
	
	@Autowired
	private RadiologyService radiologyService;
	
	private final String RADIOLOGY_REPORT_FORM_PATH = "module/radiology/radiologyReportForm";
	
	private final String RADIOLOGY_ORDER_FORM_URL = "redirect:/module/radiology/radiologyOrder.form?";
	
	private final String RADIOLOGY_REPORT_FORM_URL = "redirect:/module/radiology/radiologyReport.form?";
	
	private final String RADIOLOGY_ORDER_LIST_PATH = "redirect:/module/radiology/radiologyOrder.list";
	
	/**
	 * Handles GET requests for the radiologyReportForm
	 *
	 * @param order Order
	 * @param radiologyReportId Integer
	 * @return model and view containing a RadiologyReport
	 * @should populate ModelAndView RadiologyReportForm containing a RadiologyReport for a
	 *         RadiologyOrder
	 * @should populate ModelAndView RadiologyReportForm containing a new created RadiologyReport
	 *         for an RadiologyOrder if radiologyReportId is null
	 * @should populate ModelAndView RadiologyOrderListForm if radiologyReport could not has been
	 *         created
	 * @should populate ModelAndView RadiologyOrderListForm if the given order does not match with
	 *         the radiologyOrder of the creted radiologyReport
	 */
	@RequestMapping(method = RequestMethod.GET)
	protected ModelAndView getRadiologyReport(@RequestParam(value = "orderId", required = true) Order order,
	        @RequestParam(value = "radiologyReportId", required = false) Integer radiologyReportId) {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_PATH);
		if (Context.isAuthenticated()) {
			RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(order.getOrderId());
			
			RadiologyReport radiologyReport;
			if (radiologyReportId == null) {
				radiologyReport = radiologyService.createAndClaimRadiologyReport(radiologyOrder);
				modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_URL + "orderId=" + order.getOrderId()
				        + "&radiologyReportId=" + radiologyReport.getId());
			} else {
				radiologyReport = radiologyService.getRadiologyReportByRadiologyReportId(radiologyReportId);
			}
			
			if (radiologyReport == null) {
				modelAndView = new ModelAndView(RADIOLOGY_ORDER_LIST_PATH);
				return modelAndView;
			}
			if (!radiologyReport.getRadiologyOrder().getId().equals(order.getId())) {
				modelAndView = new ModelAndView(RADIOLOGY_ORDER_LIST_PATH);
				return modelAndView;
			}
			
			addObjectsToModelAndView(modelAndView, order, radiologyOrder, radiologyReport);
			return modelAndView;
		}
		return modelAndView;
	}
	
	/**
	 * Handles POST request for the RadiologyReportForm to save a RadiologyReport
	 *
	 * @param order Order
	 * @param radiologyReport RadiologyReport
	 * @return ModelAndView containing the saved RadiologyReport
	 * @should populate ModelAndView RadiologyReportForm containing the saved RadiologyReport for a
	 *         RadiologyOrder
	 */
	@RequestMapping(method = RequestMethod.POST, params = "saveRadiologyReport")
	protected ModelAndView saveRadiologyReport(@RequestParam(value = "orderId", required = true) Order order,
	        @ModelAttribute("radiologyReport") RadiologyReport radiologyReport) {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_PATH);
		if (Context.isAuthenticated()) {
			RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(order.getOrderId());
			radiologyReport.setRadiologyOrder(radiologyOrder);
			radiologyReport.setReportBody(radiologyReport.getReportBody());
			radiologyReport.setPrincipalResultsInterpreter(radiologyReport.getPrincipalResultsInterpreter());
			
			radiologyService.saveRadiologyReport(radiologyReport);
			addObjectsToModelAndView(modelAndView, order, radiologyOrder, radiologyReport);
			return modelAndView;
		}
		return modelAndView;
	}
	
	/**
	 * Handles POST request for the RadiologyReportForm to unclaim a RadiologyReport
	 *
	 * @param order Order
	 * @param radiologyReport RadiologyReport
	 * @return ModelAndView RadiologyOrderForm if unclaim was successful, otherwise
	 *         RadiologyReportForm
	 * @should populate ModelAndView RadiologyOrderForm if unclaim was successful
	 * @should populate ModelAndView RadiologyReportForm if unclaim failed
	 */
	@RequestMapping(method = RequestMethod.POST, params = "unclaimRadiologyReport")
	protected ModelAndView unclaimRadiologyReport(@RequestParam(value = "orderId", required = true) Order order,
	        @ModelAttribute("radiologyReport") RadiologyReport radiologyReport) {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_PATH);
		if (Context.isAuthenticated()) {
			RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(order.getOrderId());
			radiologyReport.setRadiologyOrder(radiologyOrder);
			radiologyService.unclaimRadiologyReport(radiologyReport);
			return new ModelAndView(RADIOLOGY_ORDER_FORM_URL + "orderId=" + order.getOrderId());
		}
		return modelAndView;
	}
	
	/**
	 * Handles POST request for the RadiologyReportForm to complete a RadiologyReport
	 *
	 * @param order Order
	 * @param radiologyReport RadiologyReport
	 * @param bindingResult BindingResult
	 * @return ModelAndView RadiologyOrderForm if complete was successful, otherwise the
	 *         ModelAndView with BindingResult errors
	 * @should populate ModelAndView RadiologyReportForm containing the completed RadiologyReport
	 * @should populate ModelAndView RadiologyReportForm with BindingResult errors if provider is
	 *         null
	 */
	@RequestMapping(method = RequestMethod.POST, params = "completeRadiologyReport")
	protected ModelAndView completeRadiologyReport(@RequestParam(value = "orderId", required = true) Order order,
	        @ModelAttribute("radiologyReport") RadiologyReport radiologyReport, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_PATH);
		if (Context.isAuthenticated()) {
			RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(order.getOrderId());
			radiologyReport.setRadiologyOrder(radiologyOrder);
			radiologyReport.setReportBody(radiologyReport.getReportBody());
			radiologyReport.setPrincipalResultsInterpreter(radiologyReport.getPrincipalResultsInterpreter());
			
			if (validateForm(radiologyReport, bindingResult, modelAndView, order, radiologyOrder)) {
				return modelAndView;
			}
			
			radiologyService.completeRadiologyReport(radiologyReport, radiologyReport.getPrincipalResultsInterpreter());
			addObjectsToModelAndView(modelAndView, order, radiologyOrder, radiologyReport);
			return modelAndView;
		}
		return modelAndView;
	}
	
	/**
	 * Convenience method to check, if RadiologyReportForm has BindingErrors (e.g. Provider is not
	 * set)
	 *
	 * @param radiologyReport RadiologyReport
	 * @param bindingResult BindingResult
	 * @param modelAndView ModelAndView
	 * @param order Order
	 * @param radiologyOrder RadiologyOrder
	 * @return true, if RadiologyReportFrom has errors (e.g. Provider is missing), false if
	 *         RadiologyReportForm has no errors
	 */
	private boolean validateForm(RadiologyReport radiologyReport, BindingResult bindingResult, ModelAndView modelAndView,
	        Order order, RadiologyOrder radiologyOrder) {
		if (radiologyReport.getPrincipalResultsInterpreter() == null) {
			new RadiologyReportValidator().validate(radiologyReport, bindingResult);
		}
		if (bindingResult.hasErrors()) {
			addObjectsToModelAndView(modelAndView, order, radiologyOrder, radiologyReport);
			return true;
		}
		return false;
	}
	
	/**
	 * Convenience method to add objects (order, radiologyOrder, radiologyReport) to modelAndView
	 *
	 * @param modelAndView ModelAndView
	 * @param order Order
	 * @param radiologyOrder RadiologyOrder
	 * @param radiologyReport RadiologyReport
	 */
	private void addObjectsToModelAndView(ModelAndView modelAndView, Order order, RadiologyOrder radiologyOrder,
	        RadiologyReport radiologyReport) {
		modelAndView.addObject("order", order);
		modelAndView.addObject("radiologyOrder", radiologyOrder);
		modelAndView.addObject("radiologyReport", radiologyReport);
	}
}
