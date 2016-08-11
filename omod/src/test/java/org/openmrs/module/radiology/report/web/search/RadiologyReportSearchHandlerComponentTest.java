/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.web.search;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.api.ProviderService;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportSearchCriteria;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests {@link RadiologyReportSearchHandler}.
 */
public class RadiologyReportSearchHandlerComponentTest extends MainResourceControllerTest {
    
    
    protected static final String TEST_DATASET = "RadiologyReportSearchHandlerComponentTestDataset.xml";
    
    private static final String RADIOLOGY_REPORT_UUID_OF_VOIDED = "90765170-473f-11e6-beb8-9e71128cae77";
    
    private static final String DATE_AFTER_REPORT_DATES = "2016-07-02";
    
    private static final String DATE_BEFORE_REPORT_DATES = "2016-05-28";
    
    private static final String DATE_BETWEEN_REPORT_DATES = "2016-06-15";
    
    private static final String PROVIDER_WITH_RADIOLOGY_REPORTS = "c2299800-cca9-11e0-9572-0800200c9a66";
    
    private static final String PROVIDER_WITHOUT_RADIOLOGY_REPORTS = "550e8400-e29b-11d4-a716-446655440000";
    
    private static final String RADIOLOGY_REPORT_UUID = "82d3fb80-e403-4b9b-982c-22161ec29811";
    
    @Autowired
    RadiologyReportService radiologyReportService;
    
