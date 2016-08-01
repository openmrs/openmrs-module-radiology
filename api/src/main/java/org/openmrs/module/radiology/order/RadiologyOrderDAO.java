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

/**
 * {@code RadiologyOrder} related database methods.
 * 
 * @see org.openmrs.module.radiology.order.RadiologyOrderService
 * @see org.openmrs.module.radiology.order.RadiologyOrder
 */
interface RadiologyOrderDAO {
    
    
    /**
     * @see org.openmrs.module.radiology.order.RadiologyOrderService#getNextAccessionNumberSeedSequenceValue()
     */
    public Long getNextAccessionNumberSeedSequenceValue();
    
    /**
     * @see org.openmrs.module.radiology.order.RadiologyOrderService#getRadiologyOrder(Integer)
     */
    public RadiologyOrder getRadiologyOrder(Integer orderId);
    
    /**
     * @see org.openmrs.module.radiology.order.RadiologyOrderService#getRadiologyOrderByUuid(String)
     */
    public RadiologyOrder getRadiologyOrderByUuid(String uuid);
    
    /**
     * @see org.openmrs.module.radiology.order.RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     */
    List<RadiologyOrder> getRadiologyOrders(RadiologyOrderSearchCriteria searchCriteria);
}
