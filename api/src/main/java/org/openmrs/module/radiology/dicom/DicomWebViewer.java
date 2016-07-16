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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.study.RadiologyStudy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * A class that will return an URL to open dicom images of a given study in the configured
 * dicomviewer.
 */
@Component
public class DicomWebViewer {
    
    
    @Autowired
    private RadiologyProperties radiologyProperties;
    
    /**
     * Return URL to open DICOM web viewer for given RadiologyStudy.
     * 
     * @param radiologyStudy RadiologyStudy for which DICOM web viewer URL should be created
     * @throws IllegalArgumentException given null
     * @throws IllegalArgumentException given a study with studyInstanceUid null
     * @should return a url to open dicom images of the given study in the configured dicom viewer
     * @should add query param server name to url if local server name is not blank
     * @should throw an illegal argument exception given null
     * @should throw an illegal argument exception given study with studyInstanceUid null
     */
    public String getDicomViewerUrl(RadiologyStudy radiologyStudy) {
        if (radiologyStudy == null) {
            throw new IllegalArgumentException("study cannot be null");
        } else if (radiologyStudy.getStudyInstanceUid() == null) {
            throw new IllegalArgumentException("studyInstanceUid cannot be null");
        }
        
        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(radiologyProperties.getDicomWebViewerAddress())
                .port(Integer.valueOf(radiologyProperties.getDicomWebViewerPort()))
                .path(radiologyProperties.getDicomWebViewerBaseUrl())
                .queryParam("studyUID", radiologyStudy.getStudyInstanceUid());
        
        final String serverName = radiologyProperties.getDicomWebViewerLocalServerName();
        if (StringUtils.isNotBlank(serverName)) {
            uriComponentsBuilder.queryParam("serverName", serverName);
        }
        
        return uriComponentsBuilder.buildAndExpand()
                .encode()
                .toString();
    }
}
