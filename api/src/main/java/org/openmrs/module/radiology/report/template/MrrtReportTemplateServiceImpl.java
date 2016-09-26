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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
class MrrtReportTemplateServiceImpl extends BaseOpenmrsService implements MrrtReportTemplateService {
    
    
    private MrrtReportTemplateFileParser parser;
    
    private RadiologyProperties radiologyProperties;
    
    private MrrtReportTemplateDAO mrrtReportTemplateDAO;
    
    public void setMrrtReportTemplateDAO(MrrtReportTemplateDAO mrrtReportTemplateDAO) {
        this.mrrtReportTemplateDAO = mrrtReportTemplateDAO;
    }
    
    public void setParser(MrrtReportTemplateFileParser parser) {
        this.parser = parser;
    }
    
    public void setRadiologyProperties(RadiologyProperties radiologyProperties) {
        this.radiologyProperties = radiologyProperties;
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#importMrrtReportTemplate(InputStream)
     */
    @Override
    @Transactional
    public void importMrrtReportTemplate(InputStream in) throws IOException {
        final File tmp = File.createTempFile(java.util.UUID.randomUUID()
                .toString(),
            java.util.UUID.randomUUID()
                    .toString());
        OpenmrsUtil.copyFile(in, new FileOutputStream(tmp));
        final MrrtReportTemplate template = parser.parse(new FileInputStream(tmp));
        final File destinationFile = new File(radiologyProperties.getReportTemplateHome(), java.util.UUID.randomUUID()
                .toString());
        template.setPath(destinationFile.getAbsolutePath());
        saveMrrtReportTemplate(template);
        OpenmrsUtil.copyFile(new FileInputStream(tmp), new FileOutputStream(destinationFile));
        in.close();
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
        final MrrtReportTemplate existing = getMrrtReportTemplateByIdentifier(template.getDcTermsIdentifier());
        if (existing != null) {
            throw new APIException(
                    "Template with identifier '" + existing.getDcTermsIdentifier() + "' already exist in the system.");
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
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
     */
    @Override
    public List<MrrtReportTemplate>
            getMrrtReportTemplates(MrrtReportTemplateSearchCriteria mrrtReportTemplateSearchCriteria) {
        if (mrrtReportTemplateSearchCriteria == null) {
            throw new IllegalArgumentException("mrrtReportTemplateSearchCriteria cannot be null");
        }
        return mrrtReportTemplateDAO.getMrrtReportTemplates(mrrtReportTemplateSearchCriteria);
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplateHtmlBody(MrrtReportTemplate)
     */
    @Override
    public String getMrrtReportTemplateHtmlBody(MrrtReportTemplate mrrtReportTemplate) throws IOException {
        if (mrrtReportTemplate == null) {
            throw new IllegalArgumentException("mrrtReportTemplate cannot be null");
        }
        final File templateFile = new File(mrrtReportTemplate.getPath());
        final Document doc = Jsoup.parse(templateFile, null);
        
        return doc.select("body")
                .html();
    }
}
