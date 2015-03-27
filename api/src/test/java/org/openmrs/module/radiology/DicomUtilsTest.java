/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.Study.Modality;
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.CommonOrderPriority;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.OBR;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;

/**
 * Tests methods in the {@link DicomUtils}
 */
public class DicomUtilsTest {
	
	/**
	 * Tests the DicomUtils.createHL7Message method with a study with mwlstatus = 0 and
	 * OrderRequest.Save_Order
	 * 
	 * @throws Exception
	 * @see {@link DicomUtils#createHL7Message(Study, Order, OrderRequest)}
	 */
	@Test
	@Verifies(value = "should return encoded HL7 ORMO01 message string with new order control given study with mwlstatus zero and save order request", method = "createHL7Message(Study, Order, OrderRequest)")
	public void createHL7Message_shouldReturnEncodedHL7ORMO01MessageStringWithNewOrderControlGivenStudyWithMwlstatusZeroAndSaveOrderRequest()
	        throws Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		patient.setBirthdate(cal.getTime());
		
		Order order = new Order();
		order.setId(20);
		order.setPatient(patient);
		cal.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		order.setStartDate(cal.getTime());
		order.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Study study = new Study();
		study.setId(1);
		study.setOrderID(order.getId());
		study.setUid("1.2.826.0.1.3680043.8.2186.1.1");
		study.setModality(2);
		study.setPriority(0);
		study.setMwlStatus(0);
		
		String saveOrderHL7String = DicomUtils.createHL7Message(study, order, DicomUtils.OrderRequest.Save_Order);
		
