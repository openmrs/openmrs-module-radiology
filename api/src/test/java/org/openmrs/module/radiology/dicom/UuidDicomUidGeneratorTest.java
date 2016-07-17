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
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests {@see UuidDicomUidGenerator}.
 */
public class UuidDicomUidGeneratorTest {
    
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    UuidDicomUidGenerator dicomUidGenerator = new UuidDicomUidGenerator();
    
    /**
     * @see DicomUidGenerator#getMaxRootLength()
     * @verifies return maximum allowed root length
     */
    @Test
    public void getMaxRootLength_shouldReturnMaximumAllowedRootLength() throws Exception {
        
        assertThat(dicomUidGenerator.getMaxRootLength(), is(24));
    }
    
    /**
     * @see DicomUidGenerator#getNewDicomUid(String)
     * @verifies return a valid uid prefixed with root
     */
    @Test
    public void getNewDicomUid_shouldReturnAValidUidPrefixedWithRoot() throws Exception {
        
        String root = "1.2.5.6.7.1220";
        
        String uid = dicomUidGenerator.getNewDicomUid(root);
        assertThat(uid, startsWith(root));
    }
    
    /**
     * @see DicomUidGenerator#getNewDicomUid(String)
     * @verifies always return unique uids when called multiple times
     */
    @Test
    public void getNewDicomUid_shouldAlwaysReturnUniqueUidsWhenCalledMultipleTimes() throws Exception {
        
        String root = "1.2.5.6.7.1220";
        
        int N = 1000;
        final Set<String> uniqueStudyInstanceUids = new HashSet<String>(N);
        for (int i = 0; i < N; i++) {
            uniqueStudyInstanceUids.add(dicomUidGenerator.getNewDicomUid(root));
        }
        
        // since we used a set we should have the size as N indicating that there were no duplicates
        assertThat(uniqueStudyInstanceUids.size(), is(N));
    }
    
    /**
     * @see DicomUidGenerator#getNewDicomUid(String)
     * @verifies return a uid not exceeding 64 characters
     */
    @Test
    public void getNewDicomUid_shouldReturnAUidNotExceeding64Characters() throws Exception {
        
        String uid = dicomUidGenerator.getNewDicomUid("1.2.5.6.7.1220");
        assertThat(uid.length(), is(not(greaterThan(64))));
    }
    
    /**
     * @see DicomUidGenerator#getNewDicomUid(String)
     * @verifies return a uid composed only of characters 0-9 separated by a dot
     */
    @Test
    public void getNewDicomUid_shouldReturnAUidComposedOnlyOfCharacters09SeparatedByADot() throws Exception {
        
        String uid = dicomUidGenerator.getNewDicomUid("1.2.5.6.7.1220");
        assertTrue(DicomUidValidator.isPatternValid(uid));
    }
    
    /**
     * @see DicomUidGenerator#getNewDicomUid(String)
     * @verifies return a uid with no non-significant leading zeros
     */
    @Test
    public void getNewDicomUid_shouldReturnAUidWithNoNonsignificantLeadingZeros() throws Exception {
        
        String uid = dicomUidGenerator.getNewDicomUid("1.2.5.6.7.1220");
        assertTrue(DicomUidValidator.isPatternValid(uid));
    }
    
    /**
     * @see DicomUidGenerator#getNewDicomUid(String)
     * @verifies throw a null pointer exception if root is null
     */
    @Test
    public void getNewDicomUid_shouldThrowANullPointerExceptionIfRootIsNull() throws Exception {
        
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("root is required");
        dicomUidGenerator.getNewDicomUid(null);
    }
    
    /**
     * @see DicomUidGenerator#getNewDicomUid(String)
     * @verifies throw an illegal argument exception if root is empty
     */
    @Test
    public void getNewDicomUid_shouldThrowAnIllegalArgumentExceptionIfRootIsEmpty() throws Exception {
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("root is an invalid DICOM UID");
        dicomUidGenerator.getNewDicomUid("  ");
    }
    
    /**
     * @see DicomUidGenerator#getNewDicomUid(String)
     * @verifies throw an illegal argument exception if root is not a valid UID
     */
    @Test
    public void getNewDicomUid_shouldThrowAnIllegalArgumentExceptionIfRootIsNotAValidUID() throws Exception {
        
        // test with invalid root pattern but smaller than 64 characters
        String root = "1.2.A.1";
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("root is an invalid DICOM UID");
        dicomUidGenerator.getNewDicomUid(root);
        
        // test with valid root pattern but exceeding 64 characters
        root = StringUtils.repeat("1.2", 22);
        
        assertThat(root.length(), is(greaterThan(64)));
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("root is an invalid DICOM UID");
        dicomUidGenerator.getNewDicomUid(root);
    }
    
    /**
     * @see DicomUidGenerator#getNewDicomUid(String)
     * @verifies throw an illegal argument exception if root exceeds the maximum length
     */
    @Test
    public void getNewDicomUid_shouldThrowAnIllegalArgumentExceptionIfRootExceedsTheMaximumLength() throws Exception {
        
        String root = StringUtils.repeat("1.2", 10);
        
        // ensure root itself is a valid DICOM UID but exceeds max length
        assertThat(root.length(), is(greaterThan(dicomUidGenerator.getMaxRootLength())));
        assertTrue(DicomUidValidator.isPatternValid(root));
        
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("root length is > " + dicomUidGenerator.getMaxRootLength());
        dicomUidGenerator.getNewDicomUid(root);
    }
}
