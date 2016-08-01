/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.GlobalProperty;
import org.openmrs.Order.Urgency;
import org.openmrs.api.APIException;
import org.openmrs.module.radiology.RadiologyConstants;

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
     * @see org.openmrs.module.radiology.order.RadiologyOrderService#getNextAccessionNumberSeedSequenceValue()
     * @throws APIException if global property radiology.nextAccessionNumberSeed is missing
     * @throws APIException if global property radiology.nextAccessionNumberSeed value is empty or only contains whitespaces
     * @throws APIException if global property radiology.nextAccessionNumberSeed value cannot be parsed to Long
     * @should return the next accession number seed stored as global property radiology next accession number and increment
     *         the global property value
     * @should throw an api exception if global property radiology next accession number seed is missing
     * @should throw an api exception if global property radiology next accession number seed value is empty or only contains
     *         whitespaces
     * @should throw an api exception if global property radiology next accession number seed value value cannot be parsed to
     *         long
     */
    @Override
    public Long getNextAccessionNumberSeedSequenceValue() {
        
        final GlobalProperty globalProperty = (GlobalProperty) sessionFactory.getCurrentSession()
                .get(GlobalProperty.class, RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED, LockOptions.UPGRADE);
        
        if (globalProperty == null) {
            throw new APIException("GlobalProperty.missing",
                    new Object[] { RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED });
        }
        
        final String gpTextValue = globalProperty.getPropertyValue();
        if (StringUtils.isBlank(gpTextValue)) {
            throw new APIException("GlobalProperty.invalid.value",
                    new Object[] { RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED });
        }
        
        Long globalPropertyValue = null;
        try {
            globalPropertyValue = Long.parseLong(gpTextValue);
        }
        catch (NumberFormatException ex) {
            throw new APIException("GlobalProperty.invalid.value",
                    new Object[] { RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED });
        }
        
        globalProperty.setPropertyValue(String.valueOf(globalPropertyValue + 1));
        
        sessionFactory.getCurrentSession()
                .save(globalProperty);
        
        return globalPropertyValue;
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
     * @see org.openmrs.module.radiology.order.RadiologyOrderService#getRadiologyOrderByUuid(String)
     */
    @Override
    public RadiologyOrder getRadiologyOrderByUuid(String uuid) {
        
        return (RadiologyOrder) sessionFactory.getCurrentSession()
                .createCriteria(RadiologyOrder.class)
                .add(Restrictions.eq("uuid", uuid))
                .uniqueResult();
    }
    
    /**
     * @see org.openmrs.module.radiology.order.RadiologyOrderService#getRadiologyOrders(RadiologyOrderSearchCriteria)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RadiologyOrder> getRadiologyOrders(RadiologyOrderSearchCriteria searchCriteria) {
        
        final Criteria crit = sessionFactory.getCurrentSession()
                .createCriteria(RadiologyOrder.class);
        
        if (searchCriteria.getPatient() != null) {
            crit.add(Restrictions.eq("patient", searchCriteria.getPatient()));
        }
        
        if (!searchCriteria.getIncludeVoided()) {
            crit.add(Restrictions.not(Restrictions.eq("voided", true)));
        }
        
        if (searchCriteria.getUrgency() != null) {
            crit.add(Restrictions.eq("urgency", searchCriteria.getUrgency()));
        }
        if (searchCriteria.getFromEffectiveStartDate() != null) {
            final Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.conjunction()
                    .add(Restrictions.eq("urgency", Urgency.ON_SCHEDULED_DATE))
                    .add(Restrictions.ge("scheduledDate", searchCriteria.getFromEffectiveStartDate())));
            disjunction.add(Restrictions.conjunction()
                    .add(Restrictions.not(Restrictions.eq("urgency", Urgency.ON_SCHEDULED_DATE)))
                    .add(Restrictions.ge("dateActivated", searchCriteria.getFromEffectiveStartDate())));
            crit.add(disjunction);
        }
        
        if (searchCriteria.getToEffectiveStartDate() != null) {
            final Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.conjunction()
                    .add(Restrictions.eq("urgency", Urgency.ON_SCHEDULED_DATE))
                    .add(Restrictions.le("scheduledDate", searchCriteria.getToEffectiveStartDate())));
            disjunction.add(Restrictions.conjunction()
                    .add(Restrictions.not(Restrictions.eq("urgency", Urgency.ON_SCHEDULED_DATE)))
                    .add(Restrictions.le("dateActivated", searchCriteria.getToEffectiveStartDate())));
            crit.add(disjunction);
        }
        
        if (StringUtils.isNotBlank(searchCriteria.getAccessionNumber())) {
            crit.add(Restrictions.eq("accessionNumber", searchCriteria.getAccessionNumber()));
        }
        
        if (searchCriteria.getOrderer() != null) {
            crit.add(Restrictions.eq("orderer", searchCriteria.getOrderer()));
        }
        
        crit.addOrder(Order.asc("accessionNumber"));
        return crit.list();
    }
}
