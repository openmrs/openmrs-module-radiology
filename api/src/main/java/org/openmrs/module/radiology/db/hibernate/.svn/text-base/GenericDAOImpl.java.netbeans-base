package org.openmrs.module.radiology.db.hibernate;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.openmrs.module.radiology.db.GenericDAO;

public class GenericDAOImpl implements GenericDAO{

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	
	public Object get(String query,boolean unique) {
		Query query2 = sessionFactory.getCurrentSession().createQuery(query);
		if(unique) return query2.uniqueResult();
		else return query2.list();
	}
	
	public Session session(){ return sessionFactory.getCurrentSession();}
}
