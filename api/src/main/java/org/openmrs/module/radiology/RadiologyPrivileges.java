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
 * <p>
 * All Privileges used within the radiologymodule are defined as constants here. They are used for the @authorized
 * annotation.
 * </p>
 */
public class RadiologyPrivileges {
    
    
    public static final String ADD_RADIOLOGY_ORDERS = "Add Radiology Orders";
    
    public static final String ADD_RADIOLOGY_REPORTS = "Add Radiology Reports";
    
    public static final String ADD_RADIOLOGY_REPORT_TEMPLATES = "Add Radiology Report Templates";
    
    public static final String DELETE_RADIOLOGY_ORDERS = "Delete Radiology Orders";
    
    public static final String DELETE_RADIOLOGY_REPORTS = "Delete Radiology Reports";
    
    public static final String DELETE_RADIOLOGY_REPORT_TEMPLATES = "Delete Radiology Report Templates";
    
    public static final String EDIT_RADIOLOGY_REPORTS = "Edit Radiology Reports";
    
    public static final String EDIT_RADIOLOGY_REPORT_TEMPLATES = "Edit Radiology Report Templates";
    
    public static final String GET_RADIOLOGY_ORDERS = "Get Radiology Orders";
    
    public static final String GET_RADIOLOGY_REPORTS = "Get Radiology Reports";
    
    public static final String GET_RADIOLOGY_REPORT_TEMPLATES = "Get Radiology Report Templates";
    
    public static final String VIEW_RADIOLOGY_SECTION = "Patient Dashboard - View Radiology Section";
    
    public static final String VIEW_RADIOLOGY_REPORT_TEMPLATES = "View Radiology Report Templates";
    
    private RadiologyPrivileges() {
        // Utility class not meant to be instantiated.
    }
}
