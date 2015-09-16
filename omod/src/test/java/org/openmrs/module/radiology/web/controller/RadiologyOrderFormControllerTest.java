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

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.test.Verifies;
import org.openmrs.web.WebConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyOrderFormController}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(RadiologyProperties.class)
@PowerMockIgnore( { "org.apache.commons.logging.*" })
public class RadiologyOrderFormControllerTest extends BaseContextMockTest {
	
	@Mock
	private PatientService patientService;
	
	@Mock
	private OrderService orderService;
	
	@Mock
	private RadiologyService radiologyService;
	
	@Mock
	private MessageSourceService messageSourceService;
	
	@Mock
	private AdministrationService administrationService;
	
	@InjectMocks
	private RadiologyOrderFormController radiologyOrderFormController = new RadiologyOrderFormController();
	
	@Before
	public void runBeforeAllTests() {
		PowerMockito.mockStatic(RadiologyProperties.class);
		
		when(RadiologyProperties.getRadiologyTestOrderType()).thenReturn(RadiologyTestData.getMockRadiologyOrderType());
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
	 * @see RadiologyOrderFormController#getRadiologyOrderFormWithNewRadiologyOrder()
	 */
	@Test
	@Verifies(value = "should populate model and view with new radiology order with prefilled orderer when requested by referring physician", method = "getRadiologyOrderFormWithNewRadiologyOrder()")
	public void getRadiologyOrderFormWithNewRadiologyOrder_shouldPopulateModelAndViewWithNewRadiologyOrderWithPrefilledOrdererWhenRequestedByReferringPhysician()
	        throws Exception {
		
		//given
		User mockReferringPhysician = RadiologyTestData.getMockRadiologyReferringPhysician();
		when(userContext.getAuthenticatedUser()).thenReturn(mockReferringPhysician);
		
		ModelAndView modelAndView = radiologyOrderFormController.getRadiologyOrderFormWithNewRadiologyOrder();
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNull(order.getOrderId());
		
		assertNotNull(order.getStudy());
		assertNull(order.getStudy().getStudyId());
		
		assertNotNull(order.getOrderer());
		assertThat(order.getOrderer(), is(mockReferringPhysician));
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
	@Verifies(value = "should populate model and view with new radiology order with prefilled orderer when requested by referring physician", method = "getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(Integer)")
	public void getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient_shouldPopulateModelAndViewWithNewRadiologyOrderWithPrefilledOrdererWhenRequestedByReferringPhysician()
	        throws Exception {
		
		//given
		Patient mockPatient = RadiologyTestData.getMockPatient1();
		User mockReferringPhysician = RadiologyTestData.getMockRadiologyReferringPhysician();
		
		when(userContext.getAuthenticatedUser()).thenReturn(mockReferringPhysician);
		when(patientService.getPatient(mockPatient.getPatientId())).thenReturn(mockPatient);
		
		ModelAndView modelAndView = radiologyOrderFormController
		        .getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(mockPatient);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNull(order.getOrderId());
		
		assertNotNull(order.getStudy());
		assertNull(order.getStudy().getStudyId());
		
		assertNotNull(order.getOrderer());
		assertThat(order.getOrderer(), is(mockReferringPhysician));
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
		        .getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(mockRadiologyOrder.getOrderId());
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertThat(order, is(mockRadiologyOrder));
	}
	
	/**
	 * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to order saved and redirect to radiology order list when save study was successful", method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
	public void postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToOrderSavedAndRedirectToRadiologyOrderListWhenSaveStudyWasSuccessful()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.SAVE_OK);
		
		when(radiologyService.saveRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		when(radiologyService.saveStudy(mockRadiologyOrder.getStudy())).thenReturn(mockStudyPostSave);
		when(radiologyService.getStudy(mockStudyPostSave.getStudyId())).thenReturn(mockStudyPostSave);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, null,
		    mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/module/radiology/radiologyOrder.list"));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.saved"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to order saved and redirect to patient dashboard when save study was successful and given patient id", method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
	public void postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToOrderSavedAndRedirectToPatientDashboardWhenSaveStudyWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.SAVE_OK);
		
		when(radiologyService.saveRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		when(radiologyService.saveStudy(mockRadiologyOrder.getStudy())).thenReturn(mockStudyPostSave);
		when(radiologyService.getStudy(mockStudyPostSave.getStudyId())).thenReturn(mockStudyPostSave);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder
		        .getPatient().getPatientId(), mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockRadiologyOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.saved"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to saved fail worklist and redirect to patient dashboard when save study was not successful and given patient id", method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
	public void postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToSavedFailWorklistAndRedirectToPatientDashboardWhenSaveStudyWasNotSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPreSave = RadiologyTestData.getMockStudy1PreSave();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.SAVE_ERR);
		
		when(radiologyService.saveRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		when(radiologyService.saveStudy(mockRadiologyOrder.getStudy())).thenReturn(mockStudyPostSave);
		when(radiologyService.getStudy(mockStudyPostSave.getStudyId())).thenReturn(mockStudyPostSave);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder
		        .getPatient().getPatientId(), mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockRadiologyOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("radiology.savedFailWorklist"));
		
		mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.UPDATE_ERR);
		when(radiologyService.saveStudy(mockStudyPreSave)).thenReturn(mockStudyPostSave);
		
		modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder.getPatient()
		        .getPatientId(), mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockRadiologyOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("radiology.savedFailWorklist"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to study performed when study performed status is in progress and scheduler is empty and request was issued by radiology scheduler", method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
	public void postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToStudyPerformedWhenStudyPerformedStatusIsInProgressAndSchedulerIsEmptyAndRequestWasIssuedByRadiologyScheduler()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		mockRadiologyOrder.setStudy(RadiologyTestData.getMockStudy1PostSave());
		mockRadiologyOrder.getStudy().setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		User mockRadiologyScheduler = RadiologyTestData.getMockRadiologyScheduler();
		
		when(userContext.getAuthenticatedUser()).thenReturn(mockRadiologyScheduler);
		when(radiologyService.saveRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		when(radiologyService.saveStudy(mockRadiologyOrder.getStudy())).thenReturn(mockStudyPostSave);
		when(radiologyService.getStudy(mockStudyPostSave.getStudyId())).thenReturn(mockStudyPostSave);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder
		        .getPatient().getPatientId(), mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR), is("radiology.studyPerformed"));
	}
	
	/**
	 * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should not redirect if radiology order is not valid according to order validator", method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
	public void postSaveRadiologyOrder_shouldNotRedirectIfRadiologyOrderIsNotValidAccordingToOrderValidator()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPreSave = RadiologyTestData.getMockStudy1PreSave();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		User mockRadiologyScheduler = RadiologyTestData.getMockRadiologyScheduler();
		
		when(userContext.getAuthenticatedUser()).thenReturn(mockRadiologyScheduler);
		when(radiologyService.saveRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
		when(radiologyService.saveStudy(mockStudyPreSave)).thenReturn(mockStudyPostSave);
		when(radiologyService.getStudy(mockStudyPostSave.getStudyId())).thenReturn(mockStudyPostSave);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(true);
		
		ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder
		        .getPatient().getPatientId(), mockRadiologyOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(HttpServletRequest, Integer, Order)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to voided successfully and redirect to patient dashboard when void order was successful and given patient id", method = "post(HttpServletRequest, Integer, Order)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToVoidedSuccessfullyAndRedirectToPatientDashboardWhenVoidOrderWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.VOID_OK);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(radiologyService.getStudyByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockStudyPostSave);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("voidOrder", "voidOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockRadiologyOrder.getPatient()
		        .getPatientId(), mockRadiologyOrder);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockRadiologyOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.voidedSuccessfully"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(HttpServletRequest, Integer, Order)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to unvoided successfully and redirect to patient dashboard when unvoid order was successful and given patient id", method = "post(HttpServletRequest, Integer, Order)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToUnvoidedSuccessfullyAndRedirectToPatientDashboardWhenUnvoidOrderWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.UNVOID_OK);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(radiologyService.getStudyByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockStudyPostSave);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("unvoidOrder", "unvoidOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockRadiologyOrder.getPatient()
		        .getPatientId(), mockRadiologyOrder);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockRadiologyOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.unvoidedSuccessfully"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(HttpServletRequest, Integer, Order)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to discontinued successfully and redirect to patient dashboard when discontinue order was successful and given patient id", method = "post(HttpServletRequest, Integer, Order)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToDiscontinuedSuccessfullyAndRedirectToPatientDashboardWhenDiscontinueOrderWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.DISCONTINUE_OK);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(radiologyService.getStudyByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockStudyPostSave);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("discontinueOrder", "discontinueOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockRadiologyOrder.getPatient()
		        .getPatientId(), mockRadiologyOrder);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockRadiologyOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.discontinuedSuccessfully"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(HttpServletRequest, Integer, Order)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to undiscontinued successfully and redirect to patient dashboard when undiscontinue order was successful and given patient id", method = "post(HttpServletRequest, Integer, Order)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToUndiscontinueSuccessfullyAndRedirectToPatientDashboardWhenUndiscontinueOrderWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.UNDISCONTINUE_OK);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(radiologyService.getStudyByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockStudyPostSave);
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("undiscontinueOrder", "undiscontinueOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockRadiologyOrder.getPatient()
		        .getPatientId(), mockRadiologyOrder);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockRadiologyOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.undiscontinuedSuccessfully"));
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserReferringPhysician()
	 */
	@Test
	@Verifies(value = "should return true if the current user is authenticated as a referring physician", method = "isUserReferringPhysician()")
	public void isUserReferringPhysician_ShouldReturnTrueIfTheCurrentUserIsAuthenticatedAsAReferringPhysician()
	        throws Exception {
		
		User referringPhysician = RadiologyTestData.getMockRadiologyReferringPhysician();
		when(Context.getAuthenticatedUser()).thenReturn(referringPhysician);
		
		Method isUserReferringPhysicianMethod = radiologyOrderFormController.getClass().getDeclaredMethod(
		    "isUserReferringPhysician", new Class[] {});
		isUserReferringPhysicianMethod.setAccessible(true);
		
		Boolean isUserReferringPhysician = (Boolean) isUserReferringPhysicianMethod.invoke(radiologyOrderFormController,
		    new Object[] {});
		
		assertThat(isUserReferringPhysician, is(true));
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserReferringPhysician()
	 */
	@Test
	@Verifies(value = "should return false if the current user is not authenticated as a referring physician", method = "isUserReferringPhysician()")
	public void isUserReferringPhysician_ShouldReturnFalseIfTheCurrentUserIsNotAuthenticatedAsAReferringPhysician()
	        throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReadingPhysician());
		
		Method isUserReferringPhysicianMethod = radiologyOrderFormController.getClass().getDeclaredMethod(
		    "isUserReferringPhysician", new Class[] {});
		isUserReferringPhysicianMethod.setAccessible(true);
		
		Boolean isUserReferringPhysician = (Boolean) isUserReferringPhysicianMethod.invoke(radiologyOrderFormController,
		    new Object[] {});
		
		assertThat(isUserReferringPhysician, is(false));
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserReferringPhysician()
	 */
	@Test(expected = APIAuthenticationException.class)
	@Verifies(value = "should throw api authentication exception if the current user is not authenticated", method = "isUserReferringPhysician()")
	public void isUserReferringPhysician_ShouldThrowApiAuthenticationExceptionIfTheCurrentUserIsNotAuthenticated()
	        throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(null);
		
		Method isUserReferringPhysicianMethod = radiologyOrderFormController.getClass().getDeclaredMethod(
		    "isUserReferringPhysician", new Class[] {});
		isUserReferringPhysicianMethod.setAccessible(true);
		
		isUserReferringPhysicianMethod.invoke(radiologyOrderFormController, new Object[] {});
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserScheduler()
	 */
	@Test
	@Verifies(value = "should return true if the current user is authenticated as a scheduler", method = "isUserScheduler()")
	public void isUserScheduler_ShouldReturnTrueIfTheCurrentUserIsAuthenticatedAsAScheduler() throws Exception {
		
		User Scheduler = RadiologyTestData.getMockRadiologyScheduler();
		when(Context.getAuthenticatedUser()).thenReturn(Scheduler);
		
		Method isUserSchedulerMethod = radiologyOrderFormController.getClass().getDeclaredMethod("isUserScheduler",
		    new Class[] {});
		isUserSchedulerMethod.setAccessible(true);
		
		Boolean isUserScheduler = (Boolean) isUserSchedulerMethod.invoke(radiologyOrderFormController, new Object[] {});
		
		assertThat(isUserScheduler, is(true));
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserScheduler()
	 */
	@Test
	@Verifies(value = "should return false if the current user is not authenticated as a scheduler", method = "isUserScheduler()")
	public void isUserScheduler_ShouldReturnFalseIfTheCurrentUserIsNotAuthenticatedAsAScheduler() throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReferringPhysician());
		
		Method isUserSchedulerMethod = radiologyOrderFormController.getClass().getDeclaredMethod("isUserScheduler",
		    new Class[] {});
		isUserSchedulerMethod.setAccessible(true);
		
		Boolean isUserScheduler = (Boolean) isUserSchedulerMethod.invoke(radiologyOrderFormController, new Object[] {});
		
		assertThat(isUserScheduler, is(false));
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserScheduler()
	 */
	@Test(expected = APIAuthenticationException.class)
	@Verifies(value = "should throw api authentication exception if the current user is not authenticated", method = "isUserScheduler()")
	public void isUserScheduler_ShouldThrowApiAuthenticationExceptionIfTheCurrentUserIsNotAuthenticated() throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(null);
		
		Method isUserSchedulerMethod = radiologyOrderFormController.getClass().getDeclaredMethod("isUserScheduler",
		    new Class[] {});
		isUserSchedulerMethod.setAccessible(true);
		
		isUserSchedulerMethod.invoke(radiologyOrderFormController, new Object[] {});
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserPerformingPhysician()
	 */
	@Test
	@Verifies(value = "should return true if the current user is authenticated as a Performing physician", method = "isUserPerformingPhysician()")
	public void isUserPerformingPhysician_ShouldReturnTrueIfTheCurrentUserIsAuthenticatedAsAPerformingPhysician()
	        throws Exception {
		
		User PerformingPhysician = RadiologyTestData.getMockRadiologyPerformingPhysician();
		when(Context.getAuthenticatedUser()).thenReturn(PerformingPhysician);
		
		Method isUserPerformingPhysicianMethod = radiologyOrderFormController.getClass().getDeclaredMethod(
		    "isUserPerformingPhysician", new Class[] {});
		isUserPerformingPhysicianMethod.setAccessible(true);
		
		Boolean isUserPerformingPhysician = (Boolean) isUserPerformingPhysicianMethod.invoke(radiologyOrderFormController,
		    new Object[] {});
		
		assertThat(isUserPerformingPhysician, is(true));
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserPerformingPhysician()
	 */
	@Test
	@Verifies(value = "should return false if the current user is not authenticated as a Performing physician", method = "isUserPerformingPhysician()")
	public void isUserPerformingPhysician_ShouldReturnFalseIfTheCurrentUserIsNotAuthenticatedAsAPerformingPhysician()
	        throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReadingPhysician());
		
		Method isUserPerformingPhysicianMethod = radiologyOrderFormController.getClass().getDeclaredMethod(
		    "isUserPerformingPhysician", new Class[] {});
		isUserPerformingPhysicianMethod.setAccessible(true);
		
		Boolean isUserPerformingPhysician = (Boolean) isUserPerformingPhysicianMethod.invoke(radiologyOrderFormController,
		    new Object[] {});
		
		assertThat(isUserPerformingPhysician, is(false));
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserPerformingPhysician()
	 */
	@Test(expected = APIAuthenticationException.class)
	@Verifies(value = "should throw api authentication exception if the current user is not authenticated", method = "isUserPerformingPhysician()")
	public void isUserPerformingPhysician_ShouldThrowApiAuthenticationExceptionTheCurrentUserIsNotAuthenticated()
	        throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(null);
		
		Method isUserPerformingPhysicianMethod = radiologyOrderFormController.getClass().getDeclaredMethod(
		    "isUserPerformingPhysician", new Class[] {});
		isUserPerformingPhysicianMethod.setAccessible(true);
		
		isUserPerformingPhysicianMethod.invoke(radiologyOrderFormController, new Object[] {});
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserReadingPhysician()
	 */
	@Test
	@Verifies(value = "should return true if the current user is authenticated as a Reading physician", method = "isUserReadingPhysician()")
	public void isUserReadingPhysician_ShouldReturnTrueIfTheCurrentUserIsAuthenticatedAsAReadingPhysician() throws Exception {
		
		User ReadingPhysician = RadiologyTestData.getMockRadiologyReadingPhysician();
		when(Context.getAuthenticatedUser()).thenReturn(ReadingPhysician);
		
		Method isUserReadingPhysicianMethod = radiologyOrderFormController.getClass().getDeclaredMethod(
		    "isUserReadingPhysician", new Class[] {});
		isUserReadingPhysicianMethod.setAccessible(true);
		
		isUserReadingPhysicianMethod.invoke(radiologyOrderFormController, new Object[] {});
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserReadingPhysician()
	 */
	@Test
	@Verifies(value = "should return false if the current user is not authenticated as a Reading physician", method = "isUserReadingPhysician()")
	public void isUserReadingPhysician_ShouldReturnFalseIfTheCurrentUserIsNotAuthenticatedAsAReadingPhysician()
	        throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReferringPhysician());
		
		Method isUserReadingPhysicianMethod = radiologyOrderFormController.getClass().getDeclaredMethod(
		    "isUserReadingPhysician", new Class[] {});
		isUserReadingPhysicianMethod.setAccessible(true);
		
		Boolean isUserReadingPhysician = (Boolean) isUserReadingPhysicianMethod.invoke(radiologyOrderFormController,
		    new Object[] {});
		
		assertThat(isUserReadingPhysician, is(false));
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserReadingPhysician()
	 */
	@Test(expected = APIAuthenticationException.class)
	@Verifies(value = "should throw api authentication exception if the current user is not authenticated", method = "isUserReadingPhysician()")
	public void isUserReadingPhysician_ShouldThrowApiAuthenticationExceptionIfTheCurrentUserIsNotAuthenticated()
	        throws Exception {
		when(Context.getAuthenticatedUser()).thenReturn(null);
		
		Method isUserReadingPhysicianMethod = radiologyOrderFormController.getClass().getDeclaredMethod(
		    "isUserReadingPhysician", new Class[] {});
		isUserReadingPhysicianMethod.setAccessible(true);
		
		isUserReadingPhysicianMethod.invoke(radiologyOrderFormController, new Object[] {});
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserSuper()
	 */
	@Test
	@Verifies(value = "should return true if the current user is authenticated as a super user", method = "isUserSuper()")
	public void isUserSuper_ShouldReturnTrueIfTheCurrentUserIsAuthenticatedAsASuperUser() throws Exception {
		
		User Super = RadiologyTestData.getMockRadiologySuperUser();
		when(Context.getAuthenticatedUser()).thenReturn(Super);
		
		Method isUserSuperMethod = radiologyOrderFormController.getClass().getDeclaredMethod("isUserSuper", new Class[] {});
		isUserSuperMethod.setAccessible(true);
		
		Boolean isUserSuper = (Boolean) isUserSuperMethod.invoke(radiologyOrderFormController, new Object[] {});
		
		assertThat(isUserSuper, is(true));
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserSuper()
	 */
	@Test
	@Verifies(value = "should return false if the current user is not authenticated as a super user", method = "isUserSuper()")
	public void isUserSuper_ShouldReturnFalseIfTheCurrentUserIsNotAuthenticatedAsASuperUser() throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReferringPhysician());
		
		Method isUserSuperMethod = radiologyOrderFormController.getClass().getDeclaredMethod("isUserSuper", new Class[] {});
		isUserSuperMethod.setAccessible(true);
		
		Boolean isUserSuper = (Boolean) isUserSuperMethod.invoke(radiologyOrderFormController, new Object[] {});
		
		assertThat(isUserSuper, is(false));
	}
	
	/**
	 * @see RadiologyOrderFormController#isUserSuper()
	 */
	@Test(expected = APIAuthenticationException.class)
	@Verifies(value = "should throw api authentication exception if the current user is not authenticated", method = "isUserSuper()")
	public void isUserSuper_ShouldThrowApiAuthenticationExceptionIfTheCurrentUserIsNotAuthenticated() throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(null);
		
		Method isUserSuperMethod = radiologyOrderFormController.getClass().getDeclaredMethod("isUserSuper", new Class[] {});
		isUserSuperMethod.setAccessible(true);
		
		isUserSuperMethod.invoke(radiologyOrderFormController, new Object[] {});
	}
}
