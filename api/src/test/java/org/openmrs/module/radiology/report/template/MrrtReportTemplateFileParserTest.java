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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@code MrrtReportTemplateFileParser}.
 */
public class MrrtReportTemplateFileParserTest extends BaseModuleContextSensitiveTest {
    
    
    @Autowired
    private MrrtReportTemplateFileParser parser;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    private static final String CHARSET = "UTF-8";
    
    private static final String TEST_DCTERMS_TITLE = "CT Chest-Abdomen";
    
    private static final String TEST_DCTERMS_DESCRIPTION = "CT Chest-Abdomen";
    
    private static final String TEST_DCTERMS_IDENTIFIER = "1.3.6.1.4.1.21367.13.199.1015";
    
    private static final String TEST_DCTERMS_LANGUAGE = "en";
    
    private static final String TEST_DCTERMS_TYPE = "IMAGE_REPORT_TEMPLATE";
    
    private static final String TEST_DCTERMS_PUBLISHER = "IHE CAT Publisher";
    
    private static final String TEST_DCTERMS_RIGHTS = "IHE Connectathon Rights";
    
    private static final String TEST_DCTERMS_LICENSE = "IHE Connectathon License";
    
    private static final String TEST_DCTERMS_DATE = "2013-06-01";
    
    private static final String TEST_DCTERMS_CREATOR = "Creator James, et al.";
    
    /**
     * @see MrrtReportTemplateFileParser#parse()
     * @verifies return an mrrt template object if file is valid
     */
    @Test
    public void parse_shouldReturnAnMrrtTemplateObjectIfFileIsValid() throws Exception {
        File file = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/CTChestAbdomen.html")
                .getFile());
        
        FileInputStream in = new FileInputStream(file);
        MrrtReportTemplate template = parser.parse(in);
        
        assertNotNull(template);
        assertThat(template.getCharset(), is(CHARSET));
        assertThat(template.getDcTermsTitle(), is(TEST_DCTERMS_TITLE));
        assertThat(template.getDcTermsDescription(), is(TEST_DCTERMS_DESCRIPTION));
        assertThat(template.getDcTermsIdentifier(), is(TEST_DCTERMS_IDENTIFIER));
        assertThat(template.getDcTermsLanguage(), is(TEST_DCTERMS_LANGUAGE));
        assertThat(template.getDcTermsLanguage(), is(TEST_DCTERMS_LANGUAGE));
        assertThat(template.getDcTermsType(), is(TEST_DCTERMS_TYPE));
        assertThat(template.getDcTermsPublisher(), is(TEST_DCTERMS_PUBLISHER));
        assertThat(template.getDcTermsRights(), is(TEST_DCTERMS_RIGHTS));
        assertThat(template.getDcTermsLicense(), is(TEST_DCTERMS_LICENSE));
        assertThat(template.getDcTermsDate(), is(TEST_DCTERMS_DATE));
        assertThat(template.getDcTermsCreator(), is(TEST_DCTERMS_CREATOR));
    }
}
