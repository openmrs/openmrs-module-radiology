/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openmrs.PersonName;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.datatype.XPN;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests methods in the {@link HL7Utils}
 */
public class HL7UtilsTest {
	
	private static EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	/**
	 * Test HL7Utils.getExtendedPersonNameFrom
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given person name with family given and middlename", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameForGivenPersonNameWithFamilyGivenAndMiddleName()
	        throws DataTypeException {
		
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String extendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(extendedPersonName, is("Doe^John^Francis"));
	}
	
	/**
	 * Test HL7Utils.getExtendedPersonNameFrom
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given person name with familyname", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameForGivenPersonNameWithFamilyName()
	        throws DataTypeException {
		
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String extendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(extendedPersonName, is("Doe"));
	}
	
	/**
	 * Test HL7Utils.getExtendedPersonNameFrom
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given person name with givenname", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameForGivenPersonNameWithGivenName()
	        throws DataTypeException {
		
		PersonName personName = new PersonName();
		personName.setGivenName("John");
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String extendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(extendedPersonName, is("^John"));
	}
	
	/**
	 * Test HL7Utils.getExtendedPersonNameFrom
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given person name with middlename", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameForGivenPersonNameWithMiddleName()
	        throws DataTypeException {
		
		PersonName personName = new PersonName();
		personName.setMiddleName("Francis");
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String extendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(extendedPersonName, is("^^Francis"));
	}
	
	/**
	 * Test HL7Utils.getExtendedPersonNameFrom
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given person name with family and givenname", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameForGivenPersonNameWithFamilyAndGivenName()
	        throws DataTypeException {
		
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String extendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(extendedPersonName, is("Doe^John"));
	}
	
	/**
	 * Test HL7Utils.getExtendedPersonNameFrom
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given empty person name", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameForGivenEmptyPersonName() throws DataTypeException {
		
		PersonName personName = new PersonName();
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String extendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(extendedPersonName, is(""));
	}
	
	/**
	 * Test HL7Utils.getExtendedPersonNameFrom
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return empty extended person name given null", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnEmptyExtendedPersonNameGivenNull() throws DataTypeException {
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(null);
		String extendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(extendedPersonName, is(""));
	}
}
