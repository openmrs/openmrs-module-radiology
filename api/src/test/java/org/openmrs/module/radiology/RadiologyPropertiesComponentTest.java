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
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests {@link RadiologyProperties}
 */
public class RadiologyPropertiesComponentTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see RadiologyProperties#getStudyPrefix()
	 * @verifies return study prefix consisting of application uid and study uid slug
	 */
	@Test
	public void getStudyPrefix_shouldReturnStudyPrefixConsistingofApplicationUidAndStudyUidSlug() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_APPLICATION_UID,
		        "1.2.826.0.1.3680043.8.2186"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_STUDY_UID_SLUG, "1"));
		
		assertThat(radiologyProperties.getStudyPrefix(), is("1.2.826.0.1.3680043.8.2186.1."));
	}
	
	/**
	 * @see RadiologyProperties#getDicomViewerLocalServerName()
	 * @verifies return dicom viewer local server name if defined in global properties
	 */
	@Test
	public void getDicomViewerLocalServerName_shouldReturnDicomViewerLocalServerNameIfDefinedInGlobalProperties() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME,
		        "oviyamlocal"));
		
		assertThat(radiologyProperties.getDicomViewerLocalServerName(), is("serverName=oviyamlocal&"));
	}
	
	/**
	 * @see RadiologyProperties#getDicomViewerLocalServerName()
	 * @verifies return empty string if dicom viewer local server name if not defined in global properties
	 */
	@Test
	public void getDicomViewerLocalServerName_shouldReturnEmptyStringIfDicomViewerLocalServerNameIfNotDefinedInGlobalProperties() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME,
		        null));
		
		assertThat(radiologyProperties.getDicomViewerLocalServerName(), is(""));
	}
	
	/**
	 * @see RadiologyProperties#getDicomViewerUrl()
	 * @verifies return dicom viewer url consisting of server adress and server port and dicom viewer url base and dicom viewer local server name for oviyam
	 */
	@Test
	public void getDicomViewerUrl_shouldReturnDicomViewerUrlConsistingOfServerAdressAndServerPortAndDicomViewerUrlBaseAndDicomViewerLocalServerNameForOviyam() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_SERVERS_ADDRESS, "localhost"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_SERVERS_PORT, "8081"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_URL_BASE,
		        "/oviyam2/viewer.html?"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME,
		        "oviyamlocal"));
		
		assertThat(radiologyProperties.getDicomViewerUrl(),
		    is("http://localhost:8081/oviyam2/viewer.html?serverName=oviyamlocal&"));
	}
	
	/**
	 * @see RadiologyProperties#getDicomViewerUrl()
	 * @verifies return dicom viewer url consisting of server adress and server port and dicom viewer url base and dicom viewer local server name for weasis
	 */
	@Test
	public void getDicomViewerUrl_shouldReturnDicomViewerUrlConsistingOfServerAdressAndServerPortAndDicomViewerUrlBaseAndDicomViewerLocalServerNameForWeasis() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_SERVERS_ADDRESS, "localhost"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_SERVERS_PORT, "8081"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_URL_BASE,
		        "/weasis-pacs-connector/viewer?"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_VIEWER_LOCAL_SERVER_NAME,
		        null));
		
		assertThat(radiologyProperties.getDicomViewerUrl(), is("http://localhost:8081/weasis-pacs-connector/viewer?"));
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyCareSetting()
	 * @verifies return radiology care setting
	 */
	@Test
	public void getRadiologyCareSetting_shouldReturnRadiologyCareSetting() {
		
		String outpatientCareSettingUuidInOpenMrsCore = "6f0c9a92-6f24-11e3-af88-005056821db0";
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_RADIOLOGY_CARE_SETTING,
		        outpatientCareSettingUuidInOpenMrsCore));
		
		assertThat(radiologyProperties.getRadiologyCareSetting().getUuid(), is(outpatientCareSettingUuidInOpenMrsCore));
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyCareSetting()
	 * @verifies throw illegal state exception if radiology care setting cannot be found
	 */
	@Test
	public void getRadiologyCareSetting_shouldThrowIllegalStateExceptionIfRadiologyCareSettingCannotBeFound() {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required for property: "
		        + RadiologyConstants.GP_RADIOLOGY_CARE_SETTING);
		assertThat(radiologyProperties.getRadiologyCareSetting(), is(nullValue()));
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyTestOrderType()
	 * @verifies return order type for radiology test orders
	 */
	@Test
	public void getRadiologyTestOrderType_shouldReturnOrderTypeForRadiologyTestOrders() {
		OrderType radiologyOrderType = new OrderType("Radiology Order", "Order type for radiology exams",
		        "org.openmrs.module.radiology.RadiologyOrder");
		radiologyOrderType.setUuid(RadiologyConstants.RADIOLOGY_TEST_ORDER_TYPE_UUID);
		orderService.saveOrderType(radiologyOrderType);
		
		assertThat(radiologyProperties.getRadiologyTestOrderType().getName(), is("Radiology Order"));
		assertThat(radiologyProperties.getRadiologyTestOrderType().getUuid(),
		    is(RadiologyConstants.RADIOLOGY_TEST_ORDER_TYPE_UUID));
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyTestOrderType()
	 * @verifies throw illegal state exception for non existing radiology test order type
	 */
	@Test
	public void getRadiologyTestOrderType_shouldThrowIllegalStateExceptionForNonExistingRadiologyTestOrderType() {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("OrderType for radiology orders not in database (not found under uuid="
		        + RadiologyConstants.RADIOLOGY_TEST_ORDER_TYPE_UUID + ").");
		
		radiologyProperties.getRadiologyTestOrderType();
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyEncounterType()
	 * @verifies return encounter type for radiology orders
	 */
	@Test
	public void getRadiologyEncounterType_shouldReturnEncounterTypeForRadiologyOrders() {
		
		EncounterType encounterType = new EncounterType("Radiology Order", "Ordering radiology exams");
		encounterType.setUuid(RadiologyConstants.RADIOLOGY_ENCOUNTER_TYPE_UUID);
		encounterService.saveEncounterType(encounterType);
		
		assertThat(radiologyProperties.getRadiologyEncounterType().getUuid(),
		    is(RadiologyConstants.RADIOLOGY_ENCOUNTER_TYPE_UUID));
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyEncounterType()
	 * @verifies throw illegal state exception for non existing radiology encounter type
	 */
	@Test
	public void getRadiologyEncounterType_shouldThrowIllegalStateExceptionForNonExistingRadiologyEncounterType() {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("EncounterType for radiology orders not in database (not found under uuid="
		        + RadiologyConstants.RADIOLOGY_ENCOUNTER_TYPE_UUID + ").");
		
		radiologyProperties.getRadiologyEncounterType();
	}
	
	/**
	 * @see RadiologyProperties#getOrderingProviderEncounterRole()
	 * @verifies return encounter role for ordering provider
	 */
	@Test
	public void getOrderingProviderEncounterRole_shouldReturnEncounterRoleForOrderingProvider() {
		
		EncounterRole encounterRole = new EncounterRole();
		encounterRole.setName("Ordering Provider");
		encounterRole.setUuid(RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID);
		encounterService.saveEncounterRole(encounterRole);
		
		assertThat(radiologyProperties.getOrderingProviderEncounterRole().getUuid(),
		    is(RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID));
	}
	
	/**
	 * @see RadiologyProperties#getOrderingProviderEncounterRole()
	 * @verifies throw illegal state exception for non existing ordering provider encounter role
	 */
	@Test
	public void getOrderingProviderEncounterRole_shouldThrowIllegalStateExceptionForNonExistingOrderingProviderEncounterRole() {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("EncounterRole for ordering provider not in database (not found under uuid="
		        + RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID + ").");
		
		radiologyProperties.getOrderingProviderEncounterRole();
	}
	
	/**
	 * @verifies throw illegal state exception if false ConceptClass UUID
	 * @see RadiologyProperties#getRadiologyConceptClassNames()
	 */
	@org.junit.Test
	public void getRadiologyConceptClassNames_shouldThrowIllegalStateExceptionIfFalseConceptClassUUID() throws Exception {
		ConceptClass drug = Context.getConceptService().getConceptClassByName("Drug");
		administrationService.setGlobalProperty("radiology.radiologyConcepts", drug.getUuid() + "test");
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("One of the UUIDs is not correct");
		
		String text = radiologyProperties.getRadiologyConceptClassNames();
	}
	
	/**
	 * @verifies return String with ConceptClass Name Drug
	 * @see RadiologyProperties#getRadiologyConceptClassNames()
	 */
	@org.junit.Test
	public void getRadiologyConceptClassNames_shouldReturnStringWithConceptClassNameDrug() throws Exception {
		ConceptClass drug = Context.getConceptService().getConceptClassByName("Drug");
		administrationService.setGlobalProperty("radiology.radiologyConcepts", drug.getUuid());
		
		String text = radiologyProperties.getRadiologyConceptClassNames();
		
		assertEquals("Drug", text);
	}
	
	/**
	 * @verifies return String with ConceptClass Name Drug and LabSet
	 * @see RadiologyProperties#getRadiologyConceptClassNames()
	 */
	@org.junit.Test
	public void getRadiologyConceptClassNames_shouldReturnStringWithConceptClassNameDrugAndLabSet() throws Exception {
		ConceptClass drug = Context.getConceptService().getConceptClassByName("Drug");
		ConceptClass labset = Context.getConceptService().getConceptClassByName("LabSet");
		administrationService.setGlobalProperty("radiology.radiologyConcepts", drug.getUuid() + "," + labset.getUuid());
		
		String text = radiologyProperties.getRadiologyConceptClassNames();
		
		assertEquals("Drug,LabSet", text);
	}
	
	/**
	 * @verifies throw illegal state exception for a non existent UUID
	 * @see RadiologyProperties#getRadiologyConceptClassNames()
	 */
	@org.junit.Test
	public void getRadiologyConceptClassNames_shouldThrowIllegalStateExceptionForANonExistentUUID() throws Exception {
		administrationService.setGlobalProperty("radiology.radiologyConcepts", "");
		
		expectedException.expect(IllegalStateException.class);
		expectedException
		        .expectMessage("There is no Concept Class defined for the Concept Filter, Setting: radiologyConcepts");
		
		String text = radiologyProperties.getRadiologyConceptClassNames();
		
	}
}
