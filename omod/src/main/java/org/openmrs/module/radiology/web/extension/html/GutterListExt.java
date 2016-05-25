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

import org.openmrs.module.radiology.RadiologyPrivileges;
import org.openmrs.module.radiology.order.web.RadiologyDashboardFormController;
import org.openmrs.module.web.extension.LinkExt;

public class GutterListExt extends LinkExt {
	
	@Override
	public String getLabel() {
		return "radiology.home.title";
	}
	
	@Override
	public String getRequiredPrivilege() {
		return RadiologyPrivileges.VIEW_RADIOLOGY_SECTION;
	}
	
	@Override
	public String getUrl() {
		return RadiologyDashboardFormController.RADIOLOGY_DASHBOARD_FORM_REQUEST_MAPPING;
	}
}
