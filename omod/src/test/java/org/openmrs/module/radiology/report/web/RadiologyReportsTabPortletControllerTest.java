/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.web;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

public class RadiologyReportsTabPortletControllerTest {
    
    
    private RadiologyReportsTabPortletController radiologyReportsTabPortletController =
            new RadiologyReportsTabPortletController();
    
    /**
     * @see RadiologyReportsTabPortletController#populateModel(HttpServletRequest,Map)
     * @verifies populate model with an entry containing all report status values and an empty string
     */
    @Test
    public void populateModel_shouldPopulateModelWithAnEntryContainingAllReportStatusValuesAndAnEmptyString()
            throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        radiologyReportsTabPortletController.populateModel(mockRequest, model);
        
        assertThat(model, hasKey("radiologyReportStatuses"));
        List<String> statuses = (List<String>) model.get("radiologyReportStatuses");
        
        assertThat(statuses, hasItem(""));
        for (RadiologyReportStatus status : RadiologyReportStatus.values()) {
            assertThat(statuses, hasItem(status.name()));
        }
    }
}
