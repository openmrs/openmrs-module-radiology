/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.util;

import java.math.BigInteger;
import java.util.UUID;

/**
 * Translates a {@link java.util.UUID} into its decimal representation using {@link java.math.BigInteger}.
 */
public class DecimalUuid {
    
    
    private static final int RADIX_HEX = 16;
    
    /**
     * Holds the decimal representation of a {@link java.util.UUID}.
     */
    private final BigInteger decimalUuid;
    
    /**
     * Create a {@code DecimalUuid} from given {@code uuid}.
     * 
     * @param uuid UUID to be translated into its decimal representation
     */
    public DecimalUuid(UUID uuid) {
        this.decimalUuid = getBigIntegerFromUuid(uuid);
    }
    
    /**
     * Get decimal representation {@code decimalUuid}.
     * 
     * @return decimal representation decimalUuid
     */
    public BigInteger getDecimalUuid() {
        return this.decimalUuid;
    }
    
    /**
     * Translate given {@code uuid} to {@code BigInteger}.
     * 
     * @param uuid UUID to translate into BigInteger
     * @return big integer representation of uuid
     * @should translate given uuid into a big integer
     */
    private static BigInteger getBigIntegerFromUuid(UUID uuid) {
        
        final String pureHexUuid = uuid.toString()
                .replaceAll("-", "");
        return new BigInteger(pureHexUuid, RADIX_HEX);
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
        return this.decimalUuid.toString();
    }
}
