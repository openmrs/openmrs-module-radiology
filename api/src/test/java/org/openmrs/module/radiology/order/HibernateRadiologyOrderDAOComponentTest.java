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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.GlobalProperty;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.radiology.RadiologyConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests {@link HibernateRadiologyOrderDAO}.
 */
public class HibernateRadiologyOrderDAOComponentTest extends BaseModuleContextSensitiveTest {
    
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Qualifier("adminService")
    @Autowired
    AdministrationService administrationService;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private HibernateRadiologyOrderDAO hibernateRadiologyOrderDAO;
    
    private String globalPropertyMissing =
            "Missing global property named: " + RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED;
    
    private String globalPropertyInvalid =
            "Invalid value for global property named: " + RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED;
    
    @Before
    public void setUp() {
        
        hibernateRadiologyOrderDAO = new HibernateRadiologyOrderDAO();
        hibernateRadiologyOrderDAO.setSessionFactory(sessionFactory);
    }
    
    /**
     * @see HibernateRadiologyOrderDAO#getNextAccessionNumberSeedSequenceValue()
     * @verifies return the next accession number seed stored as global property radiology next accession number and
     *           increment the global property value
     */
    @Test
    public void
            getNextAccessionNumberSeedSequenceValue_shouldReturnTheNextAccessionNumberSeedStoredAsGlobalPropertyRadiologyNextAccessionNumberAndIncrementTheGlobalPropertyValue()
                    throws Exception {
        
        GlobalProperty nextAccessionNumberGlobalProperty =
                new GlobalProperty(RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED);
        nextAccessionNumberGlobalProperty.setPropertyValue("1");
        administrationService.saveGlobalProperty(nextAccessionNumberGlobalProperty);
        
        Long seed = (Long) hibernateRadiologyOrderDAO.getNextAccessionNumberSeedSequenceValue();
        assertThat(seed, is(1L));
        
        for (int i = 0; i < 10; i++) {
            assertThat(hibernateRadiologyOrderDAO.getNextAccessionNumberSeedSequenceValue(), is(++seed));
        }
    }
    
    /**
     * @see HibernateRadiologyOrderDAO#getNextAccessionNumberSeedSequenceValue()
     * @verifies throw an api exception if global property radiology next accession number seed is missing
     */
    @Test
    public void
            getNextAccessionNumberSeedSequenceValue_shouldThrowAnApiExceptionIfGlobalPropertyRadiologyNextAccessionNumberSeedIsMissing()
                    throws Exception {
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage(globalPropertyMissing);
        hibernateRadiologyOrderDAO.getNextAccessionNumberSeedSequenceValue();
    }
    
    /**
     * @see HibernateRadiologyOrderDAO#getNextAccessionNumberSeedSequenceValue()
     * @verifies throw an api exception if global property radiology next accession number seed value is empty or only
     *           contains whitespaces
     */
    @Test
    public void
            getNextAccessionNumberSeedSequenceValue_shouldThrowAnApiExceptionIfGlobalPropertyRadiologyNextAccessionNumberSeedValueIsEmptyOrOnlyContainsWhitespaces()
                    throws Exception {
        
        GlobalProperty nextAccessionNumberGlobalProperty =
                new GlobalProperty(RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED);
        nextAccessionNumberGlobalProperty.setPropertyValue("1");
        administrationService.saveGlobalProperty(nextAccessionNumberGlobalProperty);
        
        String[] invalidEmptyValues = { "", "  ", null };
        
        for (String invalidValue : invalidEmptyValues) {
            administrationService.updateGlobalProperty(RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED, invalidValue);
            nextAccessionNumberGlobalProperty.setPropertyValue(invalidValue);
            administrationService.saveGlobalProperty(nextAccessionNumberGlobalProperty);
            
            expectedException.expect(APIException.class);
            expectedException.expectMessage(globalPropertyInvalid);
            hibernateRadiologyOrderDAO.getNextAccessionNumberSeedSequenceValue();
        }
    }
    
    /**
     * @see HibernateRadiologyOrderDAO#getNextAccessionNumberSeedSequenceValue()
     * @verifies throw an api exception if global property radiology next accession number seed value value cannot be parsed
     *           to long
     */
    @Test
    public void
            getNextAccessionNumberSeedSequenceValue_shouldThrowAnApiExceptionIfGlobalPropertyRadiologyNextAccessionNumberSeedValueValueCannotBeParsedToLong()
                    throws Exception {
        
        GlobalProperty nextAccessionNumberGlobalProperty =
                new GlobalProperty(RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED);
        nextAccessionNumberGlobalProperty.setPropertyValue("1");
        administrationService.saveGlobalProperty(nextAccessionNumberGlobalProperty);
        
        String[] invalidLongValues = { "xxx", "3.2", Long.MAX_VALUE + "1" };
        
        for (String invalidValue : invalidLongValues) {
            administrationService.updateGlobalProperty(RadiologyConstants.GP_NEXT_ACCESSION_NUMBER_SEED, invalidValue);
            
            expectedException.expect(APIException.class);
            expectedException.expectMessage(globalPropertyInvalid);
            hibernateRadiologyOrderDAO.getNextAccessionNumberSeedSequenceValue();
        }
    }
}
