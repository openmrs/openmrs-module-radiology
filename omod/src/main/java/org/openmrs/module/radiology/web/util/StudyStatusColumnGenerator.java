package org.openmrs.module.radiology.web.util;

import static org.openmrs.module.radiology.RadiologyRoles.PERFORMING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.READING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.REFERRRING_PHYSICIAN;
import static org.openmrs.module.radiology.RadiologyRoles.SCHEDULER;

import org.openmrs.User;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;

public class StudyStatusColumnGenerator {
	
	/**
	 * Get concatenated status string of {@link org.openmrs.module.radiology.Study#scheduledStatus} and
	 * {@link org.openmrs.module.radiology.Study#performedStatus}
	 * 
	 * @param user user for whom the status should be returned
	 * @param study study for which the status should be returned
	 * @return status string for given user and study
	 * @should only return studies scheduled status for user with role scheduler
	 * @should return studies scheduled and performed status for user with role referring physician
	 * @should return studies scheduled and performed status for user with role performing physician
	 * @should only return studies performed status for user with role reading physician
	 * @should return studies scheduled and performed status for user with none of the specified roles
	 */
	public static String getStatusColumnForStudy(User user, Study study) {
		
		if (user.hasRole(REFERRRING_PHYSICIAN, true))
			return getStatusColumnForStudy(study, true, true);
		if (user.hasRole(SCHEDULER, true))
			return getStatusColumnForStudy(study, true, false);
		if (user.hasRole(PERFORMING_PHYSICIAN, true))
			return getStatusColumnForStudy(study, true, true);
		if (user.hasRole(READING_PHYSICIAN, true))
			return getStatusColumnForStudy(study, false, true);
		return getStatusColumnForStudy(study, true, true);
	}
	
	/**
	 * Get concatenated status string of {@link org.openmrs.module.radiology.Study#scheduledStatus} and
	 * {@link org.openmrs.module.radiology.Study#performedStatus}
	 * 
	 * @param study study for which the status should be returned
	 * @param hasSchedulerRole boolean indicating if the user has role scheduler
	 * @param hasAnyPhysicianRole boolean indicating if the user has role referring, performing or reading physician
	 * @return status string
	 * @should return status string
	 */
	protected static String getStatusColumnForStudy(Study study, boolean hasSchedulerRole, boolean hasAnyPhysicianRole) {
		
		String result = "";
		
		String scheduledStatusDescription = "";
		scheduledStatusDescription += ScheduledProcedureStepStatus.getNameOrUnknown(study.getScheduledStatus());
		result += hasSchedulerRole ? scheduledStatusDescription : "";
		
		String performedStatusDescription = "";
		performedStatusDescription += PerformedProcedureStepStatus.getNameOrUnknown(study.getPerformedStatus());
		result += hasAnyPhysicianRole ? (hasSchedulerRole ? " " : "") + performedStatusDescription : "";
		return result;
	}
}
