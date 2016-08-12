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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Order.Urgency;
import org.openmrs.module.radiology.web.RadiologyWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the dashboard tab containing {@code RadiologyOrders}.
 */
@Controller
@RequestMapping(RadiologyDashboardOrdersTabController.RADIOLOGY_ORDERS_TAB_REQUEST_MAPPING)
public class RadiologyDashboardOrdersTabController {
    
    
    public static final String RADIOLOGY_ORDERS_TAB_REQUEST_MAPPING = "/module/radiology/radiologyDashboardOrdersTab.htm";
    
    static final String RADIOLOGY_ORDERS_TAB_VIEW = "/module/radiology/radiologyDashboardOrdersTab";
    
    /**
     * Handles get requests for radiology orders tab page.
     * 
     * @return model and view of the radiology orders tab page
     * @should return model and view of the radiology orders tab page and set tab session attribute to radiology orders tab page if not already set
     * @should not redirect to dashboard tab page given from tab session attribute and set tab session attribute to radiology orders tab page if switch tab is set
     * @should redirect to dashboard tab page given from tab session attribute if switch tab is not set
     */
    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView getRadiologyOrdersTab(HttpServletRequest request,
            @RequestParam(required = false) String switchTab) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_ORDERS_TAB_VIEW);
        
        String tabLink = (String) request.getSession()
                .getAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE);
        if (StringUtils.isBlank(tabLink) || StringUtils.isNotBlank(switchTab)) {
            request.getSession()
                    .setAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE,
                        RADIOLOGY_ORDERS_TAB_REQUEST_MAPPING);
        } else {
            if (!RADIOLOGY_ORDERS_TAB_REQUEST_MAPPING.equals(tabLink)) {
                modelAndView.setViewName("redirect:" + tabLink);
            }
        }
        
        return modelAndView;
    }
    
    /**
     * Returns entries for urgency select element to filter radiology orders.
     * 
     * @return a map containing all urgency values and an entry to select all urgencies
     * @should return a map containing all urgency values and an entry to select all urgencies
     */
    @ModelAttribute("urgencies")
    protected Map<String, String> getUrgenciesList() {
        
        final Map<String, String> urgencies = new HashMap<String, String>();
        urgencies.put("", "allurgencies");
        
        for (final Urgency urgency : Urgency.values()) {
            urgencies.put(urgency.name(), urgency.name());
        }
        
        return urgencies;
    }
    
}
