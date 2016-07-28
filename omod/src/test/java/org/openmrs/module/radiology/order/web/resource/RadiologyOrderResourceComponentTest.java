/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order.web.resource;

import org.junit.Before;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link RadiologyOrderResource}.
 */
public class RadiologyOrderResourceComponentTest extends BaseDelegatingResourceTest<RadiologyOrderResource, RadiologyOrder> {
    
    
    protected static final String TEST_DATASET = "RadiologyOrderResourceComponentTestDataset.xml";
    
    @Autowired
    RadiologyOrderService radiologyOrderService;
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see BaseDelegatingResourceTest#getDisplayProperty()
     */
    @Override
    public String getDisplayProperty() {
        
        return "4 - FRACTURE";
    }
    
    /**
     * @see BaseDelegatingResourceTest#getUuidProperty()
     */
    @Override
    public String getUuidProperty() {
        
        return "1bae735a-fca0-11e5-9e59-08002719a237";
    }
    
    /**
     * @see BaseDelegatingResourceTest#newObject()
     */
    @Override
    public RadiologyOrder newObject() {
        
        return radiologyOrderService.getRadiologyOrderByUuid(getUuidProperty());
    }
    
    /**
     * @see BaseDelegatingResourceTest#validateDefaultRepresentation()
     */
    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropPresent("orderNumber");
        assertPropPresent("accessionNumber");
        assertPropPresent("patient");
        assertPropPresent("concept");
        assertPropPresent("action");
        assertPropPresent("careSetting");
        assertPropPresent("previousOrder");
        assertPropPresent("dateActivated");
        assertPropPresent("dateStopped");
        assertPropPresent("autoExpireDate");
        assertPropPresent("encounter");
        assertPropPresent("orderer");
        assertPropPresent("orderReason");
        assertPropPresent("orderReasonNonCoded");
        assertPropPresent("urgency");
        assertPropPresent("instructions");
        assertPropPresent("commentToFulfiller");
        assertPropPresent("display");
    }
    
    /**
     * @see BaseDelegatingResourceTest#validateFullRepresentation()
     */
    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropPresent("orderNumber");
        assertPropPresent("accessionNumber");
        assertPropPresent("patient");
        assertPropPresent("concept");
        assertPropPresent("action");
        assertPropPresent("careSetting");
        assertPropPresent("previousOrder");
        assertPropPresent("dateActivated");
        assertPropPresent("dateStopped");
        assertPropPresent("autoExpireDate");
        assertPropPresent("encounter");
        assertPropPresent("orderer");
        assertPropPresent("orderReason");
        assertPropPresent("orderReasonNonCoded");
        assertPropPresent("urgency");
        assertPropPresent("instructions");
        assertPropPresent("commentToFulfiller");
        assertPropPresent("display");
        assertPropPresent("auditInfo");
    }
}
