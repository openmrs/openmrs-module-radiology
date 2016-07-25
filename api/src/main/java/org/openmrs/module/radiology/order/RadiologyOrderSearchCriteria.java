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

import org.openmrs.Patient;

/**
 * The search parameter object for {@code RadiologyOrder's}.
 */
public class RadiologyOrderSearchCriteria {
    
    
    private final Patient patient;
    
    /**
     * @return the order patient
     */
    public Patient getPatient() {
        
        return patient;
    }
    
    public static class Builder {
        
        
        private Patient patient;
        
        /**
         * @param patient the order patient
         * @return this builder instance
         */
        public Builder withPatient(Patient patient) {
            
            this.patient = patient;
            return this;
        }
        
        /**
         * Create an {@link RadiologyOrderSearchCriteria} with the properties of this builder instance.
         * 
         * @return a new search criteria instance
         * @should create a new radiology order search criteria instance with patient if patient is set
         */
        public RadiologyOrderSearchCriteria build() {
            
            return new RadiologyOrderSearchCriteria(this);
        }
    }
    
    private RadiologyOrderSearchCriteria(Builder builder) {
        
        this.patient = builder.patient;
    }
}
