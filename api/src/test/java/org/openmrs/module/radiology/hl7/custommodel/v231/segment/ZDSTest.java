/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.custommodel.v231.segment;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.v231.datatype.RP;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests {@link ZDS}
 */
public class ZDSTest {
	
	private static EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	/**
	 * Tests ZDS constructor and sets fields
	 * 
	 * @throws HL7Exception
	 * @see {@link ZDS#ZDS(Group, ModelClassFactory)}
	 */
	@Test
	@Verifies(value = "should create a new ZDS instance given all params", method = "ZDS(Group, ModelClassFactory)")
	public void constructor_shouldCreateANewZDSInstanceGivenAllParams() throws HL7Exception {
		
		ORM_O01 result = new ORM_O01();
		ZDS zds = new ZDS(result.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG(), new DefaultModelClassFactory());
		
		assertNotNull(zds.getStudyInstanceUID());
		
		zds.getStudyInstanceUID().getPointer().setValue("1.2.826.0.1.3680043.8.2186.1.1.1");
		zds.getStudyInstanceUID().getApplicationID().getNamespaceID().setValue("1");
		zds.getStudyInstanceUID().getTypeOfData().setValue("Application");
		zds.getStudyInstanceUID().getSubtype().setValue("DICOM");
		
		assertThat(PipeParser.encode(zds, encodingCharacters),
		    is("ZDS|1.2.826.0.1.3680043.8.2186.1.1.1^1^Application^DICOM"));
	}
	
	/**
	 * Test ZDS.getStudyInstanceUID
	 * 
	 * @throws HL7Exception
	 * @see {@link ZDS#getStudyInstanceUID()}
	 */
	@Test
	@Verifies(value = "should return study instance uid field", method = "getStudyInstanceUID()")
	public void getStudyInstanceUID_shouldReturnStudyInstanceUid() throws HL7Exception {
		
		ORM_O01 result = new ORM_O01();
		ZDS zds = new ZDS(result.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG(), new DefaultModelClassFactory());
		
		RP studyInstanceUidField = zds.getStudyInstanceUID();
		
		studyInstanceUidField.getPointer().setValue("1.2.826.0.1.3680043.8.2186.1.1.1");
		assertThat(studyInstanceUidField.getPointer().getValue(), is("1.2.826.0.1.3680043.8.2186.1.1.1"));
	}
	
}
