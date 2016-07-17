/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order.web;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Provider;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests {@link DiscontinuationOrderRequestValidator}.
 */
public class DiscontinuationOrderRequestValidatorTest {
    
    
    DiscontinuationOrderRequestValidator discontinuationOrderRequestValidator;
    
    DiscontinuationOrderRequest discontinuationOrderRequest;
    
    @Before
    public void setUp() {
        
        discontinuationOrderRequestValidator = new DiscontinuationOrderRequestValidator();
        
        discontinuationOrderRequest = new DiscontinuationOrderRequest();
        discontinuationOrderRequest.setOrderer(new Provider());
        discontinuationOrderRequest.setReasonNonCoded("Wrong Procedure");
    }
    
    /**
     * @see DiscontinuationOrderRequestValidator#supports(Class)
     * @verifies return true only for discontinuation order request objects
     */
    @Test
    public void supports_shouldReturnTrueOnlyForDiscontinuationOrderRequestObjects() throws Exception {
        
        assertTrue(discontinuationOrderRequestValidator.supports(DiscontinuationOrderRequest.class));
    }
    
    /**
     * @see DiscontinuationOrderRequestValidator#supports(Class)
     * @verifies return false for other object types
     */
    @Test
    public void supports_shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        assertFalse(discontinuationOrderRequestValidator.supports(Object.class));
    }
    
    /**
     * @see DiscontinuationOrderRequestValidator#validate(Object, Errors)
     * @verifies fail validation if discontinuation order request is null
     */
    @Test
    public void validate_shouldFailValidationIfDiscontinuationOrderRequestIsNull() throws Exception {
        
        Errors errors = new BindException(discontinuationOrderRequest, "discontinuationOrderRequest");
        discontinuationOrderRequestValidator.validate(null, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.general"));
    }
    
    /**
     * @see DiscontinuationOrderRequestValidator#validate(Object, Errors)
     * @verifies fail validation if orderer is null or empty or whitespaces only
     */
    @Test
    public void validate_shouldFailValidationIfOrdererIsNull() throws Exception {
        
        discontinuationOrderRequest.setOrderer(null);
        
        Errors errors = new BindException(discontinuationOrderRequest, "discontinuationOrderRequest");
        discontinuationOrderRequestValidator.validate(discontinuationOrderRequest, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertTrue(errors.hasFieldErrors("orderer"));
        assertFalse(errors.hasFieldErrors("reasonNonCoded"));
    }
    
    /**
     * @see DiscontinuationOrderRequestValidator#validate(Object, Errors)
     * @verifies fail validation if reason non coded is null or empty or whitespaces only
     */
    @Test
    public void validate_shouldFailValidationIfOrderReasonNonCodedIsNull() throws Exception {
        
        discontinuationOrderRequest.setReasonNonCoded(null);
        
        Errors errors = new BindException(discontinuationOrderRequest, "discontinuationOrderRequest");
        discontinuationOrderRequestValidator.validate(discontinuationOrderRequest, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertFalse(errors.hasFieldErrors("orderer"));
        assertTrue(errors.hasFieldErrors("reasonNonCoded"));
        
        discontinuationOrderRequest.setReasonNonCoded("");
        
        errors = new BindException(discontinuationOrderRequest, "discontinuationOrderRequest");
        discontinuationOrderRequestValidator.validate(discontinuationOrderRequest, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertFalse(errors.hasFieldErrors("orderer"));
        assertTrue(errors.hasFieldErrors("reasonNonCoded"));
        
        discontinuationOrderRequest.setReasonNonCoded("   ");
        
        errors = new BindException(discontinuationOrderRequest, "discontinuationOrderRequest");
        discontinuationOrderRequestValidator.validate(discontinuationOrderRequest, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertFalse(errors.hasFieldErrors("orderer"));
        assertTrue(errors.hasFieldErrors("reasonNonCoded"));
    }
    
    /**
     * @see DiscontinuationOrderRequestValidator#validate(Object, Errors)
     * @verifies pass validation if all fields are correct
     */
    @Test
    public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        Errors errors = new BindException(discontinuationOrderRequest, "discontinuationOrderRequest");
        discontinuationOrderRequestValidator.validate(discontinuationOrderRequest, errors);
        
        assertFalse(errors.hasErrors());
    }
}
