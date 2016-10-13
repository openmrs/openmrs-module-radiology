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

/**
 * Utility class that contains constants which are used within this module.
 */
public final class MrrtReportTemplateConstants {
    
    
    /**
     * Constant for dcterms.date format for an {@code MrrtReportTemplate}.
     *
     * See table "Table 4.105.4.1.2-1: HTTP Query Parameters" on page 27 of
     * the MRRT Standards document.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    private MrrtReportTemplateConstants() {
        // Utility class not meant to be instantiated.
    }
    
}
