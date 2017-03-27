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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.radiology.test.ValidatorAssertions.assertSingleErrorInField;
import static org.openmrs.module.radiology.test.ValidatorAssertions.assertSingleGeneralError;
import static org.openmrs.module.radiology.test.ValidatorAssertions.assertSingleNullErrorInField;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.order.OrderUtilTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests {@link RadiologyOrderValidator}.
 */
public class RadiologyOrderValidatorTest {
    
    
    private RadiologyOrderValidator radiologyOrderValidator;
    
    private RadiologyOrder radiologyOrder;
    
    private Errors errors;
    
    @Before
    public void setUp() {
        radiologyOrderValidator = new RadiologyOrderValidator();
        
        radiologyOrder = getValidRadiologyOrder();
        
        errors = new BindException(radiologyOrder, "radiologyOrder");
    }
    
    /**
     * Creates a valid RadiologyOrder.
     */
    private RadiologyOrder getValidRadiologyOrder() {
        
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
    
    @Test
    public void shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        assertFalse(radiologyOrderValidator.supports(Object.class));
    }
    
    @Test
    public void shouldReturnTrueForRadiologyOrderObjects() throws Exception {
        
        assertTrue(radiologyOrderValidator.supports(RadiologyOrder.class));
    }
    
    @Test
    public void shouldFailValidationIfActionIsNull() throws Exception {
        
        radiologyOrder.setAction(null);
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertSingleNullErrorInField(errors, "action");
    }
    
    @Test
    public void shouldFailValidationIfConceptIsNull() throws Exception {
        
        radiologyOrder.setConcept(null);
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertSingleErrorInField(errors, "concept", "Concept.noConceptSelected");
    }
    
    @Test
    public void shouldFailValidationIfDateActivatedAfterAutoExpireDate() throws Exception {
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        radiologyOrder.setDateActivated(new Date());
        radiologyOrder.setAutoExpireDate(cal.getTime());
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertTrue(errors.hasFieldErrors("dateActivated"));
        assertTrue(errors.hasFieldErrors("autoExpireDate"));
    }
    
    @Test
    public void shouldFailValidationIfDateActivatedAfterDateStopped() throws Exception {
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        radiologyOrder.setDateActivated(new Date());
        OrderUtilTest.setDateStopped(radiologyOrder, cal.getTime());
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertTrue(errors.hasFieldErrors("dateActivated"));
        assertTrue(errors.hasFieldErrors("dateStopped"));
    }
    
    @Test
    public void shouldFailValidationIfOrdererIsNull() throws Exception {
        
        radiologyOrder.setOrderer(null);
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertSingleNullErrorInField(errors, "orderer");
    }
    
    @Test
    public void shouldFailValidationIfPatientIsNull() throws Exception {
        
        radiologyOrder.setPatient(null);
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertSingleNullErrorInField(errors, "patient");
    }
    
    @Test
    public void shouldFailValidationIfRadiologyOrderIsNull() throws Exception {
        
        radiologyOrderValidator.validate(null, errors);
        
        assertSingleGeneralError(errors);
    }
    
    @Test
    public void shouldFailValidationIfScheduledDateIsNullWhenUrgencyIsOnScheduledDate() throws Exception {
        
        radiologyOrder.setScheduledDate(null);
        radiologyOrder.setUrgency(RadiologyOrder.Urgency.ON_SCHEDULED_DATE);
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertSingleErrorInField(errors, "scheduledDate", "Order.error.scheduledDateNullForOnScheduledDateUrgency");
    }
    
    @Test
    public void shouldFailValidationIfScheduledDateIsSetAndUrgencyIsStat() throws Exception {
        
        radiologyOrder.setScheduledDate(new Date());
        radiologyOrder.setUrgency(RadiologyOrder.Urgency.STAT);
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertSingleErrorInField(errors, "urgency", "Order.error.urgencyNotOnScheduledDate");
    }
    
    @Test
    public void shouldFailValidationIfScheduledDateIsSetAndUrgencyIsRoutine() throws Exception {
        
        radiologyOrder.setScheduledDate(new Date());
        radiologyOrder.setUrgency(RadiologyOrder.Urgency.ROUTINE);
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertSingleErrorInField(errors, "urgency", "Order.error.urgencyNotOnScheduledDate");
    }
    
    @Test
    public void shouldNotFailValidationIfScheduledDateIsSetAndUrgencyIsOnScheduledDate() throws Exception {
        
        radiologyOrder.setScheduledDate(new Date());
        radiologyOrder.setUrgency(RadiologyOrder.Urgency.ON_SCHEDULED_DATE);
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertFalse(errors.hasErrors());
        assertFalse(errors.hasFieldErrors("urgency"));
    }
    
    @Test
    public void shouldFailValidationIfUrgencyIsNull() throws Exception {
        
        radiologyOrder.setUrgency(null);
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertSingleNullErrorInField(errors, "urgency");
    }
    
    @Test
    public void shouldFailValidationIfVoidedIsNull() throws Exception {
        
        radiologyOrder.setVoided(null);
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertSingleNullErrorInField(errors, "voided");
    }
    
    @Test
    public void shouldNotAllowAFutureDateActivated() throws Exception {
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        radiologyOrder.setDateActivated(cal.getTime());
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertSingleErrorInField(errors, "dateActivated", "Order.error.dateActivatedInFuture");
    }
    
    @Test
    public void shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        radiologyOrderValidator.validate(radiologyOrder, errors);
        
        assertFalse(errors.hasErrors());
    }
}
