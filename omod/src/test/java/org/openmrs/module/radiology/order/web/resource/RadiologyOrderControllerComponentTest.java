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
import org.junit.Test;
import org.openmrs.module.radiology.web.RadiologyRestController;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

/**
 * Tests {@link RadiologyRestController} with respect to {@link RadiologyOrderResource}.
 */
public class RadiologyOrderControllerComponentTest extends MainResourceControllerTest {
    
    
    protected static final String TEST_DATASET = "RadiologyOrderResourceComponentTestDataset.xml";
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        
        return "radiologyorder";
    }
    
    /**
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        
        return "1bae735a-fca0-11e5-9e59-08002719a237";
    }
    
    @Override
    public long getAllCount() {
        
        return 0;
    }
    
    /**
     * @see MainResourceControllerTest#shouldGetAll()
     */
    @Override
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        
        super.shouldGetAll();
    }
}
