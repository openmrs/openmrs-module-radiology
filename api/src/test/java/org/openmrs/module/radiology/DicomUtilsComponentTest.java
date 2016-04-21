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
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.study.RadiologyStudyService;
import org.openmrs.module.radiology.study.RadiologyStudy;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.xml.sax.SAXException;

/**
 * Tests {@link DicomUtils}
 */
public class DicomUtilsComponentTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATASET = "org/openmrs/module/radiology/include/DicomUtilsComponentTestDataset.xml";
	
	protected static final int STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER = 1;
	
	protected static final String DICOM_SPECIFIC_CHARACTER_SET = "ISO-8859-1";
	
	protected static final String MWL_DIRECTORY = "mwl";
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService administrationService;
	
	@Autowired
	private RadiologyStudyService radiologyStudyService;
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	@Rule
	public TemporaryFolder temporaryBaseFolder = new TemporaryFolder();
	
	/**
	 * Run this before each unit test in this class. It simply assigns the services used in this
	 * class to private variables The "@Before" method in {@link BaseContextSensitiveTest} is run
	 * right before this method and sets up the initial data set and authenticates to the Context
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_DICOM_SPECIFIC_CHARCATER_SET,
				DICOM_SPECIFIC_CHARACTER_SET));
		
		executeDataSet(TEST_DATASET);
	}
	
	/**
	 * Tests the DicomUtils.writeMpps method with a DicomObject containing DICOM command N-CREATE
	 * (PerformedProcedureStepStatus = IN PROGRESS) for an existing study. DICOM Performed Procedure
	 * Step Status; IN PROGRESS = Started but not complete
	 * 
	 * @see {@link DicomUtils#updateStudyPerformedStatusByMpps(DicomObject)}
	 * @verifies should set performed status of an existing study in database to performed procedure step status IN_PROGRESS
	 *           of given dicom object
	 */
	@Test
	public void updateStudyPerformedStatusByMpps_shouldSetPerformedStatusOfAnExistingStudyInDatabaseToPerformedProcedureStepStatusIN_PROGRESSOfGivenDicomObject()
			throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		RadiologyStudy studyToBeUpdated = radiologyStudyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNCreate(studyToBeUpdated, radiologyOrder);
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR,
				temporaryMwlFolder.getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertThat(studyToBeUpdated.getPerformedStatus(), is(PerformedProcedureStepStatus.IN_PROGRESS));
	}
	
	/**
	 * Convenience method to create a DicomObject containing DICOM command N-CREATE
	 * (PerformedProcedureStepStatus = IN_PROGRESS) for an existing study.
	 * 
	 * @param radiologyStudy study for which the DicomObject will be created
	 * @param radiologyOrder order associated with given study
	 */
	DicomObject getDicomNCreate(RadiologyStudy radiologyStudy, Order radiologyOrder) {
		
		String performedProcedureStatus = "IN PROGRESS";
		String performedProcedureStepStartDate = "20150313";
		String performedProcedureStepStartTime = "130225";
		String performedStationAETitle = "CR01";
		String performedStationName = "Radiology Department";
		String performedLocation = "Room 01";
		String performedProcedureStepID = "XX 01";
		
		String referencedSOPClassUID = "1.2.840.10008.3.1.2.3.1";
		String referencedSOPInstanceUID = "1.2.840.10008.5.1.4.1.1.9.1.2.1.1.1";
		
		String studyID = String.valueOf(radiologyStudy.getStudyId());
		String studyInstanceUID = radiologyStudy.getStudyInstanceUid();
		String modality = radiologyStudy.getModality()
				.name();
		
		String accessionNumber = radiologyOrder.getAccessionNumber();
		String scheduledProcedureStepDescription = radiologyOrder.getInstructions();
		
		Patient patient = radiologyOrder.getPatient();
		String patientName = patient.getPersonName()
				.getFullName()
				.replace(' ', '^');
		String patientID = patient.getPatientIdentifier()
				.getIdentifier();
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
	 * @verifies should set the performed status of an existing study in the database to DISCONTINUED given a dicom object
	 *           containing command N-CREATE
	 */
	@Test
	public void updateStudyPerformedStatusByMPPS_shouldUpdateThePerformedStatusOfAnExistingStudyInTheDatabaseBasedOnADicomObject()
			throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		RadiologyStudy studyToBeUpdated = radiologyStudyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNSet(studyToBeUpdated, radiologyOrder, "DISCONTINUED");
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR,
				temporaryMwlFolder.getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertThat(studyToBeUpdated.getPerformedStatus(), is(PerformedProcedureStepStatus.DISCONTINUED));
	}
	
	/**
	 * Convenience method to create a DicomObject containing DICOM command N-SET
	 * (PerformedProcedureStepStatus = DISCONTINUED/COMPLETED) for an existing study.
	 * 
	 * @param radiologyStudy study for which the DicomObject will be created
	 * @param radiologyOrder order associated with given study
	 * @param performedProcedureStatus DICOM Performed Procedure Step Status either DISCONTINUED or
	 *        COMPLETED
	 */
	DicomObject getDicomNSet(RadiologyStudy radiologyStudy, Order radiologyOrder, String performedProcedureStatus) {
		
		SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(
				radiologyProperties.getDicomSpecificCharacterSet());
		
		String performedProcedureStepEndDate = "20150313";
		String performedProcedureStepEndTime = "133725";
		String performingPhysicianName = "Doctor^Shiwago";
		String seriesDescription = "Thorax scan";
		String operatorsName = "Doctor^Shiwago";
		String protocolName = "Thorax";
		String seriesInstanceUID = "1.2.826.0.1.3680043.2.1545.1.2.1.7.20150313.130225.305.1";
		
		DicomObject dicomObject = getDicomNCreate(radiologyStudy, radiologyOrder);
		dicomObject.putString(Tag.PerformedProcedureStepEndDate, VR.DA, performedProcedureStepEndDate);
		dicomObject.putString(Tag.PerformedProcedureStepEndTime, VR.TM, performedProcedureStepEndTime);
		dicomObject.putString(Tag.PerformedProcedureStepStatus, VR.CS, performedProcedureStatus);
		String retrieveAETitle = dicomObject.get(Tag.PerformedStationAETitle)
				.getValueAsString(specificCharacterSet, 0);
		
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
	 * @verifies should set the performed status of an existing study in the database to COMPLETED given a dicom object
	 *           containing command N-CREATE
	 */
	@Test
	public void updateStudyPerformedStatusByMpps_shouldUpdateThePerformedStatusOfAnExistingStudyInTheDatabaseBasedOnADicomObjectCompleted()
			throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		RadiologyStudy studyToBeUpdated = radiologyStudyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNSet(studyToBeUpdated, radiologyOrder, "COMPLETED");
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR,
				temporaryMwlFolder.getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertThat(studyToBeUpdated.getPerformedStatus(), is(PerformedProcedureStepStatus.COMPLETED));
	}
	
	/**
	 * @see {@link DicomUtils#updateStudyPerformedStatusByMpps(DicomObject)}
	 * @verifies not fail if study instance uid referenced in dicom mpps cannot be found
	 */
	// @Test
	public void updateStudyPerformedStatusByMpps_shouldNotFailIfStudyInstanceUidReferencedInDicomMppsCannotBeFound()
			throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, SAXException {
		
		RadiologyStudy studyToBeUpdated = radiologyStudyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = studyToBeUpdated.getRadiologyOrder();
		DicomObject dicomObjectNCreate = getDicomNSet(studyToBeUpdated, radiologyOrder, "COMPLETED");
		dicomObjectNCreate.remove(Tag.ScheduledStepAttributesSequence);
		
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR,
				temporaryMwlFolder.getAbsolutePath()));
		
		DicomUtils.updateStudyPerformedStatusByMpps(dicomObjectNCreate);
		
		assertThat(studyToBeUpdated.getPerformedStatus(), is(PerformedProcedureStepStatus.COMPLETED));
	}
	
	/**
	 * @see {@link DicomUtils#getStudyInstanceUidFromMpps(DicomObject)}
	 * @verifies should return study instance uid given mpps dicom object
	 */
	@Test
	public void getStudyInstanceUidFromMpps_shouldReturnStudyInstanceUidGivenDicomMppsObject() {
		
		RadiologyStudy radiologyStudy = radiologyStudyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = radiologyStudy.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(radiologyStudy, radiologyOrder);
		
		String studyInstanceUid = DicomUtils.getStudyInstanceUidFromMpps(dicomMpps);
		
		assertThat(radiologyStudy.getStudyInstanceUid(), is(studyInstanceUid));
	}
	
	/**
	 * @see {@link DicomUtils#getStudyInstanceUidFromMpps(DicomObject)}
	 * @verifies should return null given dicom mpps object without scheduled step attributes sequence
	 */
	@Test
	public void getStudyInstanceUidFromMpps_shouldReturnNullGivenDicomMppsObjectWithoutScheduledStepAttributesSequence() {
		
		RadiologyStudy radiologyStudy = radiologyStudyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = radiologyStudy.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(radiologyStudy, radiologyOrder);
		dicomMpps.remove(Tag.ScheduledStepAttributesSequence);
		
		String studyInstanceUid = DicomUtils.getStudyInstanceUidFromMpps(dicomMpps);
		
		assertThat(studyInstanceUid, is(nullValue()));
	}
	
	/**
	 * @see {@link DicomUtils#getStudyInstanceUidFromMpps(DicomObject)}
	 * @verifies should return null given dicom mpps object with scheduled step attributes sequence missing study instance
	 *           uid tag
	 */
	@Test
	public void getStudyInstanceUidFromMpps_shouldReturnNullGivenDicomMppsObjectWithScheduledStepAttributesSequenceMissingStudyInstanceUidTag() {
		
		RadiologyStudy radiologyStudy = radiologyStudyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = radiologyStudy.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(radiologyStudy, radiologyOrder);
		
		dicomMpps.get(Tag.ScheduledStepAttributesSequence)
				.getDicomObject()
				.remove(Tag.StudyInstanceUID);
		
		String studyInstanceUid = DicomUtils.getStudyInstanceUidFromMpps(dicomMpps);
		
		assertThat(studyInstanceUid, is(nullValue()));
	}
	
	/**
	 * @see {@link DicomUtils#getPerformedProcedureStepStatus(DicomObject)}
	 * @verifies should return performed procedure step status given dicom object
	 */
	@Test
	public void getPerformedProcedureStepStatus_shouldReturnPerformedProcedureStepStatusGivenMppsDicomObject() {
		
		RadiologyStudy radiologyStudy = radiologyStudyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = radiologyStudy.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(radiologyStudy, radiologyOrder);
		
		String performedProcedureStepStatus = DicomUtils.getPerformedProcedureStepStatus(dicomMpps);
		
		assertThat(radiologyStudy.getPerformedStatus(), is(PerformedProcedureStepStatus.IN_PROGRESS));
		assertThat(performedProcedureStepStatus, is("IN PROGRESS"));
	}
	
	/**
	 * @see {@link DicomUtils#getPerformedProcedureStepStatus(DicomObject)}
	 * @verifies return null given given dicom object without performed procedure step status
	 */
	@Test
	public void getPerformedProcedureStepStatus_shouldReturnNullGivenDicomObjectWithoutPerformedProcedureStepStatus() {
		
		RadiologyStudy radiologyStudy = radiologyStudyService.getStudyByStudyId(STUDY_ID_OF_EXISTING_STUDY_WITH_ORDER);
		Order radiologyOrder = radiologyStudy.getRadiologyOrder();
		DicomObject dicomMpps = getDicomNCreate(radiologyStudy, radiologyOrder);
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
	RadiologyStudy getMockStudy() throws Exception {
		RadiologyStudy mockStudy = new RadiologyStudy();
		mockStudy.setStudyId(1);
		mockStudy.setRadiologyOrder(getMockRadiologyOrder());
		mockStudy.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1");
		mockStudy.setModality(Modality.CT);
		
		return mockStudy;
	}
}
