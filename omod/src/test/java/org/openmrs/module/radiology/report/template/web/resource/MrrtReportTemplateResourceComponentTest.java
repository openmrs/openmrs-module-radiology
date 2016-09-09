/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template.web.resource;

import org.junit.Before;
import org.openmrs.module.radiology.report.template.MrrtReportTemplate;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link MrrtReportTemplateResource}
 */
public class MrrtReportTemplateResourceComponentTest
        extends BaseDelegatingResourceTest<MrrtReportTemplateResource, MrrtReportTemplate> {
    
    
    protected static final String TEST_DATASET = "MrrtReportTemplateResourceComponentTestDataset.xml";
    
    @Autowired
    MrrtReportTemplateService mrrtReportTemplateService;
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see BaseDelegatingResourceTest#getDisplayProperty()
     */
    @Override
    public String getDisplayProperty() {
        return "org/radrep/0001";
    }
    
    /**
     * @see BaseDelegatingResourceTest#getUuidProperty()
     */
    @Override
    public String getUuidProperty() {
        return "aa551445-def0-4f93-9047-95f0a9afbdce";
    }
    
    /**
     * @see BaseDelegatingResourceTest#newObject()
     */
    @Override
    public MrrtReportTemplate newObject() {
        return mrrtReportTemplateService.getMrrtReportTemplateByUuid(getUuidProperty());
    }
    
    /**
     * @see BaseDelegatingResourceTest#validateDefaultRepresentation()
     */
    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropPresent("uuid");
        assertPropPresent("templateId");
        assertPropPresent("dcTermsIdentifier");
        assertPropPresent("dcTermsTitle");
        assertPropPresent("dcTermsType");
        assertPropPresent("dcTermsPublisher");
        assertPropPresent("dcTermsCreator");
        assertPropPresent("dcTermsRights");
        assertPropPresent("terms");
        assertPropPresent("display");
    }
    
    /**
     * @see BaseDelegatingResourceTest#validateFullRepresentation()
     */
    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropPresent("uuid");
        assertPropPresent("charset");
        assertPropPresent("templateId");
        assertPropPresent("dcTermsIdentifier");
        assertPropPresent("dcTermsTitle");
        assertPropPresent("dcTermsDescription");
        assertPropPresent("dcTermsType");
        assertPropPresent("dcTermsLanguage");
        assertPropPresent("dcTermsPublisher");
        assertPropPresent("dcTermsCreator");
        assertPropPresent("dcTermsRights");
        assertPropPresent("dcTermsLicense");
        assertPropPresent("dcTermsDate");
        assertPropPresent("terms");
        assertPropPresent("display");
    }
}
