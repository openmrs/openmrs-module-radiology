package org.openmrs.module.radiology.report.web.search;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.web.search.RadiologyOrderSearchHandler;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportSearchCriteria;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Tests {@link RadiologyOrderSearchHandler}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RestUtil.class, Context.class })
public class RadiologyReportSearchHandlerTest {
    
    
    private static final String REPORT1_DATE = "2016-06-01";
    
    private static final String REPORT2_DATE = "2016-07-01";
    
    private static final String DATE_AFTER_REPORT_DATES = "2016-07-02";
    
    private static final String DATE_BEFORE_REPORT_DATES = "2016-05-28";
    
    private static final String DATE_BETWEEN_REPORT_DATES = "2016-06-15";
    
    private static final String PATIENT_UUID_UNKNOWN = "99999999-fca0-11e5-9e59-08002719a237";
    
    @Mock
    RestService restService;
    
    @Mock
    RadiologyReportService radiologyReportService;
    
    RadiologyReportSearchHandler radiologyReportSearchHandler = new RadiologyReportSearchHandler();
    
    @Mock
    RadiologyOrder radiologyOrder;
    
    RadiologyReport radiologyReport1;
    
    RadiologyReport radiologyReport2;
    
    @Before
    public void setUp() throws Exception {
        
        when(radiologyOrder.isCompleted()).thenReturn(true);
        
        radiologyReport1 = new RadiologyReport(radiologyOrder);
        radiologyReport2 = new RadiologyReport(radiologyOrder);
        radiologyReport1.setReportDate((Date) ConversionUtil.convert(REPORT1_DATE, Date.class));
        radiologyReport2.setReportDate((Date) ConversionUtil.convert(REPORT2_DATE, Date.class));
        
        PowerMockito.mockStatic(RestUtil.class);
        PowerMockito.mockStatic(Context.class);
        when(Context.getService(RadiologyReportService.class)).thenReturn(radiologyReportService);
        when(Context.getService(RestService.class)).thenReturn(restService);
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return a list of radiology reports being within the date range
     */
    @Test
    public void search_shouldReturnAListOfRadiologyReportsBeingWithinTheDateRange() throws Exception {
        
        //        RadiologyReportSearchCriteria radiologyReportSearchCriteria = new RadiologyReportSearchCriteria.Builder()
        //                .setFromDate((Date) ConversionUtil.convert(DATE_BEFORE_REPORT_DATES, Date.class))
        //                .setToDate((Date) ConversionUtil.convert(DATE_AFTER_REPORT_DATES, Date.class))
        //                .build();
        //        
        //        List<RadiologyReport> radiologyReports = new ArrayList<RadiologyReport>();
        //        radiologyReports.add(radiologyReport1);
        //        radiologyReports.add(radiologyReport2);
        //               
        //        when(radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria))
        //                .thenReturn(radiologyReports);
        //        
        //        MockHttpServletRequest request = new MockHttpServletRequest();
        //        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_DATE_FROM, PATIENT_UUID_UNKNOWN);
        //        RequestContext requestContext = new RequestContext();
        //        requestContext.setRequest(request);
        //        
        //        PageableResult pageableResult = radiologyReportSearchHandler.search(requestContext);
        //        assertThat(pageableResult, is(instanceOf(EmptySearchResult.class)));
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return a list of radiology reports with report date after or equal to from date
     */
    @Test
    public void search_shouldReturnAListOfRadiologyReportsWithReportDateAfterOrEqualToFromDate() throws Exception {
        // TODO auto-generated
        Assert.fail("Not yet implemented");
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return a list of radiology reports with report date before or equal to from date
     */
    @Test
    public void search_shouldReturnAListOfRadiologyReportsWithReportDateBeforeOrEqualToFromDate() throws Exception {
        // TODO auto-generated
        Assert.fail("Not yet implemented");
    }
    
    /**
     * @see RadiologyReportSearchHandler#search(RequestContext)
     * @verifies return empty search result if no report is in date range
     */
    @Test
    public void search_shouldReturnEmptySearchResultIfNoReportIsInDateRange() throws Exception {
        
        RadiologyReportSearchCriteria radiologyReportSearchCriteria = new RadiologyReportSearchCriteria.Builder()
                .setFromDate((Date) ConversionUtil.convert(DATE_AFTER_REPORT_DATES, Date.class))
                .build();
        when(radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria))
                .thenReturn(new ArrayList<RadiologyReport>());
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(RadiologyReportSearchHandler.REQUEST_PARAM_DATE_FROM, DATE_AFTER_REPORT_DATES);
        RequestContext requestContext = new RequestContext();
        requestContext.setRequest(request);
        
        PageableResult pageableResult = radiologyReportSearchHandler.search(requestContext);
        assertThat(pageableResult, is(instanceOf(EmptySearchResult.class)));
    }
    
}
