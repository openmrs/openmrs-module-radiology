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

/**
 * A parser that is responsible for parsing mrrt report templates and extract metadata.
 */
public interface MrrtReportTemplateFileParser {
    
    
    /**
     * Parse an mrrt template and extract metadata into a {@code MrrtReportTemplate}.
     * 
     * @param mrrtTemplate the mrrt template to parse
     * @return the mrrt report template
     * @throws IOException if one is thrown during validation
     * @should return an mrrt template object if given template is valid
     * @should store terms element in template object if they match a concept reference term in openmrs
     * @should skip terms element in template file if no corresponding concept reference term was found
     * @should ignore case when searching for a matching concept source
     */
    public MrrtReportTemplate parse(String mrrtTemplate) throws IOException;
}
