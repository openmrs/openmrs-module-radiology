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
	 * Tests the HL7Utils.getExtendedPersonNameFrom with a PersonName including
	 * Family-/Given-/MiddleName and Degree
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given person name with family given and middlename and degree", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameGivenPersonNameWithFamilyGivenAndMiddleNameAndDegree()
	        throws DataTypeException {
		
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String hl7ExtendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(hl7ExtendedPersonName, is("Doe^John^Francis"));
	}
	
	/**
	 * Tests the HL7Utils.getExtendedPersonNameFrom with a PersonName including FamilyName
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given person name with familyname", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameGivenPersonNameWithFamilyName()
	        throws DataTypeException {
		
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String hl7ExtendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(hl7ExtendedPersonName, is("Doe"));
	}
	
	/**
	 * Tests the HL7Utils.getExtendedPersonNameFrom with a PersonName including GivenName
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given person name with givenname", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameGivenPersonNameWithGivenName()
	        throws DataTypeException {
		
		PersonName personName = new PersonName();
		personName.setGivenName("John");
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String hl7ExtendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(hl7ExtendedPersonName, is("^John"));
	}
	
	/**
	 * Tests the HL7Utils.getExtendedPersonNameFrom with a PersonName including MiddleName
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given person name with middlename", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameGivenPersonNameWithMiddleName()
	        throws DataTypeException {
		
		PersonName personName = new PersonName();
		personName.setMiddleName("Francis");
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String hl7ExtendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(hl7ExtendedPersonName, is("^^Francis"));
	}
	
	/**
	 * Tests the HL7Utils.getExtendedPersonNameFrom with a PersonName including Family-/GivenName
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given person name with family and givenname", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameGivenPersonNameWithFamilyAndGivenName()
	        throws DataTypeException {
		
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String hl7ExtendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(hl7ExtendedPersonName, is("Doe^John"));
	}
	
	/**
	 * Tests the HL7Utils.getExtendedPersonNameFrom with an initialized but empty PersonName
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name for given initialized but empty person name", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameGivenEmptyPersonName() throws DataTypeException {
		
		PersonName personName = new PersonName();
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(personName);
		String hl7ExtendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(hl7ExtendedPersonName, is(""));
	}
	
	/**
	 * Tests the HL7Utils.getExtendedPersonNameFrom passing null
	 * 
	 * @throws DataTypeException
	 * @see {@link HL7Utils#getExtendedPersonNameFrom(PersonName)}
	 */
	@Test
	@Verifies(value = "should return extended person name given null", method = "getExtendedPersonNameFrom(PersonName)")
	public void getExtendedPersonNameFrom_shouldReturnExtendedPersonNameGivenNull() throws DataTypeException {
		
		XPN xpn = HL7Utils.getExtendedPersonNameFrom(null);
		String hl7ExtendedPersonName = PipeParser.encode(xpn, encodingCharacters);
		
		assertThat(hl7ExtendedPersonName, is(""));
	}
}
