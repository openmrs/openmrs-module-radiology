package org.openmrs.module.radiology.study;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;

/**
 * Tests {@link Study}.
 */
public class StudyTest {
	
	/**
	 * @see Study#isInProgress()
	 * @verifies return false if performed status is null
	 */
	@Test
	public void isInProgress_shouldReturnFalseIfPerformedStatusIsNull() throws Exception {
		
		Study study = new Study();
		assertFalse(study.isInProgress());
	}
	
	/**
	 * @see Study#isInProgress()
	 * @verifies return false if performed status is not in progress
	 */
	@Test
	public void isInProgress_shouldReturnFalseIfPerformedStatusIsNotInProgress() throws Exception {
		
		Study study = new Study();
		study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		assertFalse(study.isInProgress());
	}
	
	/**
	 * @see Study#isInProgress()
	 * @verifies return true if performed status is in progress
	 */
	@Test
	public void isInProgress_shouldReturnTrueIfPerformedStatusIsInProgress() throws Exception {
		
		Study study = new Study();
		study.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		assertTrue(study.isInProgress());
	}
	
	/**
	 * @see Study#isCompleted()
	 * @verifies return false if performedStatus is null
	 */
	@Test
	public void isCompleted_shouldReturnFalseIfPerformedStatusIsNull() throws Exception {
		
		Study study = new Study();
		assertFalse(study.isCompleted());
	}
	
	/**
	 * @see Study#isCompleted()
	 * @verifies return false if performedStatus is not completed
	 */
	@Test
	public void isCompleted_shouldReturnFalseIfPerformedStatusIsNotCompleted() throws Exception {
		
		Study study = new Study();
		study.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		assertFalse(study.isCompleted());
	}
	
	/**
	 * @see Study#isCompleted()
	 * @verifies return true if performedStatus is completed
	 */
	@Test
	public void isCompleted_shouldReturnTrueIfPerformedStatusIsCompleted() throws Exception {
		
		Study study = new Study();
		study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		assertTrue(study.isCompleted());
	}
}
