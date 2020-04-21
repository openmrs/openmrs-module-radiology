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

import java.util.List;

import org.openmrs.Order;
import org.openmrs.Provider;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.RadiologyPrivileges;

/**
 * Service layer for {@code RadiologyOrder}.
 * 
 * @see org.openmrs.module.radiology.order.RadiologyOrder
 */
public interface RadiologyOrderService extends OpenmrsService {
    
    
    /**
     * Gets the next available accession number seed.
     * 
     * @return the accession number seed
     * @throws APIException
     * <strong>Should</strong> return the next accession number seed
     */
    public Long getNextAccessionNumberSeedSequenceValue();
    
    /**
     * Saves a new {@code RadiologyOrder} and its {@code RadiologyStudy} to the
     * database.
     *
     * @param radiologyOrder the radiology order to be created
     * @return the created radiology order
     * @throws IllegalArgumentException if radiologyOrder is null
     * @throws IllegalArgumentException if radiologyOrder.study is null
     * @throws APIException on saving an existing radiology order
     * <strong>Should</strong> create new radiology order and study from given radiology order
     * <strong>Should</strong> create radiology order encounter
     * <strong>Should</strong> set the radiology order accession number
     * <strong>Should</strong> throw illegal argument exception given null
     * <strong>Should</strong> throw illegal argument exception if given radiology order has no study
     * <strong>Should</strong> throw api exception on saving an existing radiology order
     */
    @Authorized(RadiologyPrivileges.ADD_RADIOLOGY_ORDERS)
    public RadiologyOrder placeRadiologyOrder(RadiologyOrder radiologyOrder);
    
    /**
     * Discontinues an existing {@code RadiologyOrder}.
     *
     * @param radiologyOrder the radiology order to be discontinued
     * @param orderer the provider ordering the discontinuation of the radiology order
     * @param discontinueReason the reason why the radiology order is discontinued
     * @return the discontinuation order
     * @throws Exception
     * @throws IllegalArgumentException if radiologyOrder is null
     * @throws IllegalArgumentException if radiologyOrder orderId is null
     * @throws IllegalArgumentException if provider is null
     * @throws APIException if radiology order is discontinued
     * @throws APIException if radiology order is in progress
     * @throws APIException if radiology order is completed
     * <strong>Should</strong> create discontinuation order which discontinues given radiology order that is not in progress or completed
     * <strong>Should</strong> create radiology order encounter
     * <strong>Should</strong> throw illegal argument exception if given radiology order is null
     * <strong>Should</strong> throw illegal argument exception if given radiology order with orderId null
     * <strong>Should</strong> throw illegal argument exception if given orderer is null
     * <strong>Should</strong> throw api exception if given radiology order is discontinued
     * <strong>Should</strong> throw api exception if given radiology order is in progress
     * <strong>Should</strong> throw api exception if given radiology order is completed
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
     * <strong>Should</strong> return radiology order matching given order id
     * <strong>Should</strong> return null if no match was found
     * <strong>Should</strong> throw illegal argument exception if given null
     */
    @Authorized({ RadiologyPrivileges.GET_RADIOLOGY_ORDERS })
    public RadiologyOrder getRadiologyOrder(Integer orderId);
    
    /**
     * Get the {@code RadiologyOrder} by its {@code UUID}.
     *
     * @param uuid the uuid of the radiology order
     * @return the radiology order matching given uuid
     * @throws IllegalArgumentException if given null
     * <strong>Should</strong> return radiology order matching given uuid
     * <strong>Should</strong> return null if no match was found
     * <strong>Should</strong> throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_ORDERS)
    public RadiologyOrder getRadiologyOrderByUuid(String uuid);
    
    /**
     * Get all {@code RadiologyOrder's} matching a variety of (nullable) criteria.
     * Each extra value for a parameter that is provided acts as an "and" and will reduce the number of results returned
     *
     * @param radiologyOrderSearchCriteria the object containing search parameters
     * @return the radiology orders matching given criteria
     * @throws IllegalArgumentException if given null
     * <strong>Should</strong> return all radiology orders for given patient if patient is specified
     * <strong>Should</strong> return all radiology orders (including voided) matching the search query if include voided is set
     * <strong>Should</strong> return all radiology orders for given urgency
     * <strong>Should</strong> return all radiology orders with effective order start date in given date range if to date and from date are
     *         specified
     * <strong>Should</strong> return all radiology orders with effective order start date after or equal to from date if only from date is
     *         specified
     * <strong>Should</strong> return all radiology orders with effective order start date before or equal to to date if only to date is
     *         specified
     * <strong>Should</strong> return empty list if from date after to date
     * <strong>Should</strong> return empty search result if no effective order start is in date range
     * <strong>Should</strong> return all radiology orders for given accession number if accession number is specified
     * <strong>Should</strong> return all radiology orders for given orderer
     * <strong>Should</strong> return all radiology orders for given urgency and orderer
     * <strong>Should</strong> throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_ORDERS)
    public List<RadiologyOrder> getRadiologyOrders(RadiologyOrderSearchCriteria radiologyOrderSearchCriteria);
}
