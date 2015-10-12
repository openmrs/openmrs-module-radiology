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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.OrderType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests {@link RadiologyProperties}
 */
public class RadiologyPropertiesTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService administrationService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	@Autowired
	private EncounterService encounterService;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * @see {@link RadiologyProperties#getStudyPrefix()}
	 */
	@Test
	@Verifies(value = "should return study prefix consisting of application uid and study uid slug", method = "getStudyPrefix()")
	public void getStudyPrefix_shouldReturnStudyPrefixConsistingofApplicationUidAndStudyUidSlug() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_APPLICATION_UID,
		        "1.2.826.0.1.3680043.8.2186"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_STUDY_UID_SLUG, "1"));
		
		assertEquals(radiologyProperties.getStudyPrefix(), "1.2.826.0.1.3680043.8.2186.1.");
	}
	
	/**
	 * @see {@link RadiologyProperties#getDicomViewerLocalServerName()}
	 */
	@Test
	@Verifies(value = "should return dicom viewer local server name if defined in global properties", method = "getDicomViewerLocalServerName()")
	public void getDicomViewerLocalServerName_shouldReturnDicomViewerLocalServerNameIfDefinedInGlobalProperties() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME,
		        "oviyamlocal"));
		
		assertEquals(radiologyProperties.getDicomViewerLocalServerName(), "serverName=oviyamlocal&");
	}
	
	/**
	 * @see {@link RadiologyProperties#getDicomViewerLocalServerName()}
	 */
	@Test
	@Verifies(value = "should return empty string if dicom viewer local server name if not defined in global properties", method = "getDicomViewerLocalServerName()")
	public void getDicomViewerLocalServerName_shouldReturnEmptyStringIfDicomViewerLocalServerNameIfNotDefinedInGlobalProperties() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME,
		        null));
		
		assertEquals(radiologyProperties.getDicomViewerLocalServerName(), "");
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
		
		assertEquals(radiologyProperties.getDicomViewerUrl(),
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
		
		assertEquals(radiologyProperties.getDicomViewerUrl(), "http://localhost:8081/weasis-pacs-connector/viewer?");
	}
	
	/**
	 * @see {@link RadiologyProperties#getRadiologyCareSetting()}
	 */
	@Test
	@Verifies(value = "should return radiology care setting", method = "getRadiologyCareSetting()")
	public void getRadiologyCareSetting_shouldReturnRadiologyCareSetting() {
		
		String outpatientCareSettingUuidInOpenMrsCore = "6f0c9a92-6f24-11e3-af88-005056821db0";
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_RADIOLOGY_CARE_SETTING,
		        outpatientCareSettingUuidInOpenMrsCore));
		
		assertThat(radiologyProperties.getRadiologyCareSetting().getUuid(), is(outpatientCareSettingUuidInOpenMrsCore));
	}
	
	/**
	 * @see {@link RadiologyProperties#getRadiologyCareSetting()}
	 */
	@Test
	@Verifies(value = "should throw illegal state exception if radiology care setting cannot be found", method = "getRadiologyCareSetting()")
	public void getRadiologyCareSetting_shouldThrowIllegalStateExceptionIfRadiologyCareSettingCannotBeFound() {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required for property: "
		        + RadiologyConstants.GP_RADIOLOGY_CARE_SETTING);
		assertNull(radiologyProperties.getRadiologyCareSetting());
	}
	
	/**
	 * @see {@link RadiologyProperties#getRadiologyTestOrderType()}
	 */
	@Test
	@Verifies(value = "should return order type for radiology test orders", method = "getRadiologyTestOrderType()")
	public void getRadiologyTestOrderType_shouldReturnOrderTypeForRadiologyTestOrders() {
		OrderType radiologyOrderType = new OrderType("Radiology Order", "Order type for radiology exams",
		        "org.openmrs.module.radiology.RadiologyOrder");
		radiologyOrderType.setUuid(RadiologyConstants.RADIOLOGY_TEST_ORDER_TYPE_UUID);
		orderService.saveOrderType(radiologyOrderType);
		
		assertEquals(radiologyProperties.getRadiologyTestOrderType().getName(), "Radiology Order");
		assertEquals(radiologyProperties.getRadiologyTestOrderType().getUuid(),
		    RadiologyConstants.RADIOLOGY_TEST_ORDER_TYPE_UUID);
	}
	
	/**
	 * @see {@link RadiologyProperties#getRadiologyTestOrderType()}
	 */
	@Test
	@Verifies(value = "should throw illegal state exception for non existing radiology test order type", method = "getRadiologyTestOrderType()")
	public void getRadiologyTestOrderType_shouldThrowIllegalStateExceptionForNonExistingRadiologyTestOrderType() {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("OrderType for radiology orders not in database (not found under uuid="
		        + RadiologyConstants.RADIOLOGY_TEST_ORDER_TYPE_UUID + ").");
		
		radiologyProperties.getRadiologyTestOrderType();
	}
	
	/**
	 * @see {@link RadiologyProperties#getRadiologyEncounterType()}
	 */
	@Test
	@Verifies(value = "should return encounter type for radiology orders", method = "getRadiologyEncounterType()")
	public void getRadiologyEncounterType_shouldReturnEncounterTypeForRadiologyOrders() {
		
		EncounterType encounterType = new EncounterType("Radiology Order", "Ordering radiology exams");
		encounterType.setUuid(RadiologyConstants.RADIOLOGY_ENCOUNTER_TYPE_UUID);
		encounterService.saveEncounterType(encounterType);
		
		assertEquals(radiologyProperties.getRadiologyEncounterType().getUuid(),
		    RadiologyConstants.RADIOLOGY_ENCOUNTER_TYPE_UUID);
	}
	
	/**
	 * @see {@link RadiologyProperties#getRadiologyEncounterType()}
	 */
	@Test
	@Verifies(value = "should throw illegal state exception for non existing radiology encounter type", method = "getRadiologyEncounterType()")
	public void getRadiologyEncounterType_shouldThrowIllegalStateExceptionForNonExistingRadiologyEncounterType() {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("EncounterType for radiology orders not in database (not found under uuid="
		        + RadiologyConstants.RADIOLOGY_ENCOUNTER_TYPE_UUID + ").");
		
		radiologyProperties.getRadiologyEncounterType();
	}
	
	/**
	 * @see {@link RadiologyProperties#getOrderingProviderEncounterRole()}
	 */
	@Test
	@Verifies(value = "should return encounter role for ordering provider", method = "getOrderingProviderEncounterRole()")
	public void getOrderingProviderEncounterRole_shouldReturnEncounterRoleForOrderingProvider() {
		
		EncounterRole encounterRole = new EncounterRole();
		encounterRole.setName("Ordering Provider");
		encounterRole.setUuid(RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID);
		encounterService.saveEncounterRole(encounterRole);
		
		assertEquals(radiologyProperties.getOrderingProviderEncounterRole().getUuid(),
		    RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID);
	}
	
	/**
	 * @see {@link RadiologyProperties#getOrderingProviderEncounterRole()}
	 */
	@Test
	@Verifies(value = "should throw illegal state exception for non existing ordering provider encounter role", method = "getOrderingProviderEncounterRole()")
	public void getOrderingProviderEncounterRole_shouldThrowIllegalStateExceptionForNonExistingOrderingProviderEncounterRole() {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("EncounterRole for ordering provider not in database (not found under uuid="
		        + RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID + ").");
		
		radiologyProperties.getOrderingProviderEncounterRole();
	}
}
