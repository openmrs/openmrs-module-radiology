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
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
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
	
	public Study getStudy(Integer id) {
		return (Study) sessionFactory.getCurrentSession().get(Study.class, id);
	}
	
	/**
	 * @see StudyDAO#getStudyByUid(String)
	 */
	@Override
	public Study getStudyByUid(String uid) {
		return (Study) sessionFactory.getCurrentSession().createCriteria(Study.class).add(Restrictions.eq("uid", uid))
		        .uniqueResult();
	}
	
	/**
	 * @see StudyDAO#getStudyByOrderId(Integer)
	 */
	public Study getStudyByOrderId(Integer orderId) {
		String query = "select study from Study as study join study.order as order where order.orderId = :orderId";
		
		Study study;
		study = (Study) sessionFactory.getCurrentSession().createQuery(query).setParameter("orderId", orderId)
		        .uniqueResult();
		
		return study == null ? new Study() : study;
	}
	
	/**
	 * @see StudyDAO#getStudyByOrder(Order)
	 */
	@Override
	public Study getStudyByOrder(Order order) {
		return (Study) sessionFactory.getCurrentSession().createCriteria(Study.class).add(Restrictions.eq("order", order))
		        .uniqueResult();
	}
	
	/**
	 * @see StudyDAO#getStudiesByPatient(Patient)
	 */
	@Override
	public List<Study> getStudiesByPatient(Patient patient) {
		String query = "select study from Study as study join study.order as order where order.patient = :patient";
		
		List<Study> studies;
		
		studies = sessionFactory.getCurrentSession().createQuery(query).setParameter("patient", patient).list();
		
		return studies;
	}
	
	/**
	 * @see StudyDAO#getStudiesByOrders(List<Order>)
	 */
	@Override
	public List<Study> getStudiesByOrders(List<Order> orders) {
		String query = "select study from Study as study where study.order in (:orders)";
		
		List<Study> studies;
		
		studies = sessionFactory.getCurrentSession().createQuery(query).setParameterList("orders", orders).list();
		
		return studies;
	}
	
	/**
	 * @see StudyDAO#getObservationsByStudy(Study)
	 */
	//TODO(teleivo) just moved the query from Study to the api part. Find out what exactly this query is supposed to do and make it cleaner and add more tests to api 
	@Override
	public List<Obs> getObservationsByStudy(Study study) {
		String innerQuery = "(Select oo.previousVersion from Obs as oo where oo.order.orderId="
		        + study.getOrder().getOrderId() + " and oo.previousVersion IS NOT NULL)";
		String query = "from Obs as o where o.order.orderId = " + study.getOrder().getOrderId() + " and o.obsId NOT IN "
		        + innerQuery;
		
		List<Obs> observations;
		
		observations = sessionFactory.getCurrentSession().createQuery(query).list();
		
		return observations;
	}
	
	/**
	 * @see StudyDAO#saveStudy(Study)
	 */
	@Override
	public Study saveStudy(Study s) {
		sessionFactory.getCurrentSession().saveOrUpdate(s);
		return s;
	}
	
}
