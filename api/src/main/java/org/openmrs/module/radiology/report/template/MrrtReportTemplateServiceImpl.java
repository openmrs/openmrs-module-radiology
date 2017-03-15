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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.RadiologyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
class MrrtReportTemplateServiceImpl extends BaseOpenmrsService implements MrrtReportTemplateService {
    
    
    private static final Logger log = LoggerFactory.getLogger(MrrtReportTemplateServiceImpl.class);
    
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
     * @see MrrtReportTemplateService#importMrrtReportTemplate(String)
     */
    @Override
    @Transactional
    public MrrtReportTemplate importMrrtReportTemplate(String mrrtTemplate) throws IOException {
        
        final MrrtReportTemplate template = parser.parse(mrrtTemplate);
        
        final File destination = new File(radiologyProperties.getReportTemplateHome(), java.util.UUID.randomUUID()
                .toString());
        FileUtils.writeStringToFile(destination, mrrtTemplate);
        
        template.setPath(destination.getAbsolutePath());
        return saveMrrtReportTemplate(template);
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
        Path templatePath = Paths.get(template.getPath());
        try {
            Files.delete(templatePath);
        }
        catch (NoSuchFileException noSuchFileException) {
            log.debug("Tried to delete " + template.getPath() + " , but wasnt found.");
        }
        catch (IOException ioException) {
            throw new APIException("radiology.MrrtReportTemplate.delete.error.fs", null, ioException);
        }
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
