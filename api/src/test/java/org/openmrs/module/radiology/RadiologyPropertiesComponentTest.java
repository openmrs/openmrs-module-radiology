/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.openmrs.ConceptClass;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.OrderType;
import org.openmrs.VisitType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.VisitService;
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
    
    @Autowired
    private VisitService visitService;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    /**
     * @see RadiologyProperties#getDicomUIDOrgRoot()
     * @verifies return dicom uid org root
     */
    @Test
    public void getDicomUIDOrgRoot_shouldReturnDicomUidOrgRoot() throws Exception {
        
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_DICOM_UID_ORG_ROOT, "1.2.826.0.1.3680043.8.2186"));
        
        assertThat(radiologyProperties.getDicomUIDOrgRoot(), is("1.2.826.0.1.3680043.8.2186"));
    }
    
    /**
     * @see RadiologyProperties#getDicomUIDOrgRoot()
     * @verifies throw illegal state exception if global property for dicom uid org root cannot be found
     */
    @Test
    public void getDicomUIDOrgRoot_shouldThrowIllegalStateExceptionIfGlobalPropertyForDicomUidOrgRootCannotBeFound()
            throws Exception {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_UID_ORG_ROOT);
        
        radiologyProperties.getDicomUIDOrgRoot();
    }
    
    /**
     * @see RadiologyProperties#getDicomWebViewerAddress()
     * @verifies return dicom web viewer address
     */
    @Test
    public void getDicomWebViewerAddress_shouldReturnDicomWebViewerAddress() throws Exception {
        
        administrationService
                .saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_ADDRESS, "localhost"));
        
        assertThat(radiologyProperties.getDicomWebViewerAddress(), is("localhost"));
    }
    
    /**
     * @see RadiologyProperties#getDicomWebViewerAddress()
     * @verifies throw illegal state exception if global property for dicom web viewer address
     *           cannot be found
     */
    @Test
    public void
            getDicomWebViewerAddress_shouldThrowIllegalStateExceptionIfGlobalPropertyForDicomWebViewerAddressCannotBeFound()
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
        
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_BASE_URL, "/weasis-pacs-connector/viewer"));
        
        assertThat(radiologyProperties.getDicomWebViewerBaseUrl(), is("/weasis-pacs-connector/viewer"));
    }
    
    /**
     * @see RadiologyProperties#getDicomWebViewerBaseUrl()
     * @verifies throw illegal state exception if global property for dicom web viewer base url
     *           cannot be found
     */
    @Test
    public void
            getDicomWebViewerBaseUrl_shouldThrowIllegalStateExceptionIfGlobalPropertyForDicomWebViewerBaseUrlCannotBeFound()
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
        
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_LOCAL_SERVER_NAME, "oviyamlocal"));
        
        assertThat(radiologyProperties.getDicomWebViewerLocalServerName(), is("oviyamlocal"));
    }
    
    /**
     * @see RadiologyProperties#getRadiologyCareSetting()
     * @verifies return radiology care setting
     */
    @Test
    public void getRadiologyCareSetting_shouldReturnRadiologyCareSetting() {
        
        String outpatientCareSettingUuidInOpenMrsCore = "6f0c9a92-6f24-11e3-af88-005056821db0";
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_RADIOLOGY_CARE_SETTING, outpatientCareSettingUuidInOpenMrsCore));
        
        assertThat(radiologyProperties.getRadiologyCareSetting()
                .getUuid(),
            is(outpatientCareSettingUuidInOpenMrsCore));
    }
    
    /**
     * @see RadiologyProperties#getRadiologyCareSetting()
     * @verifies throw illegal state exception if global property for radiology care setting cannot
     *           be found
     */
    @Test
    public void
            getRadiologyCareSetting_shouldThrowIllegalStateExceptionIfGlobalPropertyForRadiologyCareSettingCannotBeFound()
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
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_RADIOLOGY_CARE_SETTING, nonExistingCareSettingUuid));
        
        expectedException.expect(IllegalStateException.class);
        expectedException
                .expectMessage("No existing care setting for uuid: " + RadiologyConstants.GP_RADIOLOGY_CARE_SETTING);
        
        radiologyProperties.getRadiologyCareSetting();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyTestOrderType()
     * @verifies return order type for radiology test orders
     */
    @Test
    public void getRadiologyTestOrderType_shouldReturnOrderTypeForRadiologyTestOrders() {
        
        String radiologyTestOrderTypeUuid = "dbdb9a9b-56ea-11e5-a47f-08002719a237";
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_RADIOLOGY_TEST_ORDER_TYPE, radiologyTestOrderTypeUuid));
        
        OrderType radiologyOrderType = new OrderType("Radiology Order", "Order type for radiology exams",
                "org.openmrs.module.radiology.order.RadiologyOrder");
        radiologyOrderType.setUuid(radiologyTestOrderTypeUuid);
        orderService.saveOrderType(radiologyOrderType);
        
        assertThat(radiologyProperties.getRadiologyTestOrderType()
                .getName(),
            is("Radiology Order"));
        assertThat(radiologyProperties.getRadiologyTestOrderType()
                .getUuid(),
            is(radiologyTestOrderTypeUuid));
    }
    
    /**
     * @see RadiologyProperties#getRadiologyTestOrderType()
     * @verifies throw illegal state exception for non existing radiology test order type
     */
    @Test
    public void getRadiologyTestOrderType_shouldThrowIllegalStateExceptionForNonExistingRadiologyTestOrderType() {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_RADIOLOGY_TEST_ORDER_TYPE);
        
        radiologyProperties.getRadiologyTestOrderType();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderEncounterType()
     * @verifies return encounter type for radiology orders
     */
    @Test
    public void getRadiologyOrderEncounterType_shouldReturnEncounterTypeForRadiologyOrders() {
        
        String radiologyEncounterTypeUuid = "19db8c0d-3520-48f2-babd-77f2d450e5c7";
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDER_ENCOUNTER_TYPE, radiologyEncounterTypeUuid));
        
        EncounterType encounterType = new EncounterType("Radiology Order Encounter", "Ordering radiology exams");
        encounterType.setUuid(radiologyEncounterTypeUuid);
        encounterService.saveEncounterType(encounterType);
        
        assertThat(radiologyProperties.getRadiologyOrderEncounterType()
                .getName(),
            is("Radiology Order Encounter"));
        assertThat(radiologyProperties.getRadiologyOrderEncounterType()
                .getUuid(),
            is(radiologyEncounterTypeUuid));
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderEncounterType()
     * @verifies throw illegal state exception for non existing radiology encounter type
     */
    @Test
    public void getRadiologyOrderEncounterType_shouldThrowIllegalStateExceptionForNonExistingRadiologyEncounterType() {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_RADIOLOGY_ORDER_ENCOUNTER_TYPE);
        
        radiologyProperties.getRadiologyOrderEncounterType();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderingProviderEncounterRole()
     * @verifies return encounter role for ordering provider
     */
    @Test
    public void getRadiologyOrderingProviderEncounterRole_shouldReturnEncounterRoleForOrderingProvider() throws Exception {
        
        String radiologyOrderingProviderEncounterRoleUuid = "13fc9b4a-49ed-429c-9dde-ca005b387a3d";
        administrationService
                .saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDERING_PROVIDER_ENCOUNTER_ROLE,
                        radiologyOrderingProviderEncounterRoleUuid));
        
        EncounterRole encounterRole = new EncounterRole();
        encounterRole.setName("Radiology Ordering Provider Encounter Role");
        encounterRole.setUuid(radiologyOrderingProviderEncounterRoleUuid);
        encounterService.saveEncounterRole(encounterRole);
        
        assertThat(radiologyProperties.getRadiologyOrderingProviderEncounterRole()
                .getName(),
            is("Radiology Ordering Provider Encounter Role"));
        assertThat(radiologyProperties.getRadiologyOrderingProviderEncounterRole()
                .getUuid(),
            is(radiologyOrderingProviderEncounterRoleUuid));
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderingProviderEncounterRole()
     * @verifies throw illegal state exception for non existing ordering provider encounter role
     */
    @Test
    public void
            getRadiologyOrderingProviderEncounterRole_shouldThrowIllegalStateExceptionForNonExistingOrderingProviderEncounterRole()
                    throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(
            "Configuration required: " + RadiologyConstants.GP_RADIOLOGY_ORDERING_PROVIDER_ENCOUNTER_ROLE);
        
        radiologyProperties.getRadiologyOrderingProviderEncounterRole();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyVisitType()
     * @verifies return visit type for radiology orders
     */
    @Test
    public void getRadiologyVisitType_shouldReturnVisitTypeForRadiologyOrders() throws Exception {
        
        String radiologyVisitTypeUuid = "fe898a34-1ade-11e1-9c71-00248140a5eb";
        administrationService
                .saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_RADIOLOGY_VISIT_TYPE, radiologyVisitTypeUuid));
        
        VisitType visitType = new VisitType();
        visitType.setName("Radiology Visit");
        visitType.setUuid(radiologyVisitTypeUuid);
        visitService.saveVisitType(visitType);
        
        assertThat(radiologyProperties.getRadiologyVisitType()
                .getName(),
            is("Radiology Visit"));
        assertThat(radiologyProperties.getRadiologyVisitType()
                .getUuid(),
            is(radiologyVisitTypeUuid));
    }
    
    /**
     * @see RadiologyProperties#getRadiologyVisitType()
     * @verifies throw illegal state exception for non existing radiology visit type
     */
    @Test
    public void getRadiologyVisitType_shouldThrowIllegalStateExceptionForNonExistingRadiologyVisitType() throws Exception {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_RADIOLOGY_VISIT_TYPE);
        
        radiologyProperties.getRadiologyVisitType();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyConceptClassNames()
     * @verifies returns comma separated list of ConceptClass names configured via ConceptClass
     *           UUIDs in global property radiologyConceptClasses
     */
    @Test
    public void
            getRadiologyConceptClassNames_shouldReturnsCommaSeparatedListOfConceptClassNamesConfiguredViaConceptClassUUIDsInGlobalPropertyRadiologyConceptClasses()
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
    public void
            getRadiologyConceptClassNames_shouldThrowIllegalStateExceptionIfGlobalPropertyRadiologyConceptClassesIsAnEmptyString()
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
    public void
            getRadiologyConceptClassNames_shouldThrowIllegalStateExceptionIfGlobalPropertyRadiologyConceptClassesIsBadlyFormatted()
                    throws Exception {
        administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES,
            "AAAA-bbbbb-1111-2222/AAAA-BBBB-2222-3333");
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(
            "Property radiology.radiologyConcepts needs to be a comma separated list of concept class UUIDs (allowed characters [a-z][A-Z][0-9][,][-][ ])");
        
        radiologyProperties.getRadiologyConceptClassNames();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyConceptClassNames()
     * @verifies throw illegal state exception if global property radiologyConceptClasses contains a
     *           UUID not found among ConceptClasses
     */
    @Test
    public void
            getRadiologyConceptClassNames_shouldThrowIllegalStateExceptionIfGlobalPropertyRadiologyConceptClassesContainsAUUIDNotFoundAmongConceptClasses()
                    throws Exception {
        
        administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES,
            conceptService.getConceptClassByName("Drug")
                    .getUuid() + "5");
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Property radiology.radiologyConceptClasses contains UUID");
        
        radiologyProperties.getRadiologyConceptClassNames();
    }
}
