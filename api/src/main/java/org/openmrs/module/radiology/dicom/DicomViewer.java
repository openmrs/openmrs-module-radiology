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

import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.Study;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A class that will return an URL to open dicom images of a given study in the configured
 * dicomviewer.
 */
@Component
public class DicomViewer {
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	/**
	 * @should return a url to open dicom images of the given study in the configured dicom viewer
	 *         (no matter if the study is completed or not)
	 * @should throw an IllegalArgumentException given a study with studyInstanceUid null
	 * @should throw an IllegalArgumentException given null
	 */
	public String getDicomViewerUrl(Study study) {
		if (study == null) {
			throw new IllegalArgumentException("study cannot be null");
		} else if (study.getStudyInstanceUid() == null) {
			throw new IllegalArgumentException("studyInstanceUid cannot be null");
		}
		return radiologyProperties.getDicomViewerUrl() + "studyUID=" + study.getStudyInstanceUid();
	}
}
