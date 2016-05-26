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
    @Verifies(value = "should return name given scheduled procedure step status", method = "getNameOrUnknown(ScheduledProcedureStepStatus)")
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
