/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality.web.resource;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.radiology.modality.RadiologyModality;
import org.openmrs.module.radiology.modality.RadiologyModalityService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link RadiologyModalityResource}.
 */
public class RadiologyModalityResourceComponentTest
        extends BaseDelegatingResourceTest<RadiologyModalityResource, RadiologyModality> {
    
    
    protected static final String TEST_DATASET = "RadiologyModalityResourceComponentTestDataset.xml";
    
    private static final int TOTAL_MODALITIES = 4;
    
    private static final int TOTAL_MODALITIES_NON_RETIRED = 3;
    
    @Autowired
    RadiologyModalityService radiologyModalityService;
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see BaseDelegatingResourceTest#getDisplayProperty()
     */
    @Override
    public String getDisplayProperty() {
        
        return "CT01";
    }
    
    /**
     * @see BaseDelegatingResourceTest#getUuidProperty()
     */
    @Override
    public String getUuidProperty() {
        
        return "015f85fc-1316-45a3-848d-69ba192e64c4";
    }
    
    /**
     * @see BaseDelegatingResourceTest#newObject()
     */
    @Override
    public RadiologyModality newObject() {
        
        return radiologyModalityService.getRadiologyModalityByUuid(getUuidProperty());
    }
    
    /**
     * @see BaseDelegatingResourceTest#validateDefaultRepresentation()
     */
    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropPresent("display");
        assertPropPresent("aeTitle");
        assertPropPresent("name");
        assertPropPresent("description");
        assertPropPresent("retired");
    }
    
    /**
     * @see BaseDelegatingResourceTest#validateFullRepresentation()
     */
    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropPresent("display");
        assertPropPresent("aeTitle");
        assertPropPresent("name");
        assertPropPresent("description");
        assertPropPresent("retired");
        assertPropPresent("auditInfo");
    }
    
    /**
     * @see RadiologyModalityResource#doGetAll(RequestContext)
     */
    @Test
    public void shouldReturnRadiologyModalitiesIncludingRetiredOnesIfIncludeAllIsTrue() throws Exception {
        
        RadiologyModalityResource radiologyModalityResource = getResource();
        
        RequestContext context = new RequestContext();
        context.setIncludeAll(true);
        List<Object> modalities = radiologyModalityResource.getAll(context)
                .get("results");
        
        assertThat(modalities.size(), is(TOTAL_MODALITIES));
    }
    
    /**
     * @see RadiologyModalityResource#doGetAll(RequestContext)
     */
    @Test
    public void shouldReturnRadiologyModalitiesExcludingRetiredOnesIfIncludeAllIsFalse() throws Exception {
        
        RadiologyModalityResource radiologyModalityResource = getResource();
        
        RequestContext context = new RequestContext();
        List<Object> modalities = (List) radiologyModalityResource.getAll(context)
                .get("results");
        
        assertThat(modalities.size(), is(TOTAL_MODALITIES_NON_RETIRED));
        for (Object modality : modalities) {
            assertThat(PropertyUtils.getProperty(modality, "retired"), is(false));
        }
    }
}
