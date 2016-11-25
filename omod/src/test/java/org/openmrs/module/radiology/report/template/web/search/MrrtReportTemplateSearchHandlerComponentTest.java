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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateSearchCriteria;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *  Tests {@link MrrtReportTemplateSearchHandler}
 */
public class MrrtReportTemplateSearchHandlerComponentTest extends MainResourceControllerTest {
    
    
    private static final String TEST_DATASET = "MrrtReportTemplateSearchHandlerComponentTestDataset.xml";
    
    private static final String MRRT_REPORT_TEMPLATE_UUID = "2379d290-96f7-408a-bbae-270387e3b92e";
    
    private static final String NON_EXISTING_TITLE = "invalid title";
    
    private static final String TITLE_QUERY = "Cardiac MRI";
    
    private static final String PUBLISHER_QUERY = "IHE CAT Publisher";
    
    private static final String NON_EXISTING_PUBLISHER = "Non existing publisher";
    
    private static final String LICENSE_QUERY = "General Public License";
    
    private static final String NON_EXISTING_LICENSE = "Non existing license";
    
    private static final String CREATOR_QUERY = "creator1";
    
    private static final String NON_EXISTING_CREATOR = "Non existing creator";
    
    @Autowired
    MrrtReportTemplateService mrrtReportTemplateService;
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    @Override
    public long getAllCount() {
        
        return 0;
    }
    
    @Override
    public String getURI() {
        return "mrrtreporttemplate";
    }
    
    @Override
    public String getUuid() {
        return MRRT_REPORT_TEMPLATE_UUID;
    }
    
