package org.openmrs.module.radiology.report.web.search;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportSearchCriteria;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Find RadiologyReport's that match the specified search phrase.
 */
@Component
public class RadiologyReportSearchHandler implements SearchHandler {
    
    
    public static final String REQUEST_PARAM_DATE_FROM = "fromdate";
    
    public static final String REQUEST_PARAM_DATE_TO = "todate";
    
    @Autowired
    RadiologyReportService radiologyReportService;
    
    SearchQuery searchQuery = new SearchQuery.Builder("Allows you to search for RadiologyReport's by from and to date range")
            .withOptionalParameters(REQUEST_PARAM_DATE_FROM, REQUEST_PARAM_DATE_TO)
            .build();
    
    private final SearchConfig searchConfig =
            new SearchConfig("default", RestConstants.VERSION_1 + "/radiologyreport", Arrays.asList("2.0.*"), searchQuery);
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     */
    @Override
    public SearchConfig getSearchConfig() {
        
        return this.searchConfig;
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     * @should return a list of radiology reports being within the date range
     * @should return a list of radiology reports with report date after or equal to from date
     * @should return a list of radiology reports with report date before or equal to from date
     * @should return empty search result if no report is in date range
     */
    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        
        final String fromDateString = context.getRequest()
                .getParameter(REQUEST_PARAM_DATE_FROM);
        final String toDateString = context.getRequest()
                .getParameter(REQUEST_PARAM_DATE_TO);
        
        Date fromDate = null;
        Date toDate = null;
        if (fromDateString != null) {
            fromDate = (Date) ConversionUtil.convert(fromDateString, Date.class);
        }
        if (toDateString != null) {
            toDate = (Date) ConversionUtil.convert(toDateString, Date.class);
        }
        
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().setFromDate(fromDate)
                        .setToDate(toDate)
                        .build();
        
        List<RadiologyReport> result = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        
        if (result.isEmpty()) {
            return new EmptySearchResult();
        }
        return new NeedsPaging<RadiologyReport>(result, context);
    }
}
