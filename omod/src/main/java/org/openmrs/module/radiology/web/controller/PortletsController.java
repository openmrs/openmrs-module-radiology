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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyService;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PortletsController {
	
	private static final Log log = LogFactory.getLog(PortletsController.class);
	
	@Autowired
	private RadiologyService radiologyService;
	
	@Autowired
	private PatientService patientService;
	
	/**
	 * Get URL to the patientOverview portlet
	 * 
	 * @return patient info route
	 * @should return string with patient info route
	 */
	@RequestMapping("/module/radiology/portlets/patientOverview.portlet")
	String getPatientInfoRoute() {
		return "module/radiology/portlets/patientOverview";
	}
	
	/**
	 * Get model and view containing radiology orders and studies for given patient string and date
	 * range
	 * 
	 * @param patientQuery Patient string for which radiology orders and studies should be returned
	 *        for
	 * @param startDate Date from which on the radiology orders and studies should be returned for
	 * @param endDate Date until which the radiology orders and studies should be returned for
	 * @return model and view containing radiology orders and studies corresponding to given
	 *         criteria
	 * @should populate model and view with radiology orders associated with given empty patient and
	 *         given date range null
	 * @should not populate model and view with radiology orders if start date is after end date
	 */
	@RequestMapping(value = "/module/radiology/portlets/orderSearch.portlet")
	ModelAndView getRadiologyOrdersByPatientQueryAndDateRange(
			@RequestParam(value = "patientQuery", required = false) String patientQuery,
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = ISO.DATE) Date startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = ISO.DATE) Date endDate) {
		ModelAndView mav = new ModelAndView("module/radiology/portlets/orderSearch");
		
		if (isEndDateBeforeStartDate(startDate, endDate)) {
			mav.addObject("exceptionText", "radiology.crossDate");
			mav.addObject("radiologyOrders", new ArrayList<RadiologyOrder>());
			return mav;
		}
		
		List<RadiologyOrder> radiologyOrders = getRadiologyOrdersForPatientQuery(patientQuery);
		radiologyOrders = filterRadiologyOrdersByDateRange(radiologyOrders, startDate, endDate);
		mav.addObject("radiologyOrders", radiologyOrders);
		
		return mav;
	}
	
	/**
	 * Get all orders for given date range
	 * 
	 * @param unfilteredRadiologyOrders list of orders which matches a patientQuery
	 * @param startDate Date from which on the radiology orders should be returned for
	 * @param endDate Date until which the radiology should be returned for
	 * @return list of radiology orders corresponding to given criteria
	 * @should return list of orders matching a given date range
	 * @should return list of all orders with start date if start date is null and end date is null
	 * @should return empty list of orders with given end date and start date before any order has
	 *         started
	 * @should return empty list of orders with given end date and start date after any order has
	 *         started
	 * @should return list of orders started after given start date but given end date null
	 * @should return list of orders started before given end date but given start date null
	 */
	private List<RadiologyOrder> filterRadiologyOrdersByDateRange(List<RadiologyOrder> unfilteredRadiologyOrders,
			Date startDate, Date endDate) {
		
		List<RadiologyOrder> result = new Vector<RadiologyOrder>();
		
		if (startDate == null && endDate == null) {
			return unfilteredRadiologyOrders;
		} else if (startDate == null && endDate != null) {
			for (RadiologyOrder order : unfilteredRadiologyOrders) {
				if (order.getEffectiveStartDate() != null && order.getEffectiveStartDate()
						.compareTo(endDate) <= 0) {
					
					result.add(order);
				}
			}
			
		} else if (startDate != null && endDate == null) {
			for (RadiologyOrder order : unfilteredRadiologyOrders) {
				if (order.getEffectiveStartDate() != null && order.getEffectiveStartDate()
						.compareTo(startDate) >= 0) {
					result.add(order);
				}
			}
		}
		
		else {
			for (RadiologyOrder order : unfilteredRadiologyOrders) {
				if (order.getEffectiveStartDate() != null && order.getEffectiveStartDate()
						.compareTo(startDate) >= 0 && order.getEffectiveStartDate()
						.compareTo(endDate) <= 0) {
					result.add(order);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Return true if end date is before start date
	 * 
	 * @param startDate start date of the date range
	 * @param endDate end date of the date range
	 * @return true if end date is before start date
	 * @should return true if end date is after start date
	 * @should return false if end date is not after start date
	 * @should return false with given start date but end date null
	 * @should return false with given end date but start date null
	 * @should return false with given end date and end date null
	 */
	private boolean isEndDateBeforeStartDate(Date startDate, Date endDate) {
		if (startDate != null && endDate != null) {
			if (startDate.after(endDate)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get orders of type radiology for patientQuery
	 * 
	 * @param patientQuery Patient string for which radiology orders and studies should be returned
	 *        for
	 * @return list of radiology orders associated with patientQuery
	 * @should return list of all radiology orders given patientQuery empty
	 * @should return list of all radiology orders given patientQuery null
	 * @should return list of all radiology orders given patientQuery matching no patient
	 * @should return empty list for patients without radiology orders
	 * @should return list of all radiology orders for a patient given valid patientQuery
	 */
	private List<RadiologyOrder> getRadiologyOrdersForPatientQuery(String patientQuery) {
		List<Patient> patientList = patientService.getPatients(patientQuery);
		return radiologyService.getRadiologyOrdersByPatients(patientList);
	}
	
	/**
	 * Handle all exceptions of type TypeMismatchException which occur in this class
	 * 
	 * @param TypeMismatchException the thrown TypeMismatchException
	 * @return model and view with exception information
	 * @should populate model with exception text from message properties and invalid value if date
	 *         is expected but nut passed
	 * @should should populate model with exception text
	 */
	@ExceptionHandler(TypeMismatchException.class)
	public ModelAndView handleTypeMismatchException(TypeMismatchException ex) {
		
		log.error(ex.getMessage());
		ModelAndView mav = new ModelAndView();
		
		if (ex.getRequiredType()
				.equals(Date.class)) {
			mav.addObject("invalidValue", ex.getValue());
			mav.addObject("exceptionText", "typeMismatch.java.util.Date");
		} else {
			mav.addObject("exceptionText", ex.getMessage());
		}
		
		return mav;
	}
	
}
