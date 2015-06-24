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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests {@link RadiologyService}
 */
public class RadiologyServiceTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceTestDataSet.xml";
	
	private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER = 70011;
	
	private static final int PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER = 70021;
	
	private static final int ORDER_ID_WITH_ONE_OBS = 2002;
	
	private static final int ORDER_ID_WITHOUT_OBS = 2001;
	
	private static final String EXISTING_STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.2186.1.1";
	
	private static final String NON_EXISTING_STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.2186.1.9999";
	
	private PatientService patientService = null;
	
	private OrderService orderService = null;
	
	private RadiologyService radiologyService = null;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void runBeforeAllTests() throws Exception {
		
		if (patientService == null) {
			patientService = Context.getPatientService();
		}
		
		if (orderService == null) {
			orderService = Context.getOrderService();
		}
		
		if (radiologyService == null) {
			radiologyService = Context.getService(RadiologyService.class);
		}
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 */
	@Test
	@Verifies(value = "should return study matching study instance uid", method = "getStudyByStudyInstanceUid(String)")
	public void getStudyByStudyInstanceUid_shouldReturnStudyMatchingUid() throws Exception {
		Study study = radiologyService.getStudyByStudyInstanceUid(EXISTING_STUDY_INSTANCE_UID);
		
		assertNotNull(study);
		assertThat(study.getStudyInstanceUid(), is(EXISTING_STUDY_INSTANCE_UID));
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 */
	@Test
	@Verifies(value = "should return null if no match was found", method = "getStudyByStudyInstanceUid(String)")
	public void getStudyByStudyInstanceUid_shouldReturnNullIfNoMatchIsFound() throws Exception {
		Study study = radiologyService.getStudyByStudyInstanceUid(NON_EXISTING_STUDY_INSTANCE_UID);
		
		assertNull(study);
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException if study instance uid is null", method = "getStudyByStudyInstanceUid(String)")
	public void getStudyByStudyInstanceUid_shouldThrowIllegalArgumentExceptionIfUidIsNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("studyInstanceUid is required");
		radiologyService.getStudyByStudyInstanceUid(null);
	}
	
	/**
	 * @see RadiologyService#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should fetch all studies for given orders", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldFetchAllStudiesForGivenOrders() throws Exception {
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_TWO_STUDIES_AND_NO_NON_RADIOLOGY_ORDER);
		List<Order> orders = null;
		orders = orderService.getOrdersByPatient(patient);
		
		List<Study> studies = radiologyService.getStudiesByOrders(orders);
		
		assertThat(studies.size(), is(orders.size()));
		assertThat(studies.get(0).getOrderId(), is(orders.get(0).getOrderId()));
		assertThat(studies.get(1).getOrderId(), is(orders.get(1).getOrderId()));
	}
	
	/**
	 * @see RadiologyService#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should return empty list given orders without associated studies", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldReturnEmptyListGivenOrdersWithoutAssociatedStudies() throws Exception {
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
		List<Order> orders = null;
		orders = orderService.getOrdersByPatient(patient);
		
		List<Study> studies = radiologyService.getStudiesByOrders(orders);
		
		assertThat(orders.size(), is(1));
		assertThat(studies.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getStudiesByOrders(List<Order>)
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
	 * @see RadiologyService#getStudiesByOrders(List<Order>)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException given null", method = "getStudiesByOrders(List<Order>)")
	public void getStudiesByOrders_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orders are required");
		radiologyService.getStudiesByOrders(null);
	}
	
	/**
	 * @see RadiologyService#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should fetch all obs for given orderId", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldFetchAllObsForGivenOrderId() throws Exception {
		List<Obs> obs = radiologyService.getObsByOrderId(ORDER_ID_WITH_ONE_OBS);
		
		assertThat(obs.size(), is(1));
		assertThat(obs.get(0).getOrder().getOrderId(), is(ORDER_ID_WITH_ONE_OBS));
	}
	
	/**
	 * @see RadiologyService#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should return empty list given orderId without associated obs", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldReturnEmptyListGivenOrderIdWithoutAssociatedObs() throws Exception {
		List<Obs> obs = radiologyService.getObsByOrderId(ORDER_ID_WITHOUT_OBS);
		
		assertThat(obs.size(), is(0));
	}
	
	/**
	 * @see RadiologyService#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException given null", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orderId is required");
		radiologyService.getObsByOrderId(null);
	}
}
