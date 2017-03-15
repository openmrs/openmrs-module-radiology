/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * Hibernate specific RadiologyModality related functions. This class should not be used directly. All
 * calls should go through the {@link RadiologyModalityService} methods.
 *
 * @see RadiologyModalityDAO
 * @see RadiologyModalityService
 */
class HibernateRadiologyModalityDAO implements RadiologyModalityDAO {
    
    
    private SessionFactory sessionFactory;
    
    /**
     * Set session factory that allows us to connect to the database that Hibernate knows about.
     *
     * @param sessionFactory SessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * @see RadiologyModalityService#saveRadiologyModality(RadiologyModality)
     */
    @Override
    public RadiologyModality saveRadiologyModality(RadiologyModality radiologyModality) {
        sessionFactory.getCurrentSession()
                .saveOrUpdate(radiologyModality);
        return radiologyModality;
    }
    
    /**
     * @see RadiologyModalityService#getRadiologyModality(Integer)
     */
    @Override
    public RadiologyModality getRadiologyModality(Integer id) {
        return (RadiologyModality) sessionFactory.getCurrentSession()
                .get(RadiologyModality.class, id);
    }
    
    /**
     * @see RadiologyModalityService#getRadiologyModalityByUuid(String)
     */
    @Override
    public RadiologyModality getRadiologyModalityByUuid(String uuid) {
        return (RadiologyModality) sessionFactory.getCurrentSession()
                .createCriteria(RadiologyModality.class)
                .add(Restrictions.eq("uuid", uuid))
                .uniqueResult();
    }
    
    /**
     * @see RadiologyModalityService#getRadiologyModalities(boolean)
     */
    @Override
    public List<RadiologyModality> getRadiologyModalities(boolean includeRetired) {
        
        final Criteria criteria = sessionFactory.getCurrentSession()
                .createCriteria(RadiologyModality.class);
        if (!includeRetired) {
            criteria.add(Restrictions.eq("retired", false));
        }
        final List<RadiologyModality> result = (List<RadiologyModality>) criteria.list();
        return result == null ? new ArrayList<>() : result;
    }
}
