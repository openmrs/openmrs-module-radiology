/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.study;

import java.util.List;

import org.openmrs.module.radiology.order.RadiologyOrder;

/**
 * Study-related database functions
 * 
 * @see org.openmrs.module.radiology.study.RadiologyStudyService
 */
interface StudyDAO {
	
	/**
	 * @see org.openmrs.module.radiology.study.RadiologyStudyService#saveStudy(Study)
	 */
	public Study saveStudy(Study study);
	
	/**
	 * @see org.openmrs.module.radiology.study.RadiologyStudyService#getStudyByStudyId(Integer)
	 */
	public Study getStudyByStudyId(Integer studyId);
	
	/**
	 * @see org.openmrs.module.radiology.study.RadiologyStudyService#getStudyByOrderId(Integer)
	 */
	public Study getStudyByOrderId(Integer orderId);
	
	/**
	 * @see org.openmrs.module.radiology.study.RadiologyStudyService#getStudyByStudyInstanceUid(String)
	 */
	public Study getStudyByStudyInstanceUid(String studyInstanceUid);
	
	/**
	 * @see org.openmrs.module.radiology.study.RadiologyStudyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 */
	public List<Study> getStudiesByRadiologyOrders(List<RadiologyOrder> radiologyOrders);
	
}
