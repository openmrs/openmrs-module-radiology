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

import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RadiologyStudyService extends OpenmrsService {
    
    /**
     * <p>
     * Save the given <code>RadiologyStudy</code> to the database
     * </p>
     * Additionally, study and study.order information are written into a DICOM xml file.
     * 
     * @param radiologyStudy study to be created or updated
     * @return study who was created or updated
     * @should create new study from given study object
     * @should update existing study
     */
    public RadiologyStudy saveStudy(RadiologyStudy radiologyStudy);
    
    /**
     * <p>
     * Update the performedStatus of the <code>RadiologyStudy</code> associated with studyInstanceUid in the database
     * </p>
     *
     * @param studyInstanceUid study instance uid of study whos performedStatus should be updated
     * @param performedStatus performed procedure step status to which study should be set to
     * @return study whos performedStatus was updated
     * @throws IllegalArgumentException if study instance uid is null
     * @should update performed status of study associated with given study instance uid
     * @should throw illegal argument exception if study instance uid is null
     * @should throw illegal argument exception if performed status is null
     */
    public RadiologyStudy updateStudyPerformedStatus(String studyInstanceUid, PerformedProcedureStepStatus performedStatus)
            throws IllegalArgumentException;
    
    /**
     * Get RadiologyStudy by studyId
     *
     * @param studyId of the study
     * @return study associated with studyId
     * @should return study for given study id
     * @should return null if no match was found
     */
    public RadiologyStudy getStudyByStudyId(Integer studyId);
    
    /**
     * Get RadiologyStudy by its associated RadiologyOrder's orderId
     *
     * @param orderId of RadiologyOrder associated with wanted RadiologyStudy
     * @return RadiologyStudy associated with RadiologyOrder for which orderId is given
     * @throws IllegalArgumentException if order id is null
     * @should return study associated with radiology order for which order id is given
     * @should return null if no match was found
     * @should throw illegal argument exception given null
     */
    public RadiologyStudy getStudyByOrderId(Integer orderId) throws IllegalArgumentException;
    
    /**
     * Get RadiologyStudy by its Study Instance UID
     *
     * @param studyInstanceUid
     * @return study
     * @should return study matching study instance uid
     * @should return null if no match was found
     * @should throw IllegalArgumentException if study instance uid is null
     */
    public RadiologyStudy getStudyByStudyInstanceUid(String studyInstanceUid) throws IllegalArgumentException;
    
    /**
     * Get all studies corresponding to list of RadiologyOrder's
     *
     * @param radiologyOrders radiology orders for which studies will be returned
     * @return studies corresponding to given radiology orders
     * @throws IllegalArgumentException
     * @should fetch all studies for given radiology orders
     * @should return empty list given radiology orders without associated studies
     * @should return empty list given empty radiology order list
     * @should throw IllegalArgumentException given null
     */
    public List<RadiologyStudy> getStudiesByRadiologyOrders(List<RadiologyOrder> radiologyOrders)
            throws IllegalArgumentException;
}
