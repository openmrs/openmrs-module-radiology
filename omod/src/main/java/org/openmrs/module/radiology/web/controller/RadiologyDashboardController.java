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
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.web.util.StudyStatusColumnGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RadiologyDashboardController {
	
	Log log = LogFactory.getLog(getClass());
	
	@Autowired
	RadiologyService radiologyService;
	
	/**
	 * Get all orders for given patient criteria
	 * 
	 * @param patient the patient the orders should be returned for
	 * @return model and view containing table of orders corresponding to given criteria
	 * @should return model and view populated with all orders given patient
	 * @should return empty model and view populated no orders given invalid patient
	 */
	@RequestMapping("**/RadiologyDashboardTab.portlet")
	public ModelAndView ordersTable(@RequestParam(value = "patientId", required = true) Patient patient) {
		
		ModelAndView mav = new ModelAndView("/module/radiology/portlets/RadiologyDashboardTab");
		
		List<RadiologyOrder> matchedOrders = radiologyService.getRadiologyOrdersByPatient(patient);
		// TODO Status filter
		List<Study> studies = radiologyService.getStudiesByRadiologyOrders(matchedOrders);
		List<String> statuses = new Vector<String>();
		List<String> priorities = new Vector<String>();
		List<String> schedulers = new Vector<String>();
		List<String> performings = new Vector<String>();
		List<String> readings = new Vector<String>();
		List<String> modalities = new Vector<String>();
		List<String> mwlStatuses = new Vector<String>();
		for (Study study : studies) {
			if (study != null) {
				statuses.add(StudyStatusColumnGenerator.getStatusColumnForStudy(Context.getAuthenticatedUser(), study));
				priorities.add(study.getPriority().name());
				schedulers.add(study.scheduler());
				performings.add(study.performing());
				readings.add(study.reading());
				modalities.add(study.getModality().getFullName());
				mwlStatuses.add(study.getMwlStatus().name());
			}
		}
		
		// TODO optimize all the function, get orders and studies(priorities, statuses, etc) in a row				
		// Response variables
		
		mav.addObject("orderList", matchedOrders);
		mav.addObject("statuses", statuses);
		mav.addObject("priorities", priorities);
		mav.addObject("schedulers", schedulers);
		mav.addObject("performings", performings);
		mav.addObject("readings", readings);
		mav.addObject("modalities", modalities);
		mav.addObject("matchedOrdersSize", matchedOrders.size());
		mav.addObject("obsId", "&obsId");
		mav.addObject("mwlStatuses", mwlStatuses);
		
		return mav;
	}
	
}
