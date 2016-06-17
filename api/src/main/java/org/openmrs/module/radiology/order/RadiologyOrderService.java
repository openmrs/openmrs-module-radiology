/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order;

import java.util.List;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.RadiologyPrivileges;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RadiologyOrderService extends OpenmrsService {
    
    
    /**
     * Save given <code>RadiologyOrder</code> and its <code>RadiologyOrder.study</code> to the
     * database
     *
     * @param radiologyOrder radiology order to be created
     * @return RadiologyOrder who was created
     * @throws IllegalArgumentException if radiologyOrder is null
     * @throws IllegalArgumentException if radiologyOrder orderId is not null
     * @throws IllegalArgumentException if radiologyOrder.study is null
     * @should create new radiology order and study from given radiology order object
     * @should create radiology order encounter with orderer and attached to existing active visit if patient has active
     *         visit
     * @should create radiology order encounter with orderer attached to new active visit if patient without active visit
     * @should throw illegal argument exception given null
     * @should throw illegal argument exception given existing radiology order
     * @should throw illegal argument exception if given radiology order has no study
     * @should throw illegal argument exception if given study modality is null
     */
    @Authorized(RadiologyPrivileges.ADD_RADIOLOGY_ORDERS)
    public RadiologyOrder placeRadiologyOrder(RadiologyOrder radiologyOrder) throws IllegalArgumentException;
    
    /**
     * Discontinue given <code>RadiologyOrder</code>
     *
     * @param radiologyOrder radiology order to be discontinued
     * @return Order who was created to discontinue RadiologyOrder
     * @throws IllegalArgumentException if radiologyOrder is null
     * @throws IllegalArgumentException if radiologyOrder orderId is null
     * @throws IllegalArgumentException if provider is null
     * @throws IllegalArgumentException if radiology order is discontinued
     * @throws IllegalArgumentException  if radiology order is in progress
     * @throws IllegalArgumentException  if radiology order is completed
     * @should create discontinuation order which discontinues given radiology order that is not in progress or completed
     * @should create discontinuation order with encounter attached to existing active visit if patient has active visit
     * @should create discontinuation order with encounter attached to new active visit if patient without active visit
     * @should throw illegal argument exception given empty radiology order
     * @should throw illegal argument exception given radiology order with orderId null
     * @should throw illegal argument exception if radiology order is discontinued
     * @should throw illegal argument exception if radiology order is in progress
     * @should throw illegal argument exception if radiology order is completed
     * @should throw illegal argument exception given empty provider
     */
    @Authorized({ RadiologyPrivileges.DELETE_RADIOLOGY_ORDERS })
    public Order discontinueRadiologyOrder(RadiologyOrder radiologyOrder, Provider orderer, String discontinueReason)
            throws Exception;
    
    /**
     * Get the {@code RadiologyOrder} by its {@code orderId}.
     *
     * @param orderId of wanted RadiologyOrder
     * @return RadiologyOrder matching given orderId
     * @throws IllegalArgumentException if order id is null
     * @should return radiology order matching order id
     * @should return null if no match was found
     * @should throw illegal argument exception given null
     */
    @Authorized({ RadiologyPrivileges.GET_RADIOLOGY_ORDERS })
    public RadiologyOrder getRadiologyOrder(Integer orderId) throws IllegalArgumentException;
    
    /**
     * Get RadiologyOrder's by its associated Patient
     *
     * @param patient patient of wanted RadiologyOrders
     * @return RadiologyOrders associated with given patient
     * @throws IllegalArgumentException if patient is null
     * @should return all radiology orders associated with given patient
     * @should return empty list given patient without associated radiology orders
     * @should throw illegal argument exception given null
     */
    @Authorized({ RadiologyPrivileges.GET_RADIOLOGY_ORDERS })
    public List<RadiologyOrder> getRadiologyOrdersByPatient(Patient patient) throws IllegalArgumentException;
    
    /**
     * Get RadiologyOrder's by its associated Patients
     *
     * @param patients list of patients for which RadiologyOrders are queried
     * @return RadiologyOrders associated with given patients
     * @throws IllegalArgumentException if patients is null
     * @should return all radiology orders associated with given patients
     * @should return all radiology orders given empty patient list
     * @should return empty list given patient list without associated radiology orders
     * @should throw illegal argument exception given null
     */
    @Authorized({ RadiologyPrivileges.GET_RADIOLOGY_ORDERS })
    public List<RadiologyOrder> getRadiologyOrdersByPatients(List<Patient> patients) throws IllegalArgumentException;
}
