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
import java.util.ArrayList;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.User;
import org.openmrs.api.context.Context;

/**
 * A class that supports on openmrs's orders to make the module DICOM compatible, corresponds to the
 * table order_dicom_complment
 * 
 * @author Cortex
 */
public class Study {
	
	// Performed Procedure Steps Statuses - Part 3 Annex C.4.14
	public static class PerformedStatuses {
		
		public static final int IN_PROGRESS = 0;
		
		public static final int DISCONTINUED = 1;
		
		public static final int COMPLETED = 2;
		
		public static boolean has(int x) {
			return !(string(x, false).compareTo("UNKNOWN") == 0);
		}
		
		// TODO localized
		public static String string(Integer x, Boolean localized) {
			switch (x) {
				case IN_PROGRESS:
					return localized ? localized("radiology.IN_PROGRESS") : "IN PROGRESS";
				case DISCONTINUED:
					return localized ? localized("radiology.DISCONTINUED") : "DISCONTINUED";
				case COMPLETED:
					return localized ? localized("radiology.COMPLETED") : "COMPLETED";
				default:
					return localized ? localized("general.unknown") : "UNKNOWN";
			}
		}
		
		public static int value(String s) {
			if (s.toLowerCase().contains("progress"))
				return IN_PROGRESS;
			if (s.compareToIgnoreCase("discontinued") == 0)
				return DISCONTINUED;
			if (s.compareToIgnoreCase("completed") == 0)
				return COMPLETED;
			return -1;
		}
	}
	
	public static List<Study> get(List<Order> o) {
		ArrayList<Study> s = new ArrayList<Study>();
		for (Order o1 : o) {
			s.add(get(o1));
		}
		return s;
	}
	
	public static Study get(Order o) {
		return Context.getService(Main.class).getStudyByOrderId(o.getOrderId());
	}
	
	private static String localized(String code) {
		return Context.getMessageSourceService().getMessage(code);
	}
	
	static Main service() {
		return Context.getService(Main.class);
	}
	
	private int id;
	
	private String uid;
	
	private int orderID;
	
	private ScheduledProcedureStepStatus scheduledStatus;
	
	private int performedStatus = -1;
	
	private RequestedProcedurePriority priority = RequestedProcedurePriority.ROUTINE;
	
	private Modality modality;
	
	private int mwlStatus;
	
	private User scheduler;
	
	private User performingPhysician;
	
	private User readingPhysician;
	
	public Study() {
		super();
	}
	
	public Study(int id, String uid, int orderID, ScheduledProcedureStepStatus scheduledStatus, int performedStatus,
	    RequestedProcedurePriority priority, Modality modality, User schedulerUserId, User performingPhysicianUserId,
	    User readingPhysicianUserId) {
		super();
		this.id = id;
		this.uid = uid;
		this.orderID = orderID;
		this.scheduledStatus = scheduledStatus;
		this.performedStatus = performedStatus;
		this.priority = priority;
		this.modality = modality;
		this.scheduler = schedulerUserId;
		this.performingPhysician = performingPhysicianUserId;
		this.readingPhysician = readingPhysicianUserId;
	}
	
	public int getId() {
		return id;
	}
	
	public Modality getModality() {
		return modality;
	}
	
	public int getOrderID() {
		return orderID;
	}
	
	public int getPerformedStatus() {
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
	
	public int getMwlStatus() {
		return mwlStatus;
	}
	
	public boolean isCompleted() {
		return performedStatus == PerformedStatuses.COMPLETED;
	}
	
	public boolean isScheduleable() {
		return !PerformedStatuses.has(performedStatus);
	}
	
	@SuppressWarnings("unchecked")
	public List<Obs> obs() {
		//String query = "from Obs as o where o.order.orderId = " + orderID;
		String innerQuery = "(Select oo.previousVersion from Obs as oo where oo.order.orderId=" + orderID
		        + " and oo.previousVersion IS NOT NULL)";
		String query = "from Obs as o where o.order.orderId = " + orderID + " and o.obsId NOT IN " + innerQuery;
		return (List<Obs>) Context.getService(Main.class).get(query, false);
	}
	
	public Order order() {
		String query = "from Order as o where o.orderId = " + orderID;
		return (Order) Context.getService(Main.class).get(query, true);
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
	
	public String mwlStatus() {
		
		// -1 :Default
		String[] mwlMessages = { "Default ", "In Sync : Save order successful.",
		        "Out of Sync : Save order failed. Try Again!", "In Sync : Update order successful.",
		        "Out of Sync : Update order failed. Try again!", "In Sync : Void order successful.",
		        "Out of Sync : Void order failed. Try again!", "In Sync : Discontinue order successful.",
		        "Out of Sync : Discontinue order failed. Try again!", "In Sync : Undiscontinue order successful.",
		        "Out of Sync : Undiscontinue order failed. Try again!", "In Sync :  Unvoid order successfull",
		        "Out of Sync :  Unvoid order failed. Try again" };
		if (mwlStatus == -1)
			return "Default";
		else
			return mwlMessages[mwlStatus];
	}
	
	public void setMwlStatus(int status) {
		this.mwlStatus = status;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setModality(Modality modality) {
		this.modality = modality;
	}
	
	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}
	
	public void setPerformedStatus(int performedStatus) {
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
		performed += PerformedStatuses.string(performedStatus, true);
		ret += perf ? (sched ? " " : "") + performed : "";
		return ret;
	}
	
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		
		Field[] fields = this.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				buff.append(fields[i].getName()).append(": ").append(fields[i].get(this)).append(" ");
			}
			catch (IllegalAccessException ex) {}
			catch (IllegalArgumentException ex) {}
			
		}
		return buff.toString();
	}
}
