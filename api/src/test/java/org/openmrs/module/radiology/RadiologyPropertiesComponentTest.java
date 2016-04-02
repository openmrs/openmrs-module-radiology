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
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.ConceptClass;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.OrderType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
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
	
	@Autowired
	private ConceptService conceptService;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * @see RadiologyProperties#getPacsAddress()
	 * @verifies return pacs address
	 */
	@Test
	public void getPacsAddress_shouldReturnPacsAddress() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_PACS_ADDRESS, "localhost"));
		
		assertThat(radiologyProperties.getPacsAddress(), is("localhost"));
	}
	
	/**
	 * @see RadiologyProperties#getPacsAddress()
	 * @verifies throw illegal state exception if global property for pacs address cannot be found
	 */
	@Test
	public void getPacsAddress_shouldThrowIllegalStateExceptionIfGlobalPropertyForPacsAddressCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_PACS_ADDRESS);
		
		radiologyProperties.getPacsAddress();
	}
	
	/**
	 * @see RadiologyProperties#getPacsDicomMppsPort()
	 * @verifies return pacs dicom mpps port
	 */
	@Test
	public void getPacsDicomMppsPort_shouldReturnPacsDicomMppsPort() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_PACS_DICOM_MPPS_PORT, "11114"));
		
		assertThat(radiologyProperties.getPacsDicomMppsPort(), is("11114"));
	}
	
	/**
	 * @see RadiologyProperties#getPacsDicomMppsPort()
	 * @verifies throw illegal state exception if global property for pacs dicom mpps port cannot be
	 *           found
	 */
	@Test
	public void getPacsDicomMppsPort_shouldThrowIllegalStateExceptionIfGlobalPropertyForPacsDicomMppsPortCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_PACS_DICOM_MPPS_PORT);
		
		radiologyProperties.getPacsDicomMppsPort();
	}
	
	/**
	 * @see RadiologyProperties#getPacsHL7Port()
	 * @verifies return pacs hl7 port
	 */
	@Test
	public void getPacsHL7Port_shouldReturnPacsHl7Port() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_PACS_HL7_PORT, "2575"));
		
		assertThat(radiologyProperties.getPacsHL7Port(), is("2575"));
	}
	
	/**
	 * @see RadiologyProperties#getPacsHL7Port()
	 * @verifies throw illegal state exception if global property for pacs hl7 port cannot be found
	 */
	@Test
	public void getPacsHL7Port_shouldThrowIllegalStateExceptionIfGlobalPropertyForPacsHl7PortCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_PACS_HL7_PORT);
		
		radiologyProperties.getPacsHL7Port();
	}
	
	/**
	 * @see RadiologyProperties#getPacsDicomAeTitle()
	 * @verifies return pacs dicom ae title
	 */
	@Test
	public void getPacsDicomAeTitle_shouldReturnPacsDicomAeTitle() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_PACS_DICOM_AE_TITLE, "DCM4CHEE"));
		
		assertThat(radiologyProperties.getPacsDicomAeTitle(), is("DCM4CHEE"));
	}
	
	/**
	 * @see RadiologyProperties#getPacsDicomAeTitle()
	 * @verifies throw illegal state exception if global property for pacs dicom ae title cannot be
	 *           found
	 */
	@Test
	public void getPacsDicomAeTitle_shouldThrowIllegalStateExceptionIfGlobalPropertyForPacsDicomAeTitleCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_PACS_DICOM_AE_TITLE);
		
		radiologyProperties.getPacsDicomAeTitle();
	}
	
	/**
	 * @see RadiologyProperties#getDicomAeTitle()
	 * @verifies return dicom ae title
	 */
	@Test
	public void getDicomAeTitle_shouldReturnDicomAeTitle() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_AE_TITLE, "RADIOLOGY_MODULE"));
		
		assertThat(radiologyProperties.getDicomAeTitle(), is("RADIOLOGY_MODULE"));
	}
	
	/**
	 * @see RadiologyProperties#getDicomAeTitle()
	 * @verifies throw illegal state exception if global property for dicom ae title cannot be found
	 */
	@Test
	public void getDicomAeTitle_shouldThrowIllegalStateExceptionIfGlobalPropertyForDicomAeTitleCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_AE_TITLE);
		
		radiologyProperties.getDicomAeTitle();
	}
	
	/**
	 * @see RadiologyProperties#getDicomApplicationUID()
	 * @verifies dicom application uid
	 */
	@Test
	public void getDicomApplicationUID_shouldDicomApplicationUid() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_APPLICATION_UID,
				"1.2.826.0.1.3680043.8.2186"));
		
		assertThat(radiologyProperties.getDicomApplicationUID(), is("1.2.826.0.1.3680043.8.2186"));
	}
	
	/**
	 * @see RadiologyProperties#getDicomApplicationUID()
	 * @verifies throw illegal state exception if global property for dicom application uid cannot
	 *           be found
	 */
	@Test
	public void getDicomApplicationUID_shouldThrowIllegalStateExceptionIfGlobalPropertyForDicomApplicationUidCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_APPLICATION_UID);
		
		radiologyProperties.getDicomApplicationUID();
	}
	
	/**
	 * @see RadiologyProperties#getDicomStudyUIDSlug()
	 * @verifies dicom study uid slug
	 */
	@Test
	public void getDicomStudyUIDSlug_shouldDicomStudyUidSlug() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_STUDY_UID_SLUG, "1"));
		
		assertThat(radiologyProperties.getDicomStudyUIDSlug(), is("1"));
	}
	
	/**
	 * @see RadiologyProperties#getDicomStudyUIDSlug()
	 * @verifies throw illegal state exception if global property for dicom study uid slug cannot be
	 *           found
	 */
	@Test
	public void getDicomStudyUIDSlug_shouldThrowIllegalStateExceptionIfGlobalPropertyForDicomStudyUidSlugCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_STUDY_UID_SLUG);
		
		radiologyProperties.getDicomStudyUIDSlug();
	}
	
	/**
	 * @see RadiologyProperties#getDicomSpecificCharacterSet()
	 * @verifies dicom specific character set
	 */
	@Test
	public void getDicomSpecificCharacterSet_shouldDicomSpecificCharacterSet() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_SPECIFIC_CHARCATER_SET,
				"ISO-8859-1"));
		
		assertThat(radiologyProperties.getDicomSpecificCharacterSet(), is("ISO-8859-1"));
	}
	
	/**
	 * @see RadiologyProperties#getDicomSpecificCharacterSet()
	 * @verifies throw illegal state exception if global property for dicom specific character set
	 *           cannot be found
	 */
	@Test
	public void getDicomSpecificCharacterSet_shouldThrowIllegalStateExceptionIfGlobalPropertyForDicomSpecificCharacterSetCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_SPECIFIC_CHARCATER_SET);
		
		radiologyProperties.getDicomSpecificCharacterSet();
	}
	
	/**
	 * @see RadiologyProperties#getStudyPrefix()
	 * @verifies return study prefix consisting of application uid and study uid slug
	 */
	@Test
	public void getStudyPrefix_shouldReturnStudyPrefixConsistingofApplicationUidAndStudyUidSlug() {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_APPLICATION_UID,
				"1.2.826.0.1.3680043.8.2186"));
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_STUDY_UID_SLUG, "1"));
		
		assertThat(radiologyProperties.getStudyPrefix(), is("1.2.826.0.1.3680043.8.2186.1."));
	}
	
	/**
	 * @see RadiologyProperties#getDicomWebViewerAddress()
	 * @verifies return dicom web viewer address
	 */
	@Test
	public void getDicomWebViewerAddress_shouldReturnDicomWebViewerAddress() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_ADDRESS,
				"localhost"));
		
		assertThat(radiologyProperties.getDicomWebViewerAddress(), is("localhost"));
	}
	
	/**
	 * @see RadiologyProperties#getDicomWebViewerAddress()
	 * @verifies throw illegal state exception if global property for dicom web viewer address
	 *           cannot be found
	 */
	@Test
	public void getDicomWebViewerAddress_shouldThrowIllegalStateExceptionIfGlobalPropertyForDicomWebViewerAddressCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_WEB_VIEWER_ADDRESS);
		
		radiologyProperties.getDicomWebViewerAddress();
	}
	
	/**
	 * @see RadiologyProperties#getDicomWebViewerPort()
	 * @verifies return dicom web viewer port
	 */
	@Test
	public void getDicomWebViewerPort_shouldReturnDicomWebViewerPort() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_PORT, "8081"));
		
		assertThat(radiologyProperties.getDicomWebViewerPort(), is("8081"));
	}
	
	/**
	 * @see RadiologyProperties#getDicomWebViewerPort()
	 * @verifies throw illegal state exception if global property for dicom web viewer port cannot
	 *           be found
	 */
	@Test
	public void getDicomWebViewerPort_shouldThrowIllegalStateExceptionIfGlobalPropertyForDicomWebViewerPortCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_WEB_VIEWER_PORT);
		
		radiologyProperties.getDicomWebViewerPort();
	}
	
	/**
	 * @see RadiologyProperties#getDicomWebViewerBaseUrl()
	 * @verifies return dicom web viewer base url
	 */
	@Test
	public void getDicomWebViewerBaseUrl_shouldReturnDicomWebViewerBaseUrl() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_BASE_URL,
				"/weasis-pacs-connector/viewer"));
		
		assertThat(radiologyProperties.getDicomWebViewerBaseUrl(), is("/weasis-pacs-connector/viewer"));
	}
	
	/**
	 * @see RadiologyProperties#getDicomWebViewerBaseUrl()
	 * @verifies throw illegal state exception if global property for dicom web viewer base url
	 *           cannot be found
	 */
	@Test
	public void getDicomWebViewerBaseUrl_shouldThrowIllegalStateExceptionIfGlobalPropertyForDicomWebViewerBaseUrlCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_WEB_VIEWER_BASE_URL);
		
		radiologyProperties.getDicomWebViewerBaseUrl();
	}
	
	/**
	 * @see RadiologyProperties#getDicomWebViewerLocalServerName()
	 * @verifies return dicom web viewer local server name
	 */
	@Test
	public void getDicomWebViewerLocalServerName_shouldReturnDicomWebViewerLocalServerName() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(
				RadiologyConstants.GP_DICOM_WEB_VIEWER_LOCAL_SERVER_NAME, "oviyamlocal"));
		
		assertThat(radiologyProperties.getDicomWebViewerLocalServerName(), is("oviyamlocal"));
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
		
		assertThat(radiologyProperties.getRadiologyCareSetting()
				.getUuid(), is(outpatientCareSettingUuidInOpenMrsCore));
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyCareSetting()
	 * @verifies throw illegal state exception if global property for radiology care setting cannot
	 *           be found
	 */
	@Test
	public void getRadiologyCareSetting_shouldThrowIllegalStateExceptionIfGlobalPropertyForRadiologyCareSettingCannotBeFound()
			throws Exception {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_RADIOLOGY_CARE_SETTING);
		
		radiologyProperties.getRadiologyCareSetting();
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyCareSetting()
	 * @verifies throw illegal state exception if radiology care setting cannot be found
	 */
	@Test
	public void getRadiologyCareSetting_shouldThrowIllegalStateExceptionIfRadiologyCareSettingCannotBeFound() {
		
		String nonExistingCareSettingUuid = "5a1b8b43-6f24-11e3-af99-005056821db0";
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_RADIOLOGY_CARE_SETTING,
				nonExistingCareSettingUuid));
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("No existing care setting for uuid: " + RadiologyConstants.GP_RADIOLOGY_CARE_SETTING);
		
		radiologyProperties.getRadiologyCareSetting();
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
		
		assertThat(radiologyProperties.getRadiologyTestOrderType()
				.getName(), is("Radiology Order"));
		assertThat(radiologyProperties.getRadiologyTestOrderType()
				.getUuid(), is(RadiologyConstants.RADIOLOGY_TEST_ORDER_TYPE_UUID));
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
		
		assertThat(radiologyProperties.getRadiologyEncounterType()
				.getUuid(), is(RadiologyConstants.RADIOLOGY_ENCOUNTER_TYPE_UUID));
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
		
		assertThat(radiologyProperties.getOrderingProviderEncounterRole()
				.getUuid(), is(RadiologyConstants.ORDERING_PROVIDER_ENCOUNTER_ROLE_UUID));
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
	 * @see RadiologyProperties#getRadiologyConceptClassNames()
	 * @verifies returns comma separated list of ConceptClass names configured via ConceptClass
	 *           UUIDs in global property radiologyConceptClasses
	 */
	@Test
	public void getRadiologyConceptClassNames_shouldReturnsCommaSeparatedListOfConceptClassNamesConfiguredViaConceptClassUUIDsInGlobalPropertyRadiologyConceptClasses()
			throws Exception {
		List<ConceptClass> conceptClasses = new LinkedList<ConceptClass>();
		conceptClasses.add(conceptService.getConceptClassByName("Drug"));
		conceptClasses.add(conceptService.getConceptClassByName("Test"));
		conceptClasses.add(conceptService.getConceptClassByName("Anatomy"));
		conceptClasses.add(conceptService.getConceptClassByName("Question"));
		String uuidFromConceptClasses = "";
		String expectedNames = "";
		for (ConceptClass conceptClass : conceptClasses) {
			if (expectedNames.equals("")) {
				uuidFromConceptClasses = conceptClass.getUuid();
				expectedNames = conceptClass.getName();
			}
			uuidFromConceptClasses = uuidFromConceptClasses + "," + conceptClass.getUuid();
			expectedNames = expectedNames + "," + conceptClass.getName();
		}
		administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES, uuidFromConceptClasses);
		assertThat(radiologyProperties.getRadiologyConceptClassNames(), is(expectedNames));
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyConceptClassNames()
	 * @verifies throw illegal state exception if global property radiologyConceptClasses is null
	 */
	@Test
	public void getRadiologyConceptClassNames_shouldThrowIllegalStateExceptionIfGlobalPropertyRadiologyConceptClassesIsNull()
			throws Exception {
		administrationService.setGlobalProperty("radiology.radiologyConceptClasses", null);
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: radiology.radiologyConceptClasses");
		
		radiologyProperties.getRadiologyConceptClassNames();
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyConceptClassNames()
	 * @verifies throw illegal state exception if global property radiologyConceptClasses is an
	 *           empty String
	 */
	@Test
	public void getRadiologyConceptClassNames_shouldThrowIllegalStateExceptionIfGlobalPropertyRadiologyConceptClassesIsAnEmptyString()
			throws Exception {
		administrationService.setGlobalProperty("radiology.radiologyConceptClasses", "");
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Configuration required: radiology.radiologyConceptClasses");
		
		radiologyProperties.getRadiologyConceptClassNames();
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyConceptClassNames()
	 * @verifies throw illegal state exception if global property radiologyConceptClasses is badly
	 *           formatted
	 */
	@Test
	public void getRadiologyConceptClassNames_shouldThrowIllegalStateExceptionIfGlobalPropertyRadiologyConceptClassesIsBadlyFormatted()
			throws Exception {
		administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES,
			"AAAA-bbbbb-1111-2222/AAAA-BBBB-2222-3333");
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Property radiology.radiologyConcepts needs to be a comma separated list of concept class UUIDs (allowed characters [a-z][A-Z][0-9][,][-][ ])");
		
		radiologyProperties.getRadiologyConceptClassNames();
	}
	
	/**
	 * @see RadiologyProperties#getRadiologyConceptClassNames()
	 * @verifies throw illegal state exception if global property radiologyConceptClasses contains a
	 *           UUID not found among ConceptClasses
	 */
	@Test
	public void getRadiologyConceptClassNames_shouldThrowIllegalStateExceptionIfGlobalPropertyRadiologyConceptClassesContainsAUUIDNotFoundAmongConceptClasses()
			throws Exception {
		
		administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES,
			conceptService.getConceptClassByName("Drug")
					.getUuid() + "5");
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Property radiology.radiologyConceptClasses contains UUID");
		
		radiologyProperties.getRadiologyConceptClassNames();
	}
}
