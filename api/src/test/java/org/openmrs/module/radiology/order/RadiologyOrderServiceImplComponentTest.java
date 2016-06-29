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

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.study.RadiologyStudyService;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link RadiologyOrderServiceImpl}
 */
public class RadiologyOrderServiceImplComponentTest extends BaseModuleContextSensitiveTest {
    
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/RadiologyOrderServiceComponentTestDataset.xml";
    
    private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER = 70011;
    
    private static final String RADIOLOGY_ORDER_PROVIDER_UUID = "c2299800-cca9-11e0-9572-0800200c9a66";
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private EncounterService encounterService;
    
    @Autowired
    private ProviderService providerService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private HibernateRadiologyOrderDAO radiologyOrderDAO = new HibernateRadiologyOrderDAO();
    
    private RadiologyOrderServiceImpl radiologyOrderServiceImpl = null;
    
    @Autowired
    private RadiologyStudyService radiologyStudyService;
    
    @Autowired
    private RadiologyProperties radiologyProperties;
    
    private Method saveRadiologyOrderEncounterMethod = null;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        
        if (radiologyOrderServiceImpl == null) {
            radiologyOrderServiceImpl = new RadiologyOrderServiceImpl();
            radiologyOrderDAO.setSessionFactory(sessionFactory);
            radiologyOrderServiceImpl.setRadiologyOrderDAO(radiologyOrderDAO);
            radiologyOrderServiceImpl.setRadiologyStudyService(radiologyStudyService);
            radiologyOrderServiceImpl.setOrderService(orderService);
            radiologyOrderServiceImpl.setEncounterService(encounterService);
            radiologyOrderServiceImpl.setRadiologyProperties(radiologyProperties);
        }
        
        saveRadiologyOrderEncounterMethod = RadiologyOrderServiceImpl.class.getDeclaredMethod("saveRadiologyOrderEncounter",
            new Class[] { Patient.class, Provider.class, Date.class });
        saveRadiologyOrderEncounterMethod.setAccessible(true);
        
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see RadiologyOrderServiceImpl#saveRadiologyOrderEncounter(Patient,Provider,Date)
     * @verifies create radiology order encounter
     */
    @Test
    public void saveRadiologyOrderEncounter_shouldCreateRadiologyOrderEncounter() throws Exception {
        // given
        Patient patient = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
        Provider provider = providerService.getProviderByUuid(RADIOLOGY_ORDER_PROVIDER_UUID);
        Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
        
        EncounterSearchCriteriaBuilder encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setPatient(patient);
        List<Encounter> matchingEncounters =
                encounterService.getEncounters(encounterSearchCriteria.createEncounterSearchCriteria());
        assertThat(matchingEncounters, is(empty()));
        
        Encounter encounter = (Encounter) saveRadiologyOrderEncounterMethod.invoke(radiologyOrderServiceImpl,
            new Object[] { patient, provider, encounterDatetime });
        
        assertNotNull(encounter);
        encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setPatient(patient)
                .setProviders(Arrays.asList(provider))
                .setFromDate(encounterDatetime)
                .setToDate(encounterDatetime)
                .setEncounterTypes(Arrays.asList(radiologyProperties.getRadiologyOrderEncounterType()));
        matchingEncounters = encounterService.getEncounters(encounterSearchCriteria.createEncounterSearchCriteria());
        assertThat(matchingEncounters, hasItem(encounter));
        assertThat(matchingEncounters.size(), is(1));
    }
}
