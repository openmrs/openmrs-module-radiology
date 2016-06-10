package org.openmrs.module.radiology.order.web.resource;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class RadiologyOrderResourceComponentTest extends BaseDelegatingResourceTest<RadiologyOrderResource, RadiologyOrder> {
    
    
    protected static final String TEST_DATASET = "RadiologyOrderResourceComponentTestDataset.xml";
    
    @Before
    public void before() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    @Override
    public String getDisplayProperty() {
        
        return "FRACTURE";
    }
    
    @Override
    public String getUuidProperty() {
        
        return "1bae735a-fca0-11e5-9e59-08002719a237";
    }
    
    @Override
    public RadiologyOrder newObject() {
        
        return (RadiologyOrder) Context.getOrderService()
                .getOrderByUuid(getUuidProperty());
    }
    
}
