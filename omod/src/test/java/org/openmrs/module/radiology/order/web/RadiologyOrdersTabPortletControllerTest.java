/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order.web;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.openmrs.Order.Urgency;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

public class RadiologyOrdersTabPortletControllerTest {
    
    
    private RadiologyOrdersTabPortletController radiologyOrderTabPortletController =
            new RadiologyOrdersTabPortletController();
    
    /**
     * @see RadiologyOrdersTabPortletController#populateModel(HttpServletRequest,Map)
     * @verifies populate model with an entry containing all urgency values and an empty string
     */
    @Test
    public void populateModel_shouldPopulateModelWithAnEntryContainingAllUrgencyValuesAndAnEmptyString() throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        radiologyOrderTabPortletController.populateModel(mockRequest, model);
        
        assertThat(model, hasKey("urgencies"));
        List<String> urgencies = (List<String>) model.get("urgencies");
        
        assertThat(urgencies, hasItem(""));
        for (Urgency urgency : Urgency.values()) {
            assertThat(urgencies, hasItem(urgency.name()));
        }
    }
}
