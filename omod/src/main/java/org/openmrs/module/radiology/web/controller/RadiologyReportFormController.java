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
import org.openmrs.module.radiology.dicom.DicomWebViewer;
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
@RequestMapping(value = RadiologyReportFormController.RADIOLOGY_REPORT_FORM_REQUEST_MAPPING)
public class RadiologyReportFormController {
	
	protected static final String RADIOLOGY_REPORT_FORM_REQUEST_MAPPING = "/module/radiology/radiologyReport.form";
	
	private static final String RADIOLOGY_REPORT_FORM_VIEW = "/module/radiology/radiologyReportForm";
	
	@Autowired
	private RadiologyService radiologyService;
	
	@Autowired
	private DicomWebViewer dicomWebViewer;
	
	/**
	 * Handles GET requests for the radiologyReportForm creating a new radiology report for given
	 * radiology order
	 * 
	 * @param radiologyOrder radiology order for which a radiology report will be created
	 * @return model and view containing new radiology report for given radiology order
	 * @should populate model and view with new radiology report for given radiology order
	 */
	@RequestMapping(method = RequestMethod.GET, params = "orderId")
	protected ModelAndView getRadiologyReportFormWithNewRadiologyReport(
			@RequestParam(value = "orderId", required = true) RadiologyOrder radiologyOrder) {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
		
		if (Context.isAuthenticated()) {
			
			RadiologyReport radiologyReport = radiologyService.createAndClaimRadiologyReport(radiologyOrder);
			modelAndView = new ModelAndView("redirect:" + RADIOLOGY_REPORT_FORM_REQUEST_MAPPING + "?radiologyReportId="
					+ radiologyReport.getId());
			
			addObjectsToModelAndView(modelAndView, radiologyReport);
			return modelAndView;
		}
		return modelAndView;
	}
	
	/**
	 * Handles GET requests for the radiologyReportForm when called for existing radiology reports
	 * 
	 * @param radiologyReportId radiology report id of the existing radiology report which should be
	 *        put into the model and view
	 * @return model and view containing radiology report for given radiology report id
	 * @should populate model and view with existing radiology report matching given radiology
	 *         report id
	 */
	@RequestMapping(method = RequestMethod.GET, params = "radiologyReportId")
	protected ModelAndView getRadiologyReportFormWithExistingRadiologyReport(
			@RequestParam(value = "radiologyReportId", required = true) Integer radiologyReportId) {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
		
		if (Context.isAuthenticated()) {
			
			RadiologyReport radiologyReport = radiologyService.getRadiologyReportByRadiologyReportId(radiologyReportId);
			
			addObjectsToModelAndView(modelAndView, radiologyReport);
			return modelAndView;
		}
		return modelAndView;
	}
	
	/**
	 * Handles POST request for the RadiologyReportForm to save a RadiologyReport
	 *
	 * @param radiologyReport radiology report to be saved
	 * @return ModelAndView containing saved radiology report
	 * @should save given radiology report and populate model and view with it
	 */
	@RequestMapping(method = RequestMethod.POST, params = "saveRadiologyReport")
	protected ModelAndView saveRadiologyReport(@ModelAttribute("radiologyReport") RadiologyReport radiologyReport) {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
		
		if (Context.isAuthenticated()) {
			radiologyService.saveRadiologyReport(radiologyReport);
			
			addObjectsToModelAndView(modelAndView, radiologyReport);
			return modelAndView;
		}
		return modelAndView;
	}
	
	/**
	 * Handles POST request for the RadiologyReportForm to unclaim given RadiologyReport
	 *
	 * @param radiologyReport radiology report to be unclaimed
	 * @return ModelAndView with redirect to order form if unclaim was successful, otherwise stay on
	 *         report form
	 * @should redirect to radiology order form if unclaim was successful
	 */
	@RequestMapping(method = RequestMethod.POST, params = "unclaimRadiologyReport")
	protected ModelAndView unclaimRadiologyReport(@ModelAttribute("radiologyReport") RadiologyReport radiologyReport) {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
		
		if (Context.isAuthenticated()) {
			radiologyService.unclaimRadiologyReport(radiologyReport);
			return new ModelAndView("redirect:" + RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_REQUEST_MAPPING
					+ "?orderId=" + radiologyReport.getRadiologyOrder()
							.getOrderId());
		}
		return modelAndView;
	}
	
	/**
	 * Handles POST request for the RadiologyReportForm to complete given RadiologyReport
	 *
	 * @param radiologyReport radiology report to be completed
	 * @param bindingResult BindingResult
	 * @return ModelAndView RadiologyOrderForm if complete was successful, otherwise the
	 *         ModelAndView with BindingResult errors
	 * @should complete given radiology report and populate model and view with it
	 * @should populate model and view radiology report form with BindingResult errors if provider
	 *         is null
	 */
	@RequestMapping(method = RequestMethod.POST, params = "completeRadiologyReport")
	protected ModelAndView completeRadiologyReport(@ModelAttribute("radiologyReport") RadiologyReport radiologyReport,
			BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_FORM_VIEW);
		
		if (Context.isAuthenticated()) {
			if (validateForm(modelAndView, radiologyReport, bindingResult)) {
				return modelAndView;
			}
			
			final RadiologyReport completedRadiologyReport = radiologyService.completeRadiologyReport(radiologyReport,
				radiologyReport.getPrincipalResultsInterpreter());
			addObjectsToModelAndView(modelAndView, completedRadiologyReport);
			return modelAndView;
		}
		return modelAndView;
	}
	
	/**
	 * Convenience method to check, if RadiologyReportForm has BindingErrors (e.g. Provider is not
	 * set)
	 *
	 * @param radiologyReport radiology report to be validated
	 * @param bindingResult BindingResult
	 * @param modelAndView model and view where radiology report is added
	 * @return true, if RadiologyReportFrom has errors (e.g. Provider is missing), false if
	 *         RadiologyReportForm has no errors
	 */
	private boolean validateForm(ModelAndView modelAndView, RadiologyReport radiologyReport, BindingResult bindingResult) {
		if (radiologyReport.getPrincipalResultsInterpreter() == null) {
			new RadiologyReportValidator().validate(radiologyReport, bindingResult);
		}
		if (bindingResult.hasErrors()) {
			addObjectsToModelAndView(modelAndView, radiologyReport);
			return true;
		}
		return false;
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
		RadiologyOrder radiologyOrder = radiologyReport.getRadiologyOrder();
		modelAndView.addObject("dicomViewerUrl", dicomWebViewer.getDicomViewerUrl(radiologyOrder.getStudy()));
		modelAndView.addObject("radiologyOrder", radiologyOrder);
	}
}
