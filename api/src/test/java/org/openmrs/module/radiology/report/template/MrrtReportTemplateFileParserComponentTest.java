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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@code MrrtReportTemplateFileParser}.
 */
public class MrrtReportTemplateFileParserComponentTest extends BaseModuleContextSensitiveTest {
    
    
    @Autowired
    private MrrtReportTemplateFileParser parser;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/MrrtReportTemplateFileParserComponentTestDataset.xml";
    
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
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * Get a files content as string.
     *
     * @param path the path to get the file content from
     * @return the file content
     */
    private String getFileContent(String path) throws IOException {
        
        File file = getFile(path);
        return getString(file);
    }
    
    /**
     * Get a file from the test resources.
     *
     * @param path the path to get the file from
     * @return the file on given path
     */
    private File getFile(String path) {
        return new File(getClass().getClassLoader()
                .getResource(path)
                .getFile());
    }
    
    /**
     * Get a file from the test resources.
     *
     * @param file the file to get the content from
     * @return the file content
     */
    private String getString(File file) throws IOException {
        String content = null;
        try (InputStream in = new FileInputStream(file)) {
            content = IOUtils.toString(in);
        }
        return content;
    }
    
    /**
     * @see MrrtReportTemplateFileParser#parse(String)
     * @verifies return an mrrt template object if file is valid
     */
    @Test
    public void parse_shouldReturnAnMrrtTemplateObjectIfFileIsValid() throws Exception {
        
        String templateContent = getFileContent("mrrttemplates/ihe/connectathon/2015/CTChestAbdomen.html");
        
        MrrtReportTemplate template = parser.parse(templateContent);
        
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
    
    /**
     * @see MrrtReportTemplateFileParser#parse(String)
     * @verifies store terms element in template object if they match a concept reference term in openmrs
     */
    @Test
    public void parse_shouldStoreTermsElementInTemplateObjectIfTheyMatchAconceptReferenceTermInOpenmrs() throws IOException {
        
        String templateContent = getFileContent("mrrttemplates/ihe/connectathon/2015/CTChestAbdomen.html");
        
        MrrtReportTemplate template = parser.parse(templateContent);
        
        assertNotNull(template.getTerms());
        assertThat(template.getTerms()
                .size(),
            is(1));
        ConceptSource conceptSource = Context.getConceptService()
                .getConceptSourceByName("RADLEX");
        ConceptReferenceTerm referenceTerm = Context.getConceptService()
                .getConceptReferenceTermByCode("RID10321", conceptSource);
        
        assertThat(template.getTerms()
                .contains(referenceTerm),
            is(true));
    }
    
    /**
     * @see MrrtReportTemplateFileParser#parse(String)
     * @verifies skip terms element in template file if no corresponding concept reference term was found
     */
    @Test
    public void parse_skipTermElementsInTemplateFileIfNoCorrespondingConceptReferenceTermWasFound() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/CTChestAbdomen-missingReferenceTermsForTemplateAttributesTermElements.html");
        
        MrrtReportTemplate template = parser.parse(templateContent);
        
        assertNull(template.getTerms());
    }
    
    /**
     * @see MrrtReportTemplateFileParser#parse(String)
     * @verifies ignore case when searching for a matching concept source
     */
    @Test
    public void parse_shouldIgnoreCaseWhenSearchingForAMatchingConceptSource() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/CTChestAbdomen-schemeIsInLowerCase.html");
        
        MrrtReportTemplate template = parser.parse(templateContent);
        
        assertNotNull(template.getTerms());
        assertThat(template.getTerms()
                .size(),
            is(1));
        ConceptSource conceptSource = Context.getConceptService()
                .getConceptSourceByName("RADLEX");
        ConceptReferenceTerm referenceTerm = Context.getConceptService()
                .getConceptReferenceTermByCode("RID10321", conceptSource);
        
        assertThat(template.getTerms()
                .contains(referenceTerm),
            is(true));
    }
}
