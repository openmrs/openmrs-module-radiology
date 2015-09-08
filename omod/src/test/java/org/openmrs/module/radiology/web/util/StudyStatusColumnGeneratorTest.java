package org.openmrs.module.radiology.web.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.radiology.RadiologyRoles.PERFORMING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.READING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.REFERRRING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.SCHEDULER;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.test.Verifies;

/**
 * Tests {@link StudyStatusColumnGenerator}
 */
public class StudyStatusColumnGeneratorTest {
	
	/**
	 * @see StudyStatusColumnGenerator#getStatusColumnForStudy(User, Study)
	 */
	@Test
	@Verifies(value = "should only return studies scheduled status for user with role scheduler", method = "getStatusColumnForStudy(User, Study)")
	public void getStatusColumnForStudy_shouldOnlyReturnStudiesScheduledStatusForUserWithRoleScheduler() {
		
		Study study = new Study();
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		study.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		
		User userWithRoleScheduler = new User();
		Role roleScheduler = new Role();
		roleScheduler.setRole(SCHEDULER);
		Set<Role> rolesScheduler = new HashSet<Role>();
		rolesScheduler.add(roleScheduler);
		userWithRoleScheduler.setRoles(rolesScheduler);
		
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(userWithRoleScheduler, study), is("SCHEDULED"));
	}
	
	/**
	 * @see StudyStatusColumnGenerator#getStatusColumnForStudy(User, Study)
	 */
	@Test
	@Verifies(value = "should return studies scheduled and performed status for user with role referring physician", method = "getStatusColumnForStudy(User, Study)")
	public void getStatusColumnForStudy_shouldReturnStudiesScheduledAndPerformedStatusForUserWithRoleReferringPhysician() {
		
		Study study = new Study();
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		study.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		
		User userWithRoleReferringPhysician = new User();
		Role roleReferringPhysician = new Role();
		roleReferringPhysician.setRole(REFERRRING_PHYSICIAN);
		Set<Role> rolesReferringPhysician = new HashSet<Role>();
		rolesReferringPhysician.add(roleReferringPhysician);
		userWithRoleReferringPhysician.setRoles(rolesReferringPhysician);
		
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(userWithRoleReferringPhysician, study),
		    is("SCHEDULED IN_PROGRESS"));
	}
	
	/**
	 * @see StudyStatusColumnGenerator#getStatusColumnForStudy(User, Study)
	 */
	@Test
	@Verifies(value = "should return studies scheduled and performed status for user with role performing physician", method = "getStatusColumnForStudy(User, Study)")
	public void getStatusColumnForStudy_shouldReturnStudiesScheduledAndPerformedStatusForUserWithRolePerformingPhysician() {
		
		Study study = new Study();
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		study.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		
		User userWithRolePerformingPhysician = new User();
		Role rolePerformingPhysician = new Role();
		rolePerformingPhysician.setRole(PERFORMING_PHYSICIAN);
		Set<Role> rolesPerformingPhysician = new HashSet<Role>();
		rolesPerformingPhysician.add(rolePerformingPhysician);
		userWithRolePerformingPhysician.setRoles(rolesPerformingPhysician);
		
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(userWithRolePerformingPhysician, study),
		    is("SCHEDULED IN_PROGRESS"));
	}
	
	/**
	 * @see StudyStatusColumnGenerator#getStatusColumnForStudy(User, Study)
	 */
	@Test
	@Verifies(value = "should only return studies performed status for user with role reading physician", method = "getStatusColumnForStudy(User, Study)")
	public void getStatusColumnForStudy_shouldOnlyReturnStudiesPerformedStatusForUserWithRoleReadingPhysician() {
		
		Study study = new Study();
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		study.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		
		User userWithRoleReadingPhysician = new User();
		Role roleReadingPhysician = new Role();
		roleReadingPhysician.setRole(READING_PHYSICIAN);
		Set<Role> rolesReadingPhysician = new HashSet<Role>();
		rolesReadingPhysician.add(roleReadingPhysician);
		userWithRoleReadingPhysician.setRoles(rolesReadingPhysician);
		
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(userWithRoleReadingPhysician, study),
		    is("IN_PROGRESS"));
	}
	
	/**
	 * @see StudyStatusColumnGenerator#getStatusColumnForStudy(User, Study)
	 */
	@Test
	@Verifies(value = "should return studies scheduled and performed status for user with none of the specified roles", method = "getStatusColumnForStudy(User, Study)")
	public void getStatusColumnForStudy_shouldReturnStudiesScheduledAndPerformedStatusForUserWithNoneOfTheSpecifiedRoles() {
		
		Study study = new Study();
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		study.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(new User(), study), is("SCHEDULED IN_PROGRESS"));
	}
	
	/**
	 * @see StudyStatusColumnGenerator#getStatusColumnForStudy(Study, boolean, boolean)
	 */
	@Test
	@Verifies(value = "should return status string", method = "getStatusColumnForStudy(User, Study)")
	public void getStatusColumnForStudy_shouldReturnStatusString() {
		
		Study uninitializedStudy = new Study();
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(uninitializedStudy, true, true), is("UNKNOWN UNKNOWN"));
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(uninitializedStudy, false, true), is("UNKNOWN"));
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(uninitializedStudy, true, false), is("UNKNOWN"));
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(uninitializedStudy, false, false), is(""));
		
		Study studyWithScheduledStatus = new Study();
		studyWithScheduledStatus.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(studyWithScheduledStatus, true, true),
		    is("SCHEDULED UNKNOWN"));
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(studyWithScheduledStatus, false, true), is("UNKNOWN"));
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(studyWithScheduledStatus, true, false),
		    is("SCHEDULED"));
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(studyWithScheduledStatus, false, false), is(""));
		
		Study studyWithPerformedStatus = new Study();
		studyWithPerformedStatus.setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(studyWithPerformedStatus, true, true),
		    is("UNKNOWN IN_PROGRESS"));
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(studyWithPerformedStatus, false, true),
		    is("IN_PROGRESS"));
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(studyWithPerformedStatus, true, false), is("UNKNOWN"));
		assertThat(StudyStatusColumnGenerator.getStatusColumnForStudy(studyWithPerformedStatus, false, false), is(""));
	}
}
