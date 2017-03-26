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
    
    @Test
    public void shouldReturnTrueOnlyForDiscontinuationOrderRequestObjects() throws Exception {
        
        assertTrue(discontinuationOrderRequestValidator.supports(DiscontinuationOrderRequest.class));
    }
    
    @Test
    public void shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        assertFalse(discontinuationOrderRequestValidator.supports(Object.class));
    }
    
    @Test
    public void shouldFailValidationIfDiscontinuationOrderRequestIsNull() throws Exception {
        
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
    
    @Test
    public void shouldFailValidationIfOrdererIsNull() throws Exception {
        
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
    
    @Test
    public void shouldFailValidationIfOrderReasonNonCodedIsNull() throws Exception {
        
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
    
    @Test
    public void shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        Errors errors = new BindException(discontinuationOrderRequest, "discontinuationOrderRequest");
        discontinuationOrderRequestValidator.validate(discontinuationOrderRequest, errors);
        
        assertFalse(errors.hasErrors());
    }
}
