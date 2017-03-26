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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.api.APIException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link MrrtReportTemplateValidator}.
 */
public class MrrtReportTemplateValidatorComponentTest extends BaseModuleContextSensitiveTest {
    
    
    @Autowired
    MrrtReportTemplateValidator validator;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
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
    
    @Test
    public void shouldPassIfTemplateTemplateFileFollowsMrrtStandards() throws Exception {
        
        String templateContent = getFileContent("mrrttemplates/ihe/connectathon/2015/CTChestAbdomen.html");
        
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfTemplateDoesNotHaveAnHtmlElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-missingHtmlElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfTemplateHasMoreThanOneHtmlElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleHtmlElements.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHtmlElementDoesNotHaveAHeadElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noHeadElementFound.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHtmlElementHasMoreThanOneHeadElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleHeadElements.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHeadElementDoesNotHaveATitleElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noTitleElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHeadElementHasMoreThanOneTitleElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleTitleElements.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHeadElementDoesNotHaveAMetaElementWithCharsetAttribute() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noMetaElementWithCharsetAttribute.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHeadElementHasMoreThanOneMetaElementWithCharsetAttribute() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleMetaElementsWithCharsetAttribute.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHeadElementDoesNotHaveOneOrMoreMetaElementsDenotingDublinCoreAttributes() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noMetaElementDenotingDublinCoreAttributes.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHeadElementDoesNotHaveScriptElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noScriptElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHeadElementHasMoreThanOneScriptElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleScriptElements.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfScriptElementDoesNotHaveATemplateAttributesElement() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noTemplateAttributesElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfScriptElementHasMoreThanOneTemplateAttributesElement() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleTemplateAttributesElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfCodingSchemesElementDoesNotHaveAtLeastOneCodingSchemeElement() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-codingSchemesShouldHaveAtLeastOneCodingSchemeElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfTermElementDoesNotHaveACodeElement() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-termElementShouldHaveOneCodeElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfTermElementHasMoreThanOneCodeElement() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleCodeElementsInTermElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfCodeElementLacksOneOfMeaningSchemeOrValueAttribute() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-missingAttributesInCodeElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfTemplateAttributesElementDoesNotHaveACodedContentElement() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-missingAttributesInCodeElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfTemplateAttributesElementHasMoreThanOneCodedContentElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleCodedContent.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHtmlElementDoesNotHaveABodyElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noBodyElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void shouldFailIfHtmlElementHasMoreThanOneBodyElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleBodyElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    @Test
    public void catchAllViolationErrorsAndThrowAnMrrtReportTemplateException() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-missingCharsetTitleTemplateAttributesBodyElements.html");
        
        try {
            validator.validate(templateContent);
            fail("Expected an MrrtReportTemplateValidationException to be thrown");
        }
        catch (MrrtReportTemplateValidationException e) {
            assertThat(e.getValidationResult()
                    .getErrors()
                    .size(),
                is(4));
        }
    }
}
