/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report;

import java.util.List;

import org.openmrs.Provider;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.RadiologyPrivileges;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RadiologyReportService extends OpenmrsService {
	
	/**
	 * Creates a RadiologyReport and sets the radiologyReportStatus to claimed
	 *
	 * @param radiologyOrder RadiologyOrder
	 * @return a new claimed RadiologyReport
	 * @throws IllegalArgumentException if given RadiologyOrder is null
	 * @throws IllegalArgumentException if Study of given radiologyReport is null
	 * @throws IllegalArgumentException if Study of given RadiologyOrder is not completed
	 * @throws UnsupportedOperationException if given order has a completed RadiologyReport
	 * @throws UnsupportedOperationException if given order has a claimed RadiologyReport
	 * @should create a radiology order with report status claimed given a completed radiology order
	 * @should throw an illegal argument exception if given radiology order is null
	 * @should throw an illegal argument exception if given radiology order is not completed
	 * @should throw an UnsupportedOperationException if given order has a completed RadiologyReport
	 * @should throw an UnsupportedOperationException if given order has a claimed RadiologyReport
	 */
	@Authorized(RadiologyPrivileges.ADD_RADIOLOGY_REPORTS)
	public RadiologyReport createAndClaimRadiologyReport(RadiologyOrder radiologyOrder) throws IllegalArgumentException,
			UnsupportedOperationException;
	
	/**
	 * Save the given radiologyReport
	 *
	 * @param radiologyReport RadiologyReport to be saved
	 * @return the saved radiologyReport
	 * @throws IllegalArgumentException if radiologyReport is null
	 * @throws IllegalArgumentException if radiologyReportStatus is null
	 * @throws UnsupportedOperationException if radiologyReport is discontinued
	 * @throws UnsupportedOperationException if radiologyReport is completed
	 * @should save radiologyReport in database and return it
	 * @should throw an IllegalArgumentException if radiologyReport is null
	 * @should throw an IllegalArgumentException if radiologyReportStatus is null
	 * @should throw an UnsupportedOperationException if radiologyReport is discontinued
	 * @should throw an UnsupportedOperationException if radiologyReport is completed
	 */
	@Authorized(RadiologyPrivileges.EDIT_RADIOLOGY_REPORTS)
	public RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport) throws IllegalArgumentException,
			UnsupportedOperationException;
	
	/**
	 * Unclaims the given radiologyReport and sets the radiologyReportStatus to discontinued
	 *
	 * @param radiologyReport RadiologyReport to be unclaimed
	 * @return the discontinued RadiologyReport
	 * @throws IllegalArgumentException if radiologyReport is null
	 * @throws IllegalArgumentException if radiologyReportStatus is null
	 * @throws UnsupportedOperationException if radiologyReport is discontinued
	 * @throws UnsupportedOperationException if radiologyReport is completed
	 * @should set the radiologyReportStatus of radiologyReport to discontinued
	 * @should throw an IllegalArgumentException if radiologyReport is null
	 * @should throw an IllegalArgumentException if radiologyReportStatus is null
	 * @should throw an UnsupportedOperationException if radiologyReport is discontinued
	 * @should throw an UnsupportedOperationException if radiologyReport is completed
	 */
	@Authorized(RadiologyPrivileges.DELETE_RADIOLOGY_REPORTS)
	public RadiologyReport unclaimRadiologyReport(RadiologyReport radiologyReport) throws IllegalArgumentException,
			UnsupportedOperationException;
	
	/**
	 * Completes the given radiologyReport and sets the radiologyReportStatus to completed
	 *
	 * @param radiologyReport RadiologyReport to be completed
	 * @param principalResultsInterpreter which the RadiologyReport should be set to
	 * @return completed RadiologyReport matching given radiologyReport with
	 *         principalResultsInterpreter
	 * @throws IllegalArgumentException if radiologyReport is null
	 * @throws IllegalArgumentException if principalResultsInterpreter is null
	 * @throws IllegalArgumentException if radiologyReportStatus is null
	 * @throws UnsupportedOperationException if radiologyReport is discontinued
	 * @throws UnsupportedOperationException if radiologyReport is completed
	 * @should set the reportDate of the radiologyReport to the day the RadiologyReport was
	 *         completed
	 * @should set the radiologyReportStatus to complete
	 * @should throw an IllegalArgumentException if principalResultsInterpreter is null
	 * @should throw an IllegalArgumentException if radiologyReport is null
	 * @should throw an IllegalArgumentException if radiologyReportStatus is null
	 * @should throw an UnsupportedOperationException if radiologyReport is discontinued
	 * @should throw an UnsupportedOperationException if radiologyReport is completed
	 */
	@Authorized(RadiologyPrivileges.EDIT_RADIOLOGY_REPORTS)
	public RadiologyReport completeRadiologyReport(RadiologyReport radiologyReport, Provider principalResultsInterpreter)
			throws IllegalArgumentException, UnsupportedOperationException;
	
	/**
	 * Get a RadiologyReport matching the radiologyReportId
	 *
	 * @param radiologyReportId of RadiologyReport
	 * @return RadiologyReport matching given radiologyReportId
	 * @throws IllegalArgumentException if radiologyReportId is null
	 * @should fetch RadiologyReport matching given radiologyReportId
	 * @should throw IllegalArgumentException if radiologyReportId is null
	 */
	@Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
	RadiologyReport getRadiologyReportByRadiologyReportId(Integer radiologyReportId) throws IllegalArgumentException;
	
	/**
	 * Get all RadiologyReports fetched by radiologyOrder and radiologyReportStatus
	 *
	 * @param radiologyOrder RadiologyOrder for which the RadiologyReport should be fetched
	 * @param reportStatus RadiologyReportStatus the RadiologyReport should have
	 * @return List with RadiologyReport filtered by radiologyOrder and reportStatus
	 * @throws IllegalArgumentException if given radiologyOrder is null
	 * @throws IllegalArgumentException if given reportStatus is null
	 * @should return a list of completed RadiologyReport if reportStatus is completed
	 * @should return a list of claimed RadiologyReport if reportStatus is claimed
	 * @should return a list of discontinued RadiologyReport if reportStatus is discontinued
	 * @should return an empty list if there are no RadiologyReports for this radiologyOrder
	 * @should throw an IllegalArgumentException if given radiologyOrder is null
	 * @should throw an IllegalArgumentException if given reportStatus is null
	 */
	@Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
	public List<RadiologyReport> getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder radiologyOrder,
			RadiologyReportStatus reportStatus) throws IllegalArgumentException;
	
	/**
	 * Convenience method to check if a RadiologyOrder has a claimed RadiologyReport
	 *
	 * @param radiologyOrder RadiologyOrder the radiologyOrder which should be checked
	 * @return true if RadiologyOrder has a claimed RadiologyReport, otherwise false and also if RadiologyOrder is null
	 * @should return true if the RadiologyOrder has a claimed RadiologyReport
	 * @should return false if the RadiologyOrder has no claimed RadiologyReport
	 * @should return false if the RadiologyOrder is null
	 */
	@Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
	public boolean hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder radiologyOrder);
	
	/**
	 * Convenience method to check if a RadiologyOrder has a completed RadiologyReport
	 *
	 * @param radiologyOrder RadiologyOrder the radiologyOrder which should be checked
	 * @return true if RadiologyOrder has a completed RadiologyReport, otherwise false
	 * @throws IllegalArgumentException if radiologyOrder is null
	 * @should return true if the RadiologyOrder has a completed RadiologyReport
	 * @should return false if the RadiologyOrder has no completed RadiologyReport
	 * @should throw an IllegalArgumentException if radiologyOrder is null
	 */
	@Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
	public boolean hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder radiologyOrder);
	
	/**
	 * Get the active (can be claimed or completed) RadiologyReport matching the radiologyOrder
	 *
	 * @param radiologyOrder RadiologyOrder the radiologyOrder which should be checked
	 * @return RadiologyReport filtered by radiologyOrder and radiologyReportStatus not equal to
	 *         discontinued
	 * @throws IllegalArgumentException if radiologyOrder is null
	 * @should return a RadiologyReport if the reportStatus is completed
	 * @should return a RadiologyReport if the reportStatus is claimed
	 * @should return null if the reportStatus is not null, completed, or claimed
	 * @should throw an IllegalArgumentException if radiologyOrder is null
	 */
	@Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
	public RadiologyReport getActiveRadiologyReportByRadiologyOrder(RadiologyOrder radiologyOrder);
}
