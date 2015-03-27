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
 * Tests {@link DicomUtils}
 */
public class DicomUtilsTest {
	
	/**
	 * Convenience method to create a mock radiology order
	 */
	Order getMockRadiologyOrder() {
		Patient mockPatient = new Patient();
		mockPatient.setPatientId(1);
		
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
		mockPatient.addIdentifiers(patientIdentifiers);
		
		mockPatient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		mockPatient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		mockPatient.setBirthdate(cal.getTime());
		
		Order mockRadiologyOrder = new Order();
		mockRadiologyOrder.setId(20);
		mockRadiologyOrder.setPatient(mockPatient);
		cal.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		mockRadiologyOrder.setStartDate(cal.getTime());
		mockRadiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		return mockRadiologyOrder;
	}
	
	/**
	 * Convenience method to create a mock radiology study
	 */
	Study getMockStudy() {
		Study mockStudy = new Study();
		mockStudy.setId(1);
		mockStudy.setOrderId(getMockRadiologyOrder().getOrderId());
		mockStudy.setUid("1.2.826.0.1.3680043.8.2186.1.1");
		mockStudy.setModality(Modality.CT);
		mockStudy.setPriority(RequestedProcedurePriority.STAT);
		
		return mockStudy;
	}
	
	/**
	 * @throws Exception
	 * @see {@link DicomUtils#createHL7Message(Study, Order, OrderRequest)}
	 */
	@Test
	@Verifies(value = "should return encoded HL7 ORMO01 message string with new order control given study with mwlstatus default and save order request", method = "createHL7Message(Study, Order, OrderRequest)")
	public void createHL7Message_shouldReturnEncodedHL7ORMO01MessageStringWithNewOrderControlGivenStudyWithMwlstatusDefaultAndSaveOrderRequest()
	        throws Exception {
		
		Order order = getMockRadiologyOrder();
		Study study = getMockStudy();
		study.setMwlStatus(MwlStatus.DEFAULT);
		
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
		assertEquals(study.getModality().name(), obr.getDiagnosticServSectID().getValue());
		assertEquals(order.getInstructions(), obr.getProcedureCode().getText().getValue());
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertEquals(study.getUid(), terser.get("/.ZDS-1-1"));
		assertEquals(null, terser.get("/.ZDS-1-2"));
		assertEquals("Application", terser.get("/.ZDS-1-3"));
		assertEquals("DICOM", terser.get("/.ZDS-1-4"));
	}
	
	/**
	 * @throws Exception
	 * @see {@link DicomUtils#createHL7Message(Study, Order, OrderRequest)}
	 */
	@Test
	@Verifies(value = "should return encoded HL7 ORMO01 message string with cancel order control given study with mwlstatus default and void order request", method = "createHL7Message(Study, Order, OrderRequest)")
	public void createHL7Message_shouldReturnEncodedHL7ORMO01MessageStringWithCancelOrderControlGivenStudyWithMwlstatusDefaultAndVoidOrderRequest()
	        throws Exception {
		
		Order order = getMockRadiologyOrder();
		Study study = getMockStudy();
		study.setMwlStatus(MwlStatus.DEFAULT);
		
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
		assertEquals(study.getModality().name(), obr.getDiagnosticServSectID().getValue());
		assertEquals(order.getInstructions(), obr.getProcedureCode().getText().getValue());
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertEquals(study.getUid(), terser.get("/.ZDS-1-1"));
		assertEquals(null, terser.get("/.ZDS-1-2"));
		assertEquals("Application", terser.get("/.ZDS-1-3"));
		assertEquals("DICOM", terser.get("/.ZDS-1-4"));
	}
	
