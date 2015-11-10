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
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.CharArrayReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.web.WebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyObsFormController}
 */
public class RadiologyObsFormControllerTest extends BaseContextMockTest {
	
	Log log = LogFactory.getLog(getClass());
	
	private static final String RADIOLOGY_OBS_FORM_URL = "/module/radiology/radiologyObs.form?";
	
	@Mock
	private ObsService obsService;
	
	@Mock
	private RadiologyProperties radiologyProperties;
	
	@Mock
	private RadiologyService radiologyService;
	
	@InjectMocks
	private RadiologyObsFormController radiologyObsFormController = new RadiologyObsFormController();
	
	private Study mockStudy;
	
	private RadiologyOrder mockRadiologyOrder;
	
	private Obs mockObs;
	
	private Obs mockObsWithComplexConcept;
	
	private Patient mockPatient;
	
	private BindingResult obsErrors;
	
	private MockHttpServletRequest mockRequest;
	
	@Before
	public void runBeforeAllTests() {
		
		mockPatient = RadiologyTestData.getMockPatient1();
		mockStudy = RadiologyTestData.getMockStudy1PostSave();
		mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		mockRadiologyOrder.setStudy(mockStudy);
		mockRadiologyOrder.setPatient(mockPatient);
		mockRequest = new MockHttpServletRequest();
		mockObs = RadiologyTestData.getMockObs();
		mockObs.setPatient(mockPatient);
		mockObs.setOrder(mockRadiologyOrder);
		
		mockObsWithComplexConcept = RadiologyTestData.getMockObsWithComplexConcept();
		
		obsErrors = mock(BindingResult.class);
	}
	
