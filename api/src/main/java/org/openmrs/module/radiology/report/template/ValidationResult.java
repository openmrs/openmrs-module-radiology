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

import java.util.ArrayList;
import java.util.List;

/**
 * Container collecting errors found during validation of the IHE Management of Radiology Report Templates (MRRT).
 *
 */
public class ValidationResult {
    
    
    private List<ValidationError> errors;
    
    /**
     * Get the errors of this validation result.
     *
     * @return the errors
     */
    public List<ValidationError> getErrors() {
        return errors;
    }
    
    /**
     * Creates a new instance of {@link ValidationResult}.
     * @should create a new validation result initializing errors
     */
    public ValidationResult() {
        errors = new ArrayList<>();
    }
    
    /**
     * Add an error to this validation result.
     *
     * @param description the error description
     * @param messageCode the message code of the error
     * @should add new error with given parameters
     */
    public void addError(String description, String messageCode) {
        ValidationError error = new ValidationError(description, messageCode);
        errors.add(error);
    }
    
    /**
     * Add an error to this validation result.
     *
     * @param validationError the validation error
     * @should add given validation error to errors
     */
    public void addError(ValidationError validationError) {
        errors.add(validationError);
    }
    
    /**
     * Assert that this validation result has no errors and throw an exception if it does.
     *
     * @see MrrtReportTemplateValidationException
     * @should throw a validation exception if validation has errors
     * @should not throw a validation exception if validation has errors
     */
    public void assertOk() {
        if (hasErrors()) {
            throw new MrrtReportTemplateValidationException(this);
        }
    }
    
    /**
     * Check if this validation result has errors.
     *
     * @return true if this result has errors and false otherwise
     * @should return true if validation has errors
     * @should return false if validation has no errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * @see Object#toString()
     * @should return ok if validation has no errors
     * @should return error strings if validation has errors
     */
    @Override
    public String toString() {
        
        if (hasErrors()) {
            final StringBuilder result = new StringBuilder();
            result.append("Validation failed due to:\n");
            for (ValidationError error : getErrors()) {
                result.append(error);
                result.append("\n");
            }
            return result.toString();
        } else {
            return "OK";
        }
    }
}
