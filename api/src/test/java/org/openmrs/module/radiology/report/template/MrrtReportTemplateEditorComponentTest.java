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
    
    @Test
    public void shouldSetValueToMrrtReportTemplateWhosIdMatchesGivenText() throws Exception {
        
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        
        editor.setAsText("1");
        
        assertThat(editor.getValue(), is(notNullValue()));
        assertThat((MrrtReportTemplate) editor.getValue(), is(mrrtReportTemplateService.getMrrtReportTemplate(1)));
    }
    
    @Test
    public void shouldSetValueToMrrtReportTemplateWhosUuidMatchesGivenText() throws Exception {
        
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        
        editor.setAsText(EXISTING_MRRT_TEMPLATE_UUID);
        
        assertThat(editor.getValue(), is(notNullValue()));
        assertThat((MrrtReportTemplate) editor.getValue(),
            is(mrrtReportTemplateService.getMrrtReportTemplateByUuid(EXISTING_MRRT_TEMPLATE_UUID)));
    }
    
    @Test
    public void shouldThrowIllegalArgumentExceptionForMrrtReportTemplateNotFound() throws Exception {
        
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("MrrtReportTemplate not found: ");
        editor.setAsText(NON_EXISTING_MRRT_TEMPLATE_UUID);
    }
    
    @Test
    public void shouldReturnNullForEmptyText() throws Exception {
        
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        
        editor.setAsText("");
        
        assertThat(editor.getValue(), is(nullValue()));
    }
    
    @Test
    public void shouldReturnEmptyStringIfValueDoesNotContainAMrrtReportTemplate() throws Exception {
        
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        
        editor.setAsText("");
        
        assertThat(editor.getAsText(), is(""));
    }
    
    @Test
    public void shouldReturnMrrtReportTemplateIdIfValueDoesContainAMrrtReportTemplate() throws Exception {
        
        MrrtReportTemplateEditor editor = new MrrtReportTemplateEditor();
        
        editor.setAsText("1");
        
        assertThat(editor.getAsText(), is("1"));
    }
}
