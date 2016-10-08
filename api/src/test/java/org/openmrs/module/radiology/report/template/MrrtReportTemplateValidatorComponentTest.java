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
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies pass if template template follows mrrt standards
     */
    @Test
    public void validate_shouldPassIfTemplateTemplateFileFollowsMrrtStandards() throws Exception {
        
        String templateContent = getFileContent("mrrttemplates/ihe/connectathon/2015/CTChestAbdomen.html");
        
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if template does not have an html element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTemplateDoesNotHaveAnHtmlElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-missingHtmlElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if template has more than one html element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTemplateHasMoreThanOneHtmlElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleHtmlElements.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if html element does not have a head element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHtmlElementDoesNotHaveAHeadElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noHeadElementFound.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if html element has more than one head element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHtmlElementHasMoreThanOneHeadElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleHeadElements.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if head element does not have a title element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementDoesNotHaveATitleElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noTitleElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if head element has more than one title element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementHasMoreThanOneTitleElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleTitleElements.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if head element does not have a meta element with charset attribute
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementDoesNotHaveAMetaElementWithCharsetAttribute() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noMetaElementWithCharsetAttribute.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if head element has more than one meta element with charset attribute
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementHasMoreThanOneMetaElementWithCharsetAttribute()
            throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleMetaElementsWithCharsetAttribute.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if head element does not have one or more meta elements denoting dublin core attributes
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementDoesNotHaveOneOrMoreMetaElementsDenotingDublinCoreAttributes()
            throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noMetaElementDenotingDublinCoreAttributes.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if head element does not have script element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementDoesNotHaveScriptElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noScriptElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if head element has more than one script element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementHasMoreThanOneScriptElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleScriptElements.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if script element does not have a template attributes element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfScriptElementDoesNotHaveATemplateAttributesElement() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noTemplateAttributesElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if script element has more than one template attributes element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfScriptElementHasMoreThanOneTemplateAttributesElement() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleTemplateAttributesElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if coding schemes element does not have at least one coding scheme element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfCodingSchemesElementDoesNotHaveAtLeastOneCodingSchemeElement()
            throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-codingSchemesShouldHaveAtLeastOneCodingSchemeElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if term element does not have a code element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTermElementDoesNotHaveACodeElement() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-termElementShouldHaveOneCodeElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if term element has more than one code element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTermElementHasMoreThanOneCodeElement() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleCodeElementsInTermElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if code element lacks one of meaning scheme or value attribute
     */
    @Test
    public void validate_shouldThrowApiExceptionIfCodeElementLacksOneOfMeaningSchemeOrValueAttribute() throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-missingAttributesInCodeElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if template attributes element does not have a coded content element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTemplateAttributesElementDoesNotHaveACodedContentElement()
            throws Exception {
        
        String templateContent = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-missingAttributesInCodeElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if template attributes element has more than one coded content element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTemplateAttributesElementHasMoreThanOneCodedContentElement()
            throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleCodedContent.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if html element does not have a body element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHtmlElementDoesNotHaveABodyElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noBodyElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies throw api exception if html element has more than one body element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHtmlElementHasMoreThanOneBodyElement() throws Exception {
        
        String templateContent =
                getFileContent("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleBodyElement.html");
        
        expectedException.expect(APIException.class);
        validator.validate(templateContent);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     * @verifies catch all violation errors and throw an mrrt report template exception
     */
    @Test
    public void validate_catchAllViolationErrorsAndThrowAnMrrtReportTemplateException() throws Exception {
        
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
