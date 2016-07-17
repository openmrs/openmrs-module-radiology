/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.RadiologyPrivileges;

/**
 * Service layer for {@code MrrtReportTemplate}.
 * 
 * @see org.openmrs.module.radiology.report.template.MrrtReportTemplate
 */
public interface MrrtReportTemplateService extends OpenmrsService {
    
    
    /**
     * Import an {@code MrrtReportTemplate} into the system.
     * 
     * @param in the input stream of the mrrt template file
     * @throws IOException if OpenmrsUtil.copyFile throws one
     * TODO improve javadoc for exceptions and add tests
     * @should create mrrt report template in the database with metadata from input stream
     * @should create report template file in report template home directory
     * @should close input stream after report template file has been created
     */
    @Authorized(RadiologyPrivileges.ADD_RADIOLOGY_REPORT_TEMPLATES)
    public void importMrrtReportTemplate(InputStream in) throws IOException;
    
    /**
     * Get an {@code MrrtReportTemplate} with a given id.
     * 
     * @param id the {MrrtReportTemplate} id
     * @return {@code MrrtReportTemplate} with given id
     * @throws IllegalArgumentException if given null
     * @should get template with given id
     * @should return null if no match was found
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORT_TEMPLATES)
    public MrrtReportTemplate getMrrtReportTemplate(Integer id);
    
    /**
     * Get {@code MrrtReportTemplate} by its UUID.
     *
     * @param uuid UUID of {@code MrrtReportTemplate}
     * @return mrrt {@code MrrtReportTemplate} object or null
     * @throws IllegalArgumentException if given null
     * @should find object given existing uuid
     * @should return null if no object found with given uuid
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORT_TEMPLATES)
    public MrrtReportTemplate getMrrtReportTemplateByUuid(String uuid);
    
    /**
     * Get {@code MrrtReportTemplate} by its identifier.
     * 
     * @param identifier dublin identifier for {@code MrrtReportTemplate}
     * @return {@code MrrtReportTemplate} object or null
     * @throws IllegalArgumentException if given null
     * @should find object given valid identifier
     * @should return null if no object found with give identifier
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORT_TEMPLATES)
    public MrrtReportTemplate getMrrtReportTemplateByIdentifier(String identifier);
    
    /**
     * Get list of {@code MrrtReportTemplate} objects matching a particular title.
     * 
     * @param title title of {@code MrrtReportTemplate}
     * @return list of {@code MrrtReportTemplate} objects matching title
     * @throws IllegalArgumentException if given null
     * @should get list of templates that match given title
     * @should return empty list of no match is found
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORT_TEMPLATES)
    public List<MrrtReportTemplate> getMrrtReportTemplateByTitle(String title);
    
    /**
     * Saves a new or existing {@code MrrtReportTemplate}.
     *
     * @param template the {@code MrrtReportTemplate} to save
     * @return the saved template
     * @throws IllegalArgumentException if given null
     * @should throw illegal argument exception if given null
     * @should save given template
     * @should prevent template editing
     */
    @Authorized(RadiologyPrivileges.ADD_RADIOLOGY_REPORT_TEMPLATES)
    public MrrtReportTemplate saveMrrtReportTemplate(MrrtReportTemplate template);
    
    /**
     * Delete an {@code MrrtReportTemplate} from the database.
     *
     * @param template the {@code MrrtReportTemplate} that is been deleted
     * @throws IllegalArgumentException if given null
     * @should delete report from database
     * @should throw illigal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.DELETE_RADIOLOGY_REPORT_TEMPLATES)
    public void purgeMrrtReportTemplate(MrrtReportTemplate template);
}
