/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.order.OrderUtilTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

/**
 * Tests methods on the {@link RadiologyOrderValidator} class.
 */
public class RadiologyOrderValidatorComponentTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if radiologyOrder is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfRadiologyOrderIsNull() throws Exception {
		
		Errors errors = new BindException(new RadiologyOrder(), "radiologyOrder");
		new RadiologyOrderValidator().validate(null, errors);
		
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.general", ((List<ObjectError>) errors.getAllErrors()).get(0)
				.getCode());
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if voided is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfVoidedIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setVoided(null);
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(88));
		radiologyOrder.setPatient(Context.getPatientService()
				.getPatient(2));
		radiologyOrder.setOrderer(Context.getProviderService()
				.getProvider(1));
		
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("discontinued"));
		Assert.assertTrue(errors.hasFieldErrors("voided"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("patient"));
		Assert.assertFalse(errors.hasFieldErrors("orderer"));
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if concept is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfConceptIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setPatient(Context.getPatientService()
				.getPatient(2));
		radiologyOrder.setOrderer(Context.getProviderService()
				.getProvider(1));
		
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("discontinued"));
		Assert.assertTrue(errors.hasFieldErrors("concept"));
		Assert.assertFalse(errors.hasFieldErrors("patient"));
		Assert.assertFalse(errors.hasFieldErrors("orderer"));
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if patient is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfPatientIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(88));
		radiologyOrder.setOrderer(Context.getProviderService()
				.getProvider(1));
		
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("discontinued"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertTrue(errors.hasFieldErrors("patient"));
		Assert.assertFalse(errors.hasFieldErrors("orderer"));
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if orderer is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfOrdererIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(88));
		radiologyOrder.setPatient(Context.getPatientService()
				.getPatient(2));
		
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		
		Assert.assertFalse(errors.hasFieldErrors("discontinued"));
		Assert.assertFalse(errors.hasFieldErrors("concept"));
		Assert.assertTrue(errors.hasFieldErrors("orderer"));
		Assert.assertFalse(errors.hasFieldErrors("patient"));
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if urgency is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfUrgencyIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(88));
		radiologyOrder.setPatient(Context.getPatientService()
				.getPatient(2));
		radiologyOrder.setUrgency(null);
		
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("urgency"));
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if action is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfActionIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(88));
		radiologyOrder.setPatient(Context.getPatientService()
				.getPatient(2));
		radiologyOrder.setAction(null);
		
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("action"));
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if dateActivated after dateStopped", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfDateActivatedAfterDateStopped() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(88));
		radiologyOrder.setPatient(Context.getPatientService()
				.getPatient(2));
		radiologyOrder.setOrderer(Context.getProviderService()
				.getProvider(1));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		radiologyOrder.setDateActivated(new Date());
		OrderUtilTest.setDateStopped(radiologyOrder, cal.getTime());
		
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("dateActivated"));
		Assert.assertTrue(errors.hasFieldErrors("dateStopped"));
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if dateActivated after autoExpireDate", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfDateActivatedAfterAutoExpireDate() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(88));
		radiologyOrder.setPatient(Context.getPatientService()
				.getPatient(2));
		radiologyOrder.setOrderer(Context.getProviderService()
				.getProvider(1));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		radiologyOrder.setDateActivated(new Date());
		radiologyOrder.setAutoExpireDate(cal.getTime());
		
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("dateActivated"));
		Assert.assertTrue(errors.hasFieldErrors("autoExpireDate"));
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if scheduledDate is null when urgency is ON_SCHEDULED_DATE", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfScheduledDateIsNullWhenUrgencyIsON_SCHEDULED_DATE() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(88));
		radiologyOrder.setPatient(Context.getPatientService()
				.getPatient(2));
		radiologyOrder.setOrderer(Context.getProviderService()
				.getProvider(1));
		
		radiologyOrder.setUrgency(RadiologyOrder.Urgency.ON_SCHEDULED_DATE);
		radiologyOrder.setScheduledDate(null);
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		Assert.assertTrue(errors.hasFieldErrors("scheduledDate"));
		
		radiologyOrder.setScheduledDate(new Date());
		radiologyOrder.setUrgency(RadiologyOrder.Urgency.STAT);
		errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		Assert.assertFalse(errors.hasFieldErrors("scheduledDate"));
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if scheduledDate is set and urgency is not set as ON_SCHEDULED_DATE", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfScheduledDateIsSetAndUrgencyIsNotSetAsON_SCHEDULED_DATE() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(88));
		radiologyOrder.setPatient(Context.getPatientService()
				.getPatient(2));
		radiologyOrder.setOrderer(Context.getProviderService()
				.getProvider(1));
		
		radiologyOrder.setScheduledDate(new Date());
		radiologyOrder.setUrgency(RadiologyOrder.Urgency.ROUTINE);
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		Assert.assertTrue(errors.hasFieldErrors("urgency"));
		
		radiologyOrder.setScheduledDate(new Date());
		radiologyOrder.setUrgency(RadiologyOrder.Urgency.ON_SCHEDULED_DATE);
		errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		Assert.assertFalse(errors.hasFieldErrors("urgency"));
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object, Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(88));
		radiologyOrder.setOrderer(Context.getProviderService()
				.getProvider(1));
		Patient patient = Context.getPatientService()
				.getPatient(2);
		radiologyOrder.setPatient(patient);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		radiologyOrder.setDateActivated(cal.getTime());
		radiologyOrder.setAutoExpireDate(new Date());
		radiologyOrder.setUrgency(RadiologyOrder.Urgency.ROUTINE);
		radiologyOrder.setAction(RadiologyOrder.Action.NEW);
		
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
	
	/**
	 * @see {@link RadiologyOrderValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should not allow a future dateActivated", method = "validate(Object, Errors)")
	public void validate_shouldNotAllowAFutureDateActivated() throws Exception {
		
		Patient patient = Context.getPatientService()
				.getPatient(7);
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setPatient(patient);
		radiologyOrder.setConcept(Context.getConceptService()
				.getConcept(5497));
		radiologyOrder.setOrderer(Context.getProviderService()
				.getProvider(1));
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		radiologyOrder.setDateActivated(cal.getTime());
		
		Errors errors = new BindException(radiologyOrder, "radiologyOrder");
		new RadiologyOrderValidator().validate(radiologyOrder, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("dateActivated"));
		Assert.assertEquals("Order.error.dateActivatedInFuture", errors.getFieldError("dateActivated")
				.getCode());
	}
}
