/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report;

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
 * Tests {@link RadiologyReportEditor}.
 */
public class RadiologyReportEditorComponentTest extends BaseModuleContextSensitiveTest {
    
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/RadiologyReportServiceComponentTestDataset.xml";
    
    private static final String EXISTING_RADIOLOGY_REPORT_UUID = "e699d90d-e230-4762-8747-d2d0059394b0";
    
    private static final String NON_EXISTING_RADIOLOGY_REPORT_UUID = "637d5011-49f5-4ce8-b4ce-47b37ff2cda2";
    
    @Autowired
    private RadiologyReportService radiologyReportService;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
    * @see RadiologyReportEditor#setAsText(String)
    * @verifies set value to radiology report whos id matches given text
    */
    @Test
    public void setAsText_shouldSetValueToRadiologyReportWhosIdMatchesGivenText() throws Exception {
        RadiologyReportEditor editor = new RadiologyReportEditor();
        editor.setAsText("1");
        assertThat(editor.getValue(), is(notNullValue()));
        assertThat((RadiologyReport) editor.getValue(), is(radiologyReportService.getRadiologyReport(1)));
    }
    
    /**
    * @see RadiologyReportEditor#setAsText(String)
    * @verifies set value to radiology report whos uuid matches given text
    */
    @Test
    public void setAsText_shouldSetValueToRadiologyReportWhosUuidMatchesGivenText() throws Exception {
        RadiologyReportEditor editor = new RadiologyReportEditor();
        editor.setAsText(EXISTING_RADIOLOGY_REPORT_UUID);
        assertThat(editor.getValue(), is(notNullValue()));
        assertThat((RadiologyReport) editor.getValue(),
            is(radiologyReportService.getRadiologyReportByUuid(EXISTING_RADIOLOGY_REPORT_UUID)));
    }
    
    /**
    * @see RadiologyReportEditor#setAsText(String)
    * @verifies throw illegal argument exception for radiology report not found
    */
    @Test
    public void setAsText_shouldThrowIllegalArgumentExceptionForRadiologyReportNotFound() throws Exception {
        RadiologyReportEditor editor = new RadiologyReportEditor();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("RadiologyReport not found: ");
        editor.setAsText(NON_EXISTING_RADIOLOGY_REPORT_UUID);
    }
    
    /**
    * @see RadiologyReportEditor#setAsText(String)
    * @verifies return null for empty text
    */
    @Test
    public void setAsText_shouldReturnNullForEmptyText() throws Exception {
        RadiologyReportEditor editor = new RadiologyReportEditor();
        editor.setAsText("");
        assertThat(editor.getValue(), is(nullValue()));
    }
    
    /**
    * @see RadiologyReportEditor#getAsText()
    * @verifies return empty string if value does not contain a radiology report
    */
    @Test
    public void getAsText_shouldReturnEmptyStringIfValueDoesNotContainARadiologyReport() throws Exception {
        RadiologyReportEditor editor = new RadiologyReportEditor();
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }
    
    /**
    * @see RadiologyReportEditor#getAsText()
    * @verifies return radiology report id if value does contain a radiology report
    */
    @Test
    public void getAsText_shouldReturnRadiologyReportIdIfValueDoesContainARadiologyReport() throws Exception {
        RadiologyReportEditor editor = new RadiologyReportEditor();
        editor.setAsText("1");
        assertThat(editor.getAsText(), is("1"));
    }
}
