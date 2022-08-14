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

import org.openmrs.api.APIException;

/**
 * Validates an mrrt report template according to the IHE Management of Radiology Report Templates (MRRT).
 *
 * @see MrrtReportTemplate
 */
public interface MrrtReportTemplateValidator {
    
    
    /**
     * Validate an mrrt template according to the IHE standard.
     *
     * @param mrrtTemplate the mrrt report template to be validated
     * @throws IOException
     * @throws APIException if the mrrt template fails validation
     * <strong>Should</strong> pass if template template follows mrrt standards
     * <strong>Should</strong> throw api exception if template does not have an html element
     * <strong>Should</strong> throw api exception if template has more than one html element
     * <strong>Should</strong> throw api exception if html element does not have a head element
     * <strong>Should</strong> throw api exception if html element has more than one head element
     * <strong>Should</strong> throw api exception if head element does not have a title element
     * <strong>Should</strong> throw api exception if head element has more than one title element
     * <strong>Should</strong> throw api exception if head element does not have a meta element with charset attribute
     * <strong>Should</strong> throw api exception if head element has more than one meta element with charset attribute
     * <strong>Should</strong> throw api exception if head element does not have one or more meta elements denoting dublin core attributes
     * <strong>Should</strong> throw api exception if head element does not have script element
     * <strong>Should</strong> throw api exception if head element has more than one script element
     * <strong>Should</strong> throw api exception if script element does not have a template attributes element
     * <strong>Should</strong> throw api exception if script element has more than one template attributes element
     * <strong>Should</strong> throw api exception if coding schemes element does not have at least one coding scheme element
     * <strong>Should</strong> throw api exception if term element does not have a code element
     * <strong>Should</strong> throw api exception if term element has more than one code element
     * <strong>Should</strong> throw api exception if code element lacks one of meaning scheme or value attribute
     * <strong>Should</strong> throw api exception if template attributes element does not have a coded content element
     * <strong>Should</strong> throw api exception if template attributes element has more than one coded content element
     * <strong>Should</strong> throw api exception if html element does not have a body element
     * <strong>Should</strong> throw api exception if html element has more than one body element 
     * <strong>Should</strong> catch all violation errors and throw an mrrt report template exception
     */
    public void validate(String mrrtTemplate) throws IOException;
}
