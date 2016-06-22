/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
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
