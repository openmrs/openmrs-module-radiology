/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality.web;

import org.junit.Test;
import org.openmrs.module.radiology.web.RadiologyWebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests {@link RadiologyDashboardModalitiesTabController}.
 */
public class RadiologyDashboardModalitiesTabControllerTest {
    
    
    private RadiologyDashboardModalitiesTabController radiologyDashboardModalitiesTabController =
            new RadiologyDashboardModalitiesTabController();
    
    /**
     * @verifies return model and view of the radiology modalities tab page and set tab session attribute to radiology modalities tab page
     * @see RadiologyDashboardModalitiesTabController#getRadiologyModalitiesTab(HttpServletRequest)
     */
    @Test
    public void
            getRadiologyModalitiesTab_shouldReturnModelAndViewOfTheRadiologyModalitiesTabPageAndSetTabSessionAttributeToRadiologyModalitiesTabPage()
                    throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        ModelAndView modelAndView = radiologyDashboardModalitiesTabController.getRadiologyModalitiesTab(mockRequest);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyDashboardModalitiesTabController.RADIOLOGY_MODALITIES_TAB_VIEW));
        assertThat(mockSession.getAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE),
            is(RadiologyDashboardModalitiesTabController.RADIOLOGY_MODALITES_TAB_REQUEST_MAPPING));
    }
}
