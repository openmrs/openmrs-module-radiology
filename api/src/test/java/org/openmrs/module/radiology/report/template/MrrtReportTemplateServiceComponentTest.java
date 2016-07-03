/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class MrrtReportTemplateServiceComponentTest extends BaseModuleContextSensitiveTest {
    
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/MrrtReportTemplateServiceComponentTestDataset.xml";
    
    private static final int EXISTING_TEMPLATE_ID = 1;
    
    private static final String VALID_UUID = "2379d290-96f7-408a-bbae-270387e3b92e";
    
    private static final String INVALID_UUID = "invalid uuid";
    
    private static final String EXISTING_TEMPLATE_TITLE = "title1";
    
    private static final String NON_EXISTENT_TEMPLATE_TITLE = "invalid";
    
    @Autowired
    private MrrtReportTemplateService mrrtReportTemplateService;
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplate(Integer)
    * @verifies get template with given id
    */
    @Test
    public void getMrrtReportTemplate_shouldGetTemplateWithGivenId() throws Exception {
        MrrtReportTemplate existingTemplate = mrrtReportTemplateService.getMrrtReportTemplate(EXISTING_TEMPLATE_ID);
        
        assertNotNull(existingTemplate);
        assertEquals(existingTemplate.getCharset(), "UTF-8");
        assertEquals(existingTemplate.getDcTermsTitle(), "title1");
        assertEquals(existingTemplate.getDcTermsLanguage(), "en");
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplate(Integer)
    * @verifies return null if no match was found
    */
    @Test
    public void getMrrtReportTemplate_shouldReturnNullIfNoMatchWasFound() throws Exception {
        assertNull(mrrtReportTemplateService.getMrrtReportTemplate(23));
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplate(Integer)
    * @verifies throw illegal argument exception if given null
    */
    @Test
    public void getMrrtReportTemplate_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("id cannot be null");
        mrrtReportTemplateService.getMrrtReportTemplate(null);
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateByUuid(String)
    * @verifies find object given valid uuid
    */
    @Test
    public void getMrrtReportTemplateByUuid_shouldFindObjectGivenValidUuid() {
        MrrtReportTemplate valid = mrrtReportTemplateService.getMrrtReportTemplateByUuid(VALID_UUID);
        
        assertNotNull(valid);
        assertThat(valid.getTemplateId(), is(EXISTING_TEMPLATE_ID));
        assertThat(valid.getDcTermsTitle(), is("title1"));
    }
    
    /**
     * @see MrrtReportTemplateService#getMrrtReportTemplateByUuid(String)
     * @verifies return null if no object found with given uuid
     */
    @Test
    public void getMrrtReportTemplateByUuid_shouldReturnNullIfNoObjectFoundWithGivenUuid() {
        assertNull(mrrtReportTemplateService.getMrrtReportTemplateByUuid(INVALID_UUID));
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateByUuid(String)
    * @verifies throw illegal argument exception if given null
    */
    @Test
    public void getMrrtReportTemplateByUuid_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("uuid cannot be null");
        mrrtReportTemplateService.getMrrtReportTemplateByUuid(null);
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateByTitle(String)
    * @verifies should get list of templates that match given title
    */
    @Test
    public void getMrrtReportTemplateByTitle_shouldGetListOfTemplatesThatMatchGivenTitle() throws Exception {
        List<MrrtReportTemplate> templates = mrrtReportTemplateService.getMrrtReportTemplateByTitle(EXISTING_TEMPLATE_TITLE);
        
        assertNotNull(templates);
        assertThat(templates.size(), is(1));
    }
    
    /**
     * @see MrrtReportTemplateService#getMrrtReportTemplateByTitle(String)
     * @verifies should return empty list of no match is found
     */
    @Test
    public void getMrrtReportTemplateByTitle_shouldReturnEmptyListOfNoMatchIsFound() throws Exception {
        List<MrrtReportTemplate> templates =
                mrrtReportTemplateService.getMrrtReportTemplateByTitle(NON_EXISTENT_TEMPLATE_TITLE);
        
        assertNotNull(templates);
        assertEquals(templates.isEmpty(), true);
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateByTitle(String)
    * @verifies throw illegal argument exception if given null
    */
    @Test
    public void getMrrtReportTemplateByTitle_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("title cannot be null");
        mrrtReportTemplateService.getMrrtReportTemplateByTitle(null);
    }
    
    /**
    * @see MrrtReportTemplateService#purgeMrrtReportTemplate(MrrtReportTemplate)
    * @verifies delete report from database
    */
    @Test
    public void purgeMrrtReportTemplate_shouldDeleteReportFromDatabase() throws Exception {
        MrrtReportTemplate template = mrrtReportTemplateService.getMrrtReportTemplate(EXISTING_TEMPLATE_ID);
        
        assertNotNull(template);
        mrrtReportTemplateService.purgeMrrtReportTemplate(template);
        
        MrrtReportTemplate deleted = mrrtReportTemplateService.getMrrtReportTemplate(EXISTING_TEMPLATE_ID);
        assertNull(deleted); // should be null since it's been deleted
    }
    
    /**
    * @see MrrtReportTemplateService#purgeMrrtReportTemplate(MrrtReportTemplate)
    * @verifies throw illigal argument exception if given null
    */
    @Test
    public void purgeMrrtReportTemplate_shouldThrowIlligalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("template cannot be null");
        mrrtReportTemplateService.purgeMrrtReportTemplate(null);
    }
    
    /**
    * @see MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
    * @verifies save or update given template
    */
    @Test
    public void saveMrrtReportTemplate_shouldSaveOrUpdateGivenTemplate() throws Exception {
        MrrtReportTemplate template = new MrrtReportTemplate();
        
        template.setDcTermsTitle("sample title");
        template.setDcTermsDescription("sample description");
        
        MrrtReportTemplate saved = mrrtReportTemplateService.saveMrrtReportTemplate(template);
        MrrtReportTemplate newTemplate = mrrtReportTemplateService.getMrrtReportTemplate(saved.getTemplateId());
        
        assertNotNull(saved);
        assertNotNull(newTemplate);
        assertEquals(newTemplate.getDcTermsTitle(), template.getDcTermsTitle());
        assertEquals(newTemplate.getDcTermsDescription(), template.getDcTermsDescription());
    }
    
    /**
    * @see MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
    * @verifies throw illegal argument exception if given null
    */
    @Test
    public void saveMrrtReportTemplate_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("template cannot be null");
        mrrtReportTemplateService.saveMrrtReportTemplate(null);
    }
    
}
