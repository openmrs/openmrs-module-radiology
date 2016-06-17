/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;

/**
 * Hibernate specific RadiologyOrder related functions. This class should not be used directly. All
 * calls should go through the {@link org.openmrs.module.radiology.order.RadiologyOrderService} methods.
 *
 * @see org.openmrs.module.radiology.order.RadiologyOrderDAO
 * @see org.openmrs.module.radiology.order.RadiologyOrderService
 */
class HibernateRadiologyOrderDAO implements RadiologyOrderDAO {
    
    
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
     * @see org.openmrs.module.radiology.order.RadiologyOrderService#getRadiologyOrder(Integer)
     */
    @Override
    public RadiologyOrder getRadiologyOrder(Integer orderId) {
        return (RadiologyOrder) sessionFactory.getCurrentSession()
                .get(RadiologyOrder.class, orderId);
    }
    
    /**
     * @see org.openmrs.module.radiology.order.RadiologyOrderService#getRadiologyOrdersByPatient(Patient)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RadiologyOrder> getRadiologyOrdersByPatient(Patient patient) {
        
        final Criteria radiologyOrderCriteria = createRadiologyOrderCriteria();
        addRestrictionOnPatient(radiologyOrderCriteria, patient);
        
        final List<RadiologyOrder> result = (List<RadiologyOrder>) radiologyOrderCriteria.list();
        return result == null ? new ArrayList<RadiologyOrder>() : result;
    }
    
    /**
     * A utility method creating a criteria for RadiologyOrder
     *
     * @return criteria for RadiologyOrder
     */
    private Criteria createRadiologyOrderCriteria() {
        return sessionFactory.getCurrentSession()
                .createCriteria(RadiologyOrder.class);
    }
    
    /**
     * Adds an equality restriction for given patient on given criteria if patient is not null
     *
     * @param criteria criteria on which equality restriction is set if patient is not null
     * @param patient patient for which equality restriction will be set
     */
    private void addRestrictionOnPatient(Criteria criteria, Patient patient) {
        if (patient != null) {
            criteria.add(Restrictions.eq("patient", patient));
        }
    }
    
    /**
     * @see org.openmrs.module.radiology.order.RadiologyOrderService#getRadiologyOrdersByPatients
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RadiologyOrder> getRadiologyOrdersByPatients(List<Patient> patients) {
        
        final Criteria radiologyOrderCriteria = createRadiologyOrderCriteria();
        addRestrictionOnPatients(radiologyOrderCriteria, patients);
        
        final List<RadiologyOrder> result = (List<RadiologyOrder>) radiologyOrderCriteria.list();
        return result == null ? new ArrayList<RadiologyOrder>() : result;
    }
    
    /**
     * Adds an in restriction for given patients on given criteria if patients is not empty
     *
     * @param criteria criteria on which in restriction is set if patients is not empty
     * @param patients patient list for which in restriction will be set
     */
    private void addRestrictionOnPatients(Criteria criteria, List<Patient> patients) {
        if (!patients.isEmpty()) {
            criteria.add(Restrictions.in("patient", patients));
        }
    }
}
