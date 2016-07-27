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

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;

/**
 * A class that supports on openmrs's orders to make the module DICOM compatible, corresponds to the
 * table order_dicom_complment
 */
public class RadiologyStudy extends BaseOpenmrsData {
    
    
    private Integer studyId;
    
    private String studyInstanceUid;
    
    private RadiologyOrder radiologyOrder;
    
    private PerformedProcedureStepStatus performedStatus;
    
    /**
     * Get studyId of RadiologyStudy.
     * 
     * @return studyId of RadiologyStudy
     */
    @Override
    public Integer getId() {
        return getStudyId();
    }
    
    /**
     * Get studyId of RadiologyStudy.
     * 
     * @return studyId of RadiologyStudy
     */
    public Integer getStudyId() {
        return studyId;
    }
    
    public RadiologyOrder getRadiologyOrder() {
        return radiologyOrder;
    }
    
    public PerformedProcedureStepStatus getPerformedStatus() {
        return performedStatus;
    }
    
    public String getStudyInstanceUid() {
        return studyInstanceUid;
    }
    
    /**
     * Returns true when this RadiologyStudy's performedStatus is in progress and false otherwise.
     * 
     * @return true on performedStatus in progress and false otherwise
     * @should return false if performed status is null
     * @should return false if performed status is not in progress
     * @should return true if performed status is in progress
     */
    public boolean isInProgress() {
        return performedStatus == PerformedProcedureStepStatus.IN_PROGRESS;
    }
    
    /**
     * Returns true when this RadiologyStudy's performedStatus is completed and false otherwise.
     * 
     * @return true on performedStatus completed and false otherwise
     * @should return false if performedStatus is null
     * @should return false if performedStatus is not completed
     * @should return true if performedStatus is completed
     */
    public boolean isCompleted() {
        return performedStatus == PerformedProcedureStepStatus.COMPLETED;
    }
    
    /**
     * Returns true when this Study's performedStatus is null and false otherwise.
     *
     * @return true on performedStatus null and false otherwise
     * @should return true if performedStatus is null
     * @should return false if performedStatus is not null
     */
    public boolean isScheduleable() {
        return performedStatus == null;
    }
    
    /**
     * Set studyId of RadiologyStudy.
     * 
     * @param studyId of RadiologyStudy
     */
    @Override
    public void setId(Integer studyId) {
        
        setStudyId(studyId);
    }
    
    /**
     * Set studyId of RadiologyStudy.
     * 
     * @param studyId of RadiologyStudy
     */
    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }
    
    public void setRadiologyOrder(RadiologyOrder radiologyOrder) {
        this.radiologyOrder = radiologyOrder;
    }
    
    public void setPerformedStatus(PerformedProcedureStepStatus performedStatus) {
        this.performedStatus = performedStatus;
    }
    
    public void setStudyInstanceUid(String studyInstanceUid) {
        this.studyInstanceUid = studyInstanceUid;
    }
    
    /**
     * @see Object#toString()
     * @return String of Study
     * @should return string of study with null for members that are null
     * @should return string of study
     */
    @Override
    public String toString() {
        
        final StringBuilder result = new StringBuilder();
        result.append("studyId: ")
                .append(this.getStudyId())
                .append(" studyInstanceUid: ")
                .append(this.getStudyInstanceUid());
        return result.toString();
    }
}
