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

import org.openmrs.module.radiology.order.RadiologyOrder;

/**
 * {@code RadiologyReport} related database methods.
 * 
 * @see org.openmrs.module.radiology.report.RadiologyReportService
 * @see org.openmrs.module.radiology.report.RadiologyReport
 */
interface RadiologyReportDAO {
    
    
    /**
     * @see org.openmrs.module.radiology.report.RadiologyReportService#saveRadiologyReportDraft(RadiologyReport)
     */
    RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport);
    
    /**
     * @see org.openmrs.module.radiology.report.RadiologyReportService#getRadiologyReport(Integer)
     */
    RadiologyReport getRadiologyReport(Integer reportId);
    
    /**
     * @see org.openmrs.module.radiology.report.RadiologyReportService#getRadiologyReportByUuid(String)
     */
    RadiologyReport getRadiologyReportByUuid(String radiologyReportUuid);
    
    /**
     * @see org.openmrs.module.radiology.report.RadiologyReportService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
     */
    boolean hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder radiologyOrder);
    
    /**
     * @see org.openmrs.module.radiology.report.RadiologyReportService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
     */
    boolean hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder radiologyOrder);
    
    /**
     * @see org.openmrs.module.radiology.report.RadiologyReportService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
     */
    RadiologyReport getActiveRadiologyReportByRadiologyOrder(RadiologyOrder radiologyOrder);
    
    /**
     * @see org.openmrs.module.radiology.report.RadiologyReportService#getRadiologyReports(RadiologyReportSearchCriteria)
     */
    List<RadiologyReport> getRadiologyReports(RadiologyReportSearchCriteria searchCriteria);
}
