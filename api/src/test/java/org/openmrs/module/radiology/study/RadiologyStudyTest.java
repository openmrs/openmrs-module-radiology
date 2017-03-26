/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.study;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;

/**
 * Tests {@link RadiologyStudy}.
 */
public class RadiologyStudyTest {
    
    
    @Test
    public void isInProgress_shouldReturnFalseIfPerformedStatusIsNull() throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        
        assertFalse(radiologyStudy.isInProgress());
    }
    
    @Test
    public void shouldReturnFalseIfPerformedStatusIsNotInProgress() throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        
        assertFalse(radiologyStudy.isInProgress());
    }
    
    @Test
    public void shouldReturnTrueIfPerformedStatusIsInProgress() throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        
        assertTrue(radiologyStudy.isInProgress());
    }
    
    @Test
    public void isCompleted_shouldReturnFalseIfPerformedStatusIsNull() throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        
        assertFalse(radiologyStudy.isCompleted());
    }
    
    @Test
    public void shouldReturnFalseIfPerformedStatusIsNotCompleted() throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        
        assertFalse(radiologyStudy.isCompleted());
    }
    
    @Test
    public void shouldReturnTrueIfPerformedStatusIsCompleted() throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        
        assertTrue(radiologyStudy.isCompleted());
    }
    
    @Test
    public void shouldReturnTrueIfPerformedStatusIsNull() throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(null);
        
        assertTrue(radiologyStudy.isScheduleable());
    }
    
    @Test
    public void shouldReturnFalseIfPerformedStatusIsNotNull() throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        
        assertFalse(radiologyStudy.isScheduleable());
    }
    
    @Test
    public void shouldReturnStringOfStudy() throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setStudyId(2);
        radiologyStudy.setStudyInstanceUid("1.2.4.1.2");
        
        assertThat(radiologyStudy.toString(), startsWith("studyId: 2 studyInstanceUid: 1.2.4.1.2"));
    }
    
    @Test
    public void shouldReturnStringOfStudyWithNullForMembersThatAreNull() throws Exception {
        
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        
        assertThat(radiologyStudy.toString(), startsWith("studyId: null studyInstanceUid: null"));
    }
}
