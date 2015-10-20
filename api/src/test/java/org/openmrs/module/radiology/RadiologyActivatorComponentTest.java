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
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.dcm4che2.tool.dcmof.DcmOF;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link RadiologyActivator}
 */
public class RadiologyActivatorComponentTest extends BaseModuleContextSensitiveTest {
	
	private static final String ACTIVATOR_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyActivatorComponentTestDataset.xml";
	
	private RadiologyActivator radiologyActivator;
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void runBeforeAllTests() throws Exception {
		
		radiologyActivator = new RadiologyActivator();
		executeDataSet(ACTIVATOR_TEST_DATASET);
	}
	
	/**
	 * @see RadiologyActivator#startDicomOrderFiller()
	 * @verifies should successfully start the dicom order filler
	 */
	@Test
	public void startDicomOrderFiller_shouldSuccessfullyStartTheDicomOrderFiller() throws Exception {
		
		Field dicomOrderFillerField = RadiologyActivator.class.getDeclaredField("dicomOrderFiller");
		dicomOrderFillerField.setAccessible(true);
		
		DcmOF dicomOrderFillerFieldValue = (DcmOF) dicomOrderFillerField.get(radiologyActivator);
		assertThat(dicomOrderFillerFieldValue, nullValue());
		
		radiologyActivator.startDicomOrderFiller();
		
		dicomOrderFillerFieldValue = (DcmOF) dicomOrderFillerField.get(radiologyActivator);
		assertThat(dicomOrderFillerFieldValue, notNullValue());
	}
	
	/**
	 * @see RadiologyActivator#stopDicomOrderFiller()
	 * @verifies should successfully stop the dicom order filler
	 */
	@Test
	public void stopDicomOrderFiller_shouldSuccessfullyStopTheDicomOrderFiller() throws Exception {
		
		Field dicomOrderFillerField = RadiologyActivator.class.getDeclaredField("dicomOrderFiller");
		dicomOrderFillerField.setAccessible(true);
		String[] dicomOrderFillerArguments = new String[] { "-mwl", radiologyProperties.getMwlDir(), "-mpps",
		        radiologyProperties.getMppsDir(),
		        radiologyProperties.getApplicationEntityTitle() + ":" + radiologyProperties.getMwlMppsPort() };
		DcmOF dicomOrderFiller = DcmOF.main(dicomOrderFillerArguments);
		dicomOrderFillerField.set(radiologyActivator, dicomOrderFiller);
		
		radiologyActivator.stopDicomOrderFiller();
	}
	
	/**
	 * @see RadiologyActivator#stopDicomOrderFiller()
	 * @verifies should throw exception when unable to stop the dicom order filler
	 */
	@Test
	public void stopDicomOrderFiller_shouldThrowExceptionWhenUnableToStopTheDicomOrderFiller() throws Exception {
		
		Field dicomOrderFillerField = RadiologyActivator.class.getDeclaredField("dicomOrderFiller");
		dicomOrderFillerField.setAccessible(true);
		dicomOrderFillerField.set(radiologyActivator, null);
		
		expectedException.expect(NullPointerException.class);
		radiologyActivator.stopDicomOrderFiller();
	}
	
	/**
	 * @see RadiologyActivator#getDicomOrderFillerArguments()
	 * @verifies should return dicom order filler arguments
	 */
	@Test
	public void getDicomOrderFillerArguments_shouldReturnDicomOrderFillerArguments() throws Exception {
		
		String[] dicomOrderFillerArguments = radiologyActivator.getDicomOrderFillerArguments();
		
		assertThat(dicomOrderFillerArguments[0], is("-mwl"));
		assertThat(dicomOrderFillerArguments[1], is("mwl"));
		assertThat(dicomOrderFillerArguments[2], is("-mpps"));
		assertThat(dicomOrderFillerArguments[3], is("mpps"));
		assertThat(dicomOrderFillerArguments[4], is("RADIOLOGY_MODULE:11114"));
	}
	
}
