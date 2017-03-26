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
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.web.resource.RadiologyOrderResource;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
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
public class RadiologyReportResourceTest {
    
    
    private static final String RADIOLOGY_REPORT_UUID = "8a80c172-ef2f-4cdd-824a-6601556bdefb";
    
    @Mock
    RadiologyReportService radiologyReportService;
    
    @Mock
    RadiologyOrder radiologyOrder;
    
    RadiologyReportResource radiologyReportResource = new RadiologyReportResource();
    
    RadiologyReport radiologyReport;
    
    Locale localeEn = new Locale("en");
    
    @Before
    public void setUp() throws Exception {
        
        when(radiologyOrder.isCompleted()).thenReturn(true);
        
        radiologyReport = new RadiologyReport(radiologyOrder);
        radiologyReport.setUuid(RADIOLOGY_REPORT_UUID);
        
        PowerMockito.mockStatic(RestUtil.class);
        
        PowerMockito.mockStatic(LocaleUtility.class);
        Set<Locale> locales = new HashSet<Locale>();
        locales.add(localeEn);
        when(LocaleUtility.getLocalesInOrder()).thenReturn(locales);
        
        PowerMockito.mockStatic(Context.class);
        when(Context.getService(RadiologyReportService.class)).thenReturn(radiologyReportService);
        when(radiologyReportService.getRadiologyReportByUuid(RADIOLOGY_REPORT_UUID)).thenReturn(radiologyReport);
    }
    
    @Test
    public void shouldReturnDefaultRepresentationGivenInstanceOfDefaultrepresentation() throws Exception {
        
        DefaultRepresentation defaultRepresentation = new DefaultRepresentation();
        
        DelegatingResourceDescription resourceDescription =
                radiologyReportResource.getRepresentationDescription(defaultRepresentation);
        assertThat(resourceDescription.getProperties()
                .keySet(),
            contains("uuid", "radiologyOrder", "date", "principalResultsInterpreter", "status", "body", "display",
                "voided"));
        assertThat(resourceDescription.getProperties()
                .get("radiologyOrder")
                .getRep(),
            is(Representation.REF));
    }
    
    @Test
    public void shouldReturnFullRepresentationGivenInstanceOfFullrepresentation() throws Exception {
        
        FullRepresentation fullRepresentation = new FullRepresentation();
        
        DelegatingResourceDescription resourceDescription =
                radiologyReportResource.getRepresentationDescription(fullRepresentation);
        assertThat(resourceDescription.getProperties()
                .keySet(),
            contains("uuid", "radiologyOrder", "date", "principalResultsInterpreter", "status", "body", "display", "voided",
                "auditInfo"));
        assertThat(resourceDescription.getProperties()
                .get("radiologyOrder")
                .getRep(),
            is(Representation.REF));
    }
    
    @Test
    public void shouldReturnNullForRepresentationOtherThenDefaultOrFull() throws Exception {
        
        CustomRepresentation customRepresentation = new CustomRepresentation("some");
        
        assertThat(radiologyReportResource.getRepresentationDescription(customRepresentation), is(nullValue()));
        
        NamedRepresentation namedRepresentation = new NamedRepresentation("some");
        radiologyReportResource = new RadiologyReportResource();
        
        assertThat(radiologyReportResource.getRepresentationDescription(namedRepresentation), is(nullValue()));
        
        RefRepresentation refRepresentation = new RefRepresentation();
        radiologyReportResource = new RadiologyReportResource();
        
        assertThat(radiologyReportResource.getRepresentationDescription(refRepresentation), is(nullValue()));
    }
    
    @Test
    public void shouldReturnSupportedResourceVersion() throws Exception {
        
        assertThat(radiologyReportResource.getResourceVersion(), is(RestConstants2_0.RESOURCE_VERSION));
    }
    
    @Test
    public void shouldReturnRadiologyReportGivenItsUuid() throws Exception {
        
        assertThat(radiologyReportResource.getByUniqueId(RADIOLOGY_REPORT_UUID)
                .getUuid(),
            is(RADIOLOGY_REPORT_UUID));
    }
    
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldFailToInstantiateANewDelegate() throws Exception {
        
        radiologyReportResource.newDelegate();
    }
    
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldFailToSaveAReport() throws Exception {
        
        radiologyReportResource.save(radiologyReport);
    }
    
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldFailToDeleteAReport() throws Exception {
        
        RequestContext requestContext = new RequestContext();
        
        radiologyReportResource.delete(radiologyReport, "wrong report", requestContext);
    }
    
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldFailToPurgeAReport() throws Exception {
        
        RequestContext requestContext = new RequestContext();
        
        radiologyReportResource.purge(radiologyReport, requestContext);
    }
    
    @Test
    public void shouldReturnOrderNumberAndReportStatusStringOfGivenRadiologyReport() throws Exception {
        
        radiologyReport.setStatus(RadiologyReportStatus.COMPLETED);
        when(radiologyOrder.getOrderNumber()).thenReturn("ORD-1");
        
        assertThat(radiologyReportResource.getDisplayString(radiologyReport), is("ORD-1, COMPLETED"));
    }
}
