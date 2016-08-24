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

import org.apache.commons.lang3.StringUtils;
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
     * Gets the names of the concept classes for the UUIDs from the config
     *
     * @return a string that contains the names of the concept classes seperated by a comma
     * @throws IllegalStateException if global property radiologyConceptClasses is null
     * @throws IllegalStateException if global property radiologyConceptClasses is an empty string
     * @throws IllegalStateException if global property radiologyConceptClasses is badly formatted
     * @throws IllegalStateException if global property radiologyConceptClasses contains a UUID not found among concept
     *         classes
     * @should throw illegal state exception if global property radiology concept classes is null
     * @should throw illegal state exception if global property radiology concept classes is an empty
     *         string
     * @should throw illegal state exception if global property radiology concept classes is badly
     *         formatted
     * @should throw illegal state exception if global property radiology concept classes contains a
     *         UUID not found among concept classes
     * @should return comma separated list of concept class names configured via concept class UUIDs
     *         in global property radiology concept classes
     */
    public String getRadiologyConceptClassNames() {
        
        String radiologyConceptClassUuidSetting = getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES, true);
        radiologyConceptClassUuidSetting = radiologyConceptClassUuidSetting.replace(" ", "");
        if (!radiologyConceptClassUuidSetting.matches("^[0-9a-fA-f,-]+$")) {
            throw new IllegalStateException("Property " + RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES
                    + " needs to be a comma separated list of concept class UUIDs (allowed characters [a-z][A-Z][0-9][,][-][ ])");
        }
        
        final String[] radiologyConceptClassUuids = radiologyConceptClassUuidSetting.split(",");
        
        String result = "";
        for (final String radiologyConceptClassUuid : radiologyConceptClassUuids) {
            final ConceptClass fetchedConceptClass = conceptService.getConceptClassByUuid(radiologyConceptClassUuid);
            if (fetchedConceptClass == null) {
                throw new IllegalStateException(
                        "Property " + RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES + " contains UUID "
                                + radiologyConceptClassUuid + " which cannot be found as ConceptClass in the database.");
            }
            result = result + fetchedConceptClass.getName() + ",";
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }
    
    /**
     * Gets the names of the concept classes for the UUIDs from the global property radiologyOrderReasonConceptClasses
     *
     * @return a string that contains the names of the concept classes for radiology order reason seperated by a comma
     * @throws IllegalStateException if global property radiologyOrderReasonConceptClasses is badly formatted
     * @throws IllegalStateException if global property radiologyOrderReasonConceptClasses contains a UUID not found among
     *         concept classes
     * @should throw illegal state exception if global property radiology order reason concept classes is badly formatted
     * @should throw illegal state exception if global property radiology order reason concept classes contains a UUID not
     *         found among concept classes
     * @should return the name of the diagnosis concept class if global property radiology order reason concept classes is
     *         null
     * @should return the name of the diagnosis concept class if global property radiology order reason concept classes is an
     *         empty string
     * @should return an empty string if global property radiology order reason concept classes is null or an empty string
     *         and no diagnosis concept class is present
     * @should return comma separated list of concept class names configured via concept class UUIDs in global property
     *         radiology order reason concept classes
     */
    public String getRadiologyOrderReasonConceptClassNames() {
        
        String radiologyReasonConceptClassUuidSetting =
                getGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES, false);
        if (StringUtils.isBlank(radiologyReasonConceptClassUuidSetting)) {
            final ConceptClass diagnosisConceptClass = conceptService.getConceptClassByName("Diagnosis");
            if (diagnosisConceptClass == null) {
                return "";
            }
            return diagnosisConceptClass.getName();
        }
        
        radiologyReasonConceptClassUuidSetting = radiologyReasonConceptClassUuidSetting.replace(" ", "");
        if (!radiologyReasonConceptClassUuidSetting.matches("^[0-9a-fA-f,-]*$")) {
            throw new IllegalStateException("Property " + RadiologyConstants.GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES
                    + " needs to be a comma separated list of concept class UUIDs (allowed characters [a-z][A-Z][0-9][,][-][ ])");
        }
        final String[] radiologyReasonConceptClassUuids = radiologyReasonConceptClassUuidSetting.split(",");
        
        String result = "";
        for (final String radiologyReasonConceptClassUuid : radiologyReasonConceptClassUuids) {
            final ConceptClass fetchedConceptClass = conceptService.getConceptClassByUuid(radiologyReasonConceptClassUuid);
            if (fetchedConceptClass == null) {
                throw new IllegalStateException("Property " + RadiologyConstants.GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES
                        + " contains UUID " + radiologyReasonConceptClassUuid
                        + " which cannot be found as ConceptClass in the database.");
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
     * @throws IllegalStateException if global property cannot be found
     * @should create a directory under the openmrs application data directory if GP value is relative
     * @should creates a directory at GP value if it is an absolute path
     * @should throw illegal state exception if global property cannot be found
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
