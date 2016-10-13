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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Validates <meta> tags of an Mrrt Report Template.
 */
class MetaTagsValidationEngine implements ValidationEngine<Elements> {
    
    
    static String SELECTOR_QUERY_META_ATTRIBUTE_CHARSET = "meta[charset]";
    
    static String SELECTOR_QUERY_META_ATTRIBUTE_NAME = "meta[name]";
    
    static String SELECTOR_QUERY_META_ATTRIBUTE_DATE = "meta[name=dcterms.date]";
    
    private List<ValidationRule<Elements>> rules;
    
    public MetaTagsValidationEngine() {
        rules = new ArrayList<>();
        rules.add(new ElementsExpressionValidationRule("One 'meta' element with attribute 'charset' expected",
                "radiology.MrrtReportTemplate.validation.error.meta.charset.occurence",
                SELECTOR_QUERY_META_ATTRIBUTE_CHARSET, subject -> subject.isEmpty() || subject.size() > 1));
        rules.add(
            new ElementsExpressionValidationRule("At least one 'meta' element encoding dublin core attributes expected",
                    "radiology.MrrtReportTemplate.validation.error.meta.dublinCore.missing",
                    SELECTOR_QUERY_META_ATTRIBUTE_NAME, subject -> subject.isEmpty()));
        rules.add(new ElementsExpressionValidationRule("dcterms.date element should be a valid date",
                "radiology.MrrtReportTemplate.validation.error.date.invalid", SELECTOR_QUERY_META_ATTRIBUTE_DATE,
                subject -> isDateInvalid(subject)));
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.ValidationEngine#run(Object)
     * @should return validation result with no errors if subject passes all checks
     * @should return validation result with error for meta element charset attribute if not present in subject
     * @should return validation result with error for meta element charset attribute if present more than once in subject
     * @should return validation result with error for meta element dublin core if no meta element with name attribute is present in subject
     */
    @Override
    public ValidationResult run(Elements subject) {
        
        final ValidationResult validationResult = new ValidationResult();
        for (ValidationRule rule : rules) {
            rule.check(validationResult, subject);
        }
        return validationResult;
    }
    
    private final boolean isDateInvalid(Elements elements) {
        boolean result = false;
        if (elements.isEmpty()) {
            return result;
        }
        Element dcTermsDateElement = elements.get(0);
        try {
            new SimpleDateFormat(MrrtReportTemplateConstants.DATE_FORMAT).parse(dcTermsDateElement.attr("content"));
        }
        catch (ParseException ex) {
            result = true;
        }
        return result;
    }
}
