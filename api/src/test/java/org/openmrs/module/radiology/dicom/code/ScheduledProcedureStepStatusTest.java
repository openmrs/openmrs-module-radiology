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
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.Verifies;

/**
 * Tests {@link ScheduledProcedureStepStatus}
 */
public class ScheduledProcedureStepStatusTest {
    
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    /**
     * @see ScheduledProcedureStepStatus#getNameOrUnknown(ScheduledProcedureStepStatus)
     */
    @Test
    @Verifies(value = "should return name given scheduled procedure step status",
            method = "getNameOrUnknown(ScheduledProcedureStepStatus)")
    public void getNameOrUnknown_shouldReturnNameGivenScheduledProcedureStepStatus() {
        assertThat(ScheduledProcedureStepStatus.getNameOrUnknown(ScheduledProcedureStepStatus.SCHEDULED), is("SCHEDULED"));
        assertThat(ScheduledProcedureStepStatus.getNameOrUnknown(ScheduledProcedureStepStatus.ARRIVED), is("ARRIVED"));
        assertThat(ScheduledProcedureStepStatus.getNameOrUnknown(ScheduledProcedureStepStatus.READY), is("READY"));
        assertThat(ScheduledProcedureStepStatus.getNameOrUnknown(ScheduledProcedureStepStatus.STARTED), is("STARTED"));
        assertThat(ScheduledProcedureStepStatus.getNameOrUnknown(ScheduledProcedureStepStatus.DEPARTED), is("DEPARTED"));
    }
    
    /**
     * @see ScheduledProcedureStepStatus#getNameOrUnknown(ScheduledProcedureStepStatus)
     */
    @Test
    @Verifies(value = "should return unknown given null", method = "getNameOrUnknown(ScheduledProcedureStepStatus)")
    public void getNameOrUnknown_shouldReturnUnknownGivenNull() {
        assertThat(ScheduledProcedureStepStatus.getNameOrUnknown(null), is("UNKNOWN"));
    }
}
