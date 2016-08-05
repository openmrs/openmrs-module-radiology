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

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * Tests {@link VoidRadiologyReportRequest}.
 */
public class VoidRadiologyReportRequestValidatorTest {
    
    
    VoidRadiologyReportRequestValidator voidRadiologyReportRequestValidator;
    
    VoidRadiologyReportRequest voidRadiologyReportRequest;
    
    @Before
    public void setUp() {
        
        voidRadiologyReportRequestValidator = new VoidRadiologyReportRequestValidator();
        
        voidRadiologyReportRequest = new VoidRadiologyReportRequest();
        voidRadiologyReportRequest.setVoidReason("wrong order selected");
    }
    
    /**
     * @see VoidRadiologyReportRequestValidator#supports(Class)
     * @verifies return true only for void radiology report request objects
     */
    @Test
    public void supports_shouldReturnTrueOnlyForVoidRadiologyReportRequestObjects() throws Exception {
        
        assertTrue(voidRadiologyReportRequestValidator.supports(VoidRadiologyReportRequest.class));
    }
    
    /**
     * @see VoidRadiologyReportRequestValidator#supports(Class)
     * @verifies return false for other object types
     */
    @Test
    public void supports_shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        assertFalse(voidRadiologyReportRequestValidator.supports(Object.class));
    }
    
    /**
     * @see VoidRadiologyReportRequestValidator#validate(Object, Errors)
     * @verifies fail validation if void radiology report request is null
     */
    @Test
    public void validate_shouldFailValidationIfDiscontinuationOrderRequestIsNull() throws Exception {
        
        Errors errors = new BindException(voidRadiologyReportRequest, "voidRadiologyReportRequest");
        voidRadiologyReportRequestValidator.validate(null, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.general"));
    }
    
    /**
     * @see VoidRadiologyReportRequestValidator#validate(Object, Errors)
     * @verifies fail validation if void reason is null or empty or whitespaces only
     */
    @Test
    public void validate_shouldFailValidationIfVoidReasonNonCodedIsNull() throws Exception {
        
        voidRadiologyReportRequest.setVoidReason(null);
        
        Errors errors = new BindException(voidRadiologyReportRequest, "voidRadiologyReportRequest");
        voidRadiologyReportRequestValidator.validate(voidRadiologyReportRequest, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertTrue(errors.hasFieldErrors("voidReason"));
        
        voidRadiologyReportRequest.setVoidReason("");
        
        errors = new BindException(voidRadiologyReportRequest, "voidRadiologyReportRequest");
        voidRadiologyReportRequestValidator.validate(voidRadiologyReportRequest, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertTrue(errors.hasFieldErrors("voidReason"));
        
        voidRadiologyReportRequest.setVoidReason("   ");
        
        errors = new BindException(voidRadiologyReportRequest, "voidRadiologyReportRequest");
        voidRadiologyReportRequestValidator.validate(voidRadiologyReportRequest, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertTrue(errors.hasFieldErrors("voidReason"));
    }
    
    /**
     * @see VoidRadiologyReportRequestValidator#validate(Object, Errors)
     * @verifies pass validation if all fields are correct
     */
    @Test
    public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        Errors errors = new BindException(voidRadiologyReportRequest, "voidRadiologyReportRequest");
        voidRadiologyReportRequestValidator.validate(voidRadiologyReportRequest, errors);
        
        assertFalse(errors.hasErrors());
    }
}
