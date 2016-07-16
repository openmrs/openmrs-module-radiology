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

import org.openmrs.module.radiology.util.DecimalUuid;

/**
 * Generates DICOM UIDs based on decimal representations of {@link java.util.UUID#randomUUID()}.
 */
public class UuidDicomUidGenerator implements DicomUidGenerator {
    
    
    private static final int MAX_ROOT_LENGTH = 24;
    
    private static final char DICOM_UID_SEPARATOR = '.';
    
    /**
     * @see org.openmrs.module.radiology.dicom.DicomUidGenerator#getMaxRootLength()
     */
    @Override
    public int getMaxRootLength() {
        
        return MAX_ROOT_LENGTH;
    }
    
    /**
     * @see org.openmrs.module.radiology.dicom.DicomUidGenerator#getNewDicomUid(String)
     */
    @Override
    public String getNewDicomUid(String root) {
        
        if (root == null) {
            throw new NullPointerException("root is required");
        }
        
        if (!DicomUidValidator.isValid(root)) {
            throw new IllegalArgumentException("root is an invalid DICOM UID");
        }
        
        if (root.length() > MAX_ROOT_LENGTH) {
            throw new IllegalArgumentException("root length is > " + MAX_ROOT_LENGTH);
        }
        
        final String suffix = new DecimalUuid(java.util.UUID.randomUUID()).toString();
        return root + DICOM_UID_SEPARATOR + suffix;
    }
}
