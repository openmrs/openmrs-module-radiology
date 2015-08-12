/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.idSet;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.privilege;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.role;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.util.RoleConstants;
import org.springframework.stereotype.Component;

@Component("radiologyRolePrivilegeMetadata")
public class RadiologyRolePrivilegeMetadata extends AbstractMetadataBundle {
	
	/**
	 * Constants for privileges
	 */
	public static class _Privilege {
		
		public static final String APP_RADIOLOGY_VIEW_RADIOLOGY_SECTION = "Patient Dashboard - View Radiology Section";
	}
	
	/**
	 * Constants for roles
	 */
	public static class _Role {
		
		public static final String RADIOLOGY_SCHEDULER = "Radiology: Scheduler";
		
		public static final String RADIOLOGY_REFERRING_PHYSICIAN = "Radiology: Referring Physician";
		
		public static final String RADIOLOGY_PERFORMING_PHYSICIAN = "Radiology: Performing Physician";
		
		public static final String RADIOLOGY_READING_PHYSICIAN = "Radiology: Reading Physician";
	}
	
	/**
	 * install privileges, install roles, inherit privileges from provider
	 */
	@Override
	public void install() throws Exception {
		install(privilege(_Privilege.APP_RADIOLOGY_VIEW_RADIOLOGY_SECTION,
		    "Able to view the 'Radiology' tab on the patient dashboard"));
		
		install(role(_Role.RADIOLOGY_SCHEDULER, "Scheduler", idSet(RoleConstants.PROVIDER),
		    idSet(_Privilege.APP_RADIOLOGY_VIEW_RADIOLOGY_SECTION)));
		
		install(role(_Role.RADIOLOGY_REFERRING_PHYSICIAN, "Referring Physician", idSet(RoleConstants.PROVIDER),
		    idSet(_Privilege.APP_RADIOLOGY_VIEW_RADIOLOGY_SECTION)));
		
		install(role(_Role.RADIOLOGY_PERFORMING_PHYSICIAN, "Performing Physician", idSet(RoleConstants.PROVIDER),
		    idSet(_Privilege.APP_RADIOLOGY_VIEW_RADIOLOGY_SECTION)));
		
		install(role(_Role.RADIOLOGY_READING_PHYSICIAN, "Reading Physician", idSet(RoleConstants.PROVIDER),
		    idSet(_Privilege.APP_RADIOLOGY_VIEW_RADIOLOGY_SECTION)));
		
	}
}
