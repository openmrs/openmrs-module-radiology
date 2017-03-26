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

import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.APIException;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.test.BaseContextMockTest;

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
    
    @Test
    public void shouldFailToCreateReportIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.createRadiologyReport(null);
    }
    
    @Test
    public void shouldFailToCreateReportIfGivenOrderIsNotCompleted() {
        
        when(radiologyOrder.isNotCompleted()).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.create.for.not.completed.order");
        radiologyReportService.createRadiologyReport(radiologyOrder);
    }
    
    @Test
    public void shouldFailToCreateReportIfGivenOrderHasAClaimedReport() {
        
        when(radiologyReportDAO.hasRadiologyOrderClaimedRadiologyReport(radiologyOrder)).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.create.already.claimed");
        radiologyReportService.createRadiologyReport(radiologyOrder);
    }
    
    @Test
    public void shouldFailToCreateReportIfGivenOrderHasACompletedReport() {
        
        when(radiologyReportDAO.hasRadiologyOrderCompletedRadiologyReport(radiologyOrder)).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.create.already.completed");
        radiologyReportService.createRadiologyReport(radiologyOrder);
    }
    
    @Test
    public void shouldFailToSaveReportDraftGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.saveRadiologyReportDraft(null);
    }
    
    @Test
    public void shouldFailToSaveReportDraftGivenReportWithReportIdNull() {
        
        when(radiologyReport.getReportId()).thenReturn(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.reportId cannot be null");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    @Test
    public void shouldFailToSaveReportDraftIfReportIsCompleted() {
        
        when(radiologyReport.getStatus()).thenReturn(RadiologyReportStatus.COMPLETED);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.saveDraft.already.completed");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    @Test
    public void shouldFailToSaveReportDraftIfReportIsVoided() {
        
        when(radiologyReport.getVoided()).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.saveDraft.already.voided");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    @Test
    public void shouldFailToSaveReportDraftIfGivenReportsOrderHasACompletedReport() {
        
        when(radiologyReport.getRadiologyOrder()).thenReturn(radiologyOrder);
        when(radiologyReportDAO.hasRadiologyOrderCompletedRadiologyReport(radiologyOrder)).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.saveDraft.already.reported");
        radiologyReportService.saveRadiologyReportDraft(radiologyReport);
    }
    
    @Test
    public void shouldFailToVoidReportGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.voidRadiologyReport(null, null);
    }
    
    @Test
    public void shouldFailToVoidIfGivenReportWithReportIdNull() {
        
        when(radiologyReport.getReportId()).thenReturn(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.reportId cannot be null");
        radiologyReportService.voidRadiologyReport(radiologyReport, null);
    }
    
    @Test
    public void shouldFailToVoidReportIfGivenVoidReasonIsNullOrContainsOnlyWhitespaces() {
        
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
    
    @Test
    public void shouldFailToVoidReportIfReportIsCompleted() {
        
        when(radiologyReport.getStatus()).thenReturn(RadiologyReportStatus.COMPLETED);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.void.completed");
        radiologyReportService.voidRadiologyReport(radiologyReport, "some reason");
    }
    
    @Test
    public void shouldFailToSaveReportIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport cannot be null");
        radiologyReportService.saveRadiologyReport(null);
    }
    
    @Test
    public void shouldFailToSaveReportIfGivenReportWithReportIdNull() {
        
        when(radiologyReport.getReportId()).thenReturn(null);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.reportId cannot be null");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    @Test
    public void shouldFailToSaveReportIfGivenReportWithStatusNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReport.status cannot be null");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    @Test
    public void shouldFailToSaveReportIfGivenReportIsCompleted() {
        
        when(radiologyReport.getStatus()).thenReturn(RadiologyReportStatus.COMPLETED);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.complete.completed");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    @Test
    public void shouldFailToSaveReportIfGivenReportIsVoided() {
        
        when(radiologyReport.getStatus()).thenReturn(RadiologyReportStatus.DRAFT);
        when(radiologyReport.getVoided()).thenReturn(true);
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("radiology.RadiologyReport.cannot.complete.voided");
        radiologyReportService.saveRadiologyReport(radiologyReport);
    }
    
    @Test
    public void shouldFailToGetReportByIdIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("reportId cannot be null");
        radiologyReportService.getRadiologyReport(null);
    }
    
    @Test
    public void shouldFailToGetReportByUuidIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportUuid cannot be null");
        radiologyReportService.getRadiologyReportByUuid(null);
    }
    
    @Test
    public void hasOrderClaimedReport_shouldFailIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.hasRadiologyOrderClaimedRadiologyReport(null);
    }
    
    @Test
    public void hasOrderCompletedReport_shouldFailIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.hasRadiologyOrderCompletedRadiologyReport(null);
    }
    
    @Test
    public void shouldFailToGetActiveReportByOrderIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReportService.getActiveRadiologyReportByRadiologyOrder(null);
    }
    
    @Test
    public void shouldFailToGetReportsIfGivenNull() {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyReportSearchCriteria cannot be null");
        radiologyReportService.getRadiologyReports(null);
    }
    
    public interface TestRadiologyReportDAO extends RadiologyReportDAO {}
}
