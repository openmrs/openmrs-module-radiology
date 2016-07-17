/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.dicom;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.study.RadiologyStudy;
import org.openmrs.test.BaseContextMockTest;

/**
 * Tests {@link DicomWebViewer}
 */
public class DicomWebViewerTest extends BaseContextMockTest {
    
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Mock
    private RadiologyProperties radiologyProperties;
    
    @InjectMocks
    private DicomWebViewer dicomviewer = new DicomWebViewer();
    
    @Before
    public void setUp() {
        when(radiologyProperties.getDicomWebViewerAddress()).thenReturn("localhost");
        when(radiologyProperties.getDicomWebViewerPort()).thenReturn("8081");
        when(radiologyProperties.getDicomWebViewerBaseUrl()).thenReturn("/weasis-pacs-connector/viewer");
    }
    
    /**
     * @see DicomWebViewer#getDicomViewerUrl(RadiologyStudy)
     * @verifies return a url to open dicom images of the given study in the configured dicom viewer
     */
    @Test
    public void getDicomViewerUrl_shouldReturnAUrlToOpenDicomImagesOfTheGivenStudyInTheConfiguredDicomViewer() {
        RadiologyStudy radiologyStudy = getMockStudy();
        assertThat(dicomviewer.getDicomViewerUrl(radiologyStudy),
            is("http://localhost:8081/weasis-pacs-connector/viewer?studyUID=" + radiologyStudy.getStudyInstanceUid()));
    }
    
    RadiologyStudy getMockStudy() {
        RadiologyStudy mockStudy = new RadiologyStudy();
        mockStudy.setStudyId(1);
        mockStudy.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1");
        
        return mockStudy;
    }
    
    /**
     * @see DicomWebViewer#getDicomViewerUrl(RadiologyStudy)
     * @verifies add query param server name to url if local server name is not blank
     */
    @Test
    public void getDicomViewerUrl_shouldAddQueryParamServerNameToUrlIfLocalServerNameIsNotBlank() {
        
        when(radiologyProperties.getDicomWebViewerBaseUrl()).thenReturn("/oviyam2/viewer.html");
        when(radiologyProperties.getDicomWebViewerLocalServerName()).thenReturn("oviyamlocal");
        
        RadiologyStudy radiologyStudy = getMockStudy();
        
        assertThat(dicomviewer.getDicomViewerUrl(radiologyStudy), is("http://localhost:8081/oviyam2/viewer.html?studyUID="
                + radiologyStudy.getStudyInstanceUid() + "&serverName=oviyamlocal"));
    }
    
    /**
     * @see DicomWebViewer#getDicomViewerUrl(RadiologyStudy)
     * @verifies throw an illegal argument exception given null
     */
    @Test
    public void getDicomViewerUrl_shouldThrowAnIllegalArgumentExceptionGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(is("study cannot be null"));
        dicomviewer.getDicomViewerUrl(null);
    }
    
    /**
     * @see DicomWebViewer#getDicomViewerUrl(RadiologyStudy)
     * @verifies throw an illegal argument exception given study with studyInstanceUid null
     */
    @Test
    public void getDicomViewerUrl_shouldThrowAnIllegalArgumentExceptionGivenAStudyWithStudyInstanceUidNull()
            throws Exception {
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(is("studyInstanceUid cannot be null"));
        dicomviewer.getDicomViewerUrl(radiologyStudy);
    }
}
