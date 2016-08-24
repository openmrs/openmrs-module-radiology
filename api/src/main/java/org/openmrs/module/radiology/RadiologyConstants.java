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

/**
 * Utility class that contains constants for {@code GlobalProperty} properties which are used within this module.
 *
 * @see org.openmrs.module.radiology.RadiologyProperties
 */
public class RadiologyConstants {
    
    
    /**
     * {@code GlobalProperty} property for the DICOM UID component used to describe the org root.
     *
     * <p>Refer to DICOM Standard DICOM PS3.5 Chapter 9 Unique Identifiers.</p>
     */
    public static final String GP_DICOM_UID_ORG_ROOT = "radiology.dicomUIDOrgRoot";
    
    /**
     * {@code GlobalProperty} property for the IP address or hostname of the DICOM web viewer which is referred to for external DICOM images.
     *
     * @see org.openmrs.module.radiology.dicom.DicomWebViewer
     */
    public static final String GP_DICOM_WEB_VIEWER_ADDRESS = "radiology.dicomWebViewerAddress";
    
    /**
     * {@code GlobalProperty} property for the port of the DICOM web viewer which is referred to for external DICOM images.
     *
     * @see org.openmrs.module.radiology.dicom.DicomWebViewer
     */
    public static final String GP_DICOM_WEB_VIEWER_PORT = "radiology.dicomWebViewerPort";
    
    /**
     * {@code GlobalProperty} property for the base URL of the DICOM web viewer which is referred to for external DICOM images.
     *
     * @see org.openmrs.module.radiology.dicom.DicomWebViewer
     */
    public static final String GP_DICOM_WEB_VIEWER_BASE_URL = "radiology.dicomWebViewerBaseUrl";
    
    /**
     * {@code GlobalProperty} property for the serverName query parameter of the DICOM web viewer which is referred to for external DICOM images.
     *
     * @see org.openmrs.module.radiology.dicom.DicomWebViewer
     */
    public static final String GP_DICOM_WEB_VIEWER_LOCAL_SERVER_NAME = "radiology.dicomWebViewerLocalServerName";
    
    /**
     * {@code GlobalProperty} property for the UUID of the {@code CareSetting} which is used when creating a {@code RadiologyOrder}.
     */
    public static final String GP_RADIOLOGY_CARE_SETTING = "radiology.radiologyCareSetting";
    
    /**
     * {@code GlobalProperty} property for the comma separated list of concept class UUIDs which define the orderables for {@code RadiologyOrder's}.
     */
    public static final String GP_RADIOLOGY_CONCEPT_CLASSES = "radiology.radiologyConceptClasses";
    
    /**
     * {@code GlobalProperty} property for the comma separated list of concept class UUIDs which define the reasons for {@code RadiologyOrder's}.
     */
    public static final String GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES = "radiology.radiologyOrderReasonConceptClasses";
    
    /**
     * {@code GlobalProperty} property for the UUID of the {@code OrderType} which is used when creating a {@code RadiologyOrder}.
     */
    public static final String GP_RADIOLOGY_TEST_ORDER_TYPE = "radiology.radiologyTestOrderType";
    
    /**
     * {@code GlobalProperty} property for the UUID of the {@code EncounterType} which is used when creating a {@code RadiologyOrder}.
     */
    public static final String GP_RADIOLOGY_ORDER_ENCOUNTER_TYPE = "radiology.radiologyOrderEncounterType";
    
    /**
     * {@code GlobalProperty} property for the UUID of the {@code EncounterRole} which is used when creating an {@code Encounter} for {@code RadiologyOrder's}.
     */
    public static final String GP_RADIOLOGY_ORDERING_PROVIDER_ENCOUNTER_ROLE =
            "radiology.radiologyOrderingProviderEncounterRole";
    
    /**
     * {@code GlobalProperty} property for the UUID of the {@code VisitType} which can be used to create radiology {@code Visit's}.
     */
    public static final String GP_RADIOLOGY_VISIT_TYPE = "radiology.radiologyVisitType";
    
    /**
     * {@code GlobalProperty} property for the seed used to generate the next {@code Order.accessionNumber}.
     */
    public static final String GP_NEXT_ACCESSION_NUMBER_SEED = "radiology.nextAccessionNumberSeed";
    
    /**
     * {@code GlobalProperty} property for the directory where report templates are stored.
     * Allowable values are absolute and relative paths.
     */
    public static final String GP_MRRT_REPORT_TEMPLATE_DIR = "radiology.reportTemplatesHome";
    
    private RadiologyConstants() {
        // Utility class not meant to be instantiated.
    }
}
