package org.openmrs.module.radiology.util;

import java.math.BigInteger;
import java.util.UUID;

/**
 * Translates a {@link java.util.UUID} into its decimal representation using {@link java.math.BigInteger}.
 */
public class DecimalUuid {
    
    
    /**
     * Holds the decimal representation of a {@link java.util.UUID}.
     */
    final private BigInteger decimalUuid;
    
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
        return new BigInteger(pureHexUuid, 16);
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
        return this.decimalUuid.toString();
    }
}
