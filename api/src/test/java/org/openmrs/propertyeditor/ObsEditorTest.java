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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.Verifies;

public class ObsEditorTest extends BaseContextSensitiveTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * @see ObsEditor#setAsText(String)
	 * @Verifies(value = "should set using id", method = "setAsText(String)")
	 */
	@Test
	public void setAsText_shouldSetUsingId() throws Exception {
		ObsEditor editor = new ObsEditor();
		editor.setAsText("7");
		Assert.assertNotNull(editor.getValue());
	}
	
	/**
	 * @see ObsEditor#setAsText(String)
	 * @Verifies(value = "should set using uuid", method = "setAsText(String)")
	 */
	@Test
	public void setAsText_shouldSetUsingUuid() throws Exception {
		ObsEditor editor = new ObsEditor();
		editor.setAsText("39fb7f47-e80a-4056-9285-bd798be13c63");
		Assert.assertNotNull(editor.getValue());
	}
	
	/**
	 * @see ObsEditor#setAsText(String)
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception for obs not found", method = "setAsText(String)")
	public void setAsText_shouldThrowIllegalArgumentExceptionForObsNotFound() throws Exception {
		ObsEditor editor = new ObsEditor();
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Obs not found: ");
		editor.setAsText("49fb7f47-e80a-4056-9285-bd798be13c63");
		
	}
	
	/**
	 * @see ObsEditor#setAsText(String)
	 */
	@Test
	@Verifies(value = "should return null for empty text", method = "setAsText(String)")
	public void setAsText_shouldReturnNullForEmptyText() throws Exception {
		ObsEditor editor = new ObsEditor();
		editor.setAsText("");
		Assert.assertNull(editor.getValue());
	}
	
	/**
	 * @see ObsEditor#getAsText()
	 */
	@Test
	@Verifies(value = "should return empty string for non existing obs", method = "getAsText()")
	public void getAsText_shouldReturnEmptyStringForNonExistingObs() throws Exception {
		ObsEditor editor = new ObsEditor();
		editor.setAsText("");
		Assert.assertEquals("", editor.getAsText());
	}
	
	/**
	 * @see ObsEditor#getAsText()
	 */
	@Test
	@Verifies(value = "should return id as string for existing obs", method = "getAsText()")
	public void getAsText_shouldReturnIdAsStringForExistingObs() throws Exception {
		ObsEditor editor = new ObsEditor();
		editor.setAsText("7");
		Assert.assertEquals("7", editor.getAsText());
	}
}
