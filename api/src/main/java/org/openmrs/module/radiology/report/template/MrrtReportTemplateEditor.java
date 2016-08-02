/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Allows for serializing/deserializing a MrrtReportTemplate object to a string so that Spring knows how to pass
 * a MrrtReportTemplate back and forth through an html form or other medium
 * <br/>
 *
 * @see MrrtReportTemplate
 */
@Component
public class MrrtReportTemplateEditor extends PropertyEditorSupport {
    
    
    private final Log log = LogFactory.getLog(this.getClass());
    
    /**
     * @should set value to mrrt report template whos id matches given text
     * @should set value to mrrt report template whos uuid matches given text
     * @should throw illegal argument exception for mrrt report template not found
     * @should return null for empty text
     *
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    public void setAsText(String text) throws IllegalArgumentException {
        final MrrtReportTemplateService mrrtReportTemplateService = Context.getService(MrrtReportTemplateService.class);
        if (StringUtils.hasText(text)) {
            try {
                setValue(mrrtReportTemplateService.getMrrtReportTemplate(Integer.valueOf(text)));
            }
            catch (Exception ex) {
                final MrrtReportTemplate mrrtReportTemplate = mrrtReportTemplateService.getMrrtReportTemplateByUuid(text);
                setValue(mrrtReportTemplate);
                if (mrrtReportTemplate == null) {
                    log.error("Error setting text: " + text, ex);
                    throw new IllegalArgumentException("MrrtReportTemplate not found: " + ex.getMessage());
                }
            }
        } else {
            setValue(null);
        }
    }
    
    /**
     * @should return empty string if value does not contain a mrrt report template
     * @should return mrrt report template id if value does contain a mrrt report template
     *
     * @see java.beans.PropertyEditorSupport#getAsText()
     */
    public String getAsText() {
        final MrrtReportTemplate mrrtReportTemplate = (MrrtReportTemplate) getValue();
        if (mrrtReportTemplate == null) {
            return "";
        } else {
            return mrrtReportTemplate.getTemplateId()
                    .toString();
        }
    }
}
