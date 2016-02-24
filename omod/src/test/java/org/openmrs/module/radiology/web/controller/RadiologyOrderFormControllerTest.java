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
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderService;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.test.Verifies;
import org.openmrs.web.WebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyOrderFormController}
 */
public class RadiologyOrderFormControllerTest extends BaseContextMockTest {
	
	@Mock
	private OrderService orderService;
	
	@Mock
	private RadiologyService radiologyService;
	
	@Mock
	private RadiologyProperties radiologyProperties;
	
	@InjectMocks
	private RadiologyOrderFormController radiologyOrderFormController = new RadiologyOrderFormController();
	
	@Before
	public void runBeforeAllTests() {
		when(radiologyProperties.getRadiologyTestOrderType()).thenReturn(RadiologyTestData.getMockRadiologyOrderType());
	}
	
	/**
	 * @see RadiologyOrderFormController#getRadiologyOrderFormWithNewRadiologyOrder()
	 */
	@Test
	@Verifies(value = "should populate model and view with new radiology order", method = "getRadiologyOrderFormWithNewRadiologyOrder()")
	public void getRadiologyOrderFormWithNewRadiologyOrder_shouldPopulateModelAndViewWithNewRadiologyOrder()
	        throws Exception {
		
		ModelAndView modelAndView = radiologyOrderFormController.getRadiologyOrderFormWithNewRadiologyOrder();
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNull(order.getOrderId());
		
		assertNotNull(order.getStudy());
		assertNull(order.getStudy().getStudyId());
		
		assertNull(order.getOrderer());
	}
	
	/**
	 * @see RadiologyOrderFormController#getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(Integer)
	 */
	@Test
	@Verifies(value = "should populate model and view with new radiology order prefilled with given patient", method = "getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(Integer)")
	public void getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient_shouldPopulateModelAndViewWithNewRadiologyOrderPrefilledWithGivenPatient()

	throws Exception {
		
		//given
		Patient mockPatient = RadiologyTestData.getMockPatient1();
		
		ModelAndView modelAndView = radiologyOrderFormController
		        .getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(mockPatient);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNull(order.getOrderId());
		
		assertNotNull(order.getStudy());
		assertNull(order.getStudy().getStudyId());
		
		assertNotNull(order.getPatient());
		assertThat(order.getPatient(), is(mockPatient));
		
		assertThat(modelAndView.getModelMap(), hasKey("patientId"));
		Integer patientId = (Integer) modelAndView.getModelMap().get("patientId");
		assertThat(patientId, is(mockPatient.getPatientId()));
	}
	
	/**
	 * @see RadiologyOrderFormController#getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(Integer)
	 */
	@Test
	@Verifies(value = "should populate model and view with new radiology order without prefilled patient if given patient is null", method = "getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(Integer)")
	public void getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient_shouldPopulateModelAndViewWithNewRadiologyOrderWithoutPrefilledPatientIfGivenPatientIsNull()
	        throws Exception {
		
		ModelAndView modelAndView = radiologyOrderFormController
		        .getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(null);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNull(order.getOrderId());
		
		assertNotNull(order.getStudy());
		assertNull(order.getStudy().getStudyId());
		
		assertNull(order.getPatient());
		
		assertThat(modelAndView.getModelMap(), not(hasKey("patientId")));
	}
	
