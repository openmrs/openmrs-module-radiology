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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.radiology.test.ValidatorAssertions.assertSingleGeneralError;
import static org.openmrs.module.radiology.test.ValidatorAssertions.assertSingleNullErrorInField;

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
    
    
    private RadiologyReportValidator radiologyReportValidator;
    
    private RadiologyOrder radiologyOrder;
    
    private RadiologyStudy radiologyStudy;
    
    private RadiologyReport radiologyReport;
    
    private Errors errors;
    
    @Before
    public void setUp() {
        radiologyReportValidator = new RadiologyReportValidator();
        
        radiologyOrder = new RadiologyOrder();
        radiologyStudy = new RadiologyStudy();
        radiologyStudy.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        radiologyOrder.setStudy(radiologyStudy);
        radiologyReport = new RadiologyReport(radiologyOrder);
        radiologyReport.setPrincipalResultsInterpreter(new Provider());
        radiologyReport.setBody("Found a broken bone.");
        
        errors = new BindException(radiologyReport, "radiologyReport");
    }
    
    @Test
    public void shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        assertFalse(radiologyReportValidator.supports(Object.class));
    }
    
    @Test
    public void shouldReturnTrueForRadiologyReportObjects() throws Exception {
        
        assertTrue(radiologyReportValidator.supports(RadiologyReport.class));
    }
    
    @Test
    public void shouldFailValidationIfRadiologyReportIsNull() throws Exception {
        
        radiologyReportValidator.validate(null, errors);
        
        assertSingleGeneralError(errors);
    }
    
    @Test
    public void shouldFailValidationIfPrincipalResultsInterpreterIsNull() throws Exception {
        
        radiologyReport.setPrincipalResultsInterpreter(null);
        
        radiologyReportValidator.validate(radiologyReport, errors);
        
        assertSingleNullErrorInField(errors, "principalResultsInterpreter");
    }
    
    @Test
    public void shouldFailValidationIfReportBodyIsNull() throws Exception {
        
        radiologyReport.setBody(null);
        
        radiologyReportValidator.validate(radiologyReport, errors);
        
        assertSingleNullErrorInField(errors, "body");
    }
    
    @Test
    public void shouldFailValidationIfReportBodyIsEmpty() throws Exception {
        
        radiologyReport.setBody("");
        
        radiologyReportValidator.validate(radiologyReport, errors);
        
        assertSingleNullErrorInField(errors, "body");
    }
    
    @Test
    public void shouldFailValidationIfReportBodyIsWhitespacesOnly() throws Exception {
        
        radiologyReport.setBody("  ");
        
        radiologyReportValidator.validate(radiologyReport, errors);
        
        assertSingleNullErrorInField(errors, "body");
    }
    
    @Test
    public void shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        radiologyReportValidator.validate(radiologyReport, errors);
        
        assertFalse(errors.hasErrors());
    }
}
