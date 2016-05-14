/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Hibernate specific RadiologyStudy related functions. This class should not be used directly. All calls
 * should go through the {@link org.openmrs.module.radiology.study.RadiologyStudyService} methods.
 *
 * @see org.openmrs.module.radiology.study.RadiologyStudyDAO
 * @see org.openmrs.module.radiology.study.RadiologyStudyService
 */
@Repository
class HibernateRadiologyStudyDAO implements RadiologyStudyDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory that allows us to connect to the database that Hibernate knows about.
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.radiology.study.RadiologyStudyService#saveStudy(RadiologyStudy)
	 */
	@Override
	public RadiologyStudy saveStudy(RadiologyStudy radiologyStudy) {
		sessionFactory.getCurrentSession()
				.saveOrUpdate(radiologyStudy);
		return radiologyStudy;
	}
	
	/**
	 * @see org.openmrs.module.radiology.study.RadiologyStudyService#getStudyByStudyId(Integer)
	 */
	@Override
	public RadiologyStudy getStudyByStudyId(Integer studyId) {
		return (RadiologyStudy) sessionFactory.getCurrentSession()
				.get(RadiologyStudy.class, studyId);
	}
	
	/**
	 * @see org.openmrs.module.radiology.study.RadiologyStudyService#getStudyByOrderId(Integer)
	 */
	@Override
	public RadiologyStudy getStudyByOrderId(Integer orderId) {
		final String query = "from RadiologyStudy s where s.radiologyOrder.orderId = '" + orderId + "'";
		return (RadiologyStudy) sessionFactory.getCurrentSession()
				.createQuery(query)
				.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.radiology.study.RadiologyStudyService#getStudyByStudyInstanceUid(String)
	 */
	@Override
	public RadiologyStudy getStudyByStudyInstanceUid(String studyInstanceUid) {
		return (RadiologyStudy) sessionFactory.getCurrentSession()
				.createCriteria(RadiologyStudy.class)
				.add(Restrictions.eq("studyInstanceUid", studyInstanceUid))
				.uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.radiology.study.RadiologyStudyService#getStudiesByRadiologyOrders(List
	 *      <RadiologyOrder> radiologyOrders)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RadiologyStudy> getStudiesByRadiologyOrders(List<RadiologyOrder> radiologyOrders) {
		List<RadiologyStudy> result = new ArrayList<RadiologyStudy>();
		if (!radiologyOrders.isEmpty()) {
			Criteria studyCriteria = sessionFactory.getCurrentSession()
					.createCriteria(RadiologyStudy.class);
			addRestrictionOnRadiologyOrders(studyCriteria, radiologyOrders);
			result = (List<RadiologyStudy>) studyCriteria.list();
		}
		return result == null ? new ArrayList<RadiologyStudy>() : result;
	}
	
	/**
	 * Adds an in restriction for given radiologyOrders on given criteria if radiologyOrders is not
	 * empty
	 *
	 * @param criteria criteria on which in restriction is set if radiologyOrders is not empty
	 * @param radiologyOrders radiology order list for which in restriction will be set
	 */
	private void addRestrictionOnRadiologyOrders(Criteria criteria, List<RadiologyOrder> radiologyOrders) {
		if (!radiologyOrders.isEmpty()) {
			criteria.add(Restrictions.in("radiologyOrder", radiologyOrders));
		}
	}
}