	/**
	 * @see RadiologyObsFormController#getObs(Order, Obs)
	 * @verifies populate model and view with obs for given obs and given valid order
	 */
	@Test
	public void getObs_shouldPopulateModelAndViewWithObsForGivenObsAndGivenValidOrder() throws Exception {
		
		ModelAndView modelAndView = radiologyObsFormController.getObs(mockRadiologyOrder, mockObs);
		
		assertThat(modelAndView, is(notNullValue()));
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObs));
	}
	
	/**
	 * @see RadiologyObsFormController#getNewObs(Order)
	 * @verifies populate model and view with new obs given a valid order
	 */
	@Test
	public void getNewObs_shouldPopulateModelAndViewWithNewObsGivenAValidOrder() throws Exception {
		
		ModelAndView modelAndView = radiologyObsFormController.getNewObs(mockRadiologyOrder);
		
		assertThat(modelAndView, is(notNullValue()));
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		
		assertThat(obs.getOrder(), is(notNullValue()));
		assertThat((RadiologyOrder) obs.getOrder(), is(mockRadiologyOrder));
		assertThat(obs.getPerson(), is((Person) mockRadiologyOrder.getPatient()));
	}
	
	/**
	 * @see RadiologyObsFormController#voidObs(HttpServletRequest, HttpServletResponse, Order, Obs, String, String, Obs, BindingResult)
	 * @verifies void obs for given request, response, orderId, obsId, voidObs, voidReason, and obs
	 */
	@Test
	public void voidObs_ShouldVoidObsForGivenRequestResponseOrderIdObsIdVoidObsVoidReasonAndObs() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("voidObs", "voidObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyObsFormController.voidObs(mockRequest, null, mockRadiologyOrder, mockObs,
		    "Test Void Reason");
		
		assertThat(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is(notNullValue()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.voidedSuccessfully"));
		assertThat((String) modelAndView.getViewName(), is("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId="
		        + mockRadiologyOrder.getId() + "&obsId=" + mockObs.getId()));
	}
	
	/**
	 * @see RadiologyObsFormController#voidObs(HttpServletRequest, HttpServletResponse, Order, Obs, String, String, Obs, BindingResult)
	 * @verifies not void obs with empty voiding reason
	 */
	@Test
	public void voidObs_ShouldNotVoidObsWithEmptyVoidingReason() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("voidObs", "voidObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyObsFormController.voidObs(mockRequest, null, mockRadiologyOrder, mockObs, "");
		
		assertThat(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is(nullValue()));
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		
	}
	
	/**
	 * @see RadiologyObsFormController#voidObs(HttpServletRequest, HttpServletResponse, RadiologyOrder, Obs, String, String, Obs, BindingResult)
	 * @verifies not void obs with voiding reason null
	 */
	@Test
	public void voidObs_ShouldNotVoidObsWithVoidingReasonNull() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("voidObs", "voidObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyObsFormController.voidObs(mockRequest, null, mockRadiologyOrder, mockObs, null);
		
		assertThat(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is(nullValue()));
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		
	}
	
	/**
	 * @see RadiologyObsFormController#unvoidObs(HttpServletRequest, HttpServletResponse, Obs, String)
	 * @verifies unvoid voided obs for given request, response and obs
	 */
	@Test
	public void unvoidObs_shouldUnvoidVoidedObsForGivenRequestResponseAndObs() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("unvoidObs", "unvoidObs");
		mockRequest.setSession(mockSession);
		
		mockObs.setVoided(true);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyObsFormController.unvoidObs(mockRequest, null, mockObs);
		assertThat(modelAndView.getViewName(), is("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId="
		        + mockRadiologyOrder.getId() + "&obsId=" + mockObs.getId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.unvoidedSuccessfully"));
		
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 * @verifies edit obs with edit reason and complex concept
	 */
	@Test
	public void saveObs_ShouldEditObsWithEditReasonAndComplexConcept() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason",
		    mockRadiologyOrder, mockObsWithComplexConcept, obsErrors);
		
		assertThat(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is(notNullValue()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.saved"));
		assertThat(modelAndView.getViewName(), is("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId="
		        + mockRadiologyOrder.getId() + "&obsId=" + mockObsWithComplexConcept.getId()));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 * @verifies return populated model and view if binding errors occur
	 */
	@Test
	public void saveObs_shouldReturnPopulatedModelAndViewIfBindingErrorsOccur() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(true);
		
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason",
		    mockRadiologyOrder, mockObs, obsErrors);
		
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObs));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 * @verifies return populated model and view if edit reason is empty and obs id not null
	 */
	@Test
	public void saveObs_shouldReturnPopulatedModelAndViewIfEditReasonIsEmptyAndObsIdNotNull() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, "", mockRadiologyOrder, mockObs,
		    obsErrors);
		
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObs));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 * @verifies return populated model and view if edit reason is null and obs id not null
	 */
	@Test
	public void saveObs_shouldReturnPopulatedModelAndViewIfEditReasonIsNullAndObsIdNotNull() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, null, mockRadiologyOrder, mockObs,
		    obsErrors);
		
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObs));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 * @verifies return redirecting model and view for not authenticated user
	 */
	@Test
	public void saveObs_shouldReturnRedirectingModelAndViewForNotAuthenticatedUser() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		when(Context.getAuthenticatedUser()).thenReturn(null);
		
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason",
		    mockRadiologyOrder, mockObs, obsErrors);
		
		assertThat(modelAndView.getViewName(), is("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId="
		        + mockRadiologyOrder.getId() + "&obsId=" + mockObs.getId()));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 * @verifies edit obs with edit reason, complex concept and request which is an instance of multihttpserveletrequest
	 */
	@Test
	public void saveObs_ShouldEditObsWithEditReasonComplexConceptANdRequestWhichIsAnInstanceOfMultiHTTPServletRequest() {
		
		MockHttpSession mockSession = new MockHttpSession();
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		final String fileName = "test.txt";
		final byte[] content = "Hello World".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile("complexDataFile", fileName, "text/plain", content);
		mockRequest.addFile(mockMultipartFile);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		when(obsService.saveObs(mockObsWithComplexConcept, "Test Edit Reason")).thenReturn(
		    RadiologyTestData.getEditedMockObs());
		
		radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason", mockRadiologyOrder,
		    mockObsWithComplexConcept, obsErrors);
		
		assertThat(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is(notNullValue()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.saved"));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 * @verifies edit obs with edit reason concept not complex and request which is an instance of multihttpserveletrequest
	 */
	@Test
	public void saveObs_ShouldEditObsWithEditReasonConceptNotComplexAndRequestWhichIsAnInstanceOfMultiHTTPServletRequest() {
		
		MockHttpSession mockSession = new MockHttpSession();
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		mockObs.setConcept(new Concept());
		final String fileName = "test.txt";
		final byte[] content = "Hello World".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile("complexDataFile", fileName, "text/plain", content);
		mockRequest.addFile(mockMultipartFile);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		when(obsService.saveObs(mockObs, "Test Edit Reason")).thenReturn(RadiologyTestData.getEditedMockObs());
		
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason",
		    mockRadiologyOrder, mockObs, obsErrors);
		
		assertThat(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is(notNullValue()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.saved"));
		assertThat(modelAndView.getViewName(), is("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId="
		        + mockRadiologyOrder.getId() + "&obsId=" + RadiologyTestData.getEditedMockObs().getId()));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 * @verifies populate model and view with obs occuring thrown APIException
	 */
	@Test
	public void saveObs_ShouldPopulateModelAndViewWithObsOccuringThrownAPIException() throws Exception {
		
		MockHttpSession mockSession = new MockHttpSession();
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		mockRequest.addParameter("editReason", "Test Edit Reason");
		mockRequest.setSession(mockSession);
		
		mockObs.setConcept(new Concept());
		final String fileName = "test.txt";
		final byte[] content = "Hello World".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile("complexDataFile", fileName, "text/plain", content);
		mockRequest.addFile(mockMultipartFile);
		
		APIException apiException = new APIException("Test Exception Handling");
		when(obsErrors.hasErrors()).thenReturn(false);
		when(obsService.saveObs(mockObs, "Test Edit Reason")).thenThrow(apiException);
		
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason",
		    mockRadiologyOrder, mockObs, obsErrors);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		assertThat(modelAndView, is(notNullValue()));
		
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObs));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 * @verifies return populated model and view if binding errors occur for complex concept
	 */
	@Test
	public void saveObs_shouldReturnPopulatedModelAndViewIfBindingErrorsOccurForComplexConcept() {
		
		MockHttpSession mockSession = new MockHttpSession();
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		final String fileName = "test.txt";
		final byte[] content = "Hello World".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile("complexDataFile", fileName, "text/plain", content);
		mockRequest.addFile(mockMultipartFile);
		
		when(obsErrors.hasErrors()).thenReturn(true);
		when(obsService.getComplexObs(Integer.valueOf(mockObsWithComplexConcept.getId()), WebConstants.HTML_VIEW))
		        .thenReturn(RadiologyTestData.getMockComplexObsAsHtmlViewForMockObs3());
		when(obsService.getComplexObs(Integer.valueOf(mockObsWithComplexConcept.getId()), WebConstants.HYPERLINK_VIEW))
		        .thenReturn(RadiologyTestData.getMockComplexObsAsHyperlinkViewForMockObs3());
		
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason",
		    mockRadiologyOrder, mockObsWithComplexConcept, obsErrors);
		
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObsWithComplexConcept));
	}
	
	/**
	 * @see RadiologyObsFormController#populateModelAndView(RadiologyOrder,Obs,String)
	 * @verifies populate model and view with edit reason
	 */
	@Test
	public void populateModelAndView_shouldPopulateModelAndViewWithEditReason() throws Exception {
		
		Method populateModelAndViewMethod = radiologyObsFormController.getClass()
		        .getDeclaredMethod(
		            "populateModelAndView",
		            new Class[] { org.openmrs.module.radiology.RadiologyOrder.class, org.openmrs.Obs.class,
		                    java.lang.String.class });
		populateModelAndViewMethod.setAccessible(true);
		
		ModelAndView modelAndView = (ModelAndView) populateModelAndViewMethod.invoke(radiologyObsFormController,
		    new Object[] { mockRadiologyOrder, mockObs, "Changed obs date" });
		
		assertThat(modelAndView.getModelMap(), hasKey("editReason"));
		String editReason = (String) modelAndView.getModelMap().get("editReason");
		assertThat(editReason, is("Changed obs date"));
	}
	
	/**
	 * @see RadiologyObsFormController#populateModelAndView(RadiologyOrder, Obs)
	 * @verifies populate the model and view for given radiology order  with completed study and obs
	 */
	@Test
	public void populateModelAndView_ShouldPopulateModelAndViewWithObsForGivenRadiologyOrderWithCompletedStudyAndObs()
	        throws Exception {
		
		mockStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		
		Method populateModelAndViewMethod = radiologyObsFormController.getClass().getDeclaredMethod("populateModelAndView",
		    new Class[] { org.openmrs.module.radiology.RadiologyOrder.class, org.openmrs.Obs.class });
		populateModelAndViewMethod.setAccessible(true);
		
		ModelAndView modelAndView = (ModelAndView) populateModelAndViewMethod.invoke(radiologyObsFormController,
		    new Object[] { mockRadiologyOrder, mockObs });
		
		assertThat(mockObs.getPerson(), is((Person) mockRadiologyOrder.getPatient()));
		
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObs));
		
		assertThat(modelAndView.getModelMap(), hasKey("studyUID"));
		String studyUID = (String) modelAndView.getModelMap().get("studyUID");
		assertThat(studyUID, is(notNullValue()));
		assertThat(studyUID, is(mockStudy.getStudyInstanceUid()));
		
		assertThat(modelAndView.getModelMap(), hasKey("previousObs"));
		List<Obs> previousObs = (List<Obs>) modelAndView.getModelMap().get("previousObs");
		assertThat(previousObs, is(notNullValue()));
		assertThat(previousObs, is(radiologyService.getObsByOrderId(mockRadiologyOrder.getId())));
		
		assertThat(modelAndView.getModelMap(), hasKey("dicomViewerUrl"));
		assertThat(modelAndView.getModelMap(), hasKey(not("htmlView")));
		assertThat(modelAndView.getModelMap(), hasKey(not("hyperlinkView")));
	}
	
	/**
	 * @see RadiologyObsFormController#populateModelAndView(RadiologyOrder, Obs)
	 * @verifies populate the model and view for given radiology order without completed study and obs
	 */
	@Test
	public void populateModelAndView_ShouldPopulateModelAndViewWithObsForGivenRadiologyOrderWithoutCompletedStudyAndObs()
	        throws Exception {
		
		mockStudy.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		
		Method populateModelAndViewMethod = radiologyObsFormController.getClass().getDeclaredMethod("populateModelAndView",
		    new Class[] { org.openmrs.module.radiology.RadiologyOrder.class, org.openmrs.Obs.class });
		populateModelAndViewMethod.setAccessible(true);
		
		ModelAndView modelAndView = (ModelAndView) populateModelAndViewMethod.invoke(radiologyObsFormController,
		    new Object[] { mockRadiologyOrder, mockObs });
		
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObs));
		
		assertThat(modelAndView.getModelMap(), hasKey("studyUID"));
		String studyUID = (String) modelAndView.getModelMap().get("studyUID");
		assertThat(studyUID, nullValue());
		
		assertThat(modelAndView.getModelMap(), hasKey("previousObs"));
		List<Obs> previousObs = (List<Obs>) modelAndView.getModelMap().get("previousObs");
		assertThat(previousObs, is(notNullValue()));
		assertThat(previousObs, is(radiologyService.getObsByOrderId(mockRadiologyOrder.getId())));
		
		assertThat(modelAndView.getModelMap(), hasKey("dicomViewerUrl"));
		assertThat(modelAndView.getModelMap(), hasKey(not("htmlView")));
		assertThat(modelAndView.getModelMap(), hasKey(not("hyperlinkView")));
	}
	
	/**
	 * @see RadiologyObsFormController#populateModelAndView(RadiologyOrder,Obs)
	 * @verifies populate the model and view for given obs with complex concept
	 */
	@Test
	public void populateModelAndView_shouldPopulateTheModelAndViewForGivenObsWithComplexConcept() throws Exception {
		
		Field obsServiceField = RadiologyObsFormController.class.getDeclaredField("obsService");
		obsServiceField.setAccessible(true);
		obsServiceField.set(radiologyObsFormController, obsService);
		
		when(obsService.getComplexObs(Integer.valueOf(mockObsWithComplexConcept.getId()), WebConstants.HTML_VIEW))
		        .thenReturn(RadiologyTestData.getMockComplexObsAsHtmlViewForMockObs3());
		when(obsService.getComplexObs(Integer.valueOf(mockObsWithComplexConcept.getId()), WebConstants.HYPERLINK_VIEW))
		        .thenReturn(RadiologyTestData.getMockComplexObsAsHyperlinkViewForMockObs3());
		
		Method populateModelAndViewMethod = radiologyObsFormController.getClass().getDeclaredMethod("populateModelAndView",
		    new Class[] { org.openmrs.module.radiology.RadiologyOrder.class, org.openmrs.Obs.class });
		populateModelAndViewMethod.setAccessible(true);
		
		ModelAndView modelAndView = (ModelAndView) populateModelAndViewMethod.invoke(radiologyObsFormController,
		    new Object[] { mockRadiologyOrder, mockObsWithComplexConcept });
		
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObsWithComplexConcept));
		
		assertThat(modelAndView.getModelMap(), hasKey("htmlView"));
		CharArrayReader htmlComplexData = (CharArrayReader) modelAndView.getModelMap().get("htmlView");
		assertThat(htmlComplexData, is(notNullValue()));
		char[] htmlComplexDataCharArray = new char[47];
		htmlComplexData.read(htmlComplexDataCharArray);
		assertThat(String.copyValueOf(htmlComplexDataCharArray), is("<img src='/openmrs/complexObsServlet?obsId=3'/>"));
		
		assertThat(modelAndView.getModelMap(), hasKey("hyperlinkView"));
		CharArrayReader hyperlinkViewComplexData = (CharArrayReader) modelAndView.getModelMap().get("hyperlinkView");
		assertThat(hyperlinkViewComplexData, is(notNullValue()));
		char[] hyperlinkViewComplexDataCharArray = new char[33];
		hyperlinkViewComplexData.read(hyperlinkViewComplexDataCharArray);
		assertThat(String.copyValueOf(hyperlinkViewComplexDataCharArray), is("openmrs/complexObsServlet?obsId=3"));
	}
	
	/**
	 * @see RadiologyObsFormController#getDicomViewerUrl(Study, Patient)
	 * @verifies return dicom viewer url given completed study and patient
	 */
	@Test
	public void getDicomViewerUrl_ShouldReturnDicomViewerUrlGivenCompletedStudyAndPatient() throws Exception {
		
		mockStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		
		when(radiologyProperties.getDicomViewerUrl()).thenReturn("http://localhost:8081/weasis/viewer?");
		
		Method getDicomViewerUrlMethod = radiologyObsFormController.getClass().getDeclaredMethod("getDicomViewerUrl",
		    new Class[] { org.openmrs.module.radiology.Study.class, org.openmrs.Patient.class });
		getDicomViewerUrlMethod.setAccessible(true);
		
		String dicomViewerUrl = (String) getDicomViewerUrlMethod.invoke(radiologyObsFormController, new Object[] {
		        mockStudy, mockPatient });
		
		assertThat(dicomViewerUrl, is(notNullValue()));
		
		String patID = mockPatient.getPatientIdentifier().getIdentifier();
		assertThat(dicomViewerUrl, is("http://localhost:8081/weasis/viewer?studyUID=" + mockStudy.getStudyInstanceUid()
		        + "&patientID=" + patID));
	}
	
	/**
	 * @see RadiologyObsFormController#getDicomViewerUrl(Study, Patient)
	 * @verifies return null given non completed study and patient
	 */
	@Test
	public void getDicomViewerUrl_ShouldReturnNullGivenNonCompletedStudyAndPatient() throws Exception {
		
		mockStudy.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		
		when(radiologyProperties.getDicomViewerUrl()).thenReturn("http://localhost:8081/weasis/viewer?");
		
		Method getDicomViewerUrlMethod = radiologyObsFormController.getClass().getDeclaredMethod("getDicomViewerUrl",
		    new Class[] { org.openmrs.module.radiology.Study.class, org.openmrs.Patient.class });
		getDicomViewerUrlMethod.setAccessible(true);
		
		String dicomViewerUrl = (String) getDicomViewerUrlMethod.invoke(radiologyObsFormController, new Object[] {
		        mockStudy, mockPatient });
		
		assertThat(dicomViewerUrl, nullValue());
	}
}
