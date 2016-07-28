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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.RestConstants2_0;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests {@link RadiologyOrderResource}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, RestUtil.class, LocaleUtility.class })
public class RadiologyOrderResourceTest {
    
    
    private static final String RADIOLOGY_ORDER_UUID = "1bae735a-fca0-11e5-9e59-08002719a237";
    
    @Mock
    RadiologyOrderService radiologyOrderService;
    
    RadiologyOrderResource radiologyOrderResource = new RadiologyOrderResource();
    
    RadiologyOrder radiologyOrder = new RadiologyOrder();
    
    Locale localeEn = new Locale("en");
    
    @Before
    public void setUp() throws Exception {
        
        radiologyOrder.setUuid(RADIOLOGY_ORDER_UUID);
        radiologyOrder.setAccessionNumber("1");
        
        PowerMockito.mockStatic(RestUtil.class);
        
        PowerMockito.mockStatic(LocaleUtility.class);
        Set<Locale> locales = new HashSet<Locale>();
        locales.add(localeEn);
        when(LocaleUtility.getLocalesInOrder()).thenReturn(locales);
        
        PowerMockito.mockStatic(Context.class);
        when(Context.getService(RadiologyOrderService.class)).thenReturn(radiologyOrderService);
        when(radiologyOrderService.getRadiologyOrderByUuid(RADIOLOGY_ORDER_UUID)).thenReturn(radiologyOrder);
    }
    
    /**
     * @see RadiologyOrderResource#getRepresentationDescription(Representation)
     * @verifies return default representation given instance of defaultrepresentation
     */
    @Test
    public void getRepresentationDescription_shouldReturnDefaultRepresentationGivenInstanceOfDefaultrepresentation()
            throws Exception {
        
        DefaultRepresentation defaultRepresentation = new DefaultRepresentation();
        
        DelegatingResourceDescription resourceDescription =
                radiologyOrderResource.getRepresentationDescription(defaultRepresentation);
        assertThat(resourceDescription.getProperties()
                .keySet(),
            contains("uuid", "orderNumber", "accessionNumber", "patient", "concept", "action", "careSetting",
                "previousOrder", "dateActivated", "dateStopped", "autoExpireDate", "encounter", "orderer", "orderReason",
                "orderReasonNonCoded", "urgency", "scheduledDate", "instructions", "commentToFulfiller", "display"));
        assertThat(resourceDescription.getProperties()
                .get("patient")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("concept")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("careSetting")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("previousOrder")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("encounter")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("orderer")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("orderReason")
                .getRep(),
            is(Representation.REF));
    }
    
    /**
     * @see RadiologyOrderResource#getRepresentationDescription(Representation)
     * @verifies return full representation given instance of fullrepresentation
     */
    @Test
    public void getRepresentationDescription_shouldReturnFullRepresentationGivenInstanceOfFullrepresentation()
            throws Exception {
        
        FullRepresentation fullRepresentation = new FullRepresentation();
        
        DelegatingResourceDescription resourceDescription =
                radiologyOrderResource.getRepresentationDescription(fullRepresentation);
        assertThat(resourceDescription.getProperties()
                .keySet(),
            contains("uuid", "orderNumber", "accessionNumber", "patient", "concept", "action", "careSetting",
                "previousOrder", "dateActivated", "dateStopped", "autoExpireDate", "encounter", "orderer", "orderReason",
                "orderReasonNonCoded", "urgency", "scheduledDate", "instructions", "commentToFulfiller", "display",
                "auditInfo"));
        assertThat(resourceDescription.getProperties()
                .get("patient")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("concept")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("careSetting")
                .getRep(),
            is(Representation.DEFAULT));
        assertThat(resourceDescription.getProperties()
                .get("previousOrder")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("encounter")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("orderer")
                .getRep(),
            is(Representation.REF));
        assertThat(resourceDescription.getProperties()
                .get("orderReason")
                .getRep(),
            is(Representation.REF));
    }
    
