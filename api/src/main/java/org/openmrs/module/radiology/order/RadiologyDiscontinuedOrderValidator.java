/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the {@link Order} class.
 */
@Component
public class RadiologyDiscontinuedOrderValidator implements Validator {
    
    
    /** Log for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    /**
     * Determines if the command object being submitted is a valid type
     *
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     * @should return true for Order objects and subclasses
     * @should return false for other object types
     */
    public boolean supports(Class clazz) {
        return Order.class.isAssignableFrom(clazz);
    }
    
    /**
     * Checks the form object for any inconsistencies/errors
     *
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     * @should fail validation if order is null
     * @should fail validation if orderer is null
     * @should fail validation if orderReasonNonCoded is null
     * @should pass validation if all fields are correct
     */
    public void validate(Object obj, Errors errors) {
        final Order order = (Order) obj;
        if (order == null) {
            errors.reject("error.general");
        } else {
            ValidationUtils.rejectIfEmpty(errors, "orderer", "error.null");
            ValidationUtils.rejectIfEmpty(errors, "orderReasonNonCoded", "error.null");
        }
    }
}
