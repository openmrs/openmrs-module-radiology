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

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Obs;
import org.openmrs.Order;
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
	public Study getStudy(Integer id) {
		return (Study) sessionFactory.getCurrentSession().get(Study.class, id);
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
		String query = "from Study s where s.orderId = '" + orderId + "'";
		Study study = (Study) sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
		return study == null ? new Study() : study;
	}
	
	/**
	 * @see StudyDAO#getStudiesByOrders(List<Order>)
	 */
	@Override
	public List<Study> getStudiesByOrders(List<Order> orders) {
		String query = "select study from Study as study where study.orderId in (:orderIds)";
		
		List<Integer> orderIds = new ArrayList<Integer>();
		for (Order order : orders) {
			orderIds.add(order.getOrderId());
		}
		
		List<Study> studies = new ArrayList<Study>();
		
		if (orderIds.size() > 0) {
			studies = sessionFactory.getCurrentSession().createQuery(query).setParameterList("orderIds", orderIds).list();
		}
		
		return studies;
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
