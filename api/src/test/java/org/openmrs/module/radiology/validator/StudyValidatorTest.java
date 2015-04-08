/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.Study;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

/**
 * Tests methods on the {@link StudyValidator} class.
 */
public class StudyValidatorTest {
	
	/**
	 * @see StudyValidator#validate(Object, Errors)
	 */
	@Test
	@Verifies(value = "should fail validation if study is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfStudyIsNull() throws Exception {
		Errors errors = new BindException(new Study(), "study");
		new StudyValidator().validate(null, errors);
		
		assertTrue(errors.hasErrors());
		assertEquals("error.general", ((List<ObjectError>) errors.getAllErrors()).get(0).getCode());
	}
	
	/**
	 * @see StudyValidator#validate(Object, Errors)
	 */
	@Test
	@Verifies(value = "should fail validation if modality is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfModalityIsNull() throws Exception {
		Study study = new Study();
		
		Errors errors = new BindException(study, "study");
		new StudyValidator().validate(study, errors);
		
		assertTrue(errors.hasErrors());
		assertTrue(errors.hasFieldErrors("modality"));
	}
	
	/**
	 * @see {@link StudyValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object, Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		
		Study study = new Study();
		study.setModality(Modality.CT);
		
		Errors errors = new BindException(study, "study");
		new StudyValidator().validate(study, errors);
		
		assertFalse(errors.hasErrors());
	}
}
