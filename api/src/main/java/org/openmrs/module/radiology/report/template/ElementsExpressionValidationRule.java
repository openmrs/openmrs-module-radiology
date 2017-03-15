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

import java.util.function.Predicate;

import org.jsoup.select.Elements;

/**
 * Checks a condition on a subset of elements matching a selector.
 */
class ElementsExpressionValidationRule implements ValidationRule<Elements> {
    
    
    private final String description;
    
    private final String messageCode;
    
    private final String elementsSelector;
    
    private final Predicate<Elements> condition;
    
    public ElementsExpressionValidationRule(String description, String messageCode, String elementsSelector,
        Predicate<Elements> condition) {
        this.description = description;
        this.messageCode = messageCode;
        this.elementsSelector = elementsSelector;
        this.condition = condition;
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.ValidationRule#check(ValidationResult, Object)
     */
    @Override
    public void check(ValidationResult validationResult, Elements subject) {
        Elements elements = subject.select(elementsSelector);
        if (condition.test(elements)) {
            validationResult.addError(description, messageCode);
        }
    }
}
