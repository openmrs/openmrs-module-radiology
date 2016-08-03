/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.dicom.code;

/**
 * <p>
 * Represents the DICOM Performed Procedure Step Status (0040,0252) defined in DICOM PS3.3 2014b C.4.14 Performed Procedure
 * Step Information.
 * </p>
 */
public enum PerformedProcedureStepStatus {
    
    IN_PROGRESS,
    DISCONTINUED,
    COMPLETED;
    
    /**
     * Get name or UNKNOWN for given Performed Procedure Step Status
     * 
     * @param performedProcedureStepStatus PerformedProcedureStepStatus for which the name is
     *        returned
     * @return name of given PerformedProcedureStepStatus
     * @should return name given performed procedure step status
     * @should return unknown given null
     */
    public static String getNameOrUnknown(PerformedProcedureStepStatus performedProcedureStepStatus) {
        if (performedProcedureStepStatus == null) {
            return "UNKNOWN";
        } else {
            return performedProcedureStepStatus.name();
        }
    }
    
    /**
     * Get Performed Procedure Step Status for given displayName
     * 
     * @param displayName name defined by DICOM standard for which the PerformedProcedureStepStatus is returned
     * @return PerformedProcedureStepStatus PerformedProcedureStepStatus matching given displayName
     * @throws IllegalArgumentException
     * @should return performed procedure step status given display name
     * @should return null given undefined display name
     * @should throw IllegalArgumentException given null
     */
    public static PerformedProcedureStepStatus getMatchForDisplayName(String displayName) throws IllegalArgumentException {
        if (displayName == null) {
            throw new IllegalArgumentException("displayName is required");
        }
        
        if ("in progress".equalsIgnoreCase(displayName)) {
            return IN_PROGRESS;
        } else if ("discontinued".equalsIgnoreCase(displayName)) {
            return DISCONTINUED;
        } else if ("completed".equalsIgnoreCase(displayName)) {
            return COMPLETED;
        } else {
            return null;
        }
    }
}
