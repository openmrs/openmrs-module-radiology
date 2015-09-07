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

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

/**
 * Properties, mostly configured via GPs for this module.
 */
public class RadiologyProperties {
	
	private static AdministrationService administrationService = Context.getAdministrationService();
	
	public static String getAeTitle() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_APPLICATION_ENTITY_TITLE);
	}
	
	public static String getMppsDir() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_MPPS_DIR);
	}
	
	public static String getMwlDir() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_MWL_DIR);
	}
	
	public static String getMwlMppsPort() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_MWL_MPPS_PORT);
	}
	
	public static String getServersAddress() {
		return "http://" + administrationService.getGlobalProperty(RadiologyConstants.GP_SERVERS_ADDRESS);
	}
	
	/**
	 * Prefix for DICOM objects in the application, Ex: 1.2.826.0.1.3680043.8.2186
	 */
	public static String getApplicationUID() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_APPLICATION_UID);
	}
	
	public static String getStudyUIDSlug() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_STUDY_UID_SLUG);
	}
	
	public static String getSpecificCharacterSet() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_SPECIFIC_CHARCATER_SET);
	}
	
	public static String getDevModeP() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_DEV_MODE_P);
	}
	
	/**
	 * Example: 1.2.826.0.1.3680043.8.2186.1. (With last dot)
	 */
	public static String getStudyPrefix() {
		return getApplicationUID() + "." + getStudyUIDSlug() + ".";
	}
	
	public static boolean getDevMode() {
		return RadiologyConstants.GP_DEV_MODE_P.compareToIgnoreCase("on") == 0;
	}
	
	public static String getServersPort() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_SERVERS_PORT);
	}
	
	public static String getServersHL7Port() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_SERVERS_HL7_PORT);
	}
	
	public static String getDicomViewerLocalServerName() {
		String dicomViewerLocalServerName = administrationService
		        .getGlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME);
		if (dicomViewerLocalServerName == null)
			return "";
		else
			return "serverName=" + dicomViewerLocalServerName + "&";
	}
	
	public static String getDicomViewerUrlBase() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_URL_BASE);
	}
	
	public static String getDicomViewerUrl() {
		String dicomViewerUrl = getServersAddress() + ":" + getServersPort() + getDicomViewerUrlBase()
		        + getDicomViewerLocalServerName();
		return dicomViewerUrl;
	}
	
}
