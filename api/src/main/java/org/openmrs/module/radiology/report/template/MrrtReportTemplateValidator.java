/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import org.openmrs.api.APIException;

import java.io.File;

/**
 * This is a validator for {@code MRRT} template files. This is used by the {@code MrrtReportTemplateParser} to make sure all parsed templates are of valid format.
 * 
 * @see org.openmrs.module.radiology.report.template.MrrtReportTemplate
 */
public interface MrrtReportTemplateValidator {
    
    
    /**
     * Validates template file to make sure it follows MRRT standards.
     * 
     * @param templateFile the file been validated
     * @throws APIException when file violates one of the MRRT specification rules.
     * @should should throw APIException if file extension is not .html
     */
    public void validate(File templateFile) throws APIException;
}
