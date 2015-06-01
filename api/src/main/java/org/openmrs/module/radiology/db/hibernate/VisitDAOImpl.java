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
