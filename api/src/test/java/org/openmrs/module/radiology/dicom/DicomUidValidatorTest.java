/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.dicom;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * Tests {@see DicomUidValidator}.
 */
public class DicomUidValidatorTest {
    
    
    /**
     * @see DicomUidValidator#isValid(String)
     */
    @Test
    public void isValid_shouldReturnFalseGivenNull() throws Exception {
        
        assertFalse(DicomUidValidator.isValid(null));
    }
    
    /**
     * @see DicomUidValidator#isValid(String)
     */
    @Test
    public void isValid_shouldReturnFalseGivenEmptyString() throws Exception {
        
        assertFalse(DicomUidValidator.isValid("  "));
    }
    
    /**
     * @see DicomUidValidator#isValid(String)
     */
    @Test
    public void isValid_shouldReturnFalseForUidLongerThan64Characters() throws Exception {
        
        String uid = StringUtils.repeat("1.2", 22);
        assertThat(uid.length(), is(greaterThan(64)));
        
        assertFalse(DicomUidValidator.isValid(uid));
    }
    
    /**
     * @see DicomUidValidator#isValid(String)
     */
    @Test
    public void isValid_shouldReturnFalseForUidContainingCharactersOtherThan09AndDotSeparator() throws Exception {
        
        String uid = "1.2.04.12.10.200.A";
        assertFalse(DicomUidValidator.isValid(uid));
        
        uid = "1.2.04.12.10-200-9";
        assertFalse(DicomUidValidator.isValid(uid));
    }
    
    /**
     * @see DicomUidValidator#isValid(String)
     */
    @Test
    public void isValid_shouldReturnFalseForUidContainingNonsignificantLeadingZeros() throws Exception {
        
        String uid = "1.2.04.12.10.200";
        assertFalse(DicomUidValidator.isValid(uid));
    }
    
    /**
     * @see DicomUidValidator#isValid(String)
     */
    @Test
    public void isValid_shouldReturnFalseForUidWithTrailingDotCharacter() throws Exception {
        
        String uid = "1.2.04.12.10.200.";
        assertFalse(DicomUidValidator.isValid(uid));
    }
    
    /**
     * @see DicomUidValidator#isValid(String)
     */
    @Test
    public void isValid_shouldReturnFalseForUidWithCharactersOtherThan02AsFirstComponent() throws Exception {
        
        String uid = "3.2.04.12.10.200";
        assertFalse(DicomUidValidator.isValid(uid));
        
        uid = "9.2.04.12.10.200";
        assertFalse(DicomUidValidator.isValid(uid));
    }
    
    /**
     * @see DicomUidValidator#isValid(String)
     */
    @Test
    public void isValid_shouldReturnTrueForValidUid() throws Exception {
        
        List<String> validUids = new ArrayList<String>();
        validUids.add("1.2.840.10008.1.2");
        validUids.add("1.2.840.10008.1.2.1");
        validUids.add("1.2.840.10008.1.2.1.99");
        validUids.add("1.2.840.10008.1.1");
        validUids.add("1.2.840.10008.1.20.1");
        validUids.add("1.2.840.10008.1.3.10");
        validUids.add("1.2.840.10008.3.1.2.3.3");
        validUids.add("1.2.840.10008.3.1.2.3.4");
        
        for (String uid : validUids) {
            assertTrue(DicomUidValidator.isValid(uid));
        }
    }
    
    /**
     * @see DicomUidValidator#isLengthValid(String)
     */
    @Test
    public void isLengthValid_shouldReturnFalseGivenNull() throws Exception {
        
        assertFalse(DicomUidValidator.isLengthValid(null));
    }
    
    /**
     * @see DicomUidValidator#isLengthValid(String)
     */
    @Test
    public void isLengthValid_shouldReturnFalseGivenEmptyString() throws Exception {
        
        assertFalse(DicomUidValidator.isLengthValid("  "));
    }
    