		assertThat(saveOrderHL7String, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    saveOrderHL7String,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|NW|1|||||^^^20150204143500^^S\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||1|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
		
		PipeParser hl7PipeParser = new PipeParser();
		Message saveOrderHL7Message = hl7PipeParser.parse(saveOrderHL7String);
		ORM_O01 ormMsg = (ORM_O01) saveOrderHL7Message;
		
		// MSH segment
		MSH msh = ormMsg.getMSH();
		assertEquals("2.3.1", msh.getVersionID().getVersionID().getValue());
		assertEquals("ORM", msh.getMessageType().getMessageType().getValue());
		assertEquals("O01", msh.getMessageType().getTriggerEvent().getValue());
		assertEquals("OpenMRSRadiologyModule", msh.getSendingApplication().getNamespaceID().getValue());
		assertEquals("OpenMRS", msh.getSendingFacility().getNamespaceID().getValue());
		assertEquals("P", msh.getProcessingID().getProcessingID().getValue());
		
		// PID segment
		Patient expectedPatient = order.getPatient();
		PID pid = ormMsg.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1().getPID();
		assertEquals(expectedPatient.getPatientIdentifier().getIdentifier(), pid.getPatientIdentifierList(0).getID()
		        .getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(expectedPatient.getBirthdate()), pid.getDateTimeOfBirth()
		        .getTimeOfAnEvent().getValue());
		assertEquals(expectedPatient.getGender(), pid.getSex().getValue());
		assertEquals(expectedPatient.getPersonName().getFamilyName(), pid.getPatientName(0).getFamilyLastName()
		        .getFamilyName().getValue());
		assertEquals(expectedPatient.getPersonName().getMiddleName(), pid.getPatientName(0).getMiddleInitialOrName()
		        .getValue());
		assertEquals(expectedPatient.getPersonName().getGivenName(), pid.getPatientName(0).getGivenName().getValue());
		
		// ORC segment
		ORC orc = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC();
		assertEquals("NW", orc.getOrderControl().getValue());
		assertEquals(String.valueOf(study.getId()), orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
		assertEquals(null, orc.getOrderStatus().getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(order.getStartDate()), orc.getQuantityTiming()
		        .getStartDateTime().getTimeOfAnEvent().getValue());
		assertEquals("S", orc.getQuantityTiming().getPriority().getValue());
		
		// OBR segment
		OBR obr = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
		        .getOBR();
		assertEquals(order.getInstructions(), obr.getUniversalServiceID().getAlternateText().getValue());
		assertEquals(String.valueOf(study.getId()), obr.getPlacerField2().getValue());
		assertEquals(String.valueOf(study.getId()), obr.getFillerField1().getValue());
		assertEquals(Modality.values()[study.getModality()].toString(), obr.getDiagnosticServSectID().getValue());
		assertEquals(order.getInstructions(), obr.getProcedureCode().getText().getValue());
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertEquals(study.getUid(), terser.get("/.ZDS-1-1"));
		assertEquals(null, terser.get("/.ZDS-1-2"));
		assertEquals("Application", terser.get("/.ZDS-1-3"));
		assertEquals("DICOM", terser.get("/.ZDS-1-4"));
	}
	
	/**
	 * Tests the DicomUtils.createHL7Message method with a study with mwlstatus = 0 and
	 * OrderRequest.Void_Order
	 * 
	 * @throws Exception
	 * @see {@link DicomUtils#createHL7Message(Study, Order, OrderRequest)}
	 */
	@Test
	@Verifies(value = "should return encoded HL7 ORMO01 message string with cancel order control given study with mwlstatus zero and void order request", method = "createHL7Message(Study, Order, OrderRequest)")
	public void createHL7Message_shouldReturnEncodedHL7ORMO01MessageStringWithCancelOrderControlGivenStudyWithMwlstatusZeroAndVoidOrderRequest()
	        throws Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		patient.setBirthdate(cal.getTime());
		
		Order order = new Order();
		order.setId(20);
		order.setPatient(patient);
		cal.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		order.setStartDate(cal.getTime());
		order.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Study study = new Study();
		study.setId(1);
		study.setOrderID(order.getId());
		study.setUid("1.2.826.0.1.3680043.8.2186.1.1");
		study.setModality(2);
		study.setPriority(0);
		study.setMwlStatus(0);
		
		String saveOrderHL7String = DicomUtils.createHL7Message(study, order, DicomUtils.OrderRequest.Void_Order);
		
		assertThat(saveOrderHL7String, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    saveOrderHL7String,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|CA|1|||||^^^20150204143500^^S\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||1|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
		
		PipeParser hl7PipeParser = new PipeParser();
		Message saveOrderHL7Message = hl7PipeParser.parse(saveOrderHL7String);
		ORM_O01 ormMsg = (ORM_O01) saveOrderHL7Message;
		
		// MSH segment
		MSH msh = ormMsg.getMSH();
		assertEquals("2.3.1", msh.getVersionID().getVersionID().getValue());
		assertEquals("ORM", msh.getMessageType().getMessageType().getValue());
		assertEquals("O01", msh.getMessageType().getTriggerEvent().getValue());
		assertEquals("OpenMRSRadiologyModule", msh.getSendingApplication().getNamespaceID().getValue());
		assertEquals("OpenMRS", msh.getSendingFacility().getNamespaceID().getValue());
		assertEquals("P", msh.getProcessingID().getProcessingID().getValue());
		
		// PID segment
		Patient expectedPatient = order.getPatient();
		PID pid = ormMsg.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1().getPID();
		assertEquals(expectedPatient.getPatientIdentifier().getIdentifier(), pid.getPatientIdentifierList(0).getID()
		        .getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(expectedPatient.getBirthdate()), pid.getDateTimeOfBirth()
		        .getTimeOfAnEvent().getValue());
		assertEquals(expectedPatient.getGender(), pid.getSex().getValue());
		assertEquals(expectedPatient.getPersonName().getFamilyName(), pid.getPatientName(0).getFamilyLastName()
		        .getFamilyName().getValue());
		assertEquals(expectedPatient.getPersonName().getMiddleName(), pid.getPatientName(0).getMiddleInitialOrName()
		        .getValue());
		assertEquals(expectedPatient.getPersonName().getGivenName(), pid.getPatientName(0).getGivenName().getValue());
		
		// ORC segment
		ORC orc = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC();
		assertEquals("CA", orc.getOrderControl().getValue());
		assertEquals(String.valueOf(study.getId()), orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
		assertEquals(null, orc.getOrderStatus().getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(order.getStartDate()), orc.getQuantityTiming()
		        .getStartDateTime().getTimeOfAnEvent().getValue());
		assertEquals("S", orc.getQuantityTiming().getPriority().getValue());
		
		// OBR segment
		OBR obr = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
		        .getOBR();
		assertEquals(order.getInstructions(), obr.getUniversalServiceID().getAlternateText().getValue());
		assertEquals(String.valueOf(study.getId()), obr.getPlacerField2().getValue());
		assertEquals(String.valueOf(study.getId()), obr.getFillerField1().getValue());
		assertEquals(Modality.values()[study.getModality()].toString(), obr.getDiagnosticServSectID().getValue());
		assertEquals(order.getInstructions(), obr.getProcedureCode().getText().getValue());
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertEquals(study.getUid(), terser.get("/.ZDS-1-1"));
		assertEquals(null, terser.get("/.ZDS-1-2"));
		assertEquals("Application", terser.get("/.ZDS-1-3"));
		assertEquals("DICOM", terser.get("/.ZDS-1-4"));
	}
	
	/**
	 * Tests the DicomUtils.createHL7Message method with a study with mwlstatus = 1 and
	 * OrderRequest.Save_Order
	 * 
	 * @throws Exception
	 * @see {@link DicomUtils#createHL7Message(Study, Order, OrderRequest)}
	 */
	@Test
	@Verifies(value = "should return encoded HL7 ORMO01 message string with change order control given study with mwlstatus one and save order request", method = "createHL7Message(Study, Order, OrderRequest)")
	public void createHL7Message_shouldReturnEncodedHL7ORMO01MessageStringWithChangeOrderControlGivenStudyWithMwlstatusOneAndSaveOrderRequest()
	        throws Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		patient.setBirthdate(cal.getTime());
		
		Order order = new Order();
		order.setId(20);
		order.setPatient(patient);
		cal.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		order.setStartDate(cal.getTime());
		order.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Study study = new Study();
		study.setId(1);
		study.setOrderID(order.getId());
		study.setUid("1.2.826.0.1.3680043.8.2186.1.1");
		study.setModality(2);
		study.setPriority(0);
		study.setMwlStatus(1);
		
		String saveOrderHL7String = DicomUtils.createHL7Message(study, order, DicomUtils.OrderRequest.Save_Order);
		
		assertThat(saveOrderHL7String, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    saveOrderHL7String,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|XO|1|||||^^^20150204143500^^S\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||1|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
		
		PipeParser hl7PipeParser = new PipeParser();
		Message saveOrderHL7Message = hl7PipeParser.parse(saveOrderHL7String);
		ORM_O01 ormMsg = (ORM_O01) saveOrderHL7Message;
		
		// MSH segment
		MSH msh = ormMsg.getMSH();
		assertEquals("2.3.1", msh.getVersionID().getVersionID().getValue());
		assertEquals("ORM", msh.getMessageType().getMessageType().getValue());
		assertEquals("O01", msh.getMessageType().getTriggerEvent().getValue());
		assertEquals("OpenMRSRadiologyModule", msh.getSendingApplication().getNamespaceID().getValue());
		assertEquals("OpenMRS", msh.getSendingFacility().getNamespaceID().getValue());
		assertEquals("P", msh.getProcessingID().getProcessingID().getValue());
		
		// PID segment
		Patient expectedPatient = order.getPatient();
		PID pid = ormMsg.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1().getPID();
		assertEquals(expectedPatient.getPatientIdentifier().getIdentifier(), pid.getPatientIdentifierList(0).getID()
		        .getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(expectedPatient.getBirthdate()), pid.getDateTimeOfBirth()
		        .getTimeOfAnEvent().getValue());
		assertEquals(expectedPatient.getGender(), pid.getSex().getValue());
		assertEquals(expectedPatient.getPersonName().getFamilyName(), pid.getPatientName(0).getFamilyLastName()
		        .getFamilyName().getValue());
		assertEquals(expectedPatient.getPersonName().getMiddleName(), pid.getPatientName(0).getMiddleInitialOrName()
		        .getValue());
		assertEquals(expectedPatient.getPersonName().getGivenName(), pid.getPatientName(0).getGivenName().getValue());
		
		// ORC segment
		ORC orc = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC();
		assertEquals("XO", orc.getOrderControl().getValue());
		assertEquals(String.valueOf(study.getId()), orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
		assertEquals(null, orc.getOrderStatus().getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(order.getStartDate()), orc.getQuantityTiming()
		        .getStartDateTime().getTimeOfAnEvent().getValue());
		assertEquals("S", orc.getQuantityTiming().getPriority().getValue());
		
		// OBR segment
		OBR obr = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
		        .getOBR();
		assertEquals(order.getInstructions(), obr.getUniversalServiceID().getAlternateText().getValue());
		assertEquals(String.valueOf(study.getId()), obr.getPlacerField2().getValue());
		assertEquals(String.valueOf(study.getId()), obr.getFillerField1().getValue());
		assertEquals(Modality.values()[study.getModality()].toString(), obr.getDiagnosticServSectID().getValue());
		assertEquals(order.getInstructions(), obr.getProcedureCode().getText().getValue());
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertEquals(study.getUid(), terser.get("/.ZDS-1-1"));
		assertEquals(null, terser.get("/.ZDS-1-2"));
		assertEquals("Application", terser.get("/.ZDS-1-3"));
		assertEquals("DICOM", terser.get("/.ZDS-1-4"));
	}
	
	/**
	 * Tests the DicomUtils.createHL7Message method with a study with mwlstatus = 0 and
	 * OrderRequest.Default
	 * 
	 * @throws Exception
	 * @see {@link DicomUtils#createHL7Message(Study, Order, OrderRequest)}
	 */
	//TODO(teleivo) Find the purpose of OrderRequest.Default and delete if possible, since it leads to an invalid ORM^O01 message
	// Note: the old getORCType(mwlstatus, OrderRequest) method returned "" if you passed OrderRequest.Default
	// If you send an HL7 v2.3.1 ORM^O01 message with Order Control Code of "" you will get an Illegal Order Control Code ORC-1 ACK response from dcm4chee
	// Its unclear why OrderRequest.Default exists. I replaced the getORCType with getCommonOrderControlFrom to ensure type safety via an OrderControl Enum.
	// In case mwlStatus = x, OrderRequest.Default null is returned.
	//@Test
	@Verifies(value = "should return encoded HL7 ORMO01 message string with new order control given study with mwlstatus zero and default order request", method = "createHL7Message(Study, Order, OrderRequest)")
	public void createHL7Message_shouldReturnEncodedHL7ORMO01MessageStringWithNewOrderControlGivenStudyWithMwlstatusZeroAndDefaultOrderRequest()
	        throws Exception {
		
		Patient patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		patient.setBirthdate(cal.getTime());
		
		Order order = new Order();
		order.setId(20);
		order.setPatient(patient);
		cal.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		order.setStartDate(cal.getTime());
		order.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		Study study = new Study();
		study.setId(1);
		study.setOrderID(order.getId());
		study.setUid("1.2.826.0.1.3680043.8.2186.1.1");
		study.setModality(2);
		study.setPriority(0);
		study.setMwlStatus(0);
		
		String saveOrderHL7String = DicomUtils.createHL7Message(study, order, DicomUtils.OrderRequest.Default);
		
		assertThat(saveOrderHL7String, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    saveOrderHL7String,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|NW|1|||||^^^20150204143500^^S\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||1|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
		
		PipeParser hl7PipeParser = new PipeParser();
		Message saveOrderHL7Message = hl7PipeParser.parse(saveOrderHL7String);
		ORM_O01 ormMsg = (ORM_O01) saveOrderHL7Message;
		
		// MSH segment
		MSH msh = ormMsg.getMSH();
		assertEquals("2.3.1", msh.getVersionID().getVersionID().getValue());
		assertEquals("ORM", msh.getMessageType().getMessageType().getValue());
		assertEquals("O01", msh.getMessageType().getTriggerEvent().getValue());
		assertEquals("OpenMRSRadiologyModule", msh.getSendingApplication().getNamespaceID().getValue());
		assertEquals("OpenMRS", msh.getSendingFacility().getNamespaceID().getValue());
		assertEquals("P", msh.getProcessingID().getProcessingID().getValue());
		
		// PID segment
		Patient expectedPatient = order.getPatient();
		PID pid = ormMsg.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1().getPID();
		assertEquals(expectedPatient.getPatientIdentifier().getIdentifier(), pid.getPatientIdentifierList(0).getID()
		        .getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(expectedPatient.getBirthdate()), pid.getDateTimeOfBirth()
		        .getTimeOfAnEvent().getValue());
		assertEquals(expectedPatient.getGender(), pid.getSex().getValue());
		assertEquals(expectedPatient.getPersonName().getFamilyName(), pid.getPatientName(0).getFamilyLastName()
		        .getFamilyName().getValue());
		assertEquals(expectedPatient.getPersonName().getMiddleName(), pid.getPatientName(0).getMiddleInitialOrName()
		        .getValue());
		assertEquals(expectedPatient.getPersonName().getGivenName(), pid.getPatientName(0).getGivenName().getValue());
		
		// ORC segment
		ORC orc = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC();
		assertEquals("NW", orc.getOrderControl().getValue());
		assertEquals(String.valueOf(study.getId()), orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
		assertEquals(null, orc.getOrderStatus().getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(order.getStartDate()), orc.getQuantityTiming()
		        .getStartDateTime().getTimeOfAnEvent().getValue());
		assertEquals("S", orc.getQuantityTiming().getPriority().getValue());
		
		// OBR segment
		OBR obr = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
		        .getOBR();
		assertEquals(order.getInstructions(), obr.getUniversalServiceID().getAlternateText().getValue());
		assertEquals(String.valueOf(study.getId()), obr.getPlacerField2().getValue());
		assertEquals(String.valueOf(study.getId()), obr.getFillerField1().getValue());
		assertEquals(Modality.values()[study.getModality()].toString(), obr.getDiagnosticServSectID().getValue());
		assertEquals(order.getInstructions(), obr.getProcedureCode().getText().getValue());
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertEquals(study.getUid(), terser.get("/.ZDS-1-1"));
		assertEquals(null, terser.get("/.ZDS-1-2"));
		assertEquals("Application", terser.get("/.ZDS-1-3"));
		assertEquals("DICOM", terser.get("/.ZDS-1-4"));
	}
	
	/**
	 * Tests the DicomUtils.getCommonOrderControlFrom method mapping mwlstatus and OrderRequest to
	 * HL7 Order Control Code
	 * 
	 * @see {@link DicomUtils#getCommonOrderControlFrom(Integer, OrderRequest)}
	 */
	@Test
	@Verifies(value = "should return HL7 order control given mwlstatus and orderrequest", method = "getCommonOrderControlFrom(Integer, OrderRequest)")
	public void getCommonOrderControlFrom_shouldReturnHL7OrderControlGivenMwlstatusAndOrderRequest() {
		
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(0,
		    DicomUtils.OrderRequest.Save_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(2,
		    DicomUtils.OrderRequest.Save_Order));
		assertEquals(CommonOrderOrderControl.CHANGE_ORDER, DicomUtils.getCommonOrderControlFrom(1,
		    DicomUtils.OrderRequest.Save_Order));
		assertEquals(CommonOrderOrderControl.CHANGE_ORDER, DicomUtils.getCommonOrderControlFrom(-1,
		    DicomUtils.OrderRequest.Save_Order));
		assertEquals(CommonOrderOrderControl.CHANGE_ORDER, DicomUtils.getCommonOrderControlFrom(3,
		    DicomUtils.OrderRequest.Save_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(-1,
		    DicomUtils.OrderRequest.Void_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(0,
		    DicomUtils.OrderRequest.Void_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(1,
		    DicomUtils.OrderRequest.Void_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(-1,
		    DicomUtils.OrderRequest.Unvoid_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(0,
		    DicomUtils.OrderRequest.Unvoid_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(1,
		    DicomUtils.OrderRequest.Unvoid_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(-1,
		    DicomUtils.OrderRequest.Discontinue_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(0,
		    DicomUtils.OrderRequest.Discontinue_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(1,
		    DicomUtils.OrderRequest.Discontinue_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(-1,
		    DicomUtils.OrderRequest.Undiscontinue_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(0,
		    DicomUtils.OrderRequest.Undiscontinue_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(1,
		    DicomUtils.OrderRequest.Undiscontinue_Order));
		assertNull(DicomUtils.getCommonOrderControlFrom(1, DicomUtils.OrderRequest.Default));
	}
	
	/**
	 * Tests the DicomUtils.getCommonOrderPriorityFrom method mapping all Study.Priorities to HL7
	 * Common Order segment (ORC) Quantity/Timing field Priority component.
	 * 
	 * @see {@link DicomUtils#getCommonOrderPriorityFrom(Integer)}
	 */
	@Test
	@Verifies(value = "should return hl7 common order priority given study priority", method = "getCommonOrderPriorityFrom(Integer)")
	public void getCommonOrderPriorityFrom_shouldReturnHL7CommonOrderPriorityGivenStudyPriority() {
		
		assertEquals(CommonOrderPriority.STAT, DicomUtils.getCommonOrderPriorityFrom(Study.Priorities.STAT));
		assertEquals(CommonOrderPriority.ASAP, DicomUtils.getCommonOrderPriorityFrom(Study.Priorities.HIGH));
		assertEquals(CommonOrderPriority.ROUTINE, DicomUtils.getCommonOrderPriorityFrom(Study.Priorities.ROUTINE));
		assertEquals(CommonOrderPriority.TIMING_CRITICAL, DicomUtils.getCommonOrderPriorityFrom(Study.Priorities.MEDIUM));
		assertEquals(CommonOrderPriority.ROUTINE, DicomUtils.getCommonOrderPriorityFrom(Study.Priorities.LOW));
		assertEquals(CommonOrderPriority.ROUTINE, DicomUtils.getCommonOrderPriorityFrom(-1));
		assertEquals(CommonOrderPriority.ROUTINE, DicomUtils.getCommonOrderPriorityFrom(100));
	}
}
