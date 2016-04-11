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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderContext;
import org.openmrs.api.OrderService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emrapi.encounter.EmrEncounterService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.db.RadiologyOrderDAO;
import org.openmrs.module.radiology.db.RadiologyReportDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.openmrs.module.radiology.hl7.util.HL7Sender;
import org.openmrs.module.radiology.hl7.v231.code.OrderControlElement;
import org.openmrs.module.radiology.hl7.v231.message.RadiologyORMO01;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.hl7v2.HL7Exception;

class RadiologyServiceImpl extends BaseOpenmrsService implements RadiologyService {
	
	private static final Log log = LogFactory.getLog(RadiologyServiceImpl.class);
	
	@Autowired
	private RadiologyOrderDAO radiologyOrderDAO;
	
	@Autowired
	private StudyDAO studyDAO;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private EncounterService encounterService;
	
	@Autowired
	private EmrEncounterService emrEncounterService;
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	@Autowired
	private RadiologyReportDAO radiologyReportDAO;
	
	public void setRadiologyOrderDao(RadiologyOrderDAO radiologyOrderDAO) {
		this.radiologyOrderDAO = radiologyOrderDAO;
	}
	
	public void setStudyDAO(StudyDAO studyDAO) {
		this.studyDAO = studyDAO;
	}
	
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
	
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}
	
	public void setEmrEncounterService(EmrEncounterService emrEncounterService) {
		this.emrEncounterService = emrEncounterService;
	}
	
	public void setRadiologyProperties(RadiologyProperties radiologyProperties) {
		this.radiologyProperties = radiologyProperties;
	}
	
	public void setRadiologyReportDAO(RadiologyReportDAO radiologyReportDao) {
		this.radiologyReportDAO = radiologyReportDao;
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
		
		if (radiologyOrder.getStudy()
				.getModality() == null) {
			throw new IllegalArgumentException("radiologyOrder.study.modality is required");
		}
		
		final Encounter encounter = saveRadiologyOrderEncounter(radiologyOrder.getPatient(), radiologyOrder.getOrderer(),
			new Date());
		encounter.addOrder(radiologyOrder);
		
		OrderContext orderContext = new OrderContext();
		orderContext.setCareSetting(radiologyProperties.getRadiologyCareSetting());
		orderContext.setOrderType(radiologyProperties.getRadiologyTestOrderType());
		
		final RadiologyOrder result = (RadiologyOrder) orderService.saveOrder(radiologyOrder, orderContext);
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
	 * @should create radiology order encounter attached to existing active visit given patient with active visit
	 * @should create radiology order encounter attached to new active visit given patient without active visit
	 */
	@Transactional
	private Encounter saveRadiologyOrderEncounter(Patient patient, Provider provider, Date encounterDateTime) {
		
		final EncounterTransaction encounterTransaction = new EncounterTransaction();
		encounterTransaction.setPatientUuid(patient.getUuid());
		final EncounterTransaction.Provider encounterProvider = new EncounterTransaction.Provider();
		encounterProvider.setEncounterRoleUuid(radiologyProperties.getRadiologyOrderingProviderEncounterRole()
				.getUuid());
		// sets the provider of the encounterprovider
		encounterProvider.setUuid(provider.getUuid());
		final Set<EncounterTransaction.Provider> encounterProviderSet = new HashSet<EncounterTransaction.Provider>();
		encounterProviderSet.add(encounterProvider);
		encounterTransaction.setProviders(encounterProviderSet);
		encounterTransaction.setEncounterDateTime(encounterDateTime);
		encounterTransaction.setVisitTypeUuid(this.radiologyProperties.getRadiologyVisitType()
				.getUuid());
		encounterTransaction.setEncounterTypeUuid(this.radiologyProperties.getRadiologyOrderEncounterType()
				.getUuid());
		
		return this.encounterService.getEncounterByUuid(this.emrEncounterService.save(encounterTransaction)
				.getEncounterUuid());
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
	 * @see RadiologyService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
	 */
	@Transactional
	@Override
	public Order discontinueRadiologyOrder(RadiologyOrder radiologyOrderToDiscontinue, Provider orderer,
			String nonCodedDiscontinueReason) throws Exception {
		
		if (radiologyOrderToDiscontinue == null) {
			throw new IllegalArgumentException("radiologyOrder is required");
		}
		
		if (radiologyOrderToDiscontinue.getOrderId() == null) {
			throw new IllegalArgumentException("orderId is null");
		}
		
		if (!radiologyOrderToDiscontinue.isActive()) {
			throw new IllegalArgumentException("order is not active");
		}
		
		if (radiologyOrderToDiscontinue.isInProgress()) {
			throw new IllegalArgumentException("radiologyOrder is in progress");
		}
		
		if (radiologyOrderToDiscontinue.isCompleted()) {
			throw new IllegalArgumentException("radiologyOrder is completed");
		}
		
		if (orderer == null) {
			throw new IllegalArgumentException("provider is required");
		}
		
		final Encounter encounter = this.saveRadiologyOrderEncounter(radiologyOrderToDiscontinue.getPatient(), orderer, null);
		
		return this.orderService.discontinueOrder(radiologyOrderToDiscontinue, nonCodedDiscontinueReason, null, orderer,
			encounter);
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
		
		final Study studyToBeUpdated = studyDAO.getStudyByStudyInstanceUid(studyInstanceUid);
		studyToBeUpdated.setPerformedStatus(performedStatus);
		return studyDAO.saveStudy(studyToBeUpdated);
	}
	
	/**
	 * @see RadiologyService#placeRadiologyOrderInPacs(RadiologyOrder)
	 */
	@Transactional
	@Override
	public boolean placeRadiologyOrderInPacs(RadiologyOrder radiologyOrder) throws HL7Exception {
		
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder is required");
		}
		
		if (radiologyOrder.getOrderId() == null) {
			throw new IllegalArgumentException("radiologyOrder is not persisted");
		}
		
		if (radiologyOrder.getStudy() == null) {
			throw new IllegalArgumentException("radiologyOrder.study is required");
		}
		
		final String hl7message = new RadiologyORMO01().createEncodedMessage(radiologyOrder, OrderControlElement.NEW_ORDER);
		log.info("Created HL7 ORM^O01 message \n" + hl7message);
		final boolean result = HL7Sender.sendHL7Message(hl7message);
		
		updateStudyMwlStatus(radiologyOrder, result);
		return result;
	}
	
	/**
	 * @see RadiologyService#discontinueRadiologyOrderInPacs(RadiologyOrder)
	 */
	@Transactional
	@Override
	public boolean discontinueRadiologyOrderInPacs(RadiologyOrder radiologyOrder) throws HL7Exception {
		
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder is required");
		}
		
		if (radiologyOrder.getOrderId() == null) {
			throw new IllegalArgumentException("radiologyOrder is not persisted");
		}
		
		final String hl7message = new RadiologyORMO01().createEncodedMessage(radiologyOrder, OrderControlElement.NEW_ORDER);
		log.info("Created HL7 ORM^O01 message \n" + hl7message);
		final boolean result = HL7Sender.sendHL7Message(hl7message);
		
		updateStudyMwlStatus(radiologyOrder, result);
		return result;
	}
	
	/**
	 * Set MwlStatus of given RadiologyOrder's Study to IN_SYNC and OUT_OF_SYNC
	 * 
	 * @param radiologyOrder radiology order whos study mwlstatus is updated
	 * @param isInSync set the study mwlstatus to in sync if true
	 * @should set the study mwlstatus of given radiology order to in sync given is in sync true
	 * @should set the study mwlstatus of given radiology order to out of sync given is in sync false
	 */
	@Transactional
	private void updateStudyMwlStatus(RadiologyOrder radiologyOrder, final boolean isInSync) {
		
		final MwlStatus mwlStatus;
		if (isInSync) {
			mwlStatus = MwlStatus.IN_SYNC;
		} else {
			mwlStatus = MwlStatus.OUT_OF_SYNC;
		}
		
		radiologyOrder.getStudy()
				.setMwlStatus(mwlStatus);
		saveStudy(radiologyOrder.getStudy());
	}
	
	/**
	 * @see RadiologyService#getStudyByStudyId(Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public Study getStudyByStudyId(Integer studyId) {
		return studyDAO.getStudyByStudyId(studyId);
	}
	
	/**
	 * @see RadiologyService#getStudyByOrderId(Integer)
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
	 * @see RadiologyService#getStudyByStudyInstanceUid(String)
	 */
	@Transactional(readOnly = true)
	public Study getStudyByStudyInstanceUid(String studyInstanceUid) {
		if (studyInstanceUid == null) {
			throw new IllegalArgumentException("studyInstanceUid is required");
		}
		
		return studyDAO.getStudyByStudyInstanceUid(studyInstanceUid);
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
		
		final List<Study> result = studyDAO.getStudiesByRadiologyOrders(radiologyOrders);
		return result;
	}
	
	/**
	 * @see RadiologyService#createAndClaimRadiologyReport(RadiologyOrder)
	 */
	@Transactional
	@Override
	public RadiologyReport createAndClaimRadiologyReport(RadiologyOrder radiologyOrder) throws IllegalArgumentException,
			UnsupportedOperationException {
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null");
		}
		if (radiologyOrder.isNotCompleted()) {
			throw new IllegalArgumentException("radiologyOrder needs to be completed");
		}
		if (radiologyReportDAO.hasRadiologyOrderCompletedRadiologyReport(radiologyOrder)) {
			throw new UnsupportedOperationException(
					"cannot create radiologyReport for this radiologyOrder because it is already completed");
		}
		if (radiologyReportDAO.hasRadiologyOrderClaimedRadiologyReport(radiologyOrder)) {
			throw new UnsupportedOperationException(
					"cannot create radiologyReport for this radiologyOrder because it is already claimed");
		}
		final RadiologyReport radiologyReport = new RadiologyReport(radiologyOrder);
		return radiologyReportDAO.saveRadiologyReport(radiologyReport);
	}
	
	/**
	 * @see RadiologyService#saveRadiologyReport(RadiologyReport)
	 */
	@Transactional
	@Override
	public RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport) throws IllegalArgumentException,
			UnsupportedOperationException {
		if (radiologyReport == null) {
			throw new IllegalArgumentException("radiologyReport cannot be null");
		}
		if (radiologyReport.getReportStatus() == null) {
			throw new IllegalArgumentException("radiologyReportStatus cannot be null");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.DISCONTINUED) {
			throw new UnsupportedOperationException("a discontinued radiologyReport cannot be saved");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.COMPLETED) {
			throw new UnsupportedOperationException("a completed radiologyReport cannot be saved");
		}
		return radiologyReportDAO.saveRadiologyReport(radiologyReport);
	}
	
	/**
	 * @see RadiologyService#unclaimRadiologyReport(RadiologyReport)
	 */
	@Transactional
	@Override
	public RadiologyReport unclaimRadiologyReport(RadiologyReport radiologyReport) throws IllegalArgumentException,
			UnsupportedOperationException {
		if (radiologyReport == null) {
			throw new IllegalArgumentException("radiologyReport cannot be null");
		}
		if (radiologyReport.getReportStatus() == null) {
			throw new IllegalArgumentException("radiologyReportStatus cannot be null");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.DISCONTINUED) {
			throw new UnsupportedOperationException("a discontinued radiologyReport cannot be unclaimed");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.COMPLETED) {
			throw new UnsupportedOperationException("a completed radiologyReport cannot be unclaimed");
		}
		radiologyReport.setReportStatus(RadiologyReportStatus.DISCONTINUED);
		return radiologyReportDAO.saveRadiologyReport(radiologyReport);
	}
	
	/**
	 * @see RadiologyService#completeRadiologyReport(RadiologyReport, Provider)
	 */
	@Override
	public RadiologyReport completeRadiologyReport(RadiologyReport radiologyReport, Provider principalResultsInterpreter)
			throws IllegalArgumentException, UnsupportedOperationException {
		if (radiologyReport == null) {
			throw new IllegalArgumentException("radiologyReport cannot be null");
		}
		if (principalResultsInterpreter == null) {
			throw new IllegalArgumentException("principalResultsInterpreter cannot be null");
		}
		if (radiologyReport.getReportStatus() == null) {
			throw new IllegalArgumentException("radiologyReportStatus cannot be null");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.DISCONTINUED) {
			throw new UnsupportedOperationException("a discontinued radiologyReport cannot be completed");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.COMPLETED) {
			throw new UnsupportedOperationException("a completed radiologyReport cannot be completed");
		}
		radiologyReport.setReportDate(new Date());
		radiologyReport.setPrincipalResultsInterpreter(principalResultsInterpreter);
		radiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
		return radiologyReportDAO.saveRadiologyReport(radiologyReport);
	}
	
	/**
	 * @see RadiologyService#getRadiologyReportByRadiologyReportId(Integer)
	 */
	@Transactional
	@Override
	public RadiologyReport getRadiologyReportByRadiologyReportId(Integer radiologyReportId) throws IllegalArgumentException {
		if (radiologyReportId == null) {
			throw new IllegalArgumentException("radiologyReportId cannot be null");
		}
		return radiologyReportDAO.getRadiologyReportById(radiologyReportId);
	}
	
	/**
	 * @see RadiologyService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder, RadiologyReportStatus)
	 */
	public List<RadiologyReport> getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder radiologyOrder,
			RadiologyReportStatus radiologyReportStatus) throws IllegalArgumentException {
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null");
		}
		if (radiologyReportStatus == null) {
			throw new IllegalArgumentException("radiologyReportStatus cannot be null");
		}
		return radiologyReportDAO.getRadiologyReportsByRadiologyOrderAndRadiologyReportStatus(radiologyOrder,
			radiologyReportStatus)
				.size() > 0 ? radiologyReportDAO.getRadiologyReportsByRadiologyOrderAndRadiologyReportStatus(radiologyOrder,
			radiologyReportStatus) : new ArrayList<RadiologyReport>();
	}
	
	/**
	 * @see RadiologyService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
	 */
	public boolean hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder radiologyOrder) {
		return radiologyOrder == null ? false : radiologyReportDAO.hasRadiologyOrderClaimedRadiologyReport(radiologyOrder);
	}
	
	/**
	 * @see RadiologyService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
	 */
	public boolean hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder radiologyOrder) {
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null");
		}
		return radiologyReportDAO.hasRadiologyOrderCompletedRadiologyReport(radiologyOrder) ? true : false;
	}
	
	/**
	 * @see RadiologyService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
	 */
	public RadiologyReport getActiveRadiologyReportByRadiologyOrder(RadiologyOrder radiologyOrder) {
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null");
		}
		if (hasRadiologyOrderCompletedRadiologyReport(radiologyOrder)) {
			return radiologyReportDAO.getActiveRadiologyReportByRadiologyOrder(radiologyOrder);
		}
		if (hasRadiologyOrderClaimedRadiologyReport(radiologyOrder)) {
			return radiologyReportDAO.getActiveRadiologyReportByRadiologyOrder(radiologyOrder);
		}
		return null;
	}
}
