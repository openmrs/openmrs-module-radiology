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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

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
    
    @Test
    public void shouldReturnTrueOnlyForVoidRadiologyReportRequestObjects() throws Exception {
        
        assertTrue(voidRadiologyReportRequestValidator.supports(VoidRadiologyReportRequest.class));
    }
    
    @Test
    public void shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        assertFalse(voidRadiologyReportRequestValidator.supports(Object.class));
    }
    
    @Test
    public void shouldFailValidationIfDiscontinuationOrderRequestIsNull() throws Exception {
        
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
    
    @Test
    public void shouldFailValidationIfVoidReasonNonCodedIsNull() throws Exception {
        
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
    
    @Test
    public void shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        Errors errors = new BindException(voidRadiologyReportRequest, "voidRadiologyReportRequest");
        voidRadiologyReportRequestValidator.validate(voidRadiologyReportRequest, errors);
        
        assertFalse(errors.hasErrors());
    }
}
