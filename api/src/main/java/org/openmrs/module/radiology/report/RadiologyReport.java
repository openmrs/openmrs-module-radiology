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
 * RadiologyReport represents a radiology report for a RadiologyOrder
 */
public class RadiologyReport extends BaseOpenmrsData {
	
	private Integer radiologyReportId;
	
	private RadiologyOrder radiologyOrder;
	
	private Date reportDate;
	
	private Provider principalResultsInterpreter;
	
	private RadiologyReportStatus reportStatus;
	
	private String reportBody;
	
	protected RadiologyReport() {
		
	}
	
	public RadiologyReport(RadiologyOrder radiologyOrder) {
		super.setDateCreated(new Date());
		this.radiologyOrder = radiologyOrder;
		this.reportStatus = RadiologyReportStatus.CLAIMED;
	}
	
	public RadiologyOrder getRadiologyOrder() {
		return radiologyOrder;
	}
	
	public void setRadiologyOrder(RadiologyOrder radiologyOrder) {
		this.radiologyOrder = radiologyOrder;
	}
	
	public Provider getPrincipalResultsInterpreter() {
		return principalResultsInterpreter;
	}
	
	public void setPrincipalResultsInterpreter(Provider principalResultsInterpreter) {
		this.principalResultsInterpreter = principalResultsInterpreter;
	}
	
	@Override
	public Integer getId() {
		return getRadiologyReportId();
	}
	
	@Override
	public void setId(Integer radiologyReportId) {
		setRadiologyReportId(radiologyReportId);
	}
	
	private Integer getRadiologyReportId() {
		return this.radiologyReportId;
	}
	
	private void setRadiologyReportId(Integer radiologyReportId) {
		this.radiologyReportId = radiologyReportId;
	}
	
	public Date getReportDate() {
		return reportDate;
	}
	
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	
	public RadiologyReportStatus getReportStatus() {
		return reportStatus;
	}
	
	public void setReportStatus(RadiologyReportStatus reportStatus) {
		this.reportStatus = reportStatus;
	}
	
	public String getReportBody() {
		return reportBody;
	}
	
	public void setReportBody(String reportBody) {
		this.reportBody = reportBody;
	}
}
