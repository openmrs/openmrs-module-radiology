/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.api.APIException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Tests {@link RadiologyModalityService}.
 */
public class RadiologyModalityServiceComponentTest extends BaseModuleContextSensitiveTest {
    
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/RadiologyModalityServiceComponentTestDataset.xml";
    
    private static final int EXISTING_RADIOLOGY_MODALITY_ID = 1;
    
    private static final int NON_EXISTING_RADIOLOGY_MODALITY_ID = 999999;
    
    private static final String EXISTING_RADIOLOGY_MODALITY_UUID = "015f85fc-1316-45a3-848d-69ba192e64c4";
    
    private static final String NON_EXISTING_RADIOLOGY_MODALITY_UUID = "637d5011-49f5-4ce8-b4ce-47b37ff2cda2";
    
    @Autowired
    private RadiologyModalityService radiologyModalityService;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see RadiologyModalityService#saveRadiologyModality(RadiologyModality)
     * @verifies throw illegal argument exception if given radiology modality is null
     */
    @Test
    public void saveRadiologyModality_shouldThrowIllegalArgumentExceptionIfGivenRadiologyModalityIsNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyModality cannot be null");
        radiologyModalityService.saveRadiologyModality(null);
    }
    
    /**
     * @see RadiologyModalityService#saveRadiologyModality(RadiologyModality)
     * @verifies throw api exception if radiology modality is not valid
     */
    @Test
    public void saveRadiologyModality_shouldThrowAPIExceptionIfRadiologyModalityIsNotValid() throws Exception {
        
        RadiologyModality radiologyModality = new RadiologyModality();
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("failed to validate with reason:");
        radiologyModalityService.saveRadiologyModality(radiologyModality);
        
        radiologyModality.setAeTitle("  ");
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("failed to validate with reason:");
        radiologyModalityService.saveRadiologyModality(radiologyModality);
    }
    
    /**
     * @see RadiologyModalityService#getRadiologyModality(Integer)
     * @verifies return radiology modality matching given modality id
     */
    @Test
    public void getRadiologyModality_shouldReturnRadiologyModalityMatchingGivenModalityId() throws Exception {
        
        RadiologyModality radiologyModality = radiologyModalityService.getRadiologyModality(EXISTING_RADIOLOGY_MODALITY_ID);
        assertThat(radiologyModality.getId(), is(EXISTING_RADIOLOGY_MODALITY_ID));
    }
    
    /**
     * @see RadiologyModalityService#getRadiologyModality(Integer)
     * @verifies return null if no match was found
     */
    @Test
    public void getRadiologyStudy_shouldReturnNullIfNoMatchWasFound() throws Exception {
        
        assertNull(radiologyModalityService.getRadiologyModality(NON_EXISTING_RADIOLOGY_MODALITY_ID));
    }
    
    /**
     * @see RadiologyModalityService#getRadiologyModality(Integer)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyModality_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("id cannot be null");
        radiologyModalityService.getRadiologyModality(null);
    }
    
    /**
     * @see RadiologyModalityService#getRadiologyModalityByUuid(String)
     * @verifies return radiology modality matching given uuid
     */
    @Test
    public void getRadiologyModalityByUuid_shouldReturnRadiologyModalityMatchingGivenUuid() throws Exception {
        
        RadiologyModality radiologyModality =
                radiologyModalityService.getRadiologyModalityByUuid(EXISTING_RADIOLOGY_MODALITY_UUID);
        assertThat(radiologyModality.getUuid(), is(EXISTING_RADIOLOGY_MODALITY_UUID));
    }
    
    /**
     * @see RadiologyModalityService#getRadiologyModalityByUuid(String)
     * @verifies return null if no match was found
     */
    @Test
    public void getRadiologyModalityByUuid_shouldReturnNullIfNoMatchWasFound() throws Exception {
        
        assertNull(radiologyModalityService.getRadiologyModalityByUuid(NON_EXISTING_RADIOLOGY_MODALITY_UUID));
    }
    
    /**
     * @see RadiologyModalityService#getRadiologyModalityByUuid(String)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyModalityByUuid_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("uuid cannot be null");
        radiologyModalityService.getRadiologyModalityByUuid(null);
    }
}
