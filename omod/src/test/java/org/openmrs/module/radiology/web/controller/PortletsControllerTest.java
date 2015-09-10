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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.Verifies;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link PortletsController}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { Context.class, RadiologyProperties.class })
@PowerMockIgnore( { "org.apache.commons.logging.*" })
public class PortletsControllerTest {
	
	private List<Patient> mockPatients;
	
	private List<Study> mockStudies;
	
	private List<Order> mockOrders;
	
	private OrderType mockRadiologyOrderType;
	
	@Mock
	private PatientService patientService;
	
	@Mock
	private RadiologyService radiologyService;
	
	@Mock
	private OrderService orderService;
	
	@Mock
	private List<Order> matchedOrders;
	
	@Mock
	private AdministrationService administrationService;
	
	@Mock
	private MessageSourceService messageSourceService;
	
	@InjectMocks
	private PortletsController portletsController = new PortletsController();
	
	@Before
	public void runBeforeAllTests() {
		PowerMockito.mockStatic(Context.class, RadiologyProperties.class);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String patientQuery = "";
		
		mockRadiologyOrderType = RadiologyTestData.getMockRadiologyOrderType();
		
		mockOrders = new ArrayList<Order>();
		mockOrders.add(RadiologyTestData.getMockRadiologyOrder1());
		mockOrders.add(RadiologyTestData.getMockRadiologyOrder2());
		
		mockStudies = new ArrayList<Study>();
		mockStudies.add(RadiologyTestData.getMockStudy1PostSave());
		mockStudies.add(RadiologyTestData.getMockStudy2PostSave());
		
		mockPatients = new ArrayList<Patient>();
		mockPatients.add(RadiologyTestData.getMockPatient1());
		mockPatients.add(RadiologyTestData.getMockPatient2());
		
		when(Context.getDateFormat()).thenReturn(sdf);
		when(Context.getOrderService()).thenReturn(orderService);
		when(RadiologyProperties.getRadiologyTestOrderType()).thenReturn(RadiologyTestData.getMockRadiologyOrderType());
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReferringPhysician());
		when(radiologyService.getStudiesByOrders(mockOrders)).thenReturn(mockStudies);
		when(patientService.getPatients(patientQuery)).thenReturn(mockPatients);
		when(
		    (orderService.getOrders(Order.class, patientService.getPatients(patientQuery), null, null, null, null, Arrays
		            .asList(mockRadiologyOrderType)))).thenReturn(mockOrders);
	}
	
	/**
	 * @see PortletsControllerTest#ordersTable(String, Date, Date, boolean, boolean)
	 */
	@Test
	@Verifies(value = "should populate model and view with table of orders associated with given date range null", method = "ordersTable(String, Date, Date, boolean, boolean)")
	public void ordersTable_ShouldPopulateModelAndViewWithTableOfOrdersAssociatedWithGivenDateRangeNull() throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = null;
		Date endDate = null;
		
		ModelAndView mav = portletsController.ordersTable(patientQuery, startDate, endDate, true, true);
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("matchedOrdersSize"));
		Integer ordersize = (Integer) mav.getModelMap().get("matchedOrdersSize");
		assertNotNull(ordersize);
		assertTrue(ordersize == 2);
	}
	
	/**
	 * @see PortletsControllerTest#ordersTable(String, Date, Date, boolean, boolean)
	 */
	@Test
	@Verifies(value = "should not populate model and view with table of orders if start date is after end date", method = "ordersTable(String, Date, Date, boolean, boolean)")
	public void ordersTable_ShouldNotPopulateModelAndViewWithTableOfOrdersIfStartDateIsAfterEndDate() throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = new GregorianCalendar(2010, 9, 10).getTime();
		Date endDate = new GregorianCalendar(2001, 0, 01).getTime();
		
		ModelAndView mav = portletsController.ordersTable(patientQuery, startDate, endDate, true, true);
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("matchedOrdersSize"));
		Integer ordersize = (Integer) mav.getModelMap().get("matchedOrdersSize");
		assertNotNull(ordersize);
		assertTrue(ordersize == 0);
		
		assertTrue(mav.getModelMap().containsKey("exceptionText"));
		String exception = (String) mav.getModelMap().get("exceptionText");
		assertNotNull(exception);
		assertTrue(exception.equals("radiology.crossDate"));
	}
	
	/**
	 * @see PortletsControllerTest#ordersTable(String, Date, Date, boolean, boolean)
	 */
	@Test
	@Verifies(value = "should populate model and view with empty table of orders associated with given end date and start date before any order has started", method = "ordersTable(String, Date, Date, boolean, boolean)")
	public void ordersTable_ShouldPopulateModelAndViewWithEmptyTableOfOrdersAssociatedWithGivenEndDateAndStartDateBeforeAnyOrderHasStarted()
	        throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = new GregorianCalendar(2014, 02, 01).getTime();
		Date endDate = new GregorianCalendar(2014, 04, 01).getTime();
		
		ModelAndView mav = portletsController.ordersTable(patientQuery, startDate, endDate, true, true);
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("matchedOrdersSize"));
		Integer ordersize = (Integer) mav.getModelMap().get("matchedOrdersSize");
		assertNotNull(ordersize);
		assertTrue(ordersize == 0);
	}
	
	/**
	 * @see PortletsControllerTest#ordersTable(String, Date, Date, boolean, boolean)
	 */
	@Test
	@Verifies(value = "should populate model and view with empty table of orders associated with given end date and start date after any order has started", method = "ordersTable(String, Date, Date, boolean, boolean)")
	public void ordersTable_ShouldPopulateModelAndViewWithEmptyTableOfOrdersAssociatedWithGivenEndDateAndStartDateAfterAnyOrderHasStarted()
	        throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = new GregorianCalendar(2016, 02, 01).getTime();
		Date endDate = new GregorianCalendar(2016, 04, 01).getTime();
		
		ModelAndView mav = portletsController.ordersTable(patientQuery, startDate, endDate, true, true);
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("matchedOrdersSize"));
		Integer ordersize = (Integer) mav.getModelMap().get("matchedOrdersSize");
		assertNotNull(ordersize);
		assertTrue(ordersize == 0);
	}
	
	/**
	 * @see PortletsControllerTest#ordersTable(String, Date, Date, boolean, boolean)
	 */
	@Test
	@Verifies(value = "should populate model and view with table of orders associated with given start date but given end date null", method = "ordersTable(String, Date, Date, boolean, boolean)")
	public void ordersTable_ShouldPopulateModelAndViewWithTableOfOrdersAssociatedWithGivenStartDateButGivenEndDateNull()
	        throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = new GregorianCalendar(2015, 02, 01).getTime();
		Date endDate = null;
		
		ModelAndView mav = portletsController.ordersTable(patientQuery, startDate, endDate, true, true);
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("matchedOrdersSize"));
		Integer ordersize = (Integer) mav.getModelMap().get("matchedOrdersSize");
		assertNotNull(ordersize);
		assertTrue(ordersize == 1);
	}
	
	/**
	 * @see PortletsControllerTest#ordersTable(String, Date, Date, boolean, boolean)
	 */
	@Test
	@Verifies(value = "should populate model and view with table of orders associated with given end date but given start date null", method = "ordersTable(String, Date, Date, boolean, boolean)")
	public void ordersTable_ShouldPopulateModelAndViewWithTableOfOrdersAssociatedWithGivenEndDateButGivenStartDateNull()
	        throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = null;
		Date endDate = new GregorianCalendar(2015, 02, 01).getTime();
		
		ModelAndView mav = portletsController.ordersTable(patientQuery, startDate, endDate, true, true);
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("matchedOrdersSize"));
		Integer ordersize = (Integer) mav.getModelMap().get("matchedOrdersSize");
		assertNotNull(ordersize);
		assertTrue(ordersize == 1);
	}
	
	/**
	 * @see PortletsControllerTest#ordersTable(String, Date, Date, boolean, boolean)
	 */
	@Test
	@Verifies(value = "should populate model and view with table of orders including obsId accessed as reading physician", method = "ordersTable(String, Date, Date, boolean, boolean)")
	public void ordersTable_ShouldPopulateModelAndViewWithTableOfOrdersIncludingObsIdAccessedAsReadingPhysician()
	        throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = null;
		Date endDate = new GregorianCalendar(2015, 02, 01).getTime();
		
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReadingPhysician());
		
		ModelAndView mav = portletsController.ordersTable(patientQuery, startDate, endDate, true, true);
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("matchedOrdersSize"));
		Integer ordersize = (Integer) mav.getModelMap().get("matchedOrdersSize");
		assertNotNull(ordersize);
		assertTrue(ordersize == 1);
		
		assertTrue(mav.getModelMap().containsKey("obsId"));
		String obsId = (String) mav.getModelMap().get("obsId");
		assertNotNull(obsId);
		assertTrue(obsId.equals("&obsId"));
	}
	
	/**
	 * @see PortletsControllerTest#ordersTable(String, Date, Date, boolean, boolean)
	 */
	@Test
	@Verifies(value = "should populate model and view with table of orders associated with given date range", method = "ordersTable(String, Date, Date, boolean, boolean)")
	public void ordersTable_ShouldPopulateModelAndViewWithTableOfOrdersAssociatedWithGivenDateRange() throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = new GregorianCalendar(2000, 02, 01).getTime();
		Date endDate = new GregorianCalendar(2020, 04, 01).getTime();
		
		ModelAndView mav = portletsController.ordersTable(patientQuery, startDate, endDate, true, true);
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("matchedOrdersSize"));
		Integer ordersize = (Integer) mav.getModelMap().get("matchedOrdersSize");
		assertNotNull(ordersize);
		assertTrue(ordersize == 2);
	}
	
	/**
	 * @see PortletsControllerTest#handleTypeMismatchException(TypeMismatchException)
	 */
	@Test
	@Verifies(value = "should populate model with exception text and invalid value", method = "handleTypeMismatchException(TypeMismatchException)")
	public void handleTypeMismatchException_shouldPopulateModelWithExceptionTextAndInvalidValue() throws Exception {
		
		//given
		TypeMismatchException typeMismatchException = new TypeMismatchException("13", Date.class);
		
		ModelAndView mav = portletsController.handleTypeMismatchException(typeMismatchException);
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("invalidValue"));
		String invalidValue = (String) mav.getModelMap().get("invalidValue");
		assertNotNull(invalidValue);
		assertTrue(invalidValue.equals("13"));
		
		assertTrue(mav.getModelMap().containsKey("exceptionText"));
		String exceptionText = (String) mav.getModelMap().get("exceptionText");
		assertNotNull(exceptionText);
		assertTrue(exceptionText.equals("typeMismatch.java.util.Date"));
	}
	
	/**
	 * @see PortletsControllerTest#handleTypeMismatchException(TypeMismatchException)
	 */
	@Test
	@Verifies(value = "should populate model with exception text", method = "handleTypeMismatchException(TypeMismatchException)")
	public void handleTypeMismatchException_shouldPopulateModelWithExceptionText() throws Exception {
		
		//given
		TypeMismatchException typeMismatchException = new TypeMismatchException("13", Object.class);
		
		ModelAndView mav = portletsController.handleTypeMismatchException(typeMismatchException);
		assertNotNull(mav);
		
		assertFalse(mav.getModelMap().containsKey("invalidValue"));
		
		assertTrue(mav.getModelMap().containsKey("exceptionText"));
		String exceptionText = (String) mav.getModelMap().get("exceptionText");
		assertNotNull(exceptionText);
	}
	
	/**
	 * @see PortletsControllerTest#getPatientInfoRoute()
	 */
	@Test
	@Verifies(value = "should return string with patient info route", method = "getPatientInfoRoute()")
	public void getPatientInfoRoute_ShouldReturnStringWithPatientInfoRoute() throws Exception {
		
		//given
		String patientInfoRoute = portletsController.getPatientInfoRoute();
		
		assertNotNull(patientInfoRoute);
		assertTrue(patientInfoRoute.equals("module/radiology/portlets/patientOverview"));
	}
	
}
