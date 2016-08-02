/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

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
 * Tests {@link MrrtReportTemplateEditor}.
 */
public class MrrtReportTemplateEditorComponentTest extends BaseModuleContextSensitiveTest {
    
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/MrrtReportTemplateServiceComponentTestDataset.xml";
    
    private static final String EXISTING_MRRT_TEMPLATE_UUID = "aa551445-def0-4f93-9047-95f0a9afbdce";
    
    private static final String NON_EXISTING_MRRT_TEMPLATE_UUID = "637d5011-49f5-4ce8-b4ce-47b37ff2cda2";
    
    @Autowired
    private MrrtReportTemplateService mrrtReportTemplateService;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
    * @see MrrtReportTemplateEditor#setAsText(String)
    * @verifies set value to mrrt report template whos id matches given text
    */
    @Test
    public void setAsText_shouldSetValueToMrrtReportTemplateWhosIdMatchesGivenText() throws Exception {
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        editor.setAsText("1");
        assertThat(editor.getValue(), is(notNullValue()));
        assertThat((MrrtReportTemplate) editor.getValue(), is(mrrtReportTemplateService.getMrrtReportTemplate(1)));
    }
    
    /**
    * @see MrrtReportTemplateEditor#setAsText(String)
    * @verifies set value to mrrt report template whos uuid matches given text
    */
    @Test
    public void setAsText_shouldSetValueToMrrtReportTemplateWhosUuidMatchesGivenText() throws Exception {
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        editor.setAsText(EXISTING_MRRT_TEMPLATE_UUID);
        assertThat(editor.getValue(), is(notNullValue()));
        assertThat((MrrtReportTemplate) editor.getValue(),
            is(mrrtReportTemplateService.getMrrtReportTemplateByUuid(EXISTING_MRRT_TEMPLATE_UUID)));
    }
    
    /**
    * @see MrrtReportTemplateEditor#setAsText(String)
    * @verifies throw illegal argument exception for mrrt report template not found
    */
    @Test
    public void setAsText_shouldThrowIllegalArgumentExceptionForMrrtReportTemplateNotFound() throws Exception {
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("MrrtReportTemplate not found: ");
        editor.setAsText(NON_EXISTING_MRRT_TEMPLATE_UUID);
    }
    
    /**
    * @see MrrtReportTemplateEditor#setAsText(String)
    * @verifies return null for empty text
    */
    @Test
    public void setAsText_shouldReturnNullForEmptyText() throws Exception {
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        editor.setAsText("");
        assertThat(editor.getValue(), is(nullValue()));
    }
    
    /**
    * @see MrrtReportTemplateEditor#getAsText()
    * @verifies return empty string if value does not contain a mrrt report template
    */
    @Test
    public void getAsText_shouldReturnEmptyStringIfValueDoesNotContainAMrrtReportTemplate() throws Exception {
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        editor.setAsText("");
        assertThat(editor.getAsText(), is(""));
    }
    
    /**
    * @see MrrtReportTemplateEditor#getAsText()
    * @verifies return mrrt report template id if value does contain a mrrt report template
    */
    @Test
    public void getAsText_shouldReturnMrrtReportTemplateIdIfValueDoesContainAMrrtReportTemplate() throws Exception {
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        editor.setAsText("1");
        assertThat(editor.getAsText(), is("1"));
    }
}
