/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link RadiologyOrder}
 */
public class RadiologyOrderTest {
	
	/**
	 * @see RadiologyOrder#setStudy(Study)
	 * @verifies set the study attribute to given study
	 */
	@Test
	public void setStudy_shouldSetTheStudyAttributeToGivenStudy() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		Study study = new Study();
		radiologyOrder.setStudy(study);
		
		assertThat(radiologyOrder.getStudy(), is(study));
	}
	
	/**
	 * @see RadiologyOrder#setStudy(Study)
	 * @verifies set the radiology order of given study to this radiology order
	 */
	@Test
	public void setStudy_shouldSetTheRadiologyOrderOfGivenStudyToThisRadiologyOrder() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		Study study = new Study();
		radiologyOrder.setStudy(study);
		
		assertThat(study.getRadiologyOrder(), is(radiologyOrder));
	}
	
	/**
	 * @see RadiologyOrder#setStudy(Study)
	 * @verifies not fail given null
	 */
	@Test
	public void setStudy_shouldNotFailGivenNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setStudy(null);
		
		assertNotNull(radiologyOrder);
	}
	
	/**
	 * @see RadiologyOrder#isCompleted()
	 * @verifies return false if associated study is null
	 */
	@Test
	public void isCompleted_shouldReturnFalseIfAssociatedStudyIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setStudy(null);
		
		assertFalse(radiologyOrder.isCompleted());
	}
	
	/**
	 * @see RadiologyOrder#isCompleted()
	 * @verifies return false if associated study is not completed
	 */
	@Test
	public void isCompleted_shouldReturnFalseIfAssociatedStudyIsNotCompleted() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setStudy(new Study());
		
		assertFalse(radiologyOrder.isCompleted());
	}
	
	/**
	 * @see RadiologyOrder#isCompleted()
	 * @verifies return true if associated study is completed
	 */
	@Test
	public void isCompleted_shouldReturnTrueIfAssociatedStudyIsCompleted() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		Study study = new Study();
		study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		radiologyOrder.setStudy(study);
		
		assertTrue(radiologyOrder.isCompleted());
	}
	
	/**
	 * @see RadiologyOrder#isNotCompleted()
	 * @verifies return true if associated study is null
	 */
	@Test
	public void isNotCompleted_shouldReturnTrueIfAssociatedStudyIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setStudy(null);
		
		assertTrue(radiologyOrder.isNotCompleted());
	}
	
	/**
	 * @see RadiologyOrder#isNotCompleted()
	 * @verifies return true if associated study is not completed
	 */
	@Test
	public void isNotCompleted_shouldReturnTrueIfAssociatedStudyIsNotCompleted() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setStudy(new Study());
		
		assertTrue(radiologyOrder.isNotCompleted());
	}
	
	/**
	 * @see RadiologyOrder#isNotCompleted()
	 * @verifies return false if associated study is completed
	 */
	@Test
	public void isNotCompleted_shouldReturnFalseIfAssociatedStudyIsCompleted() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		Study study = new Study();
		study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		radiologyOrder.setStudy(study);
		
		assertFalse(radiologyOrder.isNotCompleted());
	}
}
