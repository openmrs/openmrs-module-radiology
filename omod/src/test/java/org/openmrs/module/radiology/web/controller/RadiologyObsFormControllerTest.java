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

import java.util.ArrayList;

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
	
	private RadiologyOrder mockOrder;
	
	private Obs mockObs;
	
	private BindingResult obsErrors;
	
	private final Integer validorderId = 20;
	
	private final Integer validObsIdForOrder20 = 1;
	
	private final Integer obsIdNull = null;
	
	private MockHttpServletRequest mockRequest;
	
	@Before
	public void runBeforeAllTests() {
		
		mockOrder = RadiologyTestData.getMockRadiologyOrder1();
		mockRequest = new MockHttpServletRequest();
		mockObs = RadiologyTestData.getMockObs();
		
		obsErrors = mock(BindingResult.class);
		
		when(radiologyService.getStudyByOrderId(validorderId)).thenReturn(mockOrder.getStudy());
		when(radiologyService.getRadiologyOrderByOrderId(validorderId)).thenReturn(mockOrder);
		when(radiologyService.getObsByOrderId(mockOrder.getOrderId())).thenReturn(new ArrayList<Obs>());
		when(obsService.getObs(validObsIdForOrder20)).thenReturn(mockObs);
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReadingPhysician());
		when(radiologyService.getObsByOrderId(mockObs.getOrder().getOrderId())).thenReturn(
		    RadiologyTestData.getPreviousMockObs());
	}
	
	/**
	 * @see RadiologyObsFormController#getObs(Integer, Integer)
	 */
	@Test
	@Verifies(value = "should populate model and view with new obs given valid order", method = "getObs(Integer, Integer)")
	public void getObs_shouldPopulateModelAndViewWithNewObsGivenValidOrder() throws Exception {
		
		ModelAndView modelAndView = radiologyObsFormController.getObs(validorderId, obsIdNull);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		
		assertTrue(modelAndView.getModelMap().containsKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertNotNull(obs.getOrder());
		assertTrue(obs.getOrder().equals(mockOrder));
		assertThat(obs.getPerson().getId(), is(RadiologyTestData.getMockPatient1().getId()));
	}
	
	/**
	 * @see RadiologyObsFormController#getObs(Integer, Integer)
	 */
	@Test
	@Verifies(value = "should populate model and view with obs for given obs and given valid order", method = "getObs(Integer, Integer)")
	public void getObs_ShouldPopulateModelAndViewWithObsForGivenObsAndGivenValidOrder() throws Exception {
		ModelAndView modelAndView = radiologyObsFormController.getObs(validorderId, validObsIdForOrder20);
		
		assertTrue(modelAndView.getModelMap().containsKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertNotNull(obs);
		assertNotNull(obs.getPerson());
		assertNotNull(obs.getOrder());
		assertThat(obs.getOrder().getId(), is(mockOrder.getId()));
		assertThat(obs.getPerson().getId(), is(RadiologyTestData.getMockPatient1().getId()));
	}
	
	/**
	 * @see RadiologyObsFormController#getObs(Integer, Integer)
	 */
	@Test
	@Verifies(value = "should populate model and view with dicom viewer url for completed study and obs for given obs and given valid order", method = "getObs(Integer, Integer)")
	public void getObs_ShouldPopulateModelAndViewWitDicomViewerUrlForCompletedStudyAndObsForGivenObsAndGivenValidOrder()
	        throws Exception {
		
		mockOrder.getStudy().setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		
		when(RadiologyProperties.getServersAddress()).thenReturn("localhost");
		when(RadiologyProperties.getServersPort()).thenReturn("8081");
		when(RadiologyProperties.getDicomViewerUrlBase()).thenReturn("/weasis/viewer?");
		
		ModelAndView modelAndView = radiologyObsFormController.getObs(validorderId, validObsIdForOrder20);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		
		assertTrue(modelAndView.getModelMap().containsKey("dicomViewerUrl"));
		String dicomViewerUrl = (String) modelAndView.getModelMap().get("dicomViewerUrl");
		assertNotNull(dicomViewerUrl);
		
		String patID = mockOrder.getPatient().getPatientIdentifier().getIdentifier();
		assertThat(dicomViewerUrl, is("http://localhost:8081/weasis/viewer?studyUID="
		        + mockOrder.getStudy().getStudyInstanceUid() + "&patientID=" + patID));
	}
	
	/**
	 * @see RadiologyObsFormController#postObs(HttpServletRequest, HttpServletResponse, Integer,
	 *      Integer, Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should populate model with new obs given no parameters and binding errors", method = "postObs(HttpServletRequest, HttpServletResponse, Integer, Integer, Obs, BindingResult)")
	public void postObs_ShouldPopulateModelWithNewObsGivenNoParametersAndBindingErrors() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.setSession(mockSession);
		when(obsErrors.hasErrors()).thenReturn(true);
		
		ModelAndView modelAndView = radiologyObsFormController.postObs(mockRequest, null, validorderId,
		    validObsIdForOrder20, mockObs, obsErrors);
		
		assertNotNull(modelAndView);
		assertTrue(modelAndView.getViewName().equals("module/radiology/radiologyObsForm"));
		
		assertTrue(modelAndView.getModelMap().containsKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertNotNull(obs);
		assertNotNull(obs.getPerson());
		assertNotNull(obs.getOrder());
		assertThat(obs.getOrder().getId(), is(mockOrder.getId()));
		assertThat(obs.getPerson().getId(), is(RadiologyTestData.getMockPatient1().getId()));
	}
	
	/**
	 * @see RadiologyObsFormController#postObs(HttpServletRequest, HttpServletResponse, Integer,
	 *      Integer, Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should unvoid voided Obs with given orderid, obsid, request and obs", method = "postObs(HttpServletRequest, HttpServletResponse, Integer, Integer, Obs, BindingResult)")
	public void postObs_ShouldUnvoidvoidedObsWithGivenOrderIdObsIdRequestAndObs() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("unvoidObs", "unvoidObs");
		mockRequest.setSession(mockSession);
		when(obsErrors.hasErrors()).thenReturn(false);
		
		mockObs.setVoided(true);
		
		ModelAndView modelAndView = radiologyObsFormController.postObs(mockRequest, null, validorderId,
		    validObsIdForOrder20, mockObs, obsErrors);
		assertTrue(modelAndView.getViewName().equals("redirect:/module/radiology/radiologyOrder.list"));
		
		assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Obs.unvoidedSuccessfully"));
	}
	
	/**
	 * @see RadiologyObsFormController#postObs(HttpServletRequest, HttpServletResponse, Integer,
	 *      Integer, Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "Should void obs with voiding reason", method = "postObs(HttpServletRequest, HttpServletResponse, Integer, Integer, Obs, BindingResult)")
	public void postObs_ShouldVoidObsWithVoidingReason() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("voidObs", "voidObs");
		mockRequest.addParameter("voidReason", "Test Void Reason");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		
		assertNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		ModelAndView modelAndView = radiologyObsFormController.postObs(mockRequest, null, validorderId,
		    validObsIdForOrder20, mockObs, obsErrors);
		
		assertNotNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		assertTrue(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR).equals("Obs.voidedSuccessfully"));
		assertTrue(modelAndView.getViewName().equals("redirect:/module/radiology/radiologyOrder.list"));
	}
	
	/**
	 * @see RadiologyObsFormController#postObs(HttpServletRequest, HttpServletResponse, Integer,
	 *      Integer, Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should edit observation with edit reason and complex concept", method = "postObs(HttpServletRequest, HttpServletResponse, Integer, Integer, Obs, BindingResult)")
	public void postObs_ShouldEditObservationWithEditReasonAndComplexConcept() {
		
		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.addParameter("editReason", "Test Edit Reason");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		ConceptComplex concept = new ConceptComplex();
		ConceptDatatype cdt = new ConceptDatatype();
		cdt.setHl7Abbreviation("ED");
		concept.setDatatype(cdt);
		mockObs.setConcept(concept);
		
		assertNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		ModelAndView modelAndView = radiologyObsFormController.postObs(mockRequest, null, validorderId,
		    validObsIdForOrder20, mockObs, obsErrors);
		
		assertNotNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		assertTrue(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR).equals("Obs.saved"));
		assertTrue(modelAndView.getViewName().equals("redirect:/module/radiology/radiologyOrder.list"));
	}
	
	/**
	 * @see RadiologyObsFormController#postObs(HttpServletRequest, HttpServletResponse, Integer,
	 *      Integer, Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should edit observation with edit reason, complex concept and request which is an instance of multihttpserveletrequest", method = "postObs(HttpServletRequest, HttpServletResponse, Integer, Integer, Obs, BindingResult)")
	public void postObs_ShouldEditObservationWithEditReasonComplexConceptANdRequestWhichIsAnInstanceOfMultiHTTPServletRequest() {
		
		MockHttpSession mockSession = new MockHttpSession();
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.addParameter("editReason", "Test Edit Reason");
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
		
		assertNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		ModelAndView modelAndView = radiologyObsFormController.postObs(mockRequest, null, validorderId,
		    validObsIdForOrder20, mockObs, obsErrors);
		
		assertNotNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		assertTrue(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR).equals("Obs.saved"));
		assertTrue(modelAndView.getViewName().equals("redirect:/module/radiology/radiologyOrder.list"));
	}
	
	/**
	 * @see RadiologyObsFormController#postObs(HttpServletRequest, HttpServletResponse, Integer,
	 *      Integer, Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should edit observation with edit reason concept not complex and request which is an instance of multihttpserveletrequest", method = "postObs(HttpServletRequest, HttpServletResponse, Integer, Integer, Obs, BindingResult)")
	public void postObs_ShouldEditObservationWithEditReasonConceptNotComplexAndRequestWhichIsAnInstanceOfMultiHTTPServletRequest() {
		
		MockHttpSession mockSession = new MockHttpSession();
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.addParameter("editReason", "Test Edit Reason");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		ConceptComplex concept = new ConceptComplex();
		mockObs.setConcept(new Concept());
		final String fileName = "test.txt";
		final byte[] content = "Hello World".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile("complexDataFile", fileName, "text/plain", content);
		
		mockRequest.addFile(mockMultipartFile);
		
		assertNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		ModelAndView modelAndView = radiologyObsFormController.postObs(mockRequest, null, validorderId,
		    validObsIdForOrder20, mockObs, obsErrors);
		
		assertNotNull(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
		assertTrue(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR).equals("Obs.saved"));
		assertTrue(modelAndView.getViewName().equals("redirect:/module/radiology/radiologyOrder.list"));
	}
	
	/**
	 * @see RadiologyObsFormController#postObs(HttpServletRequest, HttpServletResponse, Integer,
	 *      Integer, Obs, BindingResult)
	 */
	@Test
	@Verifies(value = "should populate model and view with new observation occuring thrown APIException", method = "postObs(HttpServletRequest, HttpServletResponse, Integer, Integer, Obs, BindingResult)")
	public void postObs_ShouldPopulateModelAndViewWithNewObservationOccuringThrownAPIException() throws Exception {
		
		MockHttpSession mockSession = new MockHttpSession();
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		mockRequest.addParameter("editReason", "Test Edit Reason");
		mockRequest.addParameter("saveObs", "saveObs");
		mockRequest.setSession(mockSession);
		
		when(obsErrors.hasErrors()).thenReturn(false);
		mockObs.setConcept(new Concept());
		final String fileName = "test.txt";
		final byte[] content = "Hello World".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile("complexDataFile", fileName, "text/plain", content);
		
		mockRequest.addFile(mockMultipartFile);
		APIException apiException = new APIException("Test Exception Handling");
		when(obsService.saveObs(mockObs, "Test Edit Reason")).thenThrow(apiException);
		ModelAndView modelAndView = radiologyObsFormController.postObs(mockRequest, null, validorderId,
		    validObsIdForOrder20, mockObs, obsErrors);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));
		assertNotNull(modelAndView);
		
		assertTrue(modelAndView.getModelMap().containsKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertNotNull(obs.getOrder());
		assertThat(obs.getOrder().getId(), is(mockOrder.getId()));
		assertThat(obs.getPatient().getId(), is(RadiologyTestData.getMockPatient1().getId()));
	}
	
}
