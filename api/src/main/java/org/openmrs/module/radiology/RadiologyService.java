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

import java.util.Date;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.OrderService;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.db.RadiologyOrderDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RadiologyService extends OpenmrsService {
	
	public void setRadiologyOrderDao(RadiologyOrderDAO radiologyOrderDao);
	
	public void setSdao(StudyDAO dao);
	
	public void setOrderService(OrderService orderService);
	
	public void setEncounterService(EncounterService encounterService);
	
	/**
	 * Save given <code>RadiologyOrder</code> and its <code>RadiologyOrder.study</code> to the
	 * database
	 * 
	 * @param radiologyOrder radiology order to be created
	 * @return RadiologyOrder who was created
	 * @throws IllegalArgumentException if radiologyOrder is null
	 * @throws IllegalArgumentException if radiologyOrder orderId is not null
	 * @throws IllegalArgumentException if radiologyOrder.study is null
	 * @should create new radiology order and study from given radiology order object
	 * @should throw illegal argument exception given null
	 * @should throw illegal argument exception given existing radiology order
	 * @should throw illegal argument exception if given radiology order has no study
	 * @should throw illegal argument exception if given study modality is null
	 */
	public RadiologyOrder placeRadiologyOrder(RadiologyOrder radiologyOrder) throws IllegalArgumentException;
	
	/**
	 * Discontinue given <code>RadiologyOrder</code>
	 * 
	 * @param radiologyOrder radiology order to be discontinued
	 * @return Order who was created to discontinue RadiologyOrder
	 * @throws IllegalArgumentException if radiologyOrder is null
	 * @throws IllegalArgumentException if radiologyOrder orderId is null
	 * @throws IllegalArgumentException if radiologyOrder is not active
	 * @throws IllegalArgumentException if provider is null
	 * @should create discontinuation order which discontinues given radiology order object
	 * @should throw illegal argument exception given empty radiology order
	 * @should throw illegal argument exception given radiology order with orderId null
	 * @should throw illegal argument exception if radiology order is not active
	 * @should throw illegal argument exception given empty provider
	 */
	public Order discontinueRadiologyOrder(RadiologyOrder radiologyOrder, Provider orderer, Date discontinueDate,
	        String discontinueReason) throws Exception;
	
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
	 * Update the performedStatus of the <code>Study</code> associated with studyInstanceUid in the
	 * database
	 * </p>
	 * 
	 * @param studyInstanceUid study instance uid of study whos performedStatus should be updated
	 * @param performedStatus performed procedure step status to which study should be set to
	 * @return study whos performedStatus was updated
	 * @throws IllegalArgumentException if study instance uid is null
	 * @should update performed status of study associated with given study instance uid
	 * @should throw illegal argument exception if study instance uid is null
	 * @should throw illegal argument exception if performed status is null
	 */
	public Study updateStudyPerformedStatus(String studyInstanceUid, PerformedProcedureStepStatus performedStatus)
	        throws IllegalArgumentException;
	
	public void sendModalityWorklist(RadiologyOrder radiologyOrder, OrderRequest orderRequest);
	
	public Study getStudy(Integer id);
	
	/**
	 * Get Study by its associated RadiologyOrder's orderId
	 * 
	 * @param orderId of RadiologyOrder associated with wanted Study
	 * @return Study associated with RadiologyOrder for which orderId is given
	 * @throws IllegalArgumentException if order id is null
	 * @should return study associated with radiology order for which order id is given
	 * @should return null if no match was found
	 * @should throw illegal argument exception given null
	 */
	public Study getStudyByOrderId(Integer orderId) throws IllegalArgumentException;
	
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
