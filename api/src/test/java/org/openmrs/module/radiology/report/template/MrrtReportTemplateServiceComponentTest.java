/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyConstants;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests {@code MrrtReportTemplateService}.
 */
public class MrrtReportTemplateServiceComponentTest extends BaseModuleContextSensitiveTest {
    
    
    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    
    @Autowired
    private RadiologyProperties radiologyProperties;
    
    @Autowired
    private MrrtReportTemplateFileParser parser;
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/MrrtReportTemplateServiceComponentTestDataset.xml";
    
    private static final int EXISTING_TEMPLATE_ID = 1;
    
    private static final int NON_EXISTING_TEMPLATE_ID = 23;
    
    private static final String NON_EXISTING_UUID = "invalid uuid";
    
    private static final String EXISTING_TEMPLATE_TITLE = "CT";
    
    private static final String NON_EXISTING_TEMPLATE_TITLE = "invalid";
    
    private static final String EXISTING_TEMPLATE_LICENSE = "General Public License";
    
    private static final String NON_EXISTING_TEMPLATE_LICENSE = "Non existing license";
    
    private static final String EXISTING_TEMPLATE_CREATOR = "creator1";
    
    private static final String NON_EXISTING_TEMPLATE_CREATOR = "Non existing creator";
    
    private static final String TEMPLATE_IDENTIFIER = "1.3.6.1.4.1.21367.13.199.1015";
    
    private static final String NON_EXISTING_PUBLISHER = "Non existing publisher";
    
    private static final String UUID_FOR_TEMPLATE_ONE = "aa551445-def0-4f93-9047-95f0a9afbdce";
    
    private static final String UUID_FOR_TEMPLATE_TWO = "59273e52-33b1-4fcb-8c1f-9b670bb11259";
    
    @Autowired
    private MrrtReportTemplateService mrrtReportTemplateService;
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * Get a files content as string.
     *
     * @param path the path to get the file content from
     * @return the file content
     */
    private String getFileContent(String path) throws IOException {
        
        File file = getFile(path);
        return getString(file);
    }
    
    /**
     * Get a file from the test resources.
     *
     * @param path the path to get the file from
     * @return the file on given path
     */
    private File getFile(String path) {
        return new File(getClass().getClassLoader()
                .getResource(path)
                .getFile());
    }
    
    /**
     * Get a file from the test resources.
     *
     * @param file the file to get the content from
     * @return the file content
     */
    private String getString(File file) throws IOException {
        String content = null;
        try (InputStream in = new FileInputStream(file)) {
            content = IOUtils.toString(in);
        }
        return content;
    }
    
