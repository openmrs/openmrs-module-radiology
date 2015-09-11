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

import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.db.GenericDAO;
import org.openmrs.module.radiology.db.RadiologyOrderDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RadiologyService extends OpenmrsService {
	
	public void setGdao(GenericDAO dao);
	
	public void setRadiologyOrderDao(RadiologyOrderDAO radiologyOrderDao);
	
	public void setSdao(StudyDAO dao);
	
	public Object get(String query, boolean unique);
	
	/**
	 * Save given <code>RadiologyOrder</code> to the database
	 * 
	 * @param radiologyOrder radiology order to be created or updated
	 * @return RadiologyOrder who was created or updated
	 * @throws IllegalArgumentException if radiologyOrder is null
	 * @should create new radiology order from given radiology order object
	 * @should throw illegal argument exception given null
	 */
	public RadiologyOrder saveRadiologyOrder(RadiologyOrder radiologyOrder) throws IllegalArgumentException;
	
	/**
	 * Get RadiologyOrder by its orderId
	 * 
	 * @param orderId of wanted RadiologyOrder
	 * @return RadiologyOrder matching given orderId
	 * @throws IllegalArgumentException if order id is null
	 * @should return radiology order matching order id
	 * @should return null if no match was found
	 * @should throw illegal argument exception given null
	 */
	public RadiologyOrder getRadiologyOrderByOrderId(Integer orderId) throws IllegalArgumentException;
	
	/**
	 * Get RadiologyOrder's by its associated Patient
	 * 
	 * @param patient patient of wanted RadiologyOrders
	 * @return RadiologyOrders associated with given patient
	 * @throws IllegalArgumentException if patient is null
	 * @should return all radiology orders associated with given patient
	 * @should return empty list given patient without associated radiology orders
	 * @should throw illegal argument exception given null
	 */
	public List<RadiologyOrder> getRadiologyOrdersByPatient(Patient patient) throws IllegalArgumentException;
	
	/**
	 * Get RadiologyOrder's by its associated Patients
	 * 
	 * @param patients list of patients for which RadiologyOrders are queried
	 * @return RadiologyOrders associated with given patients
	 * @throws IllegalArgumentException if patients is null
	 * @should return all radiology orders associated with given patients
	 * @should return all radiology orders given empty patient list
	 * @should throw illegal argument exception given null
	 */
	public List<RadiologyOrder> getRadiologyOrdersByPatients(List<Patient> patients) throws IllegalArgumentException;
	
	/**
	 * <p>
	 * Save the given <code>Study</code> to the database
	 * </p>
	 * Additionally, study and study.order information are written into a DICOM xml file.
	 * 
	 * @param study study to be created or updated
	 * @return study who was created or updated
	 * @throws IllegalArgumentException
	 * @throws APIException
	 * @should create new study from given study object
	 * @should update existing study
	 * @should throw IllegalArgumentException if study is null
	 * @should throw APIException given study with empty order id
	 * @should throw APIException given study with empty modality
	 */
	public Study saveStudy(Study study) throws APIException, IllegalArgumentException;
	
	public void sendModalityWorklist(Study s, OrderRequest orderRequest);
	
	public Study getStudy(Integer id);
	
	public Study getStudyByOrderId(Integer id);
	
	/**
	 * Get study by its Study Instance UID
	 * 
	 * @param studyInstanceUid
	 * @return study
	 * @should return study matching study instance uid
	 * @should return null if no match was found
	 * @should throw IllegalArgumentException if study instance uid is null
	 */
	public Study getStudyByStudyInstanceUid(String studyInstanceUid) throws IllegalArgumentException;
	
	/**
	 * Get all studies corresponding to list of RadiologyOrder's
	 * 
	 * @param radiologyOrders radiology orders for which studies will be returned
	 * @return studies corresponding to given radiology orders
	 * @throws IllegalArgumentException
	 * @should fetch all studies for given radiology orders
	 * @should return empty list given radiology orders without associated studies
	 * @should return empty list given empty radiology order list
	 * @should throw IllegalArgumentException given null
	 */
	public List<Study> getStudiesByRadiologyOrders(List<RadiologyOrder> radiologyOrders) throws IllegalArgumentException;
	
	public GenericDAO db();
	
	/**
	 * Get all obs matching the orderId
	 * 
	 * @param orderId orderId of obs
	 * @return list of obs
	 * @throws IllegalArgumentException
	 * @should fetch all obs for given orderId
	 * @should return empty list given orderId without associated obs
	 * @should throw IllegalArgumentException given null
	 */
	public List<Obs> getObsByOrderId(Integer orderId) throws IllegalArgumentException;
	
}
