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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class RadiologyStudyServiceImpl extends BaseOpenmrsService implements RadiologyStudyService {
	
	private static final Log log = LogFactory.getLog(RadiologyStudyServiceImpl.class);
	
	@Autowired
	private StudyDAO studyDAO;
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	/**
	 * @see RadiologyStudyService#saveStudy(Study)
	 */
	@Override
	@Transactional
	public Study saveStudy(Study study) {
		
		final RadiologyOrder order = study.getRadiologyOrder();
		
		if (study.getScheduledStatus() == null && order.getScheduledDate() != null) {
			study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		}
		
		try {
			Study savedStudy = studyDAO.saveStudy(study);
			final String studyInstanceUid = radiologyProperties.getStudyPrefix() + savedStudy.getStudyId();
			savedStudy.setStudyInstanceUid(studyInstanceUid);
			savedStudy = studyDAO.saveStudy(savedStudy);
			return savedStudy;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			log.warn("Can not save study in openmrs or dmc4che.");
		}
		return null;
	}
	
	/**
	 * @see RadiologyStudyService#updateStudyPerformedStatus(String, PerformedProcedureStepStatus)
	 */
	@Transactional
	@Override
	public Study updateStudyPerformedStatus(String studyInstanceUid, PerformedProcedureStepStatus performedStatus)
			throws IllegalArgumentException {
		
		if (studyInstanceUid == null) {
			throw new IllegalArgumentException("studyInstanceUid is required");
		}
		
		if (performedStatus == null) {
			throw new IllegalArgumentException("performedStatus is required");
		}
		
		final Study studyToBeUpdated = studyDAO.getStudyByStudyInstanceUid(studyInstanceUid);
		studyToBeUpdated.setPerformedStatus(performedStatus);
		return studyDAO.saveStudy(studyToBeUpdated);
	}
	
	/**
	 * @see RadiologyStudyService#getStudyByStudyId(Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public Study getStudyByStudyId(Integer studyId) {
		return studyDAO.getStudyByStudyId(studyId);
	}
	
	/**
	 * @see RadiologyStudyService#getStudyByOrderId(Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public Study getStudyByOrderId(Integer orderId) {
		if (orderId == null) {
			throw new IllegalArgumentException("orderId is required");
		}
		
		return studyDAO.getStudyByOrderId(orderId);
	}
	
	/**
	 * @see RadiologyStudyService#getStudyByStudyInstanceUid(String)
	 */
	@Transactional(readOnly = true)
	public Study getStudyByStudyInstanceUid(String studyInstanceUid) {
		if (studyInstanceUid == null) {
			throw new IllegalArgumentException("studyInstanceUid is required");
		}
		
		return studyDAO.getStudyByStudyInstanceUid(studyInstanceUid);
	}
	
	/**
	 * @see RadiologyStudyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Study> getStudiesByRadiologyOrders(List<RadiologyOrder> radiologyOrders) {
		if (radiologyOrders == null) {
			throw new IllegalArgumentException("radiologyOrders are required");
		}
		
		final List<Study> result = studyDAO.getStudiesByRadiologyOrders(radiologyOrders);
		return result;
	}
}
