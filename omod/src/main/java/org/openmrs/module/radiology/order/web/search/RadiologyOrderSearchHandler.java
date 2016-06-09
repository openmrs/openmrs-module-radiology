package org.openmrs.module.radiology.order.web.search;

import java.util.Arrays;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
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
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.PatientResource1_9;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Find RadiologyOrder's that match the specified search phrase.
 */
@Component
public class RadiologyOrderSearchHandler implements SearchHandler {
    
    
    public static final String REQUEST_PARAM_PATIENT = "patient";
    
    @Autowired
    RadiologyOrderService radiologyOrderService;
    
    SearchQuery searchQuery = new SearchQuery.Builder("Allows you to search for RadiologyOrder's by patient")
            .withRequiredParameters(REQUEST_PARAM_PATIENT)
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
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     * @should return all radiology orders for given patient
     * @should return empty search result if patient cannot be found
     * @should return empty search result if patient has no radiology orders
     */
    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        
        final String patientUuid = context.getRequest()
                .getParameter(REQUEST_PARAM_PATIENT);
        
        final Patient patient = ((PatientResource1_9) Context.getService(RestService.class)
                .getResourceBySupportedClass(Patient.class)).getByUniqueId(patientUuid);
        if (patient == null) {
            return new EmptySearchResult();
        }
        
        List<RadiologyOrder> result = radiologyOrderService.getRadiologyOrdersByPatient(patient);
        if (result.isEmpty()) {
            return new EmptySearchResult();
        } else {
            return new NeedsPaging<RadiologyOrder>(result, context);
        }
    }
}
