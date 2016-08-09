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

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Validates DICOM UIDs according to DICOM PS3.5 Chapter 9 Unique Identifiers (UIDs).
 * <p>
 * A UID is considered a valid DICOM UID if it
 * <ul>
 * <li>does not only contain whitespaces
 * <li>contains 64 characters or less
 * <li>does only contain characters 0-9 separated by '.'
 * <li>does only contain significant leading zeros
 * <li>does not contain a trailing dot character
 * <li>does only contain 0, 1 or 2 as first component
 * </ul>
 * <p>
 * See DICOM PS3.5 Chapter 9.1 UID Encoding Rules.
 * http://dicom.nema.org/MEDICAL/Dicom/current/output/chtml/part05/chapter_9.html
 */
public class DicomUidValidator {
    
    
    private static final int MAX_LENGTH = 64;
    
    private static final Pattern VALIDATION_PATTERN = Pattern.compile("^[012]((\\.0)|(\\.[1-9]\\d*))+$");
    
    /**
     * Validate {@code uid} according to the DICOM standard.
     * 
     * @param uid DICOM UID to be validated
     * @return true if uid is a valid dicom uid and false otherwise
     * @should return false given null
     * @should return false given empty string
     * @should return false for uid longer than 64 characters
     * @should return false for uid containing characters other than 0-9 and dot separator
     * @should return false for uid containing non-significant leading zeros
     * @should return false for uid with trailing dot character
     * @should return false for uid with characters other than 0-2 as first component
     * @should return true for valid uid
     */
    public static boolean isValid(String uid) {
        
        return isLengthValid(uid) && isPatternValid(uid);
    }
    
    /**
     * Validate {@code uid's} length according to the DICOM standard.
     * 
     * @param uid DICOM UID to be validated
     * @return true if uid length is smaller or equal than 64 and false otherwise
     * @should return false given null
     * @should return false given empty string
     * @should return false for uid length bigger than 64
     * @should return true for uid length smaller or equal to 64
     */
    public static boolean isLengthValid(String uid) {
        
        if (StringUtils.isBlank(uid)) {
            return false;
        }
        
        return uid.length() <= MAX_LENGTH;
    }
    
    /**
     * Validate {@code uid's} pattern according to the DICOM standard.
     * 
     * @param uid DICOM UID to be validated
     * @return true if uid pattern is valid and false otherwise
     * @should return false given null
     * @should return false given empty string
     * @should return false for uid containing characters other than 0-9 and dot separator
     * @should return false for uid containing non-significant leading zeros
     * @should return false for uid with trailing dot character
     * @should return false for uid with characters other than 0-2 as first component
     * @should return true for valid uid
     */
    public static boolean isPatternValid(String uid) {
        
        if (StringUtils.isBlank(uid)) {
            return false;
        }
        
        return VALIDATION_PATTERN.matcher(uid)
                .matches();
    }
}
