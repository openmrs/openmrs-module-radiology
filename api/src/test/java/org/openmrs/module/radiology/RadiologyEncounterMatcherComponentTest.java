package org.openmrs.module.radiology;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests {@link RadiologyEncounterMatcher}
 */
public class RadiologyEncounterMatcherComponentTest extends BaseModuleContextSensitiveTest {
    
    private static final String TEST_DATASET =
            "org/openmrs/module/radiology/include/RadiologyEncounterMatcherComponentTestDataset.xml";
    
    private static final int VISIT_ID_WITH_NON_VOIDED_ENCOUNTER = 3003;
    
    private static final int VISIT_ID_WITHOUT_ENCOUNTER = 3001;
    
    private static final int VISIT_ID_WITH_VOIDED_ENCOUNTER = 3002;
    
    private static final String NON_VOIDED_ENCOUNTER_UUID = "7f2dad34-f5a5-11e5-b84b-08002719a237";
    
    private static final String VOIDED_ENCOUNTER_UUID = "60db560a-f5a3-11e5-b84b-08002719a237";
    
    @Autowired
    private RadiologyEncounterMatcher radiologyEncounterMatcher;
    
    @Autowired
    private VisitService visitService;
    
    @Autowired
    private RadiologyProperties radiologyProperties;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(TEST_DATASET);
    }
    
    /**
     * @see RadiologyEncounterMatcher#findEncounter(Visit,EncounterParameters)
     * @verifies return encounter if encounter uuid given by encounter parameters is attached to given visit and is not
     *           voided
     */
    @Test
    public
            void
            findEncounter_shouldReturnEncounterIfEncounterUuidGivenByEncounterParametersIsAttachedToGivenVisitAndIsNotVoided()
                    throws Exception {
        // given
        EncounterParameters encounterParameters = EncounterParameters.instance()
                .setEncounterType(radiologyProperties.getRadiologyOrderEncounterType())
                .setEncounterUuid(NON_VOIDED_ENCOUNTER_UUID);
        Visit visit = visitService.getVisit(VISIT_ID_WITH_NON_VOIDED_ENCOUNTER);
        
        Encounter encounter = radiologyEncounterMatcher.findEncounter(visit, encounterParameters);
        
        assertThat(encounter, is(notNullValue()));
        assertThat(encounter.getUuid(), is(NON_VOIDED_ENCOUNTER_UUID));
    }
    
    /**
     * @see RadiologyEncounterMatcher#findEncounter(Visit,EncounterParameters)
     * @verifies return null given visit without non voided encounters
     */
    @Test
    public void findEncounter_shouldReturnNullGivenVisitWithoutNonVoidedEncounters() throws Exception {
        // given
        EncounterParameters encounterParameters = EncounterParameters.instance()
                .setEncounterType(radiologyProperties.getRadiologyOrderEncounterType());
        Visit visit = visitService.getVisit(VISIT_ID_WITHOUT_ENCOUNTER);
        
        Encounter encounter = radiologyEncounterMatcher.findEncounter(visit, encounterParameters);
        
        assertThat(encounter, is(nullValue()));
    }
    
    /**
     * @see RadiologyEncounterMatcher#findEncounter(Visit,EncounterParameters)
     * @verifies return null if encounter uuid given by encounter parameters is voided
     */
    @Test
    public void findEncounter_shouldReturnNullIfEncounterUuidGivenByEncounterParametersIsVoided() throws Exception {
        // given
        EncounterParameters encounterParameters = EncounterParameters.instance()
                .setEncounterType(radiologyProperties.getRadiologyOrderEncounterType())
                .setEncounterUuid(VOIDED_ENCOUNTER_UUID);
        Visit visit = visitService.getVisit(VISIT_ID_WITH_VOIDED_ENCOUNTER);
        
        Encounter encounter = radiologyEncounterMatcher.findEncounter(visit, encounterParameters);
        
        assertThat(encounter, is(nullValue()));
    }
    
    /**
     * @see RadiologyEncounterMatcher#findEncounter(Visit,EncounterParameters)
     * @verifies throw illegal argument exception if given visit is null
     */
    @Test
    public void findEncounter_shouldThrowIllegalArgumentExceptionIfGivenVisitIsNull() throws Exception {
        // given
        EncounterParameters encounterParameters = EncounterParameters.instance()
                .setEncounterType(radiologyProperties.getRadiologyOrderEncounterType())
                .setEncounterUuid(VOIDED_ENCOUNTER_UUID);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("visit is required");
        radiologyEncounterMatcher.findEncounter(null, encounterParameters);
    }
    
    /**
     * @see RadiologyEncounterMatcher#findEncounter(Visit,EncounterParameters)
     * @verifies throw illegal argument exception if given encounterParameters is null
     */
    @Test
    public void findEncounter_shouldThrowIllegalArgumentExceptionIfGivenEncounterParametersIsNull() throws Exception {
        // given
        Visit visit = visitService.getVisit(VISIT_ID_WITH_VOIDED_ENCOUNTER);
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("encounterParameters are required");
        radiologyEncounterMatcher.findEncounter(visit, null);
    }
}
