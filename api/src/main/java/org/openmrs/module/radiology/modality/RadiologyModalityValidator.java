/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates {@link RadiologyModality}.
 */
@Component
@Handler(supports = { RadiologyModality.class })
public class RadiologyModalityValidator implements Validator {
    
    
    protected final Log log = LogFactory.getLog(RadiologyModalityValidator.class);
    
    /**
     * Determines if the command object being submitted is a valid type.
     *
     * @see Validator#supports(Class)
     * @should return true for radiology modality objects
     * @should return false for other object types
     */
    @Override
    public boolean supports(Class clazz) {
        return RadiologyModality.class.isAssignableFrom(clazz);
    }
    
    /**
     * Checks the form object for any inconsistencies/errors.
     *
     * @see Validator#validate(Object, Errors)
     * @should fail validation if radiology modality is null
     * @should fail validation if ae title is null or empty or whitespaces only
     * @should fail validation if ae title exceeds 16 characters
     * @should pass validation if all fields are correct
     */
    @Override
    public void validate(Object obj, Errors errors) {
        final RadiologyModality radiologyModality = (RadiologyModality) obj;
        if (radiologyModality == null) {
            errors.reject("error.general");
        } else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "aeTitle", "error.null", "Cannot be empty or null");
            if (radiologyModality.getAeTitle() != null && radiologyModality.getAeTitle()
                    .length() > 16) {
                errors.rejectValue("aeTitle", "radiology.RadiologyModality.error.max.aeTitle", "Cannot exceed 16 character");
            }
        }
    }
}
