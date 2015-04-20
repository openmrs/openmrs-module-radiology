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
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.RequestedProcedurePriority;
import org.openmrs.module.radiology.Roles;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Utils;
import org.openmrs.module.radiology.Visit;
import org.openmrs.module.radiology.db.StudyDAO;
import org.openmrs.module.radiology.db.VisitDAO;
import org.springframework.transaction.annotation.Transactional;

public class RadiologyServiceImpl extends BaseOpenmrsService implements RadiologyService {
	
	private StudyDAO sdao;
	
	private VisitDAO vdao;
	
	private static final Log log = LogFactory.getLog(RadiologyServiceImpl.class);
	
	public void setSdao(StudyDAO dao) {
		this.sdao = dao;
	}
	
	public void setVdao(VisitDAO vdao) {
		this.vdao = vdao;
	}
	
	/**
	 * @see RadiologyService#getStudy(Integer)
	 */
	@Transactional(readOnly = true)
	public Study getStudy(Integer id) {
		if (id == null) {
			throw new IllegalArgumentException("id is required");
		}
		
		return sdao.getStudy(id);
	}
	
	/**
	 * @see RadiologyService#getStudyByOrderId(Integer)
	 */
	@Transactional(readOnly = true)
	public Study getStudyByOrderId(Integer id) {
		
		return sdao.getStudyByOrderId(id);
	}
	
	/**
	 * @see RadiologyService#getStudyByOrder(Order)
	 */
	@Transactional(readOnly = true)
	public Study getStudyByOrder(Order order) {
		if (order == null) {
			throw new IllegalArgumentException("order is required");
		}
		
		return sdao.getStudyByOrder(order);
	}
	
	/**
	 * @see RadiologyService#getStudyByUid(String)
	 */
	@Transactional(readOnly = true)
	public Study getStudyByUid(String uid) {
		if (uid == null) {
			throw new IllegalArgumentException("uid is required");
		}
		
		return sdao.getStudyByUid(uid);
	}
	
	/**
	 * @see RadiologyService#getStudiesByPatient(Patient)
	 */
	@Transactional(readOnly = true)
	public List<Study> getStudiesByPatient(Patient patient) {
		if (patient == null) {
			throw new IllegalArgumentException("patient is required");
		}
		
		return sdao.getStudiesByPatient(patient);
	}
	
	/**
	 * @see RadiologyService#getStudiesByOrders(List<Order>)
	 */
	@Transactional(readOnly = true)
	public List<Study> getStudiesByOrders(List<Order> orders) {
		if (orders == null) {
			throw new IllegalArgumentException("orders are required");
		}
		
		return sdao.getStudiesByOrders(orders);
	}
	
	/**
	 * @see RadiologyService#getObservationsByStudy(Study)
	 */
	@Transactional(readOnly = true)
	public List<Obs> getObservationsByStudy(Study study) {
		if (study == null) {
			throw new IllegalArgumentException("study is required");
		}
		
		return sdao.getObservationsByStudy(study);
	}
	
	/**
	 * @see RadiologyService#saveStudy(Study)
	 */
	@Transactional
	public Study saveStudy(Study studyToBeSaved) {
		if (studyToBeSaved == null) {
			throw new IllegalArgumentException("study is required");
		}
		
		if (studyToBeSaved.getOrder() == null) {
			throw new APIException("Study.order.required");
		}
		
		if (studyToBeSaved.getModality() == null) {
			throw new APIException("Study.modality.required");
		}
		
		if (studyToBeSaved.getPriority() == null) {
			studyToBeSaved.setPriority(RequestedProcedurePriority.ROUTINE);
		}
		
		if (studyToBeSaved.getScheduledStatus() == null && studyToBeSaved.getOrder().getStartDate() != null) {
			studyToBeSaved.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		}
		
		User orderer = studyToBeSaved.getOrder().getOrderer();
		if (orderer != null) {
			if (orderer.hasRole(Roles.Scheduler, true) && studyToBeSaved.getScheduler() == null) {
				if (studyToBeSaved.isScheduleable()) {
					studyToBeSaved.setScheduler(orderer);
				}
			}
			
			if (orderer.hasRole(Roles.PerformingPhysician, true) && studyToBeSaved.getPerformingPhysician() == null) {
				studyToBeSaved.setPerformingPhysician(orderer);
			}
			
			if (orderer.hasRole(Roles.ReadingPhysician, true) && studyToBeSaved.getReadingPhysician() == null) {
				studyToBeSaved.setReadingPhysician(orderer);
			}
		}
		
		try {
			Study savedStudy = sdao.saveStudy(studyToBeSaved);
			String studyUID = Utils.studyPrefix() + savedStudy.getId();
			savedStudy.setUid(studyUID);
			savedStudy = sdao.saveStudy(savedStudy);
			
			File file = new File(Utils.mwlDir(), savedStudy.getId() + ".xml");
			String path = "";
			path = file.getCanonicalPath();
			DicomUtils.write(savedStudy.getOrder(), savedStudy, file);
			log.debug("Order and study saved in " + path);
			return savedStudy;
		}
		catch (Exception e) {
			e.printStackTrace();
			log.warn("Can not save study in openmrs or dmc4che.");
		}
		return null;
	}
	
