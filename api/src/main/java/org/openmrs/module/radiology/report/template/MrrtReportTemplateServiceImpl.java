/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

class MrrtReportTemplateServiceImpl extends BaseOpenmrsService implements MrrtReportTemplateService {
    
    
    private static final Log log = LogFactory.getLog(MrrtReportTemplateServiceImpl.class);

    private MrrtReportTemplateDAO mrrtReportTemplateDAO;
    
    public MrrtReportTemplateDAO getMrrtReportTemplateDAO() {
        return mrrtReportTemplateDAO;
    }
    
    public void setMrrtReportTemplateDAO(MrrtReportTemplateDAO mrrtReportTemplateDAO) {
        this.mrrtReportTemplateDAO = mrrtReportTemplateDAO;
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplate(Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public MrrtReportTemplate getMrrtReportTemplate(Integer id) {
        return mrrtReportTemplateDAO.getMrrtReportTemplate(id);
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplateByUuid(String)
     */
    @Transactional(readOnly = true)
    @Override
    public MrrtReportTemplate getMrrtReportTemplateByUuid(String uuid) {
        return mrrtReportTemplateDAO.getMrrtReportTemplateByUuid(uuid);
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
     */
    @Transactional
    @Override
    public MrrtReportTemplate saveMrrtReportTemplate(MrrtReportTemplate template) {
        return mrrtReportTemplateDAO.saveMrrtReportTemplate(template);
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#purgeMrrtReportTemplate(MrrtReportTemplate)
     */
    @Transactional
    @Override
    public void purgeMrrtReportTemplate(MrrtReportTemplate template) {
        mrrtReportTemplateDAO.purgeMrrtReportTemplate(template);
    }
}
