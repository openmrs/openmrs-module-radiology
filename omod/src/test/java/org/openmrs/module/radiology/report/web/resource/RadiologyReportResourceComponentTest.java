/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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
        assertPropPresent("date");
        assertPropPresent("principalResultsInterpreter");
        assertPropPresent("status");
        assertPropPresent("body");
        assertPropPresent("display");
        assertPropPresent("voided");
    }
    
    /**
     * @see BaseDelegatingResourceTest#validateFullRepresentation()
     */
    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropPresent("uuid");
        assertPropPresent("radiologyOrder");
        assertPropPresent("date");
        assertPropPresent("principalResultsInterpreter");
        assertPropPresent("status");
        assertPropPresent("body");
        assertPropPresent("display");
        assertPropPresent("voided");
        assertPropPresent("auditInfo");
    }
}
