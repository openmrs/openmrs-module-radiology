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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Utils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class RadiologyDashboardController implements Controller {
	
	Log log = LogFactory.getLog(getClass());
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException {
		String patientId = request.getParameter("patientId");
		return ordersTable(patientId);
	}
	
	ModelAndView ordersTable(String patientId) {
		String str = "";
		String route = "/module/radiology/portlets/RadiologyDashboardTab";
		ModelAndView mav = new ModelAndView(route);
		
		List<Order> matchedOrders = getRadiologyOrdersByPatient(patientId, mav);
		// TODO Status filter
		List<Study> studies = Study.get(matchedOrders);
		List<String> statuses = new Vector<String>();
		List<String> priorities = new Vector<String>();
		List<String> schedulers = new Vector<String>();
		List<String> performings = new Vector<String>();
		List<String> readings = new Vector<String>();
		List<String> modalities = new Vector<String>();
		List<String> mwlStatuses = new Vector<String>();
		for (Study study : studies) {
			if (study != null) {
				statuses.add(study.getStatus(Context.getAuthenticatedUser()));
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
		
		mav.addObject(matchedOrders);
		mav.addObject("statuses", statuses);
		mav.addObject("priorities", priorities);
		mav.addObject("schedulers", schedulers);
		mav.addObject("performings", performings);
		mav.addObject("readings", readings);
		mav.addObject("modalities", modalities);
		mav.addObject("matchedOrdersSize", matchedOrders.size());
		//if(Context.getAuthenticatedUser().hasRole(Roles.ReadingPhysician, true)){
		mav.addObject("obsId", "&obsId");
		//}
		mav.addObject("mwlStatuses", mwlStatuses);
		mav.addObject("patientId", patientId);
		
		log.debug("\n***\n" + str + "\n///");
		return mav;
	}
	
	private List<Order> getRadiologyOrdersByPatient(String patientId, ModelAndView mav) {
		
		OrderService os = Context.getOrderService();
		PatientService ps = Context.getPatientService();
		Patient p = ps.getPatient(Integer.valueOf(patientId));
		List<Patient> plist = new ArrayList();
		plist.add(p);
		List<Order> matchedOrders = os.getOrders(Order.class, plist, null, null, null, null, Utils.getRadiologyOrderType());
		
		return matchedOrders;
	}
	
}
