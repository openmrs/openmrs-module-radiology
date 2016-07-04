/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import java.io.File;

import org.openmrs.api.APIException;

/**
 * Default validator implementation for {@code MRRT} templates.
 */
class DefaultMrrtReportTemplateValidator implements MrrtReportTemplateValidator {
    
    
    private static final String VALID_EXTENSION = "html";
    
    /**
     *  @see org.openmrs.module.radiology.report.template.MrrtReportTemplate
     *  @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService
     */
    @Override
    public void validate(File templateFile) throws APIException {
        
        if (!VALID_EXTENSION.equals(getFileExtension(templateFile))) {
            throw new APIException(
                    "Invalid file extension (." + getFileExtension(templateFile) + "). Only (.html) files are accepted");
        }
    }
    
    private String getFileExtension(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
        
        return fileExtension;
    }
}
