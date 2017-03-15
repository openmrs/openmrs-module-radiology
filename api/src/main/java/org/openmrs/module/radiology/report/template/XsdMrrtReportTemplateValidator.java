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
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openmrs.api.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Uses xsd with schema to validate {@code MrrtReportTemplate} files.
 */
public class XsdMrrtReportTemplateValidator implements MrrtReportTemplateValidator {
    
    
    private static final Logger log = LoggerFactory.getLogger(XsdMrrtReportTemplateValidator.class);
    
    private static final String MRRT_REPORT_TEMPLATE_SCHEMA_FILE = "MrrtReportTemplateSchema.xsd";
    
    MetaTagsValidationEngine metaTagsValidationEngine;
    
    public MetaTagsValidationEngine getMetaTagsValidationEngine() {
        return metaTagsValidationEngine;
    }
    
    public void setMetaTagsValidationEngine(MetaTagsValidationEngine metaTagsValidationEngine) {
        this.metaTagsValidationEngine = metaTagsValidationEngine;
    }
    
    /**
     * @see MrrtReportTemplateValidator#validate(String)
     */
    @Override
    public void validate(String mrrtTemplate) throws IOException {
        
        final Document document = Jsoup.parse(mrrtTemplate, "");
        final Elements metatags = document.getElementsByTag("meta");
        ValidationResult validationResult = metaTagsValidationEngine.run(metatags);
        
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema;
        final Validator validator;
        try (InputStream in = IOUtils.toInputStream(mrrtTemplate)) {
            schema = factory.newSchema(getSchemaFile());
            validator = schema.newValidator();
            validator.setErrorHandler(new ErrorHandler() {
                
                
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    log.debug(exception.getMessage(), exception);
                    validationResult.addError(exception.getMessage(), "");
                }
                
                @Override
                public void error(SAXParseException exception) throws SAXException {
                    log.debug(exception.getMessage(), exception);
                    validationResult.addError(exception.getMessage(), "");
                }
                
                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    log.debug(exception.getMessage(), exception);
                    validationResult.addError(exception.getMessage(), "");
                }
            });
            validator.validate(new StreamSource(in));
            validationResult.assertOk();
        }
        catch (SAXException e) {
            log.error(e.getMessage(), e);
            throw new APIException("radiology.report.template.validation.error", null, e);
        }
    }
    
    private File getSchemaFile() {
        return new File(getClass().getClassLoader()
                .getResource(MRRT_REPORT_TEMPLATE_SCHEMA_FILE)
                .getFile());
    }
}
