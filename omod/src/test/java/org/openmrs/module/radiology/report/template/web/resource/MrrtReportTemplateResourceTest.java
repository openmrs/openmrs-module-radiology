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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.report.template.MrrtReportTemplate;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateService;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests {@link MrrtReportTemplateResource}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, RestUtil.class })
public class MrrtReportTemplateResourceTest {
    
    
    private static final String MRRT_REPORT_TEMPLATE_UUID = "aa551445-def0-4f93-9047-95f0a9afbdce";
    
    @Mock
    MrrtReportTemplateService mrrtReportTemplateService;
    
    MrrtReportTemplateResource mrrtReportTemplateResource = new MrrtReportTemplateResource();
    
    MrrtReportTemplate mrrtReportTemplate = new MrrtReportTemplate();
    
    @Before
    public void setUp() {
        mrrtReportTemplate.setUuid(MRRT_REPORT_TEMPLATE_UUID);
        
        PowerMockito.mockStatic(RestUtil.class);
        PowerMockito.mockStatic(Context.class);
        when(Context.getService(MrrtReportTemplateService.class)).thenReturn(mrrtReportTemplateService);
        when(mrrtReportTemplateService.getMrrtReportTemplateByUuid(MRRT_REPORT_TEMPLATE_UUID))
                .thenReturn(mrrtReportTemplate);
    }
    
    /**
    * @see MrrtReportTemplateResource#getRepresentationDescription(Representation)
    * @verifies return default representation given instance of defaultrepresentation
    */
    @Test
    public void getRepresentationDescription_shouldReturnDefaultRepresentationGivenInstanceOfDefaultrepresentation()
            throws Exception {
        DefaultRepresentation defaultRepresentation = new DefaultRepresentation();
        
        DelegatingResourceDescription resourceDescription =
                mrrtReportTemplateResource.getRepresentationDescription(defaultRepresentation);
        assertThat(resourceDescription.getProperties()
                .keySet(),
            contains("uuid", "templateId", "dcTermsIdentifier", "dcTermsTitle", "dcTermsType", "dcTermsPublisher",
                "dcTermsCreator", "dcTermsRights", "terms", "display"));
    }
    
    /**
    * @see MrrtReportTemplateResource#getRepresentationDescription(Representation)
    * @verifies return full representation given instance of fullrepresentation
    */
    @Test
    public void getRepresentationDescription_shouldReturnFullRepresentationGivenInstanceOfFullrepresentation()
            throws Exception {
        FullRepresentation fullRepresentation = new FullRepresentation();
        
        DelegatingResourceDescription resourceDescription =
                mrrtReportTemplateResource.getRepresentationDescription(fullRepresentation);
        assertThat(resourceDescription.getProperties()
                .keySet(),
            contains("uuid", "charset", "templateId", "dcTermsIdentifier", "dcTermsTitle", "dcTermsDescription",
                "dcTermsType", "dcTermsLanguage", "dcTermsPublisher", "dcTermsCreator", "dcTermsRights", "dcTermsLicense",
                "dcTermsDate", "terms", "display"));
    }
    
    /**
    * @see MrrtReportTemplateResource#getRepresentationDescription(Representation)
    * @verifies return null for representation other then default or full
    */
    @Test
    public void getRepresentationDescription_shouldReturnNullForRepresentationOtherThenDefaultOrFull() throws Exception {
        CustomRepresentation customRepresentation = new CustomRepresentation("some");
        
        assertThat(mrrtReportTemplateResource.getRepresentationDescription(customRepresentation), is(nullValue()));
        
        NamedRepresentation namedRepresentation = new NamedRepresentation("some");
        mrrtReportTemplateResource = new MrrtReportTemplateResource();
        
        assertThat(mrrtReportTemplateResource.getRepresentationDescription(namedRepresentation), is(nullValue()));
        
        RefRepresentation refRepresentation = new RefRepresentation();
        mrrtReportTemplateResource = new MrrtReportTemplateResource();
        
        assertThat(mrrtReportTemplateResource.getRepresentationDescription(refRepresentation), is(nullValue()));
        
    }
    
    /**
    * @see MrrtReportTemplateResource#getResourceVersion()
    * @verifies return supported resource version
    */
    @Test
    public void getResourceVersion_shouldReturnSupportedResourceVersion() throws Exception {
        assertThat(mrrtReportTemplateResource.getResourceVersion(), is(RestConstants2_0.RESOURCE_VERSION));
    }
    
    /**
    * @see MrrtReportTemplateResource#getByUniqueId(String)
    * @verifies return radiology order given its uuid
    */
    @Test
    public void getByUniqueId_shouldReturnRadiologyOrderGivenItsUuid() throws Exception {
        assertNotNull(mrrtReportTemplateResource.getByUniqueId(MRRT_REPORT_TEMPLATE_UUID));
    }
    
    /**
    * @see MrrtReportTemplateResource#getDisplayString(MrrtReportTemplate)
    * @verifies return templateIdentifier
    */
    @Test
    public void getDisplayString_shouldReturnTemplateIdentifiertitleOfGivenMrrtReportTemplate() throws Exception {
        mrrtReportTemplate.setDcTermsIdentifier("org/radrep/0001");
        assertThat(mrrtReportTemplateResource.getDisplayString(mrrtReportTemplate), is("org/radrep/0001"));
    }
    
    /**
     * @see MrrtReportTemplateResource#delete(MrrtReportTemplate,String,RequestContext)
     * @verifies throw ResourceDoesNotSupportOperationException
     */
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void delete_shouldThrowResourceDoesNotSupportOperationException() throws Exception {
        RequestContext requestContext = new RequestContext();
        mrrtReportTemplateResource.delete(mrrtReportTemplate, "wrong template", requestContext);
    }
    
    /**
     * @see MrrtReportTemplateResource#newDelegate()
     * @verifies throw ResourceDoesNotSupportOperationException
     */
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void newDelegate_shouldThrowResourceDoesNotSupportOperationException() throws Exception {
        mrrtReportTemplateResource.newDelegate();
    }
    
    /**
     * @see MrrtReportTemplateResource#purge(MrrtReportTemplate,RequestContext)
     * @verifies throw ResourceDoesNotSupportOperationException
     */
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void purge_shouldThrowResourceDoesNotSupportOperationException() throws Exception {
        RequestContext requestContext = new RequestContext();
        mrrtReportTemplateResource.purge(mrrtReportTemplate, requestContext);
    }
    
    /**
     * @see MrrtReportTemplateResource#save(MrrtReportTemplate)
     * @verifies throw ResourceDoesNotSupportOperationException
     */
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void save_shouldThrowResourceDoesNotSupportOperationException() throws Exception {
        mrrtReportTemplateResource.save(mrrtReportTemplate);
    }
}
