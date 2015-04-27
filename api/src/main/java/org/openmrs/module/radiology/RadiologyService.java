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
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.db.StudyDAO;
import org.openmrs.module.radiology.db.VisitDAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RadiologyService extends OpenmrsService {
	
	public void setSdao(StudyDAO dao);
	
	public void setVdao(VisitDAO dao);
	
	/**
	 * <p>
	 * Save the given <code>study</code> to the database
	 * </p>
	 * Additionally, study and study.order information are written into a DICOM xml file.
	 * 
	 * @param study study to be created or updated
	 * @return study who was created or updated
	 * @throws APIException
	 * @throws IllegalArgumentException
	 * @should create new study from given study object
	 * @should create new study and set its uid
	 * @should update existing study
	 * @should not save study given study with empty modality
	 * @should not save study given study with empty order
	 * @should throw IllegalArgumentException if study is null
	 */
	public Study saveStudy(Study study) throws APIException, IllegalArgumentException;
	
	/**
	 * <p>
	 * Update <code>performedStatus</code> of existing <code>study</code> in the database
	 * </p>
	 * 
	 * @param study Study to be updated
	 * @param performedStatus Performed Procedure Step Status to set Study to
	 * @return study which was updated
	 * @throws IllegalArgumentException
	 * @should update performed status of given study in database to given performed status
	 * @should not update non existing study
	 * @should throw IllegalArgumentException if study is null
	 * @should throw IllegalArgumentException if performedStatus is null
	 */
	//TODO(teleivo) is check for non existing study.id != null enough, could study.id be set somewhere other than on saveStudy()
	public Study updateStudyPerformedStatus(Study study, PerformedProcedureStepStatus performedStatus)
	        throws IllegalArgumentException;
	
	public void sendModalityWorklist(Study s, OrderRequest orderRequest);
	
	public Visit getVisit(Integer id);
	
	public Visit saveVisit(Visit v);
	
	/**
	 * Get study by orderId
	 * 
	 * @param orderId the orderId of order corresponding to study
	 * @return study the study associated with order matching given orderId
	 * @should should return study matching order with given orderId
	 * @should should return new study instance if no match is found
	 */
	public Study getStudyByOrderId(Integer orderId);
	
	/**
	 * Get study by id
	 * 
	 * @param id
	 * @return study
	 * @throws IllegalArgumentException
	 * @should return study matching id
	 * @should return null if no match was found
	 * @should throw IllegalArgumentException if id is null
	 */
	public Study getStudy(Integer id) throws IllegalArgumentException;
	
	/**
	 * Get study by order
	 * 
	 * @param order
	 * @return study
	 * @throws IllegalArgumentException
	 * @should return study matching order
	 * @should return null if no match was found
	 * @should throw IllegalArgumentException if order is null
	 */
	public Study getStudyByOrder(Order order) throws IllegalArgumentException;
	
	/**
	 * Get study by uid
	 * 
	 * @param uid
	 * @return study
	 * @should return study matching uid
	 * @should return null if no match was found
	 * @should throw IllegalArgumentException if uid is null
	 */
	public Study getStudyByUid(String uid) throws IllegalArgumentException;
	
	/**
	 * Get all studies belonging to a patient
	 * 
	 * @param patient
	 * @return list of studies
	 * @should fetch all studies for given patient
	 * @should return empty list for given patient without studies
	 * @should throw IllegalArgumentException if patient is null
	 */
	public List<Study> getStudiesByPatient(Patient patient) throws IllegalArgumentException;
	
	/**
	 * Get all studies belonging to list of orders
	 * 
	 * @param orders
	 * @return list of studies corresponding to given orders
	 * @should fetch all studies for given orders
	 * @should return empty list for given orders without studies
	 * @should throw IllegalArgumentException if orders is null
	 */
	public List<Study> getStudiesByOrders(List<Order> orders) throws IllegalArgumentException;
	
	/**
	 * Get all obs belonging to a study
	 * 
	 * @param study Study for which obs are searched
	 * @return list of obs
	 * @should fetch all obs for given study
	 * @should return empty list for given study without obs
	 * @should throw IllegalArgumentException if study is null
	 */
	public List<Obs> getObservationsByStudy(Study study) throws IllegalArgumentException;
	
}
