package org.openmrs.module.radiology.order.web.resource;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

public class RadiologyOrderControllerComponentTest extends MainResourceControllerTest {
    
    
    protected static final String TEST_DATASET = "RadiologyOrderResourceComponentTestDataset.xml";
    
    @Before
    public void before() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        
        return "radiologyorder";
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        
        return "1bae735a-fca0-11e5-9e59-08002719a237";
    }
    
    @Override
    public long getAllCount() {
        
        return 0;
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetAll()
     */
    @Override
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        
        super.shouldGetAll();
    }
}
