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

/**
 * Implemented by classes that auto generate UIDs according to the DICOM Standard DICOM PS3.5 Chapter 9 Unique Identifiers
 * (UIDs).
 * See 9.1 UID Encoding Rules.
 * http://dicom.nema.org/MEDICAL/Dicom/current/output/chtml/part05/chapter_9.html
 */
public interface DicomUidGenerator {
    
    
    /**
     * Returns the maximum allowed {@code root} length.
     * Allows users of the {@code DicomUidGenerator} to validate their {@code root's} length before using this generator.
     * 
     * @return maximum allowed root length
     * @should return maximum allowed root length
     */
    public int getMaxRootLength();
    
    /**
     * Generates a new DICOM UID prefixed by {@code root} the UID uniquely identifying an organization.
     * Note that this method is invoked in a non thread-safe way, therefore implementations need to be thread safe.
     * 
     * @param root Org root UID uniquely identifying an organization
     * @return the new dicom uid prefixed with root
     * @throws NullPointerException if root is null
     * @throws IllegalArgumentException if root is empty
     * @throws IllegalArgumentException if the root is not a valid UID
     * @throws IllegalArgumentException if the root exceeds the maximum length
     * @should return a valid uid prefixed with root
     * @should always return unique uids when called multiple times
     * @should return a uid not exceeding 64 characters
     * @should return a uid composed only of characters 0-9 separated by a dot
     * @should return a uid with no non-significant leading zeros
     * @should throw a null pointer exception if root is null
     * @should throw an illegal argument exception if root is empty
     * @should throw an illegal argument exception if root is not a valid UID
     * @should throw an illegal argument exception if root exceeds the maximum length
     */
    public String getNewDicomUid(String root);
}
