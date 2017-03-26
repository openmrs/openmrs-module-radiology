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
    
    @Test
    public void shouldCreateAReportWithStatusClaimedGivenACompletedOrder() throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        
        RadiologyReport radiologyReport = radiologyReportService.createRadiologyReport(radiologyOrder);
        
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getRadiologyOrder(), is(radiologyOrder));
        assertThat(radiologyReport.getStatus(), is(RadiologyReportStatus.DRAFT));
    }
    
    @Test
    public void shouldSaveAReportDraftGivenAnExistingReport() throws Exception {
        
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
    
    @Test
    public void shouldVoidGivenReport() throws Exception {
        
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
    
    @Test
    public void shouldSetTheReportDateOfTheReportToTheDayTheRadiologyReportWasCompleted() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        
        RadiologyReport completedRadiologyReport = radiologyReportService.saveRadiologyReport(radiologyReport);
        
        assertNotNull(completedRadiologyReport);
        assertNotNull(completedRadiologyReport.getDate());
    }
    
    @Test
    public void shouldSaveGivenDraftReportAndSetItsStatusToComplete() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(DRAFT_RADIOLOGY_REPORT);
        
        RadiologyReport completedRadiologyReport = radiologyReportService.saveRadiologyReport(radiologyReport);
        
        assertNotNull(completedRadiologyReport);
        assertThat(completedRadiologyReport.getStatus(), is(RadiologyReportStatus.COMPLETED));
    }
    
    @Test
    public void shouldFailToSaveTheGivenReportIfItIsNotValid() throws Exception {
        
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
    
    @Test
    public void shouldReturnTheReportMatchingGivenReportId() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        
        assertThat(radiologyReport.getId(), is(EXISTING_RADIOLOGY_REPORT_ID));
    }
    
    @Test
    public void shouldReturnNullIfNoMatchWasFoundForGivenReportId() throws Exception {
        
        assertNull(radiologyReportService.getRadiologyReport(NON_EXISTING_RADIOLOGY_REPORT_ID));
    }
    
    @Test
    public void shouldReturnTheReportMatchingGivenUuid() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReportByUuid(EXISTING_RADIOLOGY_REPORT_UUID);
        
        assertThat(radiologyReport.getUuid(), is(EXISTING_RADIOLOGY_REPORT_UUID));
    }
    
    @Test
    public void shouldReturnNullIfNoMatchWasFoundForGivenUuid() throws Exception {
        
        assertNull(radiologyReportService.getRadiologyReportByUuid(NON_EXISTING_RADIOLOGY_REPORT_UUID));
    }
    
    @Test
    public void
            hasRadiologyOrderClaimedRadiologyReport_shouldReturnTrueIfGivenRadiologyOrderHasAClaimedRadiologyReportThatIsNotVoided()
                    throws Exception {
        
        assertTrue(radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_DRAFT_RADIOLOGY_REPORT)));
    }
    
    @Test
    public void
            hasRadiologyOrderClaimedRadiologyReport_shouldReturnFalseIfGivenRadiologyOrderHasNoClaimedRadiologyReportThatIsNotVoided()
                    throws Exception {
        
        assertFalse(radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_VOIDED_RADIOLOGY_REPORT)));
    }
    
    @Test
    public void shouldReturnTrueIfTheOrderHasACompletedReport() throws Exception {
        
        boolean hasRadiologyOrderClaimedRadiologyReport = radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT));
        
        assertTrue(hasRadiologyOrderClaimedRadiologyReport);
    }
    
    @Test
    public void shouldReturnFalseIfTheOrderHasNoCompletedReport() throws Exception {
        
        boolean hasRadiologyOrderCompletedRadiologyReport = radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_VOIDED_RADIOLOGY_REPORT));
        
        assertFalse(hasRadiologyOrderCompletedRadiologyReport);
    }
    
    @Test
    public void
            getActiveRadiologyReportByRadiologyOrder_shouldReturnAReportIfGivenOrderIsAssociatedWithAReportWithStatusClaimed()
                    throws Exception {
        
        RadiologyReport activeReport = radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_DRAFT_RADIOLOGY_REPORT));
        
        assertNotNull(activeReport);
        assertThat(activeReport.getStatus(), is(RadiologyReportStatus.DRAFT));
    }
    
    @Test
    public void
            getActiveRadiologyReportByRadiologyOrder_shouldReturnAReportIfGivenOrderIsAssociatedWithAReportWithStatusCompleted()
                    throws Exception {
        
        RadiologyReport activeReport = radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT));
        
        assertNotNull(activeReport);
        assertThat(activeReport.getStatus(), is(RadiologyReportStatus.COMPLETED));
    }
    
    @Test
    public void
            getActiveRadiologyReportByRadiologyOrder_shouldReturnNullIfGivenOrderIsOnlyAssociatedWithAReportWithStatusDiscontinued()
                    throws Exception {
        
        assertNull(radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_VOIDED_RADIOLOGY_REPORT)));
    }
    
    @Test
    public void shouldGetAllReportsIncludingDiscontinuedOnesMatchingTheSearchQueryIfIncludeDiscontinuedIsSet()
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
    
    @Test
    public void shouldGetAllReportsWithinGivenDateRangeIfDateToAndDateFromAreSpecified() throws Exception {
        
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
    
    @Test
    public void shouldGetAllReportsWithReportDateAfterOrEqualToFromDateIfOnlyDateFromWasSpecified() throws Exception {
        
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
    
    @Test
    public void shouldGetAllReportsWithReportDateBeforeOrEqualToToDateIfOnlyDateToWasSpecified() throws Exception {
        
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
    
    @Test
    public void shouldNotGetAllReportsButReturnAnEmptyListIfFromDateAfterToDate() throws Exception {
        
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
    
    @Test
    public void shouldNotGetAllReportsButReturnAnEmptyListIfNoReportIsInDateRange() throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        RadiologyReportSearchCriteria radiologyReportSearchCriteriaDateRange =
                new RadiologyReportSearchCriteria.Builder().fromDate(format.parse("2016-04-25"))
                        .toDate(format.parse("2016-05-27"))
                        .build();
        
        List<RadiologyReport> radiologyReportsWithDateRange =
                radiologyReportService.getRadiologyReports(radiologyReportSearchCriteriaDateRange);
        
        assertTrue(radiologyReportsWithDateRange.isEmpty());
    }
    
    @Test
    public void shouldGetAllReportsForGivenPrincipalResultsInterpreter() throws Exception {
        
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
    
    @Test
    public void shouldNotGetAllReportsButReturnAnEmptyListIfNoReportExistsForPrincipalResultsInterpreter() throws Exception {
        
        Provider principalResultsInterpreter = providerService.getProviderByUuid(PROVIDER_WITHOUT_RADIOLOGY_REPORTS);
        RadiologyReportSearchCriteria radiologyReportSearchCriteriaWithPrincipalResultsInterpreter =
                new RadiologyReportSearchCriteria.Builder().withPrincipalResultsInterpreter(principalResultsInterpreter)
                        .build();
        
        List<RadiologyReport> radiologyReportsWithPrincipalResultsInterpreter =
                radiologyReportService.getRadiologyReports(radiologyReportSearchCriteriaWithPrincipalResultsInterpreter);
        
        assertTrue(radiologyReportsWithPrincipalResultsInterpreter.isEmpty());
    }
    
    @Test
    public void shouldGetAllReportsWithGivenStatus() throws Exception {
        
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
    
    @Test
    public void shouldNotGetAllReportsButReturnAnEmptyListIfNoReportExistsForGivenStatus() throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().toDate(format.parse("2016-05-01"))
                        .withStatus(RadiologyReportStatus.DRAFT)
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        
        assertTrue(radiologyReports.isEmpty());
    }
}
