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

import org.openmrs.Order.Urgency;
import org.openmrs.Patient;
import org.openmrs.Provider;

/**
 * The search parameter object for {@code RadiologyOrder's}.
 */
public class RadiologyOrderSearchCriteria {
    
    
    private final Patient patient;
    
    private final Boolean includeVoided;
    
    private final Urgency urgency;
    
    private final Date fromEffectiveStartDate;
    
    private final Date toEffectiveStartDate;
    
    private final String accessionNumber;
    
    private final Provider orderer;
    
    /**
     * @return the order patient
     */
    public Patient getPatient() {
        
        return patient;
    }
    
    /**
     * @return the {@code Boolean} specifying whether or not to include voided radiology orders
     */
    public Boolean getIncludeVoided() {
        
        return includeVoided;
    }
    
    /**
     * @return the order urgency
     */
    public Urgency getUrgency() {
        
        return urgency;
    }
    
    /**
     * @return the minimum effective start date
     */
    public Date getFromEffectiveStartDate() {
        
        return fromEffectiveStartDate;
    }
    
    /**
     * @return the maximum effective start date
     */
    public Date getToEffectiveStartDate() {
        
        return toEffectiveStartDate;
    }
    
    /**
     * @return the order accessionNumber
     */
    public String getAccessionNumber() {
        
        return accessionNumber;
    }
    
    /**
     * @return the order orderer
     */
    public Provider getOrderer() {
        
        return orderer;
    }
    
    public static class Builder {
        
        
        private Patient patient;
        
        private Boolean includeVoided = false;
        
        private Urgency urgency;
        
        private Date fromEffectiveStartDate;
        
        private Date toEffectiveStartDate;
        
        private String accessionNumber;
        
        private Provider orderer;
        
        /**
         * @param patient the order patient
         * @return this builder instance
         */
        public Builder withPatient(Patient patient) {
            
            this.patient = patient;
            return this;
        }
        
        /**
         * Includes voided radiology orders.
         * 
         * @return this builder instance
         */
        public Builder includeVoided() {
            
            this.includeVoided = true;
            return this;
        }
        
        /**
         * @param urgency the order urgency
         * @return this builder instance
         */
        public Builder withUrgency(Urgency urgency) {
            
            this.urgency = urgency;
            return this;
        }
        
        /**
         * @return the minimum effective start date
         * @return this builder instance
         */
        public Builder withFromEffectiveStartDate(Date fromEffectiveStartDate) {
            
            this.fromEffectiveStartDate = fromEffectiveStartDate;
            return this;
        }
        
        /**
         * @return the maximum effective start date
         * @return this builder instance
         */
        public Builder withToEffectiveStartDate(Date toEffectiveStartDate) {
            
            this.toEffectiveStartDate = toEffectiveStartDate;
            return this;
        }
        
        /**
         * @param accessionNumber the order accession number
         * @return this builder instance
         */
        public Builder withAccessionNumber(String accessionNumber) {
            
            this.accessionNumber = accessionNumber;
            return this;
        }
        
        /**
         * @param urgency the order orderer
         * @return this builder instance
         */
        public Builder withOrderer(Provider orderer) {
            
            this.orderer = orderer;
            return this;
        }
        
        /**
         * Create an {@link RadiologyOrderSearchCriteria} with the properties of this builder instance.
         * 
         * @return a new search criteria instance
         * @should create a new radiology order search criteria instance with patient if patient is set
         * @should create a new radiology order search criteria instance with include voided set to true if voided orders should be included
         * @should create a new radiology order search criteria instance with urgency if urgency is set
         * @should create a new radiology order search criteria instance with from effective start date if from effective start date is set
         * @should create a new radiology order search criteria instance with to effective start date if to effective start date is set
         * @should create a new radiology order search criteria instance with accession number if accession number is set
         * @should create a new radiology order search criteria instance with orderer if orderer is set
         */
        public RadiologyOrderSearchCriteria build() {
            
            return new RadiologyOrderSearchCriteria(this);
        }
    }
    
    private RadiologyOrderSearchCriteria(Builder builder) {
        
        this.patient = builder.patient;
        this.includeVoided = builder.includeVoided;
        this.urgency = builder.urgency;
        this.fromEffectiveStartDate = builder.fromEffectiveStartDate;
        this.toEffectiveStartDate = builder.toEffectiveStartDate;
        this.accessionNumber = builder.accessionNumber;
        this.orderer = builder.orderer;
    }
}
