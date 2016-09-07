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

import java.util.Set;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.ConceptReferenceTerm;

/**
 * IHE Management of Radiology Report Templates (MRRT)
 */
public class MrrtReportTemplate extends BaseOpenmrsData {
    
    
    private static final long serialVersionUID = 4135353950631883493L;
    
    private Integer templateId;
    
    private String charset;
    
    private String path;
    
    private String dcTermsTitle;
    
    private String dcTermsDescription;
    
    private String dcTermsIdentifier;
    
    private String dcTermsType;
    
    private String dcTermsLanguage;
    
    private String dcTermsPublisher;
    
    private String dcTermsRights;
    
    private String dcTermsLicense;
    
    private String dcTermsDate;
    
    private String dcTermsCreator;
    
    private Set<ConceptReferenceTerm> terms;
    
    @Override
    public Integer getId() {
        return this.templateId;
    }
    
    @Override
    public void setId(Integer id) {
        this.templateId = id;
    }
    
    public Integer getTemplateId() {
        return getId();
    }
    
    public void setTemplateId(Integer templateId) {
        setId(templateId);
    }
    
    public String getCharset() {
        return charset;
    }
    
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getDcTermsTitle() {
        return dcTermsTitle;
    }
    
    public void setDcTermsTitle(String dcTermsTitle) {
        this.dcTermsTitle = dcTermsTitle;
    }
    
    public String getDcTermsDescription() {
        return dcTermsDescription;
    }
    
    public void setDcTermsDescription(String dcTermsDescription) {
        this.dcTermsDescription = dcTermsDescription;
    }
    
    public String getDcTermsIdentifier() {
        return dcTermsIdentifier;
    }
    
    public void setDcTermsIdentifier(String dcTermsIdentifier) {
        this.dcTermsIdentifier = dcTermsIdentifier;
    }
    
    public String getDcTermsType() {
        return dcTermsType;
    }
    
    public void setDcTermsType(String dcTermsType) {
        this.dcTermsType = dcTermsType;
    }
    
    public String getDcTermsLanguage() {
        return dcTermsLanguage;
    }
    
    public void setDcTermsLanguage(String dcTermsLanguage) {
        this.dcTermsLanguage = dcTermsLanguage;
    }
    
    public String getDcTermsPublisher() {
        return dcTermsPublisher;
    }
    
    public void setDcTermsPublisher(String dcTermsPublisher) {
        this.dcTermsPublisher = dcTermsPublisher;
    }
    
    public String getDcTermsRights() {
        return dcTermsRights;
    }
    
    public void setDcTermsRights(String dcTermsRights) {
        this.dcTermsRights = dcTermsRights;
    }
    
    public String getDcTermsLicense() {
        return dcTermsLicense;
    }
    
    public void setDcTermsLicense(String dcTermsLicense) {
        this.dcTermsLicense = dcTermsLicense;
    }
    
    public String getDcTermsDate() {
        return dcTermsDate;
    }
    
    public void setDcTermsDate(String dcTermsDate) {
        this.dcTermsDate = dcTermsDate;
    }
    
    public String getDcTermsCreator() {
        return dcTermsCreator;
    }
    
    public void setDcTermsCreator(String dcTermsCreator) {
        this.dcTermsCreator = dcTermsCreator;
    }
    
    public Set<ConceptReferenceTerm> getTerms() {
        return terms;
    }
    
    public void setTerms(Set<ConceptReferenceTerm> terms) {
        this.terms = terms;
    }
}
