/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.db;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.module.radiology.RadiologyOrder;

/**
 * RadiologyOrder-related database functions
 * 
 * @see org.openmrs.module.radiology.RadiologyService
 */
public interface RadiologyOrderDAO {
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#getRadiologyOrderByOrderId(Integer)
	 */
	public RadiologyOrder getRadiologyOrderByOrderId(Integer orderId);
	
	/**
	 * @see org.openmrs.module.radiology.RadiologyService#getRadiologyOrdersByPatient(Patient)
	 */
	public List<RadiologyOrder> getRadiologyOrdersByPatient(Patient patient);
	
	/**
	 * @see
	 *      org.openmrs.module.radiology.RadiologyService#getRadiologyOrdersByPatients(List<Patient>)
	 */
	public List<RadiologyOrder> getRadiologyOrdersByPatients(List<Patient> patients);
	
}
