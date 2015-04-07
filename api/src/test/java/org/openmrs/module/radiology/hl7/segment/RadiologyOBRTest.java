/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.segment;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Order;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.Study;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.segment.OBR;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests methods in the {@link RadiologyOBR}
 */
public class RadiologyOBRTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private static EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	/**
	 * Tests the RadiologyOBR.populateObservationRequest with all params
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyOBR#populateObservationRequest(OBR, Study, Order)}
	 */
	@Test
	@Verifies(value = "should return populated observation request segment for given study and order", method = "populateObservationRequest(OBR, Study, Order)")
	public void populateObservationRequest_shouldReturnPopulatedObservationRequestSegmentForGivenStudyAndOrder()
	        throws HL7Exception {
		
		Order order = new Order();
		order.setId(1);
		order.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Study study = new Study();
		study.setId(1);
		study.setModality(Modality.CT);
		study.setOrderID(1);
		
		ORM_O01 message = new ORM_O01();
		RadiologyOBR.populateObservationRequest(message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
		        .getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE().getOBR(), study, order);
		
		OBR observationRequestSegment = message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
		        .getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE().getOBR();
		assertThat(observationRequestSegment.getUniversalServiceID().getAlternateText().getValue(),
		    is("CT ABDOMEN PANCREAS WITH IV CONTRAST"));
		assertThat(observationRequestSegment.getPlacerField2().getValue(), is("1"));
		assertThat(observationRequestSegment.getFillerField1().getValue(), is("1"));
		assertThat(observationRequestSegment.getDiagnosticServSectID().getValue(), is("CT"));
		assertThat(observationRequestSegment.getProcedureCode().getText().getValue(),
		    is("CT ABDOMEN PANCREAS WITH IV CONTRAST"));
		assertThat(
		    PipeParser.encode(observationRequestSegment, encodingCharacters),
		    is("OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||1|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST"));
	}
	
	/**
	 * Tests the RadiologyOBR.populateObservationRequest passing null as OBR
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyOBR#populateObservationRequest(OBR, Study, Order)}
	 */
	@Test
	@Verifies(value = "should fail given null as obr", method = "populateObservationRequest(OBR, Study, Order)")
	public void populateObservationRequest_shouldFailGivenNullAsORC() throws HL7Exception {
		
		Study study = new Study();
		Order order = new Order();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("observationRequestSegment cannot be null."));
		RadiologyOBR.populateObservationRequest(null, study, order);
	}
	
	/**
	 * Tests the RadiologyOBR.populateObservationRequest passing null as Study
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyOBR#populateObservationRequest(OBR, Study, Order)}
	 */
	@Test
	@Verifies(value = "should fail given null as study", method = "populateObservationRequest(OBR, Study, Order)")
	public void populateObservationRequest_shouldFailGivenNullAsStudy() throws HL7Exception {
		
		Order order = new Order();
		ORM_O01 message = new ORM_O01();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("study cannot be null."));
		RadiologyOBR.populateObservationRequest(message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
		        .getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE().getOBR(), null, order);
	}
	
	/**
	 * Tests the RadiologyOBR.populateObservationRequest passing null as Order
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyOBR#populateObservationRequest(OBR, Study, Order)}
	 */
	@Test
	@Verifies(value = "should fail given null as order", method = "populateObservationRequest(OBR, Study, Order)")
	public void populateObservationRequest_shouldFailGivenNullAsOrder() throws HL7Exception {
		
		Study study = new Study();
		ORM_O01 message = new ORM_O01();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("order cannot be null."));
		RadiologyOBR.populateObservationRequest(message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
		        .getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE().getOBR(), study, null);
	}
}
