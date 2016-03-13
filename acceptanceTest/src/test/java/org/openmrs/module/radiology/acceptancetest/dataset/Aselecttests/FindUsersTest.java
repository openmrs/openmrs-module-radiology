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
