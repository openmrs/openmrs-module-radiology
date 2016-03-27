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

import org.openmrs.Patient;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RadiologyDashboardController {
	
	@Autowired
	RadiologyService radiologyService;
	
	/**
	 * Get all orders for given patient
	 * 
	 * @param patient the patient the radiology orders should be returned for
	 * @return model and view containing radiology orders for given patient
	 * @should return model and view populated with all radiology orders for given patient
	 * @should return model and view populated with an empty list of radiology orders if given patient is unknown
	 */
	@RequestMapping("**/RadiologyDashboardTab.portlet")
	public ModelAndView getRadiologyOrdersForPatient(@RequestParam(value = "patientId", required = true) Patient patient) {
		
		ModelAndView modelAndView = new ModelAndView("/module/radiology/portlets/radiologyDashboardTab");
		
		List<RadiologyOrder> radiologyOrders = radiologyService.getRadiologyOrdersByPatient(patient);
		modelAndView.addObject("radiologyOrders", radiologyOrders);
		return modelAndView;
	}
}
