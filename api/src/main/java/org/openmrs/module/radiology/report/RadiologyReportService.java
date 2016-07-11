/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
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

/**
 * Service layer for {@code RadiologyReport}.
 * 
 * @see org.openmrs.module.radiology.report.RadiologyReport
 */
@Transactional
public interface RadiologyReportService extends OpenmrsService {
    
    
    /**
     * Saves a {@code RadiologyReport} to the database and sets its status to claimed.
     *
     * @param radiologyOrder the radiology order for which a radiology report will be created and claimed
     * @return the created and claimed radiology report
     * @throws IllegalArgumentException if given null
     * @throws IllegalArgumentException if Study of given radiologyOrder is null
     * @throws IllegalArgumentException if Study of given radiologyOrder is not completed
     * @throws UnsupportedOperationException if given radiologyOrder has a completed RadiologyReport
     * @throws UnsupportedOperationException if given radiologyOrder has a claimed RadiologyReport
     * @should create a radiology order with report status claimed given a completed radiology order
     * @should throw illegal argument exception if given null
     * @should throw illegal argument exception if given radiology order is not completed
     * @should throw unsupported operation exception if given order has a completed radiology report
     * @should throw unsupported operation exception if given order has a claimed radiology report
     */
    @Authorized(RadiologyPrivileges.ADD_RADIOLOGY_REPORTS)
    public RadiologyReport createAndClaimRadiologyReport(RadiologyOrder radiologyOrder);
    
