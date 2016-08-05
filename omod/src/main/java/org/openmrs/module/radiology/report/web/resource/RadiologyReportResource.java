/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportService;
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
 * {@link Resource} for {@link RadiologyReport}, supporting GET operations.
 */
@Resource(name = RestConstants.VERSION_1 + "/radiologyreport", supportedClass = RadiologyReport.class,
        supportedOpenmrsVersions = { "2.0.*" })
public class RadiologyReportResource extends DataDelegatingCrudResource<RadiologyReport> {
    
    
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
            addDefaultProperties(description);
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            final DelegatingResourceDescription description = new DelegatingResourceDescription();
            addDefaultProperties(description);
            description.addProperty("auditInfo");
            description.addSelfLink();
            return description;
        } else {
            return null;
        }
    }
    
    private void addDefaultProperties(DelegatingResourceDescription description) {
        
        description.addProperty("uuid");
        description.addProperty("radiologyOrder", Representation.REF);
        description.addProperty("date");
        description.addProperty("principalResultsInterpreter", Representation.REF);
        description.addProperty("status");
        description.addProperty("body");
        description.addProperty("display");
        description.addProperty("voided");
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
     * @should return supported resource version
     */
    @Override
    public String getResourceVersion() {
        
        return RestConstants2_0.RESOURCE_VERSION;
    }
    
    /**
     * Display string for {@link RadiologyReport}
     * 
     * @param radiologyReport RadiologyReport of which display string shall be returned
     * @return order number and report status string of given radiologyReport
     * @should return order number and report status string of given radiologyReport
     */
    @PropertyGetter("display")
    public String getDisplayString(RadiologyReport radiologyReport) {
        
        return radiologyReport.getRadiologyOrder()
                .getOrderNumber() + ", "
                + radiologyReport.getStatus()
                        .toString();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
     * @should return radiology report given its uuid
     */
    @Override
    public RadiologyReport getByUniqueId(String uniqueId) {
        
        return Context.getService(RadiologyReportService.class)
                .getRadiologyReportByUuid(uniqueId);
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    public RadiologyReport newDelegate() throws ResourceDoesNotSupportOperationException {
        
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    public RadiologyReport save(RadiologyReport delegate) throws ResourceDoesNotSupportOperationException {
        
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
     *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    protected void delete(RadiologyReport delegate, String reason, RequestContext context)
            throws ResourceDoesNotSupportOperationException {
        
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
     *      org.openmrs.module.webservices.rest.web.RequestContext)
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    public void purge(RadiologyReport delegate, RequestContext context) throws ResourceDoesNotSupportOperationException {
        
        throw new ResourceDoesNotSupportOperationException();
    }
}
