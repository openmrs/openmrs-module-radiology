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

import static org.openmrs.module.radiology.RadiologyPrivileges.VIEW_RADIOLOGY_SECTION;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.PatientDashboardTabExt;

public class RadiologyDashboardExt extends PatientDashboardTabExt {
	
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getPortletUrl() {
		return "RadiologyDashboardTab";
	}
	
	@Override
	public String getRequiredPrivilege() {
		return VIEW_RADIOLOGY_SECTION;
	}
	
	@Override
	public String getTabId() {
		return "RadiologyTab";
	}
	
	@Override
	public String getTabName() {
		return "Radiology";
	}
	
}
