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

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.hl7v2.HL7Exception;

@Transactional
public interface RadiologyService extends OpenmrsService {
	
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
	 * @should create radiology order encounter with orderer and attached to existing active visit if patient has active
	 *         visit
	 * @should create radiology order encounter with orderer attached to new active visit if patient without active visit
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
	 * @should create discontinuation order which discontinues given radiology order that is not in progress or completed
	 * @should create discontinuation order with encounter attached to existing active visit if patient has active visit
	 * @should create discontinuation order with encounter attached to new active visit if patient without active visit
	 * @should throw illegal argument exception given empty radiology order
	 * @should throw illegal argument exception given radiology order with orderId null
	 * @should throw illegal argument exception if radiology order is not active
	 * @should throw illegal argument exception if radiology order is in progress
	 * @should throw illegal argument exception if radiology order is completed
	 * @should throw illegal argument exception given empty provider
	 */
	public Order discontinueRadiologyOrder(RadiologyOrder radiologyOrder, Provider orderer, String discontinueReason)
			throws Exception;
	
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
	 * Update the performedStatus of the <code>Study</code> associated with studyInstanceUid in the database
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
	
	/**
	 * Save given <code>RadiologyOrder</code> in the PACS by sending an HL7 order message.
	 *
	 * @param radiologyOrder radiology order for which hl7 order message is sent to the PACS
	 * @return true if hl7 order message to create radiology order was successfully sent to PACS and false otherwise
	 * @throws HL7Exception
	 * @throws IllegalArgumentException if radiologyOrder is null
	 * @throws IllegalArgumentException if radiologyOrder orderId is null
	 * @throws IllegalArgumentException if radiologyOrder.study is null
	 * @should send hl7 order message to pacs to create given radiology order and return true on success
	 * @should send hl7 order message to pacs to create given radiology order and return false on failure
	 * @should throw illegal argument exception given null
	 * @should throw illegal argument exception given radiology order with orderId null
	 * @should throw illegal argument exception if given radiology order has no study
	 */
	public boolean placeRadiologyOrderInPacs(RadiologyOrder radiologyOrder) throws HL7Exception;
	
	/**
	 * Discontinue given <code>RadiologyOrder</code> in the PACS by sending an HL7 order message.
	 *
	 * @param radiologyOrder radiology order for which hl7 order message is sent to the PACS
	 * @return true if hl7 order message to discontinue radiology order was successfully sent to PACS and false otherwise
	 * @throws HL7Exception
	 * @throws IllegalArgumentException if radiologyOrder is null
	 * @throws IllegalArgumentException if radiologyOrder orderId is null
	 * @should send hl7 order message to pacs to discontinue given radiology order and return true on success
	 * @should send hl7 order message to pacs to discontinue given radiology order and return false on failure
	 * @should throw illegal argument exception given null
	 * @should throw illegal argument exception given radiology order with orderId null
	 */
	public boolean discontinueRadiologyOrderInPacs(RadiologyOrder radiologyOrder) throws HL7Exception;
	
	/**
	 * Get Study by studyId
	 *
	 * @param studyId of the study
	 * @return study associated with studyId
	 * @should return study for given study id
	 * @should return null if no match was found
	 */
	public Study getStudyByStudyId(Integer studyId);
	
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
	 * Creates a RadiologyReport and sets the radiologyReportStatus to claimed
	 *
	 * @param radiologyOrder RadiologyOrder
	 * @return a new claimed RadiologyReport
	 * @throws IllegalArgumentException if given RadiologyOrder is null
	 * @throws IllegalArgumentException if Study of given radiologyReport is null
	 * @throws IllegalArgumentException if Study of given RadiologyOrder is not completed
	 * @throws UnsupportedOperationException if given order has a completed RadiologyReport
	 * @throws UnsupportedOperationException if given order has a claimed RadiologyReport
	 * @should create a radiology order with report status claimed given a completed radiology order
	 * @should throw an illegal argument exception if given radiology order is null
	 * @should throw an illegal argument exception if given radiology order is not completed
	 * @should throw an UnsupportedOperationException if given order has a completed RadiologyReport
	 * @should throw an UnsupportedOperationException if given order has a claimed RadiologyReport
	 */
	RadiologyReport createAndClaimRadiologyReport(RadiologyOrder radiologyOrder) throws IllegalArgumentException,
			UnsupportedOperationException;
	
