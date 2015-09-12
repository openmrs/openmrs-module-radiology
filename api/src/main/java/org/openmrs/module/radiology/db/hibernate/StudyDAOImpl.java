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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Obs;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.db.StudyDAO;

public class StudyDAOImpl implements StudyDAO {
	
	private SessionFactory sessionFactory;
	
	/**
	 * This is a Hibernate object. It gives us metadata about the currently connected database, the
	 * current session, the current db user, etc. To save and get objects, calls should go through
	 * sessionFactory.getCurrentSession() <br/>
	 * <br/>
	 * This is called by Spring. See the /metadata/moduleApplicationContext.xml for the
	 * "sessionFactory" setting. See the applicationContext-service.xml file in CORE openmrs for
	 * where the actual "sessionFactory" object is first defined.
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public Study getStudy(Integer studyId) {
		return (Study) sessionFactory.getCurrentSession().get(Study.class, studyId);
	}
	
	/**
	 * @see StudyDAO#getStudyByStudyInstanceUid(String)
	 */
	@Override
	public Study getStudyByStudyInstanceUid(String studyInstanceUid) {
		return (Study) sessionFactory.getCurrentSession().createCriteria(Study.class).add(
		    Restrictions.eq("studyInstanceUid", studyInstanceUid)).uniqueResult();
	}
	
	/**
	 * @see StudyDAO#getStudyByOrderId(Integer)
	 */
	@Override
	public Study getStudyByOrderId(Integer orderId) {
		String query = "from Study s where s.radiologyOrder.orderId = '" + orderId + "'";
		return (Study) sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	/**
	 * @see StudyDAO#getStudiesByRadiologyOrders(List<RadiologyOrder>)
	 */
	@Override
	public List<Study> getStudiesByRadiologyOrders(List<RadiologyOrder> radiologyOrders) {
		List<Study> result = new ArrayList<Study>();
		
		if (radiologyOrders.size() > 0) {
			Criteria studyCriteria = sessionFactory.getCurrentSession().createCriteria(Study.class);
			addRestrictionOnRadiologyOrders(studyCriteria, radiologyOrders);
			result = (List<Study>) studyCriteria.list();
		}
		
		return result;
	}
	
	/**
	 * Adds an in restriction for given radiologyOrders on given criteria if radiologyOrders is not
	 * empty
	 *
	 * @param criteria criteria on which in restriction is set if radiologyOrders is not empty
	 * @param radiologyOrders radiology order list for which in restriction will be set
	 */
	private void addRestrictionOnRadiologyOrders(Criteria criteria, List<RadiologyOrder> radiologyOrders) {
		if (radiologyOrders.size() > 0)
			criteria.add(Restrictions.in("radiologyOrder", radiologyOrders));
	}
	
	/**
	 * @see StudyDAO#saveStudy(Study)
	 */
	@Override
	public Study saveStudy(Study study) {
		sessionFactory.getCurrentSession().saveOrUpdate(study);
		return study;
	}
	
	/**
	 * @see StudyDAO#getObsByOrderId(Integer)
	 */
	@Override
	public List<Obs> getObsByOrderId(Integer orderId) {
		String innerQuery = "(Select oo.previousVersion from Obs as oo where oo.order.orderId=" + orderId
		        + " and oo.previousVersion IS NOT NULL)";
		String query = "from Obs as o where o.order.orderId = " + orderId + " and o.obsId NOT IN " + innerQuery;
		
		List<Obs> observations;
		
		observations = sessionFactory.getCurrentSession().createQuery(query).list();
		
		return observations;
	}
	
}
