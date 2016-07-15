/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.study;

import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;

/**
 * Service layer for {@code RadiologyStudy}.
 * 
 * @see org.openmrs.module.radiology.study.RadiologyStudy
 */
public interface RadiologyStudyService extends OpenmrsService {
    
    
    /**
     * Saves a {@code RadiologyStudy} to the database.
     * 
     * @param radiologyStudy the radiology study to be created or updated
     * @return the created or updated radiology study
     * @throws IllegalArgumentException if given null
     * @throws IllegalArgumentException if global property DICOM UID org root cannot be found
     * @throws IllegalArgumentException if global property DICOM UID org root is empty
     * @throws IllegalArgumentException if global property DICOM UID org root is not a valid UID
     * @throws IllegalArgumentException if global property DICOM UID org root exceeds the maximum length
     * @should create new radiology study from given radiology study
     * @should set the study instance uid of given radiology study to a valid dicom uid if null
     * @should set the study instance uid of given radiology study to a valid dicom uid if only containing whitespaces
     * @should not set the study instance uid of given radiology study if contains non whitespace characters
     * @should update existing radiology study
     * @should throw illegal argument exception if given null
     */
    public RadiologyStudy saveRadiologyStudy(RadiologyStudy radiologyStudy);
    
    /**
     * Updates a {@code RadiologyStudy's} performed status in the database.
     *
     * @param studyInstanceUid the study instance uid of the study whos performed status should be updated
     * @param performedStatus the performed procedure step status to which the study should be set to
     * @return the radiology study whos performed status was updated
     * @throws IllegalArgumentException if studyInstanceUid is null
     * @throws IllegalArgumentException if performedStatus is null
     * @should update performed status of radiology study associated with given study instance uid
     * @should throw illegal argument exception if study instance uid is null
     * @should throw illegal argument exception if performed status is null
     */
    public RadiologyStudy updateStudyPerformedStatus(String studyInstanceUid, PerformedProcedureStepStatus performedStatus);
    
    /**
     * Get the {@code RadiologyStudy} by its {@code studyId}.
     *
     * @param studyId the study id of the wanted study
     * @return the radiology study matching given study id
     * @throws IllegalArgumentException if given null
     * @should return radiology study matching given study id
     * @should return null if no match was found
     * @should throw illegal argument exception if given null
     */
    public RadiologyStudy getRadiologyStudy(Integer studyId);
    
    /**
     * Get the {@code RadiologyStudy} by its {@code UUID}.
     *
     * @param uuid the uuid of the radiology study
     * @return the radiology study matching given uuid
     * @throws IllegalArgumentException if given null
     * @should return radiology study matching given uuid
     * @should return null if no match was found
     * @should throw illegal argument exception if given null
     */
    public RadiologyStudy getRadiologyStudyByUuid(String uuid);
    
    /**
     * Get the {@code RadiologyStudy} by its associated {@code RadiologyOrder's} {@code orderId}.
     *
     * @param orderId the order id of the wanted radiology studies associated radiology order
     * @return the radiology study associated with a radiology order matching given order id
     * @throws IllegalArgumentException if given null
     * @should return radiology study associated with radiology order for which order id is given
     * @should return null if no match was found
     * @should throw illegal argument exception if given null
     */
    public RadiologyStudy getRadiologyStudyByOrderId(Integer orderId);
    
    /**
     * Get the {@code RadiologyStudy} by its Study Instance UID.
     *
     * @param studyInstanceUid the study instance uid of wanted radiology study
     * @return the radiology study exactly matching given study instance uid
     * @throws IllegalArgumentException if given null
     * @should return radiology study exactly matching given study instance uid
     * @should return null if no match was found
     * @should throw illegal argument exception if given null
     */
    public RadiologyStudy getRadiologyStudyByStudyInstanceUid(String studyInstanceUid);
    
    /**
     * Get the {@code RadiologyStudy's} associated with a list of {@code RadiologyOrder's}.
     *
     * @param radiologyOrders the radiology orders for which radiology studies will be returned
     * @return the radiology studies associated with given radiology orders
     * @throws IllegalArgumentException given null
     * @should return all radiology studies associated with given radiology orders
     * @should return empty list given radiology orders without associated radiology studies
     * @should return empty list given empty radiology order list
     * @should throw illegal argument exception given null
     */
    public List<RadiologyStudy> getRadiologyStudiesByRadiologyOrders(List<RadiologyOrder> radiologyOrders);
}
