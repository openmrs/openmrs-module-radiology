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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.test.BaseContextMockTest;

import static org.mockito.Mockito.when;

/**
 * Tests {@link RadiologyOrderService}
 */
public class RadiologyOrderServiceTest extends BaseContextMockTest {
    
    
    private static final int EXISTING_RADIOLOGY_ORDER_ID = 2001;
    
    private static final String DISCONTINUE_REASON = "Wrong Procedure";
    
    private RadiologyOrderService radiologyOrderService = new RadiologyOrderServiceImpl();
    
    @Mock
    private RadiologyOrder radiologyOrder;
    
    @Mock
    private Provider orderer;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies throw illegal argument exception given null
     */
    @Test
    public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyOrderService.placeRadiologyOrder(null);
    }
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies throw illegal argument exception if given radiology order has no study
     */
    @Test
    public void placeRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderHasNoStudy() {
        when(radiologyOrder.getOrderId()).thenReturn(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder.study cannot be null");
        radiologyOrderService.placeRadiologyOrder(radiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#placeRadiologyOrder(RadiologyOrder)
     * @verifies throw api exception on saving an existing radiology order
     */
    @Test
    public void placeRadiologyOrder_shouldThrowApiExceptionOnSavingAnExistingRadiologyOrder() {
        when(radiologyOrder.getOrderId()).thenReturn(EXISTING_RADIOLOGY_ORDER_ID);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Order.cannot.edit.existing");
        radiologyOrderService.placeRadiologyOrder(radiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw illegal argument exception if given radiology order is null
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderIsNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyOrderService.discontinueRadiologyOrder(null, orderer, DISCONTINUE_REASON);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw illegal argument exception if given radiology order with orderId null
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderWithOrderIdNull()
            throws Exception {
        when(radiologyOrder.getOrderId()).thenReturn(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder.orderId cannot be null, can only discontinue existing order");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, orderer, DISCONTINUE_REASON);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw illegal argument exception if given orderer is null
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenOrdererIsNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("orderer cannot be null");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, null, DISCONTINUE_REASON);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder,Provider,String)
     * @verifies throw api exception if given radiology order is discontinued
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderIsDiscontinued()
            throws Exception {
        when(radiologyOrder.isDiscontinuedRightNow()).thenReturn(true);
        when(radiologyOrder.getOrderer()).thenReturn(orderer);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("RadiologyOrder.cannot.discontinue.discontinued");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, orderer, DISCONTINUE_REASON);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw api exception if given radiology order is in progress
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderIsInProgress()
            throws Exception {
        when(radiologyOrder.getOrderer()).thenReturn(orderer);
        when(radiologyOrder.isInProgress()).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("RadiologyOrder.cannot.discontinue.inProgressOrcompleted");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, orderer, DISCONTINUE_REASON);
    }
    
    /**
     * @see RadiologyOrderService#discontinueRadiologyOrder(RadiologyOrder, Provider, String)
     * @verifies throw api exception if given radiology order is completed
     */
    @Test
    public void discontinueRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderIsCompleted()
            throws Exception {
        when(radiologyOrder.getOrderer()).thenReturn(orderer);
        when(radiologyOrder.isCompleted()).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("RadiologyOrder.cannot.discontinue.inProgressOrcompleted");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, orderer, DISCONTINUE_REASON);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrder(Integer)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("orderId cannot be null");
        radiologyOrderService.getRadiologyOrder(null);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrderByUuid(String)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyOrderByUuid_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("uuid cannot be null");
        radiologyOrderService.getRadiologyOrderByUuid(null);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyOrders_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrderSearchCriteria cannot be null");
        radiologyOrderService.getRadiologyOrders(null);
    }
}