	/**
	 * @see RadiologyService#updateStudyPerformedStatus(Study, int)
	 */
	@Transactional
	public Study updateStudyPerformedStatus(Study studyToBeUpdated, PerformedProcedureStepStatus performedStatus) {
		if (studyToBeUpdated == null) {
			throw new IllegalArgumentException("study is required");
		}
		
		if (studyToBeUpdated.getId() == null) {
			throw new APIException("Study.cannot.edit.nonexisting");
		}
		
		if (performedStatus == null) {
			throw new IllegalArgumentException("performedStatus is required");
		}
		
		studyToBeUpdated.setPerformedStatus(performedStatus);
		
		return sdao.saveStudy(studyToBeUpdated);
	}
	
	public void sendModalityWorklist(Study s, OrderRequest orderRequest) {
		MwlStatus mwlStatus = s.getMwlStatus();
		String hl7blob = DicomUtils.createHL7Message(s, orderRequest);
		int status = DicomUtils.sendHL7Worklist(hl7blob);
		
		if (status == 1) {
			switch (orderRequest) {
				case Save_Order:
					if (mwlStatus == MwlStatus.DEFAULT || mwlStatus == MwlStatus.OUT_SYNC_SAVE_FAILED) {
						mwlStatus = MwlStatus.IN_SYNC_SAVE_SUCCESS;
					} else {
						mwlStatus = MwlStatus.IN_SYNC_UPDATE_SUCCESS;
					}
					break;
				case Void_Order:
					mwlStatus = MwlStatus.IN_SYNC_VOID_SUCCESS;
					break;
				case Unvoid_Order:
					mwlStatus = MwlStatus.IN_SYNC_UNVOID_SUCCESS;
					break;
				case Discontinue_Order:
					mwlStatus = MwlStatus.IN_SYNC_DISCONTINUE_SUCCESS;
					break;
				case Undiscontinue_Order:
					mwlStatus = MwlStatus.IN_SYNC_UNDISCONTINUE_SUCCESS;
					break;
				case Default:
					mwlStatus = MwlStatus.DEFAULT;
					break;
				default:
					break;
				
			}
			
		} else if (status == 0) {
			switch (orderRequest) {
				case Save_Order:
					if (mwlStatus == MwlStatus.DEFAULT || mwlStatus == MwlStatus.OUT_SYNC_SAVE_FAILED) {
						mwlStatus = MwlStatus.OUT_SYNC_SAVE_FAILED;
					} else {
						mwlStatus = MwlStatus.OUT_SYNC_UPDATE_FAILED;
					}
					break;
				case Void_Order:
					mwlStatus = MwlStatus.OUT_SYNC_VOID_FAILED;
					break;
				case Unvoid_Order:
					mwlStatus = MwlStatus.OUT_SYNC_UNVOID_FAILED;
					break;
				case Discontinue_Order:
					mwlStatus = MwlStatus.OUT_SYNC_DISCONTINUE_FAILED;
					break;
				case Undiscontinue_Order:
					mwlStatus = MwlStatus.OUT_SYNC_UNDISCONTINUE_FAILED;
					break;
				case Default:
					mwlStatus = MwlStatus.DEFAULT;
					break;
				default:
					break;
			}
		}
		updateStudyMwlStatus(s, mwlStatus);
	}
	
	/**
	 * <p>
	 * Update <code>mwlStatus</code> of existing <code>study</code> in the database
	 * </p>
	 * 
	 * @param study Study to be updated
	 * @param mwlStatus MwlStatus to set Study to
	 * @return study which was updated
	 * @should update mwl status of given study in database to given mwl status
	 * @should not update non existing study
	 * @should throw IllegalArgumentException if study is null
	 */
	//TODO(teleivo) is check for non existing study.id != null enough, could study.id be set somewhere other than on saveStudy()
	@Transactional
	Study updateStudyMwlStatus(Study studyToBeUpdated, MwlStatus mwlStatus) {
		if (studyToBeUpdated == null) {
			throw new IllegalArgumentException("study is required");
		}
		
		if (studyToBeUpdated.getId() == null) {
			throw new APIException("Study.cannot.edit.nonexisting");
		}
		
		studyToBeUpdated.setMwlStatus(mwlStatus);
		
		return sdao.saveStudy(studyToBeUpdated);
	}
	
	@Transactional(readOnly = true)
	public Visit getVisit(Integer id) {
		return vdao.getVisit(id);
	}
	
	@Transactional
	public Visit saveVisit(Visit v) {
		return vdao.saveVisit(v);
	}
}
