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

import liquibase.util.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link RadiologyModalityValidator}.
 */
public class RadiologyModalityValidatorTest {
    
    
    RadiologyModality radiologyModality;
    
    @Before
    public void setUp() {
        
        radiologyModality = new RadiologyModality();
        radiologyModality.setAeTitle("CT01");
        radiologyModality.setName("Medical Corp Excelencium XT5980-Z");
    }
    
    /**
     * @see RadiologyModalityValidator#supports(Class)
     * @verifies return false for other object types
     */
    @Test
    public void supports_shouldReturnFalseForOtherObjectTypes() throws Exception {
        
        RadiologyModalityValidator radiologyModalityValidator = new RadiologyModalityValidator();
        assertFalse(radiologyModalityValidator.supports(Object.class));
    }
    
    /**
     * @see RadiologyModalityValidator#supports(Class)
     * @verifies return true for radiology modality objects
     */
    @Test
    public void supports_shouldReturnTrueForRadiologyModalityObjects() throws Exception {
        
        RadiologyModalityValidator radiologyModalityValidator = new RadiologyModalityValidator();
        assertTrue(radiologyModalityValidator.supports(RadiologyModality.class));
    }
    
    /**
     * @see RadiologyModalityValidator#validate(Object, Errors)
     * @verifies fail validation if radiology modality is null
     */
    @Test
    public void validate_shouldFailValidationIfRadiologyModalityIsNull() throws Exception {
        
        Errors errors = new BindException(radiologyModality, "radiologyModality");
        new RadiologyModalityValidator().validate(null, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.general"));
    }
    
    /**
     * @see RadiologyModalityValidator#validate(Object,Errors)
     * @verifies fail validation if ae title is null or empty or whitespaces only
     */
    @Test
    public void validate_shouldFailValidationIfAeTitleIsNullOrEmptyOrWhitespacesOnly() throws Exception {
        
        radiologyModality.setAeTitle(null);
        
        Errors errors = new BindException(radiologyModality, "radiologyModality");
        new RadiologyModalityValidator().validate(radiologyModality, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.null"));
        assertTrue(errors.hasFieldErrors("aeTitle"));
        
        radiologyModality.setAeTitle("");
        
        errors = new BindException(radiologyModality, "radiologyModality");
        new RadiologyModalityValidator().validate(radiologyModality, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.null"));
        assertTrue(errors.hasFieldErrors("aeTitle"));
        
        radiologyModality.setAeTitle("  ");
        
        errors = new BindException(radiologyModality, "radiologyModality");
        new RadiologyModalityValidator().validate(radiologyModality, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.null"));
        assertTrue(errors.hasFieldErrors("aeTitle"));
    }
    
    /**
     * @verifies fail validation if ae title exceeds 16 characters
     * @see RadiologyModalityValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfAeTitleExceeds16Characters() throws Exception {
        
        radiologyModality.setAeTitle("CTTOOLONGNAME2412");
        
        Errors errors = new BindException(radiologyModality, "radiologyModality");
        new RadiologyModalityValidator().validate(radiologyModality, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.exceededMaxLengthOfField"));
        Object[] arguments = errors.getAllErrors()
                .get(0)
                .getArguments();
        assertThat(arguments.length, is(1));
        assertThat(arguments[0], is(16));
        assertTrue(errors.hasFieldErrors("aeTitle"));
    }
    
    /**
     * @verifies fail validation if name is null or empty or whitespaces only
     * @see RadiologyModalityValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfNameIsNullOrEmptyOrWhitespacesOnly() throws Exception {
        
        radiologyModality.setName(null);
        
        Errors errors = new BindException(radiologyModality, "radiologyModality");
        new RadiologyModalityValidator().validate(radiologyModality, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.null"));
        assertTrue(errors.hasFieldErrors("name"));
        
        radiologyModality.setName("");
        
        errors = new BindException(radiologyModality, "radiologyModality");
        new RadiologyModalityValidator().validate(radiologyModality, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.null"));
        assertTrue(errors.hasFieldErrors("name"));
        
        radiologyModality.setName("  ");
        
        errors = new BindException(radiologyModality, "radiologyModality");
        new RadiologyModalityValidator().validate(radiologyModality, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.null"));
        assertTrue(errors.hasFieldErrors("name"));
    }
    
    /**
     * @see RadiologyModalityValidator#validate(Object, Errors)
     * @verifies pass validation if all fields are correct
     */
    @Test
    public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
        
        Errors errors = new BindException(radiologyModality, "radiologyModality");
        new RadiologyModalityValidator().validate(radiologyModality, errors);
        
        assertFalse(errors.hasErrors());
    }
    
    /**
     * @verifies fail validation if name exceeds 255 characters
     * @see RadiologyModalityValidator#validate(Object, Errors)
     */
    @Test
    public void validate_shouldFailValidationIfNameExceeds255Characters() throws Exception {
        
        radiologyModality.setName(StringUtils.repeat("123", 256));
        
        Errors errors = new BindException(radiologyModality, "radiologyModality");
        new RadiologyModalityValidator().validate(radiologyModality, errors);
        
        assertTrue(errors.hasErrors());
        assertThat(errors.getAllErrors()
                .size(),
            is(1));
        assertThat((errors.getAllErrors()).get(0)
                .getCode(),
            is("error.exceededMaxLengthOfField"));
        Object[] arguments = errors.getAllErrors()
                .get(0)
                .getArguments();
        assertThat(arguments.length, is(1));
        assertThat(arguments[0], is(255));
        assertTrue(errors.hasFieldErrors("name"));
    }
}
