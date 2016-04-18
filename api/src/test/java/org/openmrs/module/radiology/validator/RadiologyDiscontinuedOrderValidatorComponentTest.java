/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.validator;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.Order;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

/**
 * Tests methods on the {@link RadiologyDiscontinuedOrderValidator} class.
 */
public class RadiologyDiscontinuedOrderValidatorComponentTest {
	
	/**
	 * @see {@link RadiologyDiscontinuedOrderValidator#supports(Class c)}
	 */
	@Test
	@Verifies(value = "should return true for Order objects and subclasses", method = "supports(Class c)")
	public void supports_shouldReturnTrueForOrderObjectsAndSubclasses() throws Exception {
		RadiologyDiscontinuedOrderValidator radiologyDiscontinuedOrderValidator = new RadiologyDiscontinuedOrderValidator();
		// true for Orders
		Assert.assertTrue(radiologyDiscontinuedOrderValidator.supports(Order.class));
		// true for Subclass
		Assert.assertTrue(radiologyDiscontinuedOrderValidator.supports(RadiologyOrder.class));
	}
	
	/**
	 * @see {@link RadiologyDiscontinuedOrderValidator#supports(Class c)}
	 */
	@Test
	@Verifies(value = "should return false for other object types", method = "supports(Class c)")
	public void supports_shouldReturnFalseForOtherObjectTypes() throws Exception {
		RadiologyDiscontinuedOrderValidator radiologyDiscontinuedOrderValidator = new RadiologyDiscontinuedOrderValidator();
		Assert.assertFalse(radiologyDiscontinuedOrderValidator.supports(Object.class));
	}
	
	/**
	 * @see {@link RadiologyDiscontinuedOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if order is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfOrderIsNull() throws Exception {
		
		Errors errors = new BindException(new Order(), "order");
		new RadiologyDiscontinuedOrderValidator().validate(null, errors);
		
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.general", ((List<ObjectError>) errors.getAllErrors()).get(0)
				.getCode());
	}
	
	/**
	 * @see {@link RadiologyDiscontinuedOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if orderer is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfOrdererIsNull() throws Exception {
		
		Order order = new Order();
		order.setOrderer(null);
		order.setOrderReasonNonCoded("Wrong Procedure");
		
		Errors errors = new BindException(order, "order");
		new RadiologyDiscontinuedOrderValidator().validate(order, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("orderer"));
		Assert.assertFalse(errors.hasFieldErrors("orderReasonNonCoded"));
	}
	
	/**
	 * @see {@link RadiologyDiscontinuedOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if orderReasonNonCoded is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfOrderReasonNonCodedIsNull() throws Exception {
		
		Order order = new Order();
		
		order.setOrderer(new Provider());
		order.setOrderReasonNonCoded(null);
		
		Errors errors = new BindException(order, "order");
		new RadiologyDiscontinuedOrderValidator().validate(order, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("orderer"));
		Assert.assertTrue(errors.hasFieldErrors("orderReasonNonCoded"));
	}
	
	/**
	 * @see {@link RadiologyDiscontinuedOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object, Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		
		Order order = new Order();
		order.setOrderer(new Provider());
		order.setOrderReasonNonCoded("Wrong Procedure");
		
		Errors errors = new BindException(order, "order");
		new RadiologyDiscontinuedOrderValidator().validate(order, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
	
}
