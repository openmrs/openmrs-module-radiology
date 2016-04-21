package org.openmrs.module.radiology.study;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;

/**
 * Tests {@link RadiologyStudy}.
 */
public class RadiologyStudyTest {
	
	/**
	 * @see RadiologyStudy#isInProgress()
	 * @verifies return false if performed status is null
	 */
	@Test
	public void isInProgress_shouldReturnFalseIfPerformedStatusIsNull() throws Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		assertFalse(radiologyStudy.isInProgress());
	}
	
	/**
	 * @see RadiologyStudy#isInProgress()
	 * @verifies return false if performed status is not in progress
	 */
	@Test
	public void isInProgress_shouldReturnFalseIfPerformedStatusIsNotInProgress() throws Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		assertFalse(radiologyStudy.isInProgress());
	}
	
	/**
	 * @see RadiologyStudy#isInProgress()
	 * @verifies return true if performed status is in progress
	 */
	@Test
	public void isInProgress_shouldReturnTrueIfPerformedStatusIsInProgress() throws Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		assertTrue(radiologyStudy.isInProgress());
	}
	
	/**
	 * @see RadiologyStudy#isCompleted()
	 * @verifies return false if performedStatus is null
	 */
	@Test
	public void isCompleted_shouldReturnFalseIfPerformedStatusIsNull() throws Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		assertFalse(radiologyStudy.isCompleted());
	}
	
	/**
	 * @see RadiologyStudy#isCompleted()
	 * @verifies return false if performedStatus is not completed
	 */
	@Test
	public void isCompleted_shouldReturnFalseIfPerformedStatusIsNotCompleted() throws Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		assertFalse(radiologyStudy.isCompleted());
	}
	
	/**
	 * @see RadiologyStudy#isCompleted()
	 * @verifies return true if performedStatus is completed
	 */
	@Test
	public void isCompleted_shouldReturnTrueIfPerformedStatusIsCompleted() throws Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		assertTrue(radiologyStudy.isCompleted());
	}
}
