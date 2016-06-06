/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.springframework.transaction.annotation.Transactional;

class MrrtReportTemplateServiceImpl extends BaseOpenmrsService implements MrrtReportTemplateService {
    
    
    private MrrtReportTemplateDAO mrrtReportTemplateDAO;
    
    public MrrtReportTemplateDAO getMrrtReportTemplateDAO() {
        return mrrtReportTemplateDAO;
    }
    
    public void setMrrtReportTemplateDAO(MrrtReportTemplateDAO mrrtReportTemplateDAO) {
        this.mrrtReportTemplateDAO = mrrtReportTemplateDAO;
    }
    
    /**
     * @see MrrtReportTemplateService#getMrrtReportTemplate(Integer) 
     */
    @Transactional(readOnly = true)
    @Override
    public MrrtReportTemplate getMrrtReportTemplate(Integer id) {
        return mrrtReportTemplateDAO.getMrrtReportTemplate(id);
    }
    
    /**
     * @see MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
     */
    @Transactional
    @Override
    public MrrtReportTemplate saveMrrtReportTemplate(MrrtReportTemplate template) {
        return mrrtReportTemplateDAO.saveMrrtReportTemplate(template);
    }
    
    /**
     * @see MrrtReportTemplateService#purgeMrrtReportTemplate(MrrtReportTemplate)
     */
    @Transactional
    @Override
    public void purgeMrrtReportTemplate(MrrtReportTemplate template) {
        mrrtReportTemplateDAO.purgeMrrtReportTemplate(template);
    }
    
}
