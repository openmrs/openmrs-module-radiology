/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openmrs.api.APIException;
import org.openmrs.util.OpenmrsUtil;

/**
 * A parser to parse MRRT report templates and and return an MrrtReportTemplate object.
 */
public class MrrtReportTemplateFileParser {
    
    
    private static final Log log = LogFactory.getLog(MrrtReportTemplateFileParser.class);
    
    private static final String CHARSET = "UTF-8";
    
    private static final String DCTERMS_TITLE = "dcterms.title";
    
    private static final String DCTERMS_DESCRIPTION = "dcterms.description";
    
    private static final String DCTERMS_IDENTIFIER = "dcterms.identifier";
    
    private static final String DCTERMS_TYPE = "dcterms.type";
    
    private static final String DCTERMS_LANGUAGE = "dcterms.language";
    
    private static final String DCTERMS_PUBLISHER = "dcterms.publisher";
    
    private static final String DCTERMS_RIGHTS = "dcterms.rights";
    
    private static final String DCTERMS_LICENSE = "dcterms.license";
    
    private static final String DCTERMS_DATE = "dcterms.date";
    
    private static final String DCTERMS_CREATOR = "dcterms.creator";
    
    private File templateFile;
    
    public MrrtReportTemplateFileParser() {
    }
    
    public MrrtReportTemplateFileParser(File templateFile) {
        if (templateFile == null)
            throw new APIException("File cannot be null: " + templateFile.getName());
        this.templateFile = templateFile;
    }
    
    public MrrtReportTemplateFileParser(InputStream in) {
        FileOutputStream os = null;
        
        try {
            templateFile = File.createTempFile("mrrtTemplateFile", ".html");
            os = new FileOutputStream(templateFile);
            OpenmrsUtil.copyFile(in, os);
        }
        catch (FileNotFoundException e) {
            throw new APIException("File can not be created");
        }
        catch (IOException e) {
            throw new APIException("File cannot be created");
        }
        finally {
            try {
                os.close();
            }
            catch (Exception e) {}
            try {
                in.close();
            }
            catch (Exception e) {}
        }
    }
    
    /**
     * parse mrrt template file and return a template object
     *
     * @return returns MrrtReportTemplate object
     * @throws IOException 
     * 
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplate
     * 
     * @should return an mrrt template object if file is valid
     * 
     * @should throw an mrrt report template exception if file is invalid
     */
    public MrrtReportTemplate parse() throws IOException {
        
        MrrtReportTemplateValidator.validate(templateFile);
        
        Document doc = Jsoup.parse(templateFile, CHARSET);
        MrrtReportTemplate result = new MrrtReportTemplate();
        initializeTemplate(result, doc);
        
        return result;
    }
    
    private static final void initializeTemplate(MrrtReportTemplate template, Document doc) {
        Elements metaTags = doc.getElementsByTag("meta");
        
        template.setPath(doc.baseUri());
        template.setCharset(metaTags.attr("charset"));
        for (Element metaTag : metaTags) {
            String name = metaTag.attr("name");
            String content = metaTag.attr("content");
            
            switch (name) {
                case DCTERMS_TITLE:
                    template.setDcTermsTitle(content);
                    break;
                case DCTERMS_DESCRIPTION:
                    template.setDcTermsDescription(content);
                    break;
                case DCTERMS_IDENTIFIER:
                    template.setDcTermsIdentifier(content);
                    break;
                case DCTERMS_TYPE:
                    template.setDcTermsType(content);
                    break;
                case DCTERMS_LANGUAGE:
                    template.setDcTermsLanguage(content);
                    break;
                case DCTERMS_PUBLISHER:
                    template.setDcTermsPublisher(content);
                    break;
                case DCTERMS_RIGHTS:
                    template.setDcTermsRights(content);
                    break;
                case DCTERMS_LICENSE:
                    template.setDcTermsLicense(content);
                    break;
                case DCTERMS_DATE:
                    template.setDcTermsDate(content);
                    break;
                case DCTERMS_CREATOR:
                    template.setDcTermsCreator(content);
                    break;
                default:
                    log.debug("Unhandled meta tag " + name);
            }
        }
    }
}
