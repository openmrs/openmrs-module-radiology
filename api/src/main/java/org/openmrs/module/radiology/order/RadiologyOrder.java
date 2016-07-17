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

import org.openmrs.TestOrder;
import org.openmrs.module.radiology.study.RadiologyStudy;

/**
 * RadiologyOrder represents a radiology examination
 */
public class RadiologyOrder extends TestOrder {
    
    
    private RadiologyStudy study;
    
    public RadiologyStudy getStudy() {
        return study;
    }
    
    /**
     * Set the Order.study to the given RadiologyStudy. Keeps the bi-directional (one-to-one) association
     * between RadiologyOrder and RadiologyStudy in sync.
     *
     * @param radiologyStudy study which should be associated with this radiology order
     * @should set the study attribute to given study
     * @should set the radiology order of given study to this radiology order
     * @should not fail given null
     */
    public void setStudy(RadiologyStudy radiologyStudy) {
        if (radiologyStudy != null) {
            radiologyStudy.setRadiologyOrder(this);
        }
        this.study = radiologyStudy;
    }
    
    /**
     * Returns true if study is in progress and false otherwise.
     * 
     * @return true if study is in progress and false otherwise
     * @should return false if associated study is null
     * @should return false if associated study is not in progress
     * @should return true if associated study is in progress
     */
    public boolean isInProgress() {
        
        if (this.study == null) {
            return false;
        } else {
            return this.study.isInProgress();
        }
    }
    
    /**
     * Returns true if study is not in progress and false otherwise.
     * 
     * @return true if study is not in progress and false otherwise
     * @should return true if associated study is null
     * @should return true if associated study is not in progress
     * @should return false if associated study in progress
     */
    public boolean isNotInProgress() {
        
        return !this.isInProgress();
    }
    
    /**
     * Returns true when this RadiologyOrder has a completed RadiologyStudy and false otherwise.
     * 
     * @return true if order has completed study and false otherwise
     * @should return false if associated study is null
     * @should return false if associated study is not completed
     * @should return true if associated study is completed
     */
    public boolean isCompleted() {
        
        if (this.study == null) {
            return false;
        } else {
            return this.study.isCompleted();
        }
    }
    
    /**
     * Returns true when this RadiologyOrder does not have a completed RadiologyStudy and false otherwise.
     * 
     * @return true if order has no completed study and false otherwise
     * @should return true if associated study is null
     * @should return true if associated study is not completed
     * @should return false if associated study is completed
     */
    public boolean isNotCompleted() {
        
        return !this.isCompleted();
    }
    
    /**
     * Returns true when this RadiologyOrder can be discontinued and false otherwise.
     * 
     * @return true if radiology order can be discontinued and false otherwise
     * @should return false if radiology order is discontinued right now
     * @should return false if radiology order is in progress
     * @should return false if radiology order is completed
     * @should return true if radiology order is not discontinued right now and not in progress and not completed
     */
    public boolean isDiscontinuationAllowed() {
        
        return !this.isDiscontinuedRightNow() && this.isNotInProgress() && this.isNotCompleted();
    }
}
