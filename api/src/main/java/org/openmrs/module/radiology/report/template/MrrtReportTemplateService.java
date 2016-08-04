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
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.RadiologyPrivileges;

/**
 * Service layer for {@code MrrtReportTemplate}.
 * 
 * @see org.openmrs.module.radiology.report.template.MrrtReportTemplate
 */
public interface MrrtReportTemplateService extends OpenmrsService {
    
    
    /**
    * Saves a new {@code MrrtReportTemplate}.
    *
    * @param template the mrrt report template to be saved
    * @return the saved template
    * @throws IllegalArgumentException if given null
    * @throws APIException if saving an already saved template
    * @should throw illegal argument exception if given null
    * @should save given template
    * @should throw api exception if saving template that already exists
    */
    @Authorized(RadiologyPrivileges.ADD_RADIOLOGY_REPORT_TEMPLATES)
    public MrrtReportTemplate saveMrrtReportTemplate(MrrtReportTemplate template);
    
    /**
     * Import an {@code MrrtReportTemplate} into the system.
     * 
     * @param in the input stream of the mrrt template file
     * @throws IOException if OpenmrsUtil.copyFile throws one
     * @throws APIException if importing an invalid template file
     * @should create mrrt report template in the database with metadata from input stream
     * @should create report template file in report template home directory
     */
    @Authorized(RadiologyPrivileges.ADD_RADIOLOGY_REPORT_TEMPLATES)
    public void importMrrtReportTemplate(InputStream in) throws IOException;
    
    /**
     * Delete an {@code MrrtReportTemplate} from the database.
     *
     * @param template the mrrt report template that is been deleted
     * @throws IllegalArgumentException if given null
     * @should delete report from database
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.DELETE_RADIOLOGY_REPORT_TEMPLATES)
    public void purgeMrrtReportTemplate(MrrtReportTemplate template);
    
    /**
     * Get an {@code MrrtReportTemplate} with a given id.
     * 
     * @param id the mrrt report template id
     * @return the mrrt report template with given id
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
     * @param uuid the UUID of the mrrt report template
     * @return the template mrrt report template object or null if no template UUID found
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
     * @param identifier the dublin core identifier for mrrt report template
     * @return the template mrrt report template object or null if no template found with given identifier
     * @throws IllegalArgumentException if given null
     * @should find object with given identifier
     * @should return null if no object found with given identifier
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORT_TEMPLATES)
    public MrrtReportTemplate getMrrtReportTemplateByIdentifier(String identifier);
    
    /**
     * Get all {@code MrrtReportTemplate's} matching a variety of (nullable) criteria.
     * 
     * @param mrrtReportTemplateSearchCriteria the object containing search parameters
     * @return the mrrt report templates matching the given criteria
     * @throws IllegalArgumentException if given null
     * @should return all mrrt report templates that match given title search query if title is specified
     * @should return an empty list of no match for title was found
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORT_TEMPLATES)
    public List<MrrtReportTemplate>
            getMrrtReportTemplates(MrrtReportTemplateSearchCriteria mrrtReportTemplateSearchCriteria);
    
    /**
     * Get the HTML body content of {@code MrrtReportTemplate's} file.
     * 
     * @param mrrtReportTemplate the mrrt report template for which we want to get its html body content
     * @return the body content of the mrrt report template file
     * @throws IOException if one is thrown while reading the file
     * @should return the body content of the mrrt report template file
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.VIEW_RADIOLOGY_REPORT_TEMPLATES)
    public String getMrrtReportTemplateHtmlBody(MrrtReportTemplate mrrtReportTemplate) throws IOException;
}
