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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StaticPagesController {
	
	/**
	 * Called resources because returns a path /module/radiology/resources/+path
	 * Example path /module/radiology/static/felix-config.list
	 * @param path extracted from URL /module/radiology/static/xxx.list 
	 * @return ModelAndView with the view pointing to the file /web/module/resources/xxx.jsp
	 */
	@RequestMapping("/module/radiology/static/{path}")
	ModelAndView resources(@PathVariable("path") String path) {
		ModelAndView mav = new ModelAndView("/module/radiology/resources/" + path);
		return mav;
	}
}
