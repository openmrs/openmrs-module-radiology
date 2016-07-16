/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

/**
 * <p>
 * Represents the Roles of the users, which use the radiology module.
 * </p>
 */
public class RadiologyRoles {
    
    
    public static final String SCHEDULER = "Radiology: Scheduler";
    
    public static final String PERFORMING_PHYSICIAN = "Radiology: Performing physician";
    
    public static final String READING_PHYSICIAN = "Radiology: Reading physician";
    
    public static final String REFERRING_PHYSICIAN = "Radiology: Referring physician";
    
    private RadiologyRoles() {
        // Utility class not meant to be instantiated.
    }
}
