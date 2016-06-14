/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.RadiologyPrivileges;
import org.springframework.transaction.annotation.Transactional;

/**
 * The service for managing MrrtReportTemplates 
 */
@Transactional
public interface MrrtReportTemplateService extends OpenmrsService {
    
    
    /**
     * get a template with a given id 
     * 
     * @param id the template id
     * @return template with given id
     * 
     * @should get template with given id
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORT_TEMPLATES)
    public MrrtReportTemplate getMrrtReportTemplate(Integer id);
    
    /**
     *  get template by its UUID
     *
     *  @param uuid
     *  @return mrrt template object or null
     *  @should find object given valid uuid
     *  @should should return null of no object found with given uuid
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORT_TEMPLATES)
    public MrrtReportTemplate getMrrtReportTemplateByUuid(String uuid);
    
    /**
     *  saves a new or existing template
     *
     *  @param template the template to save
     *  @return the saved template
     *  @should save report
     */
    @Authorized(RadiologyPrivileges.ADD_RADIOLOGY_REPORT_TEMPLATES)
    public MrrtReportTemplate saveMrrtReportTemplate(MrrtReportTemplate template);
    
    /**
     * delete a template from the database
     *
     * @param template the template that is been deleted
     * @should delete report from database
     */
    @Authorized(RadiologyPrivileges.DELETE_RADIOLOGY_REPORT_TEMPLATES)
    public void purgeMrrtReportTemplate(MrrtReportTemplate template);
}
