/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests {@link RadiologyModalityEditor}.
 */
public class RadiologyModalityEditorComponentTest extends BaseModuleContextSensitiveTest {
    
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/RadiologyModalityServiceComponentTestDataset.xml";
    
    private static final String EXISTING_RADIOLOGY_MODALITY_UUID = "015f85fc-1316-45a3-848d-69ba192e64c4";
    
    private static final String NON_EXISTING_RADIOLOGY_MODALITY_UUID = "637d5011-49f5-4ce8-b4ce-47b37ff2cda2";
    
    @Autowired
    private RadiologyModalityService radiologyModalityService;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
    * @see RadiologyModalityEditor#setAsText(String)
    * @verifies set value to radiology modality whos id matches given text
    */
    @Test
    public void setAsText_shouldSetValueToRadiologyModalityWhosIdMatchesGivenText() throws Exception {
        RadiologyModalityEditor editor = new RadiologyModalityEditor();
        editor.setAsText("1");
        assertThat(editor.getValue(), is(notNullValue()));
        assertThat((RadiologyModality) editor.getValue(), is(radiologyModalityService.getRadiologyModality(1)));
    }
    
    /**
    * @see RadiologyModalityEditor#setAsText(String)
    * @verifies set value to radiology modality whos uuid matches given text
    */
    @Test
    public void setAsText_shouldSetValueToRadiologyModalityWhosUuidMatchesGivenText() throws Exception {
        RadiologyModalityEditor editor = new RadiologyModalityEditor();
        editor.setAsText(EXISTING_RADIOLOGY_MODALITY_UUID);
        assertThat(editor.getValue(), is(notNullValue()));
        assertThat((RadiologyModality) editor.getValue(),
            is(radiologyModalityService.getRadiologyModalityByUuid(EXISTING_RADIOLOGY_MODALITY_UUID)));
    }
    
    /**
    * @see RadiologyModalityEditor#setAsText(String)
    * @verifies throw illegal argument exception for radiology modality not found
    */
    @Test
    public void setAsText_shouldThrowIllegalArgumentExceptionForRadiologyModalityNotFound() throws Exception {
        
        RadiologyModalityEditor editor = new RadiologyModalityEditor();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("RadiologyModality not found: ");
        editor.setAsText(NON_EXISTING_RADIOLOGY_MODALITY_UUID);
    }
    
    /**
    * @see RadiologyModalityEditor#setAsText(String)
    * @verifies return null for empty text
    */
    @Test
    public void setAsText_shouldReturnNullForEmptyText() throws Exception {
        
        RadiologyModalityEditor editor = new RadiologyModalityEditor();
        editor.setAsText("");
        assertThat(editor.getValue(), is(nullValue()));
    }
    
    /**
    * @see RadiologyModalityEditor#getAsText()
    * @verifies return empty string if value does not contain a radiology modality
    */
    @Test
    public void getAsText_shouldReturnEmptyStringIfValueDoesNotContainARadiologyModality() throws Exception {
        
        RadiologyModalityEditor editor = new RadiologyModalityEditor();
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }
    
    /**
    * @see RadiologyModalityEditor#getAsText()
    * @verifies return radiology modality id if value does contain a radiology modality
    */
    @Test
    public void getAsText_shouldReturnRadiologyModalityIdIfValueDoesContainARadiologyModality() throws Exception {
        
        RadiologyModalityEditor editor = new RadiologyModalityEditor();
        editor.setAsText("1");
        assertThat(editor.getAsText(), is("1"));
    }
}
