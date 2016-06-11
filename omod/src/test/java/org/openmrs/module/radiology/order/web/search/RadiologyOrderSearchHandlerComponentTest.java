package org.openmrs.module.radiology.order.web.search;

import org.junit.Before;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

/**
 * Tests {@link RadiologyOrderSearchHandler}.
 */
public class RadiologyOrderSearchHandlerComponentTest extends MainResourceControllerTest {
    
    
    protected static final String TEST_DATASET = "RadiologyOrderResourceComponentTestDataset.xml";
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        
        return "radiologyorder";
    }
    
    @Override
    public long getAllCount() {
        
        return 0;
    }
    
    /**
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        
        return "1bae735a-fca0-11e5-9e59-08002719a237";
    }
}
