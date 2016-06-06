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

import java.io.IOException;
import java.io.InputStream;

/**
 * A parser that is responsible for parsing mrrt report templates and extract metadata.
 */
public interface MrrtReportTemplateFileParser {
    
    
    /**
     * Parse an {@code MRRT} template and extract metadata into a {@code MrrtReportTemplate}.
     * 
     * @param in the input stream containing the mrrt template
     * @return the mrrt report template extracted from the input stream
     * @throws IOException if the template file could not be read
     */
    public MrrtReportTemplate parse(InputStream in) throws IOException;
}
