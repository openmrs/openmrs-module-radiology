/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

/**
 * Allows for serializing/deserializing a RadiologyModality object to a string so that Spring knows how to pass
 * a RadiologyModality back and forth through an html form or other medium
 * <br/>
 * 
 * @see RadiologyModality
 */
public class RadiologyModalityEditor extends PropertyEditorSupport {
    
    
    private final Log log = LogFactory.getLog(this.getClass());
    
    /**
     * @should set value to radiology modality whos id matches given text
     * @should set value to radiology modality whos uuid matches given text
     * @should throw illegal argument exception for radiology modality not found
     * @should return null for empty text
     *
     * @see PropertyEditorSupport#setAsText(String)
     */
    public void setAsText(String text) throws IllegalArgumentException {
        final RadiologyModalityService radiologyModalityService = Context.getService(RadiologyModalityService.class);
        if (StringUtils.hasText(text)) {
            try {
                setValue(radiologyModalityService.getRadiologyModality(Integer.valueOf(text)));
            }
            catch (Exception ex) {
                final RadiologyModality radiologyModality = radiologyModalityService.getRadiologyModalityByUuid(text);
                setValue(radiologyModality);
                if (radiologyModality == null) {
                    log.error("Error setting text: " + text, ex);
                    throw new IllegalArgumentException("RadiologyModality not found: " + ex.getMessage());
                }
            }
        } else {
            setValue(null);
        }
    }
    
    /**
     * @should return empty string if value does not contain a radiology modality
     * @should return radiology modality id if value does contain a radiology modality
     *
     * @see PropertyEditorSupport#getAsText()
     */
    public String getAsText() {
        final RadiologyModality radiologyModality = (RadiologyModality) getValue();
        if (radiologyModality == null) {
            return "";
        } else {
            return radiologyModality.getModalityId()
                    .toString();
        }
    }
}
