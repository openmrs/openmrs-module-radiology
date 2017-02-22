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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.api.APIException;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.test.BaseContextMockTest;

import static org.mockito.Mockito.when;

/**
 *  Tests {@link RadiologyReportService}
 */
public class RadiologyReportServiceTest extends BaseContextMockTest {
    
    
    @InjectMocks
    private RadiologyReportService radiologyReportService = new RadiologyReportServiceImpl();
    
    @Mock
    private RadiologyOrder radiologyOrder;
    
    @Mock
    private RadiologyReport radiologyReport;
    
    @Mock
    private TestRadiologyReportDAO radiologyReportDAO;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    /**
     * @see RadiologyReportService#createRadiologyReport(RadiologyOrder)
     * @verifies throw illegal argument exception given null
     */
    @Test
    public void createRadiologyReport_shouldThrowIllegalArgumentExceptionGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.createRadiologyReport(null);
    }
    
    /**
     * @see RadiologyReportService#createRadiologyReport(RadiologyOrder)
     * @verifies throw api exception if given radiology order is not completed
     */
    @Test
    public void createRadiologyReport_shouldThrowAPIExceptionIfGivenRadiologyOrderIsNotCompleted() {
        when(radiologyOrder.isNotCompleted()).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.create.for.not.completed.order");
        radiologyReportService.createRadiologyReport(radiologyOrder);
    }
    
    /**
     * @see RadiologyReportService#createRadiologyReport(RadiologyOrder)
     * @verifies throw api exception if given order has a claimed radiology report
     */
    @Test
    public void createRadiologyReport_shouldThrowAPIExceptionIfGivenOrderHasAClaimedRadiologyReport() {
        when(radiologyReportDAO.hasRadiologyOrderClaimedRadiologyReport(radiologyOrder)).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.create.already.claimed");
        radiologyReportService.createRadiologyReport(radiologyOrder);
    }
    
    /**
     * @see RadiologyReportService#createRadiologyReport(RadiologyOrder)
     * @verifies throw api exception if given order has a completed radiology report
     */
    @Test
    public void createRadiologyReport_shouldThrowAPIExceptionIfGivenOrderHasACompletedRadiologyReport() {
        when(radiologyReportDAO.hasRadiologyOrderCompletedRadiologyReport(radiologyOrder)).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.create.already.completed");
        radiologyReportService.createRadiologyReport(radiologyOrder);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void saveRadiologyReportDraft_shouldThrowIllegalArgumentExceptionGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.saveRadiologyReportDraft(null);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies throw illegal argument exception if given radiology report with reportId null
     */
    @Test
    public void saveRadiologyReportDraft_shouldThrowIllegalArgumentExceptionGivenRadiologyReportWithReportIdNull() {
        when(radiologyReport.getReportId()).thenReturn(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.reportId cannot be null");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies throw api exception if radiology report is completed
     */
    @Test
    public void saveRadiologyReportDraft_shouldThrowAPIExceptionIfRadiologyReportIsCompleted() {
        when(radiologyReport.getStatus()).thenReturn(RadiologyReportStatus.COMPLETED);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.saveDraft.already.completed");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies throw api exception if radiology report is voided
     */
    @Test
    public void saveRadiologyReportDraft_shouldThrowAPIExceptionIfRadiologyReportIsVoided() {
        when(radiologyReport.getVoided()).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.saveDraft.already.voided");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     * @verifies throw api exception if given radiology reports order has a completed radiology report
     */
    @Test
    public void saveRadiologyReportDraft_shouldThrowAPIExceptionIfGivenRadiologyReportsOrderHasACompletedRadiologyReport() {
        when(radiologyReport.getRadiologyOrder()).thenReturn(radiologyOrder);
        when(radiologyReportDAO.hasRadiologyOrderCompletedRadiologyReport(radiologyOrder)).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.saveDraft.already.reported");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#voidRadiologyReport(RadiologyReport, String)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void voidRadiologyReport_shouldThrowIllegalArgumentExceptionGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.voidRadiologyReport(null, null);
    }
    
    /**
     * @see RadiologyReportService#voidRadiologyReport(RadiologyReport, String)
     * @verifies throw illegal argument exception if given radiology report with reportId null
     */
    @Test
    public void voidRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenRadiologyReportWithReportIdNull() {
        when(radiologyReport.getReportId()).thenReturn(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.reportId cannot be null");
        radiologyReportService.voidRadiologyReport(radiologyReport, null);
    }
    
    /**
     * @see RadiologyReportService#voidRadiologyReport(RadiologyReport, String)
     * @verifies throw illegal argument exception if given void reason is null or contains only whitespaces
     */
    @Test
    public void voidRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenVoidReasonIsNullOrContainsOnlyWhitespaces() {
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
    public void voidRadiologyReport_shouldThrowAPIExceptionIfRadiologyReportIsCompleted() {
        when(radiologyReport.getStatus()).thenReturn(RadiologyReportStatus.COMPLETED);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.void.completed");
        radiologyReportService.voidRadiologyReport(radiologyReport, "some reason");
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw illegal argument exception if given radiology report is null
     */
    @Test
    public void saveRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenRadiologyReportIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.saveRadiologyReport(null);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw illegal argument exception if given radiology report with reportId null
     */
    @Test
    public void saveRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenRadiologyReportWithReportIdNull() {
        when(radiologyReport.getReportId()).thenReturn(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.reportId cannot be null");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw illegal argument exception if given radiology report with status null
     */
    @Test
    public void saveRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenRadiologyReportWithStatusNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.status cannot be null");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw api exception if radiology report is completed
     */
    @Test
    public void saveRadiologyReport_shouldThrowAPIExceptionIfRadiologyReportIsCompleted() {
        when(radiologyReport.getStatus()).thenReturn(RadiologyReportStatus.COMPLETED);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.complete.completed");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
     * @verifies throw api exception if radiology report is voided
     */
    @Test
    public void saveRadiologyReport_shouldThrowAPIExceptionIfRadiologyReportIsVoided() {
        when(radiologyReport.getStatus()).thenReturn(RadiologyReportStatus.DRAFT);
        when(radiologyReport.getVoided()).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.complete.voided");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReport(Integer)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("reportId cannot be null");
        radiologyReportService.getRadiologyReport(null);
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReportByUuid(String)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyReportByUuid_shouldThrowIllegalArgumentExceptionIfGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportUuid cannot be null");
        radiologyReportService.getRadiologyReportByUuid(null);
    }
    
    /**
     * @see RadiologyReportService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void hasRadiologyOrderClaimedRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(null);
    }
    
    /**
     * @see RadiologyReportService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void hasRadiologyOrderCompletedRadiologyReport_shouldThrowIllegalArgumentExceptionIfGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(null);
    }
    
    /**
     * @see RadiologyReportService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getActiveRadiologyReportByRadiologyOrder_shouldThrowIllegalArgumentExceptionIfGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.getActiveRadiologyReportByRadiologyOrder(null);
    }
    
    /**
     * @see RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     * @verifies throw illegal argument exception if given null
     */
    @Test
    public void getRadiologyReports_shouldThrowIllegalArgumentExceptionIfGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportSearchCriteria cannot be null");
        radiologyReportService.getRadiologyReports(null);
    }
    
    public interface TestRadiologyReportDAO extends RadiologyReportDAO {}
}
