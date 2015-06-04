/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.ObsService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Roles;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Utils;
import org.openmrs.obs.ComplexData;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.DrugEditor;
import org.openmrs.propertyeditor.EncounterEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.OrderEditor;
import org.openmrs.propertyeditor.PersonEditor;
import org.openmrs.validator.ObsValidator;
import org.openmrs.web.WebConstants;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ObsFormController {
	
	static RadiologyService service() {
		return Context.getService(RadiologyService.class);
	}
	
	@InitBinder
	void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true));
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor(true)); //allow for an empty boolean value
		binder.registerCustomEditor(Person.class, new PersonEditor());
		binder.registerCustomEditor(Order.class, new OrderEditor());
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Drug.class, new DrugEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Encounter.class, new EncounterEditor());
	}
	
	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.GET)
	protected ModelAndView getObs(@RequestParam(value = "orderId", required = false) Integer orderId,
	        @RequestParam(value = "obsId", required = false) Integer obsId) {
		ModelAndView mav = new ModelAndView("module/radiology/obsForm");
		populate(mav, orderId, obsId);
		return mav;
	}
	
	private void populate(ModelAndView mav, Integer orderId, Integer obsId) {
		Obs obs = null;
		// Get previous obs
		List<Obs> prevs = null;
		ObsService os = Context.getObsService();
		OrderService or = Context.getOrderService();
		Study study = service().getStudyByOrderId(orderId);
		if (obsId != null) {
			obs = os.getObs(obsId);
			prevs = service().getStudyByOrderId(obs.getOrder().getOrderId()).obs();
		} else {
			obs = newObs(or.getOrder(orderId));
			prevs = study.obs();
		}
		
		mav.addObject("obs", obs);
		mav.addObject("studyUID", study.isCompleted() ? study.getUid() : null);
		if (study.isCompleted()) {
			//    System.out.println("Study UID:"+study.getUid()+" Completed : "+study.isCompleted()+" Patient ID : "+or.getOrder(orderId).getPatient().getId()+" Server : "+Utils.oviyamLocalServerName() );                    
			String patID = or.getOrder(orderId).getPatient().getPatientIdentifier().getIdentifier();
			String link = Utils.serversAddress() + ":" + Utils.serversPort() + Utils.viewerURLPath()
			        + Utils.oviyamLocalServerName() + "studyUID=" + study.getUid() + "&patientID=" + patID;
			mav.addObject("oviyamLink", link);
		} else
			mav.addObject("oviyamLink", null);
		mav.addObject("prevs", prevs);
		mav.addObject("prevsSize", prevs.size());
	}
	
	private Obs newObs(Order order) {
		Obs obs;
		obs = new Obs();
		if (order != null) {
			obs.setOrder(order);
			obs.setPerson(order.getPatient());
			obs.setEncounter(order.getEncounter());
		}
		return obs;
	}
	
	private void updateReadingPhysician(Integer orderId) {
		Study study = service().getStudyByOrderId(orderId);
		User user = Context.getAuthenticatedUser();
		if (user.hasRole(Roles.ReadingPhysician, true) && study.getReadingPhysician() == null)
			study.setReadingPhysician(user);
		service().saveStudy(study);
	}
	
	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.POST)
	ModelAndView postObs(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(value = "order", required = false) Integer orderId,
	        @RequestParam(value = "obsId", required = false) Integer obsId, @ModelAttribute("obs") Obs obs,
	        BindingResult errors) {
		HttpSession httpSession = request.getSession();
		
		new ObsValidator().validate(obs, errors);
		Boolean voidCheck = false;
		if ((request.getParameter("voidObs") == null) && (request.getParameter("unvoidObs") == null))
			voidCheck = true;
		
		if (errors.hasErrors() && voidCheck) {
			//  System.out.println("#### Has errors #####"+errors.getAllErrors().toString());                                
			ModelAndView mav = new ModelAndView("module/radiology/obsForm");
			populate(mav, orderId, obsId);
			return mav;
		}
		//			if (Context.isAuthenticated() && !errors.hasErrors() ) {
		if (Context.isAuthenticated()) {
			ObsService os = Context.getObsService();
			try {
				// if the user is just editing the observation
				if (request.getParameter("saveObs") != null) {
					String reason = request.getParameter("editReason");
					if (obs.getObsId() != null && (reason == null || reason.length() == 0)) {
						errors.reject("editReason", "Obs.edit.reason.empty");
						ModelAndView mav = new ModelAndView("module/radiology/obsForm");
						populate(mav, orderId, obsId);
						return mav;
					}
					
					// TODO get reason from form when it is being saved along with the observation												
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
								os.saveObs(obs, reason);
								updateReadingPhysician(orderId);
								complexDataInputStream.close();
							}
						}
					} else {
						os.saveObs(obs, reason);
						updateReadingPhysician(orderId);
					}
					
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Obs.saved");
				}

				// if the user is voiding out the observation
				else if (request.getParameter("voidObs") != null) {
					String voidReason = request.getParameter("voidReason");
					Obs obs2 = os.getObs(Integer.valueOf(obsId));
					if (obs2.getObsId() != null && (voidReason == null || voidReason.length() == 0)) {
						errors.reject("voidReason", "Obs.void.reason.empty");
						ModelAndView mav = new ModelAndView("module/radiology/obsForm");
						populate(mav, orderId, obsId);
						return mav;
					}
					
					os.voidObs(obs2, voidReason);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Obs.voidedSuccessfully");
				}

				// if this obs is already voided and needs to be unvoided
				else if (request.getParameter("unvoidObs") != null) {
					Obs obs2 = os.getObs(Integer.valueOf(obsId));
					os.unvoidObs(obs2);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Obs.unvoidedSuccessfully");
				}
				
			}
			catch (APIException e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getMessage());
				ModelAndView mav = new ModelAndView("module/radiology/obsForm");
				populate(mav, orderId, obsId);
				return mav;
			}
			catch (IOException e) {
				ModelAndView mav = new ModelAndView("module/radiology/obsForm");
				populate(mav, orderId, obsId);
				return mav;
			}
			
		}
		return new ModelAndView("redirect:/module/radiology/radiologyOrder.list");
	}
	
}
