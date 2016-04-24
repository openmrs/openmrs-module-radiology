package org.openmrs.module.radiology.study;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.startsWith;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.dicom.code.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Modality;

import java.util.HashSet;
import java.util.Set;

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
	
	/**
	 * @see RadiologyStudy#isScheduleable()
	 * @verifies return true if performedStatus is null
	 */
	@Test
	public void isScheduleable_shouldReturnTrueIfPerformedStatusIsNull() throws Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setPerformedStatus(null);
		assertTrue(radiologyStudy.isScheduleable());
	}
	
	/**
	 * @see RadiologyStudy#isScheduleable()
	 * @verifies return false if performedStatus is not null
	 */
	@Test
	public void isScheduleable_shouldReturnFalseIfPerformedStatusIsNotNull() throws Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		
		assertFalse(radiologyStudy.isScheduleable());
	}
	
	/**
	 * @see RadiologyStudy#toString()
	 * @verifies return string of study
	 */
	@Test
	public void toString_shouldReturnStringOfStudy() throws Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setStudyId(2);
		radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		radiologyStudy.setStudyInstanceUid("Complete");
		radiologyStudy.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		radiologyStudy.setModality(Modality.CR);
		radiologyStudy.setMwlStatus(MwlStatus.IN_SYNC);
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setOrderId(2);
		
		Patient mockPatient = new Patient();
		mockPatient.setPatientId(1);
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		mockPatient.setNames(personNames);
		radiologyOrder.setPatient(mockPatient);
		
		Concept concept = new Concept();
		concept.setConceptId(2);
		radiologyOrder.setConcept(concept);
		
		radiologyStudy.setRadiologyOrder(radiologyOrder);
		
		assertThat(
			radiologyStudy.toString(),
			startsWith("studyId: 2 studyInstanceUid: Complete radiologyOrder: Order. orderId: 2 patient: Patient#1 concept: 2 care setting: null scheduledStatus: SCHEDULED performedStatus: COMPLETED modality: CR mwlStatus: IN_SYNC "));
	}
	
	/**
	 * @see RadiologyStudy#toString()
	 * @verifies return string of study with null for members that are null
	 */
	@Test
	public void toString_shouldReturnStringOfStudyWithNullForMembersThatAreNull() throws Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setStudyId(2);
		radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		radiologyStudy.setStudyInstanceUid("Complete");
		radiologyStudy.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		radiologyStudy.setModality(Modality.CR);
		radiologyStudy.setMwlStatus(MwlStatus.IN_SYNC);
		
		assertThat(
			radiologyStudy.toString(),
			startsWith("studyId: 2 studyInstanceUid: Complete radiologyOrder: null scheduledStatus: SCHEDULED performedStatus: COMPLETED modality: CR mwlStatus: IN_SYNC "));
	}
}
