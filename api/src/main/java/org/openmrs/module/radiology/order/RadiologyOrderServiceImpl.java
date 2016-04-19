/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order;

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
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.hl7.util.HL7Sender;
import org.openmrs.module.radiology.hl7.v231.code.OrderControlElement;
import org.openmrs.module.radiology.hl7.v231.message.RadiologyORMO01;
import org.openmrs.module.radiology.study.RadiologyStudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.hl7v2.HL7Exception;

class RadiologyOrderServiceImpl extends BaseOpenmrsService implements RadiologyOrderService {
	
	private static final Log log = LogFactory.getLog(RadiologyOrderServiceImpl.class);
	
	@Autowired
	private RadiologyOrderDAO radiologyOrderDAO;
	
	@Autowired
	private RadiologyStudyService radiologyStudyService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private EncounterService encounterService;
	
	@Autowired
	private EmrEncounterService emrEncounterService;
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	/**
	 * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
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
		this.radiologyStudyService.saveStudy(result.getStudy());
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
	 * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
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
	 * @see RadiologyOrderService#getRadiologyOrderByOrderId(Integer)
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
	 * @see RadiologyOrderService#getRadiologyOrdersByPatient(Patient)
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
	 * @see RadiologyOrderService#getRadiologyOrdersByPatients(List<Patient>)
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
	 * @see RadiologyOrderService#placeRadiologyOrderInPacs(RadiologyOrder)
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
	 * @see RadiologyOrderService#discontinueRadiologyOrderInPacs(RadiologyOrder)
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
		this.radiologyStudyService.saveStudy(radiologyOrder.getStudy());
	}
}
