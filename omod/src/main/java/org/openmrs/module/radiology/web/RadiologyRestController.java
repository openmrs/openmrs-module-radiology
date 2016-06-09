package org.openmrs.module.radiology.web;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Radiology Rest Services
 */
@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + RadiologyRestController.RADIOLOGY_REST_NAMESPACE)
public class RadiologyRestController extends MainResourceController {
    
    
    public static final String RADIOLOGY_REST_NAMESPACE = "/radiologyrest";
    
    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController#getNamespace()
     */
    @Override
    public String getNamespace() {
        return RestConstants.VERSION_1 + RADIOLOGY_REST_NAMESPACE;
    }
}
