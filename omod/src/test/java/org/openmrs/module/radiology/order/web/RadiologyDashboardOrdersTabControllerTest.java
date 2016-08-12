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

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.openmrs.Order.Urgency;
import org.openmrs.module.radiology.report.web.RadiologyDashboardReportsTabController;
import org.openmrs.module.radiology.web.RadiologyWebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

public class RadiologyDashboardOrdersTabControllerTest {
    
    
    private RadiologyDashboardOrdersTabController radiologyDashboardOrdersTabController =
            new RadiologyDashboardOrdersTabController();
    
    /**
     * @see RadiologyDashboardOrdersTabController#getRadiologyOrdersTab(HttpServletRequest,String)
     * @verifies return model and view of the radiology orders tab page and set tab session attribute to radiology orders tab
     *           page if not already set
     */
    @Test
    public void
            getRadiologyOrdersTab_shouldReturnModelAndViewOfTheRadiologyOrdersTabPageAndSetTabSessionAttributeToRadiologyOrdersTabPageIfNotAlreadySet()
                    throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        ModelAndView modelAndView = radiologyDashboardOrdersTabController.getRadiologyOrdersTab(mockRequest, null);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyDashboardOrdersTabController.RADIOLOGY_ORDERS_TAB_VIEW));
        assertThat((String) mockSession.getAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE),
            is(RadiologyDashboardOrdersTabController.RADIOLOGY_ORDERS_TAB_REQUEST_MAPPING));
    }
    
    /**
     * @see RadiologyDashboardOrdersTabController#getRadiologyOrdersTab(HttpServletRequest,String)
     * @verifies redirect to dashboard tab page given from tab session attribute if switch tab is not set
     */
    @Test
    public void getRadiologyOrdersTab_shouldRedirectToDashboardTabPageGivenFromTabSessionAttributeIfSwitchTabIsNotSet()
            throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE,
            RadiologyDashboardReportsTabController.RADIOLOGY_REPORTS_TAB_REQUEST_MAPPING);
        mockRequest.setSession(mockSession);
        
        ModelAndView modelAndView = radiologyDashboardOrdersTabController.getRadiologyOrdersTab(mockRequest, null);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:" + RadiologyDashboardReportsTabController.RADIOLOGY_REPORTS_TAB_REQUEST_MAPPING));
        assertThat((String) mockSession.getAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE),
            is(RadiologyDashboardReportsTabController.RADIOLOGY_REPORTS_TAB_REQUEST_MAPPING));
    }
    
    /**
     * @see RadiologyDashboardOrdersTabController#getRadiologyOrdersTab(HttpServletRequest,String)
     * @verifies not redirect to dashboard tab page given from tab session attribute and set tab session attribute to radiology orders tab page if switch tab is set
     */
    @Test
    public void
            getRadiologyOrdersTab_shouldNotRedirectToDashboardTabPageGivenFromTabSessionAttributeAndSetTabSessionAttributeToRadiologyOrdersTabPageIfSwitchTabIsSet()
                    throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE,
            RadiologyDashboardReportsTabController.RADIOLOGY_REPORTS_TAB_REQUEST_MAPPING);
        mockRequest.setSession(mockSession);
        
        ModelAndView modelAndView = radiologyDashboardOrdersTabController.getRadiologyOrdersTab(mockRequest, "true");
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyDashboardOrdersTabController.RADIOLOGY_ORDERS_TAB_VIEW));
        assertThat((String) mockSession.getAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE),
            is(RadiologyDashboardOrdersTabController.RADIOLOGY_ORDERS_TAB_REQUEST_MAPPING));
    }
    
    /**
     * @see RadiologyDashboardOrdersTabController#getUrgenciesList()
     * @verifies return a map containing all urgency values and an entry to select all urgencies
     */
    @Test
    public void getUrgenciesList_shouldReturnAMapContainingAllUrgencyValuesAndAnEntryToSelectAllUrgencies()
            throws Exception {
        
        Map<String, String> urgencies = radiologyDashboardOrdersTabController.getUrgenciesList();
        
        assertThat(urgencies, hasEntry("", "allurgencies"));
        for (Urgency urgency : Urgency.values()) {
            assertThat(urgencies, hasEntry(urgency.name(), urgency.name()));
        }
    }
    
}
