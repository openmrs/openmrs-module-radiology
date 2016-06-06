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

import java.util.List;

/**
 * {@code MrrTReportTemplate} related database methods.
 * 
 * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService
 * @see org.openmrs.module.radiology.report.template.MrrtReportTemplate
 */
interface MrrtReportTemplateDAO {
    
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplate(Integer)
     */
    public MrrtReportTemplate getMrrtReportTemplate(Integer templateId);
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplateByUuid(String)
     */
    public MrrtReportTemplate getMrrtReportTemplateByUuid(String uuid);
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplateByIdentifier(String)
     */
    public MrrtReportTemplate getMrrtReportTemplateByIdentifier(String identifier);
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplateByTitle(String)
     */
    public List<MrrtReportTemplate> getMrrtReportTemplateByTitle(String title);
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
     */
    public MrrtReportTemplate saveMrrtReportTemplate(MrrtReportTemplate template);
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#purgeMrrtReportTemplate(MrrtReportTemplate)
     */
    public void purgeMrrtReportTemplate(MrrtReportTemplate template);
}
