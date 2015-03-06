package org.openmrs.module.radiology.web;

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
