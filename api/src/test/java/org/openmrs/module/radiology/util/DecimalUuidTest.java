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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link DecimalUuid}.
 */
public class DecimalUuidTest {
    
    
    private DecimalUuid decimalUuid;
    
    private Method getBigIntegerFromUuuidMethod;
    
    @Before
    public void setUp() throws Exception {
        
        if (decimalUuid == null) {
            decimalUuid = new DecimalUuid(java.util.UUID.randomUUID());
        }
        
        getBigIntegerFromUuuidMethod =
                DecimalUuid.class.getDeclaredMethod("getBigIntegerFromUuid", new Class[] { java.util.UUID.class });
        getBigIntegerFromUuuidMethod.setAccessible(true);
    }
    
    /**
     * @see DecimalUuid#getBigIntegerFromUuid(UUID)
     * @verifies convert given uuid into a big integer
     */
    @Test
    public void getBigIntegerFromUUID_shouldTranslateGivenUuidIntoABigInteger() throws Exception {
        
        // Sample UUIDs with corresponding decimal representation (OIDs suffix, after 2.25.) taken from
        // http://www.itu.int/en/ITU-T/asn1/Pages/UUID/generate_uuid.aspx
        UUID testUuid1 = java.util.UUID.fromString("d1e08f60-0246-11e6-973b-0002a5d5c51b");
        BigInteger expectedBigIntegerTestUuid1 = new BigInteger("278974633606539821744827903865060181275");
        assertThat(expectedBigIntegerTestUuid1,
            is(getBigIntegerFromUuuidMethod.invoke(decimalUuid, new Object[] { testUuid1 })));
        
        UUID testUuid2 = java.util.UUID.fromString("3f3a55a0-0247-11e6-a2fa-0002a5d5c51b");
        BigInteger expectedBigIntegerTestUuid2 = new BigInteger("84044253634271920957235037603861218587");
        assertThat(expectedBigIntegerTestUuid2,
            is(getBigIntegerFromUuuidMethod.invoke(decimalUuid, new Object[] { testUuid2 })));
        
        UUID testUuid3 = java.util.UUID.fromString("4c04dee0-0247-11e6-81ba-0002a5d5c51b");
        BigInteger expectedBigIntegerTestUuid3 = new BigInteger("101046617309833047802413654344205714715");
        assertThat(expectedBigIntegerTestUuid3,
            is(getBigIntegerFromUuuidMethod.invoke(decimalUuid, new Object[] { testUuid3 })));
    }
}
