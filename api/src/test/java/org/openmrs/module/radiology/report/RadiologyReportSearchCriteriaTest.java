/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.openmrs.Provider;

/**
 * Tests {@link RadiologyReportSearchCriteria}.
 */
public class RadiologyReportSearchCriteriaTest {
    
    
    private RadiologyReportSearchCriteria radiologyReportSearchCriteria;
    
    /**
     * @see RadiologyReportSearchCriteria.Builder#build()
     * @verifies create a new radiology report search criteria instance with from and to date specified if date from and date to are set
     */
    @Test
    public void build_createANewRadiologyReportSearchCriteriaInstanceWithFromAndToDateSpecifiedIfDateFromAndDateToAreSet()
            throws Exception {
        
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = format.parse("2016-05-01");
        Date toDate = format.parse("2016-05-01");
        radiologyReportSearchCriteria = new RadiologyReportSearchCriteria.Builder().fromDate(fromDate)
                .toDate(toDate)
                .build();
        
        assertTrue(radiologyReportSearchCriteria.getFromDate()
                .equals(fromDate));
        assertTrue(radiologyReportSearchCriteria.getToDate()
                .equals(toDate));
        assertFalse(radiologyReportSearchCriteria.getIncludeVoided());
        assertNull(radiologyReportSearchCriteria.getPrincipalResultsInterpreter());
        assertNull(radiologyReportSearchCriteria.getStatus());
    }
    
    /**
     * @see RadiologyReportSearchCriteria.Builder#build()
     * @verifies create a new radiology report search criteria instance with principal results interpreter specified if principal results interpreter is set
     */
    @Test
    public void
            build_createANewRadiologyReportSearchCriteriaInstanceWithPrincipalResultsInterpreterSpecifiedIfPrincipalResultsInterpreterIsSet()
                    throws Exception {
        
        radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().withPrincipalResultsInterpreter(new Provider(1))
                        .build();
        
        assertNotNull(radiologyReportSearchCriteria.getPrincipalResultsInterpreter());
        assertThat(radiologyReportSearchCriteria.getPrincipalResultsInterpreter()
                .getId(),
            is(1));
        assertFalse(radiologyReportSearchCriteria.getIncludeVoided());
        assertNull(radiologyReportSearchCriteria.getToDate());
        assertNull(radiologyReportSearchCriteria.getFromDate());
        assertNull(radiologyReportSearchCriteria.getStatus());
    }
    
    /**
     * @see RadiologyReportSearchCriteria.Builder#build()
     * @verifies create a new radiology report search criteria instance with include voided set to true if voided reports should be included
     */
    @Test
    public void
            build_createANewRadiologyReportSearchCriteriaInstanceWithIncludeDiscontinuedSetToTrueIfDiscontinuedReportsShouldBeIncluded()
                    throws Exception {
        
        radiologyReportSearchCriteria = new RadiologyReportSearchCriteria.Builder().includeVoided()
                .build();
        
        assertTrue(radiologyReportSearchCriteria.getIncludeVoided());
        assertNull(radiologyReportSearchCriteria.getToDate());
        assertNull(radiologyReportSearchCriteria.getFromDate());
        assertNull(radiologyReportSearchCriteria.getPrincipalResultsInterpreter());
        assertNull(radiologyReportSearchCriteria.getStatus());
    }
    
    /**
     * @see RadiologyReportSearchCriteria.Builder#build()
     * @verifies create a new radiology report search criteria instance with report status specified if status is set to claimed or completed
     */
    @Test
    public void
            build_createANewRadiologyReportSearchCriteriaInstanceWithReportStatusSpecifiedIfStatusIsSetToClaimedOrCompleted()
                    throws Exception {
        
        radiologyReportSearchCriteria = new RadiologyReportSearchCriteria.Builder().withStatus(RadiologyReportStatus.DRAFT)
                .build();
        
        assertThat(radiologyReportSearchCriteria.getStatus(), is(RadiologyReportStatus.DRAFT));
        assertFalse(radiologyReportSearchCriteria.getIncludeVoided());
        assertNull(radiologyReportSearchCriteria.getToDate());
        assertNull(radiologyReportSearchCriteria.getFromDate());
        assertNull(radiologyReportSearchCriteria.getPrincipalResultsInterpreter());
        
        radiologyReportSearchCriteria =
                new RadiologyReportSearchCriteria.Builder().withStatus(RadiologyReportStatus.COMPLETED)
                        .build();
        assertThat(radiologyReportSearchCriteria.getStatus(), is(RadiologyReportStatus.COMPLETED));
        assertFalse(radiologyReportSearchCriteria.getIncludeVoided());
        assertNull(radiologyReportSearchCriteria.getToDate());
        assertNull(radiologyReportSearchCriteria.getFromDate());
        assertNull(radiologyReportSearchCriteria.getPrincipalResultsInterpreter());
    }
}
