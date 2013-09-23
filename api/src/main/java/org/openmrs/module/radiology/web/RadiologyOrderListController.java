package org.openmrs.module.radiology.web;

import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Activator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/radiology/radiologyOrder.list")
public class RadiologyOrderListController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView handleRequest() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("module/radiology/radiologyOrderList");
		if(Activator.badInit(Context.getUserService(),Context.getOrderService())){
			mav.addObject("initialized","fail");
		}
		return mav;
	}
}
