package org.openmrs.module.radiology.web.controller;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyDashboardController}
 */
public class RadiologyDashboardControllerTest extends BaseContextMockTest {
	
	private List<RadiologyOrder> mockOrders;
	
	private Patient mockPatient1;
	
	private Patient invalidPatient;
	
	@Mock
	private RadiologyOrderService radiologyOrderService;
	
	@InjectMocks
	private RadiologyDashboardController radiologyDashboardController = new RadiologyDashboardController();
	
	@Before
	public void runBeforeAllTests() {
		
		mockPatient1 = RadiologyTestData.getMockPatient1();
		mockOrders = new ArrayList<RadiologyOrder>();
		mockOrders.add(RadiologyTestData.getMockRadiologyOrder1());
		when((radiologyOrderService.getRadiologyOrdersByPatient(mockPatient1))).thenReturn(mockOrders);
		
		invalidPatient = new Patient();
		ArrayList<RadiologyOrder> emptyOrdersList = new ArrayList<RadiologyOrder>();
		when((radiologyOrderService.getRadiologyOrdersByPatient(invalidPatient))).thenReturn(emptyOrdersList);
	}
	
	/**
	 * @see RadiologyDashboardController#getRadiologyOrdersForPatient(Patient)
	 * @verifies return model and view populated with all radiology orders for given patient
	 */
	@Test
	public void getRadiologyOrdersForPatient_shouldReturnModelAndViewPopulatedWithAllRadiologyOrdersForGivenPatient()
			throws Exception {
		
		ModelAndView modelAndView = radiologyDashboardController.getRadiologyOrdersForPatient(mockPatient1);
		
		assertNotNull(modelAndView);
		assertTrue(modelAndView.getViewName()
				.equals("/module/radiology/portlets/radiologyDashboardTab"));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrders"));
		ArrayList<RadiologyOrder> radiologyOrders = (ArrayList<RadiologyOrder>) modelAndView.getModelMap()
				.get("radiologyOrders");
		assertThat(radiologyOrders, is(mockOrders));
	}
	
	/**
	 * @see RadiologyDashboardController#getRadiologyOrdersForPatient(Patient)
	 * @verifies return model and view populated with an empty list of radiology orders if given
	 *           patient is unknown
	 */
	@Test
	public void getRadiologyOrdersForPatient_shouldReturnModelAndViewPopulatedWithAnEmptyListOfRadiologyOrdersIfGivenPatientIsUnknown()
			throws Exception {
		
		ModelAndView modelAndView = radiologyDashboardController.getRadiologyOrdersForPatient(invalidPatient);
		
		assertNotNull(modelAndView);
		assertTrue(modelAndView.getViewName()
				.equals("/module/radiology/portlets/radiologyDashboardTab"));
		
		assertThat(modelAndView.getModelMap(), hasKey("radiologyOrders"));
		ArrayList<RadiologyOrder> radiologyOrders = (ArrayList<RadiologyOrder>) modelAndView.getModelMap()
				.get("radiologyOrders");
		assertThat(radiologyOrders, is(empty()));
	}
}
