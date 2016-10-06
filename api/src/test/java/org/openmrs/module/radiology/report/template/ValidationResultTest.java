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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link ValidationResult}.
 */
public class ValidationResultTest {
    
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    ValidationResult validationResultWithoutErrors;
    
    ValidationResult validationResultWithErrors;
    
    @Before
    public void setUp() {
        
        validationResultWithoutErrors = new ValidationResult();
        
        validationResultWithErrors = new ValidationResult();
        validationResultWithErrors.addError("Missing title element", "missing.title");
        validationResultWithErrors.addError("Missing meta element", "missing.element");
    }
    
    /**
     * @verifies create a new validation result initializing errors
     * @see ValidationResult#ValidationResult()
     */
    @Test
    public void ValidationResult_shouldCreateANewValidationResultInitializingErrors() throws Exception {
        
        ValidationResult validationResult = new ValidationResult();
        assertThat(validationResult.getErrors(), is(empty()));
    }
    
    /**
     * @verifies add new error with given parameters
     * @see ValidationResult#addError(String, String)
     */
    @Test
    public void addError_shouldAddNewErrorWithGivenParameters() throws Exception {
        
        validationResultWithoutErrors.addError("Missing dublin core elements", "missing.dublincore");
        assertThat(validationResultWithoutErrors.getErrors()
                .size(),
            is(1));
        assertThat(validationResultWithoutErrors.getErrors()
                .get(0)
                .getDescription(),
            is("Missing dublin core elements"));
        assertThat(validationResultWithoutErrors.getErrors()
                .get(0)
                .getMessageCode(),
            is("missing.dublincore"));
    }
    
    /**
     * @verifies add given validation error to errors
     * @see ValidationResult#addError(ValidationError)
     */
    @Test
    public void addError_shouldAddGivenValidationErrorToErrors() throws Exception {
        
        ValidationError validationError = new ValidationError("Missing dublin core elements", "missing.dublincore");
        validationResultWithoutErrors.addError(validationError);
        assertThat(validationResultWithoutErrors.getErrors()
                .size(),
            is(1));
        assertThat(validationResultWithoutErrors.getErrors()
                .get(0),
            is(validationError));
    }
    
    /**
     * @verifies throw a validation exception if validation has errors
     * @see ValidationResult#assertOk()
     */
    @Test
    public void assertOk_shouldThrowAValidationExceptionIfValidationHasErrors() throws Exception {
        
        expectedException.expect(MrrtReportTemplateValidationException.class);
        validationResultWithErrors.assertOk();
    }
    
    /**
     * @verifies not throw a validation exception if validation has errors
     * @see ValidationResult#assertOk()
     */
    @Test
    public void assertOk_shouldNotThrowAValidationExceptionIfValidationHasErrors() throws Exception {
        
        validationResultWithoutErrors.assertOk();
    }
    
    /**
     * @verifies return true if validation has errors
     * @see ValidationResult#hasErrors()
     */
    @Test
    public void hasErrors_shouldReturnTrueIfValidationHasErrors() throws Exception {
        
        assertTrue(validationResultWithErrors.hasErrors());
    }
    
    /**
     * @verifies return false if validation has no errors
     * @see ValidationResult#hasErrors()
     */
    @Test
    public void hasErrors_shouldReturnFalseIfValidationHasNoErrors() throws Exception {
        
        assertFalse(validationResultWithoutErrors.hasErrors());
    }
    
    /**
     * @verifies return ok if validation has no errors
     * @see ValidationResult#toString()
     */
    @Test
    public void toString_shouldReturnOkIfValidationHasNoErrors() throws Exception {
        
        assertThat(validationResultWithoutErrors.toString(), is("OK"));
    }
    
    /**
     * @verifies return error strings if validation has errors
     * @see ValidationResult#toString()
     */
    @Test
    public void toString_shouldReturnErrorStringsIfValidationHasErrors() throws Exception {
        
        assertThat(validationResultWithErrors.toString(), startsWith("Validation failed due to:"));
        assertThat(validationResultWithErrors.toString(), containsString("Missing title element"));
        assertThat(validationResultWithErrors.toString(), containsString("Missing meta element"));
    }
}
