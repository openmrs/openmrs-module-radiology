package org.openmrs.module.radiology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PersonName;

import java.util.HashSet;
import java.util.Set;

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

	/**
	 * @see Study#isScheduleable()
	 * @verifies return true if performedStatus is null
	 */
	@Test
	public void isScheduleable_shouldReturnNull() {
		Study study = new Study();
		study.setPerformedStatus(null);
		assertTrue(study.isScheduleable());
	}

	/**
	 * @see Study#isScheduleable()
	 * @verifies return false if performedStatus is not null
	 */
	@Test
	public void isScheduleable_shouldReturnFalse() {
		Study study = new Study();
		study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		assertTrue(!study.isScheduleable());
	}

	/**
	 * @see Study#toString() ()
	 * @verifies return String of Study's components
	 */
	@Test
	public void toString_shouldReturnallComponents(){
		Study study = new Study();
		study.setStudyId(2);
		study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		study.setStudyInstanceUid("Complete");
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		study.setModality(Modality.CR);
		study.setMwlStatus(MwlStatus.IN_SYNC);
		RadiologyOrder r = new RadiologyOrder();
		r.setOrderId(2);

		Patient mockPatient = new Patient();
		mockPatient.setPatientId(1);
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		mockPatient.setNames(personNames);
		r.setPatient(mockPatient);

		Concept con = new Concept();
		con.setConceptId(2);
		r.setConcept(con);



		study.setRadiologyOrder(r);
		String s = "studyId: 2 studyInstanceUid: Complete radiologyOrder: Order. orderId: 2 patient: Patient#1 concept: 2 care setting: null scheduledStatus: SCHEDULED performedStatus: COMPLETED modality: CR mwlStatus: IN_SYNC ";
		assertEquals(s, study.toString());
	}

	/**
	 * @see Study#toString() ()
	 * @verifies return null for Study's components that are missing (radiologyOrder in this test)
	 */
	@Test
	public void toString_shouldReturnNullforradiologyOrder(){
		Study study = new Study();
		study.setStudyId(2);
		study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		study.setStudyInstanceUid("Complete");
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		study.setModality(Modality.CR);
		study.setMwlStatus(MwlStatus.IN_SYNC);
		String s = "studyId: 2 studyInstanceUid: Complete radiologyOrder: null scheduledStatus: SCHEDULED performedStatus: COMPLETED modality: CR mwlStatus: IN_SYNC ";
		assertEquals(s, study.toString());
	}
}
