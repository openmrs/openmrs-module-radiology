/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.acceptancetest.dataset.Aselecttests;

import org.openmrs.module.radiology.acceptancetest.dataset.DbTestConfiguration;

import org.junit.Test;
import static org.junit.Assert.*;

public class FindUsersTest extends DbTestConfiguration {

  @Test
  public void testFindPatientJohn() throws Exception {
    Boolean ifUserExists = defaultSupport.patientExists("2","John");
    assertTrue("User John does not exist.",ifUserExists);
  }
  @Test
  public void testFindNurseBob() throws Exception {
    Boolean ifProviderExists = defaultSupport.providerExists("2","Nurse Bob");
    assertTrue("Provider \"Nurse Bob\" does not exist.",ifProviderExists);
  }
}
