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
import org.openmrs.Patient;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.obs.ComplexData;
import org.openmrs.propertyeditor.ObsEditor;
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
	 * @should populate the model and view for given obs with complex concept
	 */
	private ModelAndView populateModelAndView(RadiologyOrder radiologyOrder, Obs obs) {
		
		ModelAndView result = new ModelAndView(RADIOLOGY_OBS_FORM_PATH);
		
		List<Obs> previousObs = radiologyService.getObsByOrderId(radiologyOrder.getOrderId());
		result.addObject("obs", obs);
		result.addObject("previousObs", previousObs);
		
		Study study = radiologyOrder.getStudy();
		result.addObject("studyUID", study.isCompleted() ? study.getStudyInstanceUid() : null);
		result.addObject("dicomViewerUrl", getDicomViewerUrl(study, radiologyOrder.getPatient()));
		
		if (obs.getId() != null && obs.getConcept() != null && obs.getConcept().isComplex()) {
			Obs complexObsAsHtmlView = obsService.getComplexObs(obs.getId(), WebConstants.HTML_VIEW);
			result.addObject("htmlView", complexObsAsHtmlView.getComplexData().getData());
			
			Obs complexObsAsHyperlinkView = obsService.getComplexObs(obs.getId(), WebConstants.HYPERLINK_VIEW);
			result.addObject("hyperlinkView", complexObsAsHyperlinkView.getComplexData().getData());
		}
		
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
		
		obsService.unvoidObs(obs);
		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Obs.unvoidedSuccessfully");
		return new ModelAndView("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId=" + obs.getOrder().getId() + "&obsId="
		        + obs.getId());
	}
	
	/**
	 * Save obs corresponding to given http servlet request, http servlet response, editReason, radiologyOrder, obs, obsErrors
	 *  
	 * @param request the http servlet request with all parameters
	 * @param response the http servlet response
	 * @param editReason reason why the obs was edited
	 * @param radiologyOrder radiology order corresponding to the obs
	 * @param obs the obs to be changed
	 * @param obsErrors the result of the parameter binding
	 * @return ModelAndView populated with obs matching the given criteria
	 * @should return redirecting model and view for not authenticated user
	 * @should return populated model and view for obs
	 * @should return populated model and view for invalid obs
	 * @should populate model and view with obs for occuring Exception
	 */
	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.POST, params = "saveObs")
	protected ModelAndView saveObs(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(value = "editReason", required = false) String editReason,
	        @RequestParam(value = "orderId", required = true) RadiologyOrder radiologyOrder, @ModelAttribute("obs") Obs obs,
	        BindingResult obsErrors) {
		
		if (Context.isAuthenticated()) {
			
			try {
				if (isObsValidToSave(obs, obsErrors, editReason)) {
					obs = obsService.saveObs(obs, editReason);
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Obs.saved");
				} else {
					return populateModelAndView(radiologyOrder, obs, editReason);
				}
				
			}
			catch (Exception e) {
				request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getMessage());
				return populateModelAndView(radiologyOrder, obs, editReason);
			}
		}
		return new ModelAndView("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId=" + obs.getOrder().getId() + "&obsId="
		        + obs.getId());
	}
	
	/**
	 * Save obs corresponding to given http servlet request, http servlet response, editReason, radiologyOrder, obs, obsErrors
	 *  
	 * @param request the http servlet request with all parameters
	 * @param response the http servlet response
	 * @param editReason reason why the obs was edited
	 * @param radiologyOrder radiology order corresponding to the obs
	 * @param obs the obs to be changed
	 * @param obsErrors the result of the parameter binding
	 * @return ModelAndView populated with obs matching the given criteria
	 * @should return redirecting model and view for not authenticated user
	 * @should return populated model and view for complex obs
	 * @should return populated model and view for invalid complex obs
	 * @should populate model and view with obs for occuring Exception
	 */
	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.POST, params = "saveComplexObs")
	protected ModelAndView saveComplexObs(MultipartHttpServletRequest request, HttpServletResponse response,
	        @RequestParam(value = "editReason", required = false) String editReason,
	        @RequestParam(value = "orderId", required = true) RadiologyOrder radiologyOrder, @ModelAttribute("obs") Obs obs,
	        BindingResult obsErrors) {
		
		if (Context.isAuthenticated()) {
			try {
				InputStream complexDataInputStream = openInputStreamForComplexDataFile(request.getFile("complexDataFile"));
				obs = populateObsWithComplexData(request.getFile("complexDataFile"), obs, complexDataInputStream);
				if (isObsValidToSave(obs, obsErrors, editReason)) {
					obs = obsService.saveObs(obs, editReason);
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Obs.saved");
				} else {
					return populateModelAndView(radiologyOrder, obs, editReason);
				}
				complexDataInputStream.close();
			}
			catch (Exception e) {
				request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getMessage());
				return populateModelAndView(radiologyOrder, obs, editReason);
			}
		}
		return new ModelAndView("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId=" + obs.getOrder().getId() + "&obsId="
		        + obs.getId());
	}
	
	/**
	 * Open input stream for complex data file
	 * 
	 * @param complexDataFile the complex data file
	 * @return input stream for complex data file
	 * @throws IOException if stream could not be opened
	 * @should open input stream for complex data file
	 * @should throw exception if input stream could not be opened
	 */
	private InputStream openInputStreamForComplexDataFile(MultipartFile complexDataFile) throws IOException {
		if (complexDataFile == null) {
			throw new IOException("error.general");
		}
		InputStream complexDataInputStream = complexDataFile.getInputStream();
		return complexDataInputStream;
	}
	
	/**
	 * Populate complex obs with complex data
	 * 
	 * @param complexDataFile the obs should be populated with
	 * @param obs to be populated
	 * @param InputStream of the file
	 * @return saved complex obs with complex data
	 * @throws IOException
	 * @should populate new obs with new complex data
	 * @should populate obs with new complex data
	 * @should throw exception for new obs with empty file
	 */
	private Obs populateObsWithComplexData(MultipartFile complexDataFile, Obs obs, InputStream complexDataInputStream)
	        throws IOException {
		
		boolean isComplexDataFileNotNullAndNotEmpty = complexDataFile != null && !complexDataFile.isEmpty();
		if (isComplexDataFileNotNullAndNotEmpty) {
			obs.setComplexData(new ComplexData(complexDataFile.getOriginalFilename(), complexDataInputStream));
			return obs;
		} else if (obs.getId() != null) {
			obs.setComplexData(obsService.getComplexObs(Integer.valueOf(obs.getId()), null).getComplexData());
			return obs;
		} else {
			throw new IOException("Obs.invalidImage");
		}
	}
	
	/**
	 * Check if Obs is Valid
	 * 
	 * @param obs to be validated
	 * @param obsErrors the result of the parameter binding
	 * @param editReason reason why the obs was edited
	 * @return true if obs is valid
	 * @should return true if obs is valid
	 * @should return false if edit reason is empty and obs id not null
	 * @should return false if edit reason is null and obs id not null
	 * @should return false if validation of the obs validator fails
	 */
	private boolean isObsValidToSave(Obs obs, BindingResult obsErrors, String editReason) {
		if (obs.getObsId() != null && (editReason == null || editReason.isEmpty())) {
			obsErrors.reject("editReason", "Obs.edit.reason.empty");
			return false;
		}
		
		new ObsValidator().validate(obs, obsErrors);
		if (obsErrors.hasErrors()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Populate model and view given radiologyOrder, obs and editReason
	 * 
	 * @param radiologyOrder to populate the model and view
	 * @param obs to populate the model and view
	 * @param reason, why the obs was edited
	 * @should populate model and view with edit reason
	 */
	private ModelAndView populateModelAndView(RadiologyOrder radiologyOrder, Obs obs, String editReason) {
		
		ModelAndView result = populateModelAndView(radiologyOrder, obs);
		
		if (editReason == null) {
			editReason = "";
		}
		result.addObject("editReason", editReason);
		return result;
	}
}
