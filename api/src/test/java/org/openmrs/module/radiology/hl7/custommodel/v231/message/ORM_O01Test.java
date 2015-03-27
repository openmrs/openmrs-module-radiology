/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.custommodel.v231.message;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openmrs.module.radiology.hl7.custommodel.v231.segment.ZDS;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.OBR;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests methods in the {@link ORM_O01}
 */
public class ORM_O01Test {
	
	private static EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	/**
	 * Test ORM_O01.getZDS()
	 * 
	 * @throws HL7Exception
	 * @see {@link ORM_O01#getZDS()}
	 */
	@Test
	@Verifies(value = "should return zds segment", method = "getZDS()")
	public void getZDS_shouldReturnZDSSegment() throws HL7Exception {
		
		ORM_O01 ormMessage = new ORM_O01();
		ZDS zds = ormMessage.getZDS();
		zds.getStudyInstanceUID().getPointer().setValue("1.2.826.0.1.3680043.8.2186.1.1.1");
		zds.getStudyInstanceUID().getApplicationID().getNamespaceID().setValue("1");
		zds.getStudyInstanceUID().getTypeOfData().setValue("Application");
		zds.getStudyInstanceUID().getSubtype().setValue("DICOM");
		assertThat(PipeParser.encode(ormMessage, encodingCharacters),
		    is("ZDS|1.2.826.0.1.3680043.8.2186.1.1.1^1^Application^DICOM\r"));
	}
	
	/**
	 * Tests the ORM_O01 by creating a new message, populating, encoding and comparing it to the
	 * expected encoded hl7 message string
	 * 
	 * @throws HL7Exception
	 * @see {@link ORM_O01#ORM_O01()}
	 */
	@Test
	@Verifies(value = "should create new ormo01 instance", method = "ORM_O01()")
	public void ORM_O01_shouldCreateNewORMO01Instance() throws HL7Exception {
		
		String expectedEncodedCustomOrmMessage = "MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||20130827154500||ORM^O01||P|2.3.1\r"
		        + "PID|||100-07||Patient^Johnny^D||19651007|M\r"
		        + "ORC|NW|2|||||^^^20130824170000^^R\r"
		        + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||2|2||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		        + "ZDS|1.2.826.0.1.3680043.8.2186.1.1.1^1^Application^DICOM\r";
		
		ORM_O01 customOrmMessage = new ORM_O01();
		
		MSH mshCustom = customOrmMessage.getMSH();
		mshCustom.getEncodingCharacters().setValue("^~\\&");
		mshCustom.getVersionID().getVersionID().setValue("2.3.1");
		mshCustom.getMessageType().getMessageType().setValue("ORM");
		mshCustom.getMessageType().getTriggerEvent().setValue("O01");
		mshCustom.getSendingApplication().getNamespaceID().setValue("OpenMRSRadiologyModule");
		mshCustom.getSendingFacility().getNamespaceID().setValue("OpenMRS");
		mshCustom.getProcessingID().getProcessingID().setValue("P");
		mshCustom.getDateTimeOfMessage().getTimeOfAnEvent().setValue("20130827154500");
		
		PID pid = customOrmMessage.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1().getPID();
		pid.getPatientIdentifierList(0).getID().setValue("100-07");
		pid.getDateTimeOfBirth().getTimeOfAnEvent().setValue("19651007");
		pid.getSex().setValue("M");
		
		pid.getPatientName(0).getFamilyLastName().getFamilyName().setValue("Patient");
		pid.getPatientName(0).getMiddleInitialOrName().setValue("D");
		pid.getPatientName(0).getGivenName().setValue("Johnny");
		
		ORC orc = customOrmMessage.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC();
		orc.getOrderControl().setValue("NW");
		orc.getPlacerOrderNumber().getEntityIdentifier().setValue("2");
		orc.getQuantityTiming().getStartDateTime().getTimeOfAnEvent().setValue("20130824170000");
		orc.getQuantityTiming().getPriority().setValue("R");
		
		OBR obr = customOrmMessage.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG()
		        .getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE().getOBR();
		obr.getUniversalServiceID().getAlternateText().setValue("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		obr.getPlacerField2().setValue("2");
		obr.getFillerField1().setValue("2");
		obr.getDiagnosticServSectID().setValue("CT");
		obr.getProcedureCode().getText().setValue("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		ZDS zds = customOrmMessage.getZDS();
		zds.getStudyInstanceUID().getPointer().setValue("1.2.826.0.1.3680043.8.2186.1.1.1");
		zds.getStudyInstanceUID().getApplicationID().getNamespaceID().setValue("1");
		zds.getStudyInstanceUID().getTypeOfData().setValue("Application");
		zds.getStudyInstanceUID().getSubtype().setValue("DICOM");
		
		assertThat(PipeParser.encode(customOrmMessage, encodingCharacters), is(expectedEncodedCustomOrmMessage));
	}
	
}
