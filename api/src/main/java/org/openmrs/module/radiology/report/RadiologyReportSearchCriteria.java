package org.openmrs.module.radiology.report;

import java.util.Date;

/**
 * The search parameter object for radiology reports. A convenience interface for building
 * instances is provided by {@link RadiologyReportSearchCriteriaBuilder}.
 *
 * @see RadiologyReportSearchCriteriaBuilder
 */
public class RadiologyReportSearchCriteria {
    
    
    private final Date fromDate;
    
    private final Date toDate;
    
    private RadiologyReportSearchCriteria(Builder builder) {
        
        this.fromDate = builder.fromDate;
        this.toDate = builder.toDate;
    }
    
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
    
    public static class Builder {
        
        
        private Date fromDate;
        
        private Date toDate;
        
        /**
         * @param fromDate the minimum date (inclusive) the report date
         * @return this builder instance
         */
        public Builder setFromDate(Date fromDate) {
            
            this.fromDate = fromDate;
            return this;
        }
        
        /**
         * @param toDate the maximum date (exclusive) the report date
         * @return this builder instance
         */
        public Builder setToDate(Date toDate) {
            
            this.toDate = toDate;
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
    
}
