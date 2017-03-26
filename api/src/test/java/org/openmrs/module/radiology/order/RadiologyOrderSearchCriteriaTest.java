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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.openmrs.Order.Urgency;
import org.openmrs.Patient;
import org.openmrs.Provider;

/**
 * Tests {@link RadiologyOrderSearchCriteria}.
 */
public class RadiologyOrderSearchCriteriaTest {
    
    
    private RadiologyOrderSearchCriteria radiologyOrderSearchCriteria;
    
    @Test
    public void createANewRadiologyOrderSearchCriteriaInstanceWithPatientIfPatientIsSet() throws Exception {
        
        Patient patient = new Patient(1);
        
        radiologyOrderSearchCriteria = new RadiologyOrderSearchCriteria.Builder().withPatient(patient)
                .build();
        
        assertTrue(radiologyOrderSearchCriteria.getPatient()
                .equals(patient));
        assertFalse(radiologyOrderSearchCriteria.getIncludeVoided());
        assertNull(radiologyOrderSearchCriteria.getUrgency());
        assertNull(radiologyOrderSearchCriteria.getFromEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getToEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getAccessionNumber());
        assertNull(radiologyOrderSearchCriteria.getOrderer());
    }
    
    @Test
    public void
            build_createANewRadiologyOrderSearchCriteriaInstanceWithIncludeVoidedSetToTrueIfVoidedOrdersShouldBeIncluded()
                    throws Exception {
        
        radiologyOrderSearchCriteria = new RadiologyOrderSearchCriteria.Builder().includeVoided()
                .build();
        
        assertTrue(radiologyOrderSearchCriteria.getIncludeVoided());
        assertNull(radiologyOrderSearchCriteria.getPatient());
        assertNull(radiologyOrderSearchCriteria.getUrgency());
        assertNull(radiologyOrderSearchCriteria.getFromEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getToEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getAccessionNumber());
        assertNull(radiologyOrderSearchCriteria.getOrderer());
    }
    
    @Test
    public void createANewRadiologyOrderSearchCriteriaInstanceWithUrgencyIfUrgencyIsSet() throws Exception {
        
        radiologyOrderSearchCriteria = new RadiologyOrderSearchCriteria.Builder().withUrgency(Urgency.ROUTINE)
                .build();
        
        assertTrue(radiologyOrderSearchCriteria.getUrgency()
                .equals(Urgency.ROUTINE));
        assertNull(radiologyOrderSearchCriteria.getPatient());
        assertFalse(radiologyOrderSearchCriteria.getIncludeVoided());
        assertNull(radiologyOrderSearchCriteria.getFromEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getToEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getAccessionNumber());
        assertNull(radiologyOrderSearchCriteria.getOrderer());
    }
    
    @Test
    public void createANewRadiologyOrderSearchCriteriaInstanceWithFromEffectiveStartDateIfFromEffectiveStartDateIsSet()
            throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromEffectiveStartDate = format.parse("2016-05-01");
        
        radiologyOrderSearchCriteria =
                new RadiologyOrderSearchCriteria.Builder().fromEffectiveStartDate(fromEffectiveStartDate)
                        .build();
        
        assertThat(radiologyOrderSearchCriteria.getFromEffectiveStartDate(), is(fromEffectiveStartDate));
        assertNull(radiologyOrderSearchCriteria.getPatient());
        assertFalse(radiologyOrderSearchCriteria.getIncludeVoided());
        assertNull(radiologyOrderSearchCriteria.getUrgency());
        assertNull(radiologyOrderSearchCriteria.getToEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getAccessionNumber());
        assertNull(radiologyOrderSearchCriteria.getOrderer());
    }
    
    @Test
    public void createANewRadiologyOrderSearchCriteriaInstanceWithToEffectiveStartDateIfToEffectiveStartDateIsSet()
            throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date toEffectiveStartDate = format.parse("2016-05-01");
        
        radiologyOrderSearchCriteria = new RadiologyOrderSearchCriteria.Builder().toEffectiveStartDate(toEffectiveStartDate)
                .build();
        
        assertThat(radiologyOrderSearchCriteria.getToEffectiveStartDate(), is(toEffectiveStartDate));
        assertNull(radiologyOrderSearchCriteria.getPatient());
        assertFalse(radiologyOrderSearchCriteria.getIncludeVoided());
        assertNull(radiologyOrderSearchCriteria.getUrgency());
        assertNull(radiologyOrderSearchCriteria.getFromEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getAccessionNumber());
        assertNull(radiologyOrderSearchCriteria.getOrderer());
    }
    
    @Test
    public void createANewRadiologyOrderSearchCriteriaInstanceWithAccessionNumberIfAccessionNumberIsSet() throws Exception {
        
        String accessionNumber = "1";
        
        radiologyOrderSearchCriteria = new RadiologyOrderSearchCriteria.Builder().withAccessionNumber(accessionNumber)
                .build();
        
        assertThat(radiologyOrderSearchCriteria.getAccessionNumber(), is(accessionNumber));
        assertNull(radiologyOrderSearchCriteria.getPatient());
        assertFalse(radiologyOrderSearchCriteria.getIncludeVoided());
        assertNull(radiologyOrderSearchCriteria.getUrgency());
        assertNull(radiologyOrderSearchCriteria.getFromEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getToEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getOrderer());
    }
    
    @Test
    public void createANewRadiologyOrderSearchCriteriaInstanceWithOrdererIfOrdererIsSet() throws Exception {
        
        Provider orderer = new Provider(1);
        
        radiologyOrderSearchCriteria = new RadiologyOrderSearchCriteria.Builder().withOrderer(orderer)
                .build();
        
        assertThat(radiologyOrderSearchCriteria.getOrderer(), is(orderer));
        assertNull(radiologyOrderSearchCriteria.getPatient());
        assertFalse(radiologyOrderSearchCriteria.getIncludeVoided());
        assertNull(radiologyOrderSearchCriteria.getUrgency());
        assertNull(radiologyOrderSearchCriteria.getFromEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getToEffectiveStartDate());
        assertNull(radiologyOrderSearchCriteria.getAccessionNumber());
    }
}