    /**
     * @see RadiologyOrderResource#getRepresentationDescription(Representation)
     * @verifies return null for representation other then default or full
     */
    @Test
    public void getRepresentationDescription_shouldReturnNullForRepresentationOtherThenDefaultOrFull() throws Exception {
        
        CustomRepresentation customRepresentation = new CustomRepresentation("some");
        
        assertThat(radiologyOrderResource.getRepresentationDescription(customRepresentation), is(nullValue()));
        
        NamedRepresentation namedRepresentation = new NamedRepresentation("some");
        radiologyOrderResource = new RadiologyOrderResource();
        
        assertThat(radiologyOrderResource.getRepresentationDescription(namedRepresentation), is(nullValue()));
        
        RefRepresentation refRepresentation = new RefRepresentation();
        radiologyOrderResource = new RadiologyOrderResource();
        
        assertThat(radiologyOrderResource.getRepresentationDescription(refRepresentation), is(nullValue()));
    }
    
    /**
     * @see RadiologyOrderResource#getResourceVersion()
     * @verifies return supported resource version
     */
    @Test
    public void getResourceVersion_shouldReturnSupportedResourceVersion() throws Exception {
        
        assertThat(radiologyOrderResource.getResourceVersion(), is(RestConstants2_0.RESOURCE_VERSION));
    }
    
    /**
     * @see RadiologyOrderResource#getByUniqueId(String)
     * @verifies return radiology order given its uuid
     */
    @Test
    public void getByUniqueId_shouldReturnRadiologyOrderGivenItsUuid() throws Exception {
        
        radiologyOrderResource.getByUniqueId(RADIOLOGY_ORDER_UUID);
    }
    
    /**
     * @see RadiologyOrderResource#getDisplayString(RadiologyOrder)
     * @verifies return accession number and concept name of given radiology order
     */
    @Test
    public void getDisplayString_shouldReturnAccessionNumberAndConceptNameOfGivenRadiologyOrder() throws Exception {
        
        ConceptName conceptName = new ConceptName();
        conceptName.setName("X-RAY, HEAD");
        conceptName.setLocale(localeEn);
        Concept concept = new Concept();
        concept.addName(conceptName);
        concept.setPreferredName(conceptName);
        radiologyOrder.setConcept(concept);
        
        assertThat(radiologyOrderResource.getDisplayString(radiologyOrder), is("1 - X-RAY, HEAD"));
    }
    
    /**
     * @see RadiologyOrderResource#getDisplayString(RadiologyOrder)
     * @verifies return no concept string if given radiologyOrders concept is null
     */
    @Test
    public void getDisplayString_shouldReturnNoConceptStringIfGivenRadiologyOrdersConceptIsNull() throws Exception {
        
        assertThat(radiologyOrderResource.getDisplayString(radiologyOrder), is("1 - [No Concept]"));
    }
    
    /**
     * @see RadiologyOrderResource#newDelegate()
     * @verifies throw ResourceDoesNotSupportOperationException
     */
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void newDelegate_shouldThrowResourceDoesNotSupportOperationException() throws Exception {
        
        radiologyOrderResource.newDelegate();
    }
    
    /**
     * @see RadiologyOrderResource#save(RadiologyOrder)
     * @verifies throw ResourceDoesNotSupportOperationException
     */
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void save_shouldThrowResourceDoesNotSupportOperationException() throws Exception {
        
        radiologyOrderResource.save(radiologyOrder);
    }
    
    /**
     * @see RadiologyOrderResource#delete(RadiologyOrder,String,RequestContext)
     * @verifies throw ResourceDoesNotSupportOperationException
     */
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void delete_shouldThrowResourceDoesNotSupportOperationException() throws Exception {
        
        RequestContext requestContext = new RequestContext();
        radiologyOrderResource.delete(radiologyOrder, "wrong order", requestContext);
    }
    
    /**
     * @see RadiologyOrderResource#purge(RadiologyOrder,RequestContext)
     * @verifies throw ResourceDoesNotSupportOperationException
     */
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void purge_shouldThrowResourceDoesNotSupportOperationException() throws Exception {
        
        RequestContext requestContext = new RequestContext();
        radiologyOrderResource.purge(radiologyOrder, requestContext);
    }
}
