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
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    
    private static final String EXISTING_RADIOLOGY_REPORT_UUID = "e699d90d-e230-4762-8747-d2d0059394b0";
    
    private static final String NON_EXISTING_RADIOLOGY_REPORT_UUID = "637d5011-49f5-4ce8-b4ce-47b37ff2cda2";
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_WITHOUT_RADIOLOGY_REPORT = 2005;
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT = 2006;
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT = 2007;
    
    private static final int RADIOLOGY_ORDER_WITH_STUDY_AND_DISCONTINUED_RADIOLOGY_REPORT = 2008;
    
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
        
        RadiologyOrder radiologyOrder = radiologyOrderService.getRadiologyOrderByOrderId(EXISTING_RADIOLOGY_ORDER_ID);
        radiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        RadiologyReport radiologyReport = radiologyReportService.createAndClaimRadiologyReport(radiologyOrder);
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getReportStatus(), is(RadiologyReportStatus.CLAIMED));
    }
    
    /**
     * @see RadiologyOrderService#createAndClaimRadiologyReport(RadiologyOrder)
     * @verifies throw an illegal argument exception if given radiology order is null
     */
    @Test
    public void createAndClaimRadiologyReport_shouldThrowAnIllegalArgumentExceptionIfGivenRadiologyOrderIsNull()
            throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.createAndClaimRadiologyReport(null);
    }
    
    /**
     * @see RadiologyOrderService#createAndClaimRadiologyReport(RadiologyOrder)
     * @verifies throw an illegal argument exception if given radiology order is not completed
     */
    @Test
    public void createAndClaimRadiologyReport_shouldThrowAnIllegalArgumentExceptionIfGivenRadiologyOrderIsNotCompleted()
            throws Exception {
        
        RadiologyOrder existingRadiologyOrder =
                radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_WITHOUT_RADIOLOGY_REPORT);
        existingRadiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder needs to be completed");
        radiologyReportService.createAndClaimRadiologyReport(existingRadiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#createAndClaimRadiologyReport(RadiologyOrder)
     * @verifies throw an UnsupportedOperationException if given order has a completed
     *           RadiologyReport
     */
    @Test
    public void
            createAndClaimRadiologyReport_shouldThrowAnUnsupportedOperationExceptionIfGivenOrderHasACompletedRadiologyReport()
                    throws Exception {
        RadiologyOrder existingRadiologyOrder =
                radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT);
        RadiologyReport existingRadiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
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
     * @verifies throw an UnsupportedOperationException if given order has a claimed RadiologyReport
     */
    @Test
    public void
            createAndClaimRadiologyReport_shouldThrowAnUnsupportedOperationExceptionIfGivenOrderHasAClaimedRadiologyReport()
                    throws Exception {
        
        RadiologyOrder existingRadiologyOrder =
                radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT);
        expectedException.expect(UnsupportedOperationException.class);
        expectedException
                .expectMessage("cannot create radiologyReport for this radiologyOrder because it is already claimed");
        radiologyReportService.createAndClaimRadiologyReport(existingRadiologyOrder);
    }
    
    /**
     * @see RadiologyOrderService#saveRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport)
     * @verifies save RadiologyReport in database and return it
     */
    @Test
    public void saveRadiologyReport_shouldSaveRadiologyReportInDatabaseAndReturnIt() throws Exception {
        
        RadiologyReport existingRadiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
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
     * @see RadiologyOrderService#saveRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport)
     * @verifies throw an UnsupportedOperationException if radiologyReport is completed
     */
    @Test
    public void saveRadiologyReport_shouldThrowAnUnsupportedOperationExceptionIfRadiologyReportIsCompleted()
            throws Exception {
        
        RadiologyReport radiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a completed radiologyReport cannot be saved");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#saveRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport)
     * @verifies throw an UnsupportedOperationException if radiologyReport is discontinued
     */
    @Test
    public void saveRadiologyReport_shouldThrowAnUnsupportedOperationExceptionIfRadiologyReportIsDiscontinued()
            throws Exception {
        
        RadiologyReport radiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.DISCONTINUED);
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a discontinued radiologyReport cannot be saved");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#saveRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport)
     * @verifies throw an IllegalArgumentException if radiologyReport is null
     */
    @Test
    public void saveRadiologyReport_shouldThrowAnIllegalArgumentExceptionIfRadiologyReportIsNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.saveRadiologyReport(null);
    }
    
    /**
     * @see RadiologyOrderService#saveRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport)
     * @verifies throw an IllegalArgumentException if radiologyReportStatus is null
     */
    @Test
    public void saveRadiologyReport_shouldThrowAnIllegalArgumentExceptionIfRadiologyReportStatusIsNull() throws Exception {
        
        RadiologyReport existingRadiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setReportStatus(null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportStatus cannot be null");
        radiologyReportService.saveRadiologyReport(existingRadiologyReport);
    }
    
    /**
     * @verifies set the radiologyReportStatus of radiologyReport to discontinued
     * @see RadiologyOrderService#unclaimRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport)
     */
    @Test
    public void unclaimRadiologyReport_shouldSetTheRadiologyReportStatusOfRadiologyReportToDiscontinued() throws Exception {
        
        RadiologyReport existingRadiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        RadiologyReport radiologyReport = radiologyReportService.unclaimRadiologyReport(existingRadiologyReport);
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getId(), is(EXISTING_RADIOLOGY_REPORT_ID));
        assertThat(radiologyReport.getReportStatus(), is(RadiologyReportStatus.DISCONTINUED));
    }
    
    /**
     * @verifies throw an IllegalArgumentException if radiologyReport is null
     * @see RadiologyOrderService#unclaimRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport)
     */
    @Test
    public void unclaimRadiologyReport_shouldThrowAnIllegalArgumentExceptionIfRadiologyReportIsNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.unclaimRadiologyReport(null);
    }
    
    /**
     * @verifies throw an IllegalArgumentException if radiologyReportStatus is null
     * @see RadiologyOrderService#unclaimRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport)
     */
    @Test
    public void unclaimRadiologyReport_shouldThrowAnIllegalArgumentExceptionIfRadiologyReportStatusIsNull()
            throws Exception {
        
        RadiologyReport existingRadiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setReportStatus(null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportStatus cannot be null");
        radiologyReportService.unclaimRadiologyReport(existingRadiologyReport);
    }
    
    /**
     * @verifies throw an UnsupportedOperationException if radiologyReport is completed
     * @see RadiologyOrderService#unclaimRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport)
     */
    @Test
    public void unclaimRadiologyReport_shouldThrowAnUnsupportedOperationExceptionIfRadiologyReportIsCompleted()
            throws Exception {
        
        RadiologyReport radiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a completed radiologyReport cannot be unclaimed");
        radiologyReportService.unclaimRadiologyReport(radiologyReport);
    }
    
    /**
     * @verifies throw an UnsupportedOperationException if radiologyReport is discontinued
     * @see RadiologyOrderService#unclaimRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport)
     */
    @Test
    public void unclaimRadiologyReport_shouldThrowAnUnsupportedOperationExceptionIfRadiologyReportIsDiscontinued()
            throws Exception {
        
        RadiologyReport radiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.DISCONTINUED);
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a discontinued radiologyReport cannot be unclaimed");
        radiologyReportService.unclaimRadiologyReport(radiologyReport);
    }
    
    /**
     * @verifies set the completionDate of the radiologyReport to the day the RadiologyReport was
     *           completed
     * @see RadiologyOrderService#completeRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport,
     *      org.openmrs.Provider)
     */
    @Test
    public void completeRadiologyReport_shouldSetTheReportDateOfTheRadiologyReportToTheDayTheRadiologyReportWasCompleted()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.completeRadiologyReport(
            radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID),
            radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID)
                    .getPrincipalResultsInterpreter());
        
        assertNotNull(radiologyReport);
        assertNotNull(radiologyReport.getReportDate());
    }
    
    /**
     * @verifies set the radiologyReportStatus to complete
     * @see RadiologyOrderService#completeRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport,
     *      org.openmrs.Provider)
     */
    @Test
    public void completeRadiologyReport_shouldSetTheRadiologyReportStatusToComplete() throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.completeRadiologyReport(
            radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID),
            radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID)
                    .getPrincipalResultsInterpreter());
        
        assertNotNull(radiologyReport);
        assertThat(radiologyReport.getReportStatus(), is(RadiologyReportStatus.COMPLETED));
    }
    
    /**
     * @verifies throw an IllegalArgumentException if provider is null
     * @see RadiologyOrderService#completeRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport,
     *      org.openmrs.Provider)
     */
    @Test
    public void completeRadiologyReport_shouldThrowAnIllegalArgumentExceptionIfPrincipalResultsInterpreterIsNull()
            throws Exception {
        
        RadiologyReport existingRadiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.completeRadiologyReport(null, existingRadiologyReport.getPrincipalResultsInterpreter());
    }
    
    /**
     * @verifies throw an IllegalArgumentException if radiologyReport is null
     * @see RadiologyOrderService#completeRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport,
     *      org.openmrs.Provider)
     */
    @Test
    public void completeRadiologyReport_shouldThrowAnIllegalArgumentExceptionIfRadiologyReportIsNull() throws Exception {
        
        RadiologyReport existingRadiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setPrincipalResultsInterpreter(null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("principalResultsInterpreter cannot be null");
        radiologyReportService.completeRadiologyReport(existingRadiologyReport, null);
    }
    
    /**
     * @verifies throw an IllegalArgumentException if radiologyReportStatus is null
     * @see RadiologyOrderService#completeRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport,
     *      org.openmrs.Provider)
     */
    @Test
    public void completeRadiologyReport_shouldThrowAnIllegalArgumentExceptionIfRadiologyReportStatusIsNull()
            throws Exception {
        
        RadiologyReport existingRadiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        existingRadiologyReport.setReportStatus(null);
        Provider provider = new Provider();
        provider.setId(1);
        provider.setName("doctor");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportStatus cannot be null");
        radiologyReportService.completeRadiologyReport(existingRadiologyReport, provider);
    }
    
    /**
     * @verifies throw an UnsupportedOperationException if radiologyReport is completed
     * @see RadiologyOrderService#completeRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport,
     *      org.openmrs.Provider)
     */
    @Test
    public void completeRadiologyReport_shouldThrowAnUnsupportedOperationExceptionIfRadiologyReportIsCompleted()
            throws Exception {
        
        RadiologyReport radiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
        Provider provider = new Provider();
        provider.setId(1);
        provider.setName("doctor");
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a completed radiologyReport cannot be completed");
        radiologyReportService.completeRadiologyReport(radiologyReport, provider);
    }
    
    /**
     * @verifies throw an UnsupportedOperationException if radiologyReport is discontinued
     * @see RadiologyOrderService#completeRadiologyReport(org.openmrs.module.radiology.report.RadiologyReport,
     *      org.openmrs.Provider)
     */
    @Test
    public void completeRadiologyReport_shouldThrowAnUnsupportedOperationExceptionIfRadiologyReportIsDiscontinued()
            throws Exception {
        
        RadiologyReport radiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        radiologyReport.setReportStatus(RadiologyReportStatus.DISCONTINUED);
        Provider provider = new Provider();
        provider.setId(1);
        provider.setName("doctor");
        
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("a discontinued radiologyReport cannot be completed");
        radiologyReportService.completeRadiologyReport(radiologyReport, provider);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportByRadiologyReportId(Integer)
     * @verifies fetch RadiologyReport matching given radiologyReportId
     */
    @Test
    public void getRadiologyReportByRadiologyReportId_shouldFetchRadiologyReportMatchingGivenRadiologyReportId()
            throws Exception {
        
        RadiologyReport radiologyReport =
                radiologyReportService.getRadiologyReportByRadiologyReportId(EXISTING_RADIOLOGY_REPORT_ID);
        assertThat(radiologyReport.getId(), is(EXISTING_RADIOLOGY_REPORT_ID));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportByRadiologyReportId(Integer)
     * @verifies throw IllegalArgumentException if radiologyReportId is null
     */
    @Test
    public void getRadiologyReportByRadiologyReportId_shouldThrowIllegalArgumentExceptionIfRadiologyReportIdIsNull()
            throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportId cannot be null");
        radiologyReportService.getRadiologyReportByRadiologyReportId(null);
    }
    
    /**
    * @see RadiologyReportService#getRadiologyReportByUuid(String)
    * @verifies fetch RadiologyReport matching given radiologyReportUuid
    */
    @Test
    public void getRadiologyReportByUuid_shouldFetchRadiologyReportMatchingGivenRadiologyReportUuid()
            throws Exception {
        
        RadiologyReport radiologyReport = radiologyReportService.getRadiologyReportByUuid(EXISTING_RADIOLOGY_REPORT_UUID);
        assertThat(radiologyReport.getUuid(), is(EXISTING_RADIOLOGY_REPORT_UUID));
    }
    
    /**
    * @see RadiologyReportService#getRadiologyReportByUuid(String)
    * @verifies throw IllegalArgumentException if radiologyReportUuid is null
    */
    @Test
    public void getRadiologyReportByUuid_shouldThrowIllegalArgumentExceptionIfRadiologyReportUuidIsNull()
            throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportUuid cannot be null");
        radiologyReportService.getRadiologyReportByUuid(null);
    }
    
    /**
    * @see RadiologyReportService#getRadiologyReportByUuid(String)
    * @verifies return null if no radiologyReport found with given uuid
    */
    @Test
    public void getRadiologyReportByUuid_shouldReturnNullIfNoRadiologyReportFoundWithGivenUuid()
            throws Exception {
        
        RadiologyReport radiologyReport =
                radiologyReportService.getRadiologyReportByUuid(NON_EXISTING_RADIOLOGY_REPORT_UUID);
        assertThat(radiologyReport, is(nullValue()));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder,
     *      org.openmrs.module.radiology.report.RadiologyReportStatus)
     * @verifies return a list of claimed RadiologyReport if radiologyReportStatus is claimed
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldReturnAListOfClaimedRadiologyReportIfRadiologyReportStatusIsClaimed()
                    throws Exception {
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT),
            RadiologyReportStatus.CLAIMED);
        assertNotNull(radiologyReports);
        assertThat(radiologyReports.size(), is(1));
        
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder,
     *      org.openmrs.module.radiology.report.RadiologyReportStatus)
     * @verifies return a list of completed RadiologyReport if radiologyReportStatus is completed
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldReturnAListOfCompletedRadiologyReportIfReportStatusIsCompleted()
                    throws Exception {
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT),
            RadiologyReportStatus.COMPLETED);
        assertNotNull(radiologyReports);
        assertThat(radiologyReports.size(), is(1));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder,
     *      org.openmrs.module.radiology.report.RadiologyReportStatus)
     * @verifies return a list of discontinued RadiologyReport if radiologyReportStatus is claimed
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldReturnAListOfDiscontinuedRadiologyReportIfReportStatusIsClaimed()
                    throws Exception {
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_DISCONTINUED_RADIOLOGY_REPORT),
            RadiologyReportStatus.DISCONTINUED);
        assertNotNull(radiologyReports);
        assertThat(radiologyReports.size(), is(1));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder,
     *      org.openmrs.module.radiology.report.RadiologyReportStatus)
     * @verifies return null if there are no RadiologyReports for this radiologyOrder
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldReturnNullIfThereAreNoRadiologyReportsForThisRadiologyOrder()
                    throws Exception {
        
        List<RadiologyReport> radiologyReports = radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_WITHOUT_RADIOLOGY_REPORT),
            RadiologyReportStatus.CLAIMED);
        assertThat(radiologyReports.size(), is(0));
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder,
     *      org.openmrs.module.radiology.report.RadiologyReportStatus)
     * @verifies throw an IllegalArgumentException if given radiologyOrder is null
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldThrowAnIllegalArgumentExceptionIfGivenRadiologyOrderIsNull()
                    throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(null, RadiologyReportStatus.CLAIMED);
    }
    
    /**
     * @see RadiologyOrderService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder,
     *      org.openmrs.module.radiology.report.RadiologyReportStatus)
     * @verifies throw an IllegalArgumentException if given radiologyReportStatus is null
     */
    @Test
    public void
            getRadiologyReportsByRadiologyOrderAndReportStatus_shouldThrowAnIllegalArgumentExceptionIfGivenReportStatusIsNull()
                    throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportStatus cannot be null");
        radiologyReportService.getRadiologyReportsByRadiologyOrderAndReportStatus(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT), null);
    }
    
    /**
     * @see RadiologyOrderService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     * @verifies return false if the RadiologyOrder has no claimed RadiologyReport
     */
    @Test
    public void hasRadiologyOrderClaimedRadiologyReport_shouldReturnFalseIfTheRadiologyOrderHasNoClaimedRadiologyReport()
            throws Exception {
        
        boolean hasRadiologyOrderClaimedRadiologyReport = radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_DISCONTINUED_RADIOLOGY_REPORT));
        assertFalse(hasRadiologyOrderClaimedRadiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     * @verifies return true if the RadiologyOrder has a claimed RadiologyReport
     */
    @Test
    public void hasRadiologyOrderClaimedRadiologyReport_shouldReturnTrueIfTheRadiologyOrderHasAClaimedRadiologyReport()
            throws Exception {
        
        boolean hasRadiologyOrderClaimedRadiologyReport = radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT));
        assertTrue(hasRadiologyOrderClaimedRadiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     * @verifies return false if radiologyOrder is null
     */
    @Test
    public void hasRadiologyOrderClaimedRadiologyReport_shouldReturnFalseIfTheRadiologyOrderIsNull() {
        boolean hasRadiologyOrderClaimedRadiologyReport =
                radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(null);
        assertFalse(hasRadiologyOrderClaimedRadiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     * @verifies return false if the RadiologyOrder has no completed RadiologyReport
     */
    @Test
    public void hasRadiologyOrderCompletedRadiologyReport_shouldReturnFalseIfTheRadiologyOrderHasNoCompletedRadiologyReport()
            throws Exception {
        
        boolean hasRadiologyOrderCompletedRadiologyReport = radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_DISCONTINUED_RADIOLOGY_REPORT));
        assertFalse(hasRadiologyOrderCompletedRadiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     * @verifies return true if the RadiologyOrder has a completed RadiologyReport
     */
    @Test
    public void hasRadiologyOrderCompletedRadiologyReport_shouldReturnTrueIfTheRadiologyOrderHasACompletedRadiologyReport()
            throws Exception {
        
        boolean hasRadiologyOrderClaimedRadiologyReport = radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT));
        assertTrue(hasRadiologyOrderClaimedRadiologyReport);
    }
    
    /**
     * @see RadiologyOrderService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     * @verifies throw an IllegalArgumentException if radiologyOrder is null
     */
    @Test
    public void hasRadiologyOrderCompletedRadiologyReport_shouldThrowAnIllegalArgumentExceptionIfRadiologyOrderIsNull()
            throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(null);
    }
    
    /**
     * @verifies return a RadiologyReport if the reportStatus is claimed
     * @see RadiologyOrderService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     */
    @Test
    public void getActiveRadiologyReportByRadiologyOrder_shouldReturnARadiologyReportIfTheReportStatusIsClaimed()
            throws Exception {
        
        RadiologyReport activeReport = radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_CLAIMED_RADIOLOGY_REPORT));
        
        assertNotNull(activeReport);
    }
    
    /**
     * @verifies return true a RadiologyReport if the reportStatus is completed
     * @see RadiologyOrderService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     */
    @Test
    public void getActiveRadiologyReportByRadiologyOrder_shouldReturnTrueARadiologyReportIfTheReportStatusIsCompleted()
            throws Exception {
        
        RadiologyReport activeReport = radiologyReportService.getActiveRadiologyReportByRadiologyOrder(
            radiologyOrderService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_WITH_STUDY_AND_COMPLETED_RADIOLOGY_REPORT));
        
        assertNotNull(activeReport);
    }
    
    /**
     * @verifies throw an IllegalArgumentException if radiologyOrder is null
     * @see RadiologyOrderService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     */
    @Test
    public void getActiveRadiologyReportByRadiologyOrder_shouldThrowAnIllegalArgumentExceptionIfRadiologyOrderIsNull()
            throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.getActiveRadiologyReportByRadiologyOrder(null);
    }
}
