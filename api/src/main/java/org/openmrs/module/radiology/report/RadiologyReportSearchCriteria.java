/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report;

import java.util.Date;

import org.openmrs.Provider;

/**
 * Search parameter object for {@link RadiologyReport}'s.
 *
 * <p>Typical usage involves:
 * <ol>
 * <li>Set the various search criteria parameters through the respective methods of the static builder class
 * ({@link Builder#fromDate(Date)}, {@link Builder#toDate(Date)}, {@link Builder#withPrincipalResultsInterpreter(Provider)}, 
 * {@link Builder#includeVoided()} and {@link Builder#withStatus(RadiologyReportStatus)}).</li>
 * <li>Build the {@link RadiolologyReportSearchCriteria} instance with the {@link Builder#build()} method.</li>
 * <li>Get the search parameters through the getter methods (such as {@link #getFromDate()} or {@link #getStatus()}).</li>
 * </ol>
 */
public class RadiologyReportSearchCriteria {
    
    
    private final Date fromDate;
    
    private final Date toDate;
    
    private final Provider principalResultsInterpreter;
    
    private final Boolean includeVoided;
    
    private final RadiologyReportStatus status;
    
    /**
     * @return the minimum date (inclusive) the report date
     */
    public Date getFromDate() {
        
        return fromDate;
    }
    
    /**
     * @return the maximum date (inclusive) the report date
     */
    public Date getToDate() {
        
        return toDate;
    }
    
    /**
     * @return the principle result interpreter of the report
     */
    public Provider getPrincipalResultsInterpreter() {
        
        return principalResultsInterpreter;
    }
    
    /**
     * @return the {@code Boolean} specifying whether or not to include voided radiology reports
     */
    public Boolean getIncludeVoided() {
        
        return includeVoided;
    }
    
    /**
     * @return the status of the report
     */
    public RadiologyReportStatus getStatus() {
        
        return status;
    }
    
    public static class Builder {
        
        
        private Date fromDate;
        
        private Date toDate;
        
        private Provider principalResultsInterpreter;
        
        private Boolean inludeVoided = false;
        
        private RadiologyReportStatus status;
        
        /**
         * @param fromDate the minimum date (inclusive) the report date
         * @return this builder instance
         */
        public Builder fromDate(Date fromDate) {
            
            this.fromDate = fromDate;
            return this;
        }
        
        /**
         * @param toDate the maximum date (inclusive) the report date
         * @return this builder instance
         */
        public Builder toDate(Date toDate) {
            
            this.toDate = toDate;
            return this;
        }
        
        /**
         * @param principalResultsInterpreter the principal results interpreter of the report
         * @return this builder instance
         */
        public Builder withPrincipalResultsInterpreter(Provider principalResultsInterpreter) {
            
            this.principalResultsInterpreter = principalResultsInterpreter;
            return this;
        }
        
        /**
         * Includes voided radiology reports.
         * 
         * @return this builder instance
         */
        public Builder includeVoided() {
            
            this.inludeVoided = true;
            return this;
        }
        
        /**
         * Sets the criteria's report status.
         * 
         * @param status the status of the report
         * @return this builder instance
         */
        public Builder withStatus(RadiologyReportStatus status) {
            
            this.status = status;
            return this;
        }
        
        /**
         * Create an {@link RadiologyReportSearchCriteria} with the properties of this builder instance.
         * 
         * @return a new search criteria instance
         * @should create a new radiology report search criteria instance with from and to date specified if date from and date to are set
         * @should create a new radiology report search criteria instance with principal results interpreter specified if principal results interpreter is set
         * @should create a new radiology report search criteria instance with include voided set to true if voided reports should be included
         * @should create a new radiology report search criteria instance with report status specified if status is set to claimed or completed
         */
        public RadiologyReportSearchCriteria build() {
            
            return new RadiologyReportSearchCriteria(this);
        }
    }
    
    private RadiologyReportSearchCriteria(Builder builder) {
        
        this.fromDate = builder.fromDate;
        this.toDate = builder.toDate;
        this.principalResultsInterpreter = builder.principalResultsInterpreter;
        this.includeVoided = builder.inludeVoided;
        this.status = builder.status;
    }
}
