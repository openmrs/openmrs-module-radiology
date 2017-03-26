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

import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.test.BaseContextMockTest;

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
    
    @Test
    public void shouldFailToSaveRadiologyOrderGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyOrderService.placeRadiologyOrder(null);
    }
    
    @Test
    public void shouldFailToSaveRadiologyOrderIfGivenRadiologyOrderHasNoStudy() {
        
        when(radiologyOrder.getOrderId()).thenReturn(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder.study cannot be null");
        radiologyOrderService.placeRadiologyOrder(radiologyOrder);
    }
    
    @Test
    public void shouldFailToSaveRadiologyOrderGivenAnExistingOne() {
        
        when(radiologyOrder.getOrderId()).thenReturn(EXISTING_RADIOLOGY_ORDER_ID);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Order.cannot.edit.existing");
        radiologyOrderService.placeRadiologyOrder(radiologyOrder);
    }
    
    @Test
    public void shouldFailToDiscontinueRadiologyOrderIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyOrderService.discontinueRadiologyOrder(null, orderer, DISCONTINUE_REASON);
    }
    
    @Test
    public void shouldFailToDiscontinueRadiologyOrderIfGivenRadiologyOrderWithOrderIdNull() throws Exception {
        
        when(radiologyOrder.getOrderId()).thenReturn(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder.orderId cannot be null, can only discontinue existing order");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, orderer, DISCONTINUE_REASON);
    }
    
    @Test
    public void shouldFailToDiscontinueRadiologyOrderIfGivenOrdererIsNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("orderer cannot be null");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, null, DISCONTINUE_REASON);
    }
    
    @Test
    public void shouldFailToDiscontinueRadiologyOrderIfGivenRadiologyOrderIsDiscontinued() throws Exception {
        
        when(radiologyOrder.isDiscontinuedRightNow()).thenReturn(true);
        when(radiologyOrder.getOrderer()).thenReturn(orderer);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("RadiologyOrder.cannot.discontinue.discontinued");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, orderer, DISCONTINUE_REASON);
    }
    
    @Test
    public void shouldFailToDiscontinueRadiologyOrderIfGivenRadiologyOrderIsInProgress() throws Exception {
        
        when(radiologyOrder.getOrderer()).thenReturn(orderer);
        when(radiologyOrder.isInProgress()).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("RadiologyOrder.cannot.discontinue.inProgressOrcompleted");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, orderer, DISCONTINUE_REASON);
    }
    
    @Test
    public void shouldFailToDiscontinueRadiologyOrderIfGivenRadiologyOrderIsCompleted() throws Exception {
        
        when(radiologyOrder.getOrderer()).thenReturn(orderer);
        when(radiologyOrder.isCompleted()).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("RadiologyOrder.cannot.discontinue.inProgressOrcompleted");
        radiologyOrderService.discontinueRadiologyOrder(radiologyOrder, orderer, DISCONTINUE_REASON);
    }
    
    @Test
    public void shouldFailToGetRadiologyOrderByIdIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("orderId cannot be null");
        radiologyOrderService.getRadiologyOrder(null);
    }
    
    @Test
    public void shouldFailToGetRadiologyOrderByUuidIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("uuid cannot be null");
        radiologyOrderService.getRadiologyOrderByUuid(null);
    }
    
    @Test
    public void shouldFailToGetRadiologyOrdersIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrderSearchCriteria cannot be null");
        radiologyOrderService.getRadiologyOrders(null);
    }
}
