package org.openmrs.module.radiology.db.hibernate;

import org.hibernate.SessionFactory;
import org.openmrs.module.radiology.Visit;
import org.openmrs.module.radiology.db.VisitDAO;

public class VisitDAOImpl implements VisitDAO {
	private SessionFactory sessionFactory;


	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	@Override
	public Visit getVisit(Integer id) {
		return (Visit) sessionFactory.getCurrentSession().get(Visit.class, id);
	}

	@Override
	public Visit saveVisit(Visit v) {
		sessionFactory.getCurrentSession().saveOrUpdate(v);
		return v;
	}

}
