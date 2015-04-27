/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.controller;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Study;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests for the {@link RadiologyOrderFormController} which handles the Add radiologyOrder.form
 * page.
 */
public class RadiologyOrderFormControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceTestDataSet.xml";
	
	private PatientService patientService = null;
	
	private OrderService orderService = null;
	
	/**
	 * Run this before each unit test in this class. It simply assigns the services used in this
	 * class to private variables The "@Before" method in {@link BaseContextSensitiveTest} is run
	 * right before this method and sets up the initial data set and authenticates to the Context
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		
		if (patientService == null) {
			patientService = Context.getPatientService();
		}
		
		if (orderService == null) {
			orderService = Context.getOrderService();
		}
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * @see RadiologyOrderFormController#getRadiologyOrderForm()
	 * @verifies
	 */
	@Test
	public void getRadiologyOrderForm_shouldPopulateModelAndViewWithOrderAndStudy() throws Exception {
		RadiologyOrderFormController controller = (RadiologyOrderFormController) applicationContext
		        .getBean("radiologyOrderFormController");
		ModelAndView mav = controller.getRadiologyOrderForm();
		
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("study"));
		Study study = (Study) mav.getModelMap().get("study");
		assertNull(study.getId());
		
		assertTrue(mav.getModelMap().containsKey("order"));
		Order order = (Order) mav.getModelMap().get("order");
		assertNull(order.getOrderId());
		assertNotNull(order.getOrderType());
	}
	
	/**
	 * @see RadiologyOrderFormController#getRadiologyOrderFormWithPatiendId()
	 * @verifies
	 */
	@Test
	public void getRadiologyOrderFormWithPatiendId_shouldPopulateModelAndViewWithOrderAndStudy() throws Exception {
		RadiologyOrderFormController radiologyOrderFormController = (RadiologyOrderFormController) applicationContext
		        .getBean("radiologyOrderFormController");
		
		Patient requestFormForPatient = patientService.getPatient(70011);
		ModelAndView mav = radiologyOrderFormController.getRadiologyOrderFormWithPatiendId(requestFormForPatient);
		
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("study"));
		Study study = (Study) mav.getModelMap().get("study");
		assertNull(study.getId());
		
		assertTrue(mav.getModelMap().containsKey("order"));
		Order order = (Order) mav.getModelMap().get("order");
		assertNull(order.getOrderId());
		assertNotNull(order.getOrderType());
		
		assertNotNull(order.getPatient());
		assertThat(order.getPatient(), is(requestFormForPatient));
		
		assertTrue(mav.getModelMap().containsKey("patientId"));
		Integer patientId = (Integer) mav.getModelMap().get("patientId");
		assertThat(patientId, is(requestFormForPatient.getPatientId()));
	}
	
	/**
	 * @see RadiologyOrderFormController#getRadiologyOrderFormWithOrderId()
	 * @verifies
	 */
	@Test
	public void getRadiologyOrderFormWithOrderId_shouldPopulateModelAndViewWithOrderAndStudy() throws Exception {
		RadiologyOrderFormController radiologyOrderFormController = (RadiologyOrderFormController) applicationContext
		        .getBean("radiologyOrderFormController");
		
		Order requestFormForOrder = orderService.getOrder(2001);
		ModelAndView mav = radiologyOrderFormController.getRadiologyOrderFormWithOrderId(requestFormForOrder);
		
		assertNotNull(mav);
		
		assertTrue(mav.getModelMap().containsKey("study"));
		Study study = (Study) mav.getModelMap().get("study");
		assertNotNull(study.getId());
		assertNotNull(study.getOrder().getOrderId());
		
		assertTrue(mav.getModelMap().containsKey("order"));
		Order order = (Order) mav.getModelMap().get("order");
		assertNotNull(order.getOrderId());
		assertNotNull(order.getOrderType());
		
		assertThat(order, is(requestFormForOrder));
	}
}
