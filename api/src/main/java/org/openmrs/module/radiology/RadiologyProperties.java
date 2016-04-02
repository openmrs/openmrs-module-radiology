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
import org.openmrs.ConceptClass;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.OrderType;
import org.openmrs.module.emrapi.utils.ModuleProperties;
import org.springframework.stereotype.Component;

/**
 * Properties, mostly configured via GPs for this module.
 */
@Component
public class RadiologyProperties extends ModuleProperties {
	
	/**
	 * Return mpps directory.
	 * 
	 * @return mpps directory
	 */
	public String getMppsDir() {
		return getGlobalProperty(RadiologyConstants.GP_MPPS_DIR, true);
	}
	
	/**
	 * Return mwl directory.
	 * 
	 * @return mwl directory
	 */
	public String getMwlDir() {
		return getGlobalProperty(RadiologyConstants.GP_MWL_DIR, true);
	}
	
	/**
	 * Return PACS address.
	 * 
	 * @return pacs address
	 * @throws IllegalStateException if global property for pacs address cannot be found
	 * @should return pacs address
	 * @should throw illegal state exception if global property for pacs address cannot be found
	 */
	public String getPacsAddress() {
		return getGlobalProperty(RadiologyConstants.GP_PACS_ADDRESS, true);
	}
	
	/**
	 * Return PACS DICOM MPPS port.
	 * 
	 * @return pacs dicom mpps port
	 * @throws IllegalStateException if global property for pacs dicom mpps port cannot be found
	 * @should return pacs dicom mpps port
	 * @should throw illegal state exception if global property for pacs dicom mpps port cannot be found
	 */
	public String getPacsDicomMppsPort() {
		return getGlobalProperty(RadiologyConstants.GP_PACS_DICOM_MPPS_PORT, true);
	}
	
	/**
	 * Return PACS HL7 port.
	 * 
	 * @return pacs hl7 port
	 * @throws IllegalStateException if global property for pacs hl7 port cannot be found
	 * @should return pacs hl7 port
	 * @should throw illegal state exception if global property for pacs hl7 port cannot be found
	 */
	public String getPacsHL7Port() {
		return getGlobalProperty(RadiologyConstants.GP_PACS_HL7_PORT, true);
	}
	
	/**
	 * Return PACS DICOM AE title.
	 * 
	 * @return pacs dicom ae title
	 * @throws IllegalStateException if global property for pacs dicom ae title cannot be found
	 * @should return pacs dicom ae title
	 * @should throw illegal state exception if global property for pacs dicom ae title cannot be found
	 */
	public String getPacsDicomAeTitle() {
		return getGlobalProperty(RadiologyConstants.GP_PACS_DICOM_AE_TITLE, true);
	}
	
	/**
	 * Return DICOM AE title.
	 * 
	 * @return dicom ae title
	 * @throws IllegalStateException if global property for dicom ae title cannot be found
	 * @should return dicom ae title
	 * @should throw illegal state exception if global property for dicom ae title cannot be found
	 */
	public String getDicomAeTitle() {
		return getGlobalProperty(RadiologyConstants.GP_DICOM_AE_TITLE, true);
	}
	
	/**
	 * Return DICOM Application UID.
	 * 
	 * @return dicom application uid
	 * @throws IllegalStateException if global property for dicom application uid cannot be found
	 * @should return dicom application uid
	 * @should throw illegal state exception if global property for dicom application uid cannot be found
	 */
	public String getDicomApplicationUID() {
		return getGlobalProperty(RadiologyConstants.GP_DICOM_APPLICATION_UID, true);
	}
	
	/**
	 * Return DICOM study uid slug.
	 * 
	 * @return dicom study uid slug
	 * @throws IllegalStateException if global property for dicom study uid slug cannot be found
	 * @should return dicom study uid slug
	 * @should throw illegal state exception if global property for dicom study uid slug cannot be found
	 */
	public String getDicomStudyUIDSlug() {
		return getGlobalProperty(RadiologyConstants.GP_DICOM_STUDY_UID_SLUG, true);
	}
	
	/**
	 * Return DICOM specific character set.
	 * 
	 * @return dicom specific character set
	 * @throws IllegalStateException if global property for dicom specific character set cannot be found
	 * @should return dicom specific character set
	 * @should throw illegal state exception if global property for dicom specific character set cannot be found
	 */
	public String getDicomSpecificCharacterSet() {
		return getGlobalProperty(RadiologyConstants.GP_DICOM_SPECIFIC_CHARCATER_SET, true);
	}
	
	/**
	 * Return study prefix Example: 1.2.826.0.1.3680043.8.2186.1. (With last dot)
	 * 
	 * @return study prefix
	 * @should return study prefix consisting of application uid and study uid slug
	 */
	public String getStudyPrefix() {
		return getDicomApplicationUID() + "." + getDicomStudyUIDSlug() + ".";
	}
	
