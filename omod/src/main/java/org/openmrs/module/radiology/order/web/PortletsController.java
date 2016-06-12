/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PortletsController {
    
    
    private static final Log log = LogFactory.getLog(PortletsController.class);
    
    /**
     * Get URL to the patientOverview portlet
     * 
     * @return patient info route
     * @should return string with patient info route
     */
    @RequestMapping("/module/radiology/portlets/patientOverview.portlet")
    String getPatientInfoRoute() {
        return "module/radiology/portlets/patientOverview";
    }
}
