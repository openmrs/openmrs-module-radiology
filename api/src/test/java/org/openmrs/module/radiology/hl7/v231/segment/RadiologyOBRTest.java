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
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Order;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.hl7.HL7Constants;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.study.Study;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.segment.OBR;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests {@link RadiologyOBR}
 */
public class RadiologyOBRTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * @see {@link RadiologyOBR#populateObservationRequest(OBR, RadiologyOrder)}
	 */
	@Test
	@Verifies(value = "should return populated observation request segment given all params", method = "populateObservationRequest(OBR, RadiologyOrder)")
	public void populateObservationRequest_shouldReturnPopulatedObservationRequestSegmentGivenAllParams() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setOrderId(1);
		
		Field orderNumber = Order.class.getDeclaredField("orderNumber");
		orderNumber.setAccessible(true);
		orderNumber.set(radiologyOrder, "ORD-" + radiologyOrder.getOrderId());
		
		radiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Study study = new Study();
		study.setStudyId(1);
		study.setModality(Modality.CT);
		radiologyOrder.setStudy(study);
		
		ORM_O01 message = new ORM_O01();
		RadiologyOBR.populateObservationRequest(message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
				.getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
				.getOBR(), radiologyOrder);
		
		OBR observationRequestSegment = message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
				.getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
				.getOBR();
		assertThat(observationRequestSegment.getUniversalServiceID()
				.getAlternateText()
				.getValue(), is("CT ABDOMEN PANCREAS WITH IV CONTRAST"));
		assertThat(observationRequestSegment.getPlacerField2()
				.getValue(), is("ORD-1"));
		assertThat(observationRequestSegment.getFillerField1()
				.getValue(), is("1"));
		assertThat(observationRequestSegment.getDiagnosticServSectID()
				.getValue(), is("CT"));
		assertThat(observationRequestSegment.getProcedureCode()
				.getText()
				.getValue(), is("CT ABDOMEN PANCREAS WITH IV CONTRAST"));
		assertThat(
			PipeParser.encode(observationRequestSegment, HL7Constants.ENCODING_CHARACTERS),
			is("OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||ORD-1|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST"));
	}
	
	/**
	 * @see {@link RadiologyOBR#populateObservationRequest(OBR, RadiologyOrder)}
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null as observation request segment", method = "populateObservationRequest(OBR, RadiologyOrder)")
	public void populateObservationRequest_shouldThrowIllegalArgumentExceptionGivenNullAsObservationRequestSegment()
			throws HL7Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setStudy(new Study());
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("observationRequestSegment cannot be null."));
		RadiologyOBR.populateObservationRequest(null, radiologyOrder);
	}
	
	/**
	 * @see {@link RadiologyOBR#populateObservationRequest(OBR, RadiologyOrder)}
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null as radiology order", method = "populateObservationRequest(OBR, RadiologyOrder)")
	public void populateObservationRequest_shouldThrowIllegalArgumentExceptionGivenNullAsRadiologyOrder()
			throws HL7Exception {
		
		ORM_O01 message = new ORM_O01();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("radiologyOrder cannot be null."));
		RadiologyOBR.populateObservationRequest(message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
				.getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
				.getOBR(), null);
	}
	
	/**
	 * @see {@link RadiologyOBR#populateObservationRequest(OBR, RadiologyOrder)}
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception if given radiology orders study is null", method = "populateObservationRequest(OBR, RadiologyOrder)")
	public void populateObservationRequest_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrdersStudyIsNull()
			throws HL7Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		ORM_O01 message = new ORM_O01();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("radiologyOrder.study cannot be null."));
		RadiologyOBR.populateObservationRequest(message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
				.getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
				.getOBR(), radiologyOrder);
	}
}
