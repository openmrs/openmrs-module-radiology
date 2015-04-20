/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.db;

import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.module.radiology.Study;

/**
 * radiologyResponse-related database functions
 * 
 * @author Cortex
 * @version 1.0
 */
public interface StudyDAO {
	
	public Study getStudy(Integer id);
	
	/**
	 * Save the given <code>study</code> to the database
	 * 
	 * @param study study to be created or updated
	 * @return study who was created or updated
	 * @should create new study from given study object
	 */
	public Study saveStudy(Study study);
	
	/**
	 * @param orderId the orderId of order corresponding to study
	 * @return the study associated with order matching given orderId, or new Study() if there is no such study
	 */
	public Study getStudyByOrderId(Integer orderId);
	
	/**
	 * NOTE: what to do with above behavior "or new Study() if there is no such study" => good idea?
	 * implement like that??
	 * 
	 * @param order
	 * @return the study for given order
	 */
	public Study getStudyByOrder(Order order);
	
	/**
	 * @param uid
	 * @return the study for given uid
	 */
	public Study getStudyByUid(String uid);
	
	/**
	 * @param patient
	 * @return the studies for given patient
	 */
	public List<Study> getStudiesByPatient(Patient patient);
	
	/**
	 * @param orders
	 * @return the studies for given orders
	 */
	public List<Study> getStudiesByOrders(List<Order> orders);
	
	/**
	 * @param study
	 * @return the obs for given study
	 */
	public List<Obs> getObservationsByStudy(Study study);
	
}
