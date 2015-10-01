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
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderContext;
import org.openmrs.api.OrderService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.DicomUtils;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.db.RadiologyOrderDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.springframework.transaction.annotation.Transactional;

public class RadiologyServiceImpl extends BaseOpenmrsService implements RadiologyService {
	
	private static final Log log = LogFactory.getLog(RadiologyServiceImpl.class);
	
	private RadiologyOrderDAO radiologyOrderDAO;
	
	private StudyDAO sdao;
	
	private OrderService orderService;
	
	private EncounterService encounterService;
	
	@Override
	public void setRadiologyOrderDao(RadiologyOrderDAO radiologyOrderDAO) {
		this.radiologyOrderDAO = radiologyOrderDAO;
	}
	
	@Override
	public void setSdao(StudyDAO dao) {
		this.sdao = dao;
	}
	
	@Override
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
	
	@Override
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrder(RadiologyOrder)
	 */
	@Transactional
	@Override
	public RadiologyOrder placeRadiologyOrder(RadiologyOrder radiologyOrder) {
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder is required");
		}
		
		if (radiologyOrder.getOrderId() != null) {
			throw new IllegalArgumentException("Cannot edit an existing order!");
		}
		
		if (radiologyOrder.getStudy() == null) {
			throw new IllegalArgumentException("radiologyOrder.study is required");
		}
		
		if (radiologyOrder.getStudy().getModality() == null) {
			throw new IllegalArgumentException("radiologyOrder.study.modality is required");
		}
		
		Encounter encounter = saveRadiologyOrderEncounter(radiologyOrder.getPatient(), radiologyOrder.getOrderer(),
		    new Date());
		encounter.addOrder(radiologyOrder);
		
		OrderContext orderContext = new OrderContext();
		orderContext.setCareSetting(RadiologyProperties.getRadiologyCareSetting());
		orderContext.setOrderType(RadiologyProperties.getRadiologyTestOrderType());
		
		RadiologyOrder result = (RadiologyOrder) orderService.saveOrder(radiologyOrder, orderContext);
		saveStudy(result.getStudy());
		return result;
	}
	
	/**
	 * Save radiology order encounter for given parameters
	 * 
	 * @param patient the encounter patient
	 * @param provider the encounter provider
	 * @param encounterDateTime the encounter date
	 * @return radiology order encounter for given parameters
	 * @should save radiology order encounter for given parameters
	 */
	@Transactional
	private Encounter saveRadiologyOrderEncounter(Patient patient, Provider provider, Date encounterDateTime) {
		
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setEncounterType(RadiologyProperties.getRadiologyEncounterType());
		encounter.setProvider(RadiologyProperties.getOrderingProviderEncounterRole(), provider);
		encounter.setEncounterDatetime(encounterDateTime);
		
		return encounterService.saveEncounter(encounter);
	}
	
	/**
	 * <p>
	 * Save the given <code>Study</code> to the database
	 * </p>
	 * Additionally, study and study.order information are written into a DICOM xml file.
	 * 
	 * @param study study to be created or updated
	 * @return study who was created or updated
	 * @should create new study from given study object
	 * @should update existing study
	 */
	@Transactional
	private Study saveStudy(Study study) {
		
		RadiologyOrder order = study.getRadiologyOrder();
		
		if (study.getScheduledStatus() == null && order.getScheduledDate() != null) {
			study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
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
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder, Provider, Date, String)
	 */
	@Transactional
	@Override
	public Order discontinueRadiologyOrder(RadiologyOrder radiologyOrderToDiscontinue, Provider orderer,
	        Date discontinueDate, String nonCodedDiscontinueReason) throws Exception {
		
		if (radiologyOrderToDiscontinue == null) {
			throw new IllegalArgumentException("radiologyOrder is required");
		}
		
		if (radiologyOrderToDiscontinue.getOrderId() == null) {
			throw new IllegalArgumentException("orderId is null");
		}
		
		if (radiologyOrderToDiscontinue.isActive() == false) {
			throw new IllegalArgumentException("order is not active");
		}
		
		if (orderer == null) {
			throw new IllegalArgumentException("provider is required");
		}
		
		Encounter encounter = saveRadiologyOrderEncounter(radiologyOrderToDiscontinue.getPatient(), orderer, discontinueDate);
		
		return orderService.discontinueOrder(radiologyOrderToDiscontinue, nonCodedDiscontinueReason, discontinueDate,
		    orderer, encounter);
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
	 * @see RadiologyService#updateStudyPerformedStatus(String, PerformedProcedureStepStatus)
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
		
		Study studyToBeUpdated = sdao.getStudyByStudyInstanceUid(studyInstanceUid);
		studyToBeUpdated.setPerformedStatus(performedStatus);
		return sdao.saveStudy(studyToBeUpdated);
	}
	
	@Override
	public void sendModalityWorklist(RadiologyOrder radiologyOrder, OrderRequest orderRequest) {
		MwlStatus mwlStatus = radiologyOrder.getStudy().getMwlStatus();
		String hl7blob = DicomUtils.createHL7Message(radiologyOrder, orderRequest);
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
		radiologyOrder.getStudy().setMwlStatus(mwlStatus);
		saveStudy(radiologyOrder.getStudy());
	}
	
	@Transactional(readOnly = true)
	@Override
	public Study getStudy(Integer id) {
		return sdao.getStudy(id);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Study getStudyByOrderId(Integer orderId) {
		if (orderId == null) {
			throw new IllegalArgumentException("orderId is required");
		}
		
		return sdao.getStudyByOrderId(orderId);
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
