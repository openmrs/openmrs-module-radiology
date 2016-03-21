package org.openmrs.module.radiology.web.controller;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Order;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public class RadiologyReportFormControllerTest extends BaseContextMockTest {
	
	@Mock
	private RadiologyService radiologyService;
	
	@InjectMocks
	private RadiologyReportFormController radiologyReportFormController = new RadiologyReportFormController();
	
	/**
	 * @verifies populate model and view with new radiology report for given radiology order
	 * @see RadiologyReportFormController#getRadiologyReportFormWithNewRadiologyReport(RadiologyOrder)
	 */
	@Test
	public void getRadiologyReportFormWithNewRadiologyReport_shouldPopulateModelAndViewWithNewRadiologyReportForGivenRadiologyOrder() {
		
		// given
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		RadiologyOrder mockRadiologyOrder = mockRadiologyReport.getRadiologyOrder();
		
		when(radiologyService.createAndClaimRadiologyReport(mockRadiologyOrder)).thenReturn(mockRadiologyReport);
		
		ModelAndView modelAndView = radiologyReportFormController
		        .getRadiologyReportFormWithNewRadiologyReport(mockRadiologyOrder);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/module/radiology/radiologyReport.form?radiologyReportId="
		        + mockRadiologyReport.getId()));
		
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNotNull(order);
		assertThat(order, is((Order) mockRadiologyOrder));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertThat(radiologyOrder, is(mockRadiologyOrder));
		
		RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap().get("radiologyReport");
		assertNotNull(radiologyReport);
		assertThat(radiologyReport, is(mockRadiologyReport));
	}
	
	/**
	 * @verifies populate model and view with existing radiology report matching given radiology
	 *           report id
	 * @see RadiologyReportFormController#getRadiologyReportFormWithExistingRadiologyReport(Integer)
	 */
	@Test
	public void getRadiologyReportFormWithExistingRadiologyReport_shouldPopulateModelAndViewWithExistingRadiologyReportMatchingGivenRadiologyReportId() {
		
		// given
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		
		when(radiologyService.getRadiologyReportByRadiologyReportId(mockRadiologyReport.getId())).thenReturn(
		    mockRadiologyReport);
		
		ModelAndView modelAndView = radiologyReportFormController
		        .getRadiologyReportFormWithExistingRadiologyReport(mockRadiologyReport.getId());
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyReportForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNotNull(order);
		assertThat(order, is((Order) mockRadiologyReport.getRadiologyOrder()));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
		
		RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap().get("radiologyReport");
		assertNotNull(radiologyReport);
		assertThat(radiologyReport, is(mockRadiologyReport));
	}
	
	/**
	 * @verifies save given radiology report and populate model and view with it
	 * @see RadiologyReportFormController#saveRadiologyReport(RadiologyReport)
	 */
	@Test
	public void saveRadiologyReport_shouldSaveGivenRadiologyReportAndPopulateModelAndViewWithIt() {
		
		// given
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		
		ModelAndView modelAndView = radiologyReportFormController.saveRadiologyReport(mockRadiologyReport);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyReportForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNotNull(order);
		assertThat(order, is((Order) mockRadiologyReport.getRadiologyOrder()));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
		
		RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap().get("radiologyReport");
		assertNotNull(radiologyReport);
		assertThat(radiologyReport, is(mockRadiologyReport));
	}
	
	/**
	 * @verifies redirect to radiology order form if unclaim was successful
	 * @see RadiologyReportFormController#unclaimRadiologyReport(RadiologyReport)
	 */
	@Test
	public void unclaimRadiologyReport_shouldRedirectToRadiologyOrderFormIfUnclaimWasSuccessful() {
		
		// given
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		
		ModelAndView modelAndView = radiologyReportFormController.unclaimRadiologyReport(mockRadiologyReport);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/module/radiology/radiologyOrder.form?orderId="
		        + mockRadiologyReport.getRadiologyOrder().getOrderId()));
	}
	
	/**
	 * @verifies complete given radiology report and populate model and view with it
	 * @see RadiologyReportFormController#completeRadiologyReport(RadiologyReport,
	 *      org.springframework.validation.BindingResult)
	 */
	@Test
	public void completeRadiologyReport_shouldCompleteGivenRadiologyReportAndPopulateModelAndViewWithIt() {
		
		// given
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		mockRadiologyReport.setPrincipalResultsInterpreter(RadiologyTestData.getMockProvider1());
		RadiologyReport mockCompletedRadiologyReport = mockRadiologyReport;
		mockCompletedRadiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
		BindingResult reportErrors = mock(BindingResult.class);
		
		when(
		    radiologyService.completeRadiologyReport(mockRadiologyReport, mockRadiologyReport
		            .getPrincipalResultsInterpreter())).thenReturn(mockCompletedRadiologyReport);
		
		ModelAndView modelAndView = radiologyReportFormController.completeRadiologyReport(mockRadiologyReport, reportErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyReportForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNotNull(order);
		assertThat(order, is((Order) mockRadiologyReport.getRadiologyOrder()));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNotNull(radiologyOrder);
		assertThat(radiologyOrder, is(mockRadiologyReport.getRadiologyOrder()));
		
		RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap().get("radiologyReport");
		assertNotNull(radiologyReport);
		assertThat(radiologyReport, is(mockRadiologyReport));
	}
	
	/**
	 * @verifies populate model and view radiology report form with BindingResult errors if provider
	 *           is null
	 * @see RadiologyReportFormController#completeRadiologyReport(RadiologyReport,
	 *      org.springframework.validation.BindingResult)
	 */
	@Test
	public void completeRadiologyReport_shouldPopulateModelAndViewRadiologyReportFormWithBindingResultErrorsIfProviderIsNull() {
		
		// given
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		mockRadiologyReport.setPrincipalResultsInterpreter(null);
		
		BindingResult reportErrors = mock(BindingResult.class);
		
		when(reportErrors.hasErrors()).thenReturn(true);
		
		ModelAndView modelAndView = radiologyReportFormController.completeRadiologyReport(mockRadiologyReport, reportErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyReportForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNotNull(order);
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNotNull(radiologyOrder);
		
		assertThat(mockRadiologyReport.getRadiologyOrder(), is(radiologyOrder));
		assertThat(modelAndView.getModelMap(), hasKey("radiologyReport"));
		RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap().get("radiologyReport");
		assertNotNull(radiologyReport);
		assertThat(radiologyReport.getReportStatus(), is(RadiologyReportStatus.CLAIMED));
	}
}
