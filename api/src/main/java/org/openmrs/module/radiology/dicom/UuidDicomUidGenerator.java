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
