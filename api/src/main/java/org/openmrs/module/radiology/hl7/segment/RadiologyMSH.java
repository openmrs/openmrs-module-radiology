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

import java.util.Date;

import org.openmrs.module.radiology.utils.DateTimeUtils;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.segment.MSH;

/**
 * RadiologyMSH is a utility class populating an HL7 Message Header
 */
public class RadiologyMSH {
	
	private RadiologyMSH() {
		// This class is a utility class which should not be instantiated
	};
	
	/**
	 * Fill HL7 (version 2.3.1) Message Header (MSH) with Sending Facility, Date/Time Of Message,
	 * Message Type (type and trigger event) from parameters. Field Separator, Encoding Characters,
	 * Processing ID, Version ID from constants.
	 * 
	 * @param messageHeaderSegment Message Header Segment to populate
	 * @param sendingApplication corresponds to MSH-3: Sending Application
	 * @param sendingFacility corresponds to MSH-4: Sending Facility
	 * @param dateTimeOfMessage corresponds to MSH-7: Date/Time Of Message
	 * @param messageType corresponds to MSH-9: Message Type (message type (ID))
	 * @param messageTriggerEvent corresponds to MSH-9: Message Type (trigger event (ID))
	 * @return populated messageHeaderSegment segment
	 * @throws DataTypeException
	 * @should return populated message header segment given all parameters
	 * @should return populated message header segment given empty sending application
	 * @should return populated message header segment given empty sending facility
	 * @should return populated message header segment given empty message type
	 * @should return populated message header segment given empty message trigger
	 * @should return populated message header segment given null as date time of message
	 * @should fail given null as message header segment
	 */
	public static MSH populateMessageHeader(MSH messageHeaderSegment, String sendingApplication, String sendingFacility,
			Date dateTimeOfMessage, String messageType, String messageTriggerEvent) throws DataTypeException {
		
		if (messageHeaderSegment == null) {
			throw new IllegalArgumentException("messageHeaderSegment cannot be null.");
		}
		
		messageHeaderSegment.getFieldSeparator()
				.setValue("|");
		messageHeaderSegment.getEncodingCharacters()
				.setValue("^~\\&");
		messageHeaderSegment.getSendingApplication()
				.getNamespaceID()
				.setValue(sendingApplication);
		messageHeaderSegment.getSendingFacility()
				.getNamespaceID()
				.setValue(sendingFacility);
		messageHeaderSegment.getDateTimeOfMessage()
				.getTimeOfAnEvent()
				.setValue(DateTimeUtils.getPlainDateTimeFrom(dateTimeOfMessage));
		messageHeaderSegment.getMessageType()
				.getMessageType()
				.setValue(messageType);
		messageHeaderSegment.getMessageType()
				.getTriggerEvent()
				.setValue(messageTriggerEvent);
		messageHeaderSegment.getProcessingID()
				.getProcessingID()
				.setValue("P");
		messageHeaderSegment.getVersionID()
				.getVersionID()
				.setValue("2.3.1");
		
		return messageHeaderSegment;
	}
}
