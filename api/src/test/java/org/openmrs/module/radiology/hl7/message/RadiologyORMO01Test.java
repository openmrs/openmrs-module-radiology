/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.message;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.CommonOrderPriority;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests {@link RadiologyORMO01}
 */
public class RadiologyORMO01Test {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private static final EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	private Patient patient = null;
	
	private Study study = null;
	
	private RadiologyOrder radiologyOrder = null;
	
	@Before
	public void runBeforeEachTest() throws Exception {
		
		patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		patient.setBirthdate(calendar.getTime());
		
		radiologyOrder = new RadiologyOrder();
		radiologyOrder.setOrderId(20);
		
		Field orderNumber = Order.class.getDeclaredField("orderNumber");
		orderNumber.setAccessible(true);
		orderNumber.set(radiologyOrder, "ORD-" + radiologyOrder.getOrderId());
		
		radiologyOrder.setPatient(patient);
		calendar.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		radiologyOrder.setScheduledDate(calendar.getTime());
		radiologyOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
		radiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		study = new Study();
		study.setStudyId(1);
		study.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1");
		study.setModality(Modality.CT);
		radiologyOrder.setStudy(study);
	}
	
	/**
	 * @see {@link RadiologyORMO01#getRadiologyORMO01Message(RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should return ormo01 message given all params", method = "getRadiologyORMO01Message(RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)")
	public void getRadiologyORMO01Message_shouldReturnORMO01MessageGivenAllParams() throws HL7Exception {
		
		ORM_O01 ormMessage = RadiologyORMO01.getRadiologyORMO01Message(radiologyOrder, CommonOrderOrderControl.NEW_ORDER,
		    CommonOrderPriority.STAT);
		
		String encodedOrmMessage = PipeParser.encode(ormMessage, encodingCharacters);
		assertThat(encodedOrmMessage, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    encodedOrmMessage,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|NW|ORD-20|||||^^^20150204143500^^S\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||ORD-20|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
		
	}
	
	/**
	 * @see {@link RadiologyORMO01#getRadiologyORMO01Message(RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null as radiology order", method = "getRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)")
	public void getRadiologyORMO01Message_shouldThrowIllegalArgumentExceptionGivenNullAsRadiologyOrder() throws HL7Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("radiologyOrder cannot be null."));
		RadiologyORMO01.getRadiologyORMO01Message(null, CommonOrderOrderControl.NEW_ORDER, CommonOrderPriority.STAT);
	}
	
	/**
	 * @see {@link RadiologyORMO01#getRadiologyORMO01Message(RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception if given radiology orders study is null", method = "getRadiologyORMO01Message(RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)")
	public void getRadiologyORMO01Message_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrdersStudyIsNull()
	        throws HL7Exception {
		
		radiologyOrder.setStudy(null);
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("radiologyOrder.study cannot be null."));
		RadiologyORMO01.getRadiologyORMO01Message(radiologyOrder, CommonOrderOrderControl.NEW_ORDER,
		    CommonOrderPriority.STAT);
	}
	
	/**
	 * @see {@link RadiologyORMO01#getRadiologyORMO01Message(RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null as order control code", method = "getRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)")
	public void getRadiologyORMO01Message_shouldThrowIllegalArgumentExceptionGivenNullAsOrderControlCode()
	        throws HL7Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("orderControlCode cannot be null."));
		RadiologyORMO01.getRadiologyORMO01Message(radiologyOrder, null, CommonOrderPriority.STAT);
	}
	
	/**
	 * @see {@link RadiologyORMO01#getRadiologyORMO01Message(RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null as order control priority", method = "getRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)")
	public void getRadiologyORMO01Message_shouldThrowIllegalArgumentExceptionGivenNullAsOrderControlPriority()
	        throws HL7Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("orderControlPriority cannot be null."));
		RadiologyORMO01.getRadiologyORMO01Message(radiologyOrder, CommonOrderOrderControl.NEW_ORDER, null);
	}
}
