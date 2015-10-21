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

import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyOrder;
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
@PrepareForTest( { Context.class })
@PowerMockIgnore( { "org.apache.commons.logging.*" })
public class PortletsControllerTest {
	
	private List<Study> mockStudies;
	
	private List<RadiologyOrder> mockRadiologyOrders;
	
	private RadiologyOrder mockRadiologyOrder1;
	
	private RadiologyOrder mockRadiologyOrder2;
	
	private Patient mockPatient1;
	
	private Patient mockPatient3;
	
	@Mock
	private PatientService patientService;
	
	@Mock
	private RadiologyService radiologyService;
	
	@Mock
	private AdministrationService administrationService;
	
	@InjectMocks
	private PortletsController portletsController = new PortletsController();
	
	@Before
	public void runBeforeAllTests() {
		PowerMockito.mockStatic(Context.class);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		mockRadiologyOrders = new ArrayList<RadiologyOrder>();
		mockRadiologyOrder1 = RadiologyTestData.getMockRadiologyOrder1();
		mockRadiologyOrder2 = RadiologyTestData.getMockRadiologyOrder2();
		mockRadiologyOrders.add(mockRadiologyOrder1);
		mockRadiologyOrders.add(mockRadiologyOrder2);
		
		mockStudies = new ArrayList<Study>();
		mockStudies.add(RadiologyTestData.getMockStudy1PostSave());
		mockStudies.add(RadiologyTestData.getMockStudy2PostSave());
		
		mockPatient1 = RadiologyTestData.getMockPatient1();
		mockPatient3 = RadiologyTestData.getMockPatient3();
		
		when(Context.getDateFormat()).thenReturn(simpleDateFormat);
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReferringPhysician());
		when(radiologyService.getStudiesByRadiologyOrders(mockRadiologyOrders)).thenReturn(mockStudies);
		when(radiologyService.getRadiologyOrdersByPatients(patientService.getPatients(""))).thenReturn(mockRadiologyOrders);
		when(radiologyService.getRadiologyOrdersByPatients(Arrays.asList(mockPatient1))).thenReturn(
		    Arrays.asList(mockRadiologyOrder1));
		when(radiologyService.getRadiologyOrdersByPatients(Arrays.asList(mockPatient3))).thenReturn(
		    new ArrayList<RadiologyOrder>());
		when(patientService.getPatients("Johnny")).thenReturn(new ArrayList<Patient>());
		when(patientService.getPatients("Joh")).thenReturn(Arrays.asList(mockPatient1));
		when(patientService.getPatients(mockPatient3.getFamilyName())).thenReturn(Arrays.asList(mockPatient3));
		
	}
	
	/**
	 * @see PortletsController#getRadiologyOrdersByPatientQueryAndDateRange(String, Date, Date)
	 */
	@Test
	@Verifies(value = "should populate model and view with table of orders associated with given date range null", method = "getRadiologyOrdersByPatientQueryAndDateRange(String, Date, Date)")
	public void getRadiologyOrdersByPatientQueryAndDateRange_shouldPopulateModelAndViewWithTableOfOrdersAssociatedWithGivenDateRangeNull()
	        throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = null;
		Date endDate = null;
		
		ModelAndView mav = portletsController.getRadiologyOrdersByPatientQueryAndDateRange(patientQuery, startDate, endDate);
		assertNotNull(mav);
		
		assertThat(mav.getModelMap(), hasKey("orderList"));
		List<RadiologyOrder> orderList = (List<RadiologyOrder>) mav.getModelMap().get("orderList");
		assertNotNull(orderList);
		assertEquals(mockRadiologyOrders, orderList);
	}
	
	/**
	 * @see PortletsController#getRadiologyOrdersByPatientQueryAndDateRange(String, Date, Date)
	 */
	@Test
	@Verifies(value = "should not populate model and view with table of orders if start date is after end date", method = "getRadiologyOrdersByPatientQueryAndDateRange(String, Date, Date)")
	public void ordersTable_shouldNotPopulateModelAndViewWithTableOfOrdersIfStartDateIsAfterEndDate() throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		Date endDate = new GregorianCalendar(2001, Calendar.JANUARY, 01).getTime();
		
		ModelAndView mav = portletsController.getRadiologyOrdersByPatientQueryAndDateRange(patientQuery, startDate, endDate);
		assertNotNull(mav);
		
		assertThat(mav.getModelMap(), hasKey("orderList"));
		List<RadiologyOrder> orderList = (List<RadiologyOrder>) mav.getModelMap().get("orderList");
		assertNotNull(orderList);
		assertEquals(new ArrayList<RadiologyOrder>(), orderList);
		
		assertTrue(mav.getModelMap().containsKey("exceptionText"));
		String exception = (String) mav.getModelMap().get("exceptionText");
		assertNotNull(exception);
		assertEquals("radiology.crossDate", exception);
	}
	
	/**
	 * @see PortletsController#getRadiologyOrdersByPatientQueryAndDateRange(String, Date, Date)
	 */
	@Test
	@Verifies(value = "should populate model and view with table of orders including obsId accessed as reading physician", method = "getRadiologyOrdersByPatientQueryAndDateRange(String, Date, Date)")
	public void getRadiologyOrdersByPatientQueryAndDateRange_shouldPopulateModelAndViewWithTableOfOrdersIncludingObsIdAccessedAsReadingPhysician()
	        throws Exception {
		
		//given
		String patientQuery = "";
		Date startDate = null;
		Date endDate = new GregorianCalendar(2015, Calendar.MARCH, 01).getTime();
		
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReadingPhysician());
		
		ModelAndView mav = portletsController.getRadiologyOrdersByPatientQueryAndDateRange(patientQuery, startDate, endDate);
		assertNotNull(mav);
		
		assertThat(mav.getModelMap(), hasKey("orderList"));
		List<RadiologyOrder> orderList = (List<RadiologyOrder>) mav.getModelMap().get("orderList");
		assertNotNull(orderList);
		assertEquals(Arrays.asList(mockRadiologyOrder1), orderList);
		
		assertTrue(mav.getModelMap().containsKey("obsId"));
		String obsId = (String) mav.getModelMap().get("obsId");
		assertNotNull(obsId);
		assertEquals("&obsId", obsId);
	}
	
	/**
	 * @see PortletsController#filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)
	 */
	@Test
	@Verifies(value = "should return list of orders matching a given date range", method = "filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)")
	public void filterRadiologyOrdersByDateRange_shouldPopulateModelAndViewWithTableOfOrdersAssociatedWithGivenDateRange()
	        throws Exception {
		
		//given
		Date startDate = new GregorianCalendar(2000, Calendar.MARCH, 01).getTime();
		Date endDate = new GregorianCalendar(2020, Calendar.MAY, 01).getTime();
		
		Method filterRadiologyOrdersByDateRangeMethod = portletsController.getClass().getDeclaredMethod(
		    "filterRadiologyOrdersByDateRange",
		    new Class[] { java.util.List.class, java.util.Date.class, java.util.Date.class });
		filterRadiologyOrdersByDateRangeMethod.setAccessible(true);
		
		List<RadiologyOrder> filteredRadiologyOrdersByDateRange = (List<RadiologyOrder>) filterRadiologyOrdersByDateRangeMethod
		        .invoke(portletsController, new Object[] { mockRadiologyOrders, startDate, endDate });
		
		assertNotNull(filteredRadiologyOrdersByDateRange);
		assertEquals(mockRadiologyOrders, filteredRadiologyOrdersByDateRange);
	}
	
	/**
	 * @see PortletsController#filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)
	 */
	@Test
	@Verifies(value = "should return list of all orders with start date if start date is null and end date is null", method = "filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)")
	public void filterRadiologyOrdersByDateRange_shouldReturnListOfOrdersAssociatedWithGivenDateRangeNull() throws Exception {
		
		//given
		Date startDate = null;
		Date endDate = null;
		
		Method filterRadiologyOrdersByDateRangeMethod = portletsController.getClass().getDeclaredMethod(
		    "filterRadiologyOrdersByDateRange",
		    new Class[] { java.util.List.class, java.util.Date.class, java.util.Date.class });
		filterRadiologyOrdersByDateRangeMethod.setAccessible(true);
		
		List<RadiologyOrder> filteredRadiologyOrdersByDateRange = (List<RadiologyOrder>) filterRadiologyOrdersByDateRangeMethod
		        .invoke(portletsController, new Object[] { mockRadiologyOrders, startDate, endDate });
		
		assertNotNull(filteredRadiologyOrdersByDateRange);
		assertEquals(mockRadiologyOrders, filteredRadiologyOrdersByDateRange);
	}
	
	/**
	 * @see PortletsController#filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)
	 */
	@Test
	@Verifies(value = "should return empty list of orders with given end date and start date before any order has started", method = "filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)")
	public void filterRadiologyOrdersByDateRange_shouldReturnListOfOrdersAssociatedWithGivenEndDateAndStartDateBeforeAnyOrderHasStarted()
	        throws Exception {
		
		//given
		Date startDate = new GregorianCalendar(2014, Calendar.MARCH, 01).getTime();
		Date endDate = new GregorianCalendar(2014, Calendar.MAY, 01).getTime();
		
		Method filterRadiologyOrdersByDateRangeMethod = portletsController.getClass().getDeclaredMethod(
		    "filterRadiologyOrdersByDateRange",
		    new Class[] { java.util.List.class, java.util.Date.class, java.util.Date.class });
		filterRadiologyOrdersByDateRangeMethod.setAccessible(true);
		
		List<RadiologyOrder> filteredRadiologyOrdersByDateRange = (List<RadiologyOrder>) filterRadiologyOrdersByDateRangeMethod
		        .invoke(portletsController, new Object[] { mockRadiologyOrders, startDate, endDate });
		
		assertNotNull(filteredRadiologyOrdersByDateRange);
		assertEquals(new ArrayList<RadiologyOrder>(), filteredRadiologyOrdersByDateRange);
	}
	
	/**
	 * @see PortletsController#filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)
	 */
	@Test
	@Verifies(value = "should return empty list of orders with given end date and start date after any order has started", method = "filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)")
	public void filterRadiologyOrdersByDateRange_shouldReturnListOfOrdersAssociatedWithGivenEndDateAndStartDateAfterAnyOrderHasStarted()
	        throws Exception {
		
		//given
		Date startDate = new GregorianCalendar(2016, Calendar.MARCH, 01).getTime();
		Date endDate = new GregorianCalendar(2016, Calendar.MAY, 01).getTime();
		
		Method filterRadiologyOrdersByDateRangeMethod = portletsController.getClass().getDeclaredMethod(
		    "filterRadiologyOrdersByDateRange",
		    new Class[] { java.util.List.class, java.util.Date.class, java.util.Date.class });
		filterRadiologyOrdersByDateRangeMethod.setAccessible(true);
		
		List<RadiologyOrder> filteredRadiologyOrdersByDateRange = (List<RadiologyOrder>) filterRadiologyOrdersByDateRangeMethod
		        .invoke(portletsController, new Object[] { mockRadiologyOrders, startDate, endDate });
		
		assertNotNull(filteredRadiologyOrdersByDateRange);
		assertEquals(new ArrayList<RadiologyOrder>(), filteredRadiologyOrdersByDateRange);
	}
	
	/**
	 * @see PortletsController#filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)
	 */
	@Test
	@Verifies(value = "should return list of orders started after given start date but given end date null", method = "filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)")
	public void filterRadiologyOrdersByDateRange_shouldPopulateModelAndViewWithTableOfOrdersAssociatedWithGivenStartDateButGivenEndDateNull()
	        throws Exception {
		
		//given
		Date startDate = new GregorianCalendar(2015, Calendar.MARCH, 01).getTime();
		Date endDate = null;
		
		Method filterRadiologyOrdersByDateRangeMethod = portletsController.getClass().getDeclaredMethod(
		    "filterRadiologyOrdersByDateRange",
		    new Class[] { java.util.List.class, java.util.Date.class, java.util.Date.class });
		filterRadiologyOrdersByDateRangeMethod.setAccessible(true);
		
		List<RadiologyOrder> filteredRadiologyOrdersByDateRange = (List<RadiologyOrder>) filterRadiologyOrdersByDateRangeMethod
		        .invoke(portletsController, new Object[] { mockRadiologyOrders, startDate, endDate });
		
		assertNotNull(filteredRadiologyOrdersByDateRange);
		assertEquals(Arrays.asList(mockRadiologyOrder2), filteredRadiologyOrdersByDateRange);
	}
	
	/**
	 * @see PortletsController#filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)
	 */
	@Test
	@Verifies(value = "should return list of orders started before given end date but given start date null", method = "filterRadiologyOrdersByDateRange(List<RadiologyOrder>, Date, Date)")
	public void filterRadiologyOrdersByDateRange_shouldReturnListOfOrdersAssociatedWithGivenEndDateButGivenStartDateNull()
	        throws Exception {
		
		//given
		Date startDate = null;
		Date endDate = new GregorianCalendar(2015, Calendar.MARCH, 01).getTime();
		
		Method filterRadiologyOrdersByDateRangeMethod = portletsController.getClass().getDeclaredMethod(
		    "filterRadiologyOrdersByDateRange",
		    new Class[] { java.util.List.class, java.util.Date.class, java.util.Date.class });
		filterRadiologyOrdersByDateRangeMethod.setAccessible(true);
		
		List<RadiologyOrder> filteredRadiologyOrdersByDateRange = (List<RadiologyOrder>) filterRadiologyOrdersByDateRangeMethod
		        .invoke(portletsController, new Object[] { mockRadiologyOrders, startDate, endDate });
		
		assertNotNull(filteredRadiologyOrdersByDateRange);
		assertEquals(Arrays.asList(mockRadiologyOrder1), filteredRadiologyOrdersByDateRange);
	}
	
	/**
	 * @see PortletsController#isEndDateBeforeStartDate(Date, Date)
	 */
	@Test
	@Verifies(value = "should return true if end date is after start date", method = "isEndDateBeforeStartDate(Date, Date)")
	public void isEndDateBeforeStartDate_shouldReturnTrueIfEndDateIsAfterStartDate() throws Exception {
		
		Method isEndDateBeforeStartDateMethod = portletsController.getClass().getDeclaredMethod("isEndDateBeforeStartDate",
		    new Class[] { java.util.Date.class, java.util.Date.class });
		isEndDateBeforeStartDateMethod.setAccessible(true);
		
		Date startDate = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		Date endDate = new GregorianCalendar(2001, Calendar.JANUARY, 01).getTime();
		
		Boolean isEndDateBeforeStartDate = (Boolean) isEndDateBeforeStartDateMethod.invoke(portletsController, new Object[] {
		        startDate, endDate });
		assertThat(isEndDateBeforeStartDate, is(true));
	}
	
	/**
	 * @see PortletsController#isEndDateBeforeStartDate(Date, Date)
	 */
	@Test
	@Verifies(value = "should return false if end date is not after start date", method = "isEndDateBeforeStartDate(Date, Date)")
	public void isEndDateBeforeStartDate_shouldReturnFalseIfEndDateIsNotAfterStartDate() throws Exception {
		
		Method isEndDateBeforeStartDateMethod = portletsController.getClass().getDeclaredMethod("isEndDateBeforeStartDate",
		    new Class[] { java.util.Date.class, java.util.Date.class });
		isEndDateBeforeStartDateMethod.setAccessible(true);
		
		Date startDate = new GregorianCalendar(2001, Calendar.JANUARY, 01).getTime();
		Date endDate = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		
		Boolean isEndDateBeforeStartDate = (Boolean) isEndDateBeforeStartDateMethod.invoke(portletsController, new Object[] {
		        startDate, endDate });
		assertThat(isEndDateBeforeStartDate, is(false));
	}
	
	/**
	 * @see PortletsController#isEndDateBeforeStartDate(Date, Date)
	 */
	@Test
	@Verifies(value = "should return false with given start date but end date null", method = "isEndDateBeforeStartDate(Date, Date)")
	public void isEndDateBeforeStartDate_shouldReturnFalsewithGivenStartDateButEndDateNull() throws Exception {
		
		Method isEndDateBeforeStartDateMethod = portletsController.getClass().getDeclaredMethod("isEndDateBeforeStartDate",
		    new Class[] { java.util.Date.class, java.util.Date.class });
		isEndDateBeforeStartDateMethod.setAccessible(true);
		
		Date startDate = new GregorianCalendar(2001, Calendar.JANUARY, 01).getTime();
		Date endDate = null;
		
		Boolean isEndDateBeforeStartDate = (Boolean) isEndDateBeforeStartDateMethod.invoke(portletsController, new Object[] {
		        startDate, endDate });
		assertThat(isEndDateBeforeStartDate, is(false));
	}
	
	/**
	 * @see PortletsController#isEndDateBeforeStartDate(Date, Date)
	 */
	@Test
	@Verifies(value = "should return false with given end date but start date null", method = "isEndDateBeforeStartDate(Date, Date)")
	public void isEndDateBeforeStartDate_shouldReturnFalsewithGivenEndDateButStartDateNull() throws Exception {
		
		Method isEndDateBeforeStartDateMethod = portletsController.getClass().getDeclaredMethod("isEndDateBeforeStartDate",
		    new Class[] { java.util.Date.class, java.util.Date.class });
		isEndDateBeforeStartDateMethod.setAccessible(true);
		
		Date startDate = new GregorianCalendar(2001, Calendar.JANUARY, 01).getTime();
		Date endDate = null;
		
		Boolean isEndDateBeforeStartDate = (Boolean) isEndDateBeforeStartDateMethod.invoke(portletsController, new Object[] {
		        startDate, endDate });
		assertThat(isEndDateBeforeStartDate, is(false));
	}
	
	/**
	 * @see PortletsController#isEndDateBeforeStartDate(Date, Date)
	 */
	@Test
	@Verifies(value = "should return false with given start date and end date null", method = "isEndDateBeforeStartDate(Date, Date)")
	public void isEndDateBeforeStartDate_shouldReturnFalsewithGivenStartDateAndEndDateNull() throws Exception {
		
		Method isEndDateBeforeStartDateMethod = portletsController.getClass().getDeclaredMethod("isEndDateBeforeStartDate",
		    new Class[] { java.util.Date.class, java.util.Date.class });
		isEndDateBeforeStartDateMethod.setAccessible(true);
		
		Date startDate = null;
		Date endDate = null;
		
		Boolean isEndDateBeforeStartDate = (Boolean) isEndDateBeforeStartDateMethod.invoke(portletsController, new Object[] {
		        startDate, endDate });
		assertThat(isEndDateBeforeStartDate, is(false));
	}
	
	/**
	 * @see PortletsController#handleTypeMismatchException(TypeMismatchException)
	 */
	@Test
	@Verifies(value = "should populate model with exception text and invalid value", method = "handleTypeMismatchException(TypeMismatchException)")
	public void handleTypeMismatchException_shouldPopulateModelWithExceptionTextAndInvalidValue() throws Exception {
		
		//given
		TypeMismatchException typeMismatchException = new TypeMismatchException("13", Date.class);
		
		ModelAndView mav = portletsController.handleTypeMismatchException(typeMismatchException);
		assertNotNull(mav);
		
		assertThat(mav.getModelMap(), hasKey("invalidValue"));
		String invalidValue = (String) mav.getModelMap().get("invalidValue");
		assertNotNull(invalidValue);
		assertEquals("13", invalidValue);
		
		assertThat(mav.getModelMap(), hasKey("exceptionText"));
		String exceptionText = (String) mav.getModelMap().get("exceptionText");
		assertNotNull(exceptionText);
		assertEquals("typeMismatch.java.util.Date", exceptionText);
	}
	
	/**
	 * @see PortletsController#handleTypeMismatchException(TypeMismatchException)
	 */
	@Test
	@Verifies(value = "should populate model with exception text", method = "handleTypeMismatchException(TypeMismatchException)")
	public void handleTypeMismatchException_shouldPopulateModelWithExceptionText() throws Exception {
		
		//given
		TypeMismatchException typeMismatchException = new TypeMismatchException("13", Object.class);
		
		ModelAndView mav = portletsController.handleTypeMismatchException(typeMismatchException);
		assertNotNull(mav);
		
		assertFalse(mav.getModelMap().containsKey("invalidValue"));
		
		assertThat(mav.getModelMap(), hasKey("exceptionText"));
		String exceptionText = (String) mav.getModelMap().get("exceptionText");
		assertNotNull(exceptionText);
	}
	
	/**
	 * @see PortletsController#getPatientInfoRoute()
	 */
	@Test
	@Verifies(value = "should return string with patient info route", method = "getPatientInfoRoute()")
	public void getPatientInfoRoute_ShouldReturnStringWithPatientInfoRoute() throws Exception {
		
		//given
		String patientInfoRoute = portletsController.getPatientInfoRoute();
		
		assertNotNull(patientInfoRoute);
		assertEquals("module/radiology/portlets/patientOverview", patientInfoRoute);
	}
	
	/**
	 * @see PortletsController#getRadiologyOrdersForPatientQuery(String)
	 */
	@Test
	@Verifies(value = "should return list of all radiology orders given patientQuery empty", method = "getRadiologyOrdersForPatientQuery(String)")
	public void getRadiologyOrdersForPatientQuery_shouldReturnListOfAllRadiologyOrdersGivenPatientQueryEmpty()
	        throws Exception {
		
		Method getRadiologyOrdersForPatientQueryMethod = portletsController.getClass().getDeclaredMethod(
		    "getRadiologyOrdersForPatientQuery", new Class[] { String.class });
		getRadiologyOrdersForPatientQueryMethod.setAccessible(true);
		
		String patientQuery = "";
		
		List<RadiologyOrder> RadiologyOrdersForPatientQuery = (List<RadiologyOrder>) getRadiologyOrdersForPatientQueryMethod
		        .invoke(portletsController, new Object[] { patientQuery });
		assertEquals(mockRadiologyOrders, RadiologyOrdersForPatientQuery);
	}
	
	/**
	 * @see PortletsController#getRadiologyOrdersForPatientQuery(String)
	 */
	@Test
	@Verifies(value = "should return list of all radiology orders given patientQuery null", method = "getRadiologyOrdersForPatientQuery(String)")
	public void getRadiologyOrdersForPatientQuery_shouldReturnListOfAllRadiologyOrdersGivenPatientQueryNull()
	        throws Exception {
		
		Method getRadiologyOrdersForPatientQueryMethod = portletsController.getClass().getDeclaredMethod(
		    "getRadiologyOrdersForPatientQuery", new Class[] { String.class });
		getRadiologyOrdersForPatientQueryMethod.setAccessible(true);
		
		String patientQuery = null;
		
		List<RadiologyOrder> RadiologyOrdersForPatientQuery = (List<RadiologyOrder>) getRadiologyOrdersForPatientQueryMethod
		        .invoke(portletsController, new Object[] { patientQuery });
		assertEquals(mockRadiologyOrders, RadiologyOrdersForPatientQuery);
	}
	
	/**
	 * @see PortletsController#getRadiologyOrdersForPatientQuery(String)
	 */
	@Test
	@Verifies(value = "should return list of all radiology orders given patientQuery matching no patient", method = "getRadiologyOrdersForPatientQuery(String)")
	public void getRadiologyOrdersForPatientQuery_shouldReturnListOfAllRadiologyOrdersGivenPatientQueryMatchingNoPatient()
	        throws Exception {
		
		Method getRadiologyOrdersForPatientQueryMethod = portletsController.getClass().getDeclaredMethod(
		    "getRadiologyOrdersForPatientQuery", new Class[] { String.class });
		getRadiologyOrdersForPatientQueryMethod.setAccessible(true);
		
		String patientQuery = "Johnny";
		
		List<RadiologyOrder> RadiologyOrdersForPatientQuery = (List<RadiologyOrder>) getRadiologyOrdersForPatientQueryMethod
		        .invoke(portletsController, new Object[] { patientQuery });
		assertEquals(mockRadiologyOrders, RadiologyOrdersForPatientQuery);
	}
	
	/**
	 * @see PortletsController#getRadiologyOrdersForPatientQuery(String)
	 */
	@Test
	@Verifies(value = "should return empty list for patients without radiology orders", method = "getRadiologyOrdersForPatientQuery(String)")
	public void getRadiologyOrdersForPatientQuery_shouldReturnEmptyListForPatientsWithoutRadiologyOrders() throws Exception {
		
		Method getRadiologyOrdersForPatientQueryMethod = portletsController.getClass().getDeclaredMethod(
		    "getRadiologyOrdersForPatientQuery", new Class[] { String.class });
		getRadiologyOrdersForPatientQueryMethod.setAccessible(true);
		
		String patientQuery = RadiologyTestData.getMockPatient3().getFamilyName();
		
		List<RadiologyOrder> RadiologyOrdersForPatientQuery = (List<RadiologyOrder>) getRadiologyOrdersForPatientQueryMethod
		        .invoke(portletsController, new Object[] { patientQuery });
		assertEquals(new ArrayList<RadiologyOrder>(), RadiologyOrdersForPatientQuery);
	}
	
	/**
	 * @see PortletsController#getRadiologyOrdersForPatientQuery(String)
	 */
	@Test
	@Verifies(value = "should return list of all radiology orders for a patient given valid patientQuery", method = "getRadiologyOrdersForPatientQuery(String)")
	public void getRadiologyOrdersForPatientQuery_shouldReturnListOfAllRadiologyOrdersForGivenPatientQuery()
	        throws Exception {
		
		Method getRadiologyOrdersForPatientQueryMethod = portletsController.getClass().getDeclaredMethod(
		    "getRadiologyOrdersForPatientQuery", new Class[] { String.class });
		getRadiologyOrdersForPatientQueryMethod.setAccessible(true);
		
		String patientQuery = "Joh";
		
		List<RadiologyOrder> RadiologyOrdersForPatientQuery = (List<RadiologyOrder>) getRadiologyOrdersForPatientQueryMethod
		        .invoke(portletsController, new Object[] { patientQuery });
		assertEquals(Arrays.asList(mockRadiologyOrder1), RadiologyOrdersForPatientQuery);
	}
	
}
