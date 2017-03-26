/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.study.RadiologyStudy;

/**
 * Tests {@link RadiologyOrder}
 */
public class RadiologyOrderTest {
    
    
    @Test
    public void shouldSetTheStudyToGivenStudy() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy study = new RadiologyStudy();
        
        radiologyOrder.setStudy(study);
        
        assertThat(radiologyOrder.getStudy(), is(study));
    }
    
    @Test
    public void shouldSetTheRadiologyOrderOfGivenStudyToThisRadiologyOrder() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy study = new RadiologyStudy();
        
        radiologyOrder.setStudy(study);
        
        assertThat(study.getRadiologyOrder(), is(radiologyOrder));
    }
    
    @Test
    public void shouldNotFailToSetTheStudyGivenNull() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        
        radiologyOrder.setStudy(null);
        
        assertNotNull(radiologyOrder);
    }
    
    @Test
    public void isInProgress_shouldReturnFalseIfAssociatedStudyIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        radiologyOrder.setStudy(null);
        
        assertFalse(radiologyOrder.isInProgress());
    }
    
    @Test
    public void isInProgress_shouldReturnFalseIfAssociatedStudyIsNotInProgress() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy study = new RadiologyStudy();
        study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(study);
        
        assertFalse(radiologyOrder.isInProgress());
    }
    
    @Test
    public void isInProgress_shouldReturnTrueIfAssociatedStudyIsInProgress() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy study = new RadiologyStudy();
        study.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        radiologyOrder.setStudy(study);
        
        assertTrue(radiologyOrder.isInProgress());
    }
    
    @Test
    public void isNotInProgress_shouldReturnTrueIfAssociatedStudyIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        radiologyOrder.setStudy(null);
        
        assertTrue(radiologyOrder.isNotInProgress());
    }
    
    @Test
    public void isNotInProgress_shouldReturnTrueIfAssociatedStudyIsNotInProgress() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy study = new RadiologyStudy();
        study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(study);
        
        assertTrue(radiologyOrder.isNotInProgress());
    }
    
    @Test
    public void isNotInProgress_shouldReturnFalseIfAssociatedStudyInProgress() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy study = new RadiologyStudy();
        study.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        radiologyOrder.setStudy(study);
        
        assertFalse(radiologyOrder.isNotInProgress());
    }
    
    @Test
    public void isCompleted_shouldReturnFalseIfAssociatedStudyIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        radiologyOrder.setStudy(null);
        
        assertFalse(radiologyOrder.isCompleted());
    }
    
    @Test
    public void isCompleted_shouldReturnFalseIfAssociatedStudyIsNotCompleted() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        radiologyOrder.setStudy(new RadiologyStudy());
        
        assertFalse(radiologyOrder.isCompleted());
    }
    
    @Test
    public void isCompleted_shouldReturnTrueIfAssociatedStudyIsCompleted() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy study = new RadiologyStudy();
        study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(study);
        
        assertTrue(radiologyOrder.isCompleted());
    }
    
    @Test
    public void isNotCompleted_shouldReturnTrueIfAssociatedStudyIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        radiologyOrder.setStudy(null);
        
        assertTrue(radiologyOrder.isNotCompleted());
    }
    
    @Test
    public void isNotCompleted_shouldReturnTrueIfAssociatedStudyIsNotCompleted() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        radiologyOrder.setStudy(new RadiologyStudy());
        
        assertTrue(radiologyOrder.isNotCompleted());
    }
    
    @Test
    public void isNotCompleted_shouldReturnFalseIfAssociatedStudyIsCompleted() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy study = new RadiologyStudy();
        study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(study);
        
        assertFalse(radiologyOrder.isNotCompleted());
    }
    
    @Test
    public void isDiscontinuationAllowed_shouldReturnFalseIfRadiologyOrderIsDiscontinuedRightNow() throws Exception {
        
        // fake a discontinued RadiologyOrder by setting its stoppedDate
        Field dateStoppedField = Order.class.getDeclaredField("dateStopped");
        dateStoppedField.setAccessible(true);
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        radiologyOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
        radiologyOrder.setDateActivated(Date.from(LocalDateTime.now()
                .minusMonths(3)
                .atZone(ZoneId.systemDefault())
                .toInstant()));
        radiologyOrder.setScheduledDate(Date.from(LocalDateTime.now()
                .minusMonths(2)
                .atZone(ZoneId.systemDefault())
                .toInstant()));
        dateStoppedField.set(radiologyOrder, Date.from(LocalDateTime.now()
                .minusMonths(1)
                .atZone(ZoneId.systemDefault())
                .toInstant()));
        
        assertFalse(radiologyOrder.isDiscontinuationAllowed());
    }
    
    @Test
    public void isDiscontinuationAllowed_shouldReturnFalseIfRadiologyOrderIsInProgress() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy study = new RadiologyStudy();
        study.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        radiologyOrder.setStudy(study);
        
        assertFalse(radiologyOrder.isDiscontinuationAllowed());
    }
    
    @Test
    public void isDiscontinuationAllowed_shouldReturnFalseIfRadiologyOrderIsCompleted() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy study = new RadiologyStudy();
        study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(study);
        
        assertFalse(radiologyOrder.isDiscontinuationAllowed());
    }
    
    @Test
    public void
            isDiscontinuationAllowed_shouldReturnTrueIfRadiologyOrderIsNotDiscontinuedRightNowAndNotInProgressAndNotCompleted()
                    throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        radiologyOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
        radiologyOrder.setScheduledDate(Date.from(LocalDateTime.now()
                .plusMonths(2)
                .atZone(ZoneId.systemDefault())
                .toInstant()));
        
        assertTrue(radiologyOrder.isDiscontinuationAllowed());
    }
}
