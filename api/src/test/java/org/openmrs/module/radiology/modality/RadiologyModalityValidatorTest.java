/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.radiology.test.ValidatorAssertions.assertSingleGeneralError;
import static org.openmrs.module.radiology.test.ValidatorAssertions.assertSingleNullErrorInField;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import liquibase.util.StringUtils;

/**
 * Tests {@link RadiologyModalityValidator}.
 */
public class RadiologyModalityValidatorTest extends BaseModuleContextSensitiveTest {
    
    
    private RadiologyModalityValidator radiologyModalityValidator;
    
    private RadiologyModality radiologyModality;
    
    private Errors errors;
    
    @Before
    public void setUp() {
        radiologyModalityValidator = new RadiologyModalityValidator();
        
        radiologyModality = new RadiologyModality();
        radiologyModality.setAeTitle("CT01");
        radiologyModality.setName("Medical Corp Excelencium XT5980-Z");
        
        errors = new BindException(radiologyModality, "radiologyModality");
    }
    
    @Test
    public void shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        assertFalse(radiologyModalityValidator.supports(Object.class));
    }
    
    @Test
    public void shouldReturnTrueForRadiologyModalityObjects() throws Exception {
        
        assertTrue(radiologyModalityValidator.supports(RadiologyModality.class));
    }
    
    @Test
    public void shouldFailValidationIfRadiologyModalityIsNull() throws Exception {
        
        radiologyModalityValidator.validate(null, errors);
        
        assertSingleGeneralError(errors);
    }
    
    @Test
    public void shouldFailValidationIfAeTitleIsNull() throws Exception {
        
        radiologyModality.setAeTitle(null);
        
        radiologyModalityValidator.validate(radiologyModality, errors);
        
        assertSingleNullErrorInField(errors, "aeTitle");
    }
    
    @Test
    public void shouldFailValidationIfAeTitleIsEmpty() throws Exception {
        
        radiologyModality.setAeTitle("");
        
        radiologyModalityValidator.validate(radiologyModality, errors);
        
        assertSingleNullErrorInField(errors, "aeTitle");
    }
    
    @Test
    public void shouldFailValidationIfAeTitleIsWhitespacesOnly() throws Exception {
        
        radiologyModality.setAeTitle("  ");
        
        radiologyModalityValidator.validate(radiologyModality, errors);
        
        assertSingleNullErrorInField(errors, "aeTitle");
    }
    
    @Test
    public void shouldFailValidationIfNameIsNull() throws Exception {
        
        radiologyModality.setName(null);
        
        radiologyModalityValidator.validate(radiologyModality, errors);
        
        assertSingleNullErrorInField(errors, "name");
    }
    
    @Test
    public void shouldFailValidationIfNameIsEmpty() throws Exception {
        
        radiologyModality.setName("");
        
        radiologyModalityValidator.validate(radiologyModality, errors);
        
        assertSingleNullErrorInField(errors, "name");
    }
    
    @Test
    public void shouldFailValidationIfNameIsWhitespacesOnly() throws Exception {
        
        radiologyModality.setName("  ");
        
        radiologyModalityValidator.validate(radiologyModality, errors);
        
        assertSingleNullErrorInField(errors, "name");
    }
    
    @Test
    public void shouldFailValidationIfRetireReasonIsNullOrEmptyIfRetireIsTrueAndSetRetiredToFalse() throws Exception {
        
        radiologyModality.setRetired(true);
        
        radiologyModalityValidator.validate(radiologyModality, errors);
        
        assertSingleNullErrorInField(errors, "retireReason");
    }
    
    @Test
    public void shouldFailValidationIfFieldLengthsAreNotCorrect() throws Exception {
        
        radiologyModality.setAeTitle(StringUtils.repeat("1", 17));
        radiologyModality.setName(StringUtils.repeat("1", 256));
        radiologyModality.setDescription(StringUtils.repeat("1", 256));
        radiologyModality.setRetireReason(StringUtils.repeat("1", 256));
        
        radiologyModalityValidator.validate(radiologyModality, errors);
        
        assertTrue(errors.hasErrors());
        ObjectError err = (errors.getAllErrors()).get(0);
        for (ObjectError error : errors.getAllErrors()) {
            assertThat(error.getCode(), is("error.exceededMaxLengthOfField"));
        }
        assertTrue(errors.hasFieldErrors("aeTitle"));
        assertTrue(errors.hasFieldErrors("name"));
        assertTrue(errors.hasFieldErrors("description"));
        assertTrue(errors.hasFieldErrors("retireReason"));
    }
    
    @Test
    public void shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        radiologyModalityValidator.validate(radiologyModality, errors);
        
        assertFalse(errors.hasErrors());
    }
}
