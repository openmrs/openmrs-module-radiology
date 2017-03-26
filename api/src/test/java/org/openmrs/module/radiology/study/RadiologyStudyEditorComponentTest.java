/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.study;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link RadiologyStudyEditor}.
 */
public class RadiologyStudyEditorComponentTest extends BaseModuleContextSensitiveTest {
    
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/RadiologyStudyServiceComponentTestDataset.xml";
    
    private static final String EXISTING_RADIOLOGY_STUDY_UUID = "dde7399b-6092-4a3d-88a2-405b6b4499fc";
    
    private static final String NON_EXISTING_RADIOLOGY_STUDY_UUID = "637d5011-49f5-4ce8-b4ce-47b37ff2cda2";
    
    @Autowired
    private RadiologyStudyService radiologyStudyService;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    @Test
    public void shouldSetValueToRadiologyStudyWhosIdMatchesGivenText() throws Exception {
        
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        editor.setAsText("1");
        
        assertThat(editor.getValue(), is(notNullValue()));
        assertThat((RadiologyStudy) editor.getValue(), is(radiologyStudyService.getRadiologyStudy(1)));
    }
    
    @Test
    public void shouldSetValueToRadiologyStudyWhosUuidMatchesGivenText() throws Exception {
        
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        editor.setAsText(EXISTING_RADIOLOGY_STUDY_UUID);
        
        assertThat(editor.getValue(), is(notNullValue()));
        assertThat((RadiologyStudy) editor.getValue(),
            is(radiologyStudyService.getRadiologyStudyByUuid(EXISTING_RADIOLOGY_STUDY_UUID)));
    }
    
    @Test
    public void shouldFailIfRadiologyStudyNotFound() throws Exception {
        
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("RadiologyStudy not found: ");
        editor.setAsText(NON_EXISTING_RADIOLOGY_STUDY_UUID);
    }
    
    @Test
    public void shouldReturnNullForEmptyText() throws Exception {
        
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        editor.setAsText("");
        
        assertThat(editor.getValue(), is(nullValue()));
    }
    
    @Test
    public void shouldReturnEmptyStringIfValueDoesNotContainARadiologyStudy() throws Exception {
        
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        editor.setAsText("");
        
        assertThat(editor.getAsText(), is(""));
    }
    
    @Test
    public void shouldReturnRadiologyStudyIdIfValueDoesContainARadiologyStudy() throws Exception {
        
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        editor.setAsText("1");
        
        assertThat(editor.getAsText(), is("1"));
    }
}
