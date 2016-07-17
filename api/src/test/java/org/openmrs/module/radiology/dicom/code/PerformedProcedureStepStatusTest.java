/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.dicom.code;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.Verifies;

/**
 * Tests {@link PerformedProcedureStepStatus}
 */
public class PerformedProcedureStepStatusTest {
    
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    /**
     * @see PerformedProcedureStepStatus#getNameOrUnknown(PerformedProcedureStepStatus)
     */
    @Test
    @Verifies(value = "should return name given performed procedure step status",
            method = "getNameOrUnknown(PerformedProcedureStepStatus)")
    public void getNameOrUnknown_shouldReturnDisplayNameGivenPerformedProcedureStepStatus() {
        assertThat(PerformedProcedureStepStatus.getNameOrUnknown(PerformedProcedureStepStatus.IN_PROGRESS),
            is("IN_PROGRESS"));
        assertThat(PerformedProcedureStepStatus.getNameOrUnknown(PerformedProcedureStepStatus.DISCONTINUED),
            is("DISCONTINUED"));
        assertThat(PerformedProcedureStepStatus.getNameOrUnknown(PerformedProcedureStepStatus.COMPLETED), is("COMPLETED"));
    }
    
    /**
     * @see PerformedProcedureStepStatus#getNameOrUnknown(PerformedProcedureStepStatus)
     */
    @Test
    @Verifies(value = "should return unknown given null", method = "getNameOrUnknown(PerformedProcedureStepStatus)")
    public void getNameOrUnknown_shouldReturnUnknownGivenNull() {
        assertThat(PerformedProcedureStepStatus.getNameOrUnknown(null), is("UNKNOWN"));
    }
    
    /**
     * @see PerformedProcedureStepStatus#getMatchForDisplayName(String)
     */
    @Test
    @Verifies(value = "should return performed procedure step status given display name",
            method = "getMatchForDisplayName(String)")
    public void getMatchForDisplayName_shouldReturnPerformedProcedureStepStatusGivenDisplayName() {
        assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("IN PROGRESS"),
            is(PerformedProcedureStepStatus.IN_PROGRESS));
        assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("in progress"),
            is(PerformedProcedureStepStatus.IN_PROGRESS));
        assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("In Progress"),
            is(PerformedProcedureStepStatus.IN_PROGRESS));
        
        assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("DISCONTINUED"),
            is(PerformedProcedureStepStatus.DISCONTINUED));
        assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("discontinued"),
            is(PerformedProcedureStepStatus.DISCONTINUED));
        assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("Discontinued"),
            is(PerformedProcedureStepStatus.DISCONTINUED));
        
        assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("COMPLETED"),
            is(PerformedProcedureStepStatus.COMPLETED));
        assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("completed"),
            is(PerformedProcedureStepStatus.COMPLETED));
        assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("Completed"),
            is(PerformedProcedureStepStatus.COMPLETED));
    }
    
    /**
     * @see PerformedProcedureStepStatus#getMatchForDisplayName(String)
     */
    @Test
    @Verifies(value = "should return null given undefined display name", method = "getMatchForDisplayName(String)")
    public void getMatchForDisplayName_shouldReturnNullGivenUndefinedDisplayName() {
        assertNull(PerformedProcedureStepStatus.getMatchForDisplayName("NON EXISTING FANTASY PERFORMEDSTATUS"));
    }
    
    /**
     * @see PerformedProcedureStepStatus#getMatchForDisplayName(String)
     */
    @Test
    @Verifies(value = "should throw IllegalArgumentException given null", method = "getMatchForDisplayName(String)")
    public void getMatchForDisplayName_shouldThrowIllegalArgumentExceptionGivenNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("displayName is required");
        PerformedProcedureStepStatus.getMatchForDisplayName(null);
    }
}
