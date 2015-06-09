/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.radiology;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests {@link Main}
 */
public class MainTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceTestDataSet.xml";
	
	private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER = 70011;
	
	private static final int PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER = 70021;
	
	private OrderService orderService = null;
	
	private Main radiologyService = null;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void runBeforeAllTests() throws Exception {
		
		if (orderService == null) {
			orderService = Context.getOrderService();
		}
		
		if (radiologyService == null) {
			radiologyService = Context.getService(Main.class);
		}
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * @see Main#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should fetch all studies for given orders", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldFetchAllStudiesForGivenOrders() throws Exception {
		Patient patient = Context.getPatientService().getPatient(PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER);
		List<Order> orders = null;
		orders = orderService.getOrdersByPatient(patient);
		
		List<Study> studies = radiologyService.getStudiesByOrders(orders);
		
		assertThat(studies.size(), is(orders.size()));
		assertThat(studies.get(0).getOrderID(), is(orders.get(0).getOrderId()));
		assertThat(studies.get(1).getOrderID(), is(orders.get(1).getOrderId()));
	}
	
	/**
	 * @see Main#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should return empty list given orders without associated studies", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldReturnEmptyListGivenOrdersWithoutAssociatedStudies() throws Exception {
		Patient patient = Context.getPatientService().getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
		List<Order> orders = null;
		orders = orderService.getOrdersByPatient(patient);
		
		List<Study> studies = radiologyService.getStudiesByOrders(orders);
		
		assertThat(orders.size(), is(1));
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see Main#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should return empty list given empty order list", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldReturnEmptyListGivenEmptyOrderList() throws Exception {
		List<Order> orders = new ArrayList<Order>();
		
		List<Study> studies = radiologyService.getStudiesByOrders(orders);
		
		assertThat(orders.size(), is(0));
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see Main#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException given null", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orders are required");
		radiologyService.getStudiesByOrders(null);
	}
}
