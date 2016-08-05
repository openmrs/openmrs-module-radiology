/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.web;

/**
 * Used as {@code ModelAttribute} when voiding a {@code RadiologyReport}.
 */
final class VoidRadiologyReportRequest {
    
    
    /**
     * Reason for voiding the {@code RadiologyReport}.
     */
    String voidReason;
    
    /**
     * Create a new instance of {@code VoidRadiologyReportRequest}.
     */
    protected VoidRadiologyReportRequest() {
        // shall only be used within this package
    }
    
    public String getVoidReason() {
        return voidReason;
    }
    
    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }
}
