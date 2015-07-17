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
import org.openmrs.module.radiology.Study;

/**
 * radiologyResponse-related database functions
 * 
 */
public interface StudyDAO {
	
	public Study getStudy(Integer id);
	
	/**
	 * Save the given <code>Study</code> to the database
	 * 
	 * @param study study to be created or updated
	 * @return study who was created or updated
	 */
	public Study saveStudy(Study study);
	
	/**
	 * @param orderId
	 * @return the study matching orderId, or new Study() if there is no such study
	 */
	public Study getStudyByOrderId(Integer orderId);
	
	/**
	 * @param studyInstanceUid
	 * @return the study for given studyInstanceUid
	 */
	public Study getStudyByStudyInstanceUid(String studyInstanceUid);
	
	/**
	 * @param orders
	 * @return studies for given orders
	 */
	public List<Study> getStudiesByOrders(List<Order> orders);
	
	/**
	 * @param orderId
	 * @return obs for given orderId
	 */
	public List<Obs> getObsByOrderId(Integer orderId);
	
}
