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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.openmrs.web.controller.PortletController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the radiology reports tab portlet.
 */
@Controller
@RequestMapping("**/radiologyReportsTab.portlet")
public class RadiologyReportsTabPortletController extends PortletController {
    
    
    /**
     * @see org.openmrs.web.controller.PortletController#populateModel(javax.servlet.http.HttpServletRequest,
     *      java.util.Map)
     * @should populate model with an entry containing all report status values and an empty string
     */
    @Override
    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
        
        final List<String> radiologyReportStatuses = new LinkedList<String>();
        radiologyReportStatuses.add("");
        
        for (final RadiologyReportStatus status : RadiologyReportStatus.values()) {
            radiologyReportStatuses.add(status.name());
        }
        
        model.put("radiologyReportStatuses", radiologyReportStatuses);
    }
}
