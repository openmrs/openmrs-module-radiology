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

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Provider;
import org.openmrs.module.radiology.order.RadiologyOrder;

/**
 * RadiologyReport represents a radiology report written by a Provider for a RadiologyOrder once the
 * order is completed
 */
public class RadiologyReport extends BaseOpenmrsData {
    
    
    private Integer reportId;
    
    private RadiologyOrder radiologyOrder;
    
    private Date date;
    
    private Provider principalResultsInterpreter;
    
    private RadiologyReportStatus status;
    
    private String body;
    
    /**
     * Creates a new instance of {@link RadiologyReport}.
     */
    private RadiologyReport() {
        // needed by hibernate to instantiate a bean
    }
    
    /**
     * Creates a new instance of {@link RadiologyReport} for given RadiologyOrder.
     * 
     * @param radiologyOrder the radiology order which is being/was reported
     * @throws IllegalArgumentException if given radiology order is null
     * @throws IllegalArgumentException if given radiology order is not completed
     * @should set radiology order to given radiology order and report status to claimed
     * @should throw an illegal argument exception if given radiology order is null
     * @should throw an illegal argument exception if given radiology order is not completed
     */
    public RadiologyReport(RadiologyOrder radiologyOrder) {
        
        if (radiologyOrder == null) {
            throw new IllegalArgumentException("radiologyOrder cannot be null");
        }
        
        if (radiologyOrder.isNotCompleted()) {
            throw new IllegalArgumentException("radiologyOrder is not completed");
        }
        
        this.radiologyOrder = radiologyOrder;
        this.status = RadiologyReportStatus.DRAFT;
    }
    
    /**
     * Get RadiologyOrder which is being/was reported.
     * 
     * @return RadiologyOrder which is being/was reported
     */
    public RadiologyOrder getRadiologyOrder() {
        return radiologyOrder;
    }
    
    /**
     * Set RadiologyOrder which is being/was reported.
     * 
     * @param radiologyOrder RadiologyOrder which is being/was reported
     */
    public void setRadiologyOrder(RadiologyOrder radiologyOrder) {
        this.radiologyOrder = radiologyOrder;
    }
    
    /**
     * Get Provider which is the report author.
     * 
     * @return Provider which is the report author
     */
    public Provider getPrincipalResultsInterpreter() {
        return principalResultsInterpreter;
    }
    
    /**
     * Set Provider which is the report author.
     * 
     * @param principalResultsInterpreter Provider which is the report author
     */
    public void setPrincipalResultsInterpreter(Provider principalResultsInterpreter) {
        this.principalResultsInterpreter = principalResultsInterpreter;
    }
    
    /**
     * Get reportId of RadiologyReport.
     * 
     * @return reportId of RadiologyReport
     */
    @Override
    public Integer getId() {
        return getReportId();
    }
    
    /**
     * Set reportId of RadiologyReport.
     * 
     * @param reportId the id of the RadiologyReport
     */
    @Override
    public void setId(Integer reportId) {
        setReportId(reportId);
    }
    
    /**
     * Get reportId of RadiologyReport.
     * 
     * @return reportId of RadiologyReport
     */
    public Integer getReportId() {
        return this.reportId;
    }
    
    /**
     * Set reportId of RadiologyReport.
     * 
     * @param reportId Id of RadiologyReport
     */
    private void setReportId(Integer reportId) {
        this.reportId = reportId;
    }
    
    /**
     * Get date of RadiologyReport.
     * 
     * @return date of RadiologyReport
     */
    public Date getDate() {
        return date;
    }
    
    /**
     * Set date of RadiologyReport.
     * 
     * @param date date of RadiologyReport
     */
    public void setDate(Date date) {
        this.date = date;
    }
    
    /**
     * Get status of RadiologyReport.
     * 
     * @return status of RadiologyReport
     */
    public RadiologyReportStatus getStatus() {
        return status;
    }
    
    /**
     * Set status of RadiologyReport.
     * 
     * @param status status of RadiologyReport
     */
    public void setStatus(RadiologyReportStatus status) {
        this.status = status;
    }
    
    /**
     * Get body of RadiologyReport.
     * 
     * @return body of RadiologyReport
     */
    public String getBody() {
        return body;
    }
    
    /**
     * Set body of RadiologyReport.
     * 
     * @param body body of RadiologyReport
     */
    public void setBody(String body) {
        this.body = body;
    }
}
