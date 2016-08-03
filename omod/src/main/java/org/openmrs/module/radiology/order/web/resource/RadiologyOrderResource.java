/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
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
 * {@link Resource} for {@link RadiologyOrder}, supporting GET operations.
 */
@Resource(name = RestConstants.VERSION_1 + "/radiologyorder", supportedClass = RadiologyOrder.class,
        supportedOpenmrsVersions = { "2.0.*" })
public class RadiologyOrderResource extends DataDelegatingCrudResource<RadiologyOrder> {
    
    
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
            description.addProperty("orderNumber");
            description.addProperty("accessionNumber");
            description.addProperty("patient", Representation.REF);
            description.addProperty("concept", Representation.REF);
            description.addProperty("action");
            description.addProperty("careSetting", Representation.REF);
            description.addProperty("previousOrder", Representation.REF);
            description.addProperty("dateActivated");
            description.addProperty("dateStopped");
            description.addProperty("autoExpireDate");
            description.addProperty("encounter", Representation.REF);
            description.addProperty("orderer", Representation.REF);
            description.addProperty("orderReason", Representation.REF);
            description.addProperty("orderReasonNonCoded");
            description.addProperty("urgency");
            description.addProperty("scheduledDate");
            description.addProperty("instructions");
            description.addProperty("commentToFulfiller");
            description.addProperty("display");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            final DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("orderNumber");
            description.addProperty("accessionNumber");
            description.addProperty("patient", Representation.REF);
            description.addProperty("concept", Representation.REF);
            description.addProperty("action");
            description.addProperty("careSetting", Representation.DEFAULT);
            description.addProperty("previousOrder", Representation.REF);
            description.addProperty("dateActivated");
            description.addProperty("dateStopped");
            description.addProperty("autoExpireDate");
            description.addProperty("encounter", Representation.REF);
            description.addProperty("orderer", Representation.REF);
            description.addProperty("orderReason", Representation.REF);
            description.addProperty("orderReasonNonCoded");
            description.addProperty("urgency");
            description.addProperty("scheduledDate");
            description.addProperty("instructions");
            description.addProperty("commentToFulfiller");
            description.addProperty("display");
            description.addProperty("auditInfo");
            description.addSelfLink();
            return description;
        } else {
            return null;
        }
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
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
     * @should return radiology order given its uuid
     */
    @Override
    public RadiologyOrder getByUniqueId(String uniqueId) {
        
        return Context.getService(RadiologyOrderService.class)
                .getRadiologyOrderByUuid(uniqueId);
    }
    
    /**
     * Get the display string for a {@link RadiologyOrder}.
     * 
     * @param radiologyOrder the radiology order of which the display string shall be returned
     * @return the accession number and the concept name of given radiology order
     * @should return accession number and concept name of given radiology order
     * @should return no concept string if given radiologyOrders concept is null
     */
    @PropertyGetter("display")
    public String getDisplayString(RadiologyOrder radiologyOrder) {
        
        if (radiologyOrder.getConcept() == null) {
            return radiologyOrder.getAccessionNumber() + " - " + "[No Concept]";
        } else {
            return radiologyOrder.getAccessionNumber() + " - " + radiologyOrder.getConcept()
                    .getName()
                    .getName();
        }
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    public RadiologyOrder newDelegate() throws ResourceDoesNotSupportOperationException {
        
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    public RadiologyOrder save(RadiologyOrder delegate) throws ResourceDoesNotSupportOperationException {
        
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
     *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    protected void delete(RadiologyOrder delegate, String reason, RequestContext context)
            throws ResourceDoesNotSupportOperationException {
        
        throw new ResourceDoesNotSupportOperationException();
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
     *      org.openmrs.module.webservices.rest.web.RequestContext)
     * @should throw ResourceDoesNotSupportOperationException
     */
    @Override
    public void purge(RadiologyOrder delegate, RequestContext context) throws ResourceDoesNotSupportOperationException {
        
        throw new ResourceDoesNotSupportOperationException();
    }
}
