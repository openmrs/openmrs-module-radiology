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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.order.OrderUtilTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests methods on the {@link RadiologyOrderValidator} class.
 */
public class RadiologyOrderValidatorTest {
    
    
    /**
     * helper method to create a valid RadiologyOrder
     * 
     * @return [RadiologyOrder] radiologyOrder, passes validation
     */
    public RadiologyOrder getValidRadiologyOrder() {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        radiologyOrder.setOrderer(new Provider());
        radiologyOrder.setPatient(new Patient());
        radiologyOrder.setConcept(new Concept(88));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        radiologyOrder.setDateActivated(cal.getTime());
        radiologyOrder.setAutoExpireDate(new Date());
        radiologyOrder.setUrgency(RadiologyOrder.Urgency.ROUTINE);
        radiologyOrder.setAction(RadiologyOrder.Action.NEW);
        return radiologyOrder;
    }
    
    /**
     * @verifies return false for other object types
     * @see RadiologyOrderValidator#supports(Class)
     */
    @Test
    public void supports_shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        RadiologyOrderValidator radiologyReportValidator = new RadiologyOrderValidator();
        assertFalse(radiologyReportValidator.supports(Object.class));
    }
    
    /**
     * @verifies return true for RadiologyOrder objects
     * @see RadiologyOrderValidator#supports(Class)
     */
    @Test
    public void supports_shouldReturnTrueForRadiologyOrderObjects() throws Exception {
        
        RadiologyOrderValidator radiologyReportValidator = new RadiologyOrderValidator();
        assertTrue(radiologyReportValidator.supports(RadiologyOrder.class));
    }
    
    /**
     * @verifies fail validation if action is null
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfActionIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        radiologyOrder.setAction(null);
        
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        
        assertTrue(errors.hasFieldErrors("action"));
    }
    
    /**
     * @verifies fail validation if concept is null
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfConceptIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        radiologyOrder.setConcept(null);
        
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        
        assertFalse(errors.hasFieldErrors("discontinued"));
        assertTrue(errors.hasFieldErrors("concept"));
        assertFalse(errors.hasFieldErrors("patient"));
        assertFalse(errors.hasFieldErrors("orderer"));
    }
    
    /**
     * @verifies fail validation if dateActivated after autoExpireDate
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfDateActivatedAfterAutoExpireDate() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        radiologyOrder.setDateActivated(new Date());
        radiologyOrder.setAutoExpireDate(cal.getTime());
        
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        
        assertTrue(errors.hasFieldErrors("dateActivated"));
        assertTrue(errors.hasFieldErrors("autoExpireDate"));
    }
    
    /**
     * @verifies fail validation if dateActivated after dateStopped
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfDateActivatedAfterDateStopped() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        radiologyOrder.setDateActivated(new Date());
        OrderUtilTest.setDateStopped(radiologyOrder, cal.getTime());
        
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        
        assertTrue(errors.hasFieldErrors("dateActivated"));
        assertTrue(errors.hasFieldErrors("dateStopped"));
    }
    
    /**
     * @verifies fail validation if orderer is null
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfOrdererIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        radiologyOrder.setOrderer(null);
        
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        
        assertFalse(errors.hasFieldErrors("discontinued"));
        assertFalse(errors.hasFieldErrors("concept"));
        assertTrue(errors.hasFieldErrors("orderer"));
        assertFalse(errors.hasFieldErrors("patient"));
    }
    
    /**
     * @verifies fail validation if patient is null
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfPatientIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        radiologyOrder.setPatient(null);
        
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        
        assertFalse(errors.hasFieldErrors("discontinued"));
        assertFalse(errors.hasFieldErrors("concept"));
        assertTrue(errors.hasFieldErrors("patient"));
        assertFalse(errors.hasFieldErrors("orderer"));
    }
    
    /**
     * @verifies fail validation if radiologyOrder is null
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfRadiologyOrderIsNull() throws Exception {
        
        Errors errors = new BindException(new RadiologyOrder(), "radiologyOrder");
        new RadiologyOrderValidator().validate(null, errors);
        
        assertTrue(errors.hasErrors());
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.general"));
    }
    
    /**
     * @verifies fail validation if scheduledDate is null when urgency is ON_SCHEDULED_DATE
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfScheduledDateIsNullWhenUrgencyIsON_SCHEDULED_DATE() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        
        radiologyOrder.setUrgency(RadiologyOrder.Urgency.ON_SCHEDULED_DATE);
        radiologyOrder.setScheduledDate(null);
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        assertTrue(errors.hasFieldErrors("scheduledDate"));
        
        radiologyOrder.setScheduledDate(new Date());
        radiologyOrder.setUrgency(RadiologyOrder.Urgency.STAT);
        errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        assertFalse(errors.hasFieldErrors("scheduledDate"));
    }
    
    /**
     * @verifies fail validation if scheduledDate is set and urgency is not set as ON_SCHEDULED_DATE
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfScheduledDateIsSetAndUrgencyIsNotSetAsON_SCHEDULED_DATE() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        
        radiologyOrder.setScheduledDate(new Date());
        radiologyOrder.setUrgency(RadiologyOrder.Urgency.ROUTINE);
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        assertTrue(errors.hasFieldErrors("urgency"));
        
        radiologyOrder.setScheduledDate(new Date());
        radiologyOrder.setUrgency(RadiologyOrder.Urgency.ON_SCHEDULED_DATE);
        errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        assertFalse(errors.hasFieldErrors("urgency"));
    }
    
    /**
     * @verifies fail validation if urgency is null
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfUrgencyIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        radiologyOrder.setUrgency(null);
        
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        
        assertTrue(errors.hasFieldErrors("urgency"));
    }
    
    /**
     * @verifies fail validation if voided is null
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfVoidedIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        radiologyOrder.setVoided(null);
        
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        
        assertFalse(errors.hasFieldErrors("discontinued"));
        assertTrue(errors.hasFieldErrors("voided"));
        assertFalse(errors.hasFieldErrors("concept"));
        assertFalse(errors.hasFieldErrors("patient"));
        assertFalse(errors.hasFieldErrors("orderer"));
    }
    
    /**
     * @verifies not allow a future dateActivated
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldNotAllowAFutureDateActivated() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        radiologyOrder.setDateActivated(cal.getTime());
        
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        
        assertTrue(errors.hasFieldErrors("dateActivated"));
        assertThat(errors.getFieldError("dateActivated")
                .getCode(),
            is("Order.error.dateActivatedInFuture"));
    }
    
    /**
     * @verifies pass validation if all fields are correct
     * @see RadiologyOrderValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        RadiologyOrder radiologyOrder = getValidRadiologyOrder();
        
        Errors errors = new BindException(radiologyOrder, "radiologyOrder");
        new RadiologyOrderValidator().validate(radiologyOrder, errors);
        
        assertFalse(errors.hasErrors());
    }
}
