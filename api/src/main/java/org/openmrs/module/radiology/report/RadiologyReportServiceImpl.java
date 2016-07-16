/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Provider;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
class RadiologyReportServiceImpl extends BaseOpenmrsService implements RadiologyReportService {
    
    
    private static final Log log = LogFactory.getLog(RadiologyReportServiceImpl.class);
    
    private RadiologyReportDAO radiologyReportDAO;
    
    public void setRadiologyReportDAO(RadiologyReportDAO radiologyReportDAO) {
        this.radiologyReportDAO = radiologyReportDAO;
    }
    
    /**
     * @see RadiologyReportService#createAndClaimRadiologyReport(RadiologyOrder)
     */
    @Override
    @Transactional
    public RadiologyReport createAndClaimRadiologyReport(RadiologyOrder radiologyOrder)
            throws IllegalArgumentException, UnsupportedOperationException {
        
        if (radiologyOrder == null) {
            throw new IllegalArgumentException("radiologyOrder cannot be null");
        }
        if (radiologyOrder.isNotCompleted()) {
            throw new IllegalArgumentException("radiologyOrder needs to be completed");
        }
        if (radiologyReportDAO.hasRadiologyOrderCompletedRadiologyReport(radiologyOrder)) {
            throw new UnsupportedOperationException(
                    "cannot create radiologyReport for this radiologyOrder because it is already completed");
        }
        if (radiologyReportDAO.hasRadiologyOrderClaimedRadiologyReport(radiologyOrder)) {
            throw new UnsupportedOperationException(
                    "cannot create radiologyReport for this radiologyOrder because it is already claimed");
        }
        final RadiologyReport radiologyReport = new RadiologyReport(radiologyOrder);
        return radiologyReportDAO.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     */
    @Override
    @Transactional
    public RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport)
            throws IllegalArgumentException, UnsupportedOperationException {
        
        if (radiologyReport == null) {
            throw new IllegalArgumentException("radiologyReport cannot be null");
        }
        if (radiologyReport.getReportStatus() == null) {
            throw new IllegalArgumentException("radiologyReportStatus cannot be null");
        }
        if (radiologyReport.getReportStatus() == RadiologyReportStatus.DISCONTINUED) {
            throw new UnsupportedOperationException("a discontinued radiologyReport cannot be saved");
        }
        if (radiologyReport.getReportStatus() == RadiologyReportStatus.COMPLETED) {
            throw new UnsupportedOperationException("a completed radiologyReport cannot be saved");
        }
        return radiologyReportDAO.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#unclaimRadiologyReport(RadiologyReport)
     */
    @Override
    @Transactional
    public RadiologyReport unclaimRadiologyReport(RadiologyReport radiologyReport)
            throws IllegalArgumentException, UnsupportedOperationException {
        
        if (radiologyReport == null) {
            throw new IllegalArgumentException("radiologyReport cannot be null");
        }
        if (radiologyReport.getReportStatus() == null) {
            throw new IllegalArgumentException("radiologyReportStatus cannot be null");
        }
        if (radiologyReport.getReportStatus() == RadiologyReportStatus.DISCONTINUED) {
            throw new UnsupportedOperationException("a discontinued radiologyReport cannot be unclaimed");
        }
        if (radiologyReport.getReportStatus() == RadiologyReportStatus.COMPLETED) {
            throw new UnsupportedOperationException("a completed radiologyReport cannot be unclaimed");
        }
        radiologyReport.setReportStatus(RadiologyReportStatus.DISCONTINUED);
        return radiologyReportDAO.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#completeRadiologyReport(RadiologyReport, Provider)
     */
    @Override
    @Transactional
    public RadiologyReport completeRadiologyReport(RadiologyReport radiologyReport, Provider principalResultsInterpreter)
            throws IllegalArgumentException, UnsupportedOperationException {
        
        if (radiologyReport == null) {
            throw new IllegalArgumentException("radiologyReport cannot be null");
        }
        if (principalResultsInterpreter == null) {
            throw new IllegalArgumentException("principalResultsInterpreter cannot be null");
        }
        if (radiologyReport.getReportStatus() == null) {
            throw new IllegalArgumentException("radiologyReportStatus cannot be null");
        }
        if (radiologyReport.getReportStatus() == RadiologyReportStatus.DISCONTINUED) {
            throw new UnsupportedOperationException("a discontinued radiologyReport cannot be completed");
        }
        if (radiologyReport.getReportStatus() == RadiologyReportStatus.COMPLETED) {
            throw new UnsupportedOperationException("a completed radiologyReport cannot be completed");
        }
        radiologyReport.setReportDate(new Date());
        radiologyReport.setPrincipalResultsInterpreter(principalResultsInterpreter);
        radiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
        return radiologyReportDAO.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReport(Integer)
     */
    @Override
    public RadiologyReport getRadiologyReport(Integer reportId) throws IllegalArgumentException {
        
        if (reportId == null) {
            throw new IllegalArgumentException("reportId cannot be null");
        }
        return radiologyReportDAO.getRadiologyReport(reportId);
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReportByUuid(String)
     */
    @Override
    public RadiologyReport getRadiologyReportByUuid(String radiologyReportUuid) throws IllegalArgumentException {
        
        if (radiologyReportUuid == null) {
            throw new IllegalArgumentException("radiologyReportUuid cannot be null");
        }
        return radiologyReportDAO.getRadiologyReportByUuid(radiologyReportUuid);
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder, RadiologyReportStatus)
     */
    @Override
    public List<RadiologyReport> getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder radiologyOrder,
            RadiologyReportStatus reportStatus) throws IllegalArgumentException {
        
        if (radiologyOrder == null) {
            throw new IllegalArgumentException("radiologyOrder cannot be null");
        }
        if (reportStatus == null) {
            throw new IllegalArgumentException("radiologyReportStatus cannot be null");
        }
        return radiologyReportDAO.getRadiologyReportsByRadiologyOrderAndRadiologyReportStatus(radiologyOrder, reportStatus)
                .size() > 0
                        ? radiologyReportDAO.getRadiologyReportsByRadiologyOrderAndRadiologyReportStatus(radiologyOrder,
                            reportStatus)
                        : new ArrayList<RadiologyReport>();
    }
    
    /**
     * @see RadiologyReportService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     */
    @Override
    public boolean hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder radiologyOrder) {
        
        if (radiologyOrder == null) {
            throw new IllegalArgumentException("radiologyOrder cannot be null");
        }
        return radiologyReportDAO.hasRadiologyOrderClaimedRadiologyReport(radiologyOrder);
    }
    
    /**
     * @see RadiologyReportService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     */
    @Override
    public boolean hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder radiologyOrder) {
        
        if (radiologyOrder == null) {
            throw new IllegalArgumentException("radiologyOrder cannot be null");
        }
        return radiologyReportDAO.hasRadiologyOrderCompletedRadiologyReport(radiologyOrder);
    }
    
    /**
     * @see RadiologyReportService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     */
    @Override
    public RadiologyReport getActiveRadiologyReportByRadiologyOrder(RadiologyOrder radiologyOrder) {
        
        if (radiologyOrder == null) {
            throw new IllegalArgumentException("radiologyOrder cannot be null");
        }
        if (hasRadiologyOrderCompletedRadiologyReport(radiologyOrder)) {
            return radiologyReportDAO.getActiveRadiologyReportByRadiologyOrder(radiologyOrder);
        }
        if (hasRadiologyOrderClaimedRadiologyReport(radiologyOrder)) {
            return radiologyReportDAO.getActiveRadiologyReportByRadiologyOrder(radiologyOrder);
        }
        return null;
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     */
    @Override
    public List<RadiologyReport> getRadiologyReports(RadiologyReportSearchCriteria radiologyReportSearchCriteria) {
        
        if (radiologyReportSearchCriteria == null) {
            throw new IllegalArgumentException("radiologyReportSearchCriteria cannot be null");
        }
        return radiologyReportDAO.getRadiologyReports(radiologyReportSearchCriteria);
    }
}
