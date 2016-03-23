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

import org.openmrs.TestOrder;

/**
 * RadiologyOrder represents a radiology examination
 */
public class RadiologyOrder extends TestOrder {
	
	private Study study;
	
	public Study getStudy() {
		return study;
	}
	
	/**
	 * Set the Order.study to the given Study. Keeps the bi-directional (one-to-one) association
	 * between RadiologyOrder and Study in sync.
	 *
	 * @param study study which should be associated with this radiology order
	 * @should set the study attribute to given study
	 * @should set the radiology order of given study to this radiology order
	 * @should not fail given null
	 */
	public void setStudy(Study study) {
		if (study != null) {
			study.setRadiologyOrder(this);
		}
		this.study = study;
	}
	
	/**
	 * Returns true when this RadiologyOrder has a completed Study and false otherwise.
	 * 
	 * @return true if order has completed study and false otherwise
	 * @should return false if associated study is null
	 * @should return false if associated study is not completed
	 * @should return true if associated study is completed
	 */
	public boolean isCompleted() {
		
		if (this.study == null) {
			return false;
		} else {
			return this.study.isCompleted();
		}
	}
	
	/**
	 * Returns true when this RadiologyOrder does not have a completed Study and false otherwise.
	 * 
	 * @return true if order has no completed study and false otherwise
	 * @should return true if associated study is null
	 * @should return true if associated study is not completed
	 * @should return false if associated study is completed
	 */
	public boolean isNotCompleted() {
		
		return !this.isCompleted();
	}
}
