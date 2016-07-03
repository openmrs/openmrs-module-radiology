/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.openmrs.CareSetting;
import org.openmrs.ConceptClass;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.OrderType;
import org.openmrs.VisitType;
import org.openmrs.module.emrapi.utils.ModuleProperties;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

/**
 * Properties, mostly configured via GPs for this module.
 */
@Component
public class RadiologyProperties extends ModuleProperties {
    
    
    /**
     * Return DICOM UID component used to identify the org root.
     * 
     * @return dicom uid org root
     * @throws IllegalStateException if global property for dicom uid org root cannot be found
     * @should return dicom uid org root
     * @should throw illegal state exception if global property for dicom uid org root cannot be found
     */
    public String getDicomUIDOrgRoot() {
        return getGlobalProperty(RadiologyConstants.GP_DICOM_UID_ORG_ROOT, true);
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
            throw new IllegalStateException(
                    "No existing care setting for uuid: " + RadiologyConstants.GP_RADIOLOGY_CARE_SETTING);
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
        return getOrderTypeByGlobalProperty(RadiologyConstants.GP_RADIOLOGY_TEST_ORDER_TYPE);
    }
    
    /**
     * Get EncounterType for RadiologyOrder's
     * 
     * @return EncounterType for radiology orders
     * @should return encounter type for radiology orders
     * @should throw illegal state exception for non existing radiology encounter type
     */
    public EncounterType getRadiologyOrderEncounterType() {
        return getEncounterTypeByGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDER_ENCOUNTER_TYPE);
    }
    
    /**
     * Get EncounterRole for the ordering provider
     * 
     * @return EncounterRole for ordering provider
     * @should return encounter role for ordering provider
     * @should throw illegal state exception for non existing ordering provider encounter role
     */
    public EncounterRole getRadiologyOrderingProviderEncounterRole() {
        return getEncounterRoleByGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDERING_PROVIDER_ENCOUNTER_ROLE);
    }
    
    /**
     * Get VisitType for RadiologyOrder's
     * 
     * @return visitType for radiology orders
     * @should return visit type for radiology orders
     * @should throw illegal state exception for non existing radiology visit type
     */
    public VisitType getRadiologyVisitType() {
        return getVisitTypeByGlobalProperty(RadiologyConstants.GP_RADIOLOGY_VISIT_TYPE);
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
        for (final String radiologyConceptClassUuid : radiologyConceptClassUuids) {
            final ConceptClass fetchedConceptClass = conceptService.getConceptClassByUuid(radiologyConceptClassUuid);
            if (fetchedConceptClass == null) {
                throw new IllegalStateException("Property radiology.radiologyConceptClasses contains UUID "
                        + radiologyConceptClassUuid + " which cannot be found as ConceptClass in the database.");
            }
            result = result + fetchedConceptClass.getName() + ",";
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }
    
    /**
     * Gets folder to store {@code MRRT} templates.
     * 
     * @return templates folder
     */
    public File getReportTemplateHome() {
        
        Path templatesPath = Paths.get(getGlobalProperty(RadiologyConstants.GP_MRRT_REPORT_TEMPLATE_DIR, true));
        
        if (!templatesPath.isAbsolute()) {
            templatesPath = Paths.get(OpenmrsUtil.getApplicationDataDirectory(), templatesPath.toString());
        }
        if (!templatesPath.toFile()
                .exists()) {
            templatesPath.toFile()
                    .mkdirs();
        }
        
        return templatesPath.toFile();
    }
}
