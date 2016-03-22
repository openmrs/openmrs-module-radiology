/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(RadiologyOrderListController.RADIOLOGY_ORDER_LIST_REQUEST_MAPPING)
public class RadiologyOrderListController {
	
	protected static final String RADIOLOGY_ORDER_LIST_REQUEST_MAPPING = "/module/radiology/radiologyOrder.list";
	
	private static final String RADIOLOGY_ORDER_LIST_VIEW = "/module/radiology/radiologyOrderList";
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView handleRequest() {
		final ModelAndView mav = new ModelAndView(RADIOLOGY_ORDER_LIST_VIEW);
		return mav;
	}
}
