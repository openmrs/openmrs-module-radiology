/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
class DefaultMrrtReportTemplateFileParser implements MrrtReportTemplateFileParser {
    
    
    private static final Log log = LogFactory.getLog(DefaultMrrtReportTemplateFileParser.class);
    
    private MrrtReportTemplateValidator validator = new DefaultMrrtReportTemplateValidator();
    
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
    
    /**
    * Parse {@code MRRT} file and return an {@code MrrtReportTemplate} object
    *
    * @param file file to be parsed
    * @return returns MrrtReportTemplate object
    * @throws IOException if the file could not be read
    * @throws APIException when the file is not of MRRT standards
    * 
    * @see org.openmrs.module.radiology.report.template.MrrtReportTemplate
    */
    @Override
    public MrrtReportTemplate parse(File file) throws IOException {
        validator.validate(file);
        Document doc = Jsoup.parse(file, CHARSET);
        MrrtReportTemplate result = new MrrtReportTemplate();
        initializeTemplate(result, doc);
        
        return result;
    }
    
    /**
    * Parse {@code MRRT} file and return an {@code MrrtReportTemplate} object.
    *
    * @param in input stream of template file
    * @return returns MrrtReportTemplate object
    * @throws IOException the file could not be read
    * 
    * @see org.openmrs.module.radiology.report.template.MrrtReportTemplate
    */
    @Override
    public MrrtReportTemplate parse(String fileName, InputStream in) throws IOException {
        
        final String prefix = fileName.substring(0, fileName.lastIndexOf('.'));
        final String suffix = fileName.substring(fileName.lastIndexOf('.'));
        final File file = File.createTempFile(prefix, suffix);
        final OutputStream os = new FileOutputStream(file);
        OpenmrsUtil.copyFile(in, os);
        
        return parse(file);
    }
    
    private final void initializeTemplate(MrrtReportTemplate template, Document doc) {
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
