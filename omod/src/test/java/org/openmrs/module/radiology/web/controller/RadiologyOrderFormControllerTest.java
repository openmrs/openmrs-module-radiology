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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Utils;
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
	private PatientService patientService;
	
	@Mock
	private OrderService orderService;
	
	@Mock
	private RadiologyService radiologyService;
	
	@Mock
	private ConceptService conceptService;
	
	@Mock
	private AdministrationService administrationService;
	
	@InjectMocks
	private RadiologyOrderFormController radiologyOrderFormController = new RadiologyOrderFormController();
	
	/**
	 * @see RadiologyOrderFormController#get(Integer, Integer)
	 */
	@Test
	@Verifies(value = "should populate model and view with new order and study when given empty request parameters", method = "get(Integer, Integer)")
	public void get_shouldPopulateModelAndViewWithNewOrderAndStudyWhenGivenEmptyRequestParameters() throws Exception {
		
		ModelAndView modelAndView = radiologyOrderFormController.get(null, null);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertTrue(modelAndView.getModelMap().containsKey("study"));
		Study study = (Study) modelAndView.getModelMap().get("study");
		assertThat(study.getId(), is(0));
		
		assertTrue(modelAndView.getModelMap().containsKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNull(order.getOrderId());
		
		assertNull(order.getOrderer());
	}
	
	/**
	 * @see RadiologyOrderFormController#get(Integer, Integer)
	 */
	@Test
	@Verifies(value = "should populate model and view with new order and study with prefilled orderer when given empty request parameters by referring physician", method = "get(Integer, Integer)")
	public void get_shouldPopulateModelAndViewWithNewOrderAndStudyWithPrefilledOrdererWhenGivenEmptyRequestParametersByReferringPhysician()
	        throws Exception {
		
		//given
		User mockReferringPhysician = RadiologyTestData.getMockRadiologyReferringPhysician();
		when(userContext.getAuthenticatedUser()).thenReturn(mockReferringPhysician);
		
		ModelAndView modelAndView = radiologyOrderFormController.get(null, null);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertTrue(modelAndView.getModelMap().containsKey("study"));
		Study study = (Study) modelAndView.getModelMap().get("study");
		assertThat(study.getId(), is(0));
		
		assertTrue(modelAndView.getModelMap().containsKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNull(order.getOrderId());
		
		assertNotNull(order.getOrderer());
		assertThat(order.getOrderer(), is(mockReferringPhysician));
	}
	
	/**
	 * @see RadiologyOrderFormController#get(Integer, Integer)
	 */
	@Test
	@Verifies(value = "should populate model and view with new order and study with prefilled patient when given patient id", method = "get(Integer, Integer)")
	public void get_shouldPopulateModelAndViewWithNewOrderAndStudyWithPrefilledPatientWhenGivenPatientId() throws Exception {
		
		//given
		Patient mockPatient = RadiologyTestData.getMockPatient1();
		when(patientService.getPatient(mockPatient.getPatientId())).thenReturn(mockPatient);
		
		ModelAndView modelAndView = radiologyOrderFormController.get(null, mockPatient.getPatientId());
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertTrue(modelAndView.getModelMap().containsKey("study"));
		Study study = (Study) modelAndView.getModelMap().get("study");
		assertThat(study.getId(), is(0));
		
		assertTrue(modelAndView.getModelMap().containsKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNull(order.getOrderId());
		
		assertNotNull(order.getPatient());
		assertThat(order.getPatient(), is(mockPatient));
		
		assertTrue(modelAndView.getModelMap().containsKey("patientId"));
		Integer patientId = (Integer) modelAndView.getModelMap().get("patientId");
		assertThat(patientId, is(mockPatient.getPatientId()));
	}
	
	/**
	 * @see RadiologyOrderFormController#get(Integer, Integer)
	 */
	@Test
	@Verifies(value = "should populate model and view with existing order and study when given order id", method = "get(Integer, Integer)")
	public void get_shouldPopulateModelAndViewWithExistingOrderAndStudyWhenGivenOrderId() throws Exception {
		
		//given
		Order mockOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudy = RadiologyTestData.getMockStudy1PostSave();
		
		when(orderService.getOrder(mockOrder.getOrderId())).thenReturn(mockOrder);
		when(radiologyService.getStudyByOrderId(mockOrder.getOrderId())).thenReturn(mockStudy);
		
		ModelAndView modelAndView = radiologyOrderFormController.get(mockOrder.getOrderId(), null);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		
		assertTrue(modelAndView.getModelMap().containsKey("study"));
		Study study = (Study) modelAndView.getModelMap().get("study");
		assertThat(study, is(mockStudy));
		
		assertTrue(modelAndView.getModelMap().containsKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertThat(order, is(mockOrder));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(Integer, Integer, Study, BindingResult, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to order saved and redirect to radiology order list when save study was successful", method = "post(Integer, Integer, Study, BindingResult, Order, BindingResult)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToOrderSavedAndRedirectToRadiologyOrderListWhenSaveStudyWasSuccessful()
	        throws Exception {
		
		//given
		Order mockOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPreSave = RadiologyTestData.getMockStudy1PreSave();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.SAVE_OK);
		Concept mockConcept = new Concept();
		
		when(orderService.getOrder(mockOrder.getOrderId())).thenReturn(mockOrder);
		when(orderService.saveOrder(mockOrder)).thenReturn(mockOrder);
		when(radiologyService.getStudyByOrderId(mockOrder.getOrderId())).thenReturn(mockStudyPostSave);
		when(radiologyService.saveStudy(mockStudyPreSave)).thenReturn(mockStudyPostSave);
		when(conceptService.getConcept(1)).thenReturn(mockConcept);
		when(Utils.getRadiologyOrderType()).thenReturn(Arrays.asList(RadiologyTestData.getMockRadiologyOrderType()));
		when(Utils.studyPrefix()).thenReturn(RadiologyTestData.getStudyPrefix());
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult studyErrors = mock(BindingResult.class);
		when(studyErrors.hasErrors()).thenReturn(false);
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockStudyPreSave.getId(), null,
		    mockStudyPreSave, studyErrors, mockOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/module/radiology/radiologyOrder.list"));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.saved"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(Integer, Integer, Study, BindingResult, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to order saved and redirect to patient dashboard when save study was successful and given patient id", method = "post(Integer, Integer, Study, BindingResult, Order, BindingResult)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToOrderSavedAndRedirectToPatientDashboardWhenSaveStudyWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		Order mockOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPreSave = RadiologyTestData.getMockStudy1PreSave();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.SAVE_OK);
		Concept mockConcept = new Concept();
		
		when(orderService.getOrder(mockOrder.getOrderId())).thenReturn(mockOrder);
		when(orderService.saveOrder(mockOrder)).thenReturn(mockOrder);
		when(radiologyService.getStudyByOrderId(mockOrder.getOrderId())).thenReturn(mockStudyPostSave);
		when(radiologyService.saveStudy(mockStudyPreSave)).thenReturn(mockStudyPostSave);
		when(conceptService.getConcept(1)).thenReturn(mockConcept);
		when(Utils.getRadiologyOrderType()).thenReturn(Arrays.asList(RadiologyTestData.getMockRadiologyOrderType()));
		when(Utils.studyPrefix()).thenReturn(RadiologyTestData.getStudyPrefix());
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult studyErrors = mock(BindingResult.class);
		when(studyErrors.hasErrors()).thenReturn(false);
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockStudyPreSave.getId(), mockOrder
		        .getPatient().getPatientId(), mockStudyPreSave, studyErrors, mockOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.saved"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(Integer, Integer, Study, BindingResult, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to saved fail worklist and redirect to patient dashboard when save study was not successful and given patient id", method = "post(Integer, Integer, Study, BindingResult, Order, BindingResult)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToSavedFailWorklistAndRedirectToPatientDashboardWhenSaveStudyWasNotSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		Order mockOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPreSave = RadiologyTestData.getMockStudy1PreSave();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.SAVE_ERR);
		Concept mockConcept = new Concept();
		
		when(orderService.getOrder(mockOrder.getOrderId())).thenReturn(mockOrder);
		when(orderService.saveOrder(mockOrder)).thenReturn(mockOrder);
		when(radiologyService.getStudyByOrderId(mockOrder.getOrderId())).thenReturn(mockStudyPostSave);
		when(radiologyService.saveStudy(mockStudyPreSave)).thenReturn(mockStudyPostSave);
		when(conceptService.getConcept(1)).thenReturn(mockConcept);
		when(Utils.getRadiologyOrderType()).thenReturn(Arrays.asList(RadiologyTestData.getMockRadiologyOrderType()));
		when(Utils.studyPrefix()).thenReturn(RadiologyTestData.getStudyPrefix());
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		BindingResult studyErrors = mock(BindingResult.class);
		when(studyErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockStudyPreSave.getId(), mockOrder
		        .getPatient().getPatientId(), mockStudyPreSave, studyErrors, mockOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("radiology.savedFailWorklist"));
		
		mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		mockStudyPostSave.setMwlStatus(MwlStatus.UPDATE_ERR);
		modelAndView = radiologyOrderFormController.post(mockRequest, mockStudyPreSave.getId(), mockOrder.getPatient()
		        .getPatientId(), mockStudyPreSave, studyErrors, mockOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("radiology.savedFailWorklist"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(Integer, Integer, Study, BindingResult, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to study performed when study performed status is in progress and scheduler is empty and request was issued by radiology scheduler", method = "post(Integer, Integer, Study, BindingResult, Order, BindingResult)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToStudyPerformedWhenStudyPerformedStatusIsInProgressAndSchedulerIsEmptyAndRequestWasIssuedByRadiologyScheduler()
	        throws Exception {
		
		//given
		Order mockOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPreSave = RadiologyTestData.getMockStudy1PreSave();
		mockStudyPreSave.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		Concept mockConcept = new Concept();
		User mockRadiologyScheduler = RadiologyTestData.getMockRadiologyScheduler();
		
		when(orderService.getOrder(mockOrder.getOrderId())).thenReturn(mockOrder);
		when(orderService.saveOrder(mockOrder)).thenReturn(mockOrder);
		when(radiologyService.getStudyByOrderId(mockOrder.getOrderId())).thenReturn(mockStudyPostSave);
		when(radiologyService.saveStudy(mockStudyPreSave)).thenReturn(mockStudyPostSave);
		when(conceptService.getConcept(1)).thenReturn(mockConcept);
		when(userContext.getAuthenticatedUser()).thenReturn(mockRadiologyScheduler);
		when(Utils.getRadiologyOrderType()).thenReturn(Arrays.asList(RadiologyTestData.getMockRadiologyOrderType()));
		when(Utils.studyPrefix()).thenReturn(RadiologyTestData.getStudyPrefix());
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("saveOrder", "saveOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult studyErrors = mock(BindingResult.class);
		when(studyErrors.hasErrors()).thenReturn(false);
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockStudyPreSave.getId(), mockOrder
		        .getPatient().getPatientId(), mockStudyPreSave, studyErrors, mockOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyOrderForm"));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR), is("radiology.studyPerformed"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(Integer, Integer, Study, BindingResult, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to voided successfully and redirect to patient dashboard when void order was successful and given patient id", method = "post(Integer, Integer, Study, BindingResult, Order, BindingResult)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToVoidedSuccessfullyAndRedirectToPatientDashboardWhenVoidOrderWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		Order mockOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPreSave = RadiologyTestData.getMockStudy1PreSave();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.VOID_OK);
		Concept mockConcept = new Concept();
		
		when(orderService.getOrder(mockOrder.getOrderId())).thenReturn(mockOrder);
		when(orderService.saveOrder(mockOrder)).thenReturn(mockOrder);
		when(radiologyService.getStudyByOrderId(mockOrder.getOrderId())).thenReturn(mockStudyPostSave);
		when(radiologyService.saveStudy(mockStudyPreSave)).thenReturn(mockStudyPostSave);
		when(conceptService.getConcept(1)).thenReturn(mockConcept);
		when(Utils.getRadiologyOrderType()).thenReturn(Arrays.asList(RadiologyTestData.getMockRadiologyOrderType()));
		when(Utils.studyPrefix()).thenReturn(RadiologyTestData.getStudyPrefix());
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("voidOrder", "voidOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult studyErrors = mock(BindingResult.class);
		when(studyErrors.hasErrors()).thenReturn(false);
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockStudyPreSave.getId(), mockOrder
		        .getPatient().getPatientId(), mockStudyPreSave, studyErrors, mockOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.voidedSuccessfully"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(Integer, Integer, Study, BindingResult, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to unvoided successfully and redirect to patient dashboard when unvoid order was successful and given patient id", method = "post(Integer, Integer, Study, BindingResult, Order, BindingResult)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToUnvoidedSuccessfullyAndRedirectToPatientDashboardWhenUnvoidOrderWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		Order mockOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPreSave = RadiologyTestData.getMockStudy1PreSave();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.UNVOID_OK);
		Concept mockConcept = new Concept();
		
		when(orderService.getOrder(mockOrder.getOrderId())).thenReturn(mockOrder);
		when(orderService.saveOrder(mockOrder)).thenReturn(mockOrder);
		when(radiologyService.getStudyByOrderId(mockOrder.getOrderId())).thenReturn(mockStudyPostSave);
		when(radiologyService.saveStudy(mockStudyPreSave)).thenReturn(mockStudyPostSave);
		when(conceptService.getConcept(1)).thenReturn(mockConcept);
		when(Utils.getRadiologyOrderType()).thenReturn(Arrays.asList(RadiologyTestData.getMockRadiologyOrderType()));
		when(Utils.studyPrefix()).thenReturn(RadiologyTestData.getStudyPrefix());
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("unvoidOrder", "unvoidOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult studyErrors = mock(BindingResult.class);
		when(studyErrors.hasErrors()).thenReturn(false);
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockStudyPreSave.getId(), mockOrder
		        .getPatient().getPatientId(), mockStudyPreSave, studyErrors, mockOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.unvoidedSuccessfully"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(Integer, Integer, Study, BindingResult, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to discontinued successfully and redirect to patient dashboard when discontinue order was successful and given patient id", method = "post(Integer, Integer, Study, BindingResult, Order, BindingResult)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToDiscontinuedSuccessfullyAndRedirectToPatientDashboardWhenDiscontinueOrderWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		Order mockOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPreSave = RadiologyTestData.getMockStudy1PreSave();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.DISCONTINUE_OK);
		Concept mockConcept = new Concept();
		
		when(orderService.getOrder(mockOrder.getOrderId())).thenReturn(mockOrder);
		when(orderService.saveOrder(mockOrder)).thenReturn(mockOrder);
		when(radiologyService.getStudyByOrderId(mockOrder.getOrderId())).thenReturn(mockStudyPostSave);
		when(radiologyService.saveStudy(mockStudyPreSave)).thenReturn(mockStudyPostSave);
		when(conceptService.getConcept(1)).thenReturn(mockConcept);
		when(Utils.getRadiologyOrderType()).thenReturn(Arrays.asList(RadiologyTestData.getMockRadiologyOrderType()));
		when(Utils.studyPrefix()).thenReturn(RadiologyTestData.getStudyPrefix());
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("discontinueOrder", "discontinueOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult studyErrors = mock(BindingResult.class);
		when(studyErrors.hasErrors()).thenReturn(false);
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockStudyPreSave.getId(), mockOrder
		        .getPatient().getPatientId(), mockStudyPreSave, studyErrors, mockOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.discontinuedSuccessfully"));
	}
	
	/**
	 * @see RadiologyOrderFormController#post(Integer, Integer, Study, BindingResult, Order,
	 *      BindingResult)
	 */
	@Test
	@Verifies(value = "should set http session attribute openmrs message to undiscontinued successfully and redirect to patient dashboard when undiscontinue order was successful and given patient id", method = "post(Integer, Integer, Study, BindingResult, Order, BindingResult)")
	public void post_shouldSetHttpSessionAttributeOpenmrsMessageToUndiscontinueSuccessfullyAndRedirectToPatientDashboardWhenUndiscontinueOrderWasSuccessfulAndGivenPatientId()
	        throws Exception {
		
		//given
		Order mockOrder = RadiologyTestData.getMockRadiologyOrder1();
		Study mockStudyPreSave = RadiologyTestData.getMockStudy1PreSave();
		Study mockStudyPostSave = RadiologyTestData.getMockStudy1PostSave();
		mockStudyPostSave.setMwlStatus(MwlStatus.UNDISCONTINUE_OK);
		Concept mockConcept = new Concept();
		
		when(orderService.getOrder(mockOrder.getOrderId())).thenReturn(mockOrder);
		when(orderService.saveOrder(mockOrder)).thenReturn(mockOrder);
		when(radiologyService.getStudyByOrderId(mockOrder.getOrderId())).thenReturn(mockStudyPostSave);
		when(radiologyService.saveStudy(mockStudyPreSave)).thenReturn(mockStudyPostSave);
		when(conceptService.getConcept(1)).thenReturn(mockConcept);
		when(Utils.getRadiologyOrderType()).thenReturn(Arrays.asList(RadiologyTestData.getMockRadiologyOrderType()));
		when(Utils.studyPrefix()).thenReturn(RadiologyTestData.getStudyPrefix());
		
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addParameter("undiscontinueOrder", "undiscontinueOrder");
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		
		BindingResult studyErrors = mock(BindingResult.class);
		when(studyErrors.hasErrors()).thenReturn(false);
		BindingResult orderErrors = mock(BindingResult.class);
		when(orderErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyOrderFormController.post(mockRequest, mockStudyPreSave.getId(), mockOrder
		        .getPatient().getPatientId(), mockStudyPreSave, studyErrors, mockOrder, orderErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/patientDashboard.form?patientId="
		        + mockOrder.getPatient().getPatientId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.undiscontinuedSuccessfully"));
	}
}
