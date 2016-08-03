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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.study.RadiologyStudy;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Tests {@link RadiologyReportValidator}.
 */
public class RadiologyReportValidatorTest {
    
    
    RadiologyOrder radiologyOrder;
    
    RadiologyStudy radiologyStudy;
    
    RadiologyReport radiologyReport;
    
    @Before
    public void setUp() {
        
        radiologyOrder = new RadiologyOrder();
        radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(radiologyStudy);
        radiologyReport = new RadiologyReport(radiologyOrder);
        radiologyReport.setPrincipalResultsInterpreter(new Provider());
        radiologyReport.setBody("Found a broken bone.");
    }
    
    /**
     * @see RadiologyReportValidator#supports(Class)
     * @verifies return false for other object types
     */
    @Test
    public void supports_shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        RadiologyReportValidator radiologyReportValidator = new RadiologyReportValidator();
        assertFalse(radiologyReportValidator.supports(Object.class));
    }
    
    /**
     * @see RadiologyReportValidator#supports(Class)
     * @verifies return true for radiology report objects
     */
    @Test
    public void supports_shouldReturnTrueForRadiologyReportObjects() throws Exception {
        
        RadiologyReportValidator radiologyReportValidator = new RadiologyReportValidator();
        assertTrue(radiologyReportValidator.supports(RadiologyReport.class));
    }
    
    /**
     * @see RadiologyReportValidator#validate(Object, Errors)
     * @verifies fail validation if radiology report is null
     */
    @Test
    public void validate_shouldFailValidationIfRadiologyReportIsNull() throws Exception {
        
        Errors errors = new BindException(radiologyReport, "radiologyReport");
        new RadiologyReportValidator().validate(null, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.general"));
    }
    
    /**
     * @see RadiologyReportValidator#validate(Object, Errors)
     * @verifies fail validation if principal results interpreter is null or empty or whitespaces only
     */
    @Test
    public void validate_shouldFailValidationIfPrincipalResultsInterpreterIsNullOrEmptyOrWhitespacesOnly() throws Exception {
        
        radiologyReport.setPrincipalResultsInterpreter(null);
        
        Errors errors = new BindException(radiologyReport, "radiologyReport");
        new RadiologyReportValidator().validate(radiologyReport, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.null"));
        assertTrue(errors.hasFieldErrors("principalResultsInterpreter"));
    }
    
    /**
     * @see RadiologyReportValidator#validate(Object,Errors)
     * @verifies fail validation if report body is null or empty or whitespaces only
     */
    @Test
    public void validate_shouldFailValidationIfReportBodyIsNullOrEmptyOrWhitespacesOnly() throws Exception {
        
        radiologyReport.setBody(null);
        
        Errors errors = new BindException(radiologyReport, "radiologyReport");
        new RadiologyReportValidator().validate(radiologyReport, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.null"));
        assertTrue(errors.hasFieldErrors("body"));
        
        radiologyReport.setBody("");
        
        errors = new BindException(radiologyReport, "radiologyReport");
        new RadiologyReportValidator().validate(radiologyReport, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.null"));
        assertTrue(errors.hasFieldErrors("body"));
        
        radiologyReport.setBody("  ");
        
        errors = new BindException(radiologyReport, "radiologyReport");
        new RadiologyReportValidator().validate(radiologyReport, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.null"));
        assertTrue(errors.hasFieldErrors("body"));
    }
    
    /**
     * @see RadiologyReportValidator#validate(Object, Errors)
     * @verifies pass validation if all fields are correct
     */
    @Test
    public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        Errors errors = new BindException(radiologyReport, "radiologyReport");
        new RadiologyReportValidator().validate(radiologyReport, errors);
        
        assertFalse(errors.hasErrors());
    }
}
