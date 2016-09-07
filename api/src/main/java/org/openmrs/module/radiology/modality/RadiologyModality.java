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

import org.openmrs.BaseOpenmrsMetadata;

/**
 * Represents a radiology modality used to perform imaging procedures/studies which result in radiological images.
 */
public class RadiologyModality extends BaseOpenmrsMetadata {
    
    
    private Integer modalityId;
    
    /**
     * See DICOM standard Part 5, Table 6.2-1. DICOM Value Representations - VR AE for its rules.
     */
    private String aeTitle;
    
    /**
     * @see org.openmrs.OpenmrsObject#getId()
     */
    @Override
    public Integer getId() {
        return getModalityId();
    }
    
    /**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    @Override
    public void setId(Integer modalityId) {
        setModalityId(modalityId);
    }
    
    /**
     * Get modalityId of RadiologyModality.
     *
     * @return modalityId of RadiologyModality
     */
    public Integer getModalityId() {
        return modalityId;
    }
    
    /**
     * Set modalityId of RadiologyModality.
     *
     * @param modalityId the modality id of the RadiologyModality
     */
    public void setModalityId(Integer modalityId) {
        this.modalityId = modalityId;
    }
    
    /**
     * Get AE Title of RadiologyModality.
     *
     * @return aeTitle of RadiologyModality
     */
    public String getAeTitle() {
        return aeTitle;
    }
    
    /**
     * Set AE Title of RadiologyModality.
     *
     * @param aeTitle the ae title of the RadiologyModality
     */
    public void setAeTitle(String aeTitle) {
        this.aeTitle = aeTitle;
    }
}
