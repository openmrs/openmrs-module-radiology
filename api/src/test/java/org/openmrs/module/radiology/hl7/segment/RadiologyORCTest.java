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

import java.util.Calendar;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Order;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.CommonOrderPriority;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests methods in the {@link RadiologyORC}
 */
public class RadiologyORCTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private static EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	/**
	 * Tests the RadiologyORC.populateCommonOrder with all params
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyORC#populateCommonOrder(ORC, Study, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should return populated common order segment for given study", method = "populateCommonOrder(ORC, Study, CommonOrderOrderControl, CommonOrderPriority)")
	public void populateCommonOrder_shouldReturnPopulatedCommonOrderSegmentForGivenStudyAndOrder() throws HL7Exception {
		
		Order order = new Order();
		order.setOrderId(1);
		
		Calendar calendarOrderStartDate = Calendar.getInstance();
		calendarOrderStartDate.set(2015, Calendar.FEBRUARY, 04);
		calendarOrderStartDate.set(Calendar.HOUR_OF_DAY, 14);
		calendarOrderStartDate.set(Calendar.MINUTE, 35);
		calendarOrderStartDate.set(Calendar.SECOND, 00);
		order.setStartDate(calendarOrderStartDate.getTime());
		
		Study study = new Study();
		study.setId(1);
		study.setOrder(order);
		
		ORM_O01 message = new ORM_O01();
		RadiologyORC.populateCommonOrder(message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC(), study,
		    CommonOrderOrderControl.NEW_ORDER, CommonOrderPriority.STAT);
		
		ORC commonOrderSegment = message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC();
		assertThat(commonOrderSegment.getOrderControl().getValue(), is("NW"));
		assertThat(commonOrderSegment.getPlacerOrderNumber().getEntityIdentifier().getValue(), is("1"));
		assertThat(commonOrderSegment.getQuantityTiming().getStartDateTime().getTimeOfAnEvent().getValue(),
		    is("20150204143500"));
		assertThat(commonOrderSegment.getQuantityTiming().getPriority().getValue(), is("S"));
		
		assertThat(PipeParser.encode(commonOrderSegment, encodingCharacters), is("ORC|NW|1|||||^^^20150204143500^^S"));
	}
	
	/**
	 * Tests the RadiologyORC.populateCommonOrder passing null as ORC
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyORC#populateCommonOrder(ORC, Study, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should fail given null as commonOrderSegment", method = "populateCommonOrder(ORC, Study, CommonOrderOrderControl, CommonOrderPriority)")
	public void populateCommonOrder_shouldFailGivenNullAsCommonOrderSegment() throws HL7Exception {
		
		Study study = new Study();
		Order order = new Order();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("commonOrderSegment cannot be null."));
		RadiologyORC.populateCommonOrder(null, study, CommonOrderOrderControl.NEW_ORDER, CommonOrderPriority.STAT);
	}
	
	/**
	 * Tests the RadiologyORC.populateCommonOrder passing null as Study
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyORC#populateCommonOrder(ORC, Study, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should fail given null as study", method = "populateCommonOrder(ORC, Study, CommonOrderOrderControl, CommonOrderPriority)")
	public void populateCommonOrder_shouldFailGivenNullAsStudy() throws HL7Exception {
		
		Order order = new Order();
		ORM_O01 message = new ORM_O01();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("study cannot be null."));
		RadiologyORC.populateCommonOrder(message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC(), null,
		    CommonOrderOrderControl.NEW_ORDER, CommonOrderPriority.STAT);
	}
}