	/**
	 * Return DICOM web viewer address.
	 * 
	 * @return DICOM web viewer address
	 * @throws IllegalStateException if global property for dicom web viewer address cannot be found
	 * @should return dicom web viewer address
	 * @should throw illegal state exception if global property for dicom web viewer address cannot
	 *         be found
	 */
	public String getDicomWebViewerAddress() {
		return getGlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_ADDRESS, true);
	}
	
	/**
	 * Return DICOM web viewer port.
	 * 
	 * @return DICOM web viewer port
	 * @throws IllegalStateException if global property for dicom web viewer port cannot be found
	 * @should return dicom web viewer port
	 * @should throw illegal state exception if global property for dicom web viewer port cannot be
	 *         found
	 */
	public String getDicomWebViewerPort() {
		return getGlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_PORT, true);
	}
	
	/**
	 * Return DICOM web viewer base url.
	 * 
	 * @return DICOM web viewer base url
	 * @throws IllegalStateException if global property for dicom web viewer base url cannot be
	 *         found
	 * @should return dicom web viewer base url
	 * @should throw illegal state exception if global property for dicom web viewer base url cannot
	 *         be found
	 */
	public String getDicomWebViewerBaseUrl() {
		return getGlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_BASE_URL, true);
	}
	
	/**
	 * Return DICOM web viewer local server name.
	 * 
	 * @return DICOM web viewer local server name
	 * @should return dicom web viewer local server name
	 */
	public String getDicomWebViewerLocalServerName() {
		return getGlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_LOCAL_SERVER_NAME, false);
	}
	
	/**
	 * Get CareSetting for RadiologyOrder's
	 * 
	 * @return CareSetting for radiology orders
	 * @should return radiology care setting
	 * @should throw illegal state exception if global property for radiology care setting cannot be
	 *         found
	 * @should throw illegal state exception if radiology care setting cannot be found
	 */
	public CareSetting getRadiologyCareSetting() {
		final String radiologyCareSettingUuid = getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CARE_SETTING, true);
		final CareSetting result = orderService.getCareSettingByUuid(radiologyCareSettingUuid);
		if (result == null) {
			throw new IllegalStateException("No existing care setting for uuid: "
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
	public OrderType getRadiologyTestOrderType() {
		final OrderType result = orderService.getOrderTypeByUuid(RadiologyConstants.RADIOLOGY_TEST_ORDER_TYPE_UUID);
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
	public EncounterType getRadiologyEncounterType() {
		final EncounterType result = encounterService.getEncounterTypeByUuid(RadiologyConstants.RADIOLOGY_ENCOUNTER_TYPE_UUID);
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
	public EncounterRole getOrderingProviderEncounterRole() {
		final EncounterRole result = encounterService.getEncounterRoleByUuid(RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID);
		if (result == null) {
			throw new IllegalStateException("EncounterRole for ordering provider not in database (not found under uuid="
					+ RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID + ").");
		}
		return result;
	}
	
	/**
	 * Gets the Name of the ConceptClass for the UUID from the config
	 *
	 * @return a String that contains the Names of the ConceptClasses seperated by a comma
	 * @should throw illegal state exception if global property radiologyConceptClasses is null
	 * @should throw illegal state exception if global property radiologyConceptClasses is an empty
	 *         string
	 * @should throw illegal state exception if global property radiologyConceptClasses is badly
	 *         formatted
	 * @should throw illegal state exception if global property radiologyConceptClasses contains a
	 *         UUID not found among ConceptClasses
	 * @should returns comma separated list of ConceptClass names configured via ConceptClass UUIDs
	 *         in global property radiologyConceptClasses
	 */
	public String getRadiologyConceptClassNames() {
		
		String radiologyConceptClassUuidSetting = getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES, true);
		radiologyConceptClassUuidSetting = radiologyConceptClassUuidSetting.replace(" ", "");
		if (!radiologyConceptClassUuidSetting.matches("^[0-9a-fA-f,-]+$")) {
			throw new IllegalStateException(
					"Property radiology.radiologyConcepts needs to be a comma separated list of concept class UUIDs (allowed characters [a-z][A-Z][0-9][,][-][ ])");
		}
		
		final String[] radiologyConceptClassUuids = radiologyConceptClassUuidSetting.split(",");
		
		String result = "";
		for (String radiologyConceptClassUuid : radiologyConceptClassUuids) {
			ConceptClass fetchedConceptClass = conceptService.getConceptClassByUuid(radiologyConceptClassUuid);
			if (fetchedConceptClass == null) {
				throw new IllegalStateException("Property radiology.radiologyConceptClasses contains UUID "
						+ radiologyConceptClassUuid + " which cannot be found as ConceptClass in the database.");
			}
			result = result + fetchedConceptClass.getName() + ",";
		}
		result = result.substring(0, result.length() - 1);
		return result;
	}
}
