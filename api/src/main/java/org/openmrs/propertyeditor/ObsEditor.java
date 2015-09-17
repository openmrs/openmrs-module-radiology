/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.springframework.util.StringUtils;

/**
 * Allows for serializing/deserializing a Obs object to a string so that Spring knows how to pass
 * a Obs back and forth through an html form or other medium
 * <br/>
 * In version 1.9, added ability for this to also retrieve objects by uuid
 * 
 * @see Obs
 */
public class ObsEditor extends PropertyEditorSupport {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @should set using id
	 * @should set using uuid
	 * @should throw illegal argument exception for obs not found
	 * @should return null for empty text
	 * 
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		ObsService obsService = Context.getObsService();
		if (StringUtils.hasText(text)) {
			try {
				setValue(obsService.getObs(Integer.valueOf(text)));
			}
			catch (Exception exception) {
				Obs Obs = obsService.getObsByUuid(text);
				setValue(Obs);
				if (Obs == null) {
					log.error("Error setting text: " + text, exception);
					throw new IllegalArgumentException("Obs not found: " + exception.getMessage());
				}
			}
		} else {
			setValue(null);
		}
	}
	
	/**
	 * @should return empty string for non existing obs
	 * @should return id as string for existing obs
	 * 
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 */
	public String getAsText() {
		Obs obs = (Obs) getValue();
		if (obs == null) {
			return "";
		} else {
			return obs.getObsId().toString();
		}
	}
	
}
