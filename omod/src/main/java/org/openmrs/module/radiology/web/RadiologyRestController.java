/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.web;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Radiology Rest Services.
 */
@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + RadiologyRestController.RADIOLOGY_REST_NAMESPACE)
public class RadiologyRestController extends MainResourceController {
    
    
    public static final String RADIOLOGY_REST_NAMESPACE = "/radiology";
    
    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController#getNamespace()
     */
    @Override
    public String getNamespace() {
        return RestConstants.VERSION_1 + RADIOLOGY_REST_NAMESPACE;
    }
}
