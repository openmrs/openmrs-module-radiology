package org.openmrs.module.radiology.report.web.search;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportSearchCriteria;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
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
    
    private static final String DATE_AFTER_REPORT_DATES = "2016-07-02";
    
    private static final String DATE_BEFORE_REPORT_DATES = "2016-05-28";
    
    private static final String DATE_BETWEEN_REPORT_DATES = "2016-06-15";
    
    private static final String RADIOLOGY_REPORT_UUID = "82d3fb80-e403-4b9b-982c-22161ec29811";
    
    @Autowired
    RadiologyReportService radiologyReportService;
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    DateFormat resultFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
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
     * @verifies return all radiology reports with report date after or equal to from date if only date from was specified
     */
    @SuppressWarnings("unchecked")
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
                new RadiologyReportSearchCriteria.Builder().withFromDate(format.parse(DATE_BETWEEN_REPORT_DATES))
                        .build();
        
        assertThat(PropertyUtils.getProperty(hits.get(0), "reportDate"),
            is(resultFormat.format(radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria)
                    .get(0)
                    .getReportDate())));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return all radiology reports with report date before or equal to to date if only date to was specified
     */
    @SuppressWarnings("unchecked")
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
                new RadiologyReportSearchCriteria.Builder().withToDate(format.parse(DATE_BETWEEN_REPORT_DATES))
                        .build();
        
        assertThat(PropertyUtils.getProperty(hits.get(0), "reportDate"),
            is(resultFormat.format(radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria)
                    .get(0)
                    .getReportDate())));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return all radiology reports within given date range if date to and date from are specified
     */
    @SuppressWarnings("unchecked")
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
                new RadiologyReportSearchCriteria.Builder().withFromDate(format.parse(DATE_BEFORE_REPORT_DATES))
                        .withToDate(format.parse(DATE_AFTER_REPORT_DATES))
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        
        assertThat(PropertyUtils.getProperty(hits.get(0), "reportDate"), is(resultFormat.format(radiologyReports.get(0)
                .getReportDate())));
        assertThat(PropertyUtils.getProperty(hits.get(1), "reportDate"), is(resultFormat.format(radiologyReports.get(1)
                .getReportDate())));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return empty search result if no report is in date range
     */
    @SuppressWarnings("unchecked")
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
     * @verifies return all radiology reports within given date range and totalCount if requested
     */
    @Test
    public void search_shouldReturnAllRadiologyReportsWithinGivenDateRangeAndTotalCountIfRequested() throws Exception {
        
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
