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
 * Runs a set of rules on a subject and returns collected errors in the result.
 *
 * @param <T> the type of subject the engine should validate
 * @see ValidationRule
 * @see ValidationResult
 */
public interface ValidationEngine<T> {
    
    
    /**
     * Validates a subject collecting the errors in validation result.
     *
     * @param subject the subject to be validated
     * @return the validation result containing found errors
     */
    public ValidationResult run(T subject);
}
