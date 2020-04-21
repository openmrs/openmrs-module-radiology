/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.study;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.RadiologyPrivileges;

/**
 * Service layer for {@code RadiologyStudy}.
 * 
 * @see org.openmrs.module.radiology.study.RadiologyStudy
 */
public interface RadiologyStudyService extends OpenmrsService {
    
    
    /**
     * Saves a new {@code RadiologyStudy} to the database.
     * 
     * @param radiologyStudy the radiology study to be created
     * @return the created radiology study
     * @throws IllegalArgumentException if given null
     * @throws IllegalArgumentException if global property DICOM UID org root cannot be found
     * @throws IllegalArgumentException if global property DICOM UID org root is empty
     * @throws IllegalArgumentException if global property DICOM UID org root is not a valid UID
     * @throws IllegalArgumentException if global property DICOM UID org root exceeds the maximum length
     * @throws APIException on saving an existing radiology study
     * <strong>Should</strong> create new radiology study from given radiology study
     * <strong>Should</strong> set the study instance uid of given radiology study to a valid dicom uid if null
     * <strong>Should</strong> set the study instance uid of given radiology study to a valid dicom uid if only containing whitespaces
     * <strong>Should</strong> not set the study instance uid of given radiology study if contains non whitespace characters
     * <strong>Should</strong> throw illegal argument exception if given null
     * <strong>Should</strong> throw api exception on saving an existing radiology study
     */
    @Authorized(RadiologyPrivileges.ADD_RADIOLOGY_STUDIES)
    public RadiologyStudy saveRadiologyStudy(RadiologyStudy radiologyStudy);
    
    /**
     * Get the {@code RadiologyStudy} by its {@code studyId}.
     *
     * @param studyId the study id of the wanted study
     * @return the radiology study matching given study id
     * @throws IllegalArgumentException if given null
     * <strong>Should</strong> return radiology study matching given study id
     * <strong>Should</strong> return null if no match was found
     * <strong>Should</strong> throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_STUDIES)
    public RadiologyStudy getRadiologyStudy(Integer studyId);
    
    /**
     * Get the {@code RadiologyStudy} by its {@code UUID}.
     *
     * @param uuid the uuid of the radiology study
     * @return the radiology study matching given uuid
     * @throws IllegalArgumentException if given null
     * <strong>Should</strong> return radiology study matching given uuid
     * <strong>Should</strong> return null if no match was found
     * <strong>Should</strong> throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_STUDIES)
    public RadiologyStudy getRadiologyStudyByUuid(String uuid);
    
    /**
     * Get the {@code RadiologyStudy} by its Study Instance UID.
     *
     * @param studyInstanceUid the study instance uid of wanted radiology study
     * @return the radiology study exactly matching given study instance uid
     * @throws IllegalArgumentException if given null
     * <strong>Should</strong> return radiology study exactly matching given study instance uid
     * <strong>Should</strong> return null if no match was found
     * <strong>Should</strong> throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_STUDIES)
    public RadiologyStudy getRadiologyStudyByStudyInstanceUid(String studyInstanceUid);
}
