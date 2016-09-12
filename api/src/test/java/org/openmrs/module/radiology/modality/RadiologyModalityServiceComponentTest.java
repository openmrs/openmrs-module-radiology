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
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    
    private static final int TOTAL_MODALITIES = 4;
    
    private static final int TOTAL_MODALITIES_NON_RETIRED = 3;
    
    @Autowired
    private RadiologyModalityService radiologyModalityService;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @verifies create a new radiology modality
     * @see RadiologyModalityService#saveRadiologyModality(RadiologyModality)
     */
    @Test
    public void saveRadiologyModality_shouldCreateANewRadiologyModality() throws Exception {
        
        RadiologyModality radiologyModality = new RadiologyModality();
        radiologyModality.setAeTitle("US10");
        radiologyModality.setName("Exzelsior YTO234");
        
        radiologyModalityService.saveRadiologyModality(radiologyModality);
        assertNotNull(radiologyModality.getModalityId());
    }
    
    /**
     * @verifies update an existing radiology modality
     * @see RadiologyModalityService#saveRadiologyModality(RadiologyModality)
     */
    @Test
    public void saveRadiologyModality_shouldUpdateAnExistingRadiologyModality() throws Exception {
        
        RadiologyModality radiologyModality = new RadiologyModality();
        radiologyModality.setAeTitle("US10");
        radiologyModality.setName("Exzelsior YTO234");
        radiologyModalityService.saveRadiologyModality(radiologyModality);
        
        assertThat(radiologyModality.getAeTitle(), is("US10"));
        assertNull(radiologyModality.getChangedBy());
        assertNull(radiologyModality.getDateChanged());
        
        Integer idBefore = radiologyModality.getModalityId();
        
        radiologyModality.setAeTitle("US20");
        radiologyModalityService.saveRadiologyModality(radiologyModality);
        
        assertThat(radiologyModality.getModalityId(), is(idBefore));
        assertThat(radiologyModality.getAeTitle(), is("US20"));
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
     * @verifies retire an existing radiology modality
     * @see RadiologyModalityService#retireRadiologyModality(RadiologyModality, String)
     */
    @Test
    public void retireRadiologyModality_shouldRetireAnExistingRadiologyModality() throws Exception {
        
        RadiologyModality radiologyModality =
                radiologyModalityService.getRadiologyModalityByUuid(EXISTING_RADIOLOGY_MODALITY_UUID);
        assertThat(radiologyModality.getRetired(), is(false));
        assertNull(radiologyModality.getRetiredBy());
        assertNull(radiologyModality.getRetireReason());
        assertNull(radiologyModality.getDateRetired());
        
        String reason = "for fun";
        radiologyModalityService.retireRadiologyModality(radiologyModality, reason);
        
        assertThat(radiologyModality.getRetired(), is(true));
        assertThat(radiologyModality.getRetireReason(), is(reason));
        assertThat(radiologyModality.getRetiredBy(), is(Context.getAuthenticatedUser()));
        assertNotNull(radiologyModality.getDateRetired());
    }
    
    /**
     * @verifies throw illegal argument exception if given radiology modality is null
     * @see RadiologyModalityService#retireRadiologyModality(RadiologyModality, String)
     */
    @Test
    public void retireRadiologyModality_shouldThrowIllegalArgumentExceptionIfGivenRadiologyModalityIsNull()
            throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyModality cannot be null");
        radiologyModalityService.retireRadiologyModality(null, "fo fun");
    }
    
    /**
     * @verifies throw illegal argument exception if given reason is null or contains only whitespaces
     * @see RadiologyModalityService#retireRadiologyModality(RadiologyModality, String)
     */
    @Test
    public void retireRadiologyModality_shouldThrowIllegalArgumentExceptionIfGivenReasonIsNullOrContainsOnlyWhitespaces()
            throws Exception {
        
        RadiologyModality radiologyModality =
                radiologyModalityService.getRadiologyModalityByUuid(EXISTING_RADIOLOGY_MODALITY_UUID);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Reason for deletion cannot be empty");
        radiologyModalityService.retireRadiologyModality(radiologyModality, null);
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
    
    /**
     * @verifies return radiology modalities including retired ones if given true
     * @see RadiologyModalityService#getRadiologyModalities(boolean)
     */
    @Test
    public void getRadiologyModalities_shouldReturnRadiologyModalitiesIncludingRetiredOnesIfGivenTrue() throws Exception {
        
        List<RadiologyModality> radiologyModalities = radiologyModalityService.getRadiologyModalities(true);
        assertThat(radiologyModalities.size(), is(TOTAL_MODALITIES));
    }
    
    /**
     * @verifies return radiology modalities excluding retired ones if given false
     * @see RadiologyModalityService#getRadiologyModalities(boolean)
     */
    @Test
    public void getRadiologyModalities_shouldReturnRadiologyModalitiesExcludingRetiredOnesIfGivenFalse() throws Exception {
        
        List<RadiologyModality> radiologyModalities = radiologyModalityService.getRadiologyModalities(false);
        assertThat(radiologyModalities.size(), is(TOTAL_MODALITIES_NON_RETIRED));
    }
    
    /**
     * @verifies return empty list if no match was found
     * @see RadiologyModalityService#getRadiologyModalities(boolean)
     */
    @Test
    public void getRadiologyModalities_shouldReturnEmptyListIfNoMatchWasFound() throws Exception {
        
        getConnection().createStatement()
                .execute("DELETE FROM radiology_modality;");
        getConnection().commit();
        
        List<RadiologyModality> radiologyModalities = radiologyModalityService.getRadiologyModalities(true);
        assertTrue(radiologyModalities.isEmpty());
    }
}
