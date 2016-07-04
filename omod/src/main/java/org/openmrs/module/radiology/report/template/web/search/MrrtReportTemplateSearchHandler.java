/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template.web.search;

import java.util.Arrays;
import java.util.List;

import org.openmrs.module.radiology.report.template.MrrtReportTemplate;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateService;
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
 * Find MrrtReportTemplate's that match the specified search phrase.
 */
@Component
public class MrrtReportTemplateSearchHandler implements SearchHandler {
    
    
    public static final String REQUEST_PARAM_TITLE = "title";
    
    public static final String REQUEST_PARAM_TOTAL_COUNT = "totalCount";
    
    @Autowired
    private MrrtReportTemplateService mrrtReportTemplateService;
    
    SearchQuery searchQuery = new SearchQuery.Builder("Allows you to search for MrrtReportTemplate's by title")
            .withRequiredParameters(REQUEST_PARAM_TITLE)
            .withOptionalParameters(REQUEST_PARAM_TOTAL_COUNT)
            .build();
    
    private final SearchConfig searchConfig = new SearchConfig("default", RestConstants.VERSION_1 + "/mrrtreporttemplate",
            Arrays.asList("2.0.*"), searchQuery);
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     */
    @Override
    public SearchConfig getSearchConfig() {
        return this.searchConfig;
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     * @should return all report templates that match given title
     * @should return all report templates for that match given title and totalCount if requested
     * @should return empty search result if title does not exist
     */
    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        final String templateTitle = context.getParameter("title");
        
        List<MrrtReportTemplate> result = mrrtReportTemplateService.getMrrtReportTemplateByTitle(templateTitle);
        if (result.isEmpty()) {
            return new EmptySearchResult();
        } else {
            return new NeedsPaging<MrrtReportTemplate>(result, context);
        }
    }
    
}
