/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.study;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.dicom.DicomUidGenerator;
import org.openmrs.test.BaseContextMockTest;

/**
 * Tests {@see RadiologyStudyService}.
 */
public class RadiologyStudyServiceImplTest extends BaseContextMockTest {
    
    
    private static final String ORG_ROOT_UID = "2.25";
    
    private static final String DICOM_UID_1 = ORG_ROOT_UID + ".888";
    
    private static final String DICOM_UID_2 = ORG_ROOT_UID + ".900";
    
    @Mock
    private DicomUidGenerator dicomUidGenerator;
    
    @Mock
    private RadiologyProperties radiologyProperties;
    
    @InjectMocks
    private RadiologyStudyServiceImpl radiologyStudyServiceImpl = new RadiologyStudyServiceImpl();
    
    Method setStudyInstanceUidIfBlankMethod;
    
    @Before
    public void setUp() throws Exception {
        setStudyInstanceUidIfBlankMethod = RadiologyStudyServiceImpl.class.getDeclaredMethod("setStudyInstanceUidIfBlank",
            new Class[] { RadiologyStudy.class });
        setStudyInstanceUidIfBlankMethod.setAccessible(true);
        
        when(radiologyProperties.getDicomUIDOrgRoot()).thenReturn(ORG_ROOT_UID);
        when(dicomUidGenerator.getNewDicomUid(ORG_ROOT_UID)).thenReturn(DICOM_UID_1);
    }
    
    /**
     * @see RadiologyStudyServiceImpl#setStudyInstanceUidIfBlank(RadiologyStudy)
     * @verifies set the study instance uid of given radiology study to a valid dicom uid if null
     */
    @Test
    public void setStudyInstanceUidIfBlank_shouldSetTheStudyInstanceUidOfGivenRadiologyStudyToAValidDicomUidIfNull()
            throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        assertNull(radiologyStudy.getStudyInstanceUid());
        
        setStudyInstanceUidIfBlankMethod.invoke(radiologyStudyServiceImpl, new Object[] { radiologyStudy });
        
        assertNotNull(radiologyStudy.getStudyInstanceUid());
        assertThat(radiologyStudy.getStudyInstanceUid(), is(DICOM_UID_1));
    }
    
    /**
     * @see RadiologyStudyServiceImpl#setStudyInstanceUidIfBlank(RadiologyStudy)
     * @verifies set the study instance uid of given radiology study to a valid dicom uid if only containing whitespaces
     */
    @Test
    public void
            setStudyInstanceUidIfBlank_shouldSetTheStudyInstanceUidOfGivenRadiologyStudyToAValidDicomUidIfOnlyContainingWhitespaces()
                    throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setStudyInstanceUid("   ");
        
        setStudyInstanceUidIfBlankMethod.invoke(radiologyStudyServiceImpl, new Object[] { radiologyStudy });
        
        assertNotNull(radiologyStudy.getStudyInstanceUid());
        assertThat(radiologyStudy.getStudyInstanceUid(), is(DICOM_UID_1));
    }
    
    /**
     * @see RadiologyStudyServiceImpl#setStudyInstanceUidIfBlank(RadiologyStudy)
     * @verifies not set the study instance uid of given radiology study if contains non whitespace characters
     */
    @Test
    public void
            setStudyInstanceUidIfBlank_shouldNotSetTheStudyInstanceUidOfGivenRadiologyStudyIfContainsNonWhitespaceCharacters()
                    throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setStudyInstanceUid(DICOM_UID_2);
        
        setStudyInstanceUidIfBlankMethod.invoke(radiologyStudyServiceImpl, new Object[] { radiologyStudy });
        
        assertNotNull(radiologyStudy.getStudyInstanceUid());
        assertThat(radiologyStudy.getStudyInstanceUid(), is(DICOM_UID_2));
    }
}
