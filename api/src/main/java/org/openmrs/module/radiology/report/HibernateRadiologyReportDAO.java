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

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Hibernate specific RadiologyReport related functions. This class should not be used directly. All
 * calls should go through the {@link org.openmrs.module.radiology.report.RadiologyReportService} methods.
 *
 * @see org.openmrs.module.radiology.report.RadiologyReportDAO
 * @see org.openmrs.module.radiology.report.RadiologyReportService
 */
@Repository
class HibernateRadiologyReportDAO implements RadiologyReportDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory that allows us to connect to the database that Hibernate knows about.
	 *
	 * @param sessionFactory SessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.radiology.report.RadiologyReportService#getRadiologyReportByRadiologyReportId(Integer)
	 */
	@Override
	public RadiologyReport getRadiologyReportById(Integer radiologyReportId) {
		return (RadiologyReport) sessionFactory.getCurrentSession()
				.get(RadiologyReport.class, radiologyReportId);
	}
	
	/**
	 * @see org.openmrs.module.radiology.report.RadiologyReportService#saveRadiologyReport(RadiologyReport)
	 */
	@Override
	public RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport) {
		sessionFactory.getCurrentSession()
				.saveOrUpdate(radiologyReport);
		return radiologyReport;
	}
	
	/**
	 * @see org.openmrs.module.radiology.report.RadiologyReportService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
	 *      (RadiologyReport)
	 */
	@Override
	public boolean hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder radiologyOrder) {
		final List<RadiologyReport> radiologyReports = sessionFactory.getCurrentSession()
				.createCriteria(RadiologyReport.class)
				.add(Restrictions.eq("radiologyOrder", radiologyOrder))
				.add(Restrictions.eq("reportStatus", RadiologyReportStatus.COMPLETED))
				.list();
		return radiologyReports.size() == 1 ? true : false;
	}
	
	/**
	 * @see org.openmrs.module.radiology.report.RadiologyReportService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
	 */
	public boolean hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder radiologyOrder) {
		final List<RadiologyReport> radiologyReports = sessionFactory.getCurrentSession()
				.createCriteria(RadiologyReport.class)
				.add(Restrictions.eq("radiologyOrder", radiologyOrder))
				.add(Restrictions.eq("reportStatus", RadiologyReportStatus.CLAIMED))
				.list();
		return radiologyReports.size() == 1 ? true : false;
	}
	
	/**
	 * @see org.openmrs.module.radiology.report.RadiologyReportService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder,
	 *      RadiologyReportStatus)
	 */
	@Override
	public List<RadiologyReport> getRadiologyReportsByRadiologyOrderAndRadiologyReportStatus(RadiologyOrder radiologyOrder,
			RadiologyReportStatus radiologyReportStatus) {
		return sessionFactory.getCurrentSession()
				.createCriteria(RadiologyReport.class)
				.add(Restrictions.eq("radiologyOrder", radiologyOrder))
				.add(Restrictions.eq("reportStatus", radiologyReportStatus))
				.list();
	}
	
	/**
	 * @see org.openmrs.module.radiology.report.RadiologyReportService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
	 */
	@Override
	public RadiologyReport getActiveRadiologyReportByRadiologyOrder(RadiologyOrder radiologyOrder) {
		return (RadiologyReport) sessionFactory.getCurrentSession()
				.createCriteria(RadiologyReport.class)
				.add(Restrictions.eq("radiologyOrder", radiologyOrder))
				.add(Restrictions.disjunction()
						.add(Restrictions.eq("reportStatus", RadiologyReportStatus.CLAIMED))
						.add(Restrictions.eq("reportStatus", RadiologyReportStatus.COMPLETED)))
				.list()
				.get(0);
	}
}
