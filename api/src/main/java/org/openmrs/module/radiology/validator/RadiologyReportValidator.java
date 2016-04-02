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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the {@link RadiologyReport} class.
 */
@Component
public class RadiologyReportValidator implements Validator {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public boolean supports(Class c) {
		return RadiologyReport.class.isAssignableFrom(c);
	}
	
	public void validate(Object obj, Errors errors) {
		RadiologyReport radiologyReport = (RadiologyReport) obj;
		if (radiologyReport == null) {
			errors.reject("error.general");
		} else {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "principalResultsInterpreter", "error.null",
				"Provider can not be null");
		}
	}
}
