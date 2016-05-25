/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order.web;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;

/**
 * Tests {@link PatientDashboardRadiologyTabPortletController}
 */
public class PatientDashboardRadiologyTabPortletControllerTest extends BaseContextMockTest {
	
	private List<RadiologyOrder> mockOrders;
	
	private Patient mockPatient1;
	
	private Patient invalidPatient;
	
	private HttpServletRequest request;
	
	private Map<String, Object> model;
	
	@Mock
	private RadiologyOrderService radiologyOrderService;
	
	@InjectMocks
	private PatientDashboardRadiologyTabPortletController patientDashboardRadiologyTabPortletController = new PatientDashboardRadiologyTabPortletController();
	
	@Before
	public void setUp() {
		
		request = mock(HttpServletRequest.class);
		model = new HashMap<String, Object>();
		
		mockPatient1 = RadiologyTestData.getMockPatient1();
		mockOrders = new ArrayList<RadiologyOrder>();
		mockOrders.add(RadiologyTestData.getMockRadiologyOrder1());
		when((radiologyOrderService.getRadiologyOrdersByPatient(mockPatient1))).thenReturn(mockOrders);
		
		invalidPatient = new Patient();
		ArrayList<RadiologyOrder> emptyOrdersList = new ArrayList<RadiologyOrder>();
		when((radiologyOrderService.getRadiologyOrdersByPatient(invalidPatient))).thenReturn(emptyOrdersList);
	}
	
	/**
	 * @see PatientDashboardRadiologyTabPortletController#populateModel(HttpServletRequest, Map)
	 * @verifies model is populated with all radiology orders for given patient
	 */
	@Test
	public void populateModel_shouldPopulateModelWithAllRadiologyOrdersForGivenPatient() {
		
		model.put("patient", mockPatient1);
		patientDashboardRadiologyTabPortletController.populateModel(request, model);
		
		verify(radiologyOrderService).getRadiologyOrdersByPatient(mockPatient1);
		
		List<RadiologyOrder> radiologyOrders = (ArrayList<RadiologyOrder>) model.get("radiologyOrders");
		assertThat(radiologyOrders, is(mockOrders));
	}
	
	/**
	 * @see PatientDashboardRadiologyTabPortletController#populateModel(HttpServletRequest, Map)
	 * @verifies model is populated with an empty list of radiology orders if given patient is unknown
	 */
	@Test
	public void populateModel_shouldPopulateModelWithEmptyRadiologyOrderListWhenInvalidPatientIsGiven() {
		
		model.put("patient", invalidPatient);
		patientDashboardRadiologyTabPortletController.populateModel(request, model);
		
		verify(radiologyOrderService).getRadiologyOrdersByPatient(invalidPatient);
		
		List<RadiologyOrder> radiologyOrders = (ArrayList<RadiologyOrder>) model.get("radiologyOrders");
		assertThat(radiologyOrders, is(empty()));
	}
}
