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

import java.util.List;
import java.util.Locale;

import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.ObsService;
import org.openmrs.api.OrderService;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
	
	@Autowired
	private RadiologyService radiologyService;
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ObsService obsService;
	
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
		Study study = radiologyService.getStudyByOrderId(orderId);
		if (obsId != null) {
			obs = obsService.getObs(obsId);
			mav.addObject("obsAnswer", obs.getValueAsString(Locale.ENGLISH));
			prevs = radiologyService.getObsByOrderId(obs.getOrder().getOrderId());
		} else {
			obs = newObs(orderService.getOrder(orderId));
			prevs = radiologyService.getObsByOrderId(study.getRadiologyOrder().getOrderId());
		}
		
		mav.addObject("obs", obs);
		mav.addObject("studyUID", study.isCompleted() ? study.getStudyInstanceUid() : null);
		if (study.isCompleted()) {
			String patID = orderService.getOrder(orderId).getPatient().getPatientIdentifier().getIdentifier();
			String dicomViewerUrl = radiologyProperties.getDicomViewerUrl() + "studyUID=" + study.getStudyInstanceUid()
			        + "&patientID=" + patID;
			mav.addObject("dicomViewerUrl", dicomViewerUrl);
		} else
			mav.addObject("dicomViewerUrl", null);
		mav.addObject("prevs", prevs);
		mav.addObject("prevsSize", prevs.size());
		mav.addObject("personName", orderService.getOrder(orderId).getPatient().getPersonName().getFullName());
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
