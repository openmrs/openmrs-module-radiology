/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Hibernate specific MrrtReportTemplate related functions. This class should not be used directly. All
 * calls should go through the {@link org.openmrs.module.radiology.report.template.MrrtReportTemplateService} methods.
 *
 * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateDAO
 * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService
 */
class HibernateMrrtReportTemplateDAO implements MrrtReportTemplateDAO {
    
    
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
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplate(Integer)
     */
    @Override
    public MrrtReportTemplate getMrrtReportTemplate(Integer templateId) {
        return (MrrtReportTemplate) sessionFactory.getCurrentSession()
                .get(MrrtReportTemplate.class, templateId);
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplateByUuid(String)
     */
    @Override
    public MrrtReportTemplate getMrrtReportTemplateByUuid(String uuid) {
        final Criteria criteria = createMrrtReportTemplateCriteria();
        criteria.add(Restrictions.eq("uuid", uuid));
        return (MrrtReportTemplate) criteria.uniqueResult();
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplateByIdentifier(String)
     */
    @Override
    public MrrtReportTemplate getMrrtReportTemplateByIdentifier(String identifier) {
        final Criteria criteria = createMrrtReportTemplateCriteria();
        criteria.add(Restrictions.eq("dcTermsIdentifier", identifier));
        return (MrrtReportTemplate) criteria.uniqueResult();
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MrrtReportTemplate> getMrrtReportTemplates(MrrtReportTemplateSearchCriteria searchCriteria) {
        
        final Criteria crit = sessionFactory.getCurrentSession()
                .createCriteria(MrrtReportTemplate.class);
        crit.addOrder(Order.asc("dcTermsTitle"));
        
        if (searchCriteria.getTitle() != null) {
            crit.add(Restrictions.ilike("dcTermsTitle", searchCriteria.getTitle() + "%", MatchMode.ANYWHERE));
        }
        if (searchCriteria.getPublisher() != null) {
            crit.add(Restrictions.ilike("dcTermsPublisher", searchCriteria.getPublisher() + "%", MatchMode.ANYWHERE));
        }
        if (searchCriteria.getLicense() != null) {
            crit.add(Restrictions.ilike("dcTermsLicense", searchCriteria.getLicense() + "%", MatchMode.ANYWHERE));
        }
        if (searchCriteria.getCreator() != null) {
            crit.add(Restrictions.ilike("dcTermsCreator", searchCriteria.getCreator() + "%", MatchMode.ANYWHERE));
        }
        final List<MrrtReportTemplate> result = (List<MrrtReportTemplate>) crit.list();
        return result == null ? new ArrayList<>() : result;
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
     */
    @Override
    public MrrtReportTemplate saveMrrtReportTemplate(MrrtReportTemplate template) {
        sessionFactory.getCurrentSession()
                .save(template);
        return template;
    }
    
    /**
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#purgeMrrtReportTemplate(MrrtReportTemplate)
     */
    @Override
    public void purgeMrrtReportTemplate(MrrtReportTemplate template) {
        sessionFactory.getCurrentSession()
                .delete(template);
    }
    
    /**
     * A utility method creating a criteria for MrrtReportTemplate
     *
     * @return criteria for MrrtReportTemplate
     */
    private Criteria createMrrtReportTemplateCriteria() {
        return sessionFactory.getCurrentSession()
                .createCriteria(MrrtReportTemplate.class);
    }
}
