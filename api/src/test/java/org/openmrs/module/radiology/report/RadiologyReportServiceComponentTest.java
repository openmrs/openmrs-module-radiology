/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hamcrest.Matchers;
import org.hibernate.cfg.Environment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.ProviderService;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link RadiologyReportService}.
 */
public class RadiologyReportServiceComponentTest extends BaseModuleContextSensitiveTest {
    
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/RadiologyReportServiceComponentTestDataset.xml";
    
    private static final int EXISTING_RADIOLOGY_ORDER_ID = 2001;
    
    private static final int EXISTING_RADIOLOGY_REPORT_ID = 1;
    
    private static final int NON_EXISTING_RADIOLOGY_REPORT_ID = 999999;
    
    private static final String EXISTING_RADIOLOGY_REPORT_UUID = "e699d90d-e230-4762-8747-d2d0059394b0";
    
    private static final String NON_EXISTING_RADIOLOGY_REPORT_UUID = "637d5011-49f5-4ce8-b4ce-47b37ff2cda2";
    
    private static final int DRAFT_RADIOLOGY_REPORT = 1;
    
    private static final int COMPLETED_RADIOLOGY_REPORT = 2;
    
    private static final int VOIDED_RADIOLOGY_REPORT = 3;
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_WITHOUT_RADIOLOGY_REPORT = 2005;
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_AND_DRAFT_RADIOLOGY_REPORT = 2006;
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT = 2007;
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_AND_VOIDED_RADIOLOGY_REPORT = 2008;
    
    private static final String RADIOLOGY_REPORT_UUID_OF_VOIDED = "7b2b9619-a6b2-4fb7-bf6b-fc7917d6dd59";
    
    private static final String PROVIDER_WITH_RADIOLOGY_REPORTS = "c2299800-cca9-11e0-9572-0800200c9a66";
    
    private static final String PROVIDER_WITHOUT_RADIOLOGY_REPORTS = "550e8400-e29b-11d4-a716-446655440000";
    
    @Autowired
    private ProviderService providerService;
    
    @Autowired
    private RadiologyOrderService radiologyOrderService;
    
