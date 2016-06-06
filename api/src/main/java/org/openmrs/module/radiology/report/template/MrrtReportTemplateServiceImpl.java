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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
class MrrtReportTemplateServiceImpl extends BaseOpenmrsService implements MrrtReportTemplateService {
    
    
    @Autowired
    private MrrtReportTemplateFileParser parser;
    
    @Autowired
    private RadiologyProperties radiologyProperties;
    
    private MrrtReportTemplateDAO mrrtReportTemplateDAO;
    
    public MrrtReportTemplateDAO getMrrtReportTemplateDAO() {
        return mrrtReportTemplateDAO;
    }
    
    public void setMrrtReportTemplateDAO(MrrtReportTemplateDAO mrrtReportTemplateDAO) {
        this.mrrtReportTemplateDAO = mrrtReportTemplateDAO;
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#importMrrtReportTemplate(InputStream)
     */
    @Override
    @Transactional
    public void importMrrtReportTemplate(InputStream in) throws IOException {
        File tmp = File.createTempFile(java.util.UUID.randomUUID()
                .toString(),
            java.util.UUID.randomUUID()
                    .toString());
        OpenmrsUtil.copyFile(in, new FileOutputStream(tmp));
        MrrtReportTemplate template = parser.parse(new FileInputStream(tmp));
        File destinationFile = new File(radiologyProperties.getReportTemplateHome(), java.util.UUID.randomUUID()
                .toString());
        template.setPath(destinationFile.getAbsolutePath());
        saveMrrtReportTemplate(template);
        OpenmrsUtil.copyFile(new FileInputStream(tmp), new FileOutputStream(destinationFile));
        in.close();
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplate(Integer)
     */
    @Override
    public MrrtReportTemplate getMrrtReportTemplate(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        return mrrtReportTemplateDAO.getMrrtReportTemplate(id);
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplateByUuid(String)
     */
    @Override
    public MrrtReportTemplate getMrrtReportTemplateByUuid(String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }
        return mrrtReportTemplateDAO.getMrrtReportTemplateByUuid(uuid);
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplateByIdentifier(String)
     */
    @Override
    public MrrtReportTemplate getMrrtReportTemplateByIdentifier(String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("identifier cannot be null");
        }
        return mrrtReportTemplateDAO.getMrrtReportTemplateByIdentifier(identifier);
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplateByTitle(String)
     */
    @Override
    public List<MrrtReportTemplate> getMrrtReportTemplateByTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("title cannot be null");
        }
        return mrrtReportTemplateDAO.getMrrtReportTemplateByTitle(title);
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
     */
    @Override
    @Transactional
    public MrrtReportTemplate saveMrrtReportTemplate(MrrtReportTemplate template) {
        if (template == null) {
            throw new IllegalArgumentException("template cannot be null");
        }
        MrrtReportTemplate existing = getMrrtReportTemplateByIdentifier(template.getDcTermsIdentifier());
        if (existing != null) {
            throw new APIException("Template already exist in the system.");
        }
        return mrrtReportTemplateDAO.saveMrrtReportTemplate(template);
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#purgeMrrtReportTemplate(MrrtReportTemplate)
     */
    @Override
    @Transactional
    public void purgeMrrtReportTemplate(MrrtReportTemplate template) {
        if (template == null) {
            throw new IllegalArgumentException("template cannot be null");
        }
        mrrtReportTemplateDAO.purgeMrrtReportTemplate(template);
    }
}