    /**
     * @see MainResourceControllerTest#shouldGetAll()
     */
    @Override
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        
        deserialize(handle(request(RequestMethod.GET, getURI())));
    }
    
    /**
    * @see MrrtReportTemplateSearchHandler#search(RequestContext)
    * @verifies return empty search result if title does not exist
    */
    @Test
    public void search_shouldReturnEmptySearchResultIfTitleDoesNotExist() throws Exception {
        MockHttpServletRequest mockRequest = request(RequestMethod.GET, getURI());
        mockRequest.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_TITLE, NON_EXISTING_TITLE);
        
        SimpleObject resultMrrtReportTemplate = deserialize(handle(mockRequest));
        
        assertNotNull(resultMrrtReportTemplate);
        List<Object> hits = (List<Object>) resultMrrtReportTemplate.get("results");
        assertThat(hits.size(), is(0));
    }
    
    /**
     * @see MrrtReportTemplateSearchHandler#search(RequestContext)
     * @verifies return all report templates that match given title
     */
    @Test
    public void search_shouldReturnAllReportTemplatesThatMatchGivenTitle() throws Exception {
        MockHttpServletRequest mrrtReportTemplateRequest = request(RequestMethod.GET, getURI());
        mrrtReportTemplateRequest.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_TITLE, TITLE_QUERY);
        mrrtReportTemplateRequest.setParameter("v", Representation.FULL.getRepresentation());
        SimpleObject resultMrrtReportTemplate = deserialize(handle(mrrtReportTemplateRequest));
        
        assertNotNull(resultMrrtReportTemplate);
        List<Object> hits = (List<Object>) resultMrrtReportTemplate.get("results");
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withTitle(TITLE_QUERY)
                        .build();
        assertThat(hits.size(), is(2));
        assertThat(PropertyUtils.getProperty(hits.get(0), "uuid"),
            is(mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria)
                    .get(0)
                    .getUuid()));
        assertNull(PropertyUtils.getProperty(resultMrrtReportTemplate, "totalCount"));
    }
    
    /**
     * @see MrrtReportTemplateSearchHandler#search(RequestContext)
     * @verifies return all mrrt templates that match given title and totalCount if requested
     */
    @Test
    public void search_shouldReturnAllMrrtTemplatesThatMatchGivenTitleAndTotalCountIfRequested() throws Exception {
        MockHttpServletRequest requestMrrtReportTemplate = request(RequestMethod.GET, getURI());
        requestMrrtReportTemplate.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_TITLE, TITLE_QUERY);
        requestMrrtReportTemplate.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_TOTAL_COUNT, "true");
        SimpleObject resultMrrtReportTemplate = deserialize(handle(requestMrrtReportTemplate));
        
        assertNotNull(resultMrrtReportTemplate);
        assertThat(PropertyUtils.getProperty(resultMrrtReportTemplate, "totalCount"), is(2));
    }
    
    /**
     * @see MrrtReportTemplateSearchHandler#search(RequestContext)
     * @verifies return all report templates by given publisher
     */
    @Test
    public void search_shouldReturnAllReportTemplatesByGivenPublisher() throws Exception {
        
        MockHttpServletRequest mrrtReportTemplateRequest = request(RequestMethod.GET, getURI());
        mrrtReportTemplateRequest.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_PUBLISHER, PUBLISHER_QUERY);
        SimpleObject resultMrrtReportTemplate = deserialize(handle(mrrtReportTemplateRequest));
        
        assertNotNull(resultMrrtReportTemplate);
        List<Object> hits = (List<Object>) resultMrrtReportTemplate.get("results");
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withPublisher(PUBLISHER_QUERY)
                        .build();
        assertThat(hits.size(), is(1));
        assertThat(PropertyUtils.getProperty(hits.get(0), "uuid"),
            is(mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria)
                    .get(0)
                    .getUuid()));
        assertNull(PropertyUtils.getProperty(resultMrrtReportTemplate, "totalCount"));
    }
    
    /**
     * @see MrrtReportTemplateSearchHandler#search(RequestContext)
     * @verifies return empty search result if publisher does not exist
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfPublisherDoesNotExist() throws Exception {
        
        MockHttpServletRequest mockRequest = request(RequestMethod.GET, getURI());
        mockRequest.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_PUBLISHER, NON_EXISTING_PUBLISHER);
        
        SimpleObject resultMrrtReportTemplate = deserialize(handle(mockRequest));
        
        assertNotNull(resultMrrtReportTemplate);
        List<Object> hits = (List<Object>) resultMrrtReportTemplate.get("results");
        assertThat(hits.size(), is(0));
    }
    
    /**
     * @see MrrtReportTemplateSearchHandler#search(RequestContext)
     * @verifies return all report templates that match given license
     */
    @Test
    public void search_shouldReturnAllReportTemplatesThatMatchGivenLicense() throws Exception {
        MockHttpServletRequest mrrtReportTemplateRequest = request(RequestMethod.GET, getURI());
        mrrtReportTemplateRequest.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_LICENSE, LICENSE_QUERY);
        SimpleObject resultMrrtReportTemplate = deserialize(handle(mrrtReportTemplateRequest));
        
        assertNotNull(resultMrrtReportTemplate);
        List<Object> hits = (List<Object>) resultMrrtReportTemplate.get("results");
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withLicense(LICENSE_QUERY)
                        .build();
        assertThat(hits.size(), is(1));
        assertThat(PropertyUtils.getProperty(hits.get(0), "uuid"),
            is(mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria)
                    .get(0)
                    .getUuid()));
        assertNull(PropertyUtils.getProperty(resultMrrtReportTemplate, "totalCount"));
    }
    
    /**
     * @see MrrtReportTemplateSearchHandler#search(RequestContext)
     * @verifies return empty search result if license does not exist
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfLicenseDoesNotExist() throws Exception {
        MockHttpServletRequest mockRequest = request(RequestMethod.GET, getURI());
        mockRequest.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_LICENSE, NON_EXISTING_LICENSE);
        SimpleObject resultMrrtReportTemplate = deserialize(handle(mockRequest));
        
        assertNotNull(resultMrrtReportTemplate);
        List<Object> hits = (List<Object>) resultMrrtReportTemplate.get("results");
        assertThat(hits.size(), is(0));
    }
    
    /**
     * @see MrrtReportTemplateSearchHandler#search(RequestContext)
     * @verifies return all report templates that match given creator
     */
    @Test
    public void search_shouldReturnAllReportTemplatesThatMatchGivenCreator() throws Exception {
        MockHttpServletRequest mrrtReportTemplateRequest = request(RequestMethod.GET, getURI());
        mrrtReportTemplateRequest.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_CREATOR, CREATOR_QUERY);
        SimpleObject resultMrrtReportTemplate = deserialize(handle(mrrtReportTemplateRequest));
        
        assertNotNull(resultMrrtReportTemplate);
        List<Object> hits = (List<Object>) resultMrrtReportTemplate.get("results");
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withCreator(CREATOR_QUERY)
                        .build();
        assertThat(hits.size(), is(1));
        assertThat(PropertyUtils.getProperty(hits.get(0), "uuid"),
            is(mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria)
                    .get(0)
                    .getUuid()));
        assertNull(PropertyUtils.getProperty(resultMrrtReportTemplate, "totalCount"));
    }
    
    /**
     * @see MrrtReportTemplateSearchHandler#search(RequestContext)
     * @verifies return empty search result if creator does not exist
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfCreatorDoesNotExist() throws Exception {
        MockHttpServletRequest mockRequest = request(RequestMethod.GET, getURI());
        mockRequest.setParameter(MrrtReportTemplateSearchHandler.REQUEST_PARAM_CREATOR, NON_EXISTING_CREATOR);
        SimpleObject resultMrrtReportTemplate = deserialize(handle(mockRequest));
        
        assertNotNull(resultMrrtReportTemplate);
        List<Object> hits = (List<Object>) resultMrrtReportTemplate.get("results");
        assertThat(hits.size(), is(0));
    }
}
