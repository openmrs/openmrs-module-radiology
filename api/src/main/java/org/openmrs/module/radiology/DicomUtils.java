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
import java.util.Calendar;
import java.util.Date;

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
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.SpecificCharacterSet;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.ContentHandlerAdapter;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.SAXWriter;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Study.Modality;
import org.openmrs.module.radiology.Study.PerformedStatuses;
import org.openmrs.module.radiology.Study.ScheduledStatuses;
import org.xml.sax.SAXException;

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
	 * @throws Exception
	 *             multiple ones, but just handled as one in the controller
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
		spss.putString(Tag.Modality, VR.CS, Modality.values()[s.getModality()].toString());
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
	 * Updates database with mpps object o
	 * 
	 * @param o
	 */
	public static void writeMpps(DicomObject o) {
		try {
			
			SpecificCharacterSet scs = new SpecificCharacterSet(Utils.specificCharacterSet());
			
			// Save Study
			
			int[] studyUIDPath = { Tag.ScheduledStepAttributesSequence, Tag.StudyInstanceUID };
			String studyUID = o.get(studyUIDPath[0]).getDicomObject().get(studyUIDPath[1]).getValueAsString(scs, 0);
			
			String[] uidSplit = studyUID.split("[.]");
			int id = Integer.parseInt(uidSplit[uidSplit.length - 1]);
			Study s = service().getStudy(id);
			debug(s.toString());
			String pStatus = o.get(Tag.PerformedProcedureStepStatus).getValueAsString(scs, 0);
			s.setPerformedStatus(PerformedStatuses.value(pStatus));
			service().saveStudy(s);
			log.info("Received Update from dcm4chee. Updating Performed Procedure Step Status for study :" + studyUID
			        + " to Status : " + PerformedStatuses.value(pStatus));
			
		}
		catch (NumberFormatException e) {
			log.error("Number can not be parsed");
		}
		catch (Exception e) {
			log.error("Error : " + e.getMessage());
		}
		
	}
	
	public enum OrderRequest {
		Default, Save_Order, Void_Order, Discontinue_Order, Undiscontinue_Order, Unvoid_Order;
	}
	
	// Create HL7 ORU message to create worklist request.
	public static String createHL7Message(Study study, Order order, OrderRequest orderRequest) {
		// Example HL7Message to create order
		//  String hl7blob= "MSH|^~\\&|OpenMRSRadiologyModule|MyHospital|||201308271545||ORM^O01||P|2.3||||||encoding|\n" +
		//                            "PID|||100-07||Patient^D^Johnny||19651007|M||||||||\n" +
		//                            "ORC|NW|2|||||^^^201308241700^^R||||||\n" +
		//                            "OBR||||^^^^knee|||||||||||||||2|2||||CT||||||||||||||||||||^knee^scan|\n" +
		//                            "ZDS|1.2.826.0.1.3680043.8.2186.1.2|";
		Integer mwlstatus = study.getMwlStatus();
		String orcfield1 = getORCtype(mwlstatus, orderRequest);
		String msh = "MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||" + Utils.time(new Date())
		        + "||ORM^O01||P|2.3||||||encoding|\n";
		String pid = "PID|||" + order.getPatient().getPatientIdentifier().getIdentifier() + "||"
		        + order.getPatient().getPersonName().getFullName().replace(' ', '^') + "||"
		        + Utils.plain(order.getPatient().getBirthdate()) + "|" + order.getPatient().getGender() + "||||||||\n";
		String orc = "ORC|" + orcfield1 + "|" + study.getId() + "|||||^^^" + Utils.plain(order.getStartDate()) + "^^"
		        + getTruncatedPriority(study.getPriority()) + "||||||\n";
		String obr = "OBR||||^^^^" + order.getInstructions() + "|||||||||||||||" + study.getId() + "|" + study.getId()
		        + "||||" + Modality.values()[study.getModality()].toString() + "||||||||||||||||||||"
		        + order.getInstructions() + "|\n";
		String zds = "ZDS|" + study.getUid() + "|";
		String hl7blob = msh + pid + orc + obr + zds;
		System.out.println("Created Request \n" + hl7blob);
		return hl7blob;
	}
	
	// Create Order Type for the order to be filled in the HL7 Message in the ORC-1 field
	
	public static String getORCtype(Integer mwlstatus, OrderRequest orderRequest) {
		String orc = new String();
		switch (orderRequest) {
			case Save_Order:
				if (mwlstatus.intValue() == 0 || mwlstatus.intValue() == 2) {
					orc = "NW";
				} else {
					orc = "XO";
				}
				break;
			case Void_Order:
				orc = "CA";
				break;
			case Unvoid_Order:
				orc = "NW";
				break;
			case Discontinue_Order:
				orc = "CA";
				break;
			case Undiscontinue_Order:
				orc = "NW";
				break;
			default:
				break;
			
		}
		return orc;
	}
	
	// Create Priority for the order to be filled in the HL7 Message
	public static String getTruncatedPriority(Integer priority) {
		String result = new String();
		switch (priority) {
			case 0:
				result = "S";
				break;
			case 1:
				result = "A";
				break;
			case 2:
				result = "R";
				break;
			case 3:
				result = "T";
				break;
			case 4:
				result = "R";
				break;
			default:
				result = "R";
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
	
	static Main service() {
		return Context.getService(Main.class);
	}
	
}