	/**
	 * @see RadiologyOrderFormController#getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should populate model and view with existing radiology order matching given order id", method = "getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(Integer)")
	public void getRadiologyOrderFormWithExistingRadiologyOrderByOrderId_shouldPopulateModelAndViewWithExistingRadiologyOrderMatchingGivenOrderId()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		
		ModelAndView modelAndView = radiologyOrderFormController
		        .getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(mockRadiologyOrder);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertThat(order, is(mockRadiologyOrder));
	}
	
	/**
	 * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order,
	 * BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to order saved and redirect to radiology order list when save study was successful", method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
	public void postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToOrderSavedAndRedirectToRadiologyOrderListWhenSaveStudyWasSuccessful()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		mockRadiologyOrder.getStudy().setMwlStatus(MwlStatus.SAVE_OK);
		
		when(radiologyService.placeRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, null,
		    mockRadiologyOrder, mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/module/radiology/radiologyOrder.list"));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.saved"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order,
	 * BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to order saved and redirect to patient dashboard when save study was successful and given patient id", method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
	public void postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToOrderSavedAndRedirectToPatientDashboardWhenSaveStudyWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		mockRadiologyOrder.getStudy().setMwlStatus(MwlStatus.SAVE_OK);
		
		when(radiologyService.placeRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder
		        .getPatient().getPatientId(), mockRadiologyOrder, mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockRadiologyOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.saved"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order,
	 * BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to saved fail worklist and redirect to patient dashboard when save study was not successful and given patient id", method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
	public void postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToSavedFailWorklistAndRedirectToPatientDashboardWhenSaveStudyWasNotSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		mockRadiologyOrder.getStudy().setMwlStatus(MwlStatus.SAVE_ERR);
		
		when(radiologyService.placeRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder
		        .getPatient().getPatientId(), mockRadiologyOrder, mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockRadiologyOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("radiology.savedFailWorklist"));
		
		mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		mockRadiologyOrder.getStudy().setMwlStatus(MwlStatus.SAVE_ERR);
		when(radiologyService.placeRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		
		modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder.getPatient()
		        .getPatientId(), mockRadiologyOrder, mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockRadiologyOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("radiology.savedFailWorklist"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order,
	 * BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to study performed when study performed status is in progress and request was issued by radiology scheduler", method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
	public void postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToStudyPerformedWhenStudyPerformedStatusIsInProgressAndRequestWasIssuedByRadiologyScheduler()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		mockRadiologyOrder.getStudy().setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		User mockRadiologyScheduler = RadiologyTestData.getMockRadiologyScheduler();
		
		when(userContext.getAuthenticatedUser()).thenReturn(mockRadiologyScheduler);
		when(radiologyService.placeRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder
		        .getPatient().getPatientId(), mockRadiologyOrder, mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR), is("radiology.studyPerformed"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order,
	 * BindingResult)
	 */
	@Test
	@Verifies(value = "should not redirect if radiology order is not valid according to order validator", method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
	public void postSaveRadiologyOrder_shouldNotRedirectIfRadiologyOrderIsNotValidAccordingToOrderValidator()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		User mockRadiologyScheduler = RadiologyTestData.getMockRadiologyScheduler();
		
		when(userContext.getAuthenticatedUser()).thenReturn(mockRadiologyScheduler);
		when(radiologyService.placeRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(true);
		
		ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder
		        .getPatient().getPatientId(), mockRadiologyOrder, mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postDiscontinueRadiologyOrder(HttpServletRequest,
	 * HttpServletResponse, Order, String, Date)
	 */
	@Test
	@Verifies(value = "should discontinue non discontinued order and redirect to discontinuation order", method = "postDiscontinueRadiologyOrder(HttpServletRequest, HttpServletResponse, Order, String, Date)")
	public void postDiscontinueRadiologyOrder_shouldDiscontinueNonDiscontinuedOrderAndRedirectToDiscontinuationOrder()
	        throws Exception {
		//given
		RadiologyOrder mockRadiologyOrderToDiscontinue = RadiologyTestData.getMockRadiologyOrder1();
		mockRadiologyOrderToDiscontinue.getStudy().setMwlStatus(MwlStatus.DISCONTINUE_OK);
		String discontinueReason = "Wrong Procedure";
		Date discontinueDate = new GregorianCalendar(2015, Calendar.JANUARY, 01).getTime();
		
		Order mockDiscontinuationOrder = new Order();
		mockDiscontinuationOrder.setOrderId(2);
		mockDiscontinuationOrder.setAction(Order.Action.DISCONTINUE);
		mockDiscontinuationOrder.setOrderer(mockRadiologyOrderToDiscontinue.getOrderer());
		mockDiscontinuationOrder.setOrderReasonNonCoded(discontinueReason);
		mockDiscontinuationOrder.setDateActivated(discontinueDate);
		mockDiscontinuationOrder.setPreviousOrder(mockRadiologyOrderToDiscontinue);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("discontinueOrder", "discontinueOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrderToDiscontinue.getOrderId())).thenReturn(
		    mockRadiologyOrderToDiscontinue);
		when(
		    radiologyService.discontinueRadiologyOrder(mockRadiologyOrderToDiscontinue, mockDiscontinuationOrder
		            .getOrderer(), mockDiscontinuationOrder.getDateActivated(), mockDiscontinuationOrder
		            .getOrderReasonNonCoded())).thenReturn(mockDiscontinuationOrder);
		
		BindingResult orderErrors = mock(BindingResult.class);
		assertThat(mockRadiologyOrderToDiscontinue.getAction(), is(Order.Action.NEW));
		ModelAndView modelAndView = radiologyOrderFormController.postDiscontinueRadiologyOrder(mockRequest, null,
		    mockRadiologyOrderToDiscontinue, mockDiscontinuationOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/module/radiology/radiologyOrder.form?orderId="
		        + mockDiscontinuationOrder.getOrderId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.discontinuedSuccessfully"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postDiscontinueRadiologyOrder(HttpServletRequest,
	 * HttpServletResponse, Order, String, Date)
	 */
	@Test
	@Verifies(value = "should not redirect if discontinuation failed through date in the future", method = "postDiscontinueRadiologyOrder(HttpServletRequest, HttpServletResponse, Order, String, Date)")
	public void postDiscontinueRadiologyOrder_shouldNotRedirectIfDiscontinuationFailedThroughDateInTheFuture()
	        throws Exception {
		//given
		RadiologyOrder mockRadiologyOrderToDiscontinue = RadiologyTestData.getMockRadiologyOrder1();
		mockRadiologyOrderToDiscontinue.getStudy().setMwlStatus(MwlStatus.DISCONTINUE_OK);
		String discontinueReason = "Wrong Procedure";
		Date discontinueDate = new Date();
		APIException apiException = new APIException("Discontinue date cannot be in the future");
		
		Order mockDiscontinuationOrder = new Order();
		mockDiscontinuationOrder.setOrderId(2);
		mockDiscontinuationOrder.setAction(Order.Action.DISCONTINUE);
		mockDiscontinuationOrder.setOrderer(mockRadiologyOrderToDiscontinue.getOrderer());
		mockDiscontinuationOrder.setOrderReasonNonCoded(discontinueReason);
		mockDiscontinuationOrder.setDateActivated(discontinueDate);
		mockDiscontinuationOrder.setPreviousOrder(mockRadiologyOrderToDiscontinue);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("discontinueOrder", "discontinueOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrderToDiscontinue.getOrderId())).thenReturn(
		    mockRadiologyOrderToDiscontinue);
		when(
		    radiologyService.discontinueRadiologyOrder(mockRadiologyOrderToDiscontinue, mockDiscontinuationOrder
		            .getOrderer(), mockDiscontinuationOrder.getDateActivated(), mockDiscontinuationOrder
		            .getOrderReasonNonCoded())).thenThrow(apiException);
		
		BindingResult orderErrors = mock(BindingResult.class);
		assertThat(mockRadiologyOrderToDiscontinue.getAction(), is(Order.Action.NEW));
		ModelAndView modelAndView = radiologyOrderFormController.postDiscontinueRadiologyOrder(mockRequest, null,
		    mockRadiologyOrderToDiscontinue, mockDiscontinuationOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertThat(order, is((Order) mockRadiologyOrderToDiscontinue));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertThat(radiologyOrder, is(mockRadiologyOrderToDiscontinue));
		
		assertNotNull(mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR),
		    is("Discontinue date cannot be in the future"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postDiscontinueRadiologyOrder(HttpServletRequest,
	 * HttpServletResponse, Order, String, Date)
	 */
	@Test
	@Verifies(value = "should not redirect if discontinuation failed in pacs", method = "postDiscontinueRadiologyOrder(HttpServletRequest, HttpServletResponse, Order, String, Date)")
	public void postDiscontinueRadiologyOrder_shouldNotRedirectIfDiscontinuationFailedInPacs() throws Exception {
		//given
		RadiologyOrder mockRadiologyOrderToDiscontinue = RadiologyTestData.getMockRadiologyOrder1();
		mockRadiologyOrderToDiscontinue.getStudy().setMwlStatus(MwlStatus.DISCONTINUE_ERR);
		String discontinueReason = "Wrong Procedure";
		Date discontinueDate = new GregorianCalendar(2015, Calendar.JANUARY, 01).getTime();
		
		Order mockDiscontinuationOrder = new Order();
		mockDiscontinuationOrder.setOrderId(2);
		mockDiscontinuationOrder.setAction(Order.Action.DISCONTINUE);
		mockDiscontinuationOrder.setOrderer(mockRadiologyOrderToDiscontinue.getOrderer());
		mockDiscontinuationOrder.setOrderReasonNonCoded(discontinueReason);
		mockDiscontinuationOrder.setPreviousOrder(mockRadiologyOrderToDiscontinue);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("discontinueOrder", "discontinueOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrderToDiscontinue.getOrderId())).thenReturn(
		    mockRadiologyOrderToDiscontinue);
		when(
		    orderService.discontinueOrder(mockRadiologyOrderToDiscontinue, discontinueReason, discontinueDate,
		        mockRadiologyOrderToDiscontinue.getOrderer(), mockRadiologyOrderToDiscontinue.getEncounter())).thenReturn(
		    mockDiscontinuationOrder);
		
		BindingResult orderErrors = mock(BindingResult.class);
		ModelAndView modelAndView = radiologyOrderFormController.postDiscontinueRadiologyOrder(mockRequest, null,
		    mockRadiologyOrderToDiscontinue, mockDiscontinuationOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertThat(order, is((Order) mockRadiologyOrderToDiscontinue));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertThat(radiologyOrder, is(mockRadiologyOrderToDiscontinue));
		
		assertNotNull(mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR), is("radiology.failWorklist"));
	}
}
