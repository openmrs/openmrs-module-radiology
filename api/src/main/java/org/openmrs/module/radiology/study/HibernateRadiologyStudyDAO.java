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

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * Hibernate specific RadiologyStudy related functions. This class should not be used directly. All calls
 * should go through the {@link org.openmrs.module.radiology.study.RadiologyStudyService} methods.
 *
 * @see org.openmrs.module.radiology.study.RadiologyStudyDAO
 * @see org.openmrs.module.radiology.study.RadiologyStudyService
 */
class HibernateRadiologyStudyDAO implements RadiologyStudyDAO {
    
    
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
     * @see org.openmrs.module.radiology.study.RadiologyStudyService#saveRadiologyStudy(RadiologyStudy)
     */
    @Override
    public RadiologyStudy saveRadiologyStudy(RadiologyStudy radiologyStudy) {
        sessionFactory.getCurrentSession()
                .saveOrUpdate(radiologyStudy);
        return radiologyStudy;
    }
    
    /**
     * @see org.openmrs.module.radiology.study.RadiologyStudyService#getRadiologyStudy(Integer)
     */
    @Override
    public RadiologyStudy getRadiologyStudy(Integer studyId) {
        return (RadiologyStudy) sessionFactory.getCurrentSession()
                .get(RadiologyStudy.class, studyId);
    }
    
    /**
     * @see org.openmrs.module.radiology.study.RadiologyStudyService#getRadiologyStudyByUuid(String)
     */
    @Override
    public RadiologyStudy getRadiologyStudyByUuid(String uuid) {
        
        return (RadiologyStudy) sessionFactory.getCurrentSession()
                .createCriteria(RadiologyStudy.class)
                .add(Restrictions.eq("uuid", uuid))
                .uniqueResult();
    }
    
    /**
     * @see org.openmrs.module.radiology.study.RadiologyStudyService#getRadiologyStudyByStudyInstanceUid(String)
     */
    @Override
    public RadiologyStudy getRadiologyStudyByStudyInstanceUid(String studyInstanceUid) {
        return (RadiologyStudy) sessionFactory.getCurrentSession()
                .createCriteria(RadiologyStudy.class)
                .add(Restrictions.eq("studyInstanceUid", studyInstanceUid))
                .uniqueResult();
    }
}
