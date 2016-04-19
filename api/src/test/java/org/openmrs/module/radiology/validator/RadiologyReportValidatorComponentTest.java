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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.module.radiology.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

/**
 * Tests methods on the {@link RadiologyReportValidator} class.
 */
public class RadiologyReportValidatorComponentTest {
	
	/**
	 * @see {@link RadiologyReportValidator#supports(Class c)}
	 */
	@Test
	@Verifies(value = "should return true for RadiologyReport objects", method = "supports(Class c)")
	public void supports_shouldReturnTrueForRadiologyReportObjects() throws Exception {
		
		RadiologyReportValidator radiologyReportValidator = new RadiologyReportValidator();
		Assert.assertTrue(radiologyReportValidator.supports(RadiologyReport.class));
	}
	
	/**
	 * @see {@link RadiologyReportValidator#supports(Class c)}
	 */
	@Test
	@Verifies(value = "should return false for other object types", method = "supports(Class c)")
	public void supports_shouldReturnFalseForOtherObjectTypes() throws Exception {
		
		RadiologyReportValidator radiologyReportValidator = new RadiologyReportValidator();
		Assert.assertFalse(radiologyReportValidator.supports(Object.class));
	}
	
	/**
	 * @see {@link RadiologyReportValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if radiologyReport is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfRadiologyReportIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		Study study = new Study();
		study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		radiologyOrder.setStudy(study);
		RadiologyReport radiologyReport = new RadiologyReport(radiologyOrder);
		
		Errors errors = new BindException(radiologyReport, "radiologyReport");
		new RadiologyReportValidator().validate(null, errors);
		
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.general", ((List<ObjectError>) errors.getAllErrors()).get(0)
				.getCode());
	}
	
	/**
	 * @see {@link RadiologyReportValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should fail validation if principalResultsInterpreter is null", method = "validate(Object, Errors)")
	public void validate_shouldFailValidationIfPrincipalResultsInterpreterIsNull() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		Study study = new Study();
		study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		radiologyOrder.setStudy(study);
		RadiologyReport radiologyReport = new RadiologyReport(radiologyOrder);
		radiologyReport.setPrincipalResultsInterpreter(null);
		
		Errors errors = new BindException(radiologyReport, "radiologyReport");
		new RadiologyReportValidator().validate(radiologyReport, errors);
		
		Assert.assertTrue(errors.hasFieldErrors("principalResultsInterpreter"));
	}
	
	/**
	 * @see {@link RadiologyReportValidator#validate(Object, Errors)}
	 */
	@Test
	@Verifies(value = "should pass validation if all fields are correct", method = "validate(Object, Errors)")
	public void validate_shouldPassValidationIfAllFieldsAreCorrect() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		Study study = new Study();
		study.setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
		radiologyOrder.setStudy(study);
		RadiologyReport radiologyReport = new RadiologyReport(radiologyOrder);
		radiologyReport.setPrincipalResultsInterpreter(new Provider());
		
		Errors errors = new BindException(radiologyReport, "radiologyReport");
		new RadiologyReportValidator().validate(radiologyReport, errors);
		
		Assert.assertFalse(errors.hasErrors());
	}
}
