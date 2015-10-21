package org.openmrs.module.radiology.web.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.test.Verifies;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyDashboardController}
 */
public class RadiologyDashboardControllerTest extends BaseContextMockTest {
	
	private List<RadiologyOrder> mockOrders;
	
	private Patient mockPatient1;
	
	private Patient invalidPatient;
	
	private List<Study> mockStudies;
	
	@Mock
	private RadiologyService radiologyService;
	
	@InjectMocks
	private RadiologyDashboardController radiologyDashboardController = new RadiologyDashboardController();
	
	@Before
	public void runBeforeAllTests() {
		mockPatient1 = RadiologyTestData.getMockPatient1();
		invalidPatient = new Patient();
		mockOrders = new ArrayList<RadiologyOrder>();
		mockOrders.add(RadiologyTestData.getMockRadiologyOrder1());
		
		mockStudies = new ArrayList<Study>();
		mockStudies.add(RadiologyTestData.getMockStudy1PostSave());
		
		ArrayList<RadiologyOrder> emptyOrdersList = new ArrayList<RadiologyOrder>();
		
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReferringPhysician());
		when(radiologyService.getStudiesByRadiologyOrders(mockOrders)).thenReturn(mockStudies);
		when((radiologyService.getRadiologyOrdersByPatient(mockPatient1))).thenReturn(mockOrders);
		when((radiologyService.getRadiologyOrdersByPatient(invalidPatient))).thenReturn(emptyOrdersList);
	}
	
	/**
	 * @see RadiologyDashboardController#ordersTable(Patient)
	 */
	@Test
	@Verifies(value = "should return model and view populated with all orders given patient", method = "ordersTable(Patient)")
	public void ordersTable_ShouldReturnModelAndViewPopulatedWithAllOrdersGivenPatient() throws Exception {
		
		ModelAndView mav = radiologyDashboardController.ordersTable(mockPatient1);
		assertNotNull(mav);
		assertTrue(mav.getViewName().equals("/module/radiology/portlets/RadiologyDashboardTab"));
		
		assertTrue(mav.getModelMap().containsKey("matchedOrdersSize"));
		Integer ordersize = (Integer) mav.getModelMap().get("matchedOrdersSize");
		assertNotNull(ordersize);
		assertTrue(ordersize == 1);
	}
	
	/**
	 * @see RadiologyDashboardController#ordersTable(Patient)
	 */
	@Test
	@Verifies(value = "should return empty model and view populated with no orders given invalid patient", method = "ordersTable(Patient)")
	public void ordersTable_ShouldReturnEmptyModelAndViewPopulatedWithNoOrdersGivenInvalidPatient() throws Exception {
		
		ModelAndView mav = radiologyDashboardController.ordersTable(invalidPatient);
		assertNotNull(mav);
		assertTrue(mav.getViewName().equals("/module/radiology/portlets/RadiologyDashboardTab"));
		
		assertTrue(mav.getModelMap().containsKey("matchedOrdersSize"));
		Integer ordersize = (Integer) mav.getModelMap().get("matchedOrdersSize");
		assertNotNull(ordersize);
		assertTrue(ordersize == 0);
	}
	
}
