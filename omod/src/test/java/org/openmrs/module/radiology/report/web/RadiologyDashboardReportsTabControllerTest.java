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

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.openmrs.module.radiology.web.RadiologyWebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

public class RadiologyDashboardReportsTabControllerTest {
    
    
    private RadiologyDashboardReportsTabController radiologyDashboardReportsTabController =
            new RadiologyDashboardReportsTabController();
    
    /**
     * @see RadiologyDashboardReportsTabController#getRadiologyReportsTab(HttpServletRequest)
     * @verifies return model and view of the radiology reports tab page and set tab session attribute to radiology reports
     *           tab page
     */
    @Test
    public void
            getRadiologyReportsTab_shouldReturnModelAndViewOfTheRadiologyReportsTabPageAndSetTabSessionAttributeToRadiologyReportsTabPage()
                    throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        ModelAndView modelAndView = radiologyDashboardReportsTabController.getRadiologyReportsTab(mockRequest);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyDashboardReportsTabController.RADIOLOGY_REPORTS_TAB_VIEW));
        assertThat((String) mockSession.getAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE),
            is(RadiologyDashboardReportsTabController.RADIOLOGY_REPORTS_TAB_REQUEST_MAPPING));
    }
    
    /**
     * @see RadiologyDashboardReportsTabController#getReportStatusList()
     * @verifies return a map containing all report status values and an entry to select all report statuses
     */
    @Test
    public void getReportStatusList_shouldReturnAMapContainingAllReportStatusValuesAndAnEntryToSelectAllReportStatuses()
            throws Exception {
        
        Map<String, String> reportStatuses = radiologyDashboardReportsTabController.getReportStatusList();
        
        assertThat(reportStatuses, hasEntry("", "selectStatus"));
        for (RadiologyReportStatus reportStatus : RadiologyReportStatus.values()) {
            assertThat(reportStatuses, hasEntry(reportStatus.name(), reportStatus.name()));
        }
    }
}
