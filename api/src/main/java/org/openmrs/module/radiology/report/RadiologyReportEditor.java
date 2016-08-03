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

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.springframework.util.StringUtils;

/**
 * Allows for serializing/deserializing a RadiologyReport object to a string so that Spring knows how to pass
 * a RadiologyReport back and forth through an html form or other medium
 * <br/>
 * 
 * @see RadiologyReport
 */
public class RadiologyReportEditor extends PropertyEditorSupport {
    
    
    private final Log log = LogFactory.getLog(this.getClass());
    
    /**
     * @should set value to radiology report whos id matches given text
     * @should set value to radiology report whos uuid matches given text
     * @should throw illegal argument exception for radiology report not found
     * @should return null for empty text
     * 
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    public void setAsText(String text) throws IllegalArgumentException {
        final RadiologyReportService radiologyReportService = Context.getService(RadiologyReportService.class);
        if (StringUtils.hasText(text)) {
            try {
                setValue(radiologyReportService.getRadiologyReport(Integer.valueOf(text)));
            }
            catch (Exception ex) {
                final RadiologyReport radiologyReport = radiologyReportService.getRadiologyReportByUuid(text);
                setValue(radiologyReport);
                if (radiologyReport == null) {
                    log.error("Error setting text: " + text, ex);
                    throw new IllegalArgumentException("RadiologyReport not found: " + ex.getMessage());
                }
            }
        } else {
            setValue(null);
        }
    }
    
    /**
     * @should return empty string if value does not contain a radiology report
     * @should return radiology report id if value does contain a radiology report
     * 
     * @see java.beans.PropertyEditorSupport#getAsText()
     */
    public String getAsText() {
        final RadiologyReport radiologyReport = (RadiologyReport) getValue();
        if (radiologyReport == null) {
            return "";
        } else {
            return radiologyReport.getReportId()
                    .toString();
        }
    }
    
}
