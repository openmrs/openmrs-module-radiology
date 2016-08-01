/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.study;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.springframework.util.StringUtils;

/**
 * Allows for serializing/deserializing a RadiologyStudy object to a string so that Spring knows how to pass
 * a RadiologyStudy back and forth through an html form or other medium
 * <br/>
 * 
 * @see RadiologyStudy
 */
public class RadiologyStudyEditor extends PropertyEditorSupport {
    
    
    private final Log log = LogFactory.getLog(this.getClass());
    
    /**
     * @should set value to radiology study whos id matches given text
     * @should set value to radiology study whos uuid matches given text
     * @should throw illegal argument exception for radiology study not found
     * @should return null for empty text
     * 
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    public void setAsText(String text) throws IllegalArgumentException {
        final RadiologyStudyService radiologyStudyService = Context.getService(RadiologyStudyService.class);
        if (StringUtils.hasText(text)) {
            try {
                setValue(radiologyStudyService.getRadiologyStudy(Integer.valueOf(text)));
            }
            catch (Exception ex) {
                final RadiologyStudy radiologyStudy = radiologyStudyService.getRadiologyStudyByUuid(text);
                setValue(radiologyStudy);
                if (radiologyStudy == null) {
                    log.error("Error setting text: " + text, ex);
                    throw new IllegalArgumentException("RadiologyStudy not found: " + ex.getMessage());
                }
            }
        } else {
            setValue(null);
        }
    }
    
    /**
     * @should return empty string if value does not contain a radiology study
     * @should return radiology study id if value does contain a radiology study
     * 
     * @see java.beans.PropertyEditorSupport#getAsText()
     */
    public String getAsText() {
        final RadiologyStudy radiologyStudy = (RadiologyStudy) getValue();
        if (radiologyStudy == null) {
            return "";
        } else {
            return radiologyStudy.getStudyId()
                    .toString();
        }
    }
    
}
