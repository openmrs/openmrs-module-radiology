/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.springframework.validation.Errors;

/**
 * Assertions to ease testing of {@link org.springframework.validation.Validator}.
 */
public final class ValidatorAssertions {
    
    
    private ValidatorAssertions() {
        throw new UnsupportedOperationException("Utility class not meant for instantiation");
    }
    
    /**
     * Assert that a single error in given field with code "error.null" caused the validation to fail.
     * 
     * @param errors the errors to check
     * @param field the field causing the validation to fail
     */
    public static void assertSingleNullErrorInField(Errors errors, String field) {
        assertSingleErrorInField(errors, field, "error.null");
    }
    
    /**
     * Assert that a single error in given field with given code caused the validation to fail.
     * 
     * @param errors the errors to check
     * @param field the field causing the validation to fail
     * @param errorCode the error code of the failure
     */
    public static void assertSingleErrorInField(Errors errors, String field, String errorCode) {
        assertSingleErrorOfCode(errors, errorCode);
        assertTrue(String.format("Field '%s' is not in fieldErrors", field), errors.hasFieldErrors(field));
    }
    
    /**
     * Assert that a single error with code "error.general" caused the validation to fail.
     * 
     * @param errors the errors to check
     */
    public static void assertSingleGeneralError(Errors errors) {
        assertSingleErrorOfCode(errors, "error.general");
    }
    
    /**
     * Assert that a single error with given code caused the validation to fail.
     * 
     * @param errors the errors to check
     * @param errorCode the error code of the failure
     */
    public static void assertSingleErrorOfCode(Errors errors, String errorCode) {
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is(errorCode));
    }
}
