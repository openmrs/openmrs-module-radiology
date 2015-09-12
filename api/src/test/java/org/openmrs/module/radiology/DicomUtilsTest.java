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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.SpecificCharacterSet;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openmrs.GlobalProperty;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.CommonOrderPriority;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.xml.sax.SAXException;

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
public class DicomUtilsTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceTestDataSet.xml";
	
	protected static final int STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER = 1;
	
	protected static final String DICOM_SPECIFIC_CHARACTER_SET = "ISO-8859-1";
	
	protected static final String MWL_DIRECTORY = "mwl";
	
	private AdministrationService administrationService = null;
	
	private RadiologyService radiologyService = null;
	
	private OrderService orderService = null;
	
	@Rule
	public TemporaryFolder temporaryBaseFolder = new TemporaryFolder();
	
	/**
	 * Run this before each unit test in this class. It simply assigns the services used in this
	 * class to private variables The "@Before" method in {@link BaseContextSensitiveTest} is run
	 * right before this method and sets up the initial data set and authenticates to the Context
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		
		if (administrationService == null) {
			administrationService = Context.getAdministrationService();
		}
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_SPECIFIC_CHARCATER_SET,
		        DICOM_SPECIFIC_CHARACTER_SET));
		
		if (orderService == null) {
			orderService = Context.getOrderService();
		}
		
		if (radiologyService == null) {
			radiologyService = Context.getService(RadiologyService.class);
		}
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * Tests the DicomUtils.writeMpps method with a DicomObject containing DICOM command N-CREATE
	 * (PerformedProcedureStepStatus = IN PROGRESS) for an existing study. DICOM Performed Procedure
	 * Step Status; IN PROGRESS = Started but not complete
	 * 
	 * @see {@link DicomUtils#updateStudyPerformedStatusByMpps(DicomObject)}
	 */
	@Test
	@Verifies(value = "should set performed status of an existing study in database to performed procedure step status IN_PROGRESS of given dicom object", method = "updateStudyPerformedStatusByMPPS(DicomObject)")
	public void updateStudyPerformedStatusByMpps_shouldSetPerformedStatusOfAnExistingStudyInDatabaseToPerformedProcedureStepStatusIN_PROGRESSOfGivenDicomObject()
	        throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		Study studyToBeUpdated = radiologyService.getStudy(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNCreate(studyToBeUpdated, radiologyOrder);
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertEquals(PerformedProcedureStepStatus.IN_PROGRESS, studyToBeUpdated.getPerformedStatus());
	}
	
	/**
	 * Convenience method to create a DicomObject containing DICOM command N-CREATE
	 * (PerformedProcedureStepStatus = IN_PROGRESS) for an existing study.
	 * 
	 * @param study study for which the DicomObject will be created
	 * @param radiologyOrder order associated with given study
	 */
	DicomObject getDicomNCreate(Study study, Order radiologyOrder) {
		
		String performedProcedureStatus = "IN PROGRESS";
		String performedProcedureStepStartDate = "20150313";
		String performedProcedureStepStartTime = "130225";
		String performedStationAETitle = "CR01";
		String performedStationName = "Radiology Department";
		String performedLocation = "Room 01";
		String performedProcedureStepID = "XX 01";
		
		String referencedSOPClassUID = "1.2.840.10008.3.1.2.3.1";
		String referencedSOPInstanceUID = "1.2.840.10008.5.1.4.1.1.9.1.2.1.1.1";
		
		String studyID = String.valueOf(study.getStudyId());
		String studyInstanceUID = study.getStudyInstanceUid();
		String modality = study.getModality().name();
		
		String accessionNumber = radiologyOrder.getAccessionNumber();
		String scheduledProcedureStepDescription = radiologyOrder.getInstructions();
		
		Patient patient = radiologyOrder.getPatient();
		String patientName = patient.getPersonName().getFullName().replace(' ', '^');
		String patientID = patient.getPatientIdentifier().getIdentifier();
		String issuerOfPatientID = "";
		String patientBirthDate = new SimpleDateFormat("yyyyMMdd").format(patient.getBirthdate());
		
		String patientGender = patient.getGender();
		
		BasicDicomObject dicomObject = new BasicDicomObject();
		BasicDicomObject referencedStudySequence = new BasicDicomObject();
		BasicDicomObject scheduledStepAttributesSequence = new BasicDicomObject();
		
		referencedStudySequence.putString(Tag.ReferencedSOPClassUID, VR.UI, referencedSOPClassUID);
		referencedStudySequence.putString(Tag.ReferencedSOPInstanceUID, VR.UI, referencedSOPInstanceUID);
		scheduledStepAttributesSequence.putNestedDicomObject(Tag.ReferencedStudySequence, referencedStudySequence);
		
		scheduledStepAttributesSequence.putString(Tag.AccessionNumber, VR.SH, accessionNumber);
		scheduledStepAttributesSequence.putString(Tag.StudyInstanceUID, VR.UI, studyInstanceUID);
		scheduledStepAttributesSequence.putString(Tag.RequestedProcedureDescription, VR.LO,
		    scheduledProcedureStepDescription);
		scheduledStepAttributesSequence.putString(Tag.ScheduledProcedureStepDescription, VR.LO,
		    scheduledProcedureStepDescription);
		scheduledStepAttributesSequence.putSequence(Tag.ScheduledProtocolCodeSequence);
		scheduledStepAttributesSequence.putString(Tag.ScheduledProcedureStepID, VR.SH, studyID);
		scheduledStepAttributesSequence.putString(Tag.RequestedProcedureID, VR.SH, studyID);
		
		dicomObject.putString(Tag.SpecificCharacterSet, VR.CS, DICOM_SPECIFIC_CHARACTER_SET);
		dicomObject.putString(Tag.Modality, VR.CS, modality);
		dicomObject.putString(Tag.PatientName, VR.PN, patientName);
		dicomObject.putString(Tag.PatientID, VR.LO, patientID);
		dicomObject.putString(Tag.IssuerOfPatientID, VR.LO, issuerOfPatientID);
		dicomObject.putString(Tag.PatientBirthDate, VR.DA, patientBirthDate);
		dicomObject.putString(Tag.PatientSex, VR.CS, patientGender);
		dicomObject.putString(Tag.StudyID, VR.SH, studyID);
		dicomObject.putString(Tag.PerformedStationAETitle, VR.AE, performedStationAETitle);
		dicomObject.putString(Tag.PerformedStationName, VR.SH, performedStationName);
		dicomObject.putString(Tag.PerformedLocation, VR.SH, performedLocation);
		dicomObject.putString(Tag.PerformedProcedureStepStartDate, VR.DA, performedProcedureStepStartDate);
		dicomObject.putString(Tag.PerformedProcedureStepStartTime, VR.TM, performedProcedureStepStartTime);
		dicomObject.putString(Tag.PerformedProcedureStepStatus, VR.CS, performedProcedureStatus);
		dicomObject.putString(Tag.PerformedProcedureStepID, VR.SH, performedProcedureStepID);
		dicomObject.putString(Tag.PerformedProcedureStepDescription, VR.LO, "");
		dicomObject.putString(Tag.PerformedProcedureTypeDescription, VR.LO, "");
		dicomObject.putSequence(Tag.PerformedProtocolCodeSequence);
		dicomObject.putNestedDicomObject(Tag.ScheduledStepAttributesSequence, scheduledStepAttributesSequence);
		
		return dicomObject;
	}
	
	/**
	 * Tests the DicomUtils.writeMpps method with a DicomObject containing DICOM command N-SET
	 * (PerformedProcedureStepStatus = DISCONTINUED) for an existing study. DICOM Performed
	 * Procedure Step Status; DISCONTINUED = Canceled or unsuccessfully terminated
	 * 
	 * @see {@link DicomUtils#updateStudyPerformedStatusByMPPS(DicomObject)}
	 */
	@Test
	@Verifies(value = "should set the performed status of an existing study in the database to DISCONTINUED given a dicom object containing command N-CREATE", method = "updateStudyPerformedStatusByMPPS(DicomObject)")
	public void updateStudyPerformedStatusByMPPS_shouldUpdateThePerformedStatusOfAnExistingStudyInTheDatabaseBasedOnADicomObject()
	        throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		Study studyToBeUpdated = radiologyService.getStudy(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNSet(studyToBeUpdated, radiologyOrder, "DISCONTINUED");
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertEquals(PerformedProcedureStepStatus.DISCONTINUED, studyToBeUpdated.getPerformedStatus());
	}
	
	/**
	 * Convenience method to create a DicomObject containing DICOM command N-SET
	 * (PerformedProcedureStepStatus = DISCONTINUED/COMPLETED) for an existing study.
	 * 
	 * @param study study for which the DicomObject will be created
	 * @param radiologyOrder order associated with given study
	 * @param performedProcedureStatus DICOM Performed Procedure Step Status either DISCONTINUED or COMPLETED
	 */
	DicomObject getDicomNSet(Study study, Order radiologyOrder, String performedProcedureStatus) {
		
		SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(RadiologyProperties.getSpecificCharacterSet());
		
		String performedProcedureStepEndDate = "20150313";
		String performedProcedureStepEndTime = "133725";
		String performingPhysicianName = "Doctor^Shiwago";
		String seriesDescription = "Thorax scan";
		String operatorsName = "Doctor^Shiwago";
		String protocolName = "Thorax";
		String seriesInstanceUID = "1.2.826.0.1.3680043.2.1545.1.2.1.7.20150313.130225.305.1";
		
		DicomObject dicomObject = getDicomNCreate(study, radiologyOrder);
		dicomObject.putString(Tag.PerformedProcedureStepEndDate, VR.DA, performedProcedureStepEndDate);
		dicomObject.putString(Tag.PerformedProcedureStepEndTime, VR.TM, performedProcedureStepEndTime);
		dicomObject.putString(Tag.PerformedProcedureStepStatus, VR.CS, performedProcedureStatus);
		String retrieveAETitle = dicomObject.get(Tag.PerformedStationAETitle).getValueAsString(specificCharacterSet, 0);
		
		BasicDicomObject performedSeriesSequence = new BasicDicomObject();
		performedSeriesSequence.putString(Tag.PerformingPhysicianName, VR.PN, performingPhysicianName);
		performedSeriesSequence.putString(Tag.RetrieveAETitle, VR.AE, retrieveAETitle);
		performedSeriesSequence.putString(Tag.SeriesDescription, VR.LO, seriesDescription);
		performedSeriesSequence.putString(Tag.OperatorsName, VR.PN, operatorsName);
		performedSeriesSequence.putSequence(Tag.ReferencedImageSequence);
		performedSeriesSequence.putString(Tag.ProtocolName, VR.LO, protocolName);
		performedSeriesSequence.putString(Tag.SeriesInstanceUID, VR.UI, seriesInstanceUID);
		performedSeriesSequence.putSequence(Tag.ReferencedNonImageCompositeSOPInstanceSequence);
		dicomObject.putNestedDicomObject(Tag.PerformedSeriesSequence, performedSeriesSequence);
		
		return dicomObject;
	}
	
	/**
	 * Tests the DicomUtils.writeMpps method with a DicomObject containing DICOM command N-SET
	 * (PerformedProcedureStepStatus = COMPLETED) for an existing study. DICOM Performed Procedure
	 * Step Status; COMPLETED
	 * 
	 * @see {@link DicomUtils#updateStudyPerformedStatusByMpps(DicomObject)}
	 */
	@Test
	@Verifies(value = "should set the performed status of an existing study in the database to COMPLETED given a dicom object containing command N-CREATE", method = "updateStudyPerformedStatusByMPPS(DicomObject)")
	public void updateStudyPerformedStatusByMpps_shouldUpdateThePerformedStatusOfAnExistingStudyInTheDatabaseBasedOnADicomObjectCompleted()
	        throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		Study studyToBeUpdated = radiologyService.getStudy(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNSet(studyToBeUpdated, radiologyOrder, "COMPLETED");
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertEquals(PerformedProcedureStepStatus.COMPLETED, studyToBeUpdated.getPerformedStatus());
	}
	
	/**
	 * @see {@link DicomUtils#updateStudyPerformedStatusByMpps(DicomObject)}
	 */
	//	@Test
	@Verifies(value = "not fail if study instance uid referenced in dicom mpps cannot be found", method = "updateStudyPerformedStatusByMPPS(DicomObject)")
	public void updateStudyPerformedStatusByMpps_shouldNotFailIfStudyInstanceUidReferencedInDicomMppsCannotBeFound()
	        throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		Study studyToBeUpdated = radiologyService.getStudy(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNSet(studyToBeUpdated, radiologyOrder, "COMPLETED");
		dicomObjectNCreate.remove(Tag.ScheduledStepAttributesSequence);
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertEquals(PerformedProcedureStepStatus.COMPLETED, studyToBeUpdated.getPerformedStatus());
	}
	
	/**
	 * @see {@link DicomUtils#getStudyInstanceUidFromMpps(DicomObject)}
	 */
	@Test
	@Verifies(value = "should return study instance uid given mpps dicom object", method = "getStudyInstanceUidFromMpps(DicomObject)")
	public void getStudyInstanceUidFromMpps_shouldReturnStudyInstanceUidGivenDicomMppsObject() {
		
		Study study = radiologyService.getStudy(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = study.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(study, radiologyOrder);
		
		String studyInstanceUid = DicomUtils.getStudyInstanceUidFromMpps(dicomMpps);
		
		assertThat(study.getStudyInstanceUid(), is(studyInstanceUid));
	}
	
	/**
	 * @see {@link DicomUtils#getStudyInstanceUidFromMpps(DicomObject)}
	 */
	@Test
	@Verifies(value = "should return null given dicom mpps object without scheduled step attributes sequence", method = "getStudyInstanceUidFromMpps(DicomObject)")
	public void getStudyInstanceUidFromMpps_shouldReturnNullGivenDicomMppsObjectWithoutScheduledStepAttributesSequence() {
		
		Study study = radiologyService.getStudy(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = study.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(study, radiologyOrder);
		dicomMpps.remove(Tag.ScheduledStepAttributesSequence);
		
		String studyInstanceUid = DicomUtils.getStudyInstanceUidFromMpps(dicomMpps);
		
		assertNull(studyInstanceUid);
	}
	
	/**
	 * @see {@link DicomUtils#getStudyInstanceUidFromMpps(DicomObject)}
	 */
	@Test
	@Verifies(value = "should return null given dicom mpps object with scheduled step attributes sequence missing study instance uid tag", method = "getStudyInstanceUidFromMpps(DicomObject)")
	public void getStudyInstanceUidFromMpps_shouldReturnNullGivenDicomMppsObjectWithScheduledStepAttributesSequenceMissingStudyInstanceUidTag() {
		
		Study study = radiologyService.getStudy(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = study.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(study, radiologyOrder);
		
		dicomMpps.get(Tag.ScheduledStepAttributesSequence).getDicomObject().remove(Tag.StudyInstanceUID);
		
		String studyInstanceUid = DicomUtils.getStudyInstanceUidFromMpps(dicomMpps);
		
		assertNull(studyInstanceUid);
	}
	
	/**
	 * @see {@link DicomUtils#getPerformedProcedureStepStatus(DicomObject)}
	 */
	@Test
	@Verifies(value = "should return performed procedure step status given dicom object", method = "getPerformedProcedureStepStatus(DicomObject)")
	public void getPerformedProcedureStepStatus_shouldReturnPerformedProcedureStepStatusGivenMppsDicomObject() {
		
		Study study = radiologyService.getStudy(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = study.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(study, radiologyOrder);
		
		String performedProcedureStepStatus = DicomUtils.getPerformedProcedureStepStatus(dicomMpps);
		
		assertThat(study.getPerformedStatus(), is(PerformedProcedureStepStatus.IN_PROGRESS));
		assertThat(performedProcedureStepStatus, is("IN PROGRESS"));
	}
	
	/**
	 * @see {@link DicomUtils#getPerformedProcedureStepStatus(DicomObject)}
	 */
	@Test
	@Verifies(value = "return null given given dicom object without performed procedure step status", method = "getPerformedProcedureStepStatus(DicomObject)")
	public void getPerformedProcedureStepStatus_shouldReturnNullGivenDicomObjectWithoutPerformedProcedureStepStatus() {
		
		Study study = radiologyService.getStudy(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = study.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(study, radiologyOrder);
		dicomMpps.remove(Tag.PerformedProcedureStepStatus);
		
		String performedProcedureStepStatus = DicomUtils.getPerformedProcedureStepStatus(dicomMpps);
		
		assertNull(performedProcedureStepStatus);
	}
	
	/**
	 * Convenience method to create a mock radiology order
	 */
	RadiologyOrder getMockRadiologyOrder() {
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
		
		RadiologyOrder mockRadiologyOrder = new RadiologyOrder();
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
		mockStudy.setStudyId(1);
		mockStudy.setRadiologyOrder(getMockRadiologyOrder());
		mockStudy.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1");
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
		assertEquals(String.valueOf(study.getStudyId()), orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
		assertEquals(null, orc.getOrderStatus().getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(order.getStartDate()), orc.getQuantityTiming()
		        .getStartDateTime().getTimeOfAnEvent().getValue());
		assertEquals("S", orc.getQuantityTiming().getPriority().getValue());
		
		// OBR segment
		OBR obr = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
		        .getOBR();
		assertEquals(order.getInstructions(), obr.getUniversalServiceID().getAlternateText().getValue());
		assertEquals(String.valueOf(study.getStudyId()), obr.getPlacerField2().getValue());
		assertEquals(String.valueOf(study.getStudyId()), obr.getFillerField1().getValue());
		assertEquals(study.getModality().name(), obr.getDiagnosticServSectID().getValue());
		assertEquals(order.getInstructions(), obr.getProcedureCode().getText().getValue());
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertEquals(study.getStudyInstanceUid(), terser.get("/.ZDS-1-1"));
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
		assertEquals(String.valueOf(study.getStudyId()), orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
		assertEquals(null, orc.getOrderStatus().getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(order.getStartDate()), orc.getQuantityTiming()
		        .getStartDateTime().getTimeOfAnEvent().getValue());
		assertEquals("S", orc.getQuantityTiming().getPriority().getValue());
		
		// OBR segment
		OBR obr = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
		        .getOBR();
		assertEquals(order.getInstructions(), obr.getUniversalServiceID().getAlternateText().getValue());
		assertEquals(String.valueOf(study.getStudyId()), obr.getPlacerField2().getValue());
		assertEquals(String.valueOf(study.getStudyId()), obr.getFillerField1().getValue());
		assertEquals(study.getModality().name(), obr.getDiagnosticServSectID().getValue());
		assertEquals(order.getInstructions(), obr.getProcedureCode().getText().getValue());
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertEquals(study.getStudyInstanceUid(), terser.get("/.ZDS-1-1"));
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
		assertEquals(String.valueOf(study.getStudyId()), orc.getPlacerOrderNumber().getEntityIdentifier().getValue());
		assertEquals(null, orc.getOrderStatus().getValue());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(order.getStartDate()), orc.getQuantityTiming()
		        .getStartDateTime().getTimeOfAnEvent().getValue());
		assertEquals("S", orc.getQuantityTiming().getPriority().getValue());
		
		// OBR segment
		OBR obr = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
		        .getOBR();
		assertEquals(order.getInstructions(), obr.getUniversalServiceID().getAlternateText().getValue());
		assertEquals(String.valueOf(study.getStudyId()), obr.getPlacerField2().getValue());
		assertEquals(String.valueOf(study.getStudyId()), obr.getFillerField1().getValue());
		assertEquals(study.getModality().name(), obr.getDiagnosticServSectID().getValue());
		assertEquals(order.getInstructions(), obr.getProcedureCode().getText().getValue());
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertEquals(study.getStudyInstanceUid(), terser.get("/.ZDS-1-1"));
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