    @Autowired
    ProviderService providerService;
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    DateFormat resultFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        
        return "radiologyreport";
    }
    
    /**
     * @see MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        
        return 0;
    }
    
    /**
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        
        return RADIOLOGY_REPORT_UUID;
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
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return all radiology reports (including voided) matching the search query if include all is set
     */
    @Test
    public void search_shouldReturnAllRadiologyReportsIncludingDiscontinuedMatchingTheSearchQueryIfIncludeAllIsSet()
            throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_PRINCIPAL_RESULT_INTERPRETER,
            PROVIDER_WITH_RADIOLOGY_REPORTS);
        request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, "true");
        request.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertThat(hits.size(), is(3));
        assertThat(PropertyUtils.getProperty(hits.get(2), "uuid"), is(RADIOLOGY_REPORT_UUID_OF_VOIDED));
        assertThat(PropertyUtils.getProperty(hits.get(2), "voided"), is(true));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return all radiology reports with report date after or equal to from date if only date from was specified
     */
    @Test
    public void search_shouldReturnAllRadiologyReportsWithReportDateAfterOrEqualToFromDateIfOnlyDateFromWasSpecified()
            throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_DATE_FROM, DATE_BETWEEN_REPORT_DATES);
        request.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertThat(hits.size(), is(1));
        assertNull(PropertyUtils.getProperty(result, "totalCount"));
        
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().fromDate(format.parse(DATE_BETWEEN_REPORT_DATES))
                        .build();
        
        assertThat(PropertyUtils.getProperty(hits.get(0), "date"),
            is(resultFormat.format(radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria)
                    .get(0)
                    .getDate())));
        assertThat(PropertyUtils.getProperty(hits.get(0), "voided"), is(false));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return all radiology reports with report date before or equal to to date if only date to was specified
     */
    @Test
    public void search_shouldReturnAllRadiologyReportsWithReportDateBeforeOrEqualToToDateIfOnlyDateToWasSpecified()
            throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_DATE_TO, DATE_BETWEEN_REPORT_DATES);
        request.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertThat(hits.size(), is(1));
        assertNull(PropertyUtils.getProperty(result, "totalCount"));
        
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().toDate(format.parse(DATE_BETWEEN_REPORT_DATES))
                        .build();
        
        assertThat(PropertyUtils.getProperty(hits.get(0), "date"),
            is(resultFormat.format(radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria)
                    .get(0)
                    .getDate())));
        assertThat(PropertyUtils.getProperty(hits.get(0), "voided"), is(false));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return all radiology reports within given date range if date to and date from are specified
     */
    @Test
    public void search_shouldReturnAllRadiologyReportsWithinGivenDateRangeIfDateToAndDateFromAreSpecified()
            throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_DATE_FROM, DATE_BEFORE_REPORT_DATES);
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_DATE_TO, DATE_AFTER_REPORT_DATES);
        request.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertThat(hits.size(), is(2));
        assertNull(PropertyUtils.getProperty(result, "totalCount"));
        
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().fromDate(format.parse(DATE_BEFORE_REPORT_DATES))
                        .toDate(format.parse(DATE_AFTER_REPORT_DATES))
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        
        assertThat(PropertyUtils.getProperty(hits.get(0), "date"), is(resultFormat.format(radiologyReports.get(0)
                .getDate())));
        assertThat(PropertyUtils.getProperty(hits.get(1), "date"), is(resultFormat.format(radiologyReports.get(1)
                .getDate())));
        assertThat(PropertyUtils.getProperty(hits.get(0), "voided"), is(false));
        assertThat(PropertyUtils.getProperty(hits.get(1), "voided"), is(false));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return empty search result if no report is in date range
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfNoReportIsInDateRange() throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_DATE_FROM, DATE_AFTER_REPORT_DATES);
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertTrue(hits.isEmpty());
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return all radiology reports for given principal results interpreter
     */
    @Test
    public void search_shouldReturnAllRadiologyReportsForGivenPrincipalResultsInterpreter() throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_PRINCIPAL_RESULT_INTERPRETER,
            PROVIDER_WITH_RADIOLOGY_REPORTS);
        request.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertThat(hits.size(), is(2));
        assertThat(PropertyUtils.getProperty(hits.get(0), "voided"), is(false));
        assertThat(PropertyUtils.getProperty(hits.get(1), "voided"), is(false));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return empty search result if no report exists for principal results interpreter
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfNoReportExistsForPrincipalResultsInterpreter() throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_PRINCIPAL_RESULT_INTERPRETER,
            PROVIDER_WITHOUT_RADIOLOGY_REPORTS);
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertTrue(hits.isEmpty());
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return empty search result if principal results interpreter cannot be found
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfPrincipalResultsInterpreterCannotBeFound() throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_PRINCIPAL_RESULT_INTERPRETER, "wrong_uuid");
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertTrue(hits.isEmpty());
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return all radiology reports with given status
     */
    @Test
    public void search_shouldReturnAllRadiologyReportsWithGivenStatus() throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_STATUS, "COMPLETED");
        request.setParameter("v", Representation.FULL.getRepresentation());
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertThat(hits.size(), is(2));
        assertThat(PropertyUtils.getProperty(hits.get(0), "status"), is(RadiologyReportStatus.COMPLETED.toString()));
        assertThat(PropertyUtils.getProperty(hits.get(1), "status"), is(RadiologyReportStatus.COMPLETED.toString()));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return empty search result if no report exists for given status
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfNoReportExistsForGivenStatus() throws Exception {
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_STATUS, "DRAFT");
        
        SimpleObject result = deserialize(handle(request));
        
        assertNotNull(result);
        List<Object> hits = (List<Object>) result.get("results");
        assertTrue(hits.isEmpty());
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies throw illegal argument exception if report status doesn't exist
     */
    @Test
    public void search_shouldThrowIllegalArgumentExceptionIfReportStatusDoesntExist() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_STATUS, "wrong_status");
        
        deserialize(handle(request));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return all radiology reports matching the search query and totalCount if requested
     */
    @Test
    public void search_shouldReturnAllRadiologyReportsMatchingTheSearchQueryAndTotalCountIfRequested() throws Exception {
        
        MockHttpServletRequest requestDateRangeWithOneReport = request(RequestMethod.GET, getURI());
        requestDateRangeWithOneReport.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_DATE_FROM,
            DATE_BETWEEN_REPORT_DATES);
        requestDateRangeWithOneReport.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_TOTAL_COUNT, "true");
        
        SimpleObject resultDateRangeWithOneReport = deserialize(handle(requestDateRangeWithOneReport));
        
        assertNotNull(resultDateRangeWithOneReport);
        assertThat(PropertyUtils.getProperty(resultDateRangeWithOneReport, "totalCount"), is(1));
        
        MockHttpServletRequest requestDateRangeWithTwoReport = request(RequestMethod.GET, getURI());
        requestDateRangeWithTwoReport.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_DATE_FROM,
            DATE_BEFORE_REPORT_DATES);
        requestDateRangeWithTwoReport.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_TOTAL_COUNT, "true");
        
        SimpleObject resultDateRangeWithTwoReport = deserialize(handle(requestDateRangeWithTwoReport));
        
        assertNotNull(resultDateRangeWithTwoReport);
        assertThat(PropertyUtils.getProperty(resultDateRangeWithTwoReport, "totalCount"), is(2));
    }
}
