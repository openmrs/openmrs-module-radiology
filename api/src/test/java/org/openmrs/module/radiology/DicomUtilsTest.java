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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.test.Verifies;

/**
 * Tests {@link DicomUtils}
 */
public class DicomUtilsTest {
	
	/**
	 * @see {@link DicomUtils#getTruncatedPriority(RequestedProcedurePriority)}
	 */
	@Test
	@Verifies(value = "should return hl7 common order priority given requested procedure priority", method = "getTruncatedPriority(RequestedProcedurePriority)")
	public void getTruncatedPriority_shouldReturnHL7CommonOrderPriorityGivenRequestedProcedurePriority() {
		
		assertEquals("S", DicomUtils.getTruncatedPriority(RequestedProcedurePriority.STAT));
		assertEquals("A", DicomUtils.getTruncatedPriority(RequestedProcedurePriority.HIGH));
		assertEquals("R", DicomUtils.getTruncatedPriority(RequestedProcedurePriority.ROUTINE));
		assertEquals("T", DicomUtils.getTruncatedPriority(RequestedProcedurePriority.MEDIUM));
		assertEquals("R", DicomUtils.getTruncatedPriority(RequestedProcedurePriority.LOW));
	}
	
	/**
	 * @see {@link DicomUtils#getTruncatedPriority(RequestedProcedurePriority)}
	 */
	@Test
	@Verifies(value = "should return default hl7 common order priority given null", method = "getTruncatedPriority(RequestedProcedurePriority)")
	public void getTruncatedPriority_shouldReturnDefaultHL7CommonOrderPriorityGivenNull() {
		
		assertEquals("R", DicomUtils.getTruncatedPriority(null));
	}
	
	/**
	 * @see {@link DicomUtils#getORCtype(MwlStatus, OrderRequest)}
	 */
	@Test
	@Verifies(value = "should return hl7 order control given mwlstatus and orderrequest", method = "getCommonOrderControlFrom(MwlStatus, OrderRequest)")
	public void getORCtype_shouldReturnHL7OrderControlGivenMwlstatusAndOrderRequest() {
		
		assertEquals("NW", DicomUtils.getORCtype(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Save_Order));
		assertEquals("NW", DicomUtils.getORCtype(MwlStatus.SAVE_ERR, DicomUtils.OrderRequest.Save_Order));
		assertEquals("XO", DicomUtils.getORCtype(MwlStatus.SAVE_OK, DicomUtils.OrderRequest.Save_Order));
		assertEquals("XO", DicomUtils.getORCtype(MwlStatus.UPDATE_OK, DicomUtils.OrderRequest.Save_Order));
		assertEquals("CA", DicomUtils.getORCtype(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Void_Order));
		assertEquals("CA", DicomUtils.getORCtype(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Void_Order));
		assertEquals("CA", DicomUtils.getORCtype(MwlStatus.SAVE_OK, DicomUtils.OrderRequest.Void_Order));
		assertEquals("NW", DicomUtils.getORCtype(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Unvoid_Order));
		assertEquals("NW", DicomUtils.getORCtype(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Unvoid_Order));
		assertEquals("NW", DicomUtils.getORCtype(MwlStatus.SAVE_OK, DicomUtils.OrderRequest.Unvoid_Order));
		assertEquals("CA", DicomUtils.getORCtype(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Discontinue_Order));
		assertEquals("CA", DicomUtils.getORCtype(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Discontinue_Order));
		assertEquals("CA", DicomUtils.getORCtype(MwlStatus.SAVE_OK, DicomUtils.OrderRequest.Discontinue_Order));
		assertEquals("NW", DicomUtils.getORCtype(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Undiscontinue_Order));
		assertEquals("NW", DicomUtils.getORCtype(MwlStatus.DEFAULT, DicomUtils.OrderRequest.Undiscontinue_Order));
		assertEquals("NW", DicomUtils.getORCtype(MwlStatus.SAVE_OK, DicomUtils.OrderRequest.Undiscontinue_Order));
	}
}
