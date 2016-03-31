/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.db.hibernate;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.db.RadiologyReportDAO;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportStatus;

/**
 * Hibernate specific RadiologyReport related functions. This class should not be used directly. All
 * calls should go through the {@link org.openmrs.module.radiology.RadiologyService} methods.
 *
 * @see org.openmrs.module.radiology.db.RadiologyReportDAO
 * @see org.openmrs.module.radiology.RadiologyService
 */
public class RadiologyReportDAOImpl implements RadiologyReportDAO {
	
	/**
	 * Set session factory that allows us to connect to the database that Hibernate knows about.
	 *
	 * @param sessionFactory SessionFactory
	 */
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#getRadiologyOrderByOrderId(Integer)
	 */
	@Override
	public RadiologyReport getRadiologyReportById(Integer radiologyReportId) {
		return (RadiologyReport) sessionFactory.getCurrentSession()
				.get(RadiologyReport.class, radiologyReportId);
	}
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#saveRadiologyReport(RadiologyReport)
	 */
	@Override
	public RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport) {
		sessionFactory.getCurrentSession()
				.saveOrUpdate(radiologyReport);
		return radiologyReport;
	}
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
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
	 * @see org.openmrs.module.radiology.db.RadiologyReportDAO#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
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
	 * @see org.openmrs.module.radiology.db.RadiologyReportDAO#getRadiologyReportsByRadiologyOrderAndRadiologyReportStatus(RadiologyOrder,
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
	 * @see org.openmrs.module.radiology.RadiologyService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
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
