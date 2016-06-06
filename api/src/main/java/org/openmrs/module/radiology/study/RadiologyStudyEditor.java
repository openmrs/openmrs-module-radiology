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
 * In version 1.9, added ability for this to also retrieve objects by uuid
 * 
 * @see RadiologyStudy
 */
public class RadiologyStudyEditor extends PropertyEditorSupport {
    
    
    private Log log = LogFactory.getLog(this.getClass());
    
    /**
     * @should set using id
     * @should return null for radiology study not found
     * @should return null for empty text
     * 
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    public void setAsText(String text) throws IllegalArgumentException {
        final RadiologyStudyService radiologyStudyService = Context.getService(RadiologyStudyService.class);
        if (StringUtils.hasText(text)) {
            setValue(radiologyStudyService.getRadiologyStudy(Integer.valueOf(text)));
        } else {
            setValue(null);
        }
    }
    
    /**
     * @should return empty string for non existing radiology study
     * @should return id as string for existing radiology study
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
