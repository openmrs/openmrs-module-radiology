package org.openmrs.module.radiology.web.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.test.Verifies;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyDashboardController}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(RadiologyProperties.class)
@PowerMockIgnore( { "org.apache.commons.logging.*" })
public class RadiologyDashboardControllerTest extends BaseContextMockTest {
	
	private List<Order> mockOrders;
	
	private OrderType mockRadiologyOrderType;
	
	private Patient mockPatient1;
	
	private Patient invalidPatient;
	
	private List<Study> mockStudies;
	
	@Mock
	private PatientService patientService;
	
	@Mock
	private RadiologyService radiologyService;
	
	@Mock
	private OrderService orderService;
	
	@Mock
	private AdministrationService administrationService;
	
	@InjectMocks
	private RadiologyDashboardController radiologyDashboardController = new RadiologyDashboardController();
	
	@Before
	public void runBeforeAllTests() {
		PowerMockito.mockStatic(RadiologyProperties.class);
		
		mockPatient1 = RadiologyTestData.getMockPatient1();
		invalidPatient = new Patient();
		mockRadiologyOrderType = RadiologyTestData.getMockRadiologyOrderType();
		mockOrders = new ArrayList<Order>();
		mockOrders.add(RadiologyTestData.getMockRadiologyOrder1());
		mockOrders.get(0).setOrderType(mockRadiologyOrderType);
		
		mockStudies = new ArrayList<Study>();
		mockStudies.add(RadiologyTestData.getMockStudy1PostSave());
		
		ArrayList<Order> emptyOrdersList = new ArrayList<Order>();
		
		when(Context.getAuthenticatedUser()).thenReturn(RadiologyTestData.getMockRadiologyReferringPhysician());
		when(RadiologyProperties.getRadiologyTestOrderType()).thenReturn(RadiologyTestData.getMockRadiologyOrderType());
		
		when(radiologyService.getStudiesByOrders(mockOrders)).thenReturn(mockStudies);
		when(
		    (orderService.getOrders(Order.class, Arrays.asList(mockPatient1), null, null, null, null, Arrays
		            .asList(mockRadiologyOrderType)))).thenReturn(mockOrders);
		when(
		    (orderService.getOrders(Order.class, Arrays.asList(invalidPatient), null, null, null, null, Arrays
		            .asList(mockRadiologyOrderType)))).thenReturn(emptyOrdersList);
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
