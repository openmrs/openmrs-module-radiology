/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests {@link RadiologyProperties}
 */
public class RadiologyPropertiesTest extends BaseModuleContextSensitiveTest {
	
	private AdministrationService administrationService = null;
	
	/**
	 * Run this before each unit test in this class. It simply assigns the services used in this
	 * class to private variables.
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		
		if (administrationService == null) {
			administrationService = Context.getAdministrationService();
		}
		
	}
	
	/**
	 * @see {@link RadiologyProperties#getStudyPrefix()}
	 */
	@Test
	@Verifies(value = "should return study prefix consisting of application uid and study uid slug", method = "getStudyPrefix()")
	public void getStudyPrefix_shouldReturnStudyPrefixConsistingofApplicationUidAndStudyUidSlug() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_APPLICATION_UID,
		        "1.2.826.0.1.3680043.8.2186"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_STUDY_UID_SLUG, "1"));
		
		assertEquals(RadiologyProperties.getStudyPrefix(), "1.2.826.0.1.3680043.8.2186.1.");
	}
	
	/**
	 * @see {@link RadiologyProperties#getDicomViewerLocalServerName()}
	 */
	@Test
	@Verifies(value = "should return dicom viewer local server name if defined in global properties", method = "getDicomViewerLocalServerName()")
	public void getDicomViewerLocalServerName_shouldReturnDicomViewerLocalServerNameIfDefinedInGlobalProperties() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME,
		        "oviyamlocal"));
		
		assertEquals(RadiologyProperties.getDicomViewerLocalServerName(), "serverName=oviyamlocal&");
	}
	
	/**
	 * @see {@link RadiologyProperties#getDicomViewerLocalServerName()}
	 */
	@Test
	@Verifies(value = "should return empty string if dicom viewer local server name if not defined in global properties", method = "getDicomViewerLocalServerName()")
	public void getDicomViewerLocalServerName_shouldReturnEmptyStringIfDicomViewerLocalServerNameIfNotDefinedInGlobalProperties() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME,
		        null));
		
		assertEquals(RadiologyProperties.getDicomViewerLocalServerName(), "");
	}
	
	/**
	 * @see {@link RadiologyProperties#getDicomViewerUrl()}
	 */
	@Test
	@Verifies(value = "should return dicom viewer url consisting of server adress and server port and dicom viewer url base and dicom viewer local server name for oviyam", method = "getDicomViewerUrl()")
	public void getDicomViewerUrl_shouldReturnDicomViewerUrlConsistingOfServerAdressAndServerPortAndDicomViewerUrlBaseAndDicomViewerLocalServerNameForOviyam() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_SERVERS_ADDRESS, "localhost"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_SERVERS_PORT, "8081"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_URL_BASE,
		        "/oviyam2/viewer.html?"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME,
		        "oviyamlocal"));
		
		assertEquals(RadiologyProperties.getDicomViewerUrl(),
		    "http://localhost:8081/oviyam2/viewer.html?serverName=oviyamlocal&");
	}
	
	/**
	 * @see {@link RadiologyProperties#getDicomViewerUrl()}
	 */
	@Test
	@Verifies(value = "should return dicom viewer url consisting of server adress and server port and dicom viewer url base and dicom viewer local server name for weasis", method = "getDicomViewerUrl()")
	public void getDicomViewerUrl_shouldReturnDicomViewerUrlConsistingOfServerAdressAndServerPortAndDicomViewerUrlBaseAndDicomViewerLocalServerNameForWeasis() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_SERVERS_ADDRESS, "localhost"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_SERVERS_PORT, "8081"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_URL_BASE,
		        "/weasis-pacs-connector/viewer?"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME,
		        null));
		
		assertEquals(RadiologyProperties.getDicomViewerUrl(), "http://localhost:8081/weasis-pacs-connector/viewer?");
	}
}
