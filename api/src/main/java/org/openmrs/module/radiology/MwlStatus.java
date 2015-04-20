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

/**
 * <p>
 * Represents custom MWL Status Codes, which help determine what sync status of the order is in.
 * </p>
 */
public enum MwlStatus {
	
	DEFAULT("Default"), IN_SYNC_SAVE_SUCCESS("In Sync : Save order successful."), OUT_SYNC_SAVE_FAILED(
	        "Out of Sync : Save order failed. Try Again!"), IN_SYNC_UPDATE_SUCCESS("In Sync : Update order successful."), OUT_SYNC_UPDATE_FAILED(
	        "Out of Sync : Update order failed. Try again!"), IN_SYNC_VOID_SUCCESS("In Sync : Void order successful."), OUT_SYNC_VOID_FAILED(
	        "Out of Sync : Void order failed. Try again!"), IN_SYNC_DISCONTINUE_SUCCESS(
	        "In Sync : Discontinue order successful."), OUT_SYNC_DISCONTINUE_FAILED(
	        "Out of Sync : Discontinue order failed. Try again!"), IN_SYNC_UNDISCONTINUE_SUCCESS(
	        "In Sync : Undiscontinue order successful."), OUT_SYNC_UNDISCONTINUE_FAILED(
	        "Out of Sync : Undiscontinue order failed. Try again!"), IN_SYNC_UNVOID_SUCCESS(
	        "In Sync :  Unvoid order successfull"), OUT_SYNC_UNVOID_FAILED("Out of Sync :  Unvoid order failed. Try again");
	
	final private String displayName;
	
	MwlStatus(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
}
