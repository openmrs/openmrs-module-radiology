/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.impl;

import static org.openmrs.module.radiology.RadiologyRoles.PERFORMING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.READING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.SCHEDULER;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.DicomUtils;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.db.GenericDAO;
import org.openmrs.module.radiology.db.RadiologyOrderDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.springframework.transaction.annotation.Transactional;

public class RadiologyServiceImpl extends BaseOpenmrsService implements RadiologyService {
	
	private GenericDAO gdao;
	
	private RadiologyOrderDAO radiologyOrderDAO;
	
	private StudyDAO sdao;
	
	private static final Log log = LogFactory.getLog(RadiologyServiceImpl.class);
	
	@Override
	public void setRadiologyOrderDao(RadiologyOrderDAO radiologyOrderDAO) {
		this.radiologyOrderDAO = radiologyOrderDAO;
	}
	
	@Override
	public void setSdao(StudyDAO dao) {
		this.sdao = dao;
	}
	
	/**
	 * @see RadiologyService#saveRadiologyOrder(RadiologyOrder)
	 */
	@Transactional
	@Override
	public RadiologyOrder saveRadiologyOrder(RadiologyOrder radiologyOrder) {
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder is required");
		}
		
		return radiologyOrderDAO.saveRadiologyOrder(radiologyOrder);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrderByOrderId(Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public RadiologyOrder getRadiologyOrderByOrderId(Integer orderId) {
		if (orderId == null) {
			throw new IllegalArgumentException("orderId is required");
		}
		
		return radiologyOrderDAO.getRadiologyOrderByOrderId(orderId);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatient(Patient)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<RadiologyOrder> getRadiologyOrdersByPatient(Patient patient) {
		if (patient == null) {
			throw new IllegalArgumentException("patient is required");
		}
		
		return radiologyOrderDAO.getRadiologyOrdersByPatient(patient);
	}
	
	/**
	 * @see RadiologyService#getRadiologyOrdersByPatients(List<Patient>)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<RadiologyOrder> getRadiologyOrdersByPatients(List<Patient> patients) {
		if (patients == null) {
			throw new IllegalArgumentException("patients is required");
		}
		
		return radiologyOrderDAO.getRadiologyOrdersByPatients(patients);
	}
	
	/**
	 * @see RadiologyService#saveStudy(Study)
	 */
	@Transactional
	@Override
	public Study saveStudy(Study study) {
		if (study == null) {
			throw new IllegalArgumentException("study is required");
		}
		
		if (study.getOrderId() == null) {
			throw new APIException("Study.order.required");
		}
		
		if (study.getModality() == null) {
			throw new APIException("Study.modality.required");
		}
		
		Order order = study.order();
		
		if (study.getScheduledStatus() == null && order.getStartDate() != null) {
			study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		}
		
		User orderer = order.getOrderer();
		if (orderer != null) {
			if (orderer.hasRole(SCHEDULER, true) && study.getScheduler() == null) {
				if (study.isScheduleable()) {
					study.setScheduler(orderer);
				}
			}
			
			if (orderer.hasRole(PERFORMING_PHYSICIAN, true) && study.getPerformingPhysician() == null) {
				study.setPerformingPhysician(orderer);
			}
			
			if (orderer.hasRole(READING_PHYSICIAN, true) && study.getReadingPhysician() == null) {
				study.setReadingPhysician(orderer);
			}
		}
		
		try {
			Study savedStudy = sdao.saveStudy(study);
			String studyInstanceUid = RadiologyProperties.getStudyPrefix() + savedStudy.getStudyId();
			savedStudy.setStudyInstanceUid(studyInstanceUid);
			savedStudy = sdao.saveStudy(savedStudy);
			
			File file = new File(RadiologyProperties.getMwlDir(), savedStudy.getStudyId() + ".xml");
			String path = "";
			path = file.getCanonicalPath();
			DicomUtils.write(order, savedStudy, file);
			log.debug("Order and study saved in " + path);
			return savedStudy;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			log.warn("Can not save study in openmrs or dmc4che.");
		}
		return null;
	}
	
	@Override
	public void sendModalityWorklist(Study s, OrderRequest orderRequest) {
		Order order = s.order();
		MwlStatus mwlStatus = s.getMwlStatus();
		String hl7blob = DicomUtils.createHL7Message(s, order, orderRequest);
		int status = DicomUtils.sendHL7Worklist(hl7blob);
		
		if (status == 1) {
			switch (orderRequest) {
				case Save_Order:
					if (mwlStatus == MwlStatus.DEFAULT || mwlStatus == MwlStatus.SAVE_ERR) {
						mwlStatus = MwlStatus.SAVE_OK;
					} else {
						mwlStatus = MwlStatus.UPDATE_OK;
					}
					break;
				case Void_Order:
					mwlStatus = MwlStatus.VOID_OK;
					break;
				case Unvoid_Order:
					mwlStatus = MwlStatus.UNVOID_OK;
					break;
				case Discontinue_Order:
					mwlStatus = MwlStatus.DISCONTINUE_OK;
					break;
				case Undiscontinue_Order:
					mwlStatus = MwlStatus.UNDISCONTINUE_OK;
					break;
				default:
					break;
				
			}
			
		} else if (status == 0) {
			switch (orderRequest) {
				case Save_Order:
					if (mwlStatus == MwlStatus.DEFAULT || mwlStatus == MwlStatus.SAVE_ERR) {
						mwlStatus = MwlStatus.SAVE_ERR;
					} else {
						mwlStatus = MwlStatus.UPDATE_ERR;
					}
					break;
				case Void_Order:
					mwlStatus = MwlStatus.VOID_ERR;
					break;
				case Unvoid_Order:
					mwlStatus = MwlStatus.UNVOID_ERR;
					break;
				case Discontinue_Order:
					mwlStatus = MwlStatus.DISCONTINUE_ERR;
					break;
				case Undiscontinue_Order:
					mwlStatus = MwlStatus.UNDISCONTINUE_ERR;
					break;
				default:
					break;
			}
		}
		s.setMwlStatus(mwlStatus);
		saveStudy(s);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Study getStudy(Integer id) {
		return sdao.getStudy(id);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Study getStudyByOrderId(Integer id) {
		return sdao.getStudyByOrderId(id);
	}
	
	@Override
	public void setGdao(GenericDAO dao) {
		this.gdao = dao;
	}
	
	@Override
	public Object get(String query, boolean unique) {
		return gdao.get(query, unique);
	}
	
	@Override
	public GenericDAO db() {
		return gdao;
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 */
	@Transactional(readOnly = true)
	public Study getStudyByStudyInstanceUid(String studyInstanceUid) {
		if (studyInstanceUid == null) {
			throw new IllegalArgumentException("studyInstanceUid is required");
		}
		
		return sdao.getStudyByStudyInstanceUid(studyInstanceUid);
	}
	
	/**
	 * @see RadiologyService#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Study> getStudiesByRadiologyOrders(List<RadiologyOrder> radiologyOrders) {
		if (radiologyOrders == null) {
			throw new IllegalArgumentException("radiologyOrders are required");
		}
		
		List<Study> result = sdao.getStudiesByRadiologyOrders(radiologyOrders);
		return result;
	}
	
	/**
	 * @see RadiologyService#getObsByOrderId(Integer)
	 */
	@Transactional(readOnly = true)
	public List<Obs> getObsByOrderId(Integer orderId) {
		if (orderId == null) {
			throw new IllegalArgumentException("orderId is required");
		}
		
		return sdao.getObsByOrderId(orderId);
	}
	
}
