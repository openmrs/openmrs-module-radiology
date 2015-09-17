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
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptDatatype;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.test.Verifies;
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
	private OrderService orderService;
	
	@Mock
	private EncounterService encounterService;
	
	@Mock
	private AdministrationService administrationService;
	
	@Mock
	private RadiologyService radiologyService;
	
	@InjectMocks
	private RadiologyObsFormController radiologyObsFormController = new RadiologyObsFormController();
	
	private Study mockStudy;
	
	private RadiologyOrder mockRadiologyOrder;
	
	private Obs mockObs;
	
	private User mockReadingPhysician;
	
	private User mockRadiologyScheduler;
	
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
		mockReadingPhysician = RadiologyTestData.getMockRadiologyReadingPhysician();
		mockRadiologyScheduler = RadiologyTestData.getMockRadiologyScheduler();
		
		obsErrors = mock(BindingResult.class);
	}
	
	/**
	 * @see RadiologyObsFormController#getObs(Order, Obs)
	 */
	@Test
	@Verifies(value = "should populate model and view with obs for given obs and given valid order", method = "getObs(Order, Obs)")
	public void getObs_shouldPopulateModelAndViewWithObsForGivenObsAndGivenValidOrder() throws Exception {
		
		ModelAndView modelAndView = radiologyObsFormController.getObs(mockRadiologyOrder, mockObs);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObs));
	}
	
	/**
	 * @see RadiologyObsFormController#getNewObs(Order)
	 */
	@Test
	@Verifies(value = "should populate model and view with new obs given a valid order", method = "getNewObs(Order)")
	public void getNewObs_shouldPopulateModelAndViewWithNewObsGivenAValidOrder() throws Exception {
		
		ModelAndView modelAndView = radiologyObsFormController.getNewObs(mockRadiologyOrder);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		
		assertNotNull(obs.getOrder());
		assertThat((RadiologyOrder) obs.getOrder(), is(mockRadiologyOrder));
		assertThat(obs.getPerson(), is((Person) mockRadiologyOrder.getPatient()));
	}
	
	/**
	 * @see RadiologyObsFormController#voidObs(HttpServletRequest, HttpServletResponse, Order, Obs, String, String, Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should void obs for given request, response, orderId, obsId, voidObs, voidReason, and obs", method = "voidObs(HttpServletRequest, HttpServletResponse, Order, Obs, String, String, Obs, BindingResult)")
	public void voidObs_ShouldVoidObsForGivenRequestResponseOrderIdObsIdVoidObsVoidReasonAndObs() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("voidObs", "voidObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyObsFormController.voidObs(mockRequest, null, mockRadiologyOrder, mockObs,
		    "Test Void Reason");
		
		assertNotNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.voidedSuccessfully"));
		assertThat((String) modelAndView.getViewName(), is("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId="
		        + mockRadiologyOrder.getId() + "&obsId=" + mockObs.getId()));
	}
	
	/**
	 * @see RadiologyObsFormController#voidObs(HttpServletRequest, HttpServletResponse, Order, Obs, String, String, Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should not void obs with empty voiding reason", method = "voidObs(HttpServletRequest, HttpServletResponse, Order, Obs, String, String, Obs, BindingResult)")
	public void voidObs_ShouldNotVoidObsWithEmptyVoidingReason() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("voidObs", "voidObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyObsFormController.voidObs(mockRequest, null, mockRadiologyOrder, mockObs, "");
		
		assertNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		
	}
	
	/**
	 * @see RadiologyObsFormController#voidObs(HttpServletRequest, HttpServletResponse, RadiologyOrder, Obs, String, String, Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should not void obs with voiding reason null", method = "voidObs(HttpServletRequest, HttpServletResponse, RadiologyOrder, Obs, String, String, Obs, BindingResult)")
	public void voidObs_ShouldNotVoidObsWithVoidingReasonNull() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("voidObs", "voidObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		ModelAndView modelAndView = radiologyObsFormController.voidObs(mockRequest, null, mockRadiologyOrder, mockObs, null);
		
		assertNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		
	}
	
	/**
	 * @see RadiologyObsFormController#unvoidObs(HttpServletRequest, HttpServletResponse, Obs, String)
	 */
	@Test
	@Verifies(value = "should unvoid voided obs for given request, response and obs", method = "unvoidObs(HttpServletRequest, HttpServletResponse, Obs, String)")
	public void unvoidObs_shouldUnvoidVoidedObsForGivenRequestResponseAndObs() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("unvoidObs", "unvoidObs");
		mockRequest.setSession(mockSession);
		when(obsErrors.hasErrors()).thenReturn(false);
		
		mockObs.setVoided(true);
		
		ModelAndView modelAndView = radiologyObsFormController.unvoidObs(mockRequest, null, mockObs);
		assertThat(modelAndView.getViewName(), is("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId="
		        + mockRadiologyOrder.getId() + "&obsId=" + mockObs.getId()));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.unvoidedSuccessfully"));
		
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should edit obs with edit reason and complex concept", method = "saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)")
	public void saveObs_ShouldEditObsWithEditReasonAndComplexConcept() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		ConceptComplex concept = new ConceptComplex();
		ConceptDatatype cdt = new ConceptDatatype();
		cdt.setHl7Abbreviation("ED");
		concept.setDatatype(cdt);
		mockObs.setConcept(concept);
		
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason",
		    mockRadiologyOrder, mockObs, obsErrors);
		
		assertNotNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.saved"));
		assertThat(modelAndView.getViewName(), is("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId="
		        + mockRadiologyOrder.getId() + "&obsId=" + mockObs.getId()));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should return populated model and view if binding errors occur", method = "saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)")
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
	 */
	@Test
	@Verifies(value = "should return populated model and view if edit reason is empty and obs id not null", method = "saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)")
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
	 */
	@Test
	@Verifies(value = "should return populated model and view if edit reason is null and obs id not null", method = "saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)")
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
	 */
	@Test
	@Verifies(value = "should return redirecting model and view for not authenticated user", method = "saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)")
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
	 */
	@Test
	@Verifies(value = "should edit obs with edit reason, complex concept and request which is an instance of multihttpserveletrequest", method = "saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)")
	public void saveObs_ShouldEditObsWithEditReasonComplexConceptANdRequestWhichIsAnInstanceOfMultiHTTPServletRequest() {
		
		MockHttpSession mockSession = new MockHttpSession();
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		ConceptComplex concept = new ConceptComplex();
		ConceptDatatype cdt = new ConceptDatatype();
		cdt.setHl7Abbreviation("ED");
		concept.setDatatype(cdt);
		mockObs.setConcept(concept);
		final String fileName = "test.txt";
		final byte[] content = "Hello World".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile("complexDataFile", fileName, "text/plain", content);
		
		mockRequest.addFile(mockMultipartFile);
		
		radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason", mockRadiologyOrder, mockObs, obsErrors);
		
		assertNotNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.saved"));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should edit obs with edit reason concept not complex and request which is an instance of multihttpserveletrequest", method = "saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)")
	public void saveObs_ShouldEditObsWithEditReasonConceptNotComplexAndRequestWhichIsAnInstanceOfMultiHTTPServletRequest() {
		
		MockHttpSession mockSession = new MockHttpSession();
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		;
		mockObs.setConcept(new Concept());
		final String fileName = "test.txt";
		final byte[] content = "Hello World".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile("complexDataFile", fileName, "text/plain", content);
		
		mockRequest.addFile(mockMultipartFile);
		
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason",
		    mockRadiologyOrder, mockObs, obsErrors);
		
		assertNotNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.saved"));
		assertThat(modelAndView.getViewName(), is("redirect:" + RADIOLOGY_OBS_FORM_URL + "orderId="
		        + mockRadiologyOrder.getId() + "&obsId=" + mockObs.getId()));
	}
	
	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should populate model and view with obs occuring thrown APIException", method = "saveObs(HttpServletRequest, HttpServletResponse, String, RadiologyOrder, Obs Obs, BindingResult)")
	public void saveObs_ShouldPopulateModelAndViewWithObsOccuringThrownAPIException() throws Exception {
		
		MockHttpSession mockSession = new MockHttpSession();
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		mockRequest.addParameter("editReason", "Test Edit Reason");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		mockObs.setConcept(new Concept());
		final String fileName = "test.txt";
		final byte[] content = "Hello World".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile("complexDataFile", fileName, "text/plain", content);
		
		mockRequest.addFile(mockMultipartFile);
		APIException apiException = new APIException("Test Exception Handling");
		when(obsService.saveObs(mockObs, "Test Edit Reason")).thenThrow(apiException);
		ModelAndView modelAndView = radiologyObsFormController.saveObs(mockRequest, null, "Test Edit Reason",
		    mockRadiologyOrder, mockObs, obsErrors);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		assertNotNull(modelAndView);
		
		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObs));
	}
	
	/**
	 * @see RadiologyObsFormController#populateModelAndView(RadiologyOrder, Obs)
	 */
	@Test
	@Verifies(value = "should populate the model and view for given radiology order  with completed study and obs", method = "populateModelAndView(RadiologyOrder, Obs)")
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
		assertNotNull(studyUID);
		assertThat(studyUID, is(mockStudy.getStudyInstanceUid()));
		
		assertThat(modelAndView.getModelMap(), hasKey("previousObs"));
		List<Obs> previousObs = (List<Obs>) modelAndView.getModelMap().get("previousObs");
		assertNotNull(previousObs);
		assertThat(previousObs, is(radiologyService.getObsByOrderId(mockRadiologyOrder.getId())));
		
		assertThat(modelAndView.getModelMap(), hasKey("dicomViewerUrl"));
	}
	
	/**
	 * @see RadiologyObsFormController#populateModelAndView(RadiologyOrder, Obs)
	 */
	@Test
	@Verifies(value = "should populate the model and view for given radiology order without completed study  and obs", method = "populateModelAndView(RadiologyOrder, Obs)")
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
		assertNotNull(previousObs);
		assertThat(previousObs, is(radiologyService.getObsByOrderId(mockRadiologyOrder.getId())));
		
		assertThat(modelAndView.getModelMap(), hasKey("dicomViewerUrl"));
	}
	
	/**
	 * @see RadiologyObsFormController#updateReadingPhysician(Study)
	 */
	@Test
	@Verifies(value = "should update reading physician for given study and user authenticated  as reading physician", method = "updateReadingPhysician(Study)")
	public void updateReadingPhysician_shouldUpdateReadingPhysicianForGivenStudyAndUserAuthenticatedAsReadingPhysician()
	        throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(mockReadingPhysician);
		
		Method updateReadingPhysicianMethod = radiologyObsFormController.getClass().getDeclaredMethod(
		    "updateReadingPhysician", new Class[] { org.openmrs.module.radiology.Study.class });
		updateReadingPhysicianMethod.setAccessible(true);
		
		assertThat(mockStudy.getReadingPhysician(), nullValue());
		
		updateReadingPhysicianMethod.invoke(radiologyObsFormController, new Object[] { mockStudy });
		
		assertThat(mockStudy.getReadingPhysician(), is(mockReadingPhysician));
	}
	
	/**
	 * @see RadiologyObsFormController#updateReadingPhysician(Study)
	 */
	@Test
	@Verifies(value = "should not update reading physician if user is not authenticated as reading physician", method = "updateReadingPhysician(Study)")
	public void updateReadingPhysician_shouldNotUpdateReadingPhysicianIfUserIsNotAuthenticatedAsReadingPhysician()
	        throws Exception {
		
		when(Context.getAuthenticatedUser()).thenReturn(mockRadiologyScheduler);
		
		Method updateReadingPhysicianMethod = radiologyObsFormController.getClass().getDeclaredMethod(
		    "updateReadingPhysician", new Class[] { org.openmrs.module.radiology.Study.class });
		updateReadingPhysicianMethod.setAccessible(true);
		
		assertThat(mockStudy.getReadingPhysician(), nullValue());
		
		updateReadingPhysicianMethod.invoke(radiologyObsFormController, new Object[] { mockStudy });
		
		assertThat(mockStudy.getReadingPhysician(), nullValue());
	}
	
	/**
	 * @see RadiologyObsFormController#updateReadingPhysician(Study)
	 */
	@Test
	@Verifies(value = "should not update reading physician for given study with reading physician", method = "updateReadingPhysician(Study)")
	public void updateReadingPhysician_shouldNotUpdateReadingPhysicianForGivenStudyWithReadingPhysician() throws Exception {
		
		User otherMockReadingPhysician = RadiologyTestData.getMockRadiologyReadingPhysician();
		assertThat(mockReadingPhysician, is(not(otherMockReadingPhysician)));
		
		mockStudy.setReadingPhysician(mockReadingPhysician);
		when(Context.getAuthenticatedUser()).thenReturn(otherMockReadingPhysician);
		
		Method updateReadingPhysicianMethod = radiologyObsFormController.getClass().getDeclaredMethod(
		    "updateReadingPhysician", new Class[] { org.openmrs.module.radiology.Study.class });
		updateReadingPhysicianMethod.setAccessible(true);
		
		assertThat(mockStudy.getReadingPhysician(), is(mockReadingPhysician));
		
		updateReadingPhysicianMethod.invoke(radiologyObsFormController, new Object[] { mockStudy });
		
		assertThat(mockStudy.getReadingPhysician(), is(mockReadingPhysician));
		
	}
	
	/**
	 * @see RadiologyObsFormController#getDicomViewerUrl(Study, Patient)
	 */
	@Test
	@Verifies(value = "should return dicom viewer url given completed study and patient", method = "getDicomViewerUrl(Study, Patient)")
	public void getDicomViewerUrl_ShouldReturnDicomViewerUrlGivenCompletedStudyAndPatient() throws Exception {
		
		mockStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		
		when(RadiologyProperties.getServersAddress()).thenReturn("localhost");
		when(RadiologyProperties.getServersPort()).thenReturn("8081");
		when(RadiologyProperties.getDicomViewerUrlBase()).thenReturn("/weasis/viewer?");
		
		Method getDicomViewerUrlMethod = radiologyObsFormController.getClass().getDeclaredMethod("getDicomViewerUrl",
		    new Class[] { org.openmrs.module.radiology.Study.class, org.openmrs.Patient.class });
		getDicomViewerUrlMethod.setAccessible(true);
		
		String dicomViewerUrl = (String) getDicomViewerUrlMethod.invoke(radiologyObsFormController, new Object[] {
		        mockStudy, mockPatient });
		
		assertNotNull(dicomViewerUrl);
		
		String patID = mockPatient.getPatientIdentifier().getIdentifier();
		assertThat(dicomViewerUrl, is("http://localhost:8081/weasis/viewer?studyUID=" + mockStudy.getStudyInstanceUid()
		        + "&patientID=" + patID));
	}
	
	/**
	 * @see RadiologyObsFormController#getDicomViewerUrl(Study, Patient)
	 */
	@Test
	@Verifies(value = "should return null given non completed study and patient", method = "getDicomViewerUrl(Study, Patient)")
	public void getDicomViewerUrl_ShouldReturnNullGivenNonCompletedStudyAndPatient() throws Exception {
		
		mockStudy.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		
		when(RadiologyProperties.getServersAddress()).thenReturn("localhost");
		when(RadiologyProperties.getServersPort()).thenReturn("8081");
		when(RadiologyProperties.getDicomViewerUrlBase()).thenReturn("/weasis/viewer?");
		
		Method getDicomViewerUrlMethod = radiologyObsFormController.getClass().getDeclaredMethod("getDicomViewerUrl",
		    new Class[] { org.openmrs.module.radiology.Study.class, org.openmrs.Patient.class });
		getDicomViewerUrlMethod.setAccessible(true);
		
		String dicomViewerUrl = (String) getDicomViewerUrlMethod.invoke(radiologyObsFormController, new Object[] {
		        mockStudy, mockPatient });
		
		assertThat(dicomViewerUrl, nullValue());
	}
	
}
