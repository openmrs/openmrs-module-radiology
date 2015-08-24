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

import org.openmrs.Order;
import org.openmrs.User;
import org.openmrs.api.context.Context;

/**
 * A class that supports on openmrs's orders to make the module DICOM compatible, corresponds to the
 * table order_dicom_complment
 */
public class Study {
	
	private Integer studyId;
	
	private String studyInstanceUid;
	
	private Integer orderId;
	
	private ScheduledProcedureStepStatus scheduledStatus;
	
	private PerformedProcedureStepStatus performedStatus;
	
	private RequestedProcedurePriority priority = RequestedProcedurePriority.ROUTINE;
	
	private Modality modality;
	
	private MwlStatus mwlStatus = MwlStatus.DEFAULT;
	
	private User scheduler;
	
	private User performingPhysician;
	
	private User readingPhysician;
	
	public Integer getStudyId() {
		return studyId;
	}
	
	public Modality getModality() {
		return modality;
	}
	
	public Integer getOrderId() {
		return orderId;
	}
	
	public PerformedProcedureStepStatus getPerformedStatus() {
		return performedStatus;
	}
	
	public User getPerformingPhysician() {
		return performingPhysician;
	}
	
	public RequestedProcedurePriority getPriority() {
		return priority;
	}
	
	public User getReadingPhysician() {
		return readingPhysician;
	}
	
	public ScheduledProcedureStepStatus getScheduledStatus() {
		return scheduledStatus;
	}
	
	public User getScheduler() {
		return scheduler;
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
	
	public Order order() {
		String query = "from Order as o where o.orderId = " + orderId;
		return (Order) Context.getService(RadiologyService.class).get(query, true);
	}
	
	public String performing() {
		return getPerformingPhysician() == null ? "" : getPerformingPhysician().getPersonName().getFullName();
	}
	
	public String reading() {
		return getReadingPhysician() == null ? "" : getReadingPhysician().getPersonName().getFullName();
	}
	
	public String scheduler() {
		return getScheduler() == null ? "" : getScheduler().getPersonName().getFullName();
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
	
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	public void setPerformedStatus(PerformedProcedureStepStatus performedStatus) {
		this.performedStatus = performedStatus;
	}
	
	public void setPerformingPhysician(User performingPhysician) {
		this.performingPhysician = performingPhysician;
	}
	
	public void setPriority(RequestedProcedurePriority priority) {
		this.priority = priority;
	}
	
	public void setReadingPhysician(User readingPhysician) {
		this.readingPhysician = readingPhysician;
	}
	
	public void setScheduledStatus(ScheduledProcedureStepStatus scheduledStatus) {
		this.scheduledStatus = scheduledStatus;
	}
	
	public void setScheduler(User scheduler) {
		this.scheduler = scheduler;
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
