/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.v231.message;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertNotNull;
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
import org.openmrs.module.radiology.hl7.HL7Constants;
import org.openmrs.module.radiology.hl7.custommodel.v231.message.ORM_O01;
import org.openmrs.module.radiology.hl7.v231.code.OrderControlElement;

import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests {@link RadiologyORMO01}
 */
public class RadiologyORMO01Test {
	
	RadiologyORMO01 radiologyORMO01;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private Patient patient = null;
	
	private Study study = null;
	
	private RadiologyOrder radiologyOrder = null;
	
	@Before
	public void runBeforeEachTest() throws Exception {
		
		radiologyORMO01 = new RadiologyORMO01();
		
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
	 * @see RadiologyORMO01#createMessage(RadiologyOrder,OrderControlElement)
	 * @verifies create new ormo01 object given all params
	 */
	@Test
	public void createMessage_shouldCreateNewOrmo01ObjectGivenAllParams() throws Exception {
		
		ORM_O01 radiologyOrderMessage = radiologyORMO01.createMessage(radiologyOrder, OrderControlElement.NEW_ORDER);
		
		assertNotNull(radiologyOrderMessage);
		
		String encodedOrmMessage = PipeParser.encode(radiologyOrderMessage, HL7Constants.ENCODING_CHARACTERS);
		assertThat(encodedOrmMessage, startsWith("MSH|" + HL7Constants.ENCODING_CHARACTERS
				+ "|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
			encodedOrmMessage,
			endsWith("||ORM^O01||P|2.3.1\r"
					+ "PID|||100||Doe^John^Francis||19500401000000|M\r"
					+ "ORC|NW|ORD-20|||||^^^20150204143500^^T\r"
					+ "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||ORD-20|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
					+ "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
	}
	
	/**
	 * @see RadiologyORMO01#createMessage(RadiologyOrder,OrderControlElement)
	 * @verifies throw illegal argument exception given null as radiologyOrder
	 */
	@Test
	public void createMessage_shouldThrowIllegalArgumentExceptionGivenNullAsRadiologyOrder() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("radiologyOrder cannot be null."));
		radiologyORMO01.createMessage(null, OrderControlElement.NEW_ORDER);
	}
	
	/**
	 * @see RadiologyORMO01#createMessage(RadiologyOrder,OrderControlElement)
	 * @verifies throw illegal argument exception if given radiology orders study is null
	 */
	@Test
	public void createMessage_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrdersStudyIsNull() throws Exception {
		
		radiologyOrder.setStudy(null);
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("radiologyOrder.study cannot be null."));
		radiologyORMO01.createMessage(radiologyOrder, OrderControlElement.NEW_ORDER);
	}
	
	/**
	 * @see RadiologyORMO01#createMessage(RadiologyOrder,OrderControlElement)
	 * @verifies throw illegal argument exception given null as orderControlElement
	 */
	@Test
	public void createMessage_shouldThrowIllegalArgumentExceptionGivenNullAsOrderControlElement() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("orderControlElement cannot be null."));
		radiologyORMO01.createMessage(radiologyOrder, null);
	}
	
	/**
	 * @see RadiologyORMO01#createEncodedMessage(RadiologyOrder,OrderControlElement)
	 * @verifies create new encoded ormo01 object given all params
	 */
	@Test
	public void createEncodedMessage_shouldCreateNewEncodedOrmo01ObjectGivenAllParams() throws Exception {
		
		String encodedOrmMessage = radiologyORMO01.createEncodedMessage(radiologyOrder, OrderControlElement.NEW_ORDER);
		
		assertThat(encodedOrmMessage, startsWith("MSH|" + HL7Constants.ENCODING_CHARACTERS
				+ "|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
			encodedOrmMessage,
			endsWith("||ORM^O01||P|2.3.1\r"
					+ "PID|||100||Doe^John^Francis||19500401000000|M\r"
					+ "ORC|NW|ORD-20|||||^^^20150204143500^^T\r"
					+ "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||ORD-20|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
					+ "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
	}
	
	/**
	 * @see RadiologyORMO01#createEncodedMessage(RadiologyOrder,OrderControlElement)
	 * @verifies throw illegal argument exception given null as radiologyOrder
	 */
	@Test
	public void createEncodedMessage_shouldThrowIllegalArgumentExceptionGivenNullAsRadiologyOrder() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("radiologyOrder cannot be null."));
		radiologyORMO01.createEncodedMessage(null, OrderControlElement.NEW_ORDER);
	}
	
	/**
	 * @see RadiologyORMO01#createEncodedMessage(RadiologyOrder,OrderControlElement)
	 * @verifies throw illegal argument exception if given radiology orders study is null
	 */
	@Test
	public void createEncodedMessage_shouldThrowIllegalArgumentExceptionIfGivenRadiologyOrdersStudyIsNull() throws Exception {
		
		radiologyOrder.setStudy(null);
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("radiologyOrder.study cannot be null."));
		radiologyORMO01.createEncodedMessage(radiologyOrder, OrderControlElement.NEW_ORDER);
	}
	
	/**
	 * @see RadiologyORMO01#createEncodedMessage(RadiologyOrder,OrderControlElement)
	 * @verifies throw illegal argument exception given null as orderControlElement
	 */
	@Test
	public void createEncodedMessage_shouldThrowIllegalArgumentExceptionGivenNullAsOrderControlElement() throws Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("orderControlElement cannot be null."));
		radiologyORMO01.createEncodedMessage(radiologyOrder, null);
	}
}
