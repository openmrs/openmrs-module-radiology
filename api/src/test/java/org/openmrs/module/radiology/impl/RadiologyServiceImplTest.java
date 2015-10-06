/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openmrs.Encounter;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.radiology.DicomUtils;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.RadiologyConstants;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.ScheduledProcedureStepStatus;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.db.StudyDAO;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Tests {@link RadiologyServiceImpl}
 */
public class RadiologyServiceImplTest extends BaseModuleContextSensitiveTest {
	
	private static final String STUDIES_TEST_DATASET = "org/openmrs/module/radiology/include/RadiologyServiceTestDataSet.xml";
	
	private static final int PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER = 70011;
	
	private static final int RADIOLOGY_ORDER_ID_WITHOUT_STUDY = 2004;
	
	private static final int EXISTING_STUDY_ID = 1;
	
	private static final String MWL_DIRECTORY = "mwl";
	
	@Autowired
	private RadiologyProperties radiologyProperties;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService administrationService;
	
	@Autowired
	private ProviderService providerService;
	
	@Autowired
	private RadiologyService radiologyService;
	
	@Autowired
	private StudyDAO studyDAO;
	
	@Autowired
	private DicomUtils dicomUtils;
	
	@Autowired
	private EncounterService encounterService;
	
	private RadiologyServiceImpl radiologyServiceImpl = null;
	
	private Method saveRadiologyOrderEncounterMethod = null;
	
	private Method saveStudyMethod = null;
	
	@Rule
	public TemporaryFolder temporaryBaseFolder = new TemporaryFolder();
	
	@Before
	public void runBeforeAllTests() throws Exception {
		
		if (radiologyServiceImpl == null) {
			radiologyServiceImpl = new RadiologyServiceImpl();
		}
		
		ReflectionTestUtils.setField(radiologyServiceImpl, "studyDAO", studyDAO);
		ReflectionTestUtils.setField(radiologyServiceImpl, "radiologyProperties", radiologyProperties);
		ReflectionTestUtils.setField(radiologyServiceImpl, "dicomUtils", dicomUtils);
		ReflectionTestUtils.setField(radiologyServiceImpl, "encounterService", encounterService);
		
		saveRadiologyOrderEncounterMethod = RadiologyServiceImpl.class.getDeclaredMethod("saveRadiologyOrderEncounter",
		    new Class[] { org.openmrs.Patient.class, org.openmrs.Provider.class, java.util.Date.class });
		saveRadiologyOrderEncounterMethod.setAccessible(true);
		
		saveStudyMethod = RadiologyServiceImpl.class.getDeclaredMethod("saveStudy",
		    new Class[] { org.openmrs.module.radiology.Study.class });
		saveStudyMethod.setAccessible(true);
		
		executeDataSet(STUDIES_TEST_DATASET);
	}
	
	/**
	 * @see RadiologyServiceImpl#saveRadiologyOrderEncounter(Patient,Provider,Date)
	 * @verifies save radiology order encounter for given parameters
	 */
	@Test
	public void saveRadiologyOrderEncounter_shouldSaveRadiologyOrderEncounterForGivenParameters() throws Exception {
		
		Patient patient = patientService.getPatient(PATIENT_ID_WITH_ONLY_ONE_NON_RADIOLOGY_ORDER);
		Provider provider = providerService.getProviderByIdentifier("1");
		Date encounterDatetime = new GregorianCalendar(2010, Calendar.OCTOBER, 10).getTime();
		
		Encounter encounter = (Encounter) saveRadiologyOrderEncounterMethod.invoke(radiologyServiceImpl, new Object[] {
		        patient, provider, encounterDatetime });
		
		assertNotNull(encounter);
		assertThat(encounter.getPatient(), is(patient));
		assertThat(encounter.getProvidersByRole(radiologyProperties.getOrderingProviderEncounterRole()).size(), is(1));
		assertThat(encounter.getProvidersByRole(radiologyProperties.getOrderingProviderEncounterRole()).contains(provider),
		    is(true));
		assertThat(encounter.getEncounterDatetime(), is(encounterDatetime));
		assertThat(encounter.getEncounterType(), is(radiologyProperties.getRadiologyEncounterType()));
	}
	
	/**
	 * @see RadiologyServiceImpl#saveStudy(Study)
	 * @verifies create new study from given study object
	 */
	@Test
	public void saveStudy_shouldCreateNewStudyFromGivenStudyObject() throws Exception {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		Study radiologyStudy = getUnsavedStudy();
		RadiologyOrder radiologyOrder = radiologyService.getRadiologyOrderByOrderId(RADIOLOGY_ORDER_ID_WITHOUT_STUDY);
		radiologyOrder.setStudy(radiologyStudy);
		
		Study createdStudy = (Study) saveStudyMethod.invoke(radiologyServiceImpl, new Object[] { radiologyStudy });
		
		assertNotNull(createdStudy);
		assertThat(createdStudy, is(radiologyStudy));
		assertThat(createdStudy.getStudyId(), is(radiologyStudy.getStudyId()));
		assertNotNull(createdStudy.getStudyInstanceUid());
		assertThat(createdStudy.getStudyInstanceUid(), is(radiologyProperties.getStudyPrefix() + createdStudy.getStudyId()));
		assertThat(createdStudy.getModality(), is(radiologyStudy.getModality()));
		assertThat(createdStudy.getRadiologyOrder(), is(radiologyStudy.getRadiologyOrder()));
	}
	
	/**
	 * Convenience method to get a Study object with all required values filled (except
	 * radiologyOrder) in but which is not yet saved in the database
	 * 
	 * @return Study object that can be saved to the database
	 */
	public Study getUnsavedStudy() {
		
		Study study = new Study();
		study.setModality(Modality.CT);
		study.setMwlStatus(MwlStatus.DEFAULT);
		study.setScheduledStatus(ScheduledProcedureStepStatus.SCHEDULED);
		return study;
	}
	
	/**
	 * @see RadiologyServiceImpl#saveStudy(Study)
	 * @verifies update existing study
	 */
	@Test
	public void saveStudy_shouldUpdateExistingStudy() throws Exception {
		
		// Set temporary mwl folder, so that the DICOM MWL xml file created on saveStudy() will be removed after test finishes.
		File temporaryMwlFolder = temporaryBaseFolder.newFolder(MWL_DIRECTORY);
		administrationService.saveGlobalProperty(new GlobalProperty(RadiologyConstants.GP_MWL_DIR, temporaryMwlFolder
		        .getAbsolutePath()));
		
		Study existingStudy = radiologyServiceImpl.getStudy(EXISTING_STUDY_ID);
		Modality modalityPreUpdate = existingStudy.getModality();
		Modality modalityPostUpdate = Modality.XA;
		existingStudy.setModality(modalityPostUpdate);
		
		Study updatedStudy = (Study) saveStudyMethod.invoke(radiologyServiceImpl, new Object[] { existingStudy });
		
		assertNotNull(updatedStudy);
		assertThat(updatedStudy, is(existingStudy));
		assertThat(modalityPreUpdate, is(not(modalityPostUpdate)));
		assertThat(updatedStudy.getModality(), is(modalityPostUpdate));
	}
}
