/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.acceptancetest.dataset;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class DbSetup extends DbSetupConfiguration {

    @Test
    public void setupInsertAndCheckTestData() throws Exception {
        Boolean ifUserJohnExists = defaultSelectSupport.patientExists("2","John");
        Boolean ifProviderExists = defaultSelectSupport.providerExists("2","Nurse Bob");
        assertTrue("User John does not exist.",ifUserJohnExists);
        assertTrue("Provider \"Nurse Bob\" does not exist.",ifProviderExists);

        DatabaseOperation.CLEAN_INSERT.execute(getDatabaseConnection(), getDataSet("DbSetup_setupData.xml"));
        Boolean ifOrderOneExists = defaultSelectSupport.ifOrderExists("10","3","2","COMPLETED");
        Boolean ifOrderTwoExists = defaultSelectSupport.ifOrderExists("11","3","2","IN_PROGRESS");
        assertTrue("Could not create Order with the ID=10.",ifOrderOneExists);
        assertTrue("Could not create Order with the ID=11.",ifOrderTwoExists);
    }

}