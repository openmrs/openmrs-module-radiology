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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.modality.RadiologyModality;
import org.openmrs.module.radiology.modality.RadiologyModalityService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.RestConstants2_0;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests {@link RadiologyModalityResource}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, RestUtil.class, LocaleUtility.class })
public class RadiologyModalityResourceTest {
    
    
    private static final String RADIOLOGY_MODALITY_UUID = "015f85fc-1316-45a3-848d-69ba192e64c4";
    
    @Mock
    RadiologyModalityService radiologyModalityService;
    
    @Mock
    RequestContext requestContext;
    
    RadiologyModalityResource radiologyModalityResource = new RadiologyModalityResource();
    
    RadiologyModality radiologyModality = new RadiologyModality();
    
    Locale localeEn = new Locale("en");
    
    @Before
    public void setUp() throws Exception {
        
        radiologyModality.setName("Exzelsior MAX9000");
        radiologyModality.setAeTitle("CT01");
        radiologyModality.setUuid(RADIOLOGY_MODALITY_UUID);
        
        PowerMockito.mockStatic(RestUtil.class);
        
        PowerMockito.mockStatic(LocaleUtility.class);
        Set<Locale> locales = new HashSet<Locale>();
        locales.add(localeEn);
        when(LocaleUtility.getLocalesInOrder()).thenReturn(locales);
        
        PowerMockito.mockStatic(Context.class);
        when(Context.getService(RadiologyModalityService.class)).thenReturn(radiologyModalityService);
        when(radiologyModalityService.getRadiologyModalityByUuid(RADIOLOGY_MODALITY_UUID)).thenReturn(radiologyModality);
        when(radiologyModalityService.saveRadiologyModality(radiologyModality)).thenReturn(radiologyModality);
    }
    
    @Test
    public void shouldReturnSupportedResourceVersion() throws Exception {
        
        assertThat(radiologyModalityResource.getResourceVersion(), is(RestConstants2_0.RESOURCE_VERSION));
    }
    
    @Test
    public void shouldReturnDefaultRepresentationGivenInstanceOfDefaultrepresentation() throws Exception {
        
        DefaultRepresentation defaultRepresentation = new DefaultRepresentation();
        
        DelegatingResourceDescription resourceDescription =
                radiologyModalityResource.getRepresentationDescription(defaultRepresentation);
        assertThat(resourceDescription.getProperties()
                .keySet(),
            contains("uuid", "display", "aeTitle", "name", "description", "retired"));
    }
    
    @Test
    public void shouldReturnFullRepresentationGivenInstanceOfFullrepresentation() throws Exception {
        
        FullRepresentation fullRepresentation = new FullRepresentation();
        
        DelegatingResourceDescription resourceDescription =
                radiologyModalityResource.getRepresentationDescription(fullRepresentation);
        assertThat(resourceDescription.getProperties()
                .keySet(),
            contains("uuid", "display", "aeTitle", "name", "description", "retired", "auditInfo"));
    }
    
    @Test
    public void shouldReturnNullForRepresentationOtherThenDefaultOrFull() throws Exception {
        
        CustomRepresentation customRepresentation = new CustomRepresentation("some");
        
        assertThat(radiologyModalityResource.getRepresentationDescription(customRepresentation), is(nullValue()));
        
        NamedRepresentation namedRepresentation = new NamedRepresentation("some");
        radiologyModalityResource = new RadiologyModalityResource();
        
        assertThat(radiologyModalityResource.getRepresentationDescription(namedRepresentation), is(nullValue()));
        
        RefRepresentation refRepresentation = new RefRepresentation();
        radiologyModalityResource = new RadiologyModalityResource();
        
        assertThat(radiologyModalityResource.getRepresentationDescription(refRepresentation), is(nullValue()));
    }
    
    @Test
    public void shouldReturnAeTitleOfGivenRadiologyModality() throws Exception {
        
        assertThat(radiologyModalityResource.getDisplayString(radiologyModality), is(radiologyModality.getAeTitle()));
    }
    
    @Test
    public void shouldReturnRadiologyModalityGivenItsUuid() throws Exception {
        
        assertThat(radiologyModalityResource.getByUniqueId(RADIOLOGY_MODALITY_UUID), is(radiologyModality));
        verify(radiologyModalityService, times(1)).getRadiologyModalityByUuid(RADIOLOGY_MODALITY_UUID);
        verifyNoMoreInteractions(radiologyModalityService);
    }
    
    @Test
    public void shouldSaveGivenRadiologyModality() throws Exception {
        
        assertThat(radiologyModalityResource.save(radiologyModality), is(radiologyModality));
        verify(radiologyModalityService, times(1)).saveRadiologyModality(radiologyModality);
        verifyNoMoreInteractions(radiologyModalityService);
    }
    
    @Test
    public void shouldRetireGivenRadiologyModality() throws Exception {
        
        radiologyModalityResource.delete(radiologyModality, "out of order", requestContext);
        verify(radiologyModalityService, times(1)).retireRadiologyModality(radiologyModality, "out of order");
        verifyNoMoreInteractions(radiologyModalityService);
    }
    
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldThrowResourceDoesNotSupportOperationException() throws Exception {
        
        RequestContext requestContext = new RequestContext();
        
        radiologyModalityResource.purge(radiologyModality, requestContext);
    }
}
