/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import java.lang.reflect.Field;

/**
 * A class that supports on openmrs's orders to make the module DICOM compatible, corresponds to the
 * table order_dicom_complment
 */
public class Study {
	
	private Integer studyId;
	
	private String studyInstanceUid;
	
	private RadiologyOrder radiologyOrder;
	
	private ScheduledProcedureStepStatus scheduledStatus;
	
	private PerformedProcedureStepStatus performedStatus;
	
	private Modality modality;
	
	private MwlStatus mwlStatus = MwlStatus.DEFAULT;
	
	public Integer getStudyId() {
		return studyId;
	}
	
	public Modality getModality() {
		return modality;
	}
	
	public RadiologyOrder getRadiologyOrder() {
		return radiologyOrder;
	}
	
	public PerformedProcedureStepStatus getPerformedStatus() {
		return performedStatus;
	}
	
	public ScheduledProcedureStepStatus getScheduledStatus() {
		return scheduledStatus;
	}
	
	public String getStudyInstanceUid() {
		return studyInstanceUid;
	}
	
	public MwlStatus getMwlStatus() {
		return mwlStatus;
	}
	
	public boolean isCompleted() {
		return performedStatus == PerformedProcedureStepStatus.COMPLETED;
	}
	
	public boolean isScheduleable() {
		return performedStatus == null;
	}
	
	public void setMwlStatus(MwlStatus mwlStatus) {
		this.mwlStatus = mwlStatus;
	}
	
	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}
	
	public void setModality(Modality modality) {
		this.modality = modality;
	}
	
	public void setRadiologyOrder(RadiologyOrder radiologyOrder) {
		this.radiologyOrder = radiologyOrder;
	}
	
	public void setPerformedStatus(PerformedProcedureStepStatus performedStatus) {
		this.performedStatus = performedStatus;
	}
	
	public void setScheduledStatus(ScheduledProcedureStepStatus scheduledStatus) {
		this.scheduledStatus = scheduledStatus;
	}
	
	public void setStudyInstanceUid(String studyInstanceUid) {
		this.studyInstanceUid = studyInstanceUid;
	}
	
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				buff.append(field.getName()).append(": ").append(field.get(this)).append(" ");
			}
			catch (IllegalAccessException ex) {}
			catch (IllegalArgumentException ex) {}
		}
		return buff.toString();
	}
}
