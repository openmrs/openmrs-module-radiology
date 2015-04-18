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
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests methods in the {@link RadiologyMSH}
 */
public class RadiologyMSHTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private static EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	/**
	 * Tests the RadiologyMSH.populateMessageHeader given Sending Facility, Date/Time Of Message,
	 * Message Type (type and trigger event)
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyMSH#populateMessageHeader(MSH, String, Date, String, String)}
	 */
	@Test
	@Verifies(value = "should return populated message header segment given all parameters", method = "populateMessageHeader(MSH, String, Date, String, String)")
	public void populateMessageHeader_shouldReturnPopulatedMessageHeaderSegmentGivenAllParameters() throws HL7Exception {
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		
		ORM_O01 message = new ORM_O01();
		
		RadiologyMSH.populateMessageHeader(message.getMSH(), "OpenMRSRadiology", "OpenMRS", cal.getTime(), "ORM", "O01");
		assertThat(PipeParser.encode(message, encodingCharacters),
		    is("MSH|^~\\&|OpenMRSRadiology|OpenMRS|||20130228222510||ORM^O01||P|2.3.1\r"));
	}
	
	/**
	 * Tests the RadiologyMSH.populateMessageHeader given empty string Sending Application
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyMSH#populateMessageHeader(MSH, String, Date, String, String)}
	 */
	@Test
	@Verifies(value = "should return populated message header segment given empty sending application", method = "populateMessageHeader(MSH, String, Date, String, String)")
	public void populateMessageHeader_shouldReturnPopulatedMessageHeaderSegmentGivenEmptySendingApplication()
	        throws HL7Exception {
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		
		ORM_O01 message = new ORM_O01();
		
		RadiologyMSH.populateMessageHeader(message.getMSH(), "", "OpenMRS", cal.getTime(), "ORM", "O01");
		assertThat(PipeParser.encode(message, encodingCharacters),
		    is("MSH|^~\\&||OpenMRS|||20130228222510||ORM^O01||P|2.3.1\r"));
	}
	
	/**
	 * Tests the RadiologyMSH.populateMessageHeader given empty string Sending Facility
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyMSH#populateMessageHeader(MSH, String, Date, String, String)}
	 */
	@Test
	@Verifies(value = "should return populated message header segment given empty sending facility", method = "populateMessageHeader(MSH, String, Date, String, String)")
	public void populateMessageHeader_shouldReturnPopulatedMessageHeaderSegmentGivenEmptySendingFacility()
	        throws HL7Exception {
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		
		ORM_O01 message = new ORM_O01();
		
		RadiologyMSH.populateMessageHeader(message.getMSH(), "OpenMRSRadiology", "", cal.getTime(), "ORM", "O01");
		assertThat(PipeParser.encode(message, encodingCharacters),
		    is("MSH|^~\\&|OpenMRSRadiology||||20130228222510||ORM^O01||P|2.3.1\r"));
	}
	
	/**
	 * Tests the RadiologyMSH.populateMessageHeader given empty string Message Type (type)
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyMSH#populateMessageHeader(MSH, String, Date, String, String)}
	 */
	@Test
	@Verifies(value = "should return populated message header segment given empty message type", method = "populateMessageHeader(MSH, String, Date, String, String)")
	public void populateMessageHeader_shouldReturnPopulatedMessageHeaderSegmentGivenEmptyMessageType() throws HL7Exception {
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		
		ORM_O01 message = new ORM_O01();
		
		RadiologyMSH.populateMessageHeader(message.getMSH(), "OpenMRSRadiology", "OpenMRS", cal.getTime(), "", "O01");
		assertThat(PipeParser.encode(message, encodingCharacters),
		    is("MSH|^~\\&|OpenMRSRadiology|OpenMRS|||20130228222510||^O01||P|2.3.1\r"));
	}
	
	/**
	 * Tests the RadiologyMSH.populateMessageHeader given empty string Message Type (trigger event)
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyMSH#populateMessageHeader(MSH, String, Date, String, String)}
	 */
	@Test
	@Verifies(value = "should return populated message header segment given empty message trigger", method = "populateMessageHeader(MSH, String, Date, String, String)")
	public void populateMessageHeader_shouldReturnPopulatedMessageHeaderSegmentGivenEmptyMessageTrigger()
	        throws HL7Exception {
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		
		ORM_O01 message = new ORM_O01();
		
		RadiologyMSH.populateMessageHeader(message.getMSH(), "OpenMRSRadiology", "OpenMRS", cal.getTime(), "ORM", "");
		assertThat(PipeParser.encode(message, encodingCharacters),
		    is("MSH|^~\\&|OpenMRSRadiology|OpenMRS|||20130228222510||ORM||P|2.3.1\r"));
	}
	
	/**
	 * Tests the RadiologyMSH.populateMessageHeader given null as Date/Time Of Message
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyMSH#populateMessageHeader(MSH, String, Date, String, String)}
	 */
	@Test
	@Verifies(value = "should return populated message header segment given null as date time of message", method = "populateMessageHeader(MSH, String, Date, String, String)")
	public void populateMessageHeader_shouldReturnPopulatedMessageHeaderSegmentGivenNullAsDateTimeOfMessage()
	        throws HL7Exception {
		
		ORM_O01 message = new ORM_O01();
		
		RadiologyMSH.populateMessageHeader(message.getMSH(), "OpenMRSRadiology", "OpenMRS", null, "ORM", "O01");
		assertThat(PipeParser.encode(message, encodingCharacters),
		    is("MSH|^~\\&|OpenMRSRadiology|OpenMRS|||||ORM^O01||P|2.3.1\r"));
	}
	
	/**
	 * Tests the RadiologyMSH.populateMessageHeader given null as MSH
	 * 
	 * @throws HL7Exception
	 * @see {@link RadiologyMSH#populateMessageHeader(MSH, String, Date, String, String)}
	 */
	@Test
	@Verifies(value = "should fail given null as msh", method = "populateMessageHeader(MSH, String, Date, String, String)")
	public void populateMessageHeader_shouldFailGivenNullAsMSH() throws HL7Exception {
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.FEBRUARY, 28);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 25);
		cal.set(Calendar.SECOND, 10);
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("messageHeaderSegment cannot be null."));
		RadiologyMSH.populateMessageHeader(null, "OpenMRSRadiology", "OpenMRS", cal.getTime(), "ORM", "O01");
	}
}
