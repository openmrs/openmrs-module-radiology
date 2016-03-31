/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.db;

import java.util.List;

import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportStatus;

/**
 * RadiologyReportDAO database functions
 * 
 * @see org.openmrs.module.radiology.RadiologyService
 */
public interface RadiologyReportDAO {
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#getRadiologyReportByRadiologyReportId(Integer)
	 */
	RadiologyReport getRadiologyReportById(Integer radiologyReportId);
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#saveRadiologyReport(RadiologyReport)
	 */
	RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport);
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
	 */
	boolean hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder radiologyOrder);
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
	 */
	boolean hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder radiologyOrder);
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder,
	 *      RadiologyReportStatus)
	 */
	List<RadiologyReport> getRadiologyReportsByRadiologyOrderAndRadiologyReportStatus(RadiologyOrder radiologyOrder,
			RadiologyReportStatus radiologyReportStatus);
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
	 */
	RadiologyReport getActiveRadiologyReportByRadiologyOrder(RadiologyOrder radiologyOrder);
}
