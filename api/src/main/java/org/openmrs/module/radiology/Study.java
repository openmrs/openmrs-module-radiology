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

import org.openmrs.Order;
import org.openmrs.User;

/**
 * A class that supports on openmrs's orders to make the module DICOM compatible, corresponds to the
 * table order_dicom_complment
 * 
 * @author Cortex
 */
public class Study {
	
	private Integer id;
	
	private String uid;
	
	private Order order;
	
	private ScheduledProcedureStepStatus scheduledStatus;
	
	private PerformedProcedureStepStatus performedStatus;
	
	private RequestedProcedurePriority priority;
	
	private Modality modality;
	
	private MwlStatus mwlStatus = MwlStatus.DEFAULT;
	
	private User scheduler;
	
	private User performingPhysician;
	
	private User readingPhysician;
	
	public Study() {
		super();
	}
	
	public Study(Integer id, String uid, Order order, ScheduledProcedureStepStatus scheduledStatus,
	    PerformedProcedureStepStatus performedStatus, RequestedProcedurePriority priority, Modality modality,
	    User schedulerUserId, User performingPhysicianUserId, User readingPhysicianUserId) {
		super();
		this.id = id;
		this.uid = uid;
		this.order = order;
		this.scheduledStatus = scheduledStatus;
		this.performedStatus = performedStatus;
		this.priority = priority;
		this.modality = modality;
		this.scheduler = schedulerUserId;
		this.performingPhysician = performingPhysicianUserId;
		this.readingPhysician = readingPhysicianUserId;
	}
	
	public Integer getId() {
		return id;
	}
	
	public Modality getModality() {
		return modality;
	}
	
	public Order getOrder() {
		return order;
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
	
	public String getStatus(User u) {
		if (u.hasRole(Roles.ReferringPhysician, true))
			return statuses(true, true);
		if (u.hasRole(Roles.Scheduler, true))
			return statuses(true, false);
		if (u.hasRole(Roles.PerformingPhysician, true))
			return statuses(true, true);
		if (u.hasRole(Roles.ReadingPhysician, true))
			return statuses(false, true);
		return statuses(true, true);
	}
	
	public String getUid() {
		return uid;
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
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setModality(Modality modality) {
		this.modality = modality;
	}
	
	public void setOrder(Order order) {
		this.order = order;
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
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	private String statuses(boolean sched, boolean perf) {
		String ret = "";
		String scheduled = "";
		scheduled += ScheduledProcedureStepStatus.getDisplayNameOrUnknown(scheduledStatus);
		ret += sched ? scheduled : "";
		String performed = "";
		performed += PerformedProcedureStepStatus.getDisplayNameOrUnknown(performedStatus);
		ret += perf ? (sched ? " " : "") + performed : "";
		return ret;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Study. id: " + id + " uid: " + uid + " order: " + order;
	}
}
