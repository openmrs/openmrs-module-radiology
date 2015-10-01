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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
}
