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
import org.openmrs.Obs;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.db.StudyDAO;

public class StudyDAOImpl implements StudyDAO {
	
	private SessionFactory sessionFactory;
	
	/**
	 * This is a Hibernate object. It gives us metadata about the currently
	 * connected database, the current session, the current db user, etc. To
	 * save and get objects, calls should go through
	 * sessionFactory.getCurrentSession() <br/>
	 * <br/>
	 * This is called by Spring. See the /metadata/moduleApplicationContext.xml
	 * for the "sessionFactory" setting. See the applicationContext-service.xml
	 * file in CORE openmrs for where the actual "sessionFactory" object is
	 * first defined.
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Study getStudy(Integer id) {
		return (Study) sessionFactory.getCurrentSession().get(Study.class, id);
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.radiology.db.StudyDAO#getStudyByOrderId(java.lang.Integer)
	 */
	public Study getStudyByOrderId(Integer id) {
		String query = "from Study s where s.orderID = '" + id + "'";
		Study study = (Study) sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
		return study == null ? new Study() : study;
	}
	
	public Study saveStudy(Study s) {
		sessionFactory.getCurrentSession().saveOrUpdate(s);
		return s;
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
