package org.openmrs.module.radiology;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.Verifies;

/**
 * Tests the methods in {@link PerformedProcedureStepStatus}
 */
public class PerformedProcedureStepStatusTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * @see PerformedProcedureStepStatus#getDisplayNameOrUnknown(PerformedProcedureStepStatus)
	 */
	@Test
	@Verifies(value = "should return unknown given null as performed procedure step status", method = "getDisplayNameOrUnknown(PerformedProcedureStepStatus)")
	public void getDisplayNameOrUnknown_shouldReturnUnknownGivenNullAsPerformedProcedureStepStatus() {
		assertThat(PerformedProcedureStepStatus.getDisplayNameOrUnknown(null), is("UNKNOWN"));
	}
	
	/**
	 * @see PerformedProcedureStepStatus#getDisplayNameOrUnknown(PerformedProcedureStepStatus)
	 */
	@Test
	@Verifies(value = "should return display name given performed procedure step status", method = "getDisplayNameOrUnknown(PerformedProcedureStepStatus)")
	public void getDisplayNameOrUnknown_shouldReturnDisplayNameGivenPerformedProcedureStepStatus() {
		assertThat(PerformedProcedureStepStatus.getDisplayNameOrUnknown(PerformedProcedureStepStatus.IN_PROGRESS),
		    is("IN PROGRESS"));
		assertThat(PerformedProcedureStepStatus.getDisplayNameOrUnknown(PerformedProcedureStepStatus.DISCONTINUED),
		    is("DISCONTINUED"));
		assertThat(PerformedProcedureStepStatus.getDisplayNameOrUnknown(PerformedProcedureStepStatus.COMPLETED),
		    is("COMPLETED"));
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
	@Verifies(value = "should throw IllegalArgumentException if display name is null", method = "getMatchForDisplayName(String)")
	public void getMatchForDisplayName_shouldThrowIllegalArgumentExceptionIfDisplayNameIsNull() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("displayName is required");
		PerformedProcedureStepStatus.getMatchForDisplayName(null);
	}
	
	/**
	 * @see PerformedProcedureStepStatus#getMatchForDisplayName(String)
	 */
	@Test
	@Verifies(value = "should return performed procedure step status given display name", method = "getMatchForDisplayName(String)")
	public void getMatchForDisplayName_shouldReturnPerformedProcedureStepStatusGivenDisplayName() {
		assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("IN PROGRESS"),
		    is(PerformedProcedureStepStatus.IN_PROGRESS));
		assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("in progress"),
		    is(PerformedProcedureStepStatus.IN_PROGRESS));
		assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("In Progress"),
		    is(PerformedProcedureStepStatus.IN_PROGRESS));
		assertThat(PerformedProcedureStepStatus.getMatchForDisplayName("Progress"),
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
}
