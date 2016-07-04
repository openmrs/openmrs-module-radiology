package org.openmrs.module.radiology.report.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.radiology.web.RadiologyRestController;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

/**
 * Tests {@link RadiologyRestController} with respect to {@link RadiologyReportResource}.
 */
public class RadiologyReportControllerComponentTest extends MainResourceControllerTest {
    
    
    protected static final String TEST_DATASET = "RadiologyReportResourceComponentTestDataset.xml";
    
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
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        
        return "82d3fb80-e403-4b9b-982c-22161ec29810";
    }
    
    @Override
    public long getAllCount() {
        
        return 0;
    }
    
    /**
     * @see MainResourceControllerTest#shouldGetAll()
     */
    @Override
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        
        super.shouldGetAll();
    }
}
