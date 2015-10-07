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

import org.openmrs.CareSetting;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.OrderType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;

/**
 * Properties, mostly configured via GPs for this module.
 */
public class RadiologyProperties {
	
	private static AdministrationService administrationService = Context.getAdministrationService();
	
	private static OrderService orderService = Context.getOrderService();
	
	private static EncounterService encounterService = Context.getEncounterService();
	
	/**
	 * Return application entity title
	 * 
	 * @return application entity title
	 */
	public static String getApplicationEntityTitle() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_APPLICATION_ENTITY_TITLE);
	}
	
	/**
	 * Return mpps directory
	 * 
	 * @return mpps directory
	 */
	public static String getMppsDir() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_MPPS_DIR);
	}
	
	/**
	 * Return mwl directory
	 * 
	 * @return mwl directory
	 */
	public static String getMwlDir() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_MWL_DIR);
	}
	
	/**
	 * Return mwl mpps port
	 * 
	 * @return mwl mpps port
	 */
	public static String getMwlMppsPort() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_MWL_MPPS_PORT);
	}
	
	/**
	 * Return Server Address
	 * 
	 * @return server address
	 */
	public static String getServersAddress() {
		return "http://" + administrationService.getGlobalProperty(RadiologyConstants.GP_SERVERS_ADDRESS);
	}
	
	/**
	 * Return prefix for dicom objects in the application, Ex: 1.2.826.0.1.3680043.8.2186
	 * 
	 * @return prefix for dicom objects in the application
	 */
	public static String getApplicationUID() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_APPLICATION_UID);
	}
	
	/**
	 * Return study uid slug
	 * 
	 * @return study uid slug
	 */
	public static String getStudyUIDSlug() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_STUDY_UID_SLUG);
	}
	
	/**
	 * Return specific character set
	 * 
	 * @return specific character set
	 */
	public static String getSpecificCharacterSet() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_SPECIFIC_CHARCATER_SET);
	}
	
	/**
	 * Return study prefix Example: 1.2.826.0.1.3680043.8.2186.1. (With last dot)
	 * 
	 * @return study prefix
	 * @should should return study prefix consisting of application uid and study uid slug
	 */
	public static String getStudyPrefix() {
		return getApplicationUID() + "." + getStudyUIDSlug() + ".";
	}
	
	/**
	 * Return servers port
	 * 
	 * @return servers port
	 */
	public static String getServersPort() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_SERVERS_PORT);
	}
	
	/**
	 * Return servers hl7 port
	 * 
	 * @return servers hl7 port
	 */
	public static String getServersHL7Port() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_SERVERS_HL7_PORT);
	}
	
	/**
	 * Return server name of local dicom viewer
	 * 
	 * @return server name of local dicom viewer
	 * @should return dicom viewer local server name if defined in global properties
	 * @should return empty string if dicom viewer local server name if not defined in global
	 *         properties
	 */
	public static String getDicomViewerLocalServerName() {
		String dicomViewerLocalServerName = administrationService
		        .getGlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME);
		if (dicomViewerLocalServerName == null)
			return "";
		else
			return "serverName=" + dicomViewerLocalServerName + "&";
	}
	
	/**
	 * Return dicom viewer url base
	 * 
	 * @return dicom viewer url base
	 */
	public static String getDicomViewerUrlBase() {
		return administrationService.getGlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_URL_BASE);
	}
	
	/**
	 * Return url of dicom viewer
	 * 
	 * @return url of dicom viewer as string
	 * @should return dicom viewer url consisting of server adress and server port and dicom viewer
	 *         url base and dicom viewer local server name for oviyam
	 * @should return dicom viewer url consisting of server adress and server port and dicom viewer
	 *         url base and dicom viewer local server name for weasis
	 */
	public static String getDicomViewerUrl() {
		String dicomViewerUrl = getServersAddress() + ":" + getServersPort() + getDicomViewerUrlBase()
		        + getDicomViewerLocalServerName();
		return dicomViewerUrl;
	}
	
	/**
	 * Get CareSetting for RadiologyOrder's
	 * 
	 * @return CareSetting for radiology orders
	 * @should return radiology care setting
	 * @should throw illegal state exception if radiology care setting cannot be found
	 */
	public static CareSetting getRadiologyCareSetting() {
		String radiologyCareSettingUuid = administrationService
		        .getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CARE_SETTING);
		CareSetting result = orderService.getCareSettingByUuid(radiologyCareSettingUuid);
		
		if (result == null) {
			throw new IllegalStateException("Configuration required for property: "
			        + RadiologyConstants.GP_RADIOLOGY_CARE_SETTING);
		}
		return result;
	}
	
	/**
	 * Test order type for radiology order
	 * 
	 * @return test order type for radiology order
	 * @should return order type for radiology test orders
	 * @should throw illegal state exception for non existing radiology test order type
	 */
	public static OrderType getRadiologyTestOrderType() {
		OrderType result = orderService.getOrderTypeByUuid(RadiologyConstants.RADIOLOGY_TEST_ORDER_TYPE_UUID);
		if (result == null) {
			throw new IllegalStateException("OrderType for radiology orders not in database (not found under uuid="
			        + RadiologyConstants.RADIOLOGY_TEST_ORDER_TYPE_UUID + ").");
		}
		return result;
	}
	
	/**
	 * Get EncounterType for RadiologyOrder's
	 * 
	 * @return EncounterType for radiology orders
	 * @should return encounter type for radiology orders
	 * @should throw illegal state exception for non existing radiology encounter type
	 */
	public static EncounterType getRadiologyEncounterType() {
		EncounterType result = encounterService.getEncounterTypeByUuid(RadiologyConstants.RADIOLOGY_ENCOUNTER_TYPE_UUID);
		if (result == null) {
			throw new IllegalStateException("EncounterType for radiology orders not in database (not found under uuid="
			        + RadiologyConstants.RADIOLOGY_ENCOUNTER_TYPE_UUID + ").");
		}
		return result;
	}
	
	/**
	 * Get EncounterRole for the ordering provider
	 * 
	 * @return EncounterRole for ordering provider
	 * @should return encounter role for ordering provider
	 * @should throw illegal state exception for non existing ordering provider encounter role
	 */
	public static EncounterRole getOrderingProviderEncounterRole() {
		EncounterRole result = encounterService
		        .getEncounterRoleByUuid(RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID);
		if (result == null) {
			throw new IllegalStateException("EncounterRole for ordering provider not in database (not found under uuid="
			        + RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID + ").");
		}
		return result;
	}
}
