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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.study.RadiologyStudy;

/**
 * Tests {@link RadiologyReport}
 */
public class RadiologyReportTest {
    
    
    private RadiologyOrder radiologyOrder;
    
    private RadiologyReport radiologyReport;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() {
        
        final OrderType radiologyOrderType = new OrderType("Radiology Order", "Order type for radiology exams",
                "org.openmrs.module.radiology.order.RadiologyOrder");
        final Provider principalResultsInterpreter = new Provider();
        principalResultsInterpreter.setId(1);
        principalResultsInterpreter.setName("doctor");
        
        radiologyOrder = new RadiologyOrder();
        radiologyOrder.setOrderId(1);
        radiologyOrder.setOrderType(radiologyOrderType);
        radiologyOrder.setPatient(new Patient());
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
        radiologyOrder.setScheduledDate(calendar.getTime());
        radiologyOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
        radiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
        radiologyOrder.setVoided(false);
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setStudyId(1);
        radiologyStudy.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1");
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(radiologyStudy);
        
        radiologyReport = new RadiologyReport(radiologyOrder);
    }
    
    /**
     * @see RadiologyReport#RadiologyReport(RadiologyOrder)
     * @verifies set radiology order to given radiology order and report status to claimed
     */
    @Test
    public void RadiologyReport_shouldSetRadiologyOrderToGivenRadiologyOrderAndReportStatusToClaimed() throws Exception {
        
        radiologyReport = new RadiologyReport(radiologyOrder);
        
        assertThat(radiologyReport.getRadiologyOrder(), is(radiologyOrder));
        assertThat(radiologyReport.getStatus(), is(RadiologyReportStatus.DRAFT));
    }
    
    /**
     * @see RadiologyReport#RadiologyReport(RadiologyOrder)
     * @verifies throw an illegal argument exception if given radiology order is not completed
     */
    @Test
    public void RadiologyReport_shouldThrowAnIllegalArgumentExceptionIfGivenRadiologyOrderIsNotCompleted() throws Exception {
        
        radiologyOrder.setStudy(new RadiologyStudy());
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder is not completed");
        radiologyReport = new RadiologyReport(radiologyOrder);
    }
    
    /**
     * @see RadiologyReport#RadiologyReport(RadiologyOrder)
     * @verifies throw an illegal argument exception if given radiology order is null
     */
    @Test
    public void RadiologyReport_shouldThrowAnIllegalArgumentExceptionIfGivenRadiologyOrderIsNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("radiologyOrder cannot be null");
        radiologyReport = new RadiologyReport(null);
    }
}
