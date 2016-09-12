/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality;

import java.util.List;

/**
 * {@code RadiologyModality} related database methods.
 * 
 * @see RadiologyModalityService
 * @see RadiologyModality
 */
interface RadiologyModalityDAO {
    
    
    /**
     * @see RadiologyModalityService#saveRadiologyModality(RadiologyModality)
     */
    RadiologyModality saveRadiologyModality(RadiologyModality radiologyModality);
    
    /**
     * @see RadiologyModalityService#getRadiologyModality(Integer)
     */
    RadiologyModality getRadiologyModality(Integer id);
    
    /**
     * @see RadiologyModalityService#getRadiologyModalityByUuid(String)
     */
    RadiologyModality getRadiologyModalityByUuid(String uuid);
    
    /**
     * @see RadiologyModalityService#getRadiologyModalities(boolean)
     */
    List<RadiologyModality> getRadiologyModalities(boolean includeRetired);
}
