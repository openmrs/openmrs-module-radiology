/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template.web.resource;

import java.util.Arrays;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.report.template.MrrtReportTemplate;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.RestConstants2_0;

/**
 *
 */
@Resource(name = RestConstants.VERSION_1 + "/mrrtreporttemplate", supportedClass = MrrtReportTemplate.class,
        supportedOpenmrsVersions = { "2.0.*" })
public class MrrtReportTemplateResource extends DataDelegatingCrudResource<MrrtReportTemplate> {
    
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
     * @should return default representation given instance of defaultrepresentation
     * @should return full representation given instance of fullrepresentation
     * @should return null for representation other then default or full
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            final DelegatingResourceDescription description = new DelegatingResourceDescription();
            
            description.addProperty("uuid");
            description.addProperty("templateId");
            description.addProperty("dcTermsIdentifier");
            description.addProperty("dcTermsTitle");
            description.addProperty("dcTermsType");
            description.addProperty("dcTermsPublisher");
            description.addProperty("dcTermsCreator");
            description.addProperty("dcTermsRights");
            description.addProperty("terms", Representation.REF);
            description.addProperty("display");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            final DelegatingResourceDescription description = new DelegatingResourceDescription();
            
            description.addProperty("uuid");
            description.addProperty("charset");
            description.addProperty("templateId");
            description.addProperty("dcTermsIdentifier");
            description.addProperty("dcTermsTitle");
            description.addProperty("dcTermsDescription");
            description.addProperty("dcTermsType");
            description.addProperty("dcTermsLanguage");
            description.addProperty("dcTermsPublisher");
            description.addProperty("dcTermsCreator");
            description.addProperty("dcTermsRights");
            description.addProperty("dcTermsLicense");
            description.addProperty("dcTermsDate");
            description.addProperty("terms", Representation.REF);
            description.addProperty("display");
            description.addSelfLink();
            return description;
        } else {
            return null;
        }
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getPropertiesToExposeAsSubResources()
     */
    @Override
    public List<String> getPropertiesToExposeAsSubResources() {
        return Arrays.asList("terms");
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
     * @should return radiology order given its uuid
     */
    @Override
    public MrrtReportTemplate getByUniqueId(String uuid) {
        return Context.getService(MrrtReportTemplateService.class)
                .getMrrtReportTemplateByUuid(uuid);
    }
    
    /**
     * Display string for {@link MrrtReportTemplate}
     *
     * @param mrrtReportTemplate MrrtReportTemplate of which display string shall be returned
     * @return templateIdentifier/title of given mrrtReportTemplate
     * @should return templateIdentifier
     */
    @PropertyGetter("display")
    public String getDisplayString(MrrtReportTemplate mrrtReportTemplate) {
        return mrrtReportTemplate.getDcTermsIdentifier();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
     *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    protected void delete(MrrtReportTemplate mrrtReportTemplate, String s, RequestContext requestContext)
            throws ResourceDoesNotSupportOperationException {
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
     *      org.openmrs.module.webservices.rest.web.RequestContext)
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    public void purge(MrrtReportTemplate mrrtReportTemplate, RequestContext requestContext)
            throws ResourceDoesNotSupportOperationException {
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    public MrrtReportTemplate newDelegate() throws ResourceDoesNotSupportOperationException {
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    public MrrtReportTemplate save(MrrtReportTemplate mrrtReportTemplate) {
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
     * @should return supported resource version
     */
    @Override
    public String getResourceVersion() {
        return RestConstants2_0.RESOURCE_VERSION;
    }
}
