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
 * 
 */
public class Study {
	
	private int id;
	
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
	
	public Study() {
		super();
	}
	
	public int getId() {
		return id;
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
	
	public void setId(int id) {
		this.id = id;
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
	
	/**
	 * Fills null required values, to the moment orderer set to currentUser.<br/>
	 * Fills radiology order type. Fills concept if null.<br/>
	 * <br/>
	 * In this function goes all validation pre post request: <li>Scheduler is not allowed to
	 * schedule a completed procedure</li>
	 * 
	 * @param o Order to be filled
	 * @param studyId TODO
	 * @return Order modified
	 */
	public boolean setup(Order o, Integer studyId) {
		setId(studyId);
		
		User u = Context.getAuthenticatedUser();
		if (u.hasRole(Roles.ReferringPhysician, true) && o.getOrderer() == null)
			o.setOrderer(u);
		if (u.hasRole(Roles.Scheduler, true) && getScheduler() == null) {
			if (!isScheduleable()) {
				return false;
			} else {
				setScheduler(u);
			}
			
		}
		if (u.hasRole(Roles.PerformingPhysician, true) && getPerformingPhysician() == null)
			setPerformingPhysician(u);
		if (u.hasRole(Roles.ReadingPhysician, true) && getReadingPhysician() == null)
			setReadingPhysician(u);
		
		if (o.getStartDate() != null)
			setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		
		if (o.getOrderer() == null)
			o.setOrderer(u);
		o.setOrderType(Utils.getRadiologyOrderType().get(0));
		if (o.getConcept() == null)
			o.setConcept(Context.getConceptService().getConcept(1));
		return true;
	}
	
	private String statuses(boolean sched, boolean perf) {
		String ret = "";
		String scheduled = "";
		scheduled += ScheduledProcedureStepStatus.getNameOrUnknown(scheduledStatus);
		ret += sched ? scheduled : "";
		String performed = "";
		performed += PerformedProcedureStepStatus.getNameOrUnknown(performedStatus);
		ret += perf ? (sched ? " " : "") + performed : "";
		return ret;
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
