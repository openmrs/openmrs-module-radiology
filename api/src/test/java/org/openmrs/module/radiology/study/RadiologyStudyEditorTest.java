/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.study;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class RadiologyStudyEditorTest extends BaseModuleContextSensitiveTest {
    
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/RadiologyStudyServiceComponentTestDataset.xml";
    
    private static final String EXISTING_RADIOLOGY_STUDY_INSTANCE_UID = "1.2.826.0.1.3680043.8.2186.1.1";
    
    private static final String NON_EXISTING_RADIOLOGY_STUDY_INSTANCE_UID = "9.2.826.0.1.1234567.8.2186.1.1";
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see RadiologyStudyEditor#setAsText(String)
     * @verifies set using id
     */
    @Test
    public void setAsText_shouldSetUsingId() throws Exception {
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        editor.setAsText("1");
        Assert.assertNotNull(editor.getValue());
    }
    
    /**
     * @see RadiologyStudyEditor#setAsText(String)
     * @verifies set using instance uid
     */
    @Test
    public void setAsText_shouldSetUsingInstanceUuid() throws Exception {
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        editor.setAsText(EXISTING_RADIOLOGY_STUDY_INSTANCE_UID);
        Assert.assertNotNull(editor.getValue());
    }
    
    /**
     * @see RadiologyStudyEditor#setAsText(String)
     * @verifies throw illegal argument exception for radiology study not found
     */
    @Test
    public void setAsText_shouldThrowIllegalArgumentExceptionForRadiologyStudyNotFound() throws Exception {
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("RadiologyStudy not found: ");
        editor.setAsText(NON_EXISTING_RADIOLOGY_STUDY_INSTANCE_UID);
        
    }
    
    /**
     * @see RadiologyStudyEditor#setAsText(String)
     * @verifies return null for empty text
     */
    @Test
    public void setAsText_shouldReturnNullForEmptyText() throws Exception {
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        editor.setAsText("");
        Assert.assertNull(editor.getValue());
    }
    
    /**
     * @see RadiologyStudyEditor#getAsText()
     * @verifies return empty string for non existing radiology study
     */
    @Test
    public void getAsText_shouldReturnEmptyStringForNonExistingRadiologyStudy() throws Exception {
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        editor.setAsText("");
        Assert.assertEquals("", editor.getAsText());
    }
    
    /**
     * @see RadiologyStudyEditor#getAsText()
     * @verifies return id as string for existing radiology study
     */
    @Test
    public void getAsText_shouldReturnIdAsStringForExistingRadiologyStudy() throws Exception {
        RadiologyStudyEditor editor = new RadiologyStudyEditor();
        editor.setAsText("1");
        Assert.assertEquals("1", editor.getAsText());
    }
}