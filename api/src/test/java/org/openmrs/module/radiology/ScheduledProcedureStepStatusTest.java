package org.openmrs.module.radiology;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.Verifies;

/**
 * Tests the methods in {@link ScheduledProcedureStepStatus}
 */
public class ScheduledProcedureStepStatusTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * @see ScheduledProcedureStepStatus#getDisplayNameOrUnknown(ScheduledProcedureStepStatus)
	 */
	@Test
	@Verifies(value = "should return unknown given null as scheduled procedure step status", method = "getDisplayNameOrUnknown(ScheduledProcedureStepStatus)")
	public void getDisplayNameOrUnknown_shouldReturnUnknownGivenNullAsScheduledProcedureStepStatus() {
		assertThat(ScheduledProcedureStepStatus.getDisplayNameOrUnknown(null), is("UNKNOWN"));
	}
	
	/**
	 * @see ScheduledProcedureStepStatus#getDisplayNameOrUnknown(ScheduledProcedureStepStatus)
	 */
	@Test
	@Verifies(value = "should return display name given scheduled procedure step status", method = "getDisplayNameOrUnknown(ScheduledProcedureStepStatus)")
	public void getDisplayNameOrUnknown_shouldReturnDisplayNameGivenScheduledProcedureStepStatus() {
		assertThat(ScheduledProcedureStepStatus.getDisplayNameOrUnknown(ScheduledProcedureStepStatus.SCHEDULED),
		    is("SCHEDULED"));
		assertThat(ScheduledProcedureStepStatus.getDisplayNameOrUnknown(ScheduledProcedureStepStatus.ARRIVED), is("ARRIVED"));
		assertThat(ScheduledProcedureStepStatus.getDisplayNameOrUnknown(ScheduledProcedureStepStatus.READY), is("READY"));
		assertThat(ScheduledProcedureStepStatus.getDisplayNameOrUnknown(ScheduledProcedureStepStatus.STARTED), is("STARTED"));
		assertThat(ScheduledProcedureStepStatus.getDisplayNameOrUnknown(ScheduledProcedureStepStatus.DEPARTED),
		    is("DEPARTED"));
	}
}
