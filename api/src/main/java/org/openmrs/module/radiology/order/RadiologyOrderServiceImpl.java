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
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderContext;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.study.RadiologyStudyService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
class RadiologyOrderServiceImpl extends BaseOpenmrsService implements RadiologyOrderService, AccessionNumberGenerator {
    
    
    private RadiologyOrderDAO radiologyOrderDAO;
    
    private RadiologyStudyService radiologyStudyService;
    
    private OrderService orderService;
    
    private EncounterService encounterService;
    
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
    
    public void setRadiologyProperties(RadiologyProperties radiologyProperties) {
        this.radiologyProperties = radiologyProperties;
    }
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     */
    @Override
    @Transactional
    public synchronized RadiologyOrder placeRadiologyOrder(RadiologyOrder radiologyOrder) {
        
        if (radiologyOrder == null) {
            throw new IllegalArgumentException("radiologyOrder cannot be null");
        }
        
        if (radiologyOrder.getOrderId() != null) {
            throw new APIException("Order.cannot.edit.existing");
        }
        
        if (radiologyOrder.getStudy() == null) {
            throw new IllegalArgumentException("radiologyOrder.study cannot be null");
        }
        
        radiologyOrder.setAccessionNumber(getNewAccessionNumber());
        
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
     * Save radiology order encounter for given parameters.
     * 
     * @param patient the encounter patient
     * @param provider the encounter provider
     * @param encounterDateTime the encounter date
     * @return radiology order encounter for given parameters
     * @should create radiology order encounter
     */
    private Encounter saveRadiologyOrderEncounter(Patient patient, Provider provider, Date encounterDateTime) {
        final Encounter radiologyOrderEncounter = new Encounter();
        radiologyOrderEncounter.setPatient(patient);
        radiologyOrderEncounter.setProvider(radiologyProperties.getRadiologyOrderingProviderEncounterRole(), provider);
        radiologyOrderEncounter.setEncounterDatetime(encounterDateTime);
        radiologyOrderEncounter.setEncounterType(radiologyProperties.getRadiologyOrderEncounterType());
        return encounterService.saveEncounter(radiologyOrderEncounter);
    }
    
    /**
     * @throws Exception
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     */
    @Override
    @Transactional
    public Order discontinueRadiologyOrder(RadiologyOrder radiologyOrder, Provider orderer, String nonCodedDiscontinueReason)
            throws Exception {
        
        if (radiologyOrder == null) {
            throw new IllegalArgumentException("radiologyOrder cannot be null");
        }
        
        if (radiologyOrder.getOrderId() == null) {
            throw new IllegalArgumentException("radiologyOrder.orderId cannot be null, can only discontinue existing order");
        }
        
        if (orderer == null) {
            throw new IllegalArgumentException("orderer cannot be null");
        }
        
        if (radiologyOrder.isDiscontinuedRightNow()) {
            throw new APIException("RadiologyOrder.cannot.discontinue.discontinued");
        }
        
        if (radiologyOrder.isInProgress() | radiologyOrder.isCompleted()) {
            throw new APIException("RadiologyOrder.cannot.discontinue.inProgressOrcompleted");
        }
        
        final Encounter encounter = this.saveRadiologyOrderEncounter(radiologyOrder.getPatient(), orderer, new Date());
        
        return this.orderService.discontinueOrder(radiologyOrder, nonCodedDiscontinueReason, null, orderer, encounter);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrder(Integer)
     */
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
    @Override
    public RadiologyOrder getRadiologyOrderByUuid(String uuid) {
        
        if (uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }
        
        return radiologyOrderDAO.getRadiologyOrderByUuid(uuid);
    }
    
    /**
     * @see AccessionNumberGenerator#getNewAccessionNumber()
     */
    @Override
    public String getNewAccessionNumber() {
        return Context.getService(RadiologyOrderService.class)
                .getNextAccessionNumberSeedSequenceValue()
                .toString();
    }
    
    /**
     * @see RadiologyOrderService#getNextAccessionNumberSeedSequenceValue()
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized Long getNextAccessionNumberSeedSequenceValue() {
        
        return radiologyOrderDAO.getNextAccessionNumberSeedSequenceValue();
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     */
    @Override
    public List<RadiologyOrder> getRadiologyOrders(RadiologyOrderSearchCriteria radiologyOrderSearchCriteria) {
        
        if (radiologyOrderSearchCriteria == null) {
            throw new IllegalArgumentException("radiologyOrderSearchCriteria cannot be null");
        }
        return radiologyOrderDAO.getRadiologyOrders(radiologyOrderSearchCriteria);
    }
}
