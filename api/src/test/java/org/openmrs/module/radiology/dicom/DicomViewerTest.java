package org.openmrs.module.radiology.dicom;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.Is.is;

import org.junit.Test;
import org.junit.Rule;
import org.junit.Before;
import org.mockito.Mock;
import org.junit.rules.ExpectedException;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.RadiologyProperties;
import org.mockito.InjectMocks;
import org.openmrs.test.BaseContextMockTest;

/**
 * Tests {@link DicomViewer}
 */
public class DicomViewerTest extends BaseContextMockTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Mock
	private RadiologyProperties radiologyProperties;
	
	@InjectMocks
	private DicomViewer dicomviewer = new DicomViewer();
	
	@Before
	public void runBeforeAllTests() {
		when(radiologyProperties.getDicomViewerUrl()).thenReturn("http://localhost:8081/weasis-pacs-connector/viewer?");
	}
	
	/**
	 * @see DicomViewer#getDicomViewerUrl(Study)
	 * @verifies return a url to open dicom images of the given study in the configured dicom viewer (no matter if the study is completed or not)
	 */
	@Test
	public void getDicomViewerUrl_shouldReturnAUrlToOpenDicomImagesOfTheGivenStudyInTheConfiguredDicomViewerNoMatterIfTheStudyIsCompletedOrNot() {
		Study study = getMockStudy();
		assertThat(dicomviewer.getDicomViewerUrl(study), is(notNullValue()));
	}
	
	Study getMockStudy() {
		Study mockStudy = new Study();
		mockStudy.setStudyId(1);
		mockStudy.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1");
		
		return mockStudy;
	}
	
	/**
	 * @see DicomViewer#getDicomViewerUrl(Study)
	 * @verifies throw an IllegalArgumentException given a study with studyInstanceUid null
	 */
	@Test
	public void getDicomViewerUrl_shouldThrowAnIllegalArgumentExceptionGivenAStudyWithStudyInstanceUidNull()
	        throws Exception {
		Study study = new Study();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("studyInstanceUid cannot be null"));
		dicomviewer.getDicomViewerUrl(study);
	}
	
	/**
	 * @see DicomViewer#getDicomViewerUrl(Study)
	 * @verifies throw an IllegalArgumentException given null
	 */
	@Test
	public void getDicomViewerUrl_shouldThrowAnIllegalArgumentExceptionGivenNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("study cannot be null"));
		dicomviewer.getDicomViewerUrl(null);
	}
}
