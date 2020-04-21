/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report;

import org.openmrs.annotation.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates {@link RadiologyReport}.
 */
@Component
@Handler(supports = { RadiologyReport.class })
public class RadiologyReportValidator implements Validator {
    
    
    protected final Logger log = LoggerFactory.getLogger(RadiologyReportValidator.class);
    
    /**
     * Determines if the command object being submitted is a valid type
     *
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     * <strong>Should</strong> return true for radiology report objects
     * <strong>Should</strong> return false for other object types
     */
    @Override
    public boolean supports(Class clazz) {
        return RadiologyReport.class.isAssignableFrom(clazz);
    }
    
    /**
     * Checks the form object for any inconsistencies/errors
     *
     * @see org.springframework.validation.Validator#validate(Object, Errors)
     * <strong>Should</strong> fail validation if radiology report is null
     * <strong>Should</strong> fail validation if principal results interpreter is null or empty or whitespaces only
     * <strong>Should</strong> fail validation if report body is null or empty or whitespaces only
     * <strong>Should</strong> pass validation if all fields are correct
     */
    @Override
    public void validate(Object obj, Errors errors) {
        final RadiologyReport radiologyReport = (RadiologyReport) obj;
        if (radiologyReport == null) {
            errors.reject("error.general");
        } else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "principalResultsInterpreter", "error.null",
                "Provider cannot be null");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "body", "error.null", "Diagnosis cannot be null");
        }
    }
}
