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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.openmrs.obs.ComplexData;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.web.WebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartHttpServletRequest;
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

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

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
	 * @see RadiologyObsFormController#getObs(RadiologyOrder,Obs)
	 * @verifies populate model and view with obs for given obs and given valid radiology order
	 */
	@Test
	public void getObs_shouldPopulateModelAndViewWithObsForGivenObsAndGivenValidRadiologyOrder() throws Exception {

		ModelAndView modelAndView = radiologyObsFormController.getObs(mockRadiologyOrder, mockObs);

		assertThat(modelAndView, is(notNullValue()));
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));

		assertThat(modelAndView.getModelMap(), hasKey("obs"));
		Obs obs = (Obs) modelAndView.getModelMap().get("obs");
		assertThat(obs, is(mockObs));
	}

	/**
	 * @see RadiologyObsFormController#getNewObs(RadiologyOrder)
	 * @verifies populate model and view with new obs given a valid radiology order
	 */
	@Test
	public void getNewObs_shouldPopulateModelAndViewWithNewObsGivenAValidRadiologyOrder() throws Exception {
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
	 * @see RadiologyObsFormController#voidObs(HttpServletRequest,HttpServletResponse,RadiologyOrder,Obs,String)
	 * @verifies void obs for given request, response, radiologyOrder, obs, and voidReason
	 */
	@Test
	public void voidObs_shouldVoidObsForGivenRequestResponseRadiologyOrderObsAndVoidReason() throws Exception {

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
	 * @see RadiologyObsFormController#voidObs(HttpServletRequest,HttpServletResponse,RadiologyOrder,Obs,String)
	 * @verifies not void obs with empty voiding reason
	 */
	@Test
	public void voidObs_shouldNotVoidObsWithEmptyVoidingReason() throws Exception {

		MockHttpSession mockSession = new MockHttpSession();
		mockRequest.addParameter("voidObs", "voidObs");
		mockRequest.setSession(mockSession);

		when(obsErrors.hasErrors()).thenReturn(false);

		ModelAndView modelAndView = radiologyObsFormController.voidObs(mockRequest, null, mockRadiologyOrder, mockObs, "");

		assertThat(mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is(nullValue()));
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyObsForm"));

	}

	/**
	 * @see RadiologyObsFormController#voidObs(HttpServletRequest,HttpServletResponse,RadiologyOrder,Obs,String)
	 * @verifies not void obs with voiding reason null
	 */
	@Test
	public void voidObs_shouldNotVoidObsWithVoidingReasonNull() throws Exception {

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
	 * @see RadiologyObsFormController#populateModelAndView(RadiologyOrder,Obs)
	 * @verifies populate the model and view for given radiology order with completed study and obs
	 */
	@Test
	public void populateModelAndView_shouldPopulateTheModelAndViewForGivenRadiologyOrderWithCompletedStudyAndObs()
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
	 * @see RadiologyObsFormController#populateModelAndView(RadiologyOrder,Obs)
	 * @verifies populate the model and view for given radiology order without completed study and obs
	 */
	@Test
	public void populateModelAndView_shouldPopulateTheModelAndViewForGivenRadiologyOrderWithoutCompletedStudyAndObs()
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
	 * @see RadiologyObsFormController#getDicomViewerUrl(Study,Patient)
	 * @verifies return dicom viewer url given completed study and patient
	 */
	@Test
	public void getDicomViewerUrl_shouldReturnDicomViewerUrlGivenCompletedStudyAndPatient() throws Exception {

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
	 * @see RadiologyObsFormController#getDicomViewerUrl(Study,Patient)
	 * @verifies return null given non completed study and patient
	 */
	@Test
	public void getDicomViewerUrl_shouldReturnNullGivenNonCompletedStudyAndPatient() throws Exception {

		mockStudy.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);

		when(radiologyProperties.getDicomViewerUrl()).thenReturn("http://localhost:8081/weasis/viewer?");

		Method getDicomViewerUrlMethod = radiologyObsFormController.getClass().getDeclaredMethod("getDicomViewerUrl",
				new Class[] { org.openmrs.module.radiology.Study.class, org.openmrs.Patient.class });
		getDicomViewerUrlMethod.setAccessible(true);

		String dicomViewerUrl = (String) getDicomViewerUrlMethod.invoke(radiologyObsFormController, new Object[] {
				mockStudy, mockPatient });

		assertThat(dicomViewerUrl, nullValue());
	}

	/**
	 * @see RadiologyObsFormController#isObsValid(Obs,BindingResult,String)
	 * @verifies return true if obs is valid
	 */
	@Test
	public void isObsValid_shouldReturnTrueIfObsIsValid() throws Exception {
		//given
		String editReason = "changed date and time";

		when(obsErrors.hasErrors()).thenReturn(false);

		Method isObsValidMethod = radiologyObsFormController.getClass().getDeclaredMethod("isObsValid",
				new Class[] { Obs.class, BindingResult.class, String.class });
		isObsValidMethod.setAccessible(true);

		Boolean isObsValid = (Boolean) isObsValidMethod.invoke(radiologyObsFormController, new Object[] { mockObs,
				obsErrors, editReason });

		assertThat(isObsValid, is(true));
	}

	/**
	 * @see RadiologyObsFormController#isObsValid(Obs,BindingResult,String)
	 * @verifies return false if edit reason is empty and obs id not null
	 */
	@Test
	public void isObsValid_shouldReturnFalseIfEditReasonIsEmptyAndObsIdNotNull() throws Exception {
		//given
		String editReason = "";
		mockObs.setId(1);

		when(obsErrors.hasErrors()).thenReturn(false);

		Method isObsValidMethod = radiologyObsFormController.getClass().getDeclaredMethod("isObsValid",
				new Class[] { Obs.class, BindingResult.class, String.class });
		isObsValidMethod.setAccessible(true);

		Boolean isObsValid = (Boolean) isObsValidMethod.invoke(radiologyObsFormController, new Object[] { mockObs,
				obsErrors, editReason });

		assertThat(isObsValid, is(false));
	}

	/**
	 * @see RadiologyObsFormController#isObsValid(Obs,BindingResult,String)
	 * @verifies return false if edit reason is null and obs id not null
	 */
	@Test
	public void isObsValid_shouldReturnFalseIfEditReasonIsNullAndObsIdNotNull() throws Exception {
		//given
		String editReason = null;
		mockObs.setId(1);

		when(obsErrors.hasErrors()).thenReturn(false);

		Method isObsValidMethod = radiologyObsFormController.getClass().getDeclaredMethod("isObsValid",
				new Class[] { Obs.class, BindingResult.class, String.class });
		isObsValidMethod.setAccessible(true);

		Boolean isObsValid = (Boolean) isObsValidMethod.invoke(radiologyObsFormController, new Object[] { mockObs,
				obsErrors, editReason });

		assertThat(isObsValid, is(false));
	}

	/**
	 * @see RadiologyObsFormController#isObsValid(Obs,BindingResult,String)
	 * @verifies return false if validation of the obs validator fails
	 */
	@Test
	public void isObsValid_shouldReturnFalseIfValidationOfTheObsValidatorFails() throws Exception {
		//given
		String editReason = "changed date and time";

		when(obsErrors.hasErrors()).thenReturn(true);

		Method isObsValidMethod = radiologyObsFormController.getClass().getDeclaredMethod("isObsValid",
				new Class[] { Obs.class, BindingResult.class, String.class });
		isObsValidMethod.setAccessible(true);

		Boolean isObsValid = (Boolean) isObsValidMethod.invoke(radiologyObsFormController, new Object[] { mockObs,
				obsErrors, editReason });

		assertThat(isObsValid, is(false));
	}



	/**
	 * @see RadiologyObsFormController#openInputStreamForComplexDataFile(MultipartHttpServletRequest)
	 * @verifies open input stream for complex data file
	 */
	@Test
	public void openInputStreamForComplexDataFile_shouldOpenInputStreamForComplexDataFile() throws Exception {
		//given
		MockMultipartHttpServletRequest mockRequest = RadiologyTestData.getMockMultipartHttpServletRequestForMockObsWithComplexConcept();
		
		Method openInputStreamForComplexDataFileMethod = radiologyObsFormController.getClass().getDeclaredMethod("openInputStreamForComplexDataFile",
				new Class[] {MultipartHttpServletRequest.class});
		openInputStreamForComplexDataFileMethod.setAccessible(true);

		InputStream complexDataInputStream  = (InputStream) openInputStreamForComplexDataFileMethod.invoke(radiologyObsFormController, new Object[] { mockRequest });
		
		assertThat(complexDataInputStream, is(notNullValue()));
	}

	/**
	 * @see RadiologyObsFormController#openInputStreamForComplexDataFile(MultipartHttpServletRequest)
	 * @verifies throw exception if input stream could not be opened
	 */
	@Test
	public void openInputStreamForComplexDataFile_shouldThrowExceptionIfInputStreamCouldNotBeOpened() throws Exception {
		//given
		MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest();
		
		Method openInputStreamForComplexDataFileMethod = radiologyObsFormController.getClass().getDeclaredMethod("openInputStreamForComplexDataFile",
				new Class[] {MultipartHttpServletRequest.class});
		openInputStreamForComplexDataFileMethod.setAccessible(true);

		expectedException.expect(InvocationTargetException.class);
		expectedException.expectCause(IsInstanceOf.<Throwable> instanceOf(IOException.class));
		openInputStreamForComplexDataFileMethod.invoke(radiologyObsFormController, new Object[] { mockRequest });
	}

	/**
	 * @see RadiologyObsFormController#populateComplexObs(MultipartHttpServletRequest,Obs,InputStream)
	 * @verifies populate obs with new complex data
	 */
	@Test
	public void populateComplexObs_shouldPopulateNewObsWithNewComplexData() throws Exception {
		//given
		MockMultipartHttpServletRequest mockRequest = RadiologyTestData.getMockMultipartHttpServletRequestForMockObsWithComplexConcept();
		MockMultipartFile mockMultipartFile = RadiologyTestData.getMockMultipartFileForMockObsWithComplexConcept();
		
		Method openInputStreamForComplexDataFileMethod = radiologyObsFormController.getClass().getDeclaredMethod("openInputStreamForComplexDataFile",
				new Class[] {MultipartHttpServletRequest.class});
		openInputStreamForComplexDataFileMethod.setAccessible(true);

		InputStream complexDataInputStream  = (InputStream) openInputStreamForComplexDataFileMethod.invoke(radiologyObsFormController, new Object[] { mockRequest });
		
		Method populateComplexObsMethod = radiologyObsFormController.getClass().getDeclaredMethod("populateComplexObs",
				new Class[] {MultipartHttpServletRequest.class,Obs.class,InputStream.class});
		populateComplexObsMethod.setAccessible(true);
		
		assertThat(mockObs.getComplexData(), is(nullValue()));
		mockObs = (Obs) populateComplexObsMethod.invoke(radiologyObsFormController, new Object[] { mockRequest, mockObs, complexDataInputStream });
		assertThat(mockObs.getComplexData(), is(notNullValue()));
		assertThat(mockObs.getComplexData().getData(), is(new ComplexData(mockMultipartFile.getOriginalFilename(), complexDataInputStream).getData()));
	}

	/**
	 * @see RadiologyObsFormController#populateComplexObs(MultipartHttpServletRequest,String,InputStream)
	 * @verifies populate existing obs with old complex data
	 */
	@Test
	public void populateComplexObs_shouldPopulateExistingObsWithOldComplexData() throws Exception {
		//given
		MockMultipartHttpServletRequest mockRequest = RadiologyTestData.getMockMultipartHttpServletRequestForMockObsWithComplexConcept();
		
		Method openInputStreamForComplexDataFileMethod = radiologyObsFormController.getClass().getDeclaredMethod("openInputStreamForComplexDataFile",
				new Class[] {MultipartHttpServletRequest.class});
		openInputStreamForComplexDataFileMethod.setAccessible(true);
		InputStream complexDataInputStream  = (InputStream) openInputStreamForComplexDataFileMethod.invoke(radiologyObsFormController, new Object[] { mockRequest });
		
		log.info(obsService.getComplexObs(Integer.valueOf(mockObsWithComplexConcept.getId()), null));

		//when(obsService.getComplexObs(Integer.valueOf(mockObsWithComplexConcept.getId()), null)).thenReturn(RadiologyTestData.getMockComplexDataForMockObsWithComplexConcept());
		
		Method populateComplexObsMethod = radiologyObsFormController.getClass().getDeclaredMethod("populateComplexObs",
				new Class[] {MultipartHttpServletRequest.class,Obs.class,InputStream.class});
		populateComplexObsMethod.setAccessible(true);
		
		assertThat(mockObsWithComplexConcept.getComplexData().getData(), is(RadiologyTestData.getMockComplexDataForMockObsWithComplexConcept().getData()));
		mockObsWithComplexConcept = (Obs) populateComplexObsMethod.invoke(radiologyObsFormController, new Object[] { mockRequest, mockObsWithComplexConcept, complexDataInputStream });
		assertThat(mockObsWithComplexConcept.getComplexData().getData(), is(RadiologyTestData.getMockComplexDataForEditedMockObsWithComplexConcept().getData()));

	}



	/**
	 * @see RadiologyObsFormController#saveComplexObs(MultipartHttpServletRequest,HttpServletResponse,String,RadiologyOrder,Obs,BindingResult)
	 * @verifies return redirecting model and view for not authenticated user
	 */
	@Test
	public void saveComplexObs_shouldReturnRedirectingModelAndViewForNotAuthenticatedUser() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see RadiologyObsFormController#saveComplexObs(MultipartHttpServletRequest,HttpServletResponse,String,RadiologyOrder,Obs,BindingResult)
	 * @verifies return populated model and view for complex obs
	 */
	@Test
	public void saveComplexObs_shouldReturnPopulatedModelAndViewForComplexObs() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest,HttpServletResponse,String,RadiologyOrder,Obs,BindingResult)
	 * @verifies return redirecting model and view for not authenticated user
	 */
	@Test
	public void saveObs_shouldReturnRedirectingModelAndViewForNotAuthenticatedUser() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest,HttpServletResponse,String,RadiologyOrder,Obs,BindingResult)
	 * @verifies return populated model and view for obs
	 */
	@Test
	public void saveObs_shouldReturnPopulatedModelAndViewForObs() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @see RadiologyObsFormController#saveComplexObs(MultipartHttpServletRequest,HttpServletResponse,String,RadiologyOrder,Obs,BindingResult)
	 * @verifies populate model and view with obs occuring thrown Exception
	 */
	@Test
	public void saveComplexObs_shouldPopulateModelAndViewWithObsOccuringThrownException() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}


	/**
	 * @see RadiologyObsFormController#saveObs(HttpServletRequest,HttpServletResponse,String,RadiologyOrder,Obs,BindingResult)
	 * @verifies populate model and view with obs occuring thrown Exception
	 */
	@Test
	public void saveObs_shouldPopulateModelAndViewWithObsOccuringThrownException() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

}