    /**
     * Saves a {@code RadiologyReport} to the database.
     *
     * @param radiologyReport the radiology report to be saved
     * @return the saved radiology report
     * @throws IllegalArgumentException if given null
     * @throws IllegalArgumentException if radiologyReportStatus is null
     * @throws UnsupportedOperationException if radiologyReport is discontinued
     * @throws UnsupportedOperationException if radiologyReport is completed
     * @should save radiology report to the database and return it
     * @should throw illegal argument exception if given null
     * @should throw illegal argument exception if radiology report status is null
     * @should throw unsupported operation exception if radiology report is completed
     * @should throw unsupported operation exception if radiology report is discontinued
     */
    @Authorized(RadiologyPrivileges.EDIT_RADIOLOGY_REPORTS)
    public RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport);
    
    /**
     * Unclaims a {@code RadiologyReport} and and sets its status to discontinued.
     *
     * @param radiologyReport the radiology report to be unclaimed
     * @return the discontinued radiology report
     * @throws IllegalArgumentException if given null
     * @throws IllegalArgumentException if radiologyReportStatus is null
     * @throws UnsupportedOperationException if radiologyReport is discontinued
     * @throws UnsupportedOperationException if radiologyReport is completed
     * @should set the radiology report status to discontinued
     * @should throw illegal argument exception if given null
     * @should throw illegal argument exception if radiology report status is null
     * @should throw unsupported operation exception if radiology report is completed
     * @should throw unsupported operation exception if radiology report is discontinued
     */
    @Authorized(RadiologyPrivileges.DELETE_RADIOLOGY_REPORTS)
    public RadiologyReport unclaimRadiologyReport(RadiologyReport radiologyReport);
    
    /**
     * Completes a {@code radiologyReport} and and sets its status to completed.
     *
     * @param radiologyReport the radiology report to be completed
     * @param principalResultsInterpreter the provider which completed the radiology report
     * @return the completed radiology report
     *         principalResultsInterpreter
     * @throws IllegalArgumentException if radiologyReport is null
     * @throws IllegalArgumentException if principalResultsInterpreter is null
     * @throws IllegalArgumentException if radiologyReportStatus is null
     * @throws UnsupportedOperationException if radiologyReport is discontinued
     * @throws UnsupportedOperationException if radiologyReport is completed
     * @should set the report date of the radiology report to the day the radiology report was completed
     * @should set the radiology report status to complete
     * @should throw illegal argument exception if principal results interpreter is null
     * @should throw illegal argument exception if radiology report is null
     * @should throw illegal argument exception if radiology report status is null
     * @should throw unsupported operation exception if radiology report is completed
     * @should throw unsupported operation exception if radiology report is discontinued
     */
    @Authorized(RadiologyPrivileges.EDIT_RADIOLOGY_REPORTS)
    public RadiologyReport completeRadiologyReport(RadiologyReport radiologyReport, Provider principalResultsInterpreter);
    
    /**
     * Get the {@code RadiologyReport} by its {@code reportId}.
     *
     * @param reportId the report id of the wanted radiology report
     * @return the radiology report matching given report id
     * @throws IllegalArgumentException if given null
     * @should return radiology report matching given report id
     * @should return null if no match was found
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
    public RadiologyReport getRadiologyReport(Integer reportId);
    
    /**
     * Get the {@code RadiologyReport} by its {@code UUID}.
     *
     * @param uuid the uuid of the radiology report
     * @return the radiology report matching given uuid
     * @throws IllegalArgumentException if given null
     * @should return radiology report matching given uuid
     * @should return null if no match was found
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
    public RadiologyReport getRadiologyReportByUuid(String uuid);
    
    /**
     * Get the {@code RadiologyReport's} associated with a {@code RadiologyOrder} and a specific status.
     *
     * @param radiologyOrder the radiology order for which the radiology reports should be returned
     * @param reportStatus the status the radiology report should have
     * @return the radiology reports associated with given radiology order and matching given report status
     * @throws IllegalArgumentException if given radiologyOrder is null
     * @throws IllegalArgumentException if given reportStatus is null
     * @should return list of claimed radiology reports if report status is claimed
     * @should return list of completed radiology reports if report status is completed
     * @should return list of discontinued radiology reports if report status is discontinued
     * @should return empty list given radiology order without associated radiology reports
     * @should throw illegal argument exception if given radiology order is null
     * @should throw illegal argument exception if given report status is null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
    public List<RadiologyReport> getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder radiologyOrder,
            RadiologyReportStatus reportStatus);
    
    /**
     * Check if a {@code RadiologyOrder} has a claimed {@code RadiologyReport}.
     *
     * @param radiologyOrder the radiology order which should be checked for a claimed report
     * @return true if the radiology order has a claimed report and false otherwise
     * @throws IllegalArgumentException if given null
     * @should return true if given radiology order has a claimed radiology report
     * @should return false if given radiology order has no claimed radiology report
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
    public boolean hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder radiologyOrder);
    
    /**
     * Check if a {@code RadiologyOrder} has a completed {@code RadiologyReport}.
     *
     * @param radiologyOrder the radiology order which should be checked for a completed report
     * @return true if the radiology order has a completed report and false otherwise
     * @throws IllegalArgumentException if given null
     * @should return true if given radiology order has a completed radiology report
     * @should return false if given radiology order has no completed radiology report
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
    public boolean hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder radiologyOrder);
    
    /**
     * Get the active (can be claimed or completed) RadiologyReport matching the radiologyOrder
     *
     * @param radiologyOrder RadiologyOrder the radiologyOrder which should be checked
     * @return RadiologyReport filtered by radiologyOrder and radiologyReportStatus not equal to
     *         discontinued
     * @throws IllegalArgumentException if given null
     * @should return a radiology report if given radiology order is associated with a report with status claimed
     * @should return a radiology report if given radiology order is associated with a report with status completed
     * @should return null if given radiology order is only associated with a report with status discontinued
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
    public RadiologyReport getActiveRadiologyReportByRadiologyOrder(RadiologyOrder radiologyOrder);
    
    /**
     * Get all {@code RadiologyReport's} matching a variety of (nullable) criteria.
     * Each extra value for a parameter that is provided acts as an "and" and will reduce the number of results returned
     *
     * @param radiologyReportSearchCriteria the object containing search parameters
     * @return the radiology reports matching given criteria ordered by increasing report date
     * @throws IllegalArgumentException if given null
     * @should return all radiology reports (including discontinued) matching the search query if include discontinued is set
     * @should return all radiology reports within given date range if date to and date from are specified
     * @should return all radiology reports with report date after or equal to from date if only date from is specified
     * @should return all radiology reports with report date before or equal to to date if only date to is specified
     * @should return empty list if from date after to date
     * @should return empty search result if no report is in date range
     * @should return all radiology reports for given principal results interpreter
     * @should return empty search result if no report exists for principal results interpreter
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
    public List<RadiologyReport> getRadiologyReports(RadiologyReportSearchCriteria radiologyReportSearchCriteria);
}
