package org.openmrs.module.radiology.report.web.resource;

import org.junit.Before;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link RadiologyReportResource}.
 */
public class RadiologyReportResourceComponentTest
        extends BaseDelegatingResourceTest<RadiologyReportResource, RadiologyReport> {
    
    
    protected static final String TEST_DATASET = "RadiologyReportResourceComponentTestDataset.xml";
    
    @Autowired
    RadiologyReportService radiologyReportService;
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see BaseDelegatingResourceTest#getDisplayProperty()
     */
    @Override
    public String getDisplayProperty() {
        
        return "2007, COMPLETED";
    }
    
    /**
     * @see BaseDelegatingResourceTest#getUuidProperty()
     */
    @Override
    public String getUuidProperty() {
        
        return "82d3fb80-e403-4b9b-982c-22161ec29810";
    }
    
    /**
     * @see BaseDelegatingResourceTest#newObject()
     */
    @Override
    public RadiologyReport newObject() {
        
        return radiologyReportService.getRadiologyReportByUuid(getUuidProperty());
    }
    
    /**
     * @see BaseDelegatingResourceTest#validateDefaultRepresentation()
     */
    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropPresent("uuid");
        assertPropPresent("radiologyOrder");
        assertPropPresent("reportDate");
        assertPropPresent("principalResultsInterpreter");
        assertPropPresent("reportStatus");
        assertPropPresent("reportBody");
        assertPropPresent("display");
    }
    
    /**
     * @see BaseDelegatingResourceTest#validateFullRepresentation()
     */
    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropPresent("uuid");
        assertPropPresent("radiologyOrder");
        assertPropPresent("reportDate");
        assertPropPresent("principalResultsInterpreter");
        assertPropPresent("reportStatus");
        assertPropPresent("reportBody");
        assertPropPresent("display");
        assertPropPresent("auditInfo");
    }
}
