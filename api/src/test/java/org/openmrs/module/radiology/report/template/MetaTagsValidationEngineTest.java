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

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link MetaTagsValidationEngine}.
 */
public class MetaTagsValidationEngineTest {
    
    
    Attributes charsetAttributes;
    
    Attributes dublinElementAttributes;
    
    Element charsetElement;
    
    Element dublinElement;
    
    @Before
    public void setUp() {
        charsetAttributes = new Attributes();
        charsetAttributes.put("charset", "UTF-8");
        charsetElement = new Element(Tag.valueOf("meta"), "", charsetAttributes);
        
        dublinElementAttributes = new Attributes();
        dublinElementAttributes.put("name", "dcterms.title");
        dublinElementAttributes.put("content", "CT Abdomen");
        dublinElement = new Element(Tag.valueOf("meta"), "", dublinElementAttributes);
    }
    
    /**
     * @verifies return validation result with no errors if subject passes all checks
     * @see MetaTagsValidationEngine#run(org.jsoup.select.Elements)
     */
    @Test
    public void run_shouldReturnValidationResultWithNoErrorsIfSubjectPassesAllChecks() throws Exception {
        
        Elements elements = new Elements(charsetElement, dublinElement);
        
        MetaTagsValidationEngine validationEngine = new MetaTagsValidationEngine();
        
        ValidationResult validationResult = validationEngine.run(elements);
        assertFalse(validationResult.hasErrors());
    }
    
    /**
     * @verifies return validation result with error for meta element charset attribute if not present in subject
     * @see MetaTagsValidationEngine#run(org.jsoup.select.Elements)
     */
    @Test
    public void run_shouldReturnValidationResultWithErrorForMetaElementCharsetAttributeIfNotPresentInSubject()
            throws Exception {
        
        Elements elements = new Elements(dublinElement);
        
        MetaTagsValidationEngine validationEngine = new MetaTagsValidationEngine();
        
        ValidationResult validationResult = validationEngine.run(elements);
        assertTrue(validationResult.hasErrors());
        assertThat(validationResult.getErrors()
                .get(0)
                .getMessageCode(),
            is("radiology.MrrtReportTemplate.validation.error.meta.charset.occurence"));
    }
    
    /**
     * @verifies return validation result with error for meta element charset attribute if present more than once in subject
     * @see MetaTagsValidationEngine#run(org.jsoup.select.Elements)
     */
    @Test
    public void run_shouldReturnValidationResultWithErrorForMetaElementCharsetAttributeIfPresentMoreThanOnceInSubject()
            throws Exception {
        
        Element otherCharsetElement = new Element(Tag.valueOf("meta"), "", charsetAttributes);
        Elements elements = new Elements(charsetElement, otherCharsetElement, dublinElement);
        
        MetaTagsValidationEngine validationEngine = new MetaTagsValidationEngine();
        
        ValidationResult validationResult = validationEngine.run(elements);
        assertTrue(validationResult.hasErrors());
        assertThat(validationResult.getErrors()
                .get(0)
                .getMessageCode(),
            is("radiology.MrrtReportTemplate.validation.error.meta.charset.occurence"));
    }
    
    /**
     * @verifies return validation result with error for meta element dublin core if no meta element with name attribute is present in subject
     * @see MetaTagsValidationEngine#run(org.jsoup.select.Elements)
     */
    @Test
    public void
            run_shouldReturnValidationResultWithErrorForMetaElementDublinCoreIfNoMetaElementWithNameAttributeIsPresentInSubject()
                    throws Exception {
        
        Elements elements = new Elements(charsetElement);
        
        MetaTagsValidationEngine validationEngine = new MetaTagsValidationEngine();
        
        ValidationResult validationResult = validationEngine.run(elements);
        assertTrue(validationResult.hasErrors());
        assertThat(validationResult.getErrors()
                .get(0)
                .getMessageCode(),
            is("radiology.MrrtReportTemplate.validation.error.meta.dublinCore.missing"));
    }
}
