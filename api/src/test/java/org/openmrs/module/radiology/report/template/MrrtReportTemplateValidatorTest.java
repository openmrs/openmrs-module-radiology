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

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.api.APIException;

/**
 * Tests {@code MrrtReportTemplateValidator}
 */
public class MrrtReportTemplateValidatorTest {
    
    
    MrrtReportTemplateValidator validator = new XsdMrrtReportTemplateValidator();
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies pass if template template file follows mrrt standards
     */
    @Test
    public void validate_shouldPassIfTemplateTemplateFileFollowsMrrtStandards() throws Exception {
        File validTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/CTChestAbdomen.html")
                .getFile());
        validator.validate(validTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if template does not have an html element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTemplateDoesNotHaveAnHtmlElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-missingHtmlElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if template has more than one html element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTemplateHasMoreThanOneHtmlElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleHtmlElements.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if html element does not have a head element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHtmlElementDoesNotHaveAHeadElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noHeadElementFound.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if html element has more than one head element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHtmlElementHasMoreThanOneHeadElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleHeadElements.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if head element does not have a title element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementDoesNotHaveATitleElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noTitleElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if head element has more than one title element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementHasMoreThanOneTitleElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleTitleElements.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if head element does not have a meta element with charset attribute
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementDoesNotHaveAMetaElementWithCharsetAttribute() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource(
                    "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noMetaElementWithCharsetAttribute.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if head element has more than one meta element with charset attribute
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementHasMoreThanOneMetaElementWithCharsetAttribute()
            throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource(
                    "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleMetaElementsWithCharsetAttribute.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if head element does not have one or more meta elements denoting dublin core attributes
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementDoesNotHaveOneOrMoreMetaElementsDenotingDublinCoreAttributes()
            throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource(
                    "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noMetaElementDenotingDublinCoreAttributes.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if head element does not have script element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementDoesNotHaveScriptElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noScriptElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if head element has more than one script element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHeadElementHasMoreThanOneScriptElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleScriptElements.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if script element does not have a template attributes element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfScriptElementDoesNotHaveATemplateAttributesElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource(
                    "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noTemplateAttributesElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if script element has more than one template attributes element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfScriptElementHasMoreThanOneTemplateAttributesElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource(
                    "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleTemplateAttributesElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if coding schemes element does not have at least one coding scheme element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfCodingSchemesElementDoesNotHaveAtLeastOneCodingSchemeElement()
            throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource(
                    "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-codingSchemesShouldHaveAtLeastOneCodingSchemeElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if term element does not have a code element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTermElementDoesNotHaveACodeElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource(
                    "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-termElementShouldHaveOneCodeElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if term element has more than one code element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTermElementHasMoreThanOneCodeElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource(
                    "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleCodeElementsInTermElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if code element lacks one of meaning scheme or value attribute
     */
    @Test
    public void validate_shouldThrowApiExceptionIfCodeElementLacksOneOfMeaningSchemeOrValueAttribute() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource(
                    "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-missingAttributesInCodeElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if template attributes element does not have a coded content element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTemplateAttributesElementDoesNotHaveACodedContentElement()
            throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource(
                    "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-missingAttributesInCodeElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if template attributes element has more than one coded content element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfTemplateAttributesElementHasMoreThanOneCodedContentElement()
            throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleCodedContent.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if html element does not have a body element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHtmlElementDoesNotHaveABodyElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noBodyElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(File)
     * @verifies throw api exception if html element has more than one body element
     */
    @Test
    public void validate_shouldThrowApiExceptionIfHtmlElementHasMoreThanOneBodyElement() throws Exception {
        expectedException.expect(APIException.class);
        final File invalidTemplate = new File(getClass().getClassLoader()
                .getResource("mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-multipleBodyElement.html")
                .getFile());
        validator.validate(invalidTemplate);
    }
}
