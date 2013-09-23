package org.openmrs.module.radiology.db.hibernate;

import org.hibernate.SessionFactory;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Utils;
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
		String query = "from Study s where s.orderID = '"+id+"'";
		Study study = (Study) sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
		return study==null ? new Study() : study;
	}

	public Study saveStudy(Study s) {
		sessionFactory.getCurrentSession().saveOrUpdate(s);
		return s;
	}
		
}