    /**
     * @see DicomUidValidator#isLengthValid(String)
     */
    @Test
    public void isLengthValid_shouldReturnFalseForUidLengthBiggerThan64() throws Exception {
        
        String uid = StringUtils.repeat("1.2", 22);
        assertThat(uid.length(), is(greaterThan(64)));
        
        assertFalse(DicomUidValidator.isLengthValid(uid));
    }
    
    /**
     * @see DicomUidValidator#isLengthValid(String)
     */
    @Test
    public void isLengthValid_shouldReturnTrueForUidLengthSmallerOrEqualTo64() throws Exception {
        
        String uid = StringUtils.repeat("1.2", 20);
        assertThat(uid.length(), is(not(greaterThan(64))));
        
        assertTrue(DicomUidValidator.isLengthValid(uid));
        
        uid = StringUtils.repeat("1.2", 20) + "8317";
        assertThat(uid.length(), is(64));
        
        assertTrue(DicomUidValidator.isLengthValid(uid));
    }
    
    /**
     * @see DicomUidValidator#isPatternValid(String)
     */
    @Test
    public void isPatternValid_shouldReturnFalseGivenNull() throws Exception {
        
        assertFalse(DicomUidValidator.isPatternValid(null));
    }
    
    /**
     * @see DicomUidValidator#isPatternValid(String)
     */
    @Test
    public void isPatternValid_shouldReturnFalseGivenEmptyString() throws Exception {
        
        assertFalse(DicomUidValidator.isPatternValid("  "));
    }
    
    /**
     * @see DicomUidValidator#isPatternValid(String)
     */
    @Test
    public void isPatternValid_shouldReturnFalseForUidContainingCharactersOtherThan09AndDotSeparator() throws Exception {
        
        String uid = "1.2.04.12.10.200.A";
        assertFalse(DicomUidValidator.isPatternValid(uid));
        
        uid = "1.2.04.12.10-200-9";
        assertFalse(DicomUidValidator.isValid(uid));
        
    }
    
    /**
     * @see DicomUidValidator#isPatternValid(String)
     */
    @Test
    public void isPatternValid_shouldReturnFalseForUidContainingNonsignificantLeadingZeros() throws Exception {
        
        String uid = "1.2.04.12.10.200";
        assertFalse(DicomUidValidator.isPatternValid(uid));
    }
    
    /**
     * @see DicomUidValidator#isPatternValid(String)
     */
    @Test
    public void isPatternValid_shouldReturnFalseForUidWithTrailingDotCharacter() throws Exception {
        
        String uid = "1.2.04.12.10.200.";
        assertFalse(DicomUidValidator.isValid(uid));
    }
    
    /**
     * @see DicomUidValidator#isPatternValid(String)
     */
    @Test
    public void isPatternValid_shouldReturnFalseForUidWithCharactersOtherThan02AsFirstComponent() throws Exception {
        
        String uid = "3.2.04.12.10.200";
        assertFalse(DicomUidValidator.isValid(uid));
        
        uid = "9.2.04.12.10.200";
        assertFalse(DicomUidValidator.isValid(uid));
    }
    
    /**
     * @see DicomUidValidator#isPatternValid(String)
     */
    @Test
    public void isPatternValid_shouldReturnTrueForValidUid() throws Exception {
        
        List<String> validUids = new ArrayList<String>();
        validUids.add("1.2.840.10008.1.2");
        validUids.add("1.2.840.10008.1.2.1");
        validUids.add("1.2.840.10008.1.2.1.99");
        validUids.add("1.2.840.10008.1.1");
        validUids.add("1.2.840.10008.1.20.1");
        validUids.add("1.2.840.10008.1.3.10");
        validUids.add("1.2.840.10008.3.1.2.3.3");
        validUids.add("1.2.840.10008.3.1.2.3.4");
        
        for (String uid : validUids) {
            assertTrue(DicomUidValidator.isValid(uid));
        }
    }
}
