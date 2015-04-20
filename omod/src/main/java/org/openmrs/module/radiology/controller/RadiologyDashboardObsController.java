/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.controller;

import java.util.List;
import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Utils;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.DrugEditor;
import org.openmrs.propertyeditor.EncounterEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.OrderEditor;
import org.openmrs.propertyeditor.PersonEditor;
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

/**
 * @author Akhil
 */

@Controller
public class RadiologyDashboardObsController {
	
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
	
	@RequestMapping("/module/radiology/portlets/radiologyObsDashboard.form")
	ModelAndView getObs(@RequestParam(value = "orderId", required = true) Integer orderId,
	        @RequestParam(value = "obsId", required = false) Integer obsId) {
		ModelAndView mav = new ModelAndView("module/radiology/portlets/DashboardObsForm");
		populate(mav, orderId, obsId);
		return mav;
	}
	
	private void populate(ModelAndView mav, Integer orderId, Integer obsId) {
		Obs obs = null;
		// Get previous obs
		List<Obs> prevs = null;
		ObsService obsService = Context.getObsService();
		OrderService orderService = Context.getOrderService();
		Order order = orderService.getOrder(orderId);
		Study study = service().getStudyByOrder(order);
		prevs = service().getObservationsByStudy(study);
		
		if (obsId != null) {
			obs = obsService.getObs(obsId);
			mav.addObject("obsAnswer", obs.getValueAsString(Locale.ENGLISH));
		} else {
			obs = newObs(order);
		}
		
		mav.addObject("obs", obs);
		mav.addObject("studyUID", study.isCompleted() ? study.getUid() : null);
		if (study.isCompleted()) {
			//    System.out.println("Study UID:"+study.getUid()+" Completed : "+study.isCompleted()+" Patient ID : "+or.getOrder(orderId).getPatient().getId()+" Server : "+Utils.oviyamLocalServerName() );                    
			String patID = order.getPatient().getPatientIdentifier().getIdentifier();
			String link = Utils.serversAddress() + ":" + Utils.serversPort() + Utils.viewerURLPath()
			        + Utils.oviyamLocalServerName() + "studyUID=" + study.getUid() + "&patientID=" + patID;
			mav.addObject("oviyamLink", link);
		} else {
			mav.addObject("oviyamLink", null);
		}
		
		mav.addObject("prevs", prevs);
		mav.addObject("prevsSize", prevs.size());
		mav.addObject("personName", order.getPatient().getPersonName().getFullName());
		mav.addObject("orderId", orderId);
		
	}
	
	@RequestMapping(value = "/module/radiology/portlets/radiologyObsDetailsDashboard.form", method = RequestMethod.GET)
	ModelAndView getObsDetails(@RequestParam(value = "orderId", required = true) Integer orderId,
	        @RequestParam(value = "obsId", required = true) Integer obsId, @ModelAttribute("obs") Obs obs,
	        BindingResult errors) {
		ModelAndView mav = new ModelAndView("module/radiology/portlets/DashboardObsDetailsForm");
		populate(mav, orderId, obsId);
		return mav;
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
}
