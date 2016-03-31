package org.openmrs.module.radiology.dicom;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.Study;
import org.openmrs.test.BaseContextMockTest;

/**
 * Tests {@link DicomWebViewer}
 */
public class DicomWebViewerTest extends BaseContextMockTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Mock
	private RadiologyProperties radiologyProperties;
	
	@InjectMocks
	private DicomWebViewer dicomviewer = new DicomWebViewer();
	
	@Before
	public void runBeforeAllTests() {
		when(radiologyProperties.getDicomWebViewerAddress()).thenReturn("localhost");
		when(radiologyProperties.getDicomWebViewerPort()).thenReturn("8081");
		when(radiologyProperties.getDicomWebViewerBaseUrl()).thenReturn("/weasis-pacs-connector/viewer");
	}
	
	/**
	 * @see DicomWebViewer#getDicomViewerUrl(Study)
	 * @verifies return a url to open dicom images of the given study in the configured dicom viewer
	 */
	@Test
	public void getDicomViewerUrl_shouldReturnAUrlToOpenDicomImagesOfTheGivenStudyInTheConfiguredDicomViewer() {
		Study study = getMockStudy();
		assertThat(dicomviewer.getDicomViewerUrl(study), is("http://localhost:8081/weasis-pacs-connector/viewer?studyUID="
				+ study.getStudyInstanceUid()));
	}
	
	Study getMockStudy() {
		Study mockStudy = new Study();
		mockStudy.setStudyId(1);
		mockStudy.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1");
		
		return mockStudy;
	}
	
	/**
	 * @see DicomWebViewer#getDicomViewerUrl(Study)
	 * @verifies add query param server name to url if local server name is not blank
	 */
	@Test
	public void getDicomViewerUrl_shouldAddQueryParamServerNameToUrlIfLocalServerNameIsNotBlank() {
		
		when(radiologyProperties.getDicomWebViewerBaseUrl()).thenReturn("/oviyam2/viewer.html");
		when(radiologyProperties.getDicomWebViewerLocalServerName()).thenReturn("oviyamlocal");
		
		Study study = getMockStudy();
		
		assertThat(dicomviewer.getDicomViewerUrl(study),
			is("http://localhost:8081/oviyam2/viewer.html?studyUID=" + study.getStudyInstanceUid()
					+ "&serverName=oviyamlocal"));
	}
	
	/**
	 * @see DicomWebViewer#getDicomViewerUrl(Study)
	 * @verifies throw an illegal argument exception given null
	 */
	@Test
	public void getDicomViewerUrl_shouldThrowAnIllegalArgumentExceptionGivenNull() throws Exception {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("study cannot be null"));
		dicomviewer.getDicomViewerUrl(null);
	}
	
	/**
	 * @see DicomWebViewer#getDicomViewerUrl(Study)
	 * @verifies throw an illegal argument exception given study with studyInstanceUid null
	 */
	@Test
	public void getDicomViewerUrl_shouldThrowAnIllegalArgumentExceptionGivenAStudyWithStudyInstanceUidNull()
			throws Exception {
		Study study = new Study();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("studyInstanceUid cannot be null"));
		dicomviewer.getDicomViewerUrl(study);
	}
}
