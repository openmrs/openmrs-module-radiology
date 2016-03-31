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
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.CommonOrderPriority;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class DicomUtilsComponentTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceComponentTestDataset.xml";
	
	protected static final int STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER = 1;
	
	protected static final String DICOM_SPECIFIC_CHARACTER_SET = "ISO-8859-1";
	
	protected static final String MWL_DIRECTORY = "mwl";
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService administrationService;
	
	@Autowired
	private RadiologyService radiologyService;
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	@Rule
	public TemporaryFolder temporaryBaseFolder = new TemporaryFolder();
	
	/**
	 * Run this before each unit test in this class. It simply assigns the services used in this
	 * class to private variables The "@Before" method in {@link BaseContextSensitiveTest} is run
	 * right before this method and sets up the initial data set and authenticates to the Context
	 * 
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_SPECIFIC_CHARCATER_SET,
		        DICOM_SPECIFIC_CHARACTER_SET));
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * Tests the DicomUtils.writeMpps method with a DicomObject containing DICOM command N-CREATE
	 * (PerformedProcedureStepStatus = IN PROGRESS) for an existing study. DICOM Performed Procedure
	 * Step Status; IN PROGRESS = Started but not complete
	 * 
	 * @see {@link DicomUtils#updateStudyPerformedStatusByMpps(DicomObject)}
	 * @verifies should set performed status of an existing study in database to performed procedure step status IN_PROGRESS of given dicom object
	 */
	@Test
	public void updateStudyPerformedStatusByMpps_shouldSetPerformedStatusOfAnExistingStudyInDatabaseToPerformedProcedureStepStatusIN_PROGRESSOfGivenDicomObject()
	        throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		Study studyToBeUpdated = radiologyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNCreate(studyToBeUpdated, radiologyOrder);
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertThat(studyToBeUpdated.getPerformedStatus(), is(PerformedProcedureStepStatus.IN_PROGRESS));
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
	 * @verifies should set the performed status of an existing study in the database to DISCONTINUED given a dicom object containing command N-CREATE
	 */
	@Test
	public void updateStudyPerformedStatusByMPPS_shouldUpdateThePerformedStatusOfAnExistingStudyInTheDatabaseBasedOnADicomObject()
	        throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		Study studyToBeUpdated = radiologyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNSet(studyToBeUpdated, radiologyOrder, "DISCONTINUED");
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertThat(studyToBeUpdated.getPerformedStatus(), is(PerformedProcedureStepStatus.DISCONTINUED));
	}
	
	/**
	 * Convenience method to create a DicomObject containing DICOM command N-SET
	 * (PerformedProcedureStepStatus = DISCONTINUED/COMPLETED) for an existing study.
	 * 
	 * @param study study for which the DicomObject will be created
	 * @param radiologyOrder order associated with given study
	 * @param performedProcedureStatus DICOM Performed Procedure Step Status either DISCONTINUED or
	 *            COMPLETED
	 */
	DicomObject getDicomNSet(Study study, Order radiologyOrder, String performedProcedureStatus) {
		
		SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(radiologyProperties
		        .getDicomSpecificCharacterSet());
		
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
	 * @verifies should set the performed status of an existing study in the database to COMPLETED given a dicom object containing command N-CREATE
	 */
	@Test
	public void updateStudyPerformedStatusByMpps_shouldUpdateThePerformedStatusOfAnExistingStudyInTheDatabaseBasedOnADicomObjectCompleted()
	        throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		Study studyToBeUpdated = radiologyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNSet(studyToBeUpdated, radiologyOrder, "COMPLETED");
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertThat(studyToBeUpdated.getPerformedStatus(), is(PerformedProcedureStepStatus.COMPLETED));
	}
	
	/**
	 * @see {@link DicomUtils#updateStudyPerformedStatusByMpps(DicomObject)}
	 * @verifies not fail if study instance uid referenced in dicom mpps cannot be found
	 */
	//	@Test
	public void updateStudyPerformedStatusByMpps_shouldNotFailIfStudyInstanceUidReferencedInDicomMppsCannotBeFound()
	        throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		Study studyToBeUpdated = radiologyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNSet(studyToBeUpdated, radiologyOrder, "COMPLETED");
		dicomObjectNCreate.remove(Tag.ScheduledStepAttributesSequence);
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertThat(studyToBeUpdated.getPerformedStatus(), is(PerformedProcedureStepStatus.COMPLETED));
	}
	
	/**
	 * @see {@link DicomUtils#getStudyInstanceUidFromMpps(DicomObject)}
	 * @verifies should return study instance uid given mpps dicom object
	 */
	@Test
	public void getStudyInstanceUidFromMpps_shouldReturnStudyInstanceUidGivenDicomMppsObject() {
		
		Study study = radiologyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = study.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(study, radiologyOrder);
		
		String studyInstanceUid = DicomUtils.getStudyInstanceUidFromMpps(dicomMpps);
		
		assertThat(study.getStudyInstanceUid(), is(studyInstanceUid));
	}
	
	/**
	 * @see {@link DicomUtils#getStudyInstanceUidFromMpps(DicomObject)}
	 * @verifies should return null given dicom mpps object without scheduled step attributes sequence
	 */
	@Test
	public void getStudyInstanceUidFromMpps_shouldReturnNullGivenDicomMppsObjectWithoutScheduledStepAttributesSequence() {
		
		Study study = radiologyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = study.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(study, radiologyOrder);
		dicomMpps.remove(Tag.ScheduledStepAttributesSequence);
		
		String studyInstanceUid = DicomUtils.getStudyInstanceUidFromMpps(dicomMpps);
		
		assertThat(studyInstanceUid, is(nullValue()));
	}
	
	/**
	 * @see {@link DicomUtils#getStudyInstanceUidFromMpps(DicomObject)}
	 * @verifies should return null given dicom mpps object with scheduled step attributes sequence missing study instance uid tag
	 */
	@Test
	public void getStudyInstanceUidFromMpps_shouldReturnNullGivenDicomMppsObjectWithScheduledStepAttributesSequenceMissingStudyInstanceUidTag() {
		
		Study study = radiologyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = study.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(study, radiologyOrder);
		
		dicomMpps.get(Tag.ScheduledStepAttributesSequence).getDicomObject().remove(Tag.StudyInstanceUID);
		
		String studyInstanceUid = DicomUtils.getStudyInstanceUidFromMpps(dicomMpps);
		
		assertThat(studyInstanceUid, is(nullValue()));
	}
	
	/**
	 * @see {@link DicomUtils#getPerformedProcedureStepStatus(DicomObject)}
	 * @verifies should return performed procedure step status given dicom object
	 */
	@Test
	public void getPerformedProcedureStepStatus_shouldReturnPerformedProcedureStepStatusGivenMppsDicomObject() {
		
		Study study = radiologyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = study.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(study, radiologyOrder);
		
		String performedProcedureStepStatus = DicomUtils.getPerformedProcedureStepStatus(dicomMpps);
		
		assertThat(study.getPerformedStatus(), is(PerformedProcedureStepStatus.IN_PROGRESS));
		assertThat(performedProcedureStepStatus, is("IN PROGRESS"));
	}
	
	/**
	 * @see {@link DicomUtils#getPerformedProcedureStepStatus(DicomObject)}
	 * @verifies return null given given dicom object without performed procedure step status
	 */
	@Test
	public void getPerformedProcedureStepStatus_shouldReturnNullGivenDicomObjectWithoutPerformedProcedureStepStatus() {
		
		Study study = radiologyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = study.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(study, radiologyOrder);
		dicomMpps.remove(Tag.PerformedProcedureStepStatus);
		
		String performedProcedureStepStatus = DicomUtils.getPerformedProcedureStepStatus(dicomMpps);
		
		assertThat(performedProcedureStepStatus, is(nullValue()));
	}
	
	/**
	 * Convenience method to create a mock radiology order
	 */
	RadiologyOrder getMockRadiologyOrder() throws Exception {
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		mockPatient.setBirthdate(calendar.getTime());
		
		RadiologyOrder mockRadiologyOrder = new RadiologyOrder();
		mockRadiologyOrder.setOrderId(20);
		
		Field orderNumber = Order.class.getDeclaredField("orderNumber");
		orderNumber.setAccessible(true);
		orderNumber.set(mockRadiologyOrder, "ORD-" + mockRadiologyOrder.getOrderId());
		
		mockRadiologyOrder.setPatient(mockPatient);
		calendar.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		mockRadiologyOrder.setScheduledDate(calendar.getTime());
		mockRadiologyOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
		mockRadiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		return mockRadiologyOrder;
	}
	
	/**
	 * Convenience method to create a mock radiology study
	 */
	Study getMockStudy() throws Exception {
		Study mockStudy = new Study();
		mockStudy.setStudyId(1);
		mockStudy.setRadiologyOrder(getMockRadiologyOrder());
		mockStudy.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1");
		mockStudy.setModality(Modality.CT);
		
		return mockStudy;
	}
	
	/**
	 * @see {@link DicomUtils#createHL7Message(RadiologyOrder, OrderRequest)}
	 * @verifies should return encoded HL7 ORMO01 message string with new order control given study with mwlstatus default and save order request
	 */
	@Test
	public void createHL7Message_shouldReturnEncodedHL7ORMO01MessageStringWithNewOrderControlGivenStudyWithMwlstatusDefaultAndSaveOrderRequest()
	        throws Exception {
		
		RadiologyOrder radiologyOrder = getMockRadiologyOrder();
		Study study = getMockStudy();
		study.setMwlStatus(MwlStatus.DEFAULT);
		radiologyOrder.setStudy(study);
		
		String saveOrderHL7String = DicomUtils.createHL7Message(radiologyOrder, DicomUtils.OrderRequest.Save_Order);
		
		assertThat(saveOrderHL7String, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    saveOrderHL7String,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|NW|ORD-20|||||^^^20150204143500^^T\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||ORD-20|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
		
		PipeParser hl7PipeParser = new PipeParser();
		Message saveOrderHL7Message = hl7PipeParser.parse(saveOrderHL7String);
		ORM_O01 ormMsg = (ORM_O01) saveOrderHL7Message;
		
		// MSH segment
		MSH msh = ormMsg.getMSH();
		assertThat(msh.getVersionID().getVersionID().getValue(), is("2.3.1"));
		assertThat(msh.getMessageType().getMessageType().getValue(), is("ORM"));
		assertThat(msh.getMessageType().getTriggerEvent().getValue(), is("O01"));
		assertThat(msh.getSendingApplication().getNamespaceID().getValue(), is("OpenMRSRadiologyModule"));
		assertThat(msh.getSendingFacility().getNamespaceID().getValue(), is("OpenMRS"));
		assertThat(msh.getProcessingID().getProcessingID().getValue(), is("P"));
		
		// PID segment
		Patient expectedPatient = radiologyOrder.getPatient();
		PID pid = ormMsg.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1().getPID();
		assertThat(pid.getPatientIdentifierList(0).getID().getValue(), is(expectedPatient.getPatientIdentifier()
		        .getIdentifier()));
		assertThat(pid.getDateTimeOfBirth().getTimeOfAnEvent().getValue(), is(new SimpleDateFormat("yyyyMMddHHmmss")
		        .format(expectedPatient.getBirthdate())));
		assertThat(pid.getSex().getValue(), is(expectedPatient.getGender()));
		assertThat(pid.getPatientName(0).getFamilyLastName().getFamilyName().getValue(), is(expectedPatient.getPersonName()
		        .getFamilyName()));
		assertThat(pid.getPatientName(0).getMiddleInitialOrName().getValue(), is(expectedPatient.getPersonName()
		        .getMiddleName()));
		assertThat(pid.getPatientName(0).getGivenName().getValue(), is(expectedPatient.getPersonName().getGivenName()));
		
		// ORC segment
		ORC orc = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC();
		assertThat(orc.getOrderControl().getValue(), is("NW"));
		assertThat(orc.getPlacerOrderNumber().getEntityIdentifier().getValue(), is(radiologyOrder.getOrderNumber()));
		assertThat(orc.getOrderStatus().getValue(), is(nullValue()));
		assertThat(orc.getQuantityTiming().getStartDateTime().getTimeOfAnEvent().getValue(), is(new SimpleDateFormat(
		        "yyyyMMddHHmmss").format(radiologyOrder.getEffectiveStartDate())));
		assertThat(orc.getQuantityTiming().getPriority().getValue(), is("T"));
		
		// OBR segment
		OBR obr = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
		        .getOBR();
		assertThat(obr.getUniversalServiceID().getAlternateText().getValue(), is(radiologyOrder.getInstructions()));
		assertThat(obr.getPlacerField2().getValue(), is(radiologyOrder.getOrderNumber()));
		assertThat(obr.getFillerField1().getValue(), is(String.valueOf(study.getStudyId())));
		assertThat(obr.getDiagnosticServSectID().getValue(), is(study.getModality().name()));
		assertThat(obr.getProcedureCode().getText().getValue(), is(radiologyOrder.getInstructions()));
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertThat(terser.get("/.ZDS-1-1"), is(study.getStudyInstanceUid()));
		assertThat(terser.get("/.ZDS-1-2"), is(nullValue()));
		assertThat(terser.get("/.ZDS-1-3"), is("Application"));
		assertThat(terser.get("/.ZDS-1-4"), is("DICOM"));
	}
	
	/**
	 * @see {@link DicomUtils#createHL7Message(RadiologyOrder, OrderRequest)}
	 * @verifies should return encoded HL7 ORMO01 message string with cancel order control given study with mwlstatus default and void order request
	 */
	@Test
	public void createHL7Message_shouldReturnEncodedHL7ORMO01MessageStringWithCancelOrderControlGivenStudyWithMwlstatusDefaultAndVoidOrderRequest()
	        throws Exception {
		
		RadiologyOrder radiologyOrder = getMockRadiologyOrder();
		Study study = getMockStudy();
		study.setMwlStatus(MwlStatus.DEFAULT);
		radiologyOrder.setStudy(study);
		
		String saveOrderHL7String = DicomUtils.createHL7Message(radiologyOrder, DicomUtils.OrderRequest.Void_Order);
		
		assertThat(saveOrderHL7String, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    saveOrderHL7String,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|CA|ORD-20|||||^^^20150204143500^^T\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||ORD-20|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
		
		PipeParser hl7PipeParser = new PipeParser();
		Message saveOrderHL7Message = hl7PipeParser.parse(saveOrderHL7String);
		ORM_O01 ormMsg = (ORM_O01) saveOrderHL7Message;
		
		// MSH segment
		MSH msh = ormMsg.getMSH();
		assertThat(msh.getVersionID().getVersionID().getValue(), is("2.3.1"));
		assertThat(msh.getMessageType().getMessageType().getValue(), is("ORM"));
		assertThat(msh.getMessageType().getTriggerEvent().getValue(), is("O01"));
		assertThat(msh.getSendingApplication().getNamespaceID().getValue(), is("OpenMRSRadiologyModule"));
		assertThat(msh.getSendingFacility().getNamespaceID().getValue(), is("OpenMRS"));
		assertThat(msh.getProcessingID().getProcessingID().getValue(), is("P"));
		
		// PID segment
		Patient expectedPatient = radiologyOrder.getPatient();
		PID pid = ormMsg.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1().getPID();
		assertThat(pid.getPatientIdentifierList(0).getID().getValue(), is(expectedPatient.getPatientIdentifier()
		        .getIdentifier()));
		assertThat(pid.getDateTimeOfBirth().getTimeOfAnEvent().getValue(), is(new SimpleDateFormat("yyyyMMddHHmmss")
		        .format(expectedPatient.getBirthdate())));
		assertThat(pid.getSex().getValue(), is(expectedPatient.getGender()));
		assertThat(pid.getPatientName(0).getFamilyLastName().getFamilyName().getValue(), is(expectedPatient.getPersonName()
		        .getFamilyName()));
		assertThat(pid.getPatientName(0).getMiddleInitialOrName().getValue(), is(expectedPatient.getPersonName()
		        .getMiddleName()));
		assertThat(pid.getPatientName(0).getGivenName().getValue(), is(expectedPatient.getPersonName().getGivenName()));
		
		// ORC segment
		ORC orc = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC();
		assertThat(orc.getOrderControl().getValue(), is("CA"));
		assertThat(orc.getPlacerOrderNumber().getEntityIdentifier().getValue(), is(radiologyOrder.getOrderNumber()));
		assertThat(orc.getOrderStatus().getValue(), is(nullValue()));
		assertThat(orc.getQuantityTiming().getStartDateTime().getTimeOfAnEvent().getValue(), is(new SimpleDateFormat(
		        "yyyyMMddHHmmss").format(radiologyOrder.getEffectiveStartDate())));
		assertThat(orc.getQuantityTiming().getPriority().getValue(), is("T"));
		
		// OBR segment
		OBR obr = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
		        .getOBR();
		assertThat(obr.getUniversalServiceID().getAlternateText().getValue(), is(radiologyOrder.getInstructions()));
		assertThat(obr.getPlacerField2().getValue(), is(radiologyOrder.getOrderNumber()));
		assertThat(obr.getFillerField1().getValue(), is(String.valueOf(study.getStudyId())));
		assertThat(obr.getDiagnosticServSectID().getValue(), is(study.getModality().name()));
		assertThat(obr.getProcedureCode().getText().getValue(), is(radiologyOrder.getInstructions()));
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertThat(terser.get("/.ZDS-1-1"), is(study.getStudyInstanceUid()));
		assertThat(terser.get("/.ZDS-1-2"), is(nullValue()));
		assertThat(terser.get("/.ZDS-1-3"), is("Application"));
		assertThat(terser.get("/.ZDS-1-4"), is("DICOM"));
	}
	
	/**
	 * @see {@link DicomUtils#createHL7Message(RadiologyOrder, OrderRequest)}
	 * @verifies should return encoded HL7 ORMO01 message string with change order control given study with mwlstatus save ok and save order request
	 */
	@Test
	public void createHL7Message_shouldReturnEncodedHL7ORMO01MessageStringWithChangeOrderControlGivenStudyWithMwlstatusSaveOkAndSaveOrderRequest()
	        throws Exception {
		
		RadiologyOrder radiologyOrder = getMockRadiologyOrder();
		Study study = getMockStudy();
		study.setMwlStatus(MwlStatus.SAVE_OK);
		radiologyOrder.setStudy(study);
		
		String saveOrderHL7String = DicomUtils.createHL7Message(radiologyOrder, DicomUtils.OrderRequest.Save_Order);
		
		assertThat(saveOrderHL7String, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    saveOrderHL7String,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|XO|ORD-20|||||^^^20150204143500^^T\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||ORD-20|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
		
		PipeParser hl7PipeParser = new PipeParser();
		Message saveOrderHL7Message = hl7PipeParser.parse(saveOrderHL7String);
		ORM_O01 ormMsg = (ORM_O01) saveOrderHL7Message;
		
		// MSH segment
		MSH msh = ormMsg.getMSH();
		assertThat(msh.getVersionID().getVersionID().getValue(), is("2.3.1"));
		assertThat(msh.getMessageType().getMessageType().getValue(), is("ORM"));
		assertThat(msh.getMessageType().getTriggerEvent().getValue(), is("O01"));
		assertThat(msh.getSendingApplication().getNamespaceID().getValue(), is("OpenMRSRadiologyModule"));
		assertThat(msh.getSendingFacility().getNamespaceID().getValue(), is("OpenMRS"));
		assertThat(msh.getProcessingID().getProcessingID().getValue(), is("P"));
		
		// PID segment
		Patient expectedPatient = radiologyOrder.getPatient();
		PID pid = ormMsg.getPIDPD1NTEPV1PV2IN1IN2IN3GT1AL1().getPID();
		assertThat(pid.getPatientIdentifierList(0).getID().getValue(), is(expectedPatient.getPatientIdentifier()
		        .getIdentifier()));
		assertThat(pid.getDateTimeOfBirth().getTimeOfAnEvent().getValue(), is(new SimpleDateFormat("yyyyMMddHHmmss")
		        .format(expectedPatient.getBirthdate())));
		assertThat(pid.getSex().getValue(), is(expectedPatient.getGender()));
		assertThat(pid.getPatientName(0).getFamilyLastName().getFamilyName().getValue(), is(expectedPatient.getPersonName()
		        .getFamilyName()));
		assertThat(pid.getPatientName(0).getMiddleInitialOrName().getValue(), is(expectedPatient.getPersonName()
		        .getMiddleName()));
		assertThat(pid.getPatientName(0).getGivenName().getValue(), is(expectedPatient.getPersonName().getGivenName()));
		
		// ORC segment
		ORC orc = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC();
		assertThat(orc.getOrderControl().getValue(), is("XO"));
		assertThat(orc.getPlacerOrderNumber().getEntityIdentifier().getValue(), is(radiologyOrder.getOrderNumber()));
		assertThat(orc.getOrderStatus().getValue(), is(nullValue()));
		assertThat(orc.getQuantityTiming().getStartDateTime().getTimeOfAnEvent().getValue(), is(new SimpleDateFormat(
		        "yyyyMMddHHmmss").format(radiologyOrder.getEffectiveStartDate())));
		assertThat(orc.getQuantityTiming().getPriority().getValue(), is("T"));
		
		// OBR segment
		OBR obr = ormMsg.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTE()
		        .getOBR();
		assertThat(obr.getUniversalServiceID().getAlternateText().getValue(), is(radiologyOrder.getInstructions()));
		assertThat(obr.getPlacerField2().getValue(), is(radiologyOrder.getOrderNumber()));
		assertThat(obr.getFillerField1().getValue(), is(String.valueOf(study.getStudyId())));
		assertThat(obr.getDiagnosticServSectID().getValue(), is(study.getModality().name()));
		assertThat(obr.getProcedureCode().getText().getValue(), is(radiologyOrder.getInstructions()));
		
		// ZDS Segment
		Terser terser = new Terser(ormMsg);
		assertThat(terser.get("/.ZDS-1-1"), is(study.getStudyInstanceUid()));
		assertThat(terser.get("/.ZDS-1-2"), is(nullValue()));
		assertThat(terser.get("/.ZDS-1-3"), is("Application"));
		assertThat(terser.get("/.ZDS-1-4"), is("DICOM"));
	}
	
	/**
	 * @see {@link DicomUtils#getCommonOrderPriorityFrom(Order.Urgency)}
	 * @verifies should return hl7 common order priority given order urgency
	 */
	@Test
	public void getCommonOrderPriorityFrom_shouldReturnHL7CommonOrderPriorityGivenOrderUrgency() {
		
		assertThat(DicomUtils.getCommonOrderPriorityFrom(Order.Urgency.STAT), is(CommonOrderPriority.STAT));
		assertThat(DicomUtils.getCommonOrderPriorityFrom(Order.Urgency.ROUTINE), is(CommonOrderPriority.ROUTINE));
		assertThat(DicomUtils.getCommonOrderPriorityFrom(Order.Urgency.ON_SCHEDULED_DATE),
		    is(CommonOrderPriority.TIMING_CRITICAL));
	}
	
	/**
	 * @see {@link DicomUtils#getCommonOrderPriorityFrom(Order.Urgency)}
	 * @verifies should return default hl7 common order priority given null
	 */
	@Test
	public void getCommonOrderPriorityFrom_shouldReturnDefaultHL7CommonOrderPriorityGivenNull() {
		
		assertThat(DicomUtils.getCommonOrderPriorityFrom(null), is(CommonOrderPriority.ROUTINE));
	}
	
	/**
	 * @see {@link DicomUtils#getCommonOrderControlFrom(MwlStatus, OrderRequest)}
	 * @verifies should return hl7 order control given mwlstatus and orderrequest
	 */
	@Test
	public void getCommonOrderControlFrom_shouldReturnHL7OrderControlGivenMwlstatusAndOrderRequest() {
		
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Save_Order),
		    is(CommonOrderOrderControl.NEW_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_ERR, DicomUtils.OrderRequest.Save_Order),
		    is(CommonOrderOrderControl.NEW_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_OK, DicomUtils.OrderRequest.Save_Order),
		    is(CommonOrderOrderControl.CHANGE_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.UPDATE_OK, DicomUtils.OrderRequest.Save_Order),
		    is(CommonOrderOrderControl.CHANGE_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Void_Order),
		    is(CommonOrderOrderControl.CANCEL_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Void_Order),
		    is(CommonOrderOrderControl.CANCEL_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_OK, DicomUtils.OrderRequest.Void_Order),
		    is(CommonOrderOrderControl.CANCEL_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Unvoid_Order),
		    is(CommonOrderOrderControl.NEW_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Unvoid_Order),
		    is(CommonOrderOrderControl.NEW_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_OK, DicomUtils.OrderRequest.Unvoid_Order),
		    is(CommonOrderOrderControl.NEW_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Discontinue_Order),
		    is(CommonOrderOrderControl.CANCEL_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Discontinue_Order),
		    is(CommonOrderOrderControl.CANCEL_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_OK, DicomUtils.OrderRequest.Discontinue_Order),
		    is(CommonOrderOrderControl.CANCEL_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Undiscontinue_Order),
		    is(CommonOrderOrderControl.NEW_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Undiscontinue_Order),
		    is(CommonOrderOrderControl.NEW_ORDER));
		assertThat(DicomUtils.getCommonOrderControlFrom(MwlStatus.SAVE_OK, DicomUtils.OrderRequest.Undiscontinue_Order),
		    is(CommonOrderOrderControl.NEW_ORDER));
	}
	
}
