/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.v231.segment;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.module.radiology.hl7.HL7Constants;
import org.openmrs.module.radiology.hl7.custommodel.v231.segment.ZDS;
import org.openmrs.module.radiology.study.RadiologyStudy;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Test {@link RadiologyZDS}
 */
public class RadiologyZDSTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * Tests the RadiologyZDS.populateZDSSegment with RadiologyStudy containing RadiologyStudy.studyInstanceUid
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyZDS#populateZDSSegment(ZDS, RadiologyStudy)}
	 */
	@Test
	@Verifies(value = "should return populated zds segment for given study", method = "populateZDSSegment(ZDS, RadiologyStudy)")
	public void populateZDSSegment_shouldReturnPopulatedZDSSegmentForGivenStudy() throws HL7Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1.1");
		
		ORM_O01 message = new ORM_O01();
		ZDS zds = new ZDS(message, new DefaultModelClassFactory());
		
		RadiologyZDS.populateZDSSegment(zds, radiologyStudy);
		
		assertThat(zds.getStudyInstanceUID()
				.getPointer()
				.getValue(), is("1.2.826.0.1.3680043.8.2186.1.1.1"));
		assertNull(zds.getStudyInstanceUID()
				.getApplicationID()
				.getNamespaceID()
				.getValue());
		assertThat(zds.getStudyInstanceUID()
				.getTypeOfData()
				.getValue(), is("Application"));
		assertThat(zds.getStudyInstanceUID()
				.getSubtype()
				.getValue(), is("DICOM"));
		
		assertThat(PipeParser.encode(zds, HL7Constants.ENCODING_CHARACTERS),
			is("ZDS|1.2.826.0.1.3680043.8.2186.1.1.1^^Application^DICOM"));
	}
	
	/**
	 * Tests the RadiologyZDS.populateZDSSegment given RadiologyStudy with non-set RadiologyStudy.studyInstanceUid
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyZDS#populateZDSSegment(ZDS, RadiologyStudy)}
	 */
	@Test
	@Verifies(value = "should return populated zds segment for given study with non-set uid", method = "populateZDSSegment(ZDS, RadiologyStudy)")
	public void populateZDSSegment_shouldReturnPopulatedZDSSegmentForGivenStudyWithNonSetUid() throws HL7Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		
		ORM_O01 message = new ORM_O01();
		ZDS zds = new ZDS(message, new DefaultModelClassFactory());
		
		RadiologyZDS.populateZDSSegment(zds, radiologyStudy);
		
		assertNull(zds.getStudyInstanceUID()
				.getPointer()
				.getValue());
		assertNull(zds.getStudyInstanceUID()
				.getApplicationID()
				.getNamespaceID()
				.getValue());
		assertThat(zds.getStudyInstanceUID()
				.getTypeOfData()
				.getValue(), is("Application"));
		assertThat(zds.getStudyInstanceUID()
				.getSubtype()
				.getValue(), is("DICOM"));
	}
	
	/**
	 * Tests the RadiologyZDS.populateZDSSegment given RadiologyStudy with empty RadiologyStudy.studyInstanceUid
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyZDS#populateZDSSegment(ZDS, RadiologyStudy)}
	 */
	@Test
	@Verifies(value = "should return populated zds segment for given study with empty uid", method = "addNonstandardZDSSegment(Message, RadiologyStudy)")
	public void populateZDSSegment_shouldReturnPopulatedZDSSegmentForGivenStudyWithEmptyUid() throws HL7Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setStudyInstanceUid("");
		
		ORM_O01 message = new ORM_O01();
		ZDS zds = new ZDS(message, new DefaultModelClassFactory());
		
		RadiologyZDS.populateZDSSegment(zds, radiologyStudy);
		
		assertThat(zds.getStudyInstanceUID()
				.getPointer()
				.getValue(), is(""));
		assertNull(zds.getStudyInstanceUID()
				.getApplicationID()
				.getNamespaceID()
				.getValue());
		assertThat(zds.getStudyInstanceUID()
				.getTypeOfData()
				.getValue(), is("Application"));
		assertThat(zds.getStudyInstanceUID()
				.getSubtype()
				.getValue(), is("DICOM"));
	}
	
	/**
	 * Tests the RadiologyZDS.populateZDSSegment given null as ZDS
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyZDS#populateZDSSegment(ZDS, RadiologyStudy)}
	 */
	@Test
	@Verifies(value = "should fail given null as zds", method = "populateZDSSegment(ZDS, RadiologyStudy)")
	public void populateZDSSegment_shouldFailGivenNullAsZDS() throws HL7Exception {
		
		RadiologyStudy radiologyStudy = new RadiologyStudy();
		radiologyStudy.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1.1");
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("zds cannot be null."));
		RadiologyZDS.populateZDSSegment(null, radiologyStudy);
	}
	
	/**
	 * Tests the RadiologyZDS.populateZDSSegment given null as RadiologyStudy
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyZDS#populateZDSSegment(ZDS, RadiologyStudy)}
	 */
	@Test
	@Verifies(value = "should fail given null as study", method = "populateZDSSegment(ZDS, RadiologyStudy)")
	public void populateZDSSegment_shouldFailGivenNullAsStudy() throws HL7Exception {
		
		ORM_O01 message = new ORM_O01();
		ZDS zds = new ZDS(message, new DefaultModelClassFactory());
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("study cannot be null."));
		RadiologyZDS.populateZDSSegment(zds, null);
	}
}
