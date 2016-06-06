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
 * A class that contains constants which are used within this module.
 */
public class RadiologyConstants {
    
    
    public static final String GP_DICOM_UID_ORG_ROOT = "radiology.dicomUIDOrgRoot";
    
    public static final String GP_DICOM_WEB_VIEWER_ADDRESS = "radiology.dicomWebViewerAddress";
    
    public static final String GP_DICOM_WEB_VIEWER_PORT = "radiology.dicomWebViewerPort";
    
    public static final String GP_DICOM_WEB_VIEWER_BASE_URL = "radiology.dicomWebViewerBaseUrl";
    
    public static final String GP_DICOM_WEB_VIEWER_LOCAL_SERVER_NAME = "radiology.dicomWebViewerLocalServerName";
    
    public static final String GP_RADIOLOGY_CARE_SETTING = "radiology.radiologyCareSetting";
    
    public static final String GP_RADIOLOGY_CONCEPT_CLASSES = "radiology.radiologyConceptClasses";
    
    public static final String GP_RADIOLOGY_TEST_ORDER_TYPE = "radiology.radiologyTestOrderType";
    
    public static final String GP_RADIOLOGY_ORDER_ENCOUNTER_TYPE = "radiology.radiologyOrderEncounterType";
    
    public static final String GP_RADIOLOGY_ORDERING_PROVIDER_ENCOUNTER_ROLE =
            "radiology.radiologyOrderingProviderEncounterRole";
    
    public static final String GP_RADIOLOGY_VISIT_TYPE = "radiology.radiologyVisitType";
    
    public static final String GP_NEXT_ACCESSION_NUMBER_SEED = "radiology.nextAccessionNumberSeed";
    
    public static final String GP_MRRT_REPORT_TEMPLATE_DIR = "radiology.reportTemplatesHome";
    
    private RadiologyConstants() {
        // Utility class not meant to be instantiated.
    }
}
