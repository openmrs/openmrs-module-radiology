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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.annotation.Handler;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the {@link RadiologyOrder} class.
 */
@Handler(supports = { RadiologyOrder.class })
@Component
public class RadiologyOrderValidator implements Validator {
    
    
    /** Log for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    /**
     * Determines if the command object being submitted is a valid type
     *
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     * @should return true for RadiologyOrder objects
     * @should return false for other object types
     */
    public boolean supports(Class clazz) {
        return RadiologyOrder.class.isAssignableFrom(clazz);
    }
    
    /**
     * Checks the form object for any inconsistencies/errors
     *
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     * @should fail validation if radiologyOrder is null
     * @should fail validation if voided is null
     * @should fail validation if concept is null
     * @should fail validation if patient is null
     * @should fail validation if orderer is null
     * @should fail validation if urgency is null
     * @should fail validation if action is null
     * @should fail validation if dateActivated after dateStopped
     * @should fail validation if dateActivated after autoExpireDate
     * @should fail validation if scheduledDate is set and urgency is not set as ON_SCHEDULED_DATE
     * @should fail validation if scheduledDate is null when urgency is ON_SCHEDULED_DATE
     * @should pass validation if all fields are correct
     * @should not allow a future dateActivated
     */
    public void validate(Object obj, Errors errors) {
        final RadiologyOrder radiologyOrder = (RadiologyOrder) obj;
        if (radiologyOrder == null) {
            errors.reject("error.general");
        } else {
            // for the following elements Order.hbm.xml says: not-null="true"
            ValidationUtils.rejectIfEmpty(errors, "voided", "error.null");
            ValidationUtils.rejectIfEmpty(errors, "concept", "Concept.noConceptSelected");
            ValidationUtils.rejectIfEmpty(errors, "patient", "error.null");
            ValidationUtils.rejectIfEmpty(errors, "orderer", "error.null");
            ValidationUtils.rejectIfEmpty(errors, "urgency", "error.null");
            ValidationUtils.rejectIfEmpty(errors, "action", "error.null");
            // Order.encounter and
            // Order.orderType
            // have not null constraint as well, but are set in RadiologyOrderService.saveRadiologyOrder
            validateDateActivated(radiologyOrder, errors);
            validateScheduledDate(radiologyOrder, errors);
        }
    }
    
    private void validateDateActivated(Order order, Errors errors) {
        final Date dateActivated = order.getDateActivated();
        if (dateActivated != null) {
            if (dateActivated.after(new Date())) {
                errors.rejectValue("dateActivated", "Order.error.dateActivatedInFuture");
                return;
            }
            final Date dateStopped = order.getDateStopped();
            if (dateStopped != null && dateActivated.after(dateStopped)) {
                errors.rejectValue("dateActivated", "Order.error.dateActivatedAfterDiscontinuedDate");
                errors.rejectValue("dateStopped", "Order.error.dateActivatedAfterDiscontinuedDate");
            }
            final Date autoExpireDate = order.getAutoExpireDate();
            if (autoExpireDate != null && dateActivated.after(autoExpireDate)) {
                errors.rejectValue("dateActivated", "Order.error.dateActivatedAfterAutoExpireDate");
                errors.rejectValue("autoExpireDate", "Order.error.dateActivatedAfterAutoExpireDate");
            }
            final Encounter encounter = order.getEncounter();
            if (encounter != null && encounter.getEncounterDatetime() != null && encounter.getEncounterDatetime()
                    .after(dateActivated)) {
                errors.rejectValue("dateActivated", "Order.error.dateActivatedAfterEncounterDatetime");
            }
        }
    }
    
    private void validateScheduledDate(Order order, Errors errors) {
        final boolean isUrgencyOnScheduledDate = order.getUrgency() != null && order.getUrgency()
                .equals(Order.Urgency.ON_SCHEDULED_DATE);
        if (order.getScheduledDate() != null && !isUrgencyOnScheduledDate) {
            errors.rejectValue("urgency", "Order.error.urgencyNotOnScheduledDate");
        }
        if (isUrgencyOnScheduledDate && order.getScheduledDate() == null) {
            errors.rejectValue("scheduledDate", "Order.error.scheduledDateNullForOnScheduledDateUrgency");
        }
    }
}
