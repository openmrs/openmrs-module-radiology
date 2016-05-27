/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.radiology.order;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.Provider;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests {@link RadiologyDiscontinuedOrderValidator}.
 */
public class RadiologyDiscontinuedOrderValidatorTest {
    
    
    /**
     * @verifies return false for other object types
     * @see RadiologyDiscontinuedOrderValidator#supports(Class)
     */
    @Test
    public void supports_shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        RadiologyDiscontinuedOrderValidator radiologyDiscontinuedOrderValidator = new RadiologyDiscontinuedOrderValidator();
        assertFalse(radiologyDiscontinuedOrderValidator.supports(Object.class));
    }
    
    /**
     * @verifies return true for Order objects and subclasses
     * @see RadiologyDiscontinuedOrderValidator#supports(Class)
     */
    @Test
    public void supports_shouldReturnTrueForOrderObjectsAndSubclasses() throws Exception {
        
        RadiologyDiscontinuedOrderValidator radiologyDiscontinuedOrderValidator = new RadiologyDiscontinuedOrderValidator();
        // true for Orders
        assertTrue(radiologyDiscontinuedOrderValidator.supports(Order.class));
        // true for Subclass
        assertTrue(radiologyDiscontinuedOrderValidator.supports(RadiologyOrder.class));
    }
    
    /**
     * @verifies fail validation if order is null
     * @see RadiologyDiscontinuedOrderValidator#validate(Object, org.springframework.validation.Errors)
     */
    @Test
    public void validate_shouldFailValidationIfOrderIsNull() throws Exception {
        
        Errors errors = new BindException(new Order(), "order");
        new RadiologyDiscontinuedOrderValidator().validate(null, errors);
        
        assertTrue(errors.hasErrors());
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.general"));
    }
    
    /**
     * @verifies fail validation if orderer is null
     * @see RadiologyDiscontinuedOrderValidator#validate(Object, org.springframework.validation.Errors)
     */
    @Test
    public void validate_shouldFailValidationIfOrdererIsNull() throws Exception {
        
        Order order = new Order();
        order.setOrderer(null);
        order.setOrderReasonNonCoded("Wrong Procedure");
        
        Errors errors = new BindException(order, "order");
        new RadiologyDiscontinuedOrderValidator().validate(order, errors);
        
        assertTrue(errors.hasFieldErrors("orderer"));
        assertFalse(errors.hasFieldErrors("orderReasonNonCoded"));
    }
    
    /**
     * @verifies fail validation if orderReasonNonCoded is null
     * @see RadiologyDiscontinuedOrderValidator#validate(Object, org.springframework.validation.Errors)
     */
    @Test
    public void validate_shouldFailValidationIfOrderReasonNonCodedIsNull() throws Exception {
        
        Order order = new Order();
        
        order.setOrderer(new Provider());
        order.setOrderReasonNonCoded(null);
        
        Errors errors = new BindException(order, "order");
        new RadiologyDiscontinuedOrderValidator().validate(order, errors);
        
        assertFalse(errors.hasFieldErrors("orderer"));
        assertTrue(errors.hasFieldErrors("orderReasonNonCoded"));
    }
    
    /**
     * @verifies pass validation if all fields are correct
     * @see RadiologyDiscontinuedOrderValidator#validate(Object, org.springframework.validation.Errors)
     */
    @Test
    public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        Order order = new Order();
        order.setOrderer(new Provider());
        order.setOrderReasonNonCoded("Wrong Procedure");
        
        Errors errors = new BindException(order, "order");
        new RadiologyDiscontinuedOrderValidator().validate(order, errors);
        
        assertFalse(errors.hasErrors());
    }
}
