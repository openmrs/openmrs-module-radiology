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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
import org.openmrs.CareSetting;
import org.openmrs.ConceptClass;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.OrderType;
import org.openmrs.VisitType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.VisitService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Properties, mostly configured via GPs for this module.
 */
@Component
public class RadiologyProperties {
    
    
    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ConceptService conceptService;
    
    @Autowired
    private EncounterService encounterService;
    
    @Autowired
    private VisitService visitService;
    
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
        final CareSetting result =
                orderService.getCareSettingByUuid(getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CARE_SETTING, true));
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
        return orderService.getOrderTypeByUuid(getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_TEST_ORDER_TYPE, true));
    }
    
    /**
     * Get EncounterType for RadiologyOrder's
     * 
     * @return EncounterType for radiology orders
     * @should return encounter type for radiology orders
     * @should throw illegal state exception for non existing radiology encounter type
     */
    public EncounterType getRadiologyOrderEncounterType() {
        return encounterService
                .getEncounterTypeByUuid(getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDER_ENCOUNTER_TYPE, true));
    }
    
    /**
     * Get EncounterRole for the ordering provider
     * 
     * @return EncounterRole for ordering provider
     * @should return encounter role for ordering provider
     * @should throw illegal state exception for non existing ordering provider encounter role
     */
    public EncounterRole getRadiologyOrderingProviderEncounterRole() {
        return encounterService.getEncounterRoleByUuid(
            getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDERING_PROVIDER_ENCOUNTER_ROLE, true));
    }
    
    /**
     * Get VisitType for RadiologyOrder's
     * 
     * @return visitType for radiology orders
     * @should return visit type for radiology orders
     * @should throw illegal state exception for non existing radiology visit type
     */
    public VisitType getRadiologyVisitType() {
        return visitService.getVisitTypeByUuid(getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_VISIT_TYPE, true));
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
     * Gets a global property by its name.
     * 
     * @param globalPropertyName the name of the requested global property
     * @param required indicates if the global property must be configured
     * @return value of global property for given name
     * @throws IllegalStateException if global property cannot be found
     * @should return global property given valid global property name
     * @should return null given non required and non configured global property
     * @should throw illegal state exception given required non configured global property
     */
    private String getGlobalProperty(String globalPropertyName, boolean required) {
        final String result = administrationService.getGlobalProperty(globalPropertyName);
        if (required && StringUtils.isBlank(result)) {
            throw new IllegalStateException("Configuration required: " + globalPropertyName);
        }
        return result;
    }
    
    /**
     * Gets folder to store {@code MRRT} templates.
     * 
     * @return templates folder
     * @should return directory specified by global property
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
