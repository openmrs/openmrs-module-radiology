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
 * Represents an error found during validation of the IHE Management of Radiology Report Templates (MRRT).
 */
public class ValidationError {
    
    
    private final String description;
    
    private final String messageCode;
    
    /**
     * Get this description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get this message code.
     *
     * @return the message code
     */
    public String getMessageCode() {
        return messageCode;
    }
    
    /**
     * Creates a new instance of {@link ValidationError}.
     *
     * @param description the description of the error
     * @param messageCode the message code of the error
     */
    public ValidationError(String description, String messageCode) {
        this.description = description;
        this.messageCode = messageCode;
    }
    
    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return description;
    }
}
