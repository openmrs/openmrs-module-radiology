/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.modality.RadiologyModality;
import org.openmrs.module.radiology.modality.RadiologyModalityService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.RestConstants2_0;

/**
 * {@link Resource} for {@link RadiologyModality}.
 */
@Resource(name = RestConstants.VERSION_1 + "/radiologymodality", supportedClass = RadiologyModality.class,
        supportedOpenmrsVersions = { "2.0.*" }, order = 1)
public class RadiologyModalityResource extends MetadataDelegatingCrudResource<RadiologyModality> {
    
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
     * @should return supported resource version
     */
    @Override
    public String getResourceVersion() {
        
        return RestConstants2_0.RESOURCE_VERSION;
    }
    
    /**
    * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
     * @should return default representation given instance of defaultrepresentation
     * @should return full representation given instance of fullrepresentation
     * @should return null for representation other then default or full
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addProperty("aeTitle");
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("retired");
            description.addSelfLink();
            if (rep instanceof DefaultRepresentation) {
                description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            } else {
                description.addProperty("auditInfo");
            }
            return description;
        }
        return null;
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        
        DelegatingResourceDescription description = super.getCreatableProperties();
        description.addRequiredProperty("aeTitle");
        return description;
    }
    
    /**
     * Get the display string for a {@link RadiologyModality}.
     *
     * @param delegate the radiology modality of which the display string shall be returned
     * @return the ae title of given radiology order
     * @should return ae title of given radiology modality
     */
    @PropertyGetter("display")
    public String getDisplayString(RadiologyModality delegate) {
        return delegate.getAeTitle();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(String)
     * @should return radiology modality given its uuid
     */
    @Override
    public RadiologyModality getByUniqueId(String uniqueId) {
        return Context.getService(RadiologyModalityService.class)
                .getRadiologyModalityByUuid(uniqueId);
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
     */
    @Override
    public RadiologyModality newDelegate() {
        return new RadiologyModality();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#save(Object)
     * @should save given radiology modality
     */
    @Override
    public RadiologyModality save(RadiologyModality delegate) {
        return Context.getService(RadiologyModalityService.class)
                .saveRadiologyModality(delegate);
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(Object, String, RequestContext)
     * @should retire given radiology modality
     */
    @Override
    public void delete(RadiologyModality delegate, String reason, RequestContext context) throws ResponseException {
        Context.getService(RadiologyModalityService.class)
                .retireRadiologyModality(delegate, reason);
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(Object, RequestContext)
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    public void purge(RadiologyModality delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(RequestContext)
     * @should return radiology modalities including retired ones if include all is true
     * @should return radiology modalities excluding retired ones if include all is false
     */
    @Override
    protected NeedsPaging<RadiologyModality> doGetAll(RequestContext context) {
        return new NeedsPaging<>(Context.getService(RadiologyModalityService.class)
                .getRadiologyModalities(context.getIncludeAll()), context);
    }
}