    @Autowired
    private RadiologyReportService radiologyReportService;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    /**
     * Overriding following method is necessary to enable MVCC which is disabled by default in DB h2
     * used for the component tests. This prevents following exception:
     * org.hibernate.exception.GenericJDBCException: could not load an entity:
     * [org.openmrs.GlobalProperty#order.nextOrderNumberSeed] due to "Timeout trying to lock table "
     * GLOBAL_PROPERTY"; SQL statement:" which occurs in all tests touching methods that call
     * orderService.saveOrder()
     */
    @Override
    public Properties getRuntimeProperties() {
        Properties result = super.getRuntimeProperties();
        String url = result.getProperty(Environment.URL);
        if (url.contains("jdbc:h2:") && !url.contains(";MVCC=TRUE")) {
            result.setProperty(Environment.URL, url + ";MVCC=TRUE");
        }
        return result;
    }
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see RadiologyReportService#createRadiologyReport(RadiologyOrder)
     * @verifies create a radiology order with report status claimed given a completed radiology
     *           order
     */
    @Test
    public void createRadiologyReport_shouldCreateARadiologyOrderWithReportStatusClaimedGivenACompletedRadiologyOrder()
            throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        
        RadiologyReport radiologyReport = radiologyReportService.createRadiologyReport(radiologyOrder);
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getRadiologyOrder(), is(radiologyOrder));
        assertThat(radiologyReport.getStatus(), is(RadiologyReportStatus.DRAFT));
    }
    
    /**
     * @see RadiologyReportService#createRadiologyReport(RadiologyOrder)
     * @verifies throw illegal argument exception given null
     */
    @Test
    public void createRadiologyReport_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.createRadiologyReport(null);
    }
    
    /**
     * @see RadiologyReportService#createRadiologyReport(RadiologyOrder)
     * @verifies throw api exception if given radiology order is not completed
     */
    @Test
    public void createRadiologyReport_shouldThrowAPIExceptionIfGivenRadiologyOrderIsNotCompleted() throws Exception {
        
        RadiologyOrder existingRadiologyOrder =
                radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_WITHOUT_RADIOLOGY_REPORT);
        existingRadiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.create.for.not.completed.order");
        radiologyReportService.createRadiologyReport(existingRadiologyOrder);
    }
    
    /**
     * @see RadiologyReportService#createRadiologyReport(RadiologyOrder)
     * @verifies throw api exception if given order has a claimed radiology report
     */
    @Test
    public void createRadiologyReport_shouldThrowAPIExceptionIfGivenOrderHasAClaimedRadiologyReport() throws Exception {
        
        RadiologyOrder radiologyOrder =
                radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_DRAFT_RADIOLOGY_REPORT);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.create.already.claimed");
        radiologyReportService.createRadiologyReport(radiologyOrder);
    }
    
    /**
     * @see RadiologyReportService#createRadiologyReport(RadiologyOrder)
     * @verifies throw api exception if given order has a completed radiology report
     */
    @Test
    public void createRadiologyReport_shouldThrowAPIExceptionIfGivenOrderHasACompletedRadiologyReport() throws Exception {
        
        RadiologyOrder radiologyOrder =
                radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.create.already.completed");
        radiologyReportService.createRadiologyReport(radiologyOrder);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies save existing radiology report to the database and return it
     */
    @Test
    public void saveRadiologyReportDraft_shouldSaveExistingRadiologyReportTotTheDatabaseAndReturnIt() throws Exception {
        
        RadiologyReport existingRadiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setStatus(RadiologyReportStatus.DRAFT);
        existingRadiologyReport.setBody("test - text");
        
        assertNotNull(radiologyReportService.saveRadiologyReportDraft(existingRadiologyReport));
        assertThat(radiologyReportService.saveRadiologyReportDraft(existingRadiologyReport)
                .getId(),
            is(EXISTING_RADIOLOGY_REPORT_ID));
        assertThat(radiologyReportService.saveRadiologyReportDraft(existingRadiologyReport)
                .getBody(),
            is("test - text"));
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void saveRadiologyReportDraft_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.saveRadiologyReportDraft(null);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies throw illegal argument exception if given radiology report with reportId null
     */
    @Test
    public void saveRadiologyReportDraft_shouldThrowIllegalArgumentExceptionGivenRadiologyReportWithReportIdNull()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setId(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.reportId cannot be null");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies throw api exception if radiology report is completed
     */
    @Test
    public void saveRadiologyReportDraft_shouldThrowAPIExceptionIfRadiologyReportIsCompleted() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(COMPLETED_RADIOLOGY_REPORT);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.saveDraft.already.completed");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies throw api exception if radiology report is voided
     */
    @Test
    public void saveRadiologyReportDraft_shouldThrowAPIExceptionIfRadiologyReportIsVoided() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(VOIDED_RADIOLOGY_REPORT);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.saveDraft.already.voided");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies throw api exception if given radiology reports order has a completed radiology report
     */
    @Test
    public void saveRadiologyReportDraft_shouldThrowAPIExceptionIfGivenRadiologyReportsOrderHasACompletedRadiologyReport()
            throws Exception {
        
        RadiologyOrder radiologyOrder =
                radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT);
        RadiologyReport radiologyReport = new RadiologyReport(radiologyOrder);
        radiologyReport.setId(1000);
        radiologyReport.setBody("fracture somewhere; not done still draft.");
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.saveDraft.already.reported");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#voidRadiologyReport(RadiologyReport, String)
     * @verifies void the given radiology report
     */
    @Test
    public void voidRadiologyReport_shouldVoidTheGivenRadiologyReport() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        assertFalse(radiologyReport.getVoided());
        assertNull(radiologyReport.getDateVoided());
        assertNull(radiologyReport.getVoidedBy());
        assertNull(radiologyReport.getVoidReason());
        
        radiologyReportService.voidRadiologyReport(radiologyReport, "wrong order");
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getVoided(), is(true));
        assertNotNull(radiologyReport.getDateVoided());
        assertNotNull(radiologyReport.getVoidedBy());
        assertNotNull(radiologyReport.getVoidReason());
    }
    
    /**
     * @see RadiologyReportService#voidRadiologyReport(RadiologyReport, String)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void voidRadiologyReport_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.voidRadiologyReport(null, null);
    }
    
    /**
     * @see RadiologyReportService#voidRadiologyReport(RadiologyReport, String)
     * @verifies throw illegal argument exception if given radiology report with reportId null
     */
    @Test
    public void voidRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenRadiologyReportWithReportIdNull()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        radiologyReport.setId(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.reportId cannot be null");
        radiologyReportService.voidRadiologyReport(radiologyReport, null);
    }
    
    /**
     * @see RadiologyReportService#voidRadiologyReport(RadiologyReport, String)
     * @verifies throw illegal argument exception if given void reason is null or contains only whitespaces
     */
    @Test
    public void voidRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenVoidReasonIsNullOrContainsOnlyWhitespaces()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("voidReason cannot be null or empty");
        radiologyReportService.voidRadiologyReport(radiologyReport, null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("voidReason cannot be null or empty");
        radiologyReportService.voidRadiologyReport(radiologyReport, "");
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("voidReason cannot be null or empty");
        radiologyReportService.voidRadiologyReport(radiologyReport, "   ");
    }
    
    /**
     * @see RadiologyReportService#voidRadiologyReport(RadiologyReport, String)
     * @verifies throw api exception if radiology report is completed
     */
    @Test
    public void voidRadiologyReport_shouldThrowAPIExceptionIfRadiologyReportIsCompleted() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(COMPLETED_RADIOLOGY_REPORT);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.void.completed");
        radiologyReportService.voidRadiologyReport(radiologyReport, "some reason");
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies set the report date of the radiology report to the day the radiology report was completed
     */
    @Test
    public void saveRadiologyReport_shouldSetTheReportDateOfTheRadiologyReportToTheDayTheRadiologyReportWasCompleted()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        
        RadiologyReport completedRadiologyReport = radiologyReportService.saveRadiologyReport(radiologyReport);
        
        assertNotNull(completedRadiologyReport);
        assertNotNull(completedRadiologyReport.getDate());
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies set the radiology report status to complete
     */
    @Test
    public void saveRadiologyReport_shouldSetTheRadiologyReportStatusToComplete() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        
        RadiologyReport completedRadiologyReport = radiologyReportService.saveRadiologyReport(radiologyReport);
        
        assertNotNull(completedRadiologyReport);
        assertThat(completedRadiologyReport.getStatus(), is(RadiologyReportStatus.COMPLETED));
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw illegal argument exception if given radiology report is null
     */
    @Test
    public void saveRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenRadiologyReportIsNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.saveRadiologyReport(null);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw illegal argument exception if given radiology report with reportId null
     */
    @Test
    public void saveRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenRadiologyReportWithReportIdNull()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        radiologyReport.setId(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.reportId cannot be null");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw illegal argument exception if given radiology report with status null
     */
    @Test
    public void saveRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenRadiologyReportWithStatusNull()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        radiologyReport.setStatus(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.status cannot be null");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw api exception if radiology report is completed
     */
    @Test
    public void saveRadiologyReport_shouldThrowAPIExceptionIfRadiologyReportIsCompleted() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(COMPLETED_RADIOLOGY_REPORT);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.complete.completed");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw api exception if radiology report is voided
     */
    @Test
    public void saveRadiologyReport_shouldThrowAPIExceptionIfRadiologyReportIsVoided() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(VOIDED_RADIOLOGY_REPORT);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.complete.voided");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw api exception if radiology report is not valid
     */
    @Test
    public void saveRadiologyReport_shouldThrowAPIExceptionIfRadiologyReportIsNotValid() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        radiologyReport.setPrincipalResultsInterpreter(null);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("failed to validate with reason:");
        radiologyReportService.saveRadiologyReport(radiologyReport);
        
        radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        radiologyReport.setBody(null);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("failed to validate with reason:");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReport(Integer)
     * @verifies return radiology report matching given report id
     */
    @Test
    public void getRadiologyReport_shouldReturnRadiologyReportMatchingGivenReportId() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        assertThat(radiologyReport.getId(), is(EXISTING_RADIOLOGY_REPORT_ID));
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReport(Integer)
     * @verifies return null if no match was found
     */
    @Test
    public void getRadiologyStudy_shouldReturnNullIfNoMatchWasFound() throws Exception {
        
        assertNull(radiologyReportService.getRadiologyReport(NON_EXISTING_RADIOLOGY_REPORT_ID));
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReport(Integer)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("reportId cannot be null");
        radiologyReportService.getRadiologyReport(null);
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReportByUuid(String)
     * @verifies return radiology report matching given uuid
     */
    @Test
    public void getRadiologyReportByUuid_shouldReturnRadiologyReportMatchingGivenUuid() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReportByUuid(EXISTING_RADIOLOGY_REPORT_UUID);
        assertThat(radiologyReport.getUuid(), is(EXISTING_RADIOLOGY_REPORT_UUID));
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReportByUuid(String)
     * @verifies return null if no match was found
     */
    @Test
    public void getRadiologyReportByUuid_shouldReturnNullIfNoMatchWasFound() throws Exception {
        
        assertNull(radiologyReportService.getRadiologyReportByUuid(NON_EXISTING_RADIOLOGY_REPORT_UUID));
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReportByUuid(String)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyReportByUuid_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportUuid cannot be null");
        radiologyReportService.getRadiologyReportByUuid(null);
    }
    
    /**
     * @see RadiologyReportService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     * @should return true if given radiology order has a claimed radiology report that is not voided
     */
    @Test
    public void
            hasRadiologyOrderClaimedRadiologyReport_shouldReturnTrueIfGivenRadiologyOrderHasAClaimedRadiologyReportThatIsNotVoided()
                    throws Exception {
        
        assertTrue(radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_DRAFT_RADIOLOGY_REPORT)));
    }
    
    /**
     * @see RadiologyReportService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     * @should return false if given radiology order has no claimed radiology report that is not voided
     */
    @Test
    public void
            hasRadiologyOrderClaimedRadiologyReport_shouldReturnFalseIfGivenRadiologyOrderHasNoClaimedRadiologyReportThatIsNotVoided()
                    throws Exception {
        
        assertFalse(radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_VOIDED_RADIOLOGY_REPORT)));
    }
    
    /**
     * @see RadiologyReportService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void hasRadiologyOrderClaimedRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(null);
    }
    
    /**
     * @see RadiologyReportService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     * @should return true if given radiology order has a completed radiology report
     */
    @Test
    public void hasRadiologyOrderCompletedRadiologyReport_shouldReturnTrueIfTheRadiologyOrderHasACompletedRadiologyReport()
            throws Exception {
        
        boolean hasRadiologyOrderClaimedRadiologyReport = radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT));
        assertTrue(hasRadiologyOrderClaimedRadiologyReport);
    }
    
    /**
     * @see RadiologyReportService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     * @should return false if given radiology order has no completed radiology report
     */
    @Test
    public void hasRadiologyOrderCompletedRadiologyReport_shouldReturnFalseIfTheRadiologyOrderHasNoCompletedRadiologyReport()
            throws Exception {
        
        boolean hasRadiologyOrderCompletedRadiologyReport = radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_VOIDED_RADIOLOGY_REPORT));
        assertFalse(hasRadiologyOrderCompletedRadiologyReport);
    }
    
    /**
     * @see RadiologyReportService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void hasRadiologyOrderCompletedRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(null);
    }
    
    /**
     * @see RadiologyReportService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     * @verifies return a radiology report if given radiology order is associated with a report with status claimed
     */
    @Test
    public void
            getActiveRadiologyReportByRadiologyOrder_shouldReturnARadiologyReportIfGivenRadiologyOrderIsAssociatedWithAReportWithStatusClaimed()
                    throws Exception {
        
        RadiologyReport activeReport = radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_DRAFT_RADIOLOGY_REPORT));
        
        assertNotNull(activeReport);
        assertThat(activeReport.getStatus(), is(RadiologyReportStatus.DRAFT));
    }
    
    /**
     * @see RadiologyReportService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     * @verifies return a radiology report if given radiology order is associated with a report with status completed
     */
    @Test
    public void
            getActiveRadiologyReportByRadiologyOrder_shouldReturnARadiologyReportIfGivenRadiologyOrderIsAssociatedWithAReportWithStatusCompleted()
                    throws Exception {
        
        RadiologyReport activeReport = radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT));
        
        assertNotNull(activeReport);
        assertThat(activeReport.getStatus(), is(RadiologyReportStatus.COMPLETED));
    }
    
    /**
     * @see RadiologyReportService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     * @verifies return null if given radiology order is only associated with a voided report
     */
    @Test
    public void
            getActiveRadiologyReportByRadiologyOrder_shouldReturnNullIfGivenRadiologyOrderIsOnlyAssociatedWithAReportWithStatusDiscontinued()
                    throws Exception {
        
        assertNull(radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_VOIDED_RADIOLOGY_REPORT)));
    }
    
    /**
     * @see RadiologyReportService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getActiveRadiologyReportByRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.getActiveRadiologyReportByRadiologyOrder(null);
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies return all radiology reports (including discontinued) matching the search query if include discontinued is
     *           set
     */
    @Test
    public void
            getRadiologyReports_shouldReturnAllRadiologyReportsIncludingDiscontinuedMatchingTheSearchQueryIfIncludeDiscontinuedIsSet()
                    throws Exception {
        
        Provider principalResultsInterpreter = providerService.getProviderByUuid(PROVIDER_WITH_RADIOLOGY_REPORTS);
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().withPrincipalResultsInterpreter(principalResultsInterpreter)
                        .includeVoided()
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        assertThat(radiologyReports.size(), is(4));
        assertThat(radiologyReports,
            hasItem(Matchers.<RadiologyReport> hasProperty("uuid", is(RADIOLOGY_REPORT_UUID_OF_VOIDED))));
        assertThat(radiologyReports, hasItem(Matchers.<RadiologyReport> hasProperty("voided", is(true))));
        
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies return all radiology reports within given date range if date to and date from are specified
     */
    @Test
    public void getRadiologyReports_shouldReturnAllRadiologyReportsWithinGivenDateRangeIfDateToAndDateFromAreSpecified()
            throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = format.parse("2016-05-28");
        Date toDate = format.parse("2016-07-01");
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().fromDate(fromDate)
                        .toDate(toDate)
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        assertThat(radiologyReports.size(), is(3));
        for (RadiologyReport radiologyReport : radiologyReports) {
            assertTrue(radiologyReport.getDate()
                    .compareTo(fromDate) >= 0);
            assertTrue(radiologyReport.getDate()
                    .compareTo(toDate) <= 0);
            assertThat(radiologyReport.getVoided(), is(not(true)));
        }
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies return all radiology reports with report date after or equal to from date if only date from was specified
     */
    @Test
    public void
            getRadiologyReports_shouldReturnAllRadiologyReportsWithReportDateAfterOrEqualToFromDateIfOnlyDateFromWasSpecified()
                    throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = format.parse("2016-05-29");
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().fromDate(fromDate)
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        assertThat(radiologyReports.size(), is(2));
        for (RadiologyReport radiologyReport : radiologyReports) {
            assertTrue(radiologyReport.getDate()
                    .compareTo(fromDate) >= 0);
            assertThat(radiologyReport.getVoided(), is(not(true)));
        }
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies return all radiology reports with report date before or equal to to date if only date to was specified
     */
    @Test
    public void
            getRadiologyReports_shouldReturnAllRadiologyReportsWithReportDateBeforeOrEqualToToDateIfOnlyDateToWasSpecified()
                    throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date toDate = format.parse("2016-06-30");
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().toDate(toDate)
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        assertThat(radiologyReports.size(), is(2));
        for (RadiologyReport radiologyReport : radiologyReports) {
            assertTrue(radiologyReport.getDate()
                    .compareTo(toDate) <= 0);
            assertThat(radiologyReport.getVoided(), is(not(true)));
        }
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies return empty list if from date after to date
     */
    @Test
    public void getRadiologyReports_shouldReturnEmptyListIfFromDateAfterToDate() throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = format.parse("2016-06-30");
        Date toDate = format.parse("2016-05-29");
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().fromDate(fromDate)
                        .toDate(toDate)
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        assertTrue(radiologyReports.isEmpty());
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies return empty search result if no report is in date range
     */
    @Test
    public void getRadiologyReports_shouldReturnEmptySearchResultIfNoReportIsInDateRange() throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        RadiologyReportSearchCriteria radiologyReportSearchCriteriaDateRange =
                new RadiologyReportSearchCriteria.Builder().fromDate(format.parse("2016-04-25"))
                        .toDate(format.parse("2016-05-27"))
                        .build();
        
        List<RadiologyReport> radiologyReportsWithDateRange =
                radiologyReportService.getRadiologyReports(radiologyReportSearchCriteriaDateRange);
        assertTrue(radiologyReportsWithDateRange.isEmpty());
        
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies return all radiology reports for given principal results interpreter
     */
    @Test
    public void getRadiologyReports_shouldReturnAllRadiologyReportsForGivenPrincipalResultsInterpreter() throws Exception {
        
        Provider principalResultsInterpreter = providerService.getProviderByUuid(PROVIDER_WITH_RADIOLOGY_REPORTS);
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().withPrincipalResultsInterpreter(principalResultsInterpreter)
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        assertThat(radiologyReports.size(), is(3));
        for (RadiologyReport radiologyReport : radiologyReports) {
            assertThat(radiologyReport.getPrincipalResultsInterpreter(), is(principalResultsInterpreter));
            assertThat(radiologyReport.getVoided(), is(not(true)));
        }
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies return empty search result if no report exists for principal results interpreter
     */
    @Test
    public void getRadiologyReports_shouldReturnEmptySearchResultIfNoReportExistsForPrincipalResultsInterpreter()
            throws Exception {
        
        Provider principalResultsInterpreter = providerService.getProviderByUuid(PROVIDER_WITHOUT_RADIOLOGY_REPORTS);
        RadiologyReportSearchCriteria radiologyReportSearchCriteriaWithPrincipalResultsInterpreter =
                new RadiologyReportSearchCriteria.Builder().withPrincipalResultsInterpreter(principalResultsInterpreter)
                        .build();
        
        List<RadiologyReport> radiologyReportsWithPrincipalResultsInterpreter =
                radiologyReportService.getRadiologyReports(radiologyReportSearchCriteriaWithPrincipalResultsInterpreter);
        assertTrue(radiologyReportsWithPrincipalResultsInterpreter.isEmpty());
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies return all radiology reports with given status
     */
    @Test
    public void getRadiologyReports_shouldReturnAllRadiologyReportsWithGivenStatus() throws Exception {
        
        RadiologyReportSearchCriteria radiologyReportSearchCriteriaWithCompletedStatus =
                new RadiologyReportSearchCriteria.Builder().withStatus(RadiologyReportStatus.COMPLETED)
                        .build();
        
        List<RadiologyReport> radiologyReportsWithCompletedStatus =
                radiologyReportService.getRadiologyReports(radiologyReportSearchCriteriaWithCompletedStatus);
        assertThat(radiologyReportsWithCompletedStatus.size(), is(2));
        for (RadiologyReport radiologyReport : radiologyReportsWithCompletedStatus) {
            assertThat(radiologyReport.getStatus(), is(RadiologyReportStatus.COMPLETED));
        }
        
        RadiologyReportSearchCriteria radiologyReportSearchCriteriaWithClaimedStatus =
                new RadiologyReportSearchCriteria.Builder().withStatus(RadiologyReportStatus.DRAFT)
                        .build();
        
        List<RadiologyReport> radiologyReportsWithClaimedStatus =
                radiologyReportService.getRadiologyReports(radiologyReportSearchCriteriaWithClaimedStatus);
        assertThat(radiologyReportsWithClaimedStatus.size(), is(1));
        for (RadiologyReport radiologyReport : radiologyReportsWithClaimedStatus) {
            assertThat(radiologyReport.getStatus(), is(RadiologyReportStatus.DRAFT));
        }
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies return empty search result if no report exists for given status
     */
    @Test
    public void getRadiologyReports_shouldReturnEmptySearchResultIfNoReportExistsForGivenStatus() throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().toDate(format.parse("2016-05-01"))
                        .withStatus(RadiologyReportStatus.DRAFT)
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        assertTrue(radiologyReports.isEmpty());
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyReports_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportSearchCriteria cannot be null");
        radiologyReportService.getRadiologyReports(null);
    }
}
