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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportSearchCriteria;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ProviderResource1_9;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Find {@code RadiologyReport's} that match the specified search phrase.
 * 
 * @see org.openmrs.module.radiology.report.RadiologyReport
 */
@Component
public class RadiologyReportSearchHandler implements SearchHandler {
    
    
    public static final String REQUEST_PARAM_DATE_FROM = "fromdate";
    
    public static final String REQUEST_PARAM_DATE_TO = "todate";
    
    public static final String REQUEST_PARAM_PRINCIPAL_RESULT_INTERPRETER = "principalResultsInterpreter";
    
    public static final String REQUEST_PARAM_STATUS = "status";
    
    public static final String REQUEST_PARAM_TOTAL_COUNT = "totalCount";
    
    @Autowired
    RadiologyReportService radiologyReportService;
    
    SearchQuery searchQuery = new SearchQuery.Builder(
            "Allows you to search for RadiologyReport's by from date, to date and principal results interpreter")
                    .withOptionalParameters(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, REQUEST_PARAM_DATE_FROM,
                        REQUEST_PARAM_DATE_TO, REQUEST_PARAM_PRINCIPAL_RESULT_INTERPRETER, REQUEST_PARAM_STATUS,
                        REQUEST_PARAM_TOTAL_COUNT)
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
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#search(RequestContext)
     * @throws IllegalArgumentException if report status doesn't exist
     * @should return all radiology reports (including voided) matching the search query if include all is set
     * @should return all radiology reports within given date range if date to and date from are specified
     * @should return all radiology reports with report date after or equal to from date if only date from is specified
     * @should return all radiology reports with report date before or equal to to date if only date to is specified
     * @should return empty search result if no report is in date range
     * @should return all radiology reports for given principal results interpreter
     * @should return empty search result if no report exists for principal results interpreter
     * @should return empty search result if principal results interpreter cannot be found
     * @should return all radiology reports with given status
     * @should return empty search result if no report exists for given status
     * @should throw illegal argument exception if report status doesn't exist
     * @should return all radiology reports matching the search query and totalCount if requested
     */
    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        
        final String principalResultsInterpreterUuid = context.getRequest()
                .getParameter(REQUEST_PARAM_PRINCIPAL_RESULT_INTERPRETER);
        Provider principalResultsInterpreter = null;
        if (StringUtils.isNotBlank(principalResultsInterpreterUuid)) {
            principalResultsInterpreter = ((ProviderResource1_9) Context.getService(RestService.class)
                    .getResourceBySupportedClass(Provider.class)).getByUniqueId(principalResultsInterpreterUuid);
            if (principalResultsInterpreter == null) {
                return new EmptySearchResult();
            }
        }
        
        final String fromDateString = context.getRequest()
                .getParameter(REQUEST_PARAM_DATE_FROM);
        Date fromDate = null;
        if (StringUtils.isNotBlank(fromDateString)) {
            fromDate = (Date) ConversionUtil.convert(fromDateString, java.util.Date.class);
        }
        
        final String toDateString = context.getRequest()
                .getParameter(REQUEST_PARAM_DATE_TO);
        Date toDate = null;
        if (StringUtils.isNotBlank(toDateString)) {
            toDate = (Date) ConversionUtil.convert(toDateString, java.util.Date.class);
        }
        
        final String statusString = context.getRequest()
                .getParameter(REQUEST_PARAM_STATUS);
        RadiologyReportStatus status = null;
        if (StringUtils.isNotBlank(statusString)) {
            status = RadiologyReportStatus.valueOf(statusString);
        }
        
        RadiologyReportSearchCriteria.Builder radiologyReportSearchCriteriaBuilder =
                new RadiologyReportSearchCriteria.Builder();
        if (context.getIncludeAll()) {
            radiologyReportSearchCriteriaBuilder.includeVoided();
        }
        RadiologyReportSearchCriteria radiologyReportSearchCriteria = radiologyReportSearchCriteriaBuilder.fromDate(fromDate)
                .toDate(toDate)
                .withPrincipalResultsInterpreter(principalResultsInterpreter)
                .withStatus(status)
                .build();
        
        final List<RadiologyReport> result = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        
        if (result.isEmpty()) {
            return new EmptySearchResult();
        }
        return new NeedsPaging<RadiologyReport>(result, context);
    }
}
