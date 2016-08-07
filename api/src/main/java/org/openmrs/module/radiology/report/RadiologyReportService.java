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

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.RadiologyPrivileges;
import org.openmrs.module.radiology.order.RadiologyOrder;

/**
 * Service layer for {@code RadiologyReport}.
 *
 * <p>Typical usage involves:
 * <ol>
 * <li>Create a new {@code RadiologyReport} for a completed {@code RadiologyOrder}.
 * <pre>{@code
 * RadiologyReport radiologyReport = RadiologyReportService.createRadiologyReport(RadiologyOrder);
 * }</pre>
 * </li>
 * <li>Set fields like for example {@code radiologyReport.setBody("Fracture of around 5mm visible in right tibia.")} through the setters of {@link RadiologyReport}.</li>
 * <li>Optionally, save the {@code RadiologyReport} as a draft via {@link #saveRadiologyReportDraft(RadiologyReport)}.</li>
 * <li>Optionally, void the {@code RadiologyReport} via {@link #voidRadiologyReport(RadiologyReport, String)}.</li>
 * <li>Finally, complete the {@code RadiologyReport} via {@link #saveRadiologyReport(RadiologyReport)}.</li>
 * </ol>
 *
 * @see org.openmrs.module.radiology.report.RadiologyReport
 */
public interface RadiologyReportService extends OpenmrsService {
    
    
    /**
     * Saves a new {@code RadiologyReport} to the database and sets its status to claimed.
     *
     * @param radiologyOrder the radiology order for which a radiology report will be created
     * @return the created radiology report
     * @throws IllegalArgumentException if given null
     * @throws APIException if RadiologyStudy of given radiologyOrder is not completed
     * @throws APIException if given radiologyOrder has a claimed RadiologyReport
     * @throws APIException if given radiologyOrder has a completed RadiologyReport
     * @should create a radiology order with report status claimed given a completed radiology order
     * @should throw illegal argument exception if given null
     * @should throw api exception if given radiology order is not completed
     * @should throw api exception if given order has a claimed radiology report
     * @should throw api exception if given order has a completed radiology report
     */
    @Authorized(RadiologyPrivileges.ADD_RADIOLOGY_REPORTS)
    public RadiologyReport createRadiologyReport(RadiologyOrder radiologyOrder);
    
    /**
     * Saves an existing {@code RadiologyReport} which is in a draft state to the database.
     * <p>
     * A {@code RadiologyReport} is considered a draft as long as its status is {@code DRAFT}.
     * </p>
     * @param radiologyReport the existing radiology report to be saved
     * @return the saved radiology report
     * @throws IllegalArgumentException if given null
     * @throws IllegalArgumentException if radiologyReport reportId is null
     * @throws APIException if radiologyReport is completed
     * @throws APIException if radiologyReport is voided
     * @throws APIException if radiologyReport.radiologyOrder has a completed RadiologyReport
     * @should save existing radiology report to the database and return it
     * @should throw illegal argument exception if given null
     * @should throw illegal argument exception if given radiology report with reportId null
     * @should throw api exception if radiology report is completed
     * @should throw api exception if radiology report is voided
     * @should throw api exception if given radiology reports order has a completed radiology report
     */
    @Authorized(RadiologyPrivileges.EDIT_RADIOLOGY_REPORTS)
    public RadiologyReport saveRadiologyReportDraft(RadiologyReport radiologyReport);
    
    /**
     * Marks a {@code RadiologyReport} as voided.
     *
     * @param radiologyReport the radiology report to be voided
     * @param voidReason the reason why the radiology report should be voided
     * @return the voided radiology report
     * @throws IllegalArgumentException if given null
     * @throws IllegalArgumentException if radiologyReport reportId is null
     * @throws IllegalArgumentException if voidReason is null or contains only whitespaces
     * @throws APIException if radiologyReport is completed
     * @should void the given radiology report
     * @should throw illegal argument exception if given radiology report is null
     * @should throw illegal argument exception if given radiology report with reportId null
     * @should throw illegal argument exception if given void reason is null or contains only whitespaces
     * @should throw api exception if radiology report is completed
     */
    @Authorized(RadiologyPrivileges.DELETE_RADIOLOGY_REPORTS)
    public RadiologyReport voidRadiologyReport(RadiologyReport radiologyReport, String voidReason);
    
    /**
     * Saves an existing {@code RadiologyReport} and and sets its status to completed.
     *
     * @param radiologyReport the radiology report to be saved and completed
     * @return the saved radiology report
     * @throws IllegalArgumentException if radiologyReport is null
     * @throws IllegalArgumentException if radiologyReport reportId is null
     * @throws IllegalArgumentException if radiologyReport status is null
     * @throws APIException if radiologyReport is completed
     * @throws APIException if radiologyReport is voided
     * @throws APIException if radiologyReport is not valid
     * @should set the report date of the radiology report to the day the radiology report was completed
     * @should set the radiology report status to complete
     * @should throw illegal argument exception if given radiology report is null
     * @should throw illegal argument exception if given radiology report with reportId null
     * @should throw illegal argument exception if given radiology report with status null
     * @should throw api exception if radiology report is completed
     * @should throw api exception if radiology report is voided
     * @should throw api exception if radiology report is not valid
     */
    @Authorized(RadiologyPrivileges.EDIT_RADIOLOGY_REPORTS)
    public RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport);
    
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
     * Check if a {@code RadiologyOrder} has a claimed {@code RadiologyReport}.
     *
     * @param radiologyOrder the radiology order which should be checked for a claimed report
     * @return true if the radiology order has a claimed report and false otherwise
     * @throws IllegalArgumentException if given null
     * @should return true if given radiology order has a claimed radiology report that is not voided
     * @should return false if given radiology order has no claimed radiology report that is not voided
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
     * Get the active (can be a non-voided draft or completed) {@code RadiologyReport} for a {@code RadiologyOrder}.
     *
     * @param radiologyOrder the radiology order for which an active report should be returned
     * @return the active radiology report associated with given radiology order
     * @throws IllegalArgumentException if given null
     * @should return a radiology report if given radiology order is associated with a report with status claimed
     * @should return a radiology report if given radiology order is associated with a report with status completed
     * @should return null if given radiology order is only associated with a voided report
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
     * @should return all radiology reports (including voided) matching the search query if include voided is set
     * @should return all radiology reports within given date range if date to and date from are specified
     * @should return all radiology reports with report date after or equal to from date if only date from is specified
     * @should return all radiology reports with report date before or equal to to date if only date to is specified
     * @should return empty list if from date after to date
     * @should return empty search result if no report is in date range
     * @should return all radiology reports for given principal results interpreter
     * @should return empty search result if no report exists for principal results interpreter
     * @should return all radiology reports with given status
     * @should return empty search result if no report exists for given status
     * @should throw illegal argument exception if given null
     */
    @Authorized(RadiologyPrivileges.GET_RADIOLOGY_REPORTS)
    public List<RadiologyReport> getRadiologyReports(RadiologyReportSearchCriteria radiologyReportSearchCriteria);
}
