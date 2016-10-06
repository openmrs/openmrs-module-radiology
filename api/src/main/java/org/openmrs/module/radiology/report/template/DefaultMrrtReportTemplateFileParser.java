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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A parser to parse MRRT report templates and and return an MrrtReportTemplate object.
 */
class DefaultMrrtReportTemplateFileParser implements MrrtReportTemplateFileParser {
    
    
    private static final Log log = LogFactory.getLog(DefaultMrrtReportTemplateFileParser.class);
    
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
    
    private MrrtReportTemplateValidator validator;
    
    public void setValidator(MrrtReportTemplateValidator validator) {
        this.validator = validator;
    }
    
    /**
     * @see MrrtReportTemplateFileParser#parse(String)
     */
    @Override
    public MrrtReportTemplate parse(String mrrtTemplate) throws IOException {
        
        validator.validate(mrrtTemplate);
        
        final Document doc = Jsoup.parse(mrrtTemplate, "");
        final MrrtReportTemplate result = new MrrtReportTemplate();
        initializeTemplate(result, doc);
        try {
            addTermsToTemplate(result, doc.getElementsByTag("script")
                    .get(0)
                    .toString());
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new APIException("radiology.report.template.parser.error", null, e);
        }
        return result;
    }
    
    private final void initializeTemplate(MrrtReportTemplate template, Document doc) {
        final Elements metaTags = doc.getElementsByTag("meta");
        
        template.setPath(doc.baseUri());
        template.setCharset(metaTags.attr("charset"));
        for (Element metaTag : metaTags) {
            final String name = metaTag.attr("name");
            final String content = metaTag.attr("content");
            
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
    
    private final void addTermsToTemplate(MrrtReportTemplate template, String script)
            throws ParserConfigurationException, SAXException, IOException {
        
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream in = new ByteArrayInputStream(script.getBytes())) {
            final org.w3c.dom.Document scriptPartAsDocument = builder.parse(in);
            scriptPartAsDocument.getDocumentElement()
                    .normalize();
            final NodeList terms = scriptPartAsDocument.getElementsByTagName("term");
            final ConceptService conceptService = Context.getService(ConceptService.class);
            final Set<ConceptReferenceTerm> referenceTerms = new HashSet<>();
            
            for (int i = 0; i < terms.getLength(); i++) {
                final org.w3c.dom.Element termElement = (org.w3c.dom.Element) terms.item(i);
                final org.w3c.dom.Element codeElement = (org.w3c.dom.Element) termElement.getElementsByTagName("code")
                        .item(0);
                final ConceptSource conceptSource =
                        getConceptSourceByName(codeElement.getAttribute("scheme"), conceptService);
                if (conceptSource != null) {
                    final ConceptReferenceTerm referenceTerm =
                            conceptService.getConceptReferenceTermByCode(codeElement.getAttribute("value"), conceptSource);
                    if (referenceTerm != null) {
                        referenceTerms.add(referenceTerm);
                    }
                }
            }
            if (!referenceTerms.isEmpty()) {
                template.setTerms(referenceTerms);
            }
        }
    }
    
    private final ConceptSource getConceptSourceByName(String name, ConceptService conceptService) {
        
        final List<ConceptSource> conceptSources = conceptService.getAllConceptSources(false);
        for (ConceptSource conceptSource : conceptSources) {
            if (conceptSource.getName()
                    .equalsIgnoreCase(name)) {
                return conceptSource;
            }
        }
        return null;
    }
}
