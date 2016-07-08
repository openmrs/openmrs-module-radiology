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
    
    public static class Builder {
        
        
        private Date fromDate;
        
        private Date toDate;
        
        private Provider principalResultsInterpreter;
        
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
         * Create an {@link RadiologyReportSearchCriteria} with the properties of this builder instance.
         * 
         * @return a new search criteria instance
         */
        public RadiologyReportSearchCriteria build() {
            return new RadiologyReportSearchCriteria(this);
        }
    }
    
    private RadiologyReportSearchCriteria(Builder builder) {
        
        this.fromDate = builder.fromDate;
        this.toDate = builder.toDate;
        this.principalResultsInterpreter = builder.principalResultsInterpreter;
    }
}
