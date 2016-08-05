/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates {@link VoidRadiologyReportRequest}.
 */
@Component
public class VoidRadiologyReportRequestValidator implements Validator {
    
    
    /** Log for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    /**
     * Determines if the command object being submitted is a valid type
     *
     * @see Validator#supports(Class)
     * @should return true only for void radiology report request objects
     * @should return false for other object types
     */
    @Override
    public boolean supports(Class clazz) {
        return VoidRadiologyReportRequest.class.equals(clazz);
    }
    
    /**
     * Checks the form object for any inconsistencies/errors
     *
     * @see Validator#validate(Object, Errors)
     * @should fail validation if void reason is null or empty or whitespaces only
     * @should pass validation if all fields are correct
     */
    @Override
    public void validate(Object obj, Errors errors) {
        final VoidRadiologyReportRequest voidRadiologyReportRequest = (VoidRadiologyReportRequest) obj;
        if (voidRadiologyReportRequest == null) {
            errors.reject("error.general");
        } else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "voidReason", "error.null");
        }
    }
}
