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
    
    /**
     * @verifies return false for other object types
     * @see RadiologyReportValidator#supports(Class)
     */
    @Test
    public void supports_shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        RadiologyReportValidator radiologyReportValidator = new RadiologyReportValidator();
        assertFalse(radiologyReportValidator.supports(Object.class));
    }
    
    /**
     * @verifies return true for RadiologyReport objects
     * @see RadiologyReportValidator#supports(Class)
     */
    @Test
    public void supports_shouldReturnTrueForRadiologyReportObjects() throws Exception {
        
        RadiologyReportValidator radiologyReportValidator = new RadiologyReportValidator();
        assertTrue(radiologyReportValidator.supports(RadiologyReport.class));
    }
    
    /**
     * @verifies fail validation if principalResultsInterpreter is empty or whitespace
     * @see RadiologyReportValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfPrincipalResultsInterpreterIsEmptyOrWhitespace() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(radiologyStudy);
        RadiologyReport radiologyReport = new RadiologyReport(radiologyOrder);
        radiologyReport.setPrincipalResultsInterpreter(null);
        
        Errors errors = new BindException(radiologyReport, "radiologyReport");
        new RadiologyReportValidator().validate(radiologyReport, errors);
        
        assertTrue(errors.hasFieldErrors("principalResultsInterpreter"));
    }
    
    /**
     * @verifies fail validation if radiologyReport is null
     * @see RadiologyReportValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfRadiologyReportIsNull() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(radiologyStudy);
        RadiologyReport radiologyReport = new RadiologyReport(radiologyOrder);
        
        Errors errors = new BindException(radiologyReport, "radiologyReport");
        new RadiologyReportValidator().validate(null, errors);
        
        assertTrue(errors.hasErrors());
        assertThat((errors.getAllErrors()).get(0)
                .getCode(), is("error.general"));
    }
    
    /**
     * @verifies pass validation if all fields are correct
     * @see RadiologyReportValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        RadiologyOrder radiologyOrder = new RadiologyOrder();
        RadiologyStudy radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(radiologyStudy);
        RadiologyReport radiologyReport = new RadiologyReport(radiologyOrder);
        radiologyReport.setPrincipalResultsInterpreter(new Provider());
        
        Errors errors = new BindException(radiologyReport, "radiologyReport");
        new RadiologyReportValidator().validate(radiologyReport, errors);
        
        assertFalse(errors.hasErrors());
    }
}
