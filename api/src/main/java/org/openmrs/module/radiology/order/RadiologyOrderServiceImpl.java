/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
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
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.study.RadiologyStudyService;
import org.springframework.transaction.annotation.Transactional;

class RadiologyOrderServiceImpl extends BaseOpenmrsService implements RadiologyOrderService {
    
    
    private static final Log log = LogFactory.getLog(RadiologyOrderServiceImpl.class);
    
    private RadiologyOrderDAO radiologyOrderDAO;
    
    private RadiologyStudyService radiologyStudyService;
    
    private OrderService orderService;
    
    private EncounterService encounterService;
    
    private EmrEncounterService emrEncounterService;
    
    private RadiologyProperties radiologyProperties;
    
    public void setRadiologyOrderDAO(RadiologyOrderDAO radiologyOrderDAO) {
        this.radiologyOrderDAO = radiologyOrderDAO;
    }
    
    public void setRadiologyStudyService(RadiologyStudyService radiologyStudyService) {
        this.radiologyStudyService = radiologyStudyService;
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
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     */
    @Transactional
    @Override
    public RadiologyOrder placeRadiologyOrder(RadiologyOrder radiologyOrder) {
        
        if (radiologyOrder == null) {
            throw new IllegalArgumentException("radiologyOrder cannot be null");
        }
        
        if (radiologyOrder.getOrderId() != null) {
            throw new IllegalArgumentException("Cannot edit an existing RadiologyOrder");
        }
        
        if (radiologyOrder.getStudy() == null) {
            throw new IllegalArgumentException("radiologyOrder.study cannot be null");
        }
        
        if (radiologyOrder.getStudy()
                .getModality() == null) {
            throw new IllegalArgumentException("radiologyOrder.study.modality cannot be null");
        }
        
        final Encounter encounter =
                saveRadiologyOrderEncounter(radiologyOrder.getPatient(), radiologyOrder.getOrderer(), new Date());
        encounter.addOrder(radiologyOrder);
        
        final OrderContext orderContext = new OrderContext();
        orderContext.setCareSetting(radiologyProperties.getRadiologyCareSetting());
        orderContext.setOrderType(radiologyProperties.getRadiologyTestOrderType());
        
        final RadiologyOrder result = (RadiologyOrder) orderService.saveOrder(radiologyOrder, orderContext);
        this.radiologyStudyService.saveRadiologyStudy(result.getStudy());
        return result;
    }
    
    /**
     * Save radiology order encounter for given parameters
     * 
     * @param patient the encounter patient
     * @param provider the encounter provider
     * @param encounterDateTime the encounter date
     * @return radiology order encounter for given parameters
     * @should create radiology order encounter attached to existing active visit given patient with
     *         active visit
     * @should create radiology order encounter attached to new active visit given patient without
     *         active visit
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
     * @throws Exception
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     */
    @Transactional
    @Override
    public Order discontinueRadiologyOrder(RadiologyOrder radiologyOrderToDiscontinue, Provider orderer,
            String nonCodedDiscontinueReason) throws Exception {
        
        if (radiologyOrderToDiscontinue == null) {
            throw new IllegalArgumentException("radiologyOrder cannot be null");
        }
        
        if (radiologyOrderToDiscontinue.getOrderId() == null) {
            throw new IllegalArgumentException("can only discontinue existing RadiologyOrder. orderId is null");
        }
        
        if (radiologyOrderToDiscontinue.isDiscontinuedRightNow()) {
            throw new IllegalArgumentException("radiologyOrder is already discontinued");
        }
        
        if (radiologyOrderToDiscontinue.isInProgress()) {
            throw new IllegalArgumentException("radiologyOrder is in progress");
        }
        
        if (radiologyOrderToDiscontinue.isCompleted()) {
            throw new IllegalArgumentException("radiologyOrder is completed");
        }
        
        if (orderer == null) {
            throw new IllegalArgumentException("provider cannot be null");
        }
        
        final Encounter encounter =
                this.saveRadiologyOrderEncounter(radiologyOrderToDiscontinue.getPatient(), orderer, null);
        
        return this.orderService.discontinueOrder(radiologyOrderToDiscontinue, nonCodedDiscontinueReason, null, orderer,
            encounter);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrder(Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public RadiologyOrder getRadiologyOrder(Integer orderId) {
        
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null");
        }
        
        return radiologyOrderDAO.getRadiologyOrder(orderId);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrderByUuid(String)
     */
    @Transactional(readOnly = true)
    @Override
    public RadiologyOrder getRadiologyOrderByUuid(String uuid) {
        
        if (uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }
        
        return radiologyOrderDAO.getRadiologyOrderByUuid(uuid);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrdersByPatient(Patient)
     */
    @Transactional(readOnly = true)
    @Override
    public List<RadiologyOrder> getRadiologyOrdersByPatient(Patient patient) {
        
        if (patient == null) {
            throw new IllegalArgumentException("patient cannot be null");
        }
        
        return radiologyOrderDAO.getRadiologyOrdersByPatient(patient);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrdersByPatients
     */
    @Transactional(readOnly = true)
    @Override
    public List<RadiologyOrder> getRadiologyOrdersByPatients(List<Patient> patients) {
        
        if (patients == null) {
            throw new IllegalArgumentException("patients cannot be null");
        }
        
        return radiologyOrderDAO.getRadiologyOrdersByPatients(patients);
    }
}
