package org.openmrs.module.radiology;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StudyTest {
	
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
