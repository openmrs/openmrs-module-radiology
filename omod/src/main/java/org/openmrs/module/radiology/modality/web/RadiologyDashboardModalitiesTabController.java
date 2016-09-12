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

import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.openmrs.module.radiology.web.RadiologyWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the dashboard tab containing {@code RadiologyModality's}.
 */
@Controller
@RequestMapping(RadiologyDashboardModalitiesTabController.RADIOLOGY_MODALITES_TAB_REQUEST_MAPPING)
public class RadiologyDashboardModalitiesTabController {
    
    
    public static final String RADIOLOGY_MODALITES_TAB_REQUEST_MAPPING =
            "/module/radiology/radiologyDashboardModalitiesTab.htm";
    
    static final String RADIOLOGY_MODALITIES_TAB_VIEW = "/module/radiology/radiologyDashboardModalitiesTab";
    
    /**
     * Handles get requests for radiology modalities tab page.
     * 
     * @return model and view of the radiology modalities tab page
     * @should return model and view of the radiology modalities tab page and set tab session attribute to radiology modalities tab page
     */
    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView getRadiologyModalitiesTab(HttpServletRequest request) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_MODALITIES_TAB_VIEW);
        request.getSession()
                .setAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE,
                    RADIOLOGY_MODALITES_TAB_REQUEST_MAPPING);
        return modelAndView;
    }
}
