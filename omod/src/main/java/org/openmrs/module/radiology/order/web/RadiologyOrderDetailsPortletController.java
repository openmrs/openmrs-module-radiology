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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.radiology.dicom.DicomWebViewer;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.web.controller.PortletController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the radiology order details portlet.
 */
@Controller
@RequestMapping("**/radiologyOrderDetails.portlet")
public class RadiologyOrderDetailsPortletController extends PortletController {
    
    
    @Autowired
    private DicomWebViewer dicomWebViewer;
    
    @Autowired
    private RadiologyOrderService radiologyOrderService;
    
    /**
     * @see org.openmrs.web.controller.PortletController#populateModel(javax.servlet.http.HttpServletRequest,
     *      java.util.Map)
     * @should populate model with radiology order if given order uuid model entry matches a radiology order and dicom viewer
     *         url if radiology order is completed
     * @should populate model with radiology order if given order uuid model entry matches a radiology order and no dicom
     *         viewer url if radiology order is not completed
     * @should not populate model with radiology order and dicom viewer url if no radiology order was found
     * @should not populate model with radiology order and dicom viewer url if model has no entry for order uuid
     */
    @Override
    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
        
        if (model.containsKey("radiologyOrder")) {
            return;
        }
        String orderUuid = (String) model.get("orderUuid");
        if (StringUtils.isBlank(orderUuid)) {
            return;
        }
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByUuid(orderUuid);
        if (radiologyOrder != null) {
            model.put("radiologyOrder", radiologyOrder);
            if (radiologyOrder.isCompleted()) {
                model.put("dicomViewerUrl", dicomWebViewer.getDicomViewerUrl(radiologyOrder.getStudy()));
            }
        }
    }
}
