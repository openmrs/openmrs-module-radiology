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

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.RadiologyPrivileges;

/**
 * Service layer for {@code RadiologyModality}.
 *
 * @see RadiologyModality
 */
public interface RadiologyModalityService extends OpenmrsService {
    
    
    /**
     * Saves a new or updates an existing {@code RadiologyModality}.
     *
     * @param radiologyModality the radiology modality to be saved
     * @return the saved radiology modality
     * @throws IllegalArgumentException if radiologyModality is null
     * @throws APIException if radiologyModality is not valid
     * <strong>Should</strong> create a new radiology modality
     * <strong>Should</strong> update an existing radiology modality
     * <strong>Should</strong> throw illegal argument exception if given radiology modality is null
     * <strong>Should</strong> throw api exception if radiology modality is not valid
     */
    @Authorized(RadiologyPrivileges.MANAGE_RADIOLOGY_MODALITIES)
    public RadiologyModality saveRadiologyModality(RadiologyModality radiologyModality);
    
    /**
     * Retires an existing {@code RadiologyModality}.
     * <p>This effectively removes the modality from circulation or use.</p>
     *
     * @param radiologyModality the radiology modality to retire
     * @param reason the reason why to retire the radiology modality
     * @return the retired radiology modality
     * @throws IllegalArgumentException if radiologyModality is null
     * @throws IllegalArgumentException if reason is null or contains only whitespaces
     * <strong>Should</strong> retire an existing radiology modality
     * <strong>Should</strong> throw illegal argument exception if given radiology modality is null
     * <strong>Should</strong> throw illegal argument exception if given reason is null or contains only whitespaces
     */
    @Authorized(RadiologyPrivileges.MANAGE_RADIOLOGY_MODALITIES)
    public RadiologyModality retireRadiologyModality(RadiologyModality radiologyModality, String reason);
    
    /**
     * Get the {@code RadiologyModality} by its {@code id}.
     *
     * @param id the modality id of the wanted radiology modality
     * @return the radiology modality matching given id
     * @throws IllegalArgumentException if given null
     * <strong>Should</strong> return radiology modality matching given id
     * <strong>Should</strong> return null if no match was found
     * <strong>Should</strong> throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_MODALITIES)
    public RadiologyModality getRadiologyModality(Integer id);
    
    /**
     * Get the {@code RadiologyModality} by its {@code UUID}.
     *
     * @param uuid the uuid of the radiology modality
     * @return the radiology modality matching given uuid
     * @throws IllegalArgumentException if given null
     * <strong>Should</strong> return radiology modality matching given uuid
     * <strong>Should</strong> return null if no match was found
     * <strong>Should</strong> throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_MODALITIES)
    public RadiologyModality getRadiologyModalityByUuid(String uuid);
    
    /**
     * Get the {@code RadiologyModality's}.
     *
     * @param includeRetired specifies if retired modalities should also be returned
     *
     * @return the radiology modalities
     * <strong>Should</strong> return radiology modalities including retired ones if given true
     * <strong>Should</strong> return radiology modalities excluding retired ones if given false
     * <strong>Should</strong> return empty list if no match was found
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_MODALITIES)
    public List<RadiologyModality> getRadiologyModalities(boolean includeRetired);
}
