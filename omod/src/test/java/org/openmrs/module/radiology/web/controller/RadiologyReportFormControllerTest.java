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
	 * @verifies populate ModelAndView RadiologyReportForm containing a new created RadiologyReport
	 * @see RadiologyReportFormController#getRadiologyReport(org.openmrs.Order, Integer) for an
	 *      RadiologyOrder if radiologyReportId is null
	 */
	@Test
	public void getRadiologyReport_shouldPopulateModelAndViewRadiologyReportFormContainingANewCreatedRadiologyReportForAnRadiologyOrderIfRadiologyReportIdIsNull()
	        throws Exception {
		
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(radiologyService.createAndClaimRadiologyReport(mockRadiologyOrder)).thenReturn(mockRadiologyReport);
		when(radiologyService.hasRadiologyOrderClaimedRadiologyReport(mockRadiologyOrder)).thenReturn(false);
		
		ModelAndView modelAndView = radiologyReportFormController.getRadiologyReport(mockRadiologyOrder, null);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(),
		    is("redirect:/module/radiology/radiologyReport.form?orderId=1&radiologyReportId=1"));
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNotNull(order);
		assertThat(modelAndView.getModelMap(), hasKey("radiologyReport"));
		RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap().get("radiologyReport");
		assertNotNull(radiologyReport);
		assertThat(radiologyReport, is(mockRadiologyReport));
	}
	
	/**
	 * @verifies populate ModelAndView RadiologyReportForm containing a RadiologyReport for a
	 *           RadiologyOrder
	 * @see RadiologyReportFormController#getRadiologyReport(org.openmrs.Order, Integer)
	 */
	@Test
	public void getRadiologyReport_shouldPopulateModelAndViewRadiologyReportFormContainingARadiologyReportForARadiologyOrder()
	        throws Exception {
		
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(radiologyService.getRadiologyReportByRadiologyReportId(mockRadiologyReport.getId())).thenReturn(
		    mockRadiologyReport);
		
		ModelAndView modelAndView = radiologyReportFormController.getRadiologyReport(mockRadiologyOrder, mockRadiologyReport
		        .getId());
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyReportForm"));
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNotNull(order);
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNotNull(radiologyOrder);
		assertThat(mockRadiologyOrder, is(radiologyOrder));
	}
	
	/**
	 * @verifies populate ModelAndView RadiologyOrderListForm if radiologyReport could not has been
	 *           created
	 * @see RadiologyReportFormController#getRadiologyReport(Order, Integer)
	 */
	@Test
	public void getRadiologyReport_shouldPopulateModelAndViewRadiologyOrderListFormIfRadiologyReportCouldNotHasBeenCreated()
	        throws Exception {
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(radiologyService.createAndClaimRadiologyReport(mockRadiologyOrder)).thenReturn(null);
		
		ModelAndView modelAndView = radiologyReportFormController.getRadiologyReport(mockRadiologyOrder, mockRadiologyReport
		        .getId());
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/module/radiology/radiologyOrder.list"));
	}
	
	/**
	 * @verifies populate ModelAndView RadiologyOrderListForm if the given order does not match with
	 *           the radiologyOrder of the creted radiologyReport
	 * @see RadiologyReportFormController#getRadiologyReport(Order, Integer)
	 */
	@Test
	public void getRadiologyReport_shouldPopulateModelAndViewRadiologyOrderListFormIfTheGivenOrderDoesNotMatchWithTheRadiologyOrderOfTheCretedRadiologyReport()
	        throws Exception {
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		RadiologyReport mockRadiologyReport2 = RadiologyTestData.getMockRadiologyReport1();
		mockRadiologyReport2.setRadiologyOrder(RadiologyTestData.getMockRadiologyOrder2());
		
		when(radiologyService.getRadiologyOrderByOrderId(mockRadiologyOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(radiologyService.createAndClaimRadiologyReport(mockRadiologyOrder)).thenReturn(mockRadiologyReport2);
		
		ModelAndView modelAndView = radiologyReportFormController.getRadiologyReport(mockRadiologyOrder, mockRadiologyReport
		        .getId());
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/module/radiology/radiologyOrder.list"));
	}
	
	/**
	 * @verifies populate ModelAndView RadiologyReportForm containing the saved RadiologyReport for
	 *           a RadiologyOrder
	 * @see RadiologyReportFormController#saveRadiologyReport(org.openmrs.Order,
	 *      org.openmrs.module.radiology.report.RadiologyReport)
	 */
	@Test
	public void saveRadiologyReport_shouldPopulateModelAndViewRadiologyReportFormContainingTheSavedRadiologyReportForARadiologyOrder()
	        throws Exception {
		Order mockOrder = RadiologyTestData.getMockOrder1();
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		List<RadiologyReport> radiologyReportList = new ArrayList();
		radiologyReportList.add(mockRadiologyReport);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(
		    radiologyService.getRadiologyReportsByRadiologyOrderAndReportStatus(mockRadiologyOrder,
		        RadiologyReportStatus.CLAIMED)).thenReturn(radiologyReportList);
		
		ModelAndView modelAndView = radiologyReportFormController.saveRadiologyReport(mockRadiologyOrder,
		    mockRadiologyReport);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyReportForm"));
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNotNull(order);
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNotNull(radiologyOrder);
		assertThat(mockRadiologyOrder, is(radiologyOrder));
		assertThat(modelAndView.getModelMap(), hasKey("radiologyReport"));
		RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap().get("radiologyReport");
		assertNotNull(radiologyReport);
	}
	
	/**
	 * @verifies populate ModelAndView RadiologyOrderForm if unclaim was successful
	 * @see RadiologyReportFormController#unclaimRadiologyReport(org.openmrs.Order,
	 *      org.openmrs.module.radiology.report.RadiologyReport)
	 */
	@Test
	public void unclaimRadiologyReport_shouldPopulateModelAndViewRadiologyOrderFormIfUnclaimWasSuccessful() throws Exception {
		Order mockOrder = RadiologyTestData.getMockOrder1();
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		List<RadiologyReport> radiologyReportList = new ArrayList();
		radiologyReportList.add(mockRadiologyReport);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(
		    radiologyService.getRadiologyReportsByRadiologyOrderAndReportStatus(mockRadiologyOrder,
		        RadiologyReportStatus.CLAIMED)).thenReturn(radiologyReportList);
		
		ModelAndView modelAndView = radiologyReportFormController.unclaimRadiologyReport(mockOrder, mockRadiologyReport);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("redirect:/module/radiology/radiologyOrder.form?orderId=1"));
	}
	
	/**
	 * @verifies populate ModelAndView RadiologyReportForm containing the completed RadiologyReport
	 * @see RadiologyReportFormController#completeRadiologyReport(org.openmrs.Order,
	 *      org.openmrs.module.radiology.report.RadiologyReport,
	 *      org.springframework.validation.BindingResult)
	 */
	@Test
	public void completeRadiologyReport_shouldPopulateModelAndViewRadiologyReportFormContainingTheCompletedRadiologyReport()
	        throws Exception {
		Order mockOrder = RadiologyTestData.getMockOrder1();
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		mockRadiologyReport.setPrincipalResultsInterpreter(RadiologyTestData.getMockProvider1());
		List<RadiologyReport> radiologyReportList = new ArrayList();
		radiologyReportList.add(mockRadiologyReport);
		BindingResult reportErrors = mock(BindingResult.class);
		RadiologyReport mockCompletedRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		mockCompletedRadiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(
		    radiologyService.getRadiologyReportsByRadiologyOrderAndReportStatus(mockRadiologyOrder,
		        RadiologyReportStatus.CLAIMED)).thenReturn(radiologyReportList);
		when(
		    radiologyService.completeRadiologyReport(mockRadiologyReport, mockRadiologyReport
		            .getPrincipalResultsInterpreter())).thenReturn(mockCompletedRadiologyReport);
		
		ModelAndView modelAndView = radiologyReportFormController.completeRadiologyReport(mockOrder, mockRadiologyReport,
		    reportErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyReportForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNotNull(order);
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNotNull(radiologyOrder);
	}
	
	/**
	 * @verifies populate ModelAndView RadiologyReportForm with BindingResult errors if provider is
	 *           null
	 * @see RadiologyReportFormController#completeRadiologyReport(org.openmrs.Order,
	 *      org.openmrs.module.radiology.report.RadiologyReport,
	 *      org.springframework.validation.BindingResult)
	 */
	@Test
	public void completeRadiologyReport_shouldPopulateModelAndViewRadiologyReportFormWithBindingResultErrorsIfProviderIsNull()
	        throws Exception {
		Order mockOrder = RadiologyTestData.getMockOrder1();
		RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
		RadiologyReport mockRadiologyReport = RadiologyTestData.getMockRadiologyReport1();
		mockRadiologyReport.setPrincipalResultsInterpreter(null);
		List<RadiologyReport> radiologyReportList = new ArrayList();
		radiologyReportList.add(mockRadiologyReport);
		BindingResult reportErrors = mock(BindingResult.class);
		
		when(radiologyService.getRadiologyOrderByOrderId(mockOrder.getOrderId())).thenReturn(mockRadiologyOrder);
		when(
		    radiologyService.getRadiologyReportsByRadiologyOrderAndReportStatus(mockRadiologyOrder,
		        RadiologyReportStatus.CLAIMED)).thenReturn(radiologyReportList);
		when(reportErrors.hasErrors()).thenReturn(true);
		
		ModelAndView modelAndView = radiologyReportFormController.completeRadiologyReport(mockOrder, mockRadiologyReport,
		    reportErrors);
		
		assertNotNull(modelAndView);
		assertThat(modelAndView.getViewName(), is("module/radiology/radiologyReportForm"));
		
		assertThat(modelAndView.getModelMap(), hasKey("order"));
		Order order = (Order) modelAndView.getModelMap().get("order");
		assertNotNull(order);
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
		RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap().get("radiologyOrder");
		assertNotNull(radiologyOrder);
		
		assertThat(mockRadiologyOrder, is(radiologyOrder));
		assertThat(modelAndView.getModelMap(), hasKey("radiologyReport"));
		RadiologyReport radiologyReport = (RadiologyReport) modelAndView.getModelMap().get("radiologyReport");
		assertNotNull(radiologyReport);
		assertThat(radiologyReport.getReportStatus(), is(RadiologyReportStatus.CLAIMED));
	}
}
