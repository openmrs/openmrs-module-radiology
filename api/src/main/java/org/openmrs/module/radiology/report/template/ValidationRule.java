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

/**
 * Represents a rule which can be checked during validation.
 */
public interface ValidationRule<T> {
    
    
    /**
     * Checks given subject and populates validation results.
     *
     * @param validationResult the validation result to be populated
     * @param subject the subject to be checked
     */
    public void check(ValidationResult validationResult, T subject);
}
