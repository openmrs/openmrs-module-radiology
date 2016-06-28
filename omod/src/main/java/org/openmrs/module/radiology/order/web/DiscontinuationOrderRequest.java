package org.openmrs.module.radiology.order.web;

import javax.validation.constraints.NotNull;

import org.openmrs.Provider;

/**
 * Used as {@code ModelAttribute} when discontinuing {@ Order's}.
 */
final class DiscontinuationOrderRequest {
    
    
    /**
     * Provider ordering discontinuation of an {@code Order}.
     */
    @NotNull
    Provider orderer;
    
    /**
     * Non coded reason why an {@code Order} should be discontinued.
     */
    @NotNull
    String reasonNonCoded;
    
    public Provider getOrderer() {
        return this.orderer;
    }
    
    public void setOrderer(Provider orderer) {
        this.orderer = orderer;
    }
    
    public String getReasonNonCoded() {
        return this.reasonNonCoded;
    }
    
    public void setReasonNonCoded(String reasonNonCoded) {
        this.reasonNonCoded = reasonNonCoded;
    }
    
    /**
     * Create a {@code DiscontinuationOrderRequest}.
     */
    protected DiscontinuationOrderRequest() {
        // shall only be used within this package
    }
}
