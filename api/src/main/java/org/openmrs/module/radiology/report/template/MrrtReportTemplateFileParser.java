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
import java.io.IOException;
import java.io.InputStream;

/**
 * A parser that is responsible for parsing mrrt report templates and extract metadata.
 */
public interface MrrtReportTemplateFileParser {
    
    
    /**
     * Parse an {@code MRRT} template file and extract metada and create an {@code MrrtReportTemplate} object
     * 
     * @param file
     * @return
     * @throws IOException when the file could not be read.
     * @throws APIException when the file fails validation. That is it is not of MRRT standards.
     * @should throw an APIException when file failed validation.
     * @should return an MrrtReportTemplate object if file is valid.
     */
    public MrrtReportTemplate parse(File file) throws IOException;
    
    /**
     * Parse an {@code MRRT} template file and extract metada and create an {@code MrrtReportTemplate} object
     * 
     * @param fileName the name of the file being parsed
     * @param in input stream of the file being parsed
     * @return returns an {@code MrrtReportTemplate}
     * @throws IOException when the file could not be read.
     * @throws APIException when the file fails validation. That is it is not of MRRT standards
     * @should throw an APIException when file is invalid
     * @should return an MrrtReportTemplate object is file is valid.
     */
    public MrrtReportTemplate parse(String fileName, InputStream in) throws IOException;
}
