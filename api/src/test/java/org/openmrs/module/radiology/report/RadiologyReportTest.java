/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.module.radiology.RadiologyOrder;

public class RadiologyReportTest {
	
	private RadiologyOrder radiologyOrder;
	
	private Provider provider1;
	
	private Provider provider2;
	
	static OrderType radiologyOrderType = new OrderType("Radiology Order", "Order type for radiology exams",
	        "org.openmrs.module.radiology.RadiologyOrder");
	
	@Before
	public void setUp() {
		provider1 = new Provider();
		provider1.setId(1);
		provider1.setName("doctor");
		provider2 = new Provider();
		provider2.setId(2);
		provider2.setName("Nurse");
		radiologyOrder = new RadiologyOrder();
		radiologyOrder.setOrderId(1);
		radiologyOrder.setOrderType(radiologyOrderType);
		radiologyOrder.setPatient(new Patient());
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		radiologyOrder.setScheduledDate(calendar.getTime());
		radiologyOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
		radiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		radiologyOrder.setVoided(false);
		radiologyOrder.setStudy(null);
	}
	
	/**
	 * @see RadiologyReport#setPrincipalResultsInterpreter(Provider)
	 * @verifies set provider for RadiologyReport if status is not discontinued and given provider
	 *           is not null
	 */
	@Test
	public void setPrincipalResultsInterpreter_shouldSetPrincipalResultsInterpreterForRadiologyReportIfStatusIsNotDiscontinuedAndGivenProviderIsNotNull()
	        throws Exception {
		RadiologyReport report = new RadiologyReport(radiologyOrder);
		report.setPrincipalResultsInterpreter(provider2);
		assertThat(report.getPrincipalResultsInterpreter(), is(provider2));
	}
	
	/**
	 * @see RadiologyReport#setPrincipalResultsInterpreter(Provider) (Provider)
	 * @verifies not set provider if given provider is null
	 */
	@Test
	public void setProvider_shouldNotSetProviderIfGivenProviderIsNull() throws Exception {
		RadiologyReport report = new RadiologyReport(radiologyOrder);
		
		try {
			report.setPrincipalResultsInterpreter(null);
		}
		catch (IllegalArgumentException e) {
			assertThat(report.getPrincipalResultsInterpreter(), is(provider1));
		}
	}
	
	/**
	 * @see RadiologyReport#setReportDate(java.util.Date)
	 * @verifies set completionDate for RadiologyReport if status is not discontinued and given
	 *           completionDate is not null
	 */
	@Test
	public void setCompletionDate_shouldSetCompletionDateForRadiologyReportIfStatusIsNotDiscontinuedAndGivenCompletionDateIsNotNull()
	        throws Exception {
		RadiologyReport report = new RadiologyReport(radiologyOrder);
		Date date = new Date();
		report.setReportDate(date);
		assertThat(report.getReportDate(), is(date));
	}
	
	/**
	 * @see RadiologyReport#setReportDate(java.util.Date)
	 * @verifies not set completionDate if given completionDate is null
	 */
	@Test
	public void setCompletionDate_shouldNotSetCompletionDateIfGivenCompletionDateIsNull() throws Exception {
		RadiologyReport report = new RadiologyReport(radiologyOrder);
		report.setReportDate(new Date());
		Date compDate = report.getReportDate();
		try {
			report.setReportDate(null);
		}
		catch (IllegalArgumentException e) {
			assertThat(report.getReportDate(), is(compDate));
		}
	}
}
