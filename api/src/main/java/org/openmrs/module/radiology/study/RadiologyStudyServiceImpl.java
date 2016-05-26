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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.dicom.code.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class RadiologyStudyServiceImpl extends BaseOpenmrsService implements RadiologyStudyService {
    
    private static final Log log = LogFactory.getLog(RadiologyStudyServiceImpl.class);
    
    @Autowired
    private RadiologyStudyDAO radiologyStudyDAO;
    
    @Autowired
    private RadiologyProperties radiologyProperties;
    
    /**
     * @see RadiologyStudyService#saveStudy(RadiologyStudy)
     */
    @Override
    @Transactional
    public RadiologyStudy saveStudy(RadiologyStudy radiologyStudy) {
        
        final RadiologyOrder order = radiologyStudy.getRadiologyOrder();
        
        if (radiologyStudy.getScheduledStatus() == null && order.getScheduledDate() != null) {
            radiologyStudy.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
        }
        
        try {
            RadiologyStudy savedStudy = radiologyStudyDAO.saveStudy(radiologyStudy);
            final String studyInstanceUid = radiologyProperties.getStudyPrefix() + savedStudy.getStudyId();
            savedStudy.setStudyInstanceUid(studyInstanceUid);
            savedStudy = radiologyStudyDAO.saveStudy(savedStudy);
            return savedStudy;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            log.warn("Can not save study in openmrs or dmc4che.");
        }
        return null;
    }
    
    /**
     * @see RadiologyStudyService#updateStudyPerformedStatus(String, PerformedProcedureStepStatus)
     */
    @Transactional
    @Override
    public RadiologyStudy updateStudyPerformedStatus(String studyInstanceUid, PerformedProcedureStepStatus performedStatus)
            throws IllegalArgumentException {
        
        if (studyInstanceUid == null) {
            throw new IllegalArgumentException("studyInstanceUid is required");
        }
        
        if (performedStatus == null) {
            throw new IllegalArgumentException("performedStatus is required");
        }
        
        final RadiologyStudy studyToBeUpdated = radiologyStudyDAO.getStudyByStudyInstanceUid(studyInstanceUid);
        studyToBeUpdated.setPerformedStatus(performedStatus);
        return radiologyStudyDAO.saveStudy(studyToBeUpdated);
    }
    
    /**
     * @see RadiologyStudyService#getStudyByStudyId(Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public RadiologyStudy getStudyByStudyId(Integer studyId) {
        return radiologyStudyDAO.getStudyByStudyId(studyId);
    }
    
    /**
     * @see RadiologyStudyService#getStudyByOrderId(Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public RadiologyStudy getStudyByOrderId(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId is required");
        }
        
        return radiologyStudyDAO.getStudyByOrderId(orderId);
    }
    
    /**
     * @see RadiologyStudyService#getStudyByStudyInstanceUid(String)
     */
    @Transactional(readOnly = true)
    public RadiologyStudy getStudyByStudyInstanceUid(String studyInstanceUid) {
        if (studyInstanceUid == null) {
            throw new IllegalArgumentException("studyInstanceUid is required");
        }
        
        return radiologyStudyDAO.getStudyByStudyInstanceUid(studyInstanceUid);
    }
    
    /**
     * @see RadiologyStudyService#getStudiesByRadiologyOrders
     */
    @Override
    @Transactional(readOnly = true)
    public List<RadiologyStudy> getStudiesByRadiologyOrders(List<RadiologyOrder> radiologyOrders) {
        if (radiologyOrders == null) {
            throw new IllegalArgumentException("radiologyOrders are required");
        }
        
        final List<RadiologyStudy> result = radiologyStudyDAO.getStudiesByRadiologyOrders(radiologyOrders);
        return result;
    }
}
