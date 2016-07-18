/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order;

/**
 * Generate new unique accession numbers.
 */
public interface AccessionNumberGenerator {
    
    
    /**
     * Generates a new accession number. Note that this method is invoked in a non thread-safe way,
     * therefore implementations need to be thread safe.
     * 
     * @return the new accession number
     * @should always return a unique accession number when called multiple times
     */
    public String getNewAccessionNumber();
}