    /**
     * Sets up the global property defining the MRRT template directory using junits temporary folder.
     *
     * @throws IOException
     */
    private void setUpTemporaryFolder() throws IOException {
        
        File tempFolder = temporaryFolder.newFolder("/mrrt_templates");
        administrationService.setGlobalProperty(RadiologyConstants.GP_MRRT_REPORT_TEMPLATE_DIR,
            tempFolder.getAbsolutePath());
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplate(Integer)
    * @verifies get template with given id
    */
    @Test
    public void getMrrtReportTemplate_shouldGetTemplateWithGivenId() throws Exception {
        
        MrrtReportTemplate template = mrrtReportTemplateService.getMrrtReportTemplate(EXISTING_TEMPLATE_ID);
        
        assertNotNull(template);
        assertThat(template.getId(), is(EXISTING_TEMPLATE_ID));
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplate(Integer)
    * @verifies return null if no match was found
    */
    @Test
    public void getMrrtReportTemplate_shouldReturnNullIfNoMatchWasFound() throws Exception {
        assertNull(mrrtReportTemplateService.getMrrtReportTemplate(NON_EXISTING_TEMPLATE_ID));
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplate(Integer)
    * @verifies throw illegal argument exception if given null
    */
    @Test
    public void getMrrtReportTemplate_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("id cannot be null");
        mrrtReportTemplateService.getMrrtReportTemplate(null);
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateByUuid(String)
    * @verifies find object given existing uuid
    */
    @Test
    public void getMrrtReportTemplateByUuid_shouldFindObjectGivenExistingUuid() {
        MrrtReportTemplate valid = mrrtReportTemplateService.getMrrtReportTemplateByUuid(UUID_FOR_TEMPLATE_ONE);
        
        assertNotNull(valid);
        assertThat(valid.getTemplateId(), is(EXISTING_TEMPLATE_ID));
        assertThat(valid.getUuid(), is(UUID_FOR_TEMPLATE_ONE));
    }
    
    /**
     * @see MrrtReportTemplateService#getMrrtReportTemplateByUuid(String)
     * @verifies return null if no object found with given uuid
     */
    @Test
    public void getMrrtReportTemplateByUuid_shouldReturnNullIfNoObjectFoundWithGivenUuid() {
        assertNull(mrrtReportTemplateService.getMrrtReportTemplateByUuid(NON_EXISTING_UUID));
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateByUuid(String)
    * @verifies throw illegal argument exception if given null
    */
    @Test
    public void getMrrtReportTemplateByUuid_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("uuid cannot be null");
        mrrtReportTemplateService.getMrrtReportTemplateByUuid(null);
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateByIdentifier(String)
    * @verifies find object given valid identifier
    */
    @Test
    public void getMrrtReportTemplateByIdentifier_shouldFindObjectWithGivenIdentifier() throws Exception {
        MrrtReportTemplate template = mrrtReportTemplateService.getMrrtReportTemplateByIdentifier("identifier1");
        
        assertNotNull(template);
        assertThat(template.getDcTermsIdentifier(), is("identifier1"));
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateByIdentifier(String)
    * @verifies return null if no object found with give identifier
    */
    @Test
    public void getMrrtReportTemplateByIdentifier_shouldReturnNullIfNoObjectFoundWithGivenIdentifier() throws Exception {
        assertNull(mrrtReportTemplateService.getMrrtReportTemplateByIdentifier("invalid identifier"));
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateByIdentifier(String)
    * @verifies throw illegal argument exception if given null
    */
    @Test
    public void getMrrtReportTemplateByIdentifier_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("identifier cannot be null");
        mrrtReportTemplateService.getMrrtReportTemplateByIdentifier(null);
    }
    
    /**
    * @see MrrtReportTemplateService#purgeMrrtReportTemplate(MrrtReportTemplate)
    * @verifies delete report template from database and also delete template file from the system
    */
    @Test
    public void purgeMrrtReportTemplate_shouldDeleteReportTemplateFromDatabaseAndAlsoDeleteTemplateFileFromTheSystem()
            throws Exception {
        
        setUpTemporaryFolder();
        MrrtReportTemplate template = new MrrtReportTemplate();
        File templateFile = new File(radiologyProperties.getReportTemplateHome(), java.util.UUID.randomUUID()
                .toString());
        templateFile.createNewFile();
        template.setDcTermsTitle("sample title");
        template.setDcTermsDescription("sample description");
        template.setDcTermsIdentifier("identifier3");
        template.setPath(templateFile.getAbsolutePath());
        MrrtReportTemplate saved = mrrtReportTemplateService.saveMrrtReportTemplate(template);
        assertNotNull(saved.getId());
        File savedFile = new File(saved.getPath());
        assertThat(savedFile.exists(), is(true));
        mrrtReportTemplateService.purgeMrrtReportTemplate(saved);
        assertNull(mrrtReportTemplateService.getMrrtReportTemplate(saved.getId()));
        assertThat(savedFile.exists(), is(false));
    }
    
    /**
    * @see MrrtReportTemplateService#purgeMrrtReportTemplate(MrrtReportTemplate)
    * @verifies throw illegal argument exception if given null
    */
    @Test
    public void purgeMrrtReportTemplate_shouldThrowIlligalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("template cannot be null");
        mrrtReportTemplateService.purgeMrrtReportTemplate(null);
    }
    
    /**
     * @see MrrtReportTemplateService#purgeMrrtReportTemplate(MrrtReportTemplate)
     * @verifies catch file not found exception when the file been deleted is missing
     */
    @Test
    public void purgeMrrtReportTemplate_shouldCatchFileNotFoundExceptionWhenTheFileBeenDeletedIsMissing() {
        
        MrrtReportTemplate template = mrrtReportTemplateService.getMrrtReportTemplate(1);
        assertNotNull(template);
        assertThat(new File(template.getPath()).exists(), is(false));
        mrrtReportTemplateService.purgeMrrtReportTemplate(template);
    }
    
    /**
    * @see MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
    * @verifies save given template
    */
    @Test
    public void saveMrrtReportTemplate_shouldSaveGivenTemplate() throws Exception {
        MrrtReportTemplate template = new MrrtReportTemplate();
        
        template.setDcTermsTitle("sample title");
        template.setDcTermsDescription("sample description");
        template.setDcTermsIdentifier("identifier3");
        
        MrrtReportTemplate saved = mrrtReportTemplateService.saveMrrtReportTemplate(template);
        MrrtReportTemplate newTemplate = mrrtReportTemplateService.getMrrtReportTemplate(saved.getTemplateId());
        
        assertNotNull(saved);
        assertNotNull(newTemplate);
        assertEquals(newTemplate.getDcTermsTitle(), template.getDcTermsTitle());
        assertEquals(newTemplate.getDcTermsDescription(), template.getDcTermsDescription());
    }
    
    /**
    * @see MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
    * @verifies throw api exception if saving template that already exists
    */
    @Test
    public void saveMrrtReportTemplate_shouldThrowApiExceptionIfSavingTemplateThatAlreadyExists() throws Exception {
        MrrtReportTemplate existing = mrrtReportTemplateService.getMrrtReportTemplate(EXISTING_TEMPLATE_ID);
        existing.setDcTermsTitle("modified");
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Template already exist in the system.");
        mrrtReportTemplateService.saveMrrtReportTemplate(existing);
    }
    
    /**
    * @see MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
    * @verifies throw illegal argument exception if given null
    */
    @Test
    public void saveMrrtReportTemplate_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("template cannot be null");
        mrrtReportTemplateService.saveMrrtReportTemplate(null);
    }
    
    /**
     * @verifies create mrrt report template in the database and on the file system
     * @see MrrtReportTemplateService#importMrrtReportTemplate(String)
     */
    @Test
    public void importMrrtReportTemplate_shouldCreateMrrtReportTemplateInTheDatabaseAndOnTheFileSystem() throws Exception {
        
        setUpTemporaryFolder();
        
        String sourcePath = "mrrttemplates/ihe/connectathon/2015/CTChestAbdomen.html";
        String template = getFileContent(sourcePath);
        
        MrrtReportTemplate saved = mrrtReportTemplateService.importMrrtReportTemplate(template);
        
        assertNotNull(saved);
        assertThat(saved.getDcTermsIdentifier(), is(TEMPLATE_IDENTIFIER));
        
        File templateHome = radiologyProperties.getReportTemplateHome();
        File templatePath = new File(saved.getPath());
        assertThat(templatePath.getParentFile()
                .getName(),
            is(templateHome.getName()));
        assertTrue(FileUtils.contentEquals(getFile(sourcePath), templatePath.getAbsoluteFile()));
    }
    
    /**
     * @verifies not create an mrrt report template in the database and store the template as file if given template is invalid
     * @see MrrtReportTemplateService#importMrrtReportTemplate(String)
     */
    @Test
    public void
            importMrrtReportTemplate_shouldNotCreateAnMrrtReportTemplateInTheDatabaseAndStoreTheTemplateAsFileIfGivenTemplateIsInvalid()
                    throws Exception {
        
        setUpTemporaryFolder();
        
        String template = getFileContent(
            "mrrttemplates/ihe/connectathon/2015/invalidMrrtReportTemplate-noMetaElementWithCharsetAttribute.html");
        
        expectedException.expect(APIException.class);
        mrrtReportTemplateService.importMrrtReportTemplate(template);
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
    * @verifies return all mrrt report templates that match given title search query if title is specified
    */
    @Test
    public void getMrrtReportTemplates_shouldReturnAllMrrtReportTemplatesThatMatchGivenTitleSearchQueryIfTitleIsSpecified()
            throws Exception {
        
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withTitle(EXISTING_TEMPLATE_TITLE)
                        .build();
        List<MrrtReportTemplate> templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        assertNotNull(templates);
        assertThat(templates.size(), is(2));
        assertThat(templates.get(0)
                .getDcTermsTitle(),
            is("CT Cardiac Bypass Graft"));
        assertThat(templates.get(1)
                .getDcTermsTitle(),
            is("CT Chest Pulmonary Embolism"));
        
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
    * @verifies return an empty list of no match for title was found
    */
    @Test
    public void getMrrtRepdortTemplates_shouldReturnAnEmptyListOfNoMatchForTitleWasFound() throws Exception {
        
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withTitle(NON_EXISTING_TEMPLATE_TITLE)
                        .build();
        List<MrrtReportTemplate> templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        assertNotNull(templates);
        assertTrue(templates.isEmpty());
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
    * @verifies throw illegal argument exception of given null
    */
    @Test
    public void getMrrtReportTemplates_shouldThrowIllegalArgumentExceptionOfGivenNull() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("mrrtReportTemplateSearchCriteria cannot be null");
        mrrtReportTemplateService.getMrrtReportTemplates(null);
    }
    
    /**
     * @see MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
     * @verifies return all mrrt report templates that match given publisher anywhere in dcterms publisher insensitive to case
     */
    @Test
    public void
            getMrrtReportTemplates_shouldReturnAllMrrtReportTemplatesThatMatchGivenPublisherAnywhereInDctermsPublisherInsensitiveToCase()
                    throws Exception {
        
        String partialPublisherString = "cat";
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withPublisher(partialPublisherString)
                        .build();
        
        List<MrrtReportTemplate> templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        
        assertNotNull(templates);
        assertThat(templates.size(), is(2));
        for (MrrtReportTemplate template : templates) {
            assertThat(template.getDcTermsPublisher()
                    .toLowerCase(),
                containsString(partialPublisherString));
        }
        
        String exactPublisherString = "IHE CAT Publisher";
        searchCriteria = new MrrtReportTemplateSearchCriteria.Builder().withPublisher(exactPublisherString)
                .build();
        templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        
        assertNotNull(templates);
        assertThat(templates.size(), is(1));
        for (MrrtReportTemplate template : templates) {
            assertThat(template.getDcTermsPublisher(), is(exactPublisherString));
        }
    }
    
    /**
     * @see MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
     * @verifies return an empty list if no match for publisher was found
     */
    @Test
    public void getMrrtRepdortTemplates_shouldReturnAnEmptyListIfNoMatchForPublisherWasFound() throws Exception {
        
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withPublisher(NON_EXISTING_PUBLISHER)
                        .build();
        List<MrrtReportTemplate> templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        assertNotNull(templates);
        assertTrue(templates.isEmpty());
    }
    
    /**
     * @see MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
     * @verifies return all mrrt report templates that match given license anywhere in dcterms license insensitive to case
     */
    @Test
    public void
            getMrrtReportTemplates_shouldReturnAllMrrtReportTemplatesThatMatchGivenLicenseAnywhereInDctermsLicenseInsensitiveToCase()
                    throws Exception {
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withLicense(EXISTING_TEMPLATE_LICENSE)
                        .build();
        List<MrrtReportTemplate> templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        
        assertNotNull(templates);
        assertThat(templates.size(), is(1));
        assertThat(templates.get(0)
                .getDcTermsLicense(),
            is(EXISTING_TEMPLATE_LICENSE));
        
        searchCriteria = new MrrtReportTemplateSearchCriteria.Builder().withLicense("public")
                .build();
        templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        List<String> licenses = new ArrayList<>();
        templates.forEach(template -> licenses.add(template.getDcTermsLicense()));
        
        assertNotNull(licenses);
        assertThat(licenses.size(), is(2));
        assertThat(licenses, hasItem("Mozilla Public License"));
        assertThat(licenses, hasItem("General Public License"));
    }
    
    /**
     * @see MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
     * @verifies return an empty list if no match for license was found
     */
    @Test
    public void getMrrtReportTemplates_shouldReturnAnEmptyListIfNoMatchForLicenseWasFound() throws Exception {
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withLicense(NON_EXISTING_TEMPLATE_LICENSE)
                        .build();
        List<MrrtReportTemplate> templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        
        assertNotNull(templates);
        assertTrue(templates.isEmpty());
    }
    
    /**
     * @see MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
     * @verifies return all mrrt report templates that match given creator anywhere in dcterms creator insensitive to case
     */
    @Test
    public void
            getMrrtReportTemplates_shouldReturnAllMrrtReportTemplatesThatMatchGivenCreatorAnywhereInDctermsCreatorInsensitiveToCase()
                    throws Exception {
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withCreator(EXISTING_TEMPLATE_CREATOR)
                        .build();
        List<MrrtReportTemplate> templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        
        assertNotNull(templates);
        assertThat(templates.size(), is(1));
        assertThat(templates.get(0)
                .getDcTermsCreator(),
            is(EXISTING_TEMPLATE_CREATOR));
        
        searchCriteria = new MrrtReportTemplateSearchCriteria.Builder().withCreator("CREATOR")
                .build();
        templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        List<String> creators = new ArrayList<>();
        templates.forEach(template -> creators.add(template.getDcTermsCreator()));
        
        assertNotNull(creators);
        assertThat(creators.size(), is(2));
        assertThat(creators, hasItem("creator1"));
        assertThat(creators, hasItem("creator2"));
    }
    
    /**
     * @see MrrtReportTemplateService#getMrrtReportTemplates(MrrtReportTemplateSearchCriteria)
     * @verifies return an empty list if no match for creator was found
     */
    @Test
    public void getMrrtReportTemplates_shouldReturnAnEmptyListIfNoMatchForCreatorWasFound() throws Exception {
        MrrtReportTemplateSearchCriteria searchCriteria =
                new MrrtReportTemplateSearchCriteria.Builder().withCreator(NON_EXISTING_TEMPLATE_CREATOR)
                        .build();
        List<MrrtReportTemplate> templates = mrrtReportTemplateService.getMrrtReportTemplates(searchCriteria);
        
        assertNotNull(templates);
        assertTrue(templates.isEmpty());
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateHtmlBody(MrrtReportTemplate)
    * @verifies return the body content of the mrrt report template file
    */
    @Test
    public void getMrrtReportTemplateHtmlBody_shouldReturnTheBodyContentOfTheMrrtReportTemplateFile() throws Exception {
        File tmpTemplateFile = temporaryFolder.newFile();
        FileUtils.writeStringToFile(tmpTemplateFile,
            "<html>" + "<head><title>Sample Template</title></head>" + "<body><p>Sample Template</p></body>" + "</html>");
        MrrtReportTemplate mockTemplate = mock(MrrtReportTemplate.class);
        when(mockTemplate.getPath()).thenReturn(tmpTemplateFile.getAbsolutePath());
        String bodyContentReturned = mrrtReportTemplateService.getMrrtReportTemplateHtmlBody(mockTemplate);
        assertNotNull(bodyContentReturned);
        assertThat(bodyContentReturned, is("<p>Sample Template</p>"));
    }
    
    /**
    * @see MrrtReportTemplateService#getMrrtReportTemplateHtmlBody(MrrtReportTemplate)
    * @verifies throw illegal argument exception if given null
    */
    @Test
    public void getMrrtReportTemplateHtmlBody_shouldThrowIllegalArgumentExceptionIfGivenNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("mrrtReportTemplate cannot be null");
        mrrtReportTemplateService.getMrrtReportTemplateHtmlBody(null);
    }
    
    /**
     * @see MrrtReportTemplateService#saveMrrtReportTemplate(MrrtReportTemplate)
     * @verifies save template object with terms if matching concept reference term was found
     */
    @Test
    public void saveMrrtReportTemplate_shouldSaveTemplateObjectWithTermsIfMatchingConceptReferenceTermWasFound()
            throws Exception {
        
        String templateString = getFileContent("mrrttemplates/ihe/connectathon/2015/CTChestAbdomen.html");
        
        MrrtReportTemplate template = parser.parse(templateString);
        
        MrrtReportTemplate saved = mrrtReportTemplateService.saveMrrtReportTemplate(template);
        assertNotNull(saved);
        assertThat(saved.getTerms()
                .size(),
            is(1));
    }
}