	/**
	 * @throws Exception
	 * @see {@link DicomUtils#createHL7Message(Study, Order, OrderRequest)}
	 */
	@Test
	@Verifies(value = "should return encoded HL7 ORMO01 message string with change order control given study with mwlstatus save ok and save order request", method = "createHL7Message(Study, Order, OrderRequest)")
	public void createHL7Message_shouldReturnEncodedHL7ORMO01MessageStringWithChangeOrderControlGivenStudyWithMwlstatusSaveOkAndSaveOrderRequest()
	        throws Exception {
		
		Order order = getMockRadiologyOrder();
		Study study = getMockStudy();
		study.setMwlStatus(MwlStatus.SAVE_OK);
		
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
		assertEquals(study.getModality().name(), obr.getDiagnosticServSectID().getValue());
		assertEquals(order.getInstructions(), obr.getProcedureCode().getText().getValue());
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertEquals(study.getUid(), terser.get("/.ZDS-1-1"));
		assertEquals(null, terser.get("/.ZDS-1-2"));
		assertEquals("Application", terser.get("/.ZDS-1-3"));
		assertEquals("DICOM", terser.get("/.ZDS-1-4"));
	}
	
	/**
	 * @see {@link DicomUtils#getCommonOrderPriorityFrom(RequestedProcedurePriority)}
	 */
	@Test
	@Verifies(value = "should return hl7 common order priority given requested procedure priority", method = "getCommonOrderPriorityFrom(RequestedProcedurePriority)")
	public void getCommonOrderPriorityFrom_shouldReturnHL7CommonOrderPriorityGivenStudyPriority() {
		
		assertEquals(CommonOrderPriority.STAT, DicomUtils.getCommonOrderPriorityFrom(RequestedProcedurePriority.STAT));
		assertEquals(CommonOrderPriority.ASAP, DicomUtils.getCommonOrderPriorityFrom(RequestedProcedurePriority.HIGH));
		assertEquals(CommonOrderPriority.ROUTINE, DicomUtils.getCommonOrderPriorityFrom(RequestedProcedurePriority.ROUTINE));
		assertEquals(CommonOrderPriority.TIMING_CRITICAL, DicomUtils
		        .getCommonOrderPriorityFrom(RequestedProcedurePriority.MEDIUM));
		assertEquals(CommonOrderPriority.ROUTINE, DicomUtils.getCommonOrderPriorityFrom(RequestedProcedurePriority.LOW));
	}
	
	/**
	 * @see {@link DicomUtils#getCommonOrderPriorityFrom(RequestedProcedurePriority)}
	 */
	@Test
	@Verifies(value = "should return default hl7 common order priority given null", method = "getCommonOrderPriorityFrom(RequestedProcedurePriority)")
	public void getCommonOrderPriorityFrom_shouldReturnDefaultHL7CommonOrderPriorityGivenNull() {
		
		assertEquals(CommonOrderPriority.ROUTINE, DicomUtils.getCommonOrderPriorityFrom(null));
	}
	
	/**
	 * @see {@link DicomUtils#getCommonOrderControlFrom(MwlStatus, OrderRequest)}
	 */
	@Test
	@Verifies(value = "should return hl7 order control given mwlstatus and orderrequest", method = "getCommonOrderControlFrom(MwlStatus, OrderRequest)")
	public void getCommonOrderControlFrom_shouldReturnHL7OrderControlGivenMwlstatusAndOrderRequest() {
		
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT,
		    DicomUtils.OrderRequest.Save_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_ERR,
		    DicomUtils.OrderRequest.Save_Order));
		assertEquals(CommonOrderOrderControl.CHANGE_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_OK,
		    DicomUtils.OrderRequest.Save_Order));
		assertEquals(CommonOrderOrderControl.CHANGE_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.UPDATE_OK,
		    DicomUtils.OrderRequest.Save_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT,
		    DicomUtils.OrderRequest.Void_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT,
		    DicomUtils.OrderRequest.Void_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_OK,
		    DicomUtils.OrderRequest.Void_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT,
		    DicomUtils.OrderRequest.Unvoid_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT,
		    DicomUtils.OrderRequest.Unvoid_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_OK,
		    DicomUtils.OrderRequest.Unvoid_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT,
		    DicomUtils.OrderRequest.Discontinue_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT,
		    DicomUtils.OrderRequest.Discontinue_Order));
		assertEquals(CommonOrderOrderControl.CANCEL_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_OK,
		    DicomUtils.OrderRequest.Discontinue_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT,
		    DicomUtils.OrderRequest.Undiscontinue_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT,
		    DicomUtils.OrderRequest.Undiscontinue_Order));
		assertEquals(CommonOrderOrderControl.NEW_ORDER, DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_OK,
		    DicomUtils.OrderRequest.Undiscontinue_Order));
	}
	
}
