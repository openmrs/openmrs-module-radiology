/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.web.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;
import static org.openmrs.module.radiology.order.web.RadiologyDashboardOrdersTabController.RADIOLOGY_ORDERS_TAB_REQUEST_MAPPING;

public class AdminList extends AdministrationSectionExt {
    
    
    @Override
    public Extension.MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }
    
    @Override
    public String getTitle() {
        return "radiology.title";
    }
    
    @Override
    public Map<String, String> getLinks() {
        
        final Map<String, String> map = new HashMap<String, String>();
        
        map.put(RADIOLOGY_ORDERS_TAB_REQUEST_MAPPING, "radiology.administrationSection.links.dashboard");
        
        return map;
    }
    
}
