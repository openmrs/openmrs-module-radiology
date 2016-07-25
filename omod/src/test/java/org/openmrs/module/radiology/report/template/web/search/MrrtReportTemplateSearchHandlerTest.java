/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template.web.search;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.report.template.MrrtReportTemplate;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateSearchCriteria;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Tests {@link MrrtReportTemplateSearchHandler}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RestUtil.class, Context.class })
public class MrrtReportTemplateSearchHandlerTest {
    
    
    private static final String MRRT_REPORT_TEMPLATE1_TITLE = "Cardiac MRI: Adenosine Stress Protocol";
    
    private static final String MRRT_REPORT_TEMPLATE2_TITLE = "Cardiac MRI: Function and Viability";
    
    private static final String NON_EXISTING_TITLE = "Invalid";
    
    private static final String BLANK_TITLE = "";
    
    private static final String TITLE_QUERY = "Cardiac MRI";
    
    @Mock
    RestService RestService;
    
    @Mock
    MrrtReportTemplateService mrrtReportTemplateService;
    
    @InjectMocks
    MrrtReportTemplateSearchHandler mrrtReportTemplateSearchHandler;
    
    MrrtReportTemplate mrrtReportTemplate1 = new MrrtReportTemplate();
    
    MrrtReportTemplate mrrtReportTemplate2 = new MrrtReportTemplate();
    
    List<MrrtReportTemplate> mrrtReportTemplates;
    
    @Before
    public void setUp() {
        mrrtReportTemplate1.setDcTermsTitle(MRRT_REPORT_TEMPLATE1_TITLE);
        mrrtReportTemplate2.setDcTermsTitle(MRRT_REPORT_TEMPLATE2_TITLE);
        mrrtReportTemplates = new ArrayList<>();
        mrrtReportTemplates.add(mrrtReportTemplate1);
        mrrtReportTemplates.add(mrrtReportTemplate2);
        
        PowerMockito.mockStatic(RestUtil.class);
        PowerMockito.mockStatic(Context.class);
    }
    
    /**
    * @see MrrtReportTemplateSearchHandler#search(RequestContext)
    * @verifies return empty search result if title does not exist
    */
    @Test
    public void search_shouldReturnEmptySearchResultIfTitleDoesNotExist() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_TITLE, NON_EXISTING_TITLE);
        RequestContext requestContext = new RequestContext();
        requestContext.setRequest(request);
        
        PageableResult pageableResult = mrrtReportTemplateSearchHandler.search(requestContext);
        assertThat(pageableResult, is(instanceOf(EmptySearchResult.class)));
    }
    
    /**
     * @see MrrtReportTemplateSearchHandler#search(RequestContext)
     * @verifies return all report templates that match given title
     */
    @Test
    public void search_shouldReturnAllReportTemplatesThatMatchGivenTitle() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_TITLE, TITLE_QUERY);
        RequestContext requestContext = new RequestContext();
        requestContext.setRequest(request);
        when(mrrtReportTemplateService.getMrrtReportTemplates(any(MrrtReportTemplateSearchCriteria.class)))
                .thenReturn(mrrtReportTemplates);
        
        PageableResult pageableResult = mrrtReportTemplateSearchHandler.search(requestContext);
        assertThat(pageableResult, is(instanceOf(NeedsPaging.class)));
    }
}
