/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import org.hibernate.SessionFactory;

/**
 * Hibernate specific MrrtReportTemplate related functions. This class should not be used directly. All
 * calls should go through the {@link org.openmrs.module.radiology.report.template.MrrtReportTemplateService} methods.
 *
 * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateDAO
 * @see org.openmrs.module.radiology.order.template.MrrtReportTemplateService
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
     * @see org.openmrs.module.radiology.report.template.MrrtReportTemplateService#getSaveMrrtReportTemplate(MrrtReportTemplate)
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
    
}
