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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.obs.ComplexData;
import org.openmrs.propertyeditor.ObsEditor;
import org.openmrs.propertyeditor.OrderEditor;
import org.openmrs.validator.ObsValidator;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RadiologyObsFormController {
	
	Log log = LogFactory.getLog(getClass());
	
	private static final String RADIOLOGY_OBS_FORM_PATH = "module/radiology/radiologyObsForm";
	
	private static final String RADIOLOGY_OBS_FORM_URL = "/module/radiology/radiologyObs.form?";
	
	@Autowired
	RadiologyService radiologyService;
	
	@Autowired
	ObsService obsService;
	
	@Autowired
	RadiologyProperties radiologyProperties;
	
	@InitBinder
	void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Order.class, new OrderEditor());
		binder.registerCustomEditor(Obs.class, new ObsEditor());
	}
	
	/**
	 * Get obs corresponding to given radiologyOrder and obs
	 * 
	 * @param order order for which the obs should be returned
	 * @param obs obs which should be returned
	 * @return model and view populated with an obs matching the given criteria
	 * @should populate model and view with obs for given obs and given valid radiology order
	 */
	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.GET, params = "obsId")
	protected ModelAndView getObs(@RequestParam(value = "orderId", required = true) RadiologyOrder radiologyOrder,
	        @RequestParam(value = "obsId", required = true) Obs obs) {
		return populateModelAndView(radiologyOrder, obs);
	}
	
	/**
	 * Populate model and view given radiologyOrder and obs
	 * 
	 * @param radiologyOrder to populate the model and view
	 * @param obs to populate the model and view
	 * @should populate the model and view for given radiology order with completed study and obs
	 * @should populate the model and view for given radiology order without completed study and obs
	 */
	private ModelAndView populateModelAndView(RadiologyOrder radiologyOrder, Obs obs) {
		
		ModelAndView result = new ModelAndView(RADIOLOGY_OBS_FORM_PATH);
		
		List<Obs> previousObs = radiologyService.getObsByOrderId(radiologyOrder.getOrderId());
		result.addObject("obs", obs);
		result.addObject("previousObs", previousObs);
		
		Study study = radiologyOrder.getStudy();
		result.addObject("studyUID", study.isCompleted() ? study.getStudyInstanceUid() : null);
		result.addObject("dicomViewerUrl", getDicomViewerUrl(study, radiologyOrder.getPatient()));
		
		return result;
	}
	
	/**
	 * Get dicom viewer URL for given study and patient
	 * 
	 * @param study study for the dicom viewer url
	 * @param patient patient for the dicom viewer url
	 * @should return dicom viewer url given completed study and patient
	 * @should return null given non completed study and patient
	 */
	private String getDicomViewerUrl(Study study, Patient patient) {
		
		if (study.isCompleted()) {
			String studyUidUrl = "studyUID=" + study.getStudyInstanceUid();
			String patientIdUrl = "patientID=" + patient.getPatientIdentifier().getIdentifier();
			return radiologyProperties.getDicomViewerUrl() + studyUidUrl + "&" + patientIdUrl;
		} else {
			return null;
		}
	}
	
	/**
	 * Get new obs corresponding to given radiologyOrder
	 * 
	 * @param radiologyOrder radiology order for which the obs should be returned
	 * @return model and view populated with a new obs
	 * @should populate model and view with new obs given a valid radiology order
	 */
	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.GET)
	protected ModelAndView getNewObs(@RequestParam(value = "orderId", required = true) RadiologyOrder radiologyOrder) {
		Obs obs = new Obs();
		obs.setOrder(radiologyOrder);
		obs.setPerson(radiologyOrder.getPatient());
		obs.setEncounter(radiologyOrder.getEncounter());
		return populateModelAndView(radiologyOrder, obs);
	}
	
	/**
	 * 
	 * Void given obs corresponding to given http servlet request, http servlet response, radiologyOrder, obs, voidReason
	 * 
	 * @param request the http servlet request with all parameters
	 * @param response the http servlet response
	 * @param radiologyOrder the corresponding radiology order
	 * @param obs the obs
	 * @param voidReason the reason the obs was voided for
	 * @return ModelAndView for radiology obs form
	 * @should void obs for given request, response, radiologyOrder, obs, and voidReason 
	 * @should not void obs with empty voiding reason
	 * @should not void obs with voiding reason null
	 */
	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.POST, params = "voidObs")
	protected ModelAndView voidObs(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(value = "orderId", required = true) RadiologyOrder radiologyOrder,
	        @RequestParam(value = "obsId", required = true) Obs obs,
	        @RequestParam(value = "voidReason", required = true) String voidReason) {
		
		HttpSession httpSession = request.getSession();
		if (voidReason == null || voidReason.isEmpty()) {
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Obs.void.reason.empty");
			return populateModelAndView(radiologyOrder, obs);
		}
		
		obsService.voidObs(obs, voidReason);
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Obs.voidedSuccessfully");
		return new ModelAndView("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId=" + radiologyOrder.getId() + "&obsId="
		        + obs.getId());
	}
	
	/**
	 * 
	 * Unvoid given obs corresponding to given http servlet request, http servlet response and obs
	 * 
	 * @param request the http servlet request with all parameters
	 * @param response the http servlet response
	 * @param obs obs which should be unvoided
	 * @return ModelAndView for radiology order list
	 * @should unvoid voided obs for given request, response and obs
	 */
	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.POST, params = "unvoidObs")
	protected ModelAndView unvoidObs(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(value = "obsId", required = true) Obs obs) {
		
		HttpSession httpSession = request.getSession();
		obsService.unvoidObs(obs);
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Obs.unvoidedSuccessfully");
		return new ModelAndView("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId=" + obs.getOrder().getId() + "&obsId="
		        + obs.getId());
	}
	
	/**
	 * Save obs corresponding to given http servlet request, http servlet response, radiologyOrder, obs, obs, obsErrors
	 *  
	 * @param request the http servlet request with all parameters
	 * @param response the http servlet response
	 * @param radiologyOrder the corresponding radiology order
	 * @param obs the obs
	 * @param obsErrors the result of the parameter binding
	 * @return ModelAndView populated with obs matching the given criteria
	 * @should save obs with given parameters
	 * @should return populated model and view if binding errors occur
	 * @should return populated model and view if edit reason is empty and obs id not null
	 * @should return populated model and view if edit reason is null and obs id not null
	 * @should return redirecting model and view for not authenticated user
	 * @should edit obs with edit reason and complex concept
	 * @should edit obs with edit reason, complex concept and request which is an instance
	 *         of multihttpserveletrequest
	 * @should edit obs with edit reason concept not complex and request which is an
	 *         instance of multihttpserveletrequest
	 * @should populate model and view with obs occuring thrown APIException
	 */
	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.POST, params = "saveObs")
	ModelAndView saveObs(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(value = "editReason", required = false) String editReason,
	        @RequestParam(value = "orderId", required = true) RadiologyOrder radiologyOrder, @ModelAttribute("obs") Obs obs,
	        BindingResult obsErrors) {
		
		HttpSession httpSession = request.getSession();
		
		new ObsValidator().validate(obs, obsErrors);
		
		if (obsErrors.hasErrors()) {
			return populateModelAndView(radiologyOrder, obs);
		}
		if (Context.isAuthenticated()) {
			
			try {
				// if the user is just editing the obs
				if (obs.getObsId() != null && (editReason == null || editReason.isEmpty())) {
					obsErrors.reject("editReason", "Obs.edit.reason.empty");
					
					return populateModelAndView(radiologyOrder, obs);
				}
				
				if (obs.getConcept().isComplex()) {
					if (request instanceof MultipartHttpServletRequest) {
						MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
						MultipartFile complexDataFile = multipartRequest.getFile("complexDataFile");
						if (complexDataFile != null && !complexDataFile.isEmpty()) {
							InputStream complexDataInputStream = complexDataFile.getInputStream();
							
							ComplexData complexData = new ComplexData(complexDataFile.getOriginalFilename(),
							        complexDataInputStream);
							
							obs.setComplexData(complexData);
							
							// the handler on the obs.concept is called
							// with
							// the given complex data
							obs = obsService.saveObs(obs, editReason);
							complexDataInputStream.close();
						}
					}
				} else {
					obs = obsService.saveObs(obs, editReason);
				}
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Obs.saved");
			}
			catch (APIException e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getMessage());
				return populateModelAndView(radiologyOrder, obs);
			}
			catch (IOException e) {
				return populateModelAndView(radiologyOrder, obs);
			}
		}
		return new ModelAndView("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId=" + obs.getOrder().getId() + "&obsId="
		        + obs.getId());
	}
}
