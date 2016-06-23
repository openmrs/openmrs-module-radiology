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

/**
 * Service layer for {@code RadiologyOrder}.
 * 
 * @see org.openmrs.module.radiology.order.RadiologyOrder
 */
@Transactional
public interface RadiologyOrderService extends OpenmrsService {
    
    
    /**
     * Saves a new {@code RadiologyOrder} and its {@code RadiologyStudy} to the
     * database.
     *
     * @param radiologyOrder the radiology order to be created
     * @return the created radiology order
     * @throws IllegalArgumentException if radiologyOrder is null
     * @throws IllegalArgumentException if radiologyOrder.orderId is not null
     * @throws IllegalArgumentException if radiologyOrder.study is null
     * @throws IllegalArgumentException if radiologyOrder.study.modality is null
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
    public RadiologyOrder placeRadiologyOrder(RadiologyOrder radiologyOrder);
    
    /**
     * Discontinues an existing {@code RadiologyOrder}.
     *
     * @param radiologyOrder the radiology order to be discontinued
     * @return the discontinuation order
     * @throws Exception
     * @throws IllegalArgumentException if radiologyOrder is null
     * @throws IllegalArgumentException if radiologyOrder orderId is null
     * @throws IllegalArgumentException if radiology order is discontinued
     * @throws IllegalArgumentException if radiology order is in progress
     * @throws IllegalArgumentException if radiology order is completed
     * @throws IllegalArgumentException if provider is null
     * @should create discontinuation order which discontinues given radiology order that is not in progress or completed
     * @should create discontinuation order with encounter attached to existing active visit if patient has active visit
     * @should create discontinuation order with encounter attached to new active visit if patient without active visit
     * @should throw illegal argument exception if given radiology order is null
     * @should throw illegal argument exception if given radiology order with orderId null
     * @should throw illegal argument exception if given radiology order is discontinued
     * @should throw illegal argument exception if given radiology order is in progress
     * @should throw illegal argument exception if given radiology order is completed
     * @should throw illegal argument exception if given provider is null
     */
    @Authorized({ RadiologyPrivileges.DELETE_RADIOLOGY_ORDERS })
    public Order discontinueRadiologyOrder(RadiologyOrder radiologyOrder, Provider orderer, String discontinueReason)
            throws Exception;
    
    /**
     * Get the {@code RadiologyOrder} by its {@code orderId}.
     *
     * @param orderId the order id of wanted radiology order
     * @return the radiology order matching given order id
     * @throws IllegalArgumentException if given null
     * @should return radiology order matching given order id
     * @should return null if no match was found
     * @should throw illegal argument exception if given null
     */
    @Authorized({ RadiologyPrivileges.GET_RADIOLOGY_ORDERS })
    public RadiologyOrder getRadiologyOrder(Integer orderId);
    
    /**
     * Get the {@code RadiologyOrder} by its {@code UUID}.
     *
     * @param uuid the uuid of the radiology order
     * @return the radiology order matching given uuid
     * @throws IllegalArgumentException if given null
     * @should return radiology order matching given uuid
     * @should return null if no match was found
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_ORDERS)
    public RadiologyOrder getRadiologyOrderByUuid(String uuid);
    
    /**
     * Get the {@code RadiologyOrder's} associated with a {@code Patient}.
     * 
     * @param patient the patient for which radiology orders shall be returned
     * @return the radiology orders associated with given patient
     * @throws IllegalArgumentException if given null
     * @should return all radiology orders associated with given patient
     * @should return empty list given patient without associated radiology orders
     * @should throw illegal argument exception if given null
     */
    @Authorized({ RadiologyPrivileges.GET_RADIOLOGY_ORDERS })
    public List<RadiologyOrder> getRadiologyOrdersByPatient(Patient patient);
    
    /**
     * Get the {@code RadiologyOrder's} associated with a list of {@code Patient's}.
     *
     * @param patients the list of patients for which radiology orders shall be returned
     * @return the radiology orders associated with given patients
     * @throws IllegalArgumentException if given null
     * @should return all radiology orders associated with given patients
     * @should return all radiology orders given empty patient list
     * @should return empty list given patient list without associated radiology orders
     * @should throw illegal argument exception if given null
     */
    @Authorized({ RadiologyPrivileges.GET_RADIOLOGY_ORDERS })
    public List<RadiologyOrder> getRadiologyOrdersByPatients(List<Patient> patients);
}
