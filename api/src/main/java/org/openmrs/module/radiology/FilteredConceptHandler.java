/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p/>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import org.openmrs.web.taglib.fieldgen.AbstractFieldGenHandler;
import org.openmrs.web.taglib.fieldgen.FieldGenHandler;

/**
 * Created by Thomas on 06.12.15.
 */
public class FilteredConceptHandler extends AbstractFieldGenHandler implements FieldGenHandler {
	
	String defaultUrl = "filteredConcept.field";
	
	@Override
	public void run() {
		setUrl(defaultUrl);
		checkEmptyVal((FilteredConcept) null);
		if (fieldGenTag != null) {
			Object initialValue = this.fieldGenTag.getVal();
			setParameter("initialValue", initialValue == null ? "" : initialValue.toString());
		}
	}
}
