/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.radiology.report;

import static org.hamcrest.core.Is.is;
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

import org.hibernate.cfg.Environment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Provider;
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
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_WITHOUT_RADIOLOGY_REPORT = 2005;
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT = 2006;
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT = 2007;
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_AND_DISCONTINUED_RADIOLOGY_REPORT = 2008;
    
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
     * @see RadiologyOrderService#createAndClaimRadiologyReport(RadiologyOrder)
     * @verifies create a radiology order with report status claimed given a completed radiology
     *           order
     */
    @Test
    public void
            createAndClaimRadiologyReport_shouldCreateARadiologyOrderWithReportStatusClaimedGivenACompletedRadiologyOrder()
                    throws Exception {
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrder(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        RadiologyReport radiologyReport = radiologyReportService.createAndClaimRadiologyReport(radiologyOrder);
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getReportStatus(), is(RadiologyReportStatus.CLAIMED));
    }
    
    /**
     * @see RadiologyOrderService#createAndClaimRadiologyReport(RadiologyOrder)
     * @verifies throw illegal argument exception given null
     */
    @Test
    public void createAndClaimRadiologyReport_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.createAndClaimRadiologyReport(null);
    }
    
    /**
     * @see RadiologyOrderService#createAndClaimRadiologyReport(RadiologyOrder)
     * @verifies throw illegal argument exception if given radiology order is not completed
     */
    @Test
    public void createAndClaimRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderIsNotCompleted()
            throws Exception {
        
        RadiologyOrder existingRadiologyOrder =
                radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_WITHOUT_RADIOLOGY_REPORT);
        existingRadiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder needs to be completed");
        radiologyReportService.createAndClaimRadiologyReport(existingRadiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#createAndClaimRadiologyReport(RadiologyOrder)
     * @verifies throw unsupported operation exception if given order has a completed radiology report
     */
    @Test
    public void
            createAndClaimRadiologyReport_shouldThrowUnsupportedOperationExceptionIfGivenOrderHasACompletedRadiologyReport()
                    throws Exception {
        RadiologyOrder existingRadiologyOrder =
                radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT);
        RadiologyReport existingRadiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setReportStatus(RadiologyReportStatus.CLAIMED);
        radiologyReportService.saveRadiologyReport(existingRadiologyReport);
        radiologyReportService.completeRadiologyReport(existingRadiologyReport, providerService.getProvider(1));
        expectedException.expect(UnsupportedOperationException.class);
        expectedException
                .expectMessage("cannot create radiologyReport for this radiologyOrder because it is already completed");
        radiologyReportService.createAndClaimRadiologyReport(existingRadiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#createAndClaimRadiologyReport(RadiologyOrder)
     * @verifies throw unsupported operation exception if given order has a claimed radiology report
     */
    @Test
    public void
            createAndClaimRadiologyReport_shouldThrowUnsupportedOperationExceptionIfGivenOrderHasAClaimedRadiologyReport()
                    throws Exception {
        
        RadiologyOrder existingRadiologyOrder =
                radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT);
        expectedException.expect(UnsupportedOperationException.class);
        expectedException
                .expectMessage("cannot create radiologyReport for this radiologyOrder because it is already claimed");
        radiologyReportService.createAndClaimRadiologyReport(existingRadiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#saveRadiologyReport(RadiologyReport)
     * @verifies save radiology report to the database and return it
     */
    @Test
    public void saveRadiologyReport_shouldSaveRadiologyReportTotTheDatabaseAndReturnIt() throws Exception {
        
        RadiologyReport existingRadiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setReportStatus(RadiologyReportStatus.CLAIMED);
        existingRadiologyReport.setReportBody("test - text");
        
        assertNotNull(radiologyReportService.saveRadiologyReport(existingRadiologyReport));
        assertThat(radiologyReportService.saveRadiologyReport(existingRadiologyReport)
                .getId(),
            is(EXISTING_RADIOLOGY_REPORT_ID));
        assertThat(radiologyReportService.saveRadiologyReport(existingRadiologyReport)
                .getReportBody(),
            is("test - text"));
    }
    
    /**
     * @see RadiologyOrderService#saveRadiologyReport(RadiologyReport)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void saveRadiologyReport_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.saveRadiologyReport(null);
    }
    
    /**
     * @see RadiologyOrderService#saveRadiologyReport(RadiologyReport)
     * @verifies throw illegal argument exception if radiology report status is null
     */
    @Test
    public void saveRadiologyReport_shouldThrowIllegalArgumentExceptionIfRadiologyReportStatusIsNull() throws Exception {
        
        RadiologyReport existingRadiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setReportStatus(null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportStatus cannot be null");
        radiologyReportService.saveRadiologyReport(existingRadiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#saveRadiologyReport(RadiologyReport)
     * @verifies throw unsupported operation exception if radiology report is completed
     */
    @Test
    public void saveRadiologyReport_shouldThrowUnsupportedOperationExceptionIfRadiologyReportIsCompleted() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a completed radiologyReport cannot be saved");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#saveRadiologyReport(RadiologyReport)
     * @verifies throw unsupported operation exception if radiology report is discontinued
     */
    @Test
    public void saveRadiologyReport_shouldThrowUnsupportedOperationExceptionIfRadiologyReportIsDiscontinued()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.DISCONTINUED);
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a discontinued radiologyReport cannot be saved");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#unclaimRadiologyReport(RadiologyReport)
     * @verifies set the radiology report status to discontinued
     */
    @Test
    public void unclaimRadiologyReport_shouldSetTheRadiologyReportStatusToDiscontinued() throws Exception {
        
        RadiologyReport existingRadiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        RadiologyReport radiologyReport = radiologyReportService.unclaimRadiologyReport(existingRadiologyReport);
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getId(), is(EXISTING_RADIOLOGY_REPORT_ID));
        assertThat(radiologyReport.getReportStatus(), is(RadiologyReportStatus.DISCONTINUED));
    }
    
    /**
     * @see RadiologyOrderService#unclaimRadiologyReport(RadiologyReport)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void unclaimRadiologyReport_shouldThrowIllegalArgumentExceptionGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.unclaimRadiologyReport(null);
    }
    
    /**
     * @see RadiologyOrderService#unclaimRadiologyReport(RadiologyReport)
     * @verifies throw illegal argument exception if radiology report status is null
     */
    @Test
    public void unclaimRadiologyReport_shouldThrowIllegalArgumentExceptionIfRadiologyReportStatusIsNull() throws Exception {
        
        RadiologyReport existingRadiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setReportStatus(null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportStatus cannot be null");
        radiologyReportService.unclaimRadiologyReport(existingRadiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#unclaimRadiologyReport(RadiologyReport)
     * @verifies throw unsupported operation exception if radiology report is completed
     */
    @Test
    public void unclaimRadiologyReport_shouldThrowUnsupportedOperationExceptionIfRadiologyReportIsCompleted()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a completed radiologyReport cannot be unclaimed");
        radiologyReportService.unclaimRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#unclaimRadiologyReport(RadiologyReport)
     * @verifies throw unsupported operation exception if radiology report is discontinued
     */
    @Test
    public void unclaimRadiologyReport_shouldThrowUnsupportedOperationExceptionIfRadiologyReportIsDiscontinued()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.DISCONTINUED);
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a discontinued radiologyReport cannot be unclaimed");
        radiologyReportService.unclaimRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#completeRadiologyReport(RadiologyReport, Provider)
     * @verifies set the report date of the radiology report to the day the radiology report was completed
     */
    @Test
    public void completeRadiologyReport_shouldSetTheReportDateOfTheRadiologyReportToTheDayTheRadiologyReportWasCompleted()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.completeRadiologyReport(
            radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID),
            radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID)
                    .getPrincipalResultsInterpreter());
        
        assertNotNull(radiologyReport);
        assertNotNull(radiologyReport.getReportDate());
    }
    
    /**
     * @see RadiologyOrderService#completeRadiologyReport(RadiologyReport, Provider)
     * @verifies set the radiology report status to complete
     */
    @Test
    public void completeRadiologyReport_shouldSetTheRadiologyReportStatusToComplete() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.completeRadiologyReport(
            radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID),
            radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID)
                    .getPrincipalResultsInterpreter());
        
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getReportStatus(), is(RadiologyReportStatus.COMPLETED));
    }
    
    /**
     * @see RadiologyOrderService#completeRadiologyReport(RadiologyReport, Provider)
     * @verifies throw illegal argument exception if principal results interpreter is null
     */
    @Test
    public void completeRadiologyReport_shouldThrowIllegalArgumentExceptionIfPrincipalResultsInterpreterIsNull()
            throws Exception {
        
        RadiologyReport existingRadiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.completeRadiologyReport(null, existingRadiologyReport.getPrincipalResultsInterpreter());
    }
    
    /**
     * @see RadiologyOrderService#completeRadiologyReport(RadiologyReport, Provider)
     * @verifies throw illegal argument exception if radiology report is null
     */
    @Test
    public void completeRadiologyReport_shouldThrowIllegalArgumentExceptionIfRadiologyReportIsNull() throws Exception {
        
        RadiologyReport existingRadiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setPrincipalResultsInterpreter(null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("principalResultsInterpreter cannot be null");
        radiologyReportService.completeRadiologyReport(existingRadiologyReport, null);
    }
    
    /**
     * @see RadiologyOrderService#completeRadiologyReport(RadiologyReport, Provider)
     * @verifies throw illegal argument exception if radiology report status is null
     */
    @Test
    public void completeRadiologyReport_shouldThrowIllegalArgumentExceptionIfRadiologyReportStatusIsNull() throws Exception {
        
        RadiologyReport existingRadiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setReportStatus(null);
        Provider provider = new Provider();
        provider.setId(1);
        provider.setName("doctor");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportStatus cannot be null");
        radiologyReportService.completeRadiologyReport(existingRadiologyReport, provider);
    }
    
    /**
     * @see RadiologyOrderService#completeRadiologyReport(RadiologyReport, Provider)
     * @verifies throw unsupported operation exception if radiology report is completed
     */
    @Test
    public void completeRadiologyReport_shouldThrowUnsupportedOperationExceptionIfRadiologyReportIsCompleted()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
        Provider provider = new Provider();
        provider.setId(1);
        provider.setName("doctor");
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a completed radiologyReport cannot be completed");
        radiologyReportService.completeRadiologyReport(radiologyReport, provider);
    }
    
    /**
     * @see RadiologyOrderService#completeRadiologyReport(RadiologyReport, Provider)
     * @verifies throw unsupported operation exception if radiology report is discontinued
     */
    @Test
    public void completeRadiologyReport_shouldThrowUnsupportedOperationExceptionIfRadiologyReportIsDiscontinued()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReport(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.DISCONTINUED);
        Provider provider = new Provider();
        provider.setId(1);
        provider.setName("doctor");
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a discontinued radiologyReport cannot be completed");
        radiologyReportService.completeRadiologyReport(radiologyReport, provider);
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
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder, RadiologyReportStatus)
     * @verifies return list of claimed radiology reports if report status is claimed
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldReturnListOfClaimedRadiologyReportIfRadiologyReportStatusIsClaimed()
                    throws Exception {
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT),
            RadiologyReportStatus.CLAIMED);
        assertNotNull(radiologyReports);
        assertThat(radiologyReports.size(), is(1));
        
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder, RadiologyReportStatus)
     * @verifies return list of completed radiology reports if report status is completed
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldReturnListOfCompletedRadiologyReportIfReportStatusIsCompleted()
                    throws Exception {
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT),
            RadiologyReportStatus.COMPLETED);
        assertNotNull(radiologyReports);
        assertThat(radiologyReports.size(), is(1));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder, RadiologyReportStatus)
     * @verifies return list of discontinued radiology reports if report status is discontinued
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldReturnListOfDiscontinuedRadiologyReportIfReportStatusIsClaimed()
                    throws Exception {
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_DISCONTINUED_RADIOLOGY_REPORT),
            RadiologyReportStatus.DISCONTINUED);
        assertNotNull(radiologyReports);
        assertThat(radiologyReports.size(), is(1));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder, RadiologyReportStatus)
     * @should return empty list given radiology order without associated radiology reports
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldReturnEmptyListGivenRadiologyOrderWithoutAssociatedRadiologyReports()
                    throws Exception {
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_WITHOUT_RADIOLOGY_REPORT),
            RadiologyReportStatus.CLAIMED);
        assertThat(radiologyReports.size(), is(0));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder, RadiologyReportStatus)
     * @should throw illegal argument exception if given radiology order is null
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrderIsNull()
                    throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(null, RadiologyReportStatus.CLAIMED);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder, RadiologyReportStatus)
     * @should throw illegal argument exception if given report status is null
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldThrowIllegalArgumentExceptionIfGivenReportStatusIsNull()
                    throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportStatus cannot be null");
        radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT), null);
    }
    
    /**
     * @see RadiologyOrderService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     * @should return true if given radiology order has a claimed radiology report
     */
    @Test
    public void hasRadiologyOrderClaimedRadiologyReport_shouldReturnTrueIfGivenRadiologyOrderHasAClaimedRadiologyReport()
            throws Exception {
        
        assertTrue(radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT)));
    }
    
    /**
     * @see RadiologyOrderService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     * @should return false if given radiology order has no claimed radiology report
     */
    @Test
    public void hasRadiologyOrderClaimedRadiologyReport_shouldReturnFalseIfGivenRadiologyOrderHasNoClaimedRadiologyReport()
            throws Exception {
        
        assertFalse(radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_DISCONTINUED_RADIOLOGY_REPORT)));
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
     * @see RadiologyOrderService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
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
     * @see RadiologyOrderService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     * @should return false if given radiology order has no completed radiology report
     */
    @Test
    public void hasRadiologyOrderCompletedRadiologyReport_shouldReturnFalseIfTheRadiologyOrderHasNoCompletedRadiologyReport()
            throws Exception {
        
        boolean hasRadiologyOrderCompletedRadiologyReport = radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_DISCONTINUED_RADIOLOGY_REPORT));
        assertFalse(hasRadiologyOrderCompletedRadiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void hasRadiologyOrderCompletedRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(null);
    }
    
    /**
     * @see RadiologyOrderService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     * @verifies return a radiology report if given radiology order is associated with a report with status claimed
     */
    @Test
    public void
            getActiveRadiologyReportByRadiologyOrder_shouldReturnARadiologyReportIfGivenRadiologyOrderIsAssociatedWithAReportWithStatusClaimed()
                    throws Exception {
        
        RadiologyReport activeReport = radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT));
        
        assertNotNull(activeReport);
        assertThat(activeReport.getReportStatus(), is(RadiologyReportStatus.CLAIMED));
    }
    
    /**
     * @see RadiologyOrderService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     * @verifies return a radiology report if given radiology order is associated with a report with status completed
     */
    @Test
    public void
            getActiveRadiologyReportByRadiologyOrder_shouldReturnARadiologyReportIfGivenRadiologyOrderIsAssociatedWithAReportWithStatusCompleted()
                    throws Exception {
        
        RadiologyReport activeReport = radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT));
        
        assertNotNull(activeReport);
        assertThat(activeReport.getReportStatus(), is(RadiologyReportStatus.COMPLETED));
    }
    
    /**
     * @see RadiologyOrderService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     * @verifies return null if given radiology order is only associated with a report with status discontinued
     */
    @Test
    public void
            getActiveRadiologyReportByRadiologyOrder_shouldReturnNullIfGivenRadiologyOrderIsOnlyAssociatedWithAReportWithStatusDiscontinued()
                    throws Exception {
        
        RadiologyReport activeReport = radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrder(RADIOLOGY_ORDER_WITH_STUDY_AND_DISCONTINUED_RADIOLOGY_REPORT));
        
        assertNull(activeReport);
    }
    
    /**
     * @see RadiologyOrderService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
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
     * @verifies return all radiology reports within given date range if date to and date from are specified
     */
    @Test
    public void getRadiologyReports_shouldReturnAllRadiologyReportsWithinGivenDateRangeIfDateToAndDateFromAreSpecified()
            throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = format.parse("2016-05-28");
        Date toDate = format.parse("2016-07-01");
        RadiologyReportSearchCriteria radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().withFromDate(fromDate)
                        .withToDate(toDate)
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        assertThat(radiologyReports.size(), is(3));
        for (RadiologyReport radiologyReport : radiologyReports) {
            assertTrue(radiologyReport.getReportDate()
                    .compareTo(fromDate) >= 0);
            assertTrue(radiologyReport.getReportDate()
                    .compareTo(toDate) <= 0);
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
                new RadiologyReportSearchCriteria.Builder().withFromDate(fromDate)
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        assertThat(radiologyReports.size(), is(2));
        for (RadiologyReport radiologyReport : radiologyReports) {
            assertTrue(radiologyReport.getReportDate()
                    .compareTo(fromDate) >= 0);
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
                new RadiologyReportSearchCriteria.Builder().withToDate(toDate)
                        .build();
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReports(radiologyReportSearchCriteria);
        assertThat(radiologyReports.size(), is(2));
        for (RadiologyReport radiologyReport : radiologyReports) {
            assertTrue(radiologyReport.getReportDate()
                    .compareTo(toDate) <= 0);
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
                new RadiologyReportSearchCriteria.Builder().withFromDate(fromDate)
                        .withToDate(toDate)
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
                new RadiologyReportSearchCriteria.Builder().withFromDate(format.parse("2016-04-25"))
                        .withToDate(format.parse("2016-05-27"))
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
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyReports_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportSearchCriteria cannot be null");
        radiologyReportService.getRadiologyReports(null);
    }
    
}
