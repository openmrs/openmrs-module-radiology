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
import org.openmrs.module.radiology.RadiologyOrder;

/**
 * RadiologyReport represents a radiology report written by a Provider for a RadiologyOrder once the
 * order is completed
 */
public class RadiologyReport extends BaseOpenmrsData {
	
	private Integer radiologyReportId;
	
	private RadiologyOrder radiologyOrder;
	
	private Date reportDate;
	
	private Provider principalResultsInterpreter;
	
	private RadiologyReportStatus reportStatus;
	
	private String reportBody;
	
	/**
	 * Instantiate a RadiologyReport
	 */
	private RadiologyReport() {
		// needed by hibernate to instantiate a bean
	}
	
	/**
	 * Instantiate a RadiologyReport for given RadiologyOrder
	 * 
	 * @param radiologyOrder RadiologyOrder which is being/was reported
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
		this.reportStatus = RadiologyReportStatus.CLAIMED;
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
	 * Get radiologyReportId of RadiologyReport.
	 * 
	 * @return radiologyReportId of RadiologyReport
	 */
	@Override
	public Integer getId() {
		return getRadiologyReportId();
	}
	
	/**
	 * Set radiologyReportId of RadiologyReport.
	 * 
	 * @param radiologyReportId Id of RadiologyReport
	 */
	@Override
	public void setId(Integer radiologyReportId) {
		setRadiologyReportId(radiologyReportId);
	}
	
	/**
	 * Get radiologyReportId of RadiologyReport.
	 * 
	 * @return radiologyReportId of RadiologyReport
	 */
	private Integer getRadiologyReportId() {
		return this.radiologyReportId;
	}
	
	/**
	 * Set radiologyReportId of RadiologyReport.
	 * 
	 * @param radiologyReportId Id of RadiologyReport
	 */
	private void setRadiologyReportId(Integer radiologyReportId) {
		this.radiologyReportId = radiologyReportId;
	}
	
	/**
	 * Get reportDate of RadiologyReport.
	 * 
	 * @return reportDate of RadiologyReport
	 */
	public Date getReportDate() {
		return reportDate;
	}
	
	/**
	 * Set reportDate of RadiologyReport.
	 * 
	 * @param reportDate date of RadiologyReport
	 */
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	
	/**
	 * Get reportStatus of RadiologyReport.
	 * 
	 * @return reportStatus of RadiologyReport
	 */
	public RadiologyReportStatus getReportStatus() {
		return reportStatus;
	}
	
	/**
	 * Set reportStatus of RadiologyReport.
	 * 
	 * @param reportStatus status of RadiologyReport
	 */
	public void setReportStatus(RadiologyReportStatus reportStatus) {
		this.reportStatus = reportStatus;
	}
	
	/**
	 * Get reportBody of RadiologyReport.
	 * 
	 * @return reportBody of RadiologyReport
	 */
	public String getReportBody() {
		return reportBody;
	}
	
	/**
	 * Set reportBody of RadiologyReport.
	 * 
	 * @param reportBody body of RadiologyReport
	 */
	public void setReportBody(String reportBody) {
		this.reportBody = reportBody;
	}
}
