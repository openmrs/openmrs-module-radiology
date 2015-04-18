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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.dcm4che.tool.hl7snd.HL7Snd;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.SpecificCharacterSet;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.ContentHandlerAdapter;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.SAXWriter;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Study.PerformedStatuses;
import org.openmrs.module.radiology.Study.ScheduledStatuses;
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.CommonOrderPriority;
import org.openmrs.module.radiology.hl7.HL7Generator;
import org.xml.sax.SAXException;

import ca.uhn.hl7v2.HL7Exception;

public class DicomUtils {
	
	private static Logger log = Logger.getLogger(DicomUtils.class);
	
	private static void debug(String message) {
		if (log.isDebugEnabled())
			log.debug(message);
	}
	
	/**
	 * Search in the configured MPPS directory for a DICOM file whose study UID matches studyPrefix
	 * + o.getOrderId()
	 * 
	 * @param o the order from which to extract orderID
	 * @param tag the tag to return
	 * @return the value of the attribute marked by tag
	 * @throws IOException
	 */
	public static String findDCM(Order o, int[] tag) throws IOException {
		File f = new File(Utils.mppsDir());
		String status = "?";
		String[] list = f.list();
		SpecificCharacterSet scs = new SpecificCharacterSet(Utils.specificCharacterSet());
		int arg1 = 0;
		for (String string : list) {
			DicomObject d = null;
			DicomInputStream dis = null;
			try {
				dis = new DicomInputStream(new File(f, string));
				d = (dis).readDicomObject();
				
				if ((Utils.studyPrefix() + o.getOrderId().toString()).compareTo(d.get(Tag.ScheduledStepAttributesSequence)
				        .getDicomObject().get(Tag.StudyInstanceUID).getValueAsString(scs, arg1)) == 0) {
					status = d.get(tag).getValueAsString(scs, arg1);
				}
			}
			catch (IOException e) {
				throw e;
			}
			finally {
				if (dis != null)
					try {
						dis.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return status;
	}
	
	/**
	 * Search in the configured MWL directory for a XML file whose name matches o.getOrderId()+
	 * ".xml"
	 * 
	 * @param o the order from which to extract orderID
	 * @param tag the tag to return
	 * @return the value of the attribute marked by tag
	 * @throws Exception
	 */
	public static String findXML(Order o, int[] tag) throws Exception {
		String status = "?";
		SpecificCharacterSet scs = new SpecificCharacterSet(Utils.specificCharacterSet());
		int arg1 = 0;
		DicomObject d = null;
		try {
			String _ = File.separator;
			d = new BasicDicomObject();
			SAXParserFactory f = SAXParserFactory.newInstance();
			SAXParser p1 = f.newSAXParser();
			ContentHandlerAdapter ch = new ContentHandlerAdapter(d);
			String pathname = Utils.mwlDir() + _ + o.getOrderId() + ".xml";
			p1.parse(new File(pathname), ch);
			
			status = d.get(tag).getValueAsString(scs, arg1);
			
		}
		catch (Exception e) {
			throw e;
		}
		return status;
	}
	
	public static String orderPriority(Order o) throws Exception {
		int[] p = { Tag.RequestedProcedurePriority };
		return findXML(o, p);
	}
	
	/**
	 * @param o order to be searched
	 * @return String of the status that appears on this order (o) MPPS file
	 * @throws IOException
	 */
	public static String orderStatus(Order o) throws IOException {
		int[] p = { Tag.PerformedProcedureStepStatus };
		return findDCM(o, p);
	}
	
	/**
	 * Writes o to MWL file in XML format
	 * 
	 * @throws Exception multiple ones, but just handled as one in the controller
	 */
	public static void write(Order o, Study s, File file) throws TransformerConfigurationException,
	        TransformerFactoryConfigurationError, SAXException, IOException {
		
		BasicDicomObject workitem = new BasicDicomObject();
		BasicDicomObject rpcs = new BasicDicomObject();
		BasicDicomObject spss = new BasicDicomObject();
		BasicDicomObject spcs = new BasicDicomObject();
		workitem.putString(Tag.SpecificCharacterSet, VR.CS, Utils.specificCharacterSet());
		workitem.putString(Tag.AccessionNumber, VR.SH, "");
		try {
			workitem.putString(Tag.ReferringPhysicianName, VR.PN, o.getOrderer().getPersonName().getFullName().replace(' ',
			    '^'));
		}
		catch (Exception e) {
			debug("Not saving referring physician");
		}
		workitem.putSequence(Tag.ReferencedStudySequence);
		workitem.putSequence(Tag.ReferencedPatientSequence);
		try {
			workitem.putString(Tag.PatientName, VR.PN, o.getPatient().getPersonName().getFullName().replace(' ', '^'));
			workitem.putString(Tag.PatientID, VR.LO, o.getPatient().getPatientIdentifier().getIdentifier());
			workitem.putString(Tag.PatientBirthDate, VR.DA, Utils.plain(o.getPatient().getBirthdate()));
			workitem.putString(Tag.PatientSex, VR.CS, o.getPatient().getGender());
		}
		catch (Exception e) {
			debug("Not saving patient details");
		}
		workitem.putString(Tag.PatientWeight, VR.DS, "");
		workitem.putString(Tag.MedicalAlerts, VR.LO, "");
		workitem.putString(Tag.Allergies, VR.LO, "");
		workitem.putString(Tag.PregnancyStatus, VR.US, "");
		workitem.putString(Tag.StudyInstanceUID, VR.UI, Utils.studyPrefix() + s.getId());
		try {
			workitem.putString(Tag.RequestingPhysician, VR.PN, o.getOrderer().getPersonName().getFullName()
			        .replace(' ', '^'));
		}
		catch (Exception e) {
			debug("Not saving requesting physician");
		}
		try {
			workitem.putString(Tag.RequestedProcedureDescription, VR.LO, o.getInstructions());
		}
		catch (Exception e) {
			debug("Not saving order instructions");
		}
		// Requested Procedure Code Sequence - I Left !, requires coding scheme
		// (SNOMED, DCM, etc) selection
		rpcs.putString(Tag.CodeValue, VR.SH, "!");
		rpcs.putString(Tag.CodingSchemeDesignator, VR.SH, "!");
		rpcs.putString(Tag.CodeMeaning, VR.LO, "!");
		workitem.putNestedDicomObject(Tag.RequestedProcedureCodeSequence, rpcs);
		
		workitem.putString(Tag.AdmissionID, VR.LO, "");
		workitem.putString(Tag.SpecialNeeds, VR.LO, "");
		workitem.putString(Tag.CurrentPatientLocation, VR.LO, "");
		workitem.putString(Tag.PatientState, VR.LO, "");
		
		// Scheduled Procedure Step Sequence
		// ! requires form enhancement, multiple steps
		spss.putString(Tag.Modality, VR.CS, s.getModality().toString());
		spss.putString(Tag.RequestedContrastAgent, VR.LO, "");
		spss.putString(Tag.ScheduledStationAETitle, VR.AE, Utils.aeTitle());
		try {
			spss.putString(Tag.ScheduledProcedureStepStartDate, VR.DA, Utils.plain(o.getStartDate()));
			spss.putString(Tag.ScheduledProcedureStepStartTime, VR.TM, Utils.time(o.getStartDate()));
			spss.putString(Tag.ScheduledPerformingPhysicianName, VR.PN, "!");
			spss.putString(Tag.ScheduledProcedureStepDescription, VR.LO, o.getInstructions());
		}
		catch (Exception e) {
			debug("Not saving scheduled procedure");
		}
		// Scheduled Protocol Code Sequence, requires coding scheme
		spcs.putString(Tag.CodeValue, VR.SH, "!");
		spcs.putString(Tag.CodingSchemeDesignator, VR.SH, "!");
		spcs.putString(Tag.CodeMeaning, VR.LO, "!");
		spss.putNestedDicomObject(Tag.ScheduledProtocolCodeSequence, spcs);
		
		spss.putString(Tag.ScheduledProcedureStepID, VR.SH, String.valueOf(s.getId()));
		spss.putString(Tag.ScheduledStationName, VR.SH, "");
		spss.putString(Tag.ScheduledProcedureStepLocation, VR.SH, "");
		spss.putString(Tag.PreMedication, VR.LO, "");
		spss.putString(Tag.ScheduledProcedureStepStatus, VR.CS, ScheduledStatuses.string(s.getScheduledStatus(), false));
		workitem.putNestedDicomObject(Tag.ScheduledProcedureStepSequence, spss);
		
		workitem.putString(Tag.RequestedProcedureID, VR.SH, String.valueOf(s.getId()));
		workitem.putString(Tag.RequestedProcedurePriority, VR.SH, Study.Priorities.string(s.getPriority(), false));
		workitem.putString(Tag.PatientTransportArrangements, VR.LO, "");
		workitem.putString(Tag.ConfidentialityConstraintOnPatientDataDescription, VR.LO, "");
		
		TransformerHandler th = ((SAXTransformerFactory) TransformerFactory.newInstance()).newTransformerHandler();
		th.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
		th.setResult(new StreamResult(file));
		final SAXWriter writer = new SAXWriter(th, null);
		writer.write(workitem);
	}
	
	/**
	 * <p>
	 * Updates the PerformedStatus of an existing Study in the database to the
	 * PerformedProcedureStepStatus of a given DicomObject containing a DICOM N-CREATE/N-SET command
	 * </p>
	 * 
	 * @param mppsObject the DICOM MPPS object containing a DICOM N-CREATE/N-SET command with tag
	 *            performedProcedureStepStatus
	 * @should set performed status of an existing study in database to performed procedure step
	 *         status IN_PROGRESS of given mpps object
	 * @should set performed status of an existing study in database to performed procedure step
	 *         status DISCONTINUED of given mpps object
	 * @should set performed status of an existing study in database to performed procedure step
	 *         status COMPLETED of given mpps object
	 * @should not fail if study instance uid referenced in dicom mpps cannot be found
	 */
	public static void updateStudyPerformedStatusByMpps(DicomObject mppsObject) {
		try {
			
			String studyInstanceUid = getStudyInstanceUidFromMpps(mppsObject);
			
			Study studyToBeUpdated = service().getStudyByUid(studyInstanceUid);
			debug(studyToBeUpdated.toString());
			
			String performedProcedureStepStatus = getPerformedProcedureStepStatus(mppsObject);
			
			service().updateStudyPerformedStatus(studyToBeUpdated, PerformedStatuses.value(performedProcedureStepStatus));
			
			log.info("Received Update from dcm4chee. Updating Performed Procedure Step Status for study :"
			        + studyInstanceUid + " to Status : " + PerformedStatuses.value(performedProcedureStepStatus));
		}
		catch (NumberFormatException e) {
			log.error("Number can not be parsed");
		}
		catch (Exception e) {
			log.error("Error : " + e.getMessage());
		}
	}
	
	/**
	 * <p>
	 * Gets the Study Instance UID of a DICOM MPPS object
	 * </p>
	 * 
	 * @param mppsObject the DICOM MPPS object containing the Study Instance UID
	 * @should return study instance uid given dicom object
	 * @should return null given dicom mpps object without scheduled step attributes sequence
	 * @should return null given dicom mpps object with scheduled step attributes sequence missing
	 *         study instance uid tag
	 */
	public static String getStudyInstanceUidFromMpps(DicomObject mppsObject) {
		
		SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(Utils.specificCharacterSet());
		
		DicomElement scheduledStepAttributesSequenceElement = mppsObject.get(Tag.ScheduledStepAttributesSequence);
		if (scheduledStepAttributesSequenceElement == null)
			return null;
		
		DicomObject scheduledStepAttributesSequence = scheduledStepAttributesSequenceElement.getDicomObject();
		
		DicomElement studyInstanceUidElement = scheduledStepAttributesSequence.get(Tag.StudyInstanceUID);
		if (studyInstanceUidElement == null)
			return null;
		
		String studyInstanceUid = studyInstanceUidElement.getValueAsString(specificCharacterSet, 0);
		
		return studyInstanceUid;
	}
	
	/**
	 * <p>
	 * Gets the Performed Procedure Step Status of a DICOM object
	 * </p>
	 * 
	 * @param dicomObject the DICOM object containing the Performed Procedure Step Status
	 * @should return performed procedure step status given dicom object
	 * @should return null given given dicom object without performed procedure step status
	 */
	public static String getPerformedProcedureStepStatus(DicomObject dicomObject) {
		
		SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(Utils.specificCharacterSet());
		
		DicomElement performedProcedureStepStatusElement = dicomObject.get(Tag.PerformedProcedureStepStatus);
		if (performedProcedureStepStatusElement == null)
			return null;
		
		String performedProcedureStepStatus = performedProcedureStepStatusElement.getValueAsString(specificCharacterSet, 0);
		
		return performedProcedureStepStatus;
	}
	
	public enum OrderRequest {
		Default, Save_Order, Void_Order, Discontinue_Order, Undiscontinue_Order, Unvoid_Order;
	}
	
	/**
	 * Create HL7 ORM^O01 message to create a worklist request See IHE Radiology Technical Framework
	 * Volume 2.
	 * 
	 * @param study Study for which the order message is created
	 * @param orderRequest OrderRequest specifying the action of the order message
	 * @return encoded HL7 ORM^O01 message
	 * @should return encoded HL7 ORMO01 message string with new order control given study with
	 *         mwlstatus zero and save order request
	 * @should return encoded HL7 ORMO01 message string with cancel order control given study with
	 *         mwlstatus zero and void order request
	 * @should return encoded HL7 ORMO01 message string with change order control given study with
	 *         mwlstatus one and save order request
	 */
	public static String createHL7Message(Study study, OrderRequest orderRequest) {
		String encodedHL7OrmMessage = null;
		
		Integer mwlstatus = study.getMwlStatus();
		CommonOrderOrderControl commonOrderOrderControl = getCommonOrderControlFrom(mwlstatus, orderRequest);
		
		CommonOrderPriority orderPriority = getCommonOrderPriorityFrom(study.getPriority());
		
		try {
			encodedHL7OrmMessage = HL7Generator.createEncodedRadiologyORMO01Message(study, commonOrderOrderControl,
			    orderPriority);
			System.out.println("Created Request \n" + encodedHL7OrmMessage);
		}
		catch (HL7Exception e) {
			log.error("Error creating ORM^O01 Message : " + e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return encodedHL7OrmMessage;
	}
	
	/**
	 * Get the HL7 Order Control Code component used in an HL7 common order segment (ORC-1 field)
	 * given the mwlstatus and orderRequest. See IHE Radiology Technical Framework Volume 2.
	 * 
	 * @param mwlstatus mwlstatus of a study
	 * @param orderRequest
	 * @should return HL7 order control given mwlstatus and orderrequest
	 */
	public static CommonOrderOrderControl getCommonOrderControlFrom(Integer mwlstatus, OrderRequest orderRequest) {
		CommonOrderOrderControl result = null;
		
		switch (orderRequest) {
			case Save_Order:
				if (mwlstatus.intValue() == 0 || mwlstatus.intValue() == 2) {
					result = CommonOrderOrderControl.NEW_ORDER;
				} else {
					result = CommonOrderOrderControl.CHANGE_ORDER;
				}
				break;
			case Void_Order:
				result = CommonOrderOrderControl.CANCEL_ORDER;
				break;
			case Unvoid_Order:
				result = CommonOrderOrderControl.NEW_ORDER;
				break;
			case Discontinue_Order:
				result = CommonOrderOrderControl.CANCEL_ORDER;
				break;
			case Undiscontinue_Order:
				result = CommonOrderOrderControl.NEW_ORDER;
				break;
			default:
				break;
		}
		return result;
	}
	
	/**
	 * Get the HL7 Priority component of Quantity/Timing (ORC-7) field included in an HL7 version
	 * 2.3.1 Common Order segment given the DICOM Requested Procedure Priority. See IHE Radiology
	 * Technical Framework Volume 2 for mapping of DICOM Requested Procedure Priority to HL7
	 * Priority.
	 * 
	 * @param priority Study.priority representing DICOM Requested Procedure Priority
	 * @should return hl7 common order priority given study priority
	 */
	public static CommonOrderPriority getCommonOrderPriorityFrom(Integer priority) {
		CommonOrderPriority result = null;
		
		switch (priority) {
			case Study.Priorities.STAT:
				result = CommonOrderPriority.STAT;
				break;
			case Study.Priorities.HIGH:
				result = CommonOrderPriority.ASAP;
				break;
			case Study.Priorities.ROUTINE:
				result = CommonOrderPriority.ROUTINE;
				break;
			case Study.Priorities.MEDIUM:
				result = CommonOrderPriority.TIMING_CRITICAL;
				break;
			case Study.Priorities.LOW:
				result = CommonOrderPriority.ROUTINE;
				break;
			default:
				result = CommonOrderPriority.ROUTINE;
				break;
		}
		return result;
	}
	
	//Send HL7 ORU message to dcm4chee.
	public static int sendHL7Worklist(String hl7blob) {
		String serverIP = Utils.serversAddress();
		String input[] = { "-c", serverIP.substring(7) + ":2575", hl7blob };
		//String input[]={"--help"};
		int result = HL7Snd.main(input);
		return result;
	}
	
	static RadiologyService service() {
		return Context.getService(RadiologyService.class);
	}
	
}
