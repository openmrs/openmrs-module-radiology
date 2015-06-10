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

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests {@link Main}
 */
public class MainTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceTestDataSet.xml";
	
	private static final int ORDER_ID_WITH_ONE_OBS = 2002;
	
	private static final int ORDER_ID_WITHOUT_OBS = 2001;
	
	private Main radiologyService = null;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void runBeforeAllTests() throws Exception {
		
		if (radiologyService == null) {
			radiologyService = Context.getService(Main.class);
		}
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * @see Main#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should fetch all obs for given orderId", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldFetchAllObsForGivenOrderId() throws Exception {
		List<Obs> obs = radiologyService.getObsByOrderId(ORDER_ID_WITH_ONE_OBS);
		
		assertThat(obs.size(), is(1));
		assertThat(obs.get(0).getOrder().getOrderId(), is(ORDER_ID_WITH_ONE_OBS));
	}
	
	/**
	 * @see Main#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should return empty list given orderId without associated obs", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldReturnEmptyListGivenOrderIdWithoutAssociatedObs() throws Exception {
		List<Obs> obs = radiologyService.getObsByOrderId(ORDER_ID_WITHOUT_OBS);
		
		assertThat(obs.size(), is(0));
	}
	
	/**
	 * @see Main#getObsByOrderId(Integer)
	 */
	@Test
	@Verifies(value = "should throw IllegalArgumentException given null", method = "getObsByOrderId(Integer)")
	public void getObsByOrderId_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("orderId is required");
		radiologyService.getObsByOrderId(null);
	}
}
