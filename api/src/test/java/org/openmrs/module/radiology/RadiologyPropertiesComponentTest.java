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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
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
import org.openmrs.util.OpenmrsUtil;
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
    
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    
    private Method getGlobalPropertyMethod = null;
    
    @Before
    public void setUp() throws Exception {
        
        getGlobalPropertyMethod = RadiologyProperties.class.getDeclaredMethod("getGlobalProperty",
            new Class[] { String.class, boolean.class });
        getGlobalPropertyMethod.setAccessible(true);
    }
    
    /**
     * @see RadiologyProperties#getDicomUIDOrgRoot()
     */
    @Test
    public void shouldReturnDicomUidOrgRoot() throws Exception {
        
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_DICOM_UID_ORG_ROOT, "1.2.826.0.1.3680043.8.2186"));
        
        assertThat(radiologyProperties.getDicomUIDOrgRoot(), is("1.2.826.0.1.3680043.8.2186"));
    }
    
    /**
     * @see RadiologyProperties#getDicomUIDOrgRoot()
     */
    @Test
    public void shouldFailIfGlobalPropertyForDicomUidOrgRootCannotBeFound() throws Exception {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_UID_ORG_ROOT);
        
        radiologyProperties.getDicomUIDOrgRoot();
    }
    
    /**
     * @see RadiologyProperties#getDicomWebViewerAddress()
     */
    @Test
    public void shouldReturnDicomWebViewerAddress() throws Exception {
        
        administrationService
                .saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_ADDRESS, "localhost"));
        
        assertThat(radiologyProperties.getDicomWebViewerAddress(), is("localhost"));
    }
    
    /**
     * @see RadiologyProperties#getDicomWebViewerAddress()
     */
    @Test
    public void shouldFailIfGlobalPropertyForDicomWebViewerAddressCannotBeFound() throws Exception {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_WEB_VIEWER_ADDRESS);
        
        radiologyProperties.getDicomWebViewerAddress();
    }
    
    /**
     * @see RadiologyProperties#getDicomWebViewerPort()
     */
    @Test
    public void shouldReturnDicomWebViewerPort() throws Exception {
        
        administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_PORT, "8081"));
        
        assertThat(radiologyProperties.getDicomWebViewerPort(), is("8081"));
    }
    
    /**
     * @see RadiologyProperties#getDicomWebViewerPort()
     */
    @Test
    public void shouldFailIfGlobalPropertyForDicomWebViewerPortCannotBeFound() throws Exception {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_WEB_VIEWER_PORT);
        
        radiologyProperties.getDicomWebViewerPort();
    }
    
    /**
     * @see RadiologyProperties#getDicomWebViewerBaseUrl()
     */
    @Test
    public void shouldReturnDicomWebViewerBaseUrl() throws Exception {
        
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_BASE_URL, "/weasis-pacs-connector/viewer"));
        
        assertThat(radiologyProperties.getDicomWebViewerBaseUrl(), is("/weasis-pacs-connector/viewer"));
    }
    
    /**
     * @see RadiologyProperties#getDicomWebViewerBaseUrl()
     */
    @Test
    public void shouldFailIfGlobalPropertyForDicomWebViewerBaseUrlCannotBeFound() throws Exception {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_DICOM_WEB_VIEWER_BASE_URL);
        
        radiologyProperties.getDicomWebViewerBaseUrl();
    }
    
    /**
     * @see RadiologyProperties#getDicomWebViewerLocalServerName()
     */
    @Test
    public void shouldReturnDicomWebViewerLocalServerName() throws Exception {
        
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_DICOM_WEB_VIEWER_LOCAL_SERVER_NAME, "oviyamlocal"));
        
        assertThat(radiologyProperties.getDicomWebViewerLocalServerName(), is("oviyamlocal"));
    }
    
    /**
     * @see RadiologyProperties#getRadiologyCareSetting()
     */
    @Test
    public void shouldReturnRadiologyCareSetting() {
        
        String outpatientCareSettingUuidInOpenMrsCore = "6f0c9a92-6f24-11e3-af88-005056821db0";
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_RADIOLOGY_CARE_SETTING, outpatientCareSettingUuidInOpenMrsCore));
        
        assertThat(radiologyProperties.getRadiologyCareSetting()
                .getUuid(),
            is(outpatientCareSettingUuidInOpenMrsCore));
    }
    
    /**
     * @see RadiologyProperties#getRadiologyCareSetting()
     */
    @Test
    public void shouldFailIfGlobalPropertyForRadiologyCareSettingCannotBeFound() throws Exception {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_RADIOLOGY_CARE_SETTING);
        
        radiologyProperties.getRadiologyCareSetting();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyCareSetting()
     */
    @Test
    public void shouldFailIfRadiologyCareSettingCannotBeFound() {
        
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
     */
    @Test
    public void shouldReturnOrderTypeForRadiologyTestOrders() {
        
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
     */
    @Test
    public void shouldFailForNonExistingRadiologyTestOrderType() {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_RADIOLOGY_TEST_ORDER_TYPE);
        
        radiologyProperties.getRadiologyTestOrderType();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderEncounterType()
     */
    @Test
    public void shouldReturnEncounterTypeForRadiologyOrders() {
        
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
     */
    @Test
    public void shouldFailForNonExistingRadiologyEncounterType() {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_RADIOLOGY_ORDER_ENCOUNTER_TYPE);
        
        radiologyProperties.getRadiologyOrderEncounterType();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderingProviderEncounterRole()
     */
    @Test
    public void shouldReturnEncounterRoleForOrderingProvider() throws Exception {
        
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
     */
    @Test
    public void shouldFailForNonExistingOrderingProviderEncounterRole() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(
            "Configuration required: " + RadiologyConstants.GP_RADIOLOGY_ORDERING_PROVIDER_ENCOUNTER_ROLE);
        
        radiologyProperties.getRadiologyOrderingProviderEncounterRole();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyVisitType()
     */
    @Test
    public void shouldReturnVisitTypeForRadiologyOrders() throws Exception {
        
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
     */
    @Test
    public void shouldFailForNonExistingRadiologyVisitType() throws Exception {
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: " + RadiologyConstants.GP_RADIOLOGY_VISIT_TYPE);
        
        radiologyProperties.getRadiologyVisitType();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyConceptClassNames()
     */
    @Test
    public void
            shouldReturnCommaSeparatedListOfConceptClassNamesConfiguredViaConceptClassUUIDsInGlobalPropertyRadiologyConceptClasses()
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
     */
    @Test
    public void shouldFailIfGlobalPropertyRadiologyConceptClassesIsNull() throws Exception {
        administrationService.setGlobalProperty("radiology.radiologyConceptClasses", null);
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: radiology.radiologyConceptClasses");
        
        radiologyProperties.getRadiologyConceptClassNames();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyConceptClassNames()
     */
    @Test
    public void shouldFailIfGlobalPropertyRadiologyConceptClassesIsAnEmptyString() throws Exception {
        administrationService.setGlobalProperty("radiology.radiologyConceptClasses", "");
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration required: radiology.radiologyConceptClasses");
        
        radiologyProperties.getRadiologyConceptClassNames();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyConceptClassNames()
     */
    @Test
    public void shouldFailIfGlobalPropertyRadiologyConceptClassesIsBadlyFormatted() throws Exception {
        administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES,
            "AAAA-bbbbb-1111-2222/AAAA-BBBB-2222-3333");
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Property " + RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES
                + " needs to be a comma separated list of concept class UUIDs (allowed characters [a-z][A-Z][0-9][,][-][ ])");
        
        radiologyProperties.getRadiologyConceptClassNames();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyConceptClassNames()
     */
    @Test
    public void shouldFailIfGlobalPropertyRadiologyConceptClassesContainsAUUIDNotFoundAmongConceptClasses()
            throws Exception {
        
        administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES,
            conceptService.getConceptClassByName("Drug")
                    .getUuid() + "5");
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Property " + RadiologyConstants.GP_RADIOLOGY_CONCEPT_CLASSES + " contains UUID");
        
        radiologyProperties.getRadiologyConceptClassNames();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderReasonConceptClassNames()
     */
    @Test
    public void shouldFailIfGlobalPropertyRadiologyOrderReasonConceptClassesIsBadlyFormatted() throws Exception {
        
        administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES,
            "AAAA-bbbbb-1111-2222/AAAA-BBBB-2222-3333");
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Property " + RadiologyConstants.GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES
                + " needs to be a comma separated list of concept class UUIDs (allowed characters [a-z][A-Z][0-9][,][-][ ])");
        
        radiologyProperties.getRadiologyOrderReasonConceptClassNames();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderReasonConceptClassNames()
     */
    @Test
    public void shouldFailIfGlobalPropertyRadiologyOrderReasonConceptClassesContainsAUUIDNotFoundAmongConceptClasses()
            throws Exception {
        
        administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES,
            conceptService.getConceptClassByName("Drug")
                    .getUuid() + "5");
        
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(
            "Property " + RadiologyConstants.GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES + " contains UUID");
        
        radiologyProperties.getRadiologyOrderReasonConceptClassNames();
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderReasonConceptClassNames()
     */
    @Test
    public void shouldReturnTheNameOfTheDiagnosisConceptClassIfGlobalPropertyRadiologyOrderReasonConceptClassesIsNull()
            throws Exception {
        
        administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES, null);
        
        ConceptClass diagnosisConceptClass = conceptService.getConceptClassByName("Diagnosis");
        assertThat(radiologyProperties.getRadiologyOrderReasonConceptClassNames(), is(diagnosisConceptClass.getName()));
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderReasonConceptClassNames()
     */
    @Test
    public void
            shouldReturnTheNameOfTheDiagnosisConceptClassIfGlobalPropertyRadiologyOrderReasonConceptClassesIsAnEmptyString()
                    throws Exception {
        
        administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES, "");
        
        ConceptClass diagnosisConceptClass = conceptService.getConceptClassByName("Diagnosis");
        assertThat(radiologyProperties.getRadiologyOrderReasonConceptClassNames(), is(diagnosisConceptClass.getName()));
    }
    
    /**
     * @see RadiologyProperties#getRadiologyOrderReasonConceptClassNames()
     */
    @Test
    public void
            shouldReturnCommaSeparatedListOfConceptClassNamesConfiguredViaConceptClassUUIDsInGlobalPropertyRadiologyOrderReasonConceptClasses()
                    throws Exception {
        
        List<ConceptClass> conceptClasses = new LinkedList<ConceptClass>();
        conceptClasses.add(conceptService.getConceptClassByName("Drug"));
        conceptClasses.add(conceptService.getConceptClassByName("Test"));
        conceptClasses.add(conceptService.getConceptClassByName("Anatomy"));
        conceptClasses.add(conceptService.getConceptClassByName("Diagnosis"));
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
        administrationService.setGlobalProperty(RadiologyConstants.GP_RADIOLOGY_ORDER_REASON_CONCEPT_CLASSES,
            uuidFromConceptClasses);
        assertThat(radiologyProperties.getRadiologyOrderReasonConceptClassNames(), is(expectedNames));
    }
    
    /**
     * @see RadiologyProperties#getGlobalProperty(String)
     */
    @Test
    public void shouldReturnGlobalPropertyGivenValidGlobalPropertyName() throws Exception {
        administrationService.saveGlobalProperty(
            new GlobalProperty(RadiologyConstants.GP_DICOM_UID_ORG_ROOT, "1.2.826.0.1.3680043.8.2186"));
        
        String globalProperty = (String) getGlobalPropertyMethod.invoke(radiologyProperties,
            new Object[] { RadiologyConstants.GP_DICOM_UID_ORG_ROOT, true });
        assertThat(globalProperty, is("1.2.826.0.1.3680043.8.2186"));
    }
    
    /**
     * @see RadiologyProperties#getGlobalProperty(String, boolean)
     */
    @Test
    public void shouldReturnNullGivenNonRequiredAndNonConfiguredGlobalProperty() throws Exception {
        
        String globalProperty = (String) getGlobalPropertyMethod.invoke(radiologyProperties,
            new Object[] { RadiologyConstants.GP_DICOM_UID_ORG_ROOT, false });
        assertThat(globalProperty, is(nullValue()));
    }
    
    /**
     * @see RadiologyProperties#getGlobalProperty(String)
     */
    @Test
    public void shouldFailGivenRequiredNonConfiguredGlobalProperty() throws Exception {
        expectedException.expect(InvocationTargetException.class);
        
        getGlobalPropertyMethod.invoke(radiologyProperties, new Object[] { RadiologyConstants.GP_DICOM_UID_ORG_ROOT, true });
    }
    
    /**
     * @see RadiologyProperties#getReportTemplateHome()
     */
    @Test
    public void shouldCreateADirectoryUnderTheOpenmrsApplicationDataDirectoryIfGPValueIsRelative() throws Exception {
        File openmrsApplicationDataDirectory = temporaryFolder.newFolder("openmrs_home");
        OpenmrsUtil.setApplicationDataDirectory(openmrsApplicationDataDirectory.getAbsolutePath());
        administrationService.setGlobalProperty(RadiologyConstants.GP_MRRT_REPORT_TEMPLATE_DIR, "mrrt_templates");
        File templateHome = radiologyProperties.getReportTemplateHome();
        File templateHomeFromGP =
                new File(administrationService.getGlobalProperty(RadiologyConstants.GP_MRRT_REPORT_TEMPLATE_DIR));
        
        assertNotNull(templateHome);
        assertThat(templateHome.exists(), is(true));
        assertThat(templateHome.getName(), is(templateHomeFromGP.getName()));
        assertThat(templateHome.getParentFile()
                .getName(),
            is(openmrsApplicationDataDirectory.getName()));
    }
    
    /**
     * @see RadiologyProperties#getReportTemplateHome()
     */
    @Test
    public void shouldCreateADirectoryAtGPValueIfItIsAnAbsolutePath() throws Exception {
        File tempFolder = temporaryFolder.newFolder("/mrrt_templates");
        administrationService.setGlobalProperty(RadiologyConstants.GP_MRRT_REPORT_TEMPLATE_DIR,
            tempFolder.getAbsolutePath());
        File templateHome = radiologyProperties.getReportTemplateHome();
        File templateHomeFromGP =
                new File(administrationService.getGlobalProperty(RadiologyConstants.GP_MRRT_REPORT_TEMPLATE_DIR));
        
        assertNotNull(templateHome);
        assertThat(templateHome.exists(), is(true));
        assertThat(templateHome.getName(), is(templateHomeFromGP.getName()));
        assertThat(templateHome.getName(), is(tempFolder.getName()));
        assertThat(templateHome.isAbsolute(), is(true));
    }
    
    /**
     * @see RadiologyProperties#getReportTemplateHome()
     */
    @Test
    public void shouldFailIfGlobalPropertyCannotBeFound() throws Exception {
        expectedException.expect(IllegalStateException.class);
        radiologyProperties.getReportTemplateHome();
    }
}
