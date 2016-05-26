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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import org.openmrs.Patient;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.web.controller.PortletController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("**/patientDashboardRadiologyTab.portlet")
public class PatientDashboardRadiologyTabPortletController extends PortletController {
    
    public static final String PATIENT_DASHBOARD_RADIOLOGY_TAB = "patientDashboardRadiologyTab.portlet";
    
    @Autowired
    private RadiologyOrderService radiologyOrderService;
    
    /**
     * Add radiologyOrders to the <code>model</code> for patient contained in the <code>model</code>.
     * 
     * @param request HttpServletRequest that holds all information about this request
     * @param model holds variables that will be used in the jsp view
     * @should model is populated with all radiology orders for given patient
     * @should model is populated with an empty list of radiology orders if given patient is unknown
     */
    @Override
    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
        
        final Patient patient = (Patient) model.get("patient");
        final List<RadiologyOrder> radiologyOrders = radiologyOrderService.getRadiologyOrdersByPatient(patient);
        model.put("radiologyOrders", radiologyOrders);
    }
}
