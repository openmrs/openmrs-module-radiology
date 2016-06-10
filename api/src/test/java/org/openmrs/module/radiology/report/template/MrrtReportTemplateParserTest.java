/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

/**
 * Unit test for the MrrtReportTemplateParser 
 */
public class MrrtReportTemplateParserTest {
    
    
    private static final String CHARSET = "UTF-8";
    
    private static final String TEST_DCTERMS_TITLE = "Cardiac MRI: Function and Viability";
    
    private static final String TEST_DCTERMS_DESCRIPTION =
            "Cardiac MRI: Function and Viability template :: Authored by Jacobs JE, et al. ";
    
    private static final String TEST_DCTERMS_IDENTIFIER = "http://www.radreport.org/template/0000049";
    
    private static final String TEST_DCTERMS_LANGUAGE = "en";
    
    private static final String TEST_DCTERMS_TYPE = "IMAGE_REPORT_TEMPLATE";
    
    private static final String TEST_DCTERMS_PUBLISHER = "Radiological Society of North America (RSNA)";
    
    private static final String TEST_DCTERMS_RIGHTS = "May be used gratis, subject to license agreement";
    
    private static final String TEST_DCTERMS_LICENSE = "http://www.radreport.org/license.pdf";
    
    private static final String TEST_DCTERMS_DATE = "2012-07-19";
    
    private static final String TEST_DCTERMS_CREATOR = "Jacobs JE, et al. ";
    
    /**
    * @see MrrtReportTemplateParser#parse(File)
    * @verifies return an mrrt template object if file is valid
    */
    @Test
    public void parse_shouldReturnAnMrrtTemplateObjectIfFileIsValid() throws Exception {
        File file = new File(getClass().getClassLoader()
                .getResource("TestMrrtReportTemplate.html")
                .getFile());
        
        MrrtReportTemplate template = MrrtReportTemplateParser.parse(file);
        
        Assert.assertNotNull(template);
        Assert.assertThat(template.getCharset(), is(CHARSET));
        Assert.assertThat(template.getPath(), is(file.getAbsolutePath()));
        Assert.assertThat(template.getDcTermsTitle(), is(TEST_DCTERMS_TITLE));
        Assert.assertThat(template.getDcTermsDescription(), is(TEST_DCTERMS_DESCRIPTION));
        Assert.assertThat(template.getDcTermsIdentifier(), is(TEST_DCTERMS_IDENTIFIER));
        Assert.assertThat(template.getDcTermsLanguage(), is(TEST_DCTERMS_LANGUAGE));
        Assert.assertThat(template.getDcTermsLanguage(), is(TEST_DCTERMS_LANGUAGE));
        Assert.assertThat(template.getDcTermsType(), is(TEST_DCTERMS_TYPE));
        Assert.assertThat(template.getDcTermsPublisher(), is(TEST_DCTERMS_PUBLISHER));
        Assert.assertThat(template.getDcTermsRights(), is(TEST_DCTERMS_RIGHTS));
        Assert.assertThat(template.getDcTermsLicense(), is(TEST_DCTERMS_LICENSE));
        Assert.assertThat(template.getDcTermsDate(), is(TEST_DCTERMS_DATE));
        Assert.assertThat(template.getDcTermsCreator(), is(TEST_DCTERMS_CREATOR));
    }
}
