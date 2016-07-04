/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.api.APIException;

public class MrrtReportTemplateValidatorTest {
    
    
    private MrrtReportTemplateValidator validator = new DefaultMrrtReportTemplateValidator();
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    /**
    * @see MrrtReportTemplateValidator#validate(File)
    * @verifies should throw APIException if file extension is not .html
    */
    @Test
    public void validate_shouldShouldThrowAPIExceptionIfFileExtensionIsNotHtml() throws Exception {
        File invalidFile = File.createTempFile("templateFile", ".css");
        
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Invalid file extension (.css). Only (.html) files are accepted");
        validator.validate(invalidFile);
    }
}
