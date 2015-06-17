/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.db.GenericDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Main extends OpenmrsService {
	
	public void setGdao(GenericDAO dao);
	
	public void setSdao(StudyDAO dao);
	
	public Object get(String query, boolean unique);
	
	public Study getStudy(Integer id);
	
	public Study saveStudy(Study os);
	
	public void sendModalityWorklist(Study s, OrderRequest orderRequest);
	
	public Study getStudyByOrderId(Integer id);
	
	public GenericDAO db();
	
}
