package org.openmrs.module.radiology.order.web.search;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
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
 * Find RadiologyOrder's that match the specified search phrase.
 */
@Component
public class RadiologyOrderSearchHandler implements SearchHandler {
    
    
    public static final String REQUEST_PARAM_QUERY = "q";
    
    public static final String REQUEST_PARAM_PATIENT = "patient";
    
    public static final String REQUEST_PARAM_DATE_FROM = "dateFrom";
    
    public static final String REQUEST_PARAM_DATE_TO = "dateTo";
    
    @Autowired
    PatientService patientService;
    
    @Autowired
    RadiologyOrderService radiologyOrderService;
    
    SearchQuery searchQuery = new SearchQuery.Builder(
            "Allows you to search for RadiologyOrder's, by patient and encounterType (and optionally by from and to date range)")
                    .withRequiredParameters(REQUEST_PARAM_QUERY, REQUEST_PARAM_PATIENT)
                    .withOptionalParameters(REQUEST_PARAM_DATE_FROM, REQUEST_PARAM_DATE_TO)
                    .build();
    
    private final SearchConfig searchConfig =
            new SearchConfig("default", RestConstants.VERSION_1 + "/radiologyorder", Arrays.asList("2.0.*"), searchQuery);
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     */
    
    @Override
    public SearchConfig getSearchConfig() {
        
        return this.searchConfig;
    }
    
    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        
        final String patientQuery = context.getRequest()
                .getParameter(REQUEST_PARAM_PATIENT);
        final String dateFrom = context.getRequest()
                .getParameter(REQUEST_PARAM_DATE_FROM);
        final String dateTo = context.getRequest()
                .getParameter(REQUEST_PARAM_DATE_TO);
        
        final Date fromDate = dateFrom != null ? (Date) ConversionUtil.convert(dateFrom, Date.class) : null;
        final Date toDate = dateTo != null ? (Date) ConversionUtil.convert(dateTo, Date.class) : null;
        
        final List<Patient> patientList = patientService.getPatients(patientQuery);
        List<RadiologyOrder> result = radiologyOrderService.getRadiologyOrdersByPatients(patientList);
        if (result.isEmpty()) {
            return new EmptySearchResult();
        } else {
            return new NeedsPaging<RadiologyOrder>(result, context);
        }
    }
    
}