	/**
	 * Save the given radiologyReport
	 *
	 * @param radiologyReport RadiologyReport to be saved
	 * @return the saved radiologyReport
	 * @throws IllegalArgumentException if radiologyReport is null
	 * @throws IllegalArgumentException if radiologyReportStatus is null
	 * @throws UnsupportedOperationException if radiologyReport is discontinued
	 * @throws UnsupportedOperationException if radiologyReport is completed
	 * @should save radiologyReport in database and return it
	 * @should throw an IllegalArgumentException if radiologyReport is null
	 * @should throw an IllegalArgumentException if radiologyReportStatus is null
	 * @should throw an UnsupportedOperationException if radiologyReport is discontinued
	 * @should throw an UnsupportedOperationException if radiologyReport is completed
	 */
	RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport) throws IllegalArgumentException,
			UnsupportedOperationException;
	
	/**
	 * Unclaims the given radiologyReport and sets the radiologyReportStatus to discontinued
	 *
	 * @param radiologyReport RadiologyReport to be unclaimed
	 * @return the discontinued RadiologyReport
	 * @throws IllegalArgumentException if radiologyReport is null
	 * @throws IllegalArgumentException if radiologyReportStatus is null
	 * @throws UnsupportedOperationException if radiologyReport is discontinued
	 * @throws UnsupportedOperationException if radiologyReport is completed
	 * @should set the radiologyReportStatus of radiologyReport to discontinued
	 * @should throw an IllegalArgumentException if radiologyReport is null
	 * @should throw an IllegalArgumentException if radiologyReportStatus is null
	 * @should throw an UnsupportedOperationException if radiologyReport is discontinued
	 * @should throw an UnsupportedOperationException if radiologyReport is completed
	 */
	RadiologyReport unclaimRadiologyReport(RadiologyReport radiologyReport) throws IllegalArgumentException,
			UnsupportedOperationException;
	
	/**
	 * Completes the given radiologyReport and sets the radiologyReportStatus to completed
	 *
	 * @param radiologyReport RadiologyReport to be completed
	 * @param principalResultsInterpreter which the RadiologyReport should be set to
	 * @return completed RadiologyReport matching given radiologyReport with
	 *         principalResultsInterpreter
	 * @throws IllegalArgumentException if radiologyReport is null
	 * @throws IllegalArgumentException if principalResultsInterpreter is null
	 * @throws IllegalArgumentException if radiologyReportStatus is null
	 * @throws UnsupportedOperationException if radiologyReport is discontinued
	 * @throws UnsupportedOperationException if radiologyReport is completed
	 * @should set the reportDate of the radiologyReport to the day the RadiologyReport was
	 *         completed
	 * @should set the radiologyReportStatus to complete
	 * @should throw an IllegalArgumentException if principalResultsInterpreter is null
	 * @should throw an IllegalArgumentException if radiologyReport is null
	 * @should throw an IllegalArgumentException if radiologyReportStatus is null
	 * @should throw an UnsupportedOperationException if radiologyReport is discontinued
	 * @should throw an UnsupportedOperationException if radiologyReport is completed
	 */
	RadiologyReport completeRadiologyReport(RadiologyReport radiologyReport, Provider principalResultsInterpreter)
			throws IllegalArgumentException, UnsupportedOperationException;
	
	/**
	 * Get a RadiologyReport matching the radiologyReportId
	 *
	 * @param radiologyReportId of RadiologyReport
	 * @return RadiologyReport matching given radiologyReportId
	 * @throws IllegalArgumentException if radiologyReportId is null
	 * @should fetch RadiologyReport matching given radiologyReportId
	 * @should throw IllegalArgumentException if radiologyReportId is null
	 */
	RadiologyReport getRadiologyReportByRadiologyReportId(Integer radiologyReportId) throws IllegalArgumentException;
	
	/**
	 * Get all RadiologyReports fetched by radiologyOrder and radiologyReportStatus
	 *
	 * @param radiologyOrder RadiologyOrder for which the RadiologyReport should be fetched
	 * @param reportStatus RadiologyReportStatus the RadiologyReport should have
	 * @return List with RadiologyReport filtered by radiologyOrder and reportStatus
	 * @throws IllegalArgumentException if given radiologyOrder is null
	 * @throws IllegalArgumentException if given reportStatus is null
	 * @should return a list of completed RadiologyReport if reportStatus is completed
	 * @should return a list of claimed RadiologyReport if reportStatus is claimed
	 * @should return a list of discontinued RadiologyReport if reportStatus is discontinued
	 * @should return an empty list if there are no RadiologyReports for this radiologyOrder
	 * @should throw an IllegalArgumentException if given radiologyOrder is null
	 * @should throw an IllegalArgumentException if given reportStatus is null
	 */
	List<RadiologyReport> getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder radiologyOrder,
			RadiologyReportStatus reportStatus) throws IllegalArgumentException;
	
	/**
	 * Convenience method to check if a RadiologyOrder has a claimed RadiologyReport
	 *
	 * @param radiologyOrder RadiologyOrder the radiologyOrder which should be checked
	 * @return true if RadiologyOrder has a claimed RadiologyReport, otherwise false and also if RadiologyOrder is null
	 * @should return true if the RadiologyOrder has a claimed RadiologyReport
	 * @should return false if the RadiologyOrder has no claimed RadiologyReport
	 * @should return false if the RadiologyOrder is null
	 */
	boolean hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder radiologyOrder);
	
	/**
	 * Convenience method to check if a RadiologyOrder has a completed RadiologyReport
	 *
	 * @param radiologyOrder RadiologyOrder the radiologyOrder which should be checked
	 * @return true if RadiologyOrder has a completed RadiologyReport, otherwise false
	 * @throws IllegalArgumentException if radiologyOrder is null
	 * @should return true if the RadiologyOrder has a completed RadiologyReport
	 * @should return false if the RadiologyOrder has no completed RadiologyReport
	 * @should throw an IllegalArgumentException if radiologyOrder is null
	 */
	boolean hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder radiologyOrder);
	
	/**
	 * Get the active (can be claimed or completed) RadiologyReport matching the radiologyOrder
	 *
	 * @param radiologyOrder RadiologyOrder the radiologyOrder which should be checked
	 * @return RadiologyReport filtered by radiologyOrder and radiologyReportStatus not equal to
	 *         discontinued
	 * @throws IllegalArgumentException if radiologyOrder is null
	 * @should return a RadiologyReport if the reportStatus is completed
	 * @should return a RadiologyReport if the reportStatus is claimed
	 * @should return null if
	 * @should throw an IllegalArgumentException if radiologyOrder is null
	 */
	RadiologyReport getActiveRadiologyReportByRadiologyOrder(RadiologyOrder radiologyOrder);
	
}
