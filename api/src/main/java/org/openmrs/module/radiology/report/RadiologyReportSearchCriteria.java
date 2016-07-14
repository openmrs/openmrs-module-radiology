package org.openmrs.module.radiology.report;

import java.util.Date;

import org.openmrs.Provider;

/**
 * The search parameter object for {@code RadiologyReport's}.
 */
public class RadiologyReportSearchCriteria {
    
    
    private final Date fromDate;
    
    private final Date toDate;
    
    private final Provider principalResultsInterpreter;
    
    private final Boolean includeDiscontinued;
    
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
     * @return the {@code Boolean} specifying whether or not to include discontinued radiology reports
     */
    public Boolean getIncludeDiscontinued() {
        
        return includeDiscontinued;
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
        
        private Boolean includeDiscontinued = false;
        
        private RadiologyReportStatus status;
        
        /**
         * @param fromDate the minimum date (inclusive) the report date
         * @return this builder instance
         */
        public Builder withFromDate(Date fromDate) {
            
            this.fromDate = fromDate;
            return this;
        }
        
        /**
         * @param toDate the maximum date (inclusive) the report date
         * @return this builder instance
         */
        public Builder withToDate(Date toDate) {
            
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
         * Includes discontinued radiology reports.
         * 
         * @return this builder instance
         */
        public Builder includeDiscontinued() {
            
            this.includeDiscontinued = true;
            return this;
        }
        
        /**
         * Sets the criteria's report status and ensures {@code includeDiscontinued} is true if given status is discontinued.
         * 
         * @param status the status of the report
         * @return this builder instance
         */
        public Builder withStatus(RadiologyReportStatus status) {
            
            this.status = status;
            if (status == RadiologyReportStatus.DISCONTINUED) {
                this.includeDiscontinued = true;
            }
            return this;
        }
        
        /**
         * Create an {@link RadiologyReportSearchCriteria} with the properties of this builder instance.
         * 
         * @return a new search criteria instance
         * @should create a new radiology report search criteria instance with from and to date specified if date from and date to are set
         * @should create a new radiology report search criteria instance with principal results interpreter specified if principal results interpreter is set
         * @should create a new radiology report search criteria instance with include discontinued set to true if discontinued reports should be included
         * @should create a new radiology report search criteria instance with report status specified if status is set to claimed or completed
         * @should create a new radiology report search criteria instance with report status set to discontinued and include discontinued set to true if status is set to discontinued
         */
        public RadiologyReportSearchCriteria build() {
            
            return new RadiologyReportSearchCriteria(this);
        }
    }
    
    private RadiologyReportSearchCriteria(Builder builder) {
        
        this.fromDate = builder.fromDate;
        this.toDate = builder.toDate;
        this.principalResultsInterpreter = builder.principalResultsInterpreter;
        this.includeDiscontinued = builder.includeDiscontinued;
        this.status = builder.status;
    }
}
