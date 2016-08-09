/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.radiology.report.template.MrrtReportTemplate;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateService;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the form handling display of {@code MrrtReportTemplate's}.
 */
@Controller
@RequestMapping(MrrtReportTemplateFormController.MRRT_REPORT_TEMPLATE_FORM_REQUEST_MAPPING)
public class MrrtReportTemplateFormController {
    
    
    protected static final String MRRT_REPORT_TEMPLATE_FORM_REQUEST_MAPPING = "/module/radiology/mrrtReportTemplate.form";
    
    static final String MRRT_REPORT_TEMPLATE_FORM_VIEW = "/module/radiology/reports/templates/mrrtReportTemplateForm";
    
    @Autowired
    private MrrtReportTemplateService mrrtReportTemplateService;
    
    /**
     * Handles request for view an {@code MrrtReportTemplate}.
     * 
     * @param request
     *            the the HttpServletRequest to view MrrtReportTemplates
     * @param mrrtReportTemplate
     *            the MrrtReportTemplate being requested
     * @return modelAndView of the report template form page containing the body content of template inside a model object
     * @should return the model and view of the report template form page containing template body in model object
     * @should return the model and view of the radiology dashboard page with error message if io exception is thrown
     */
    @RequestMapping(method = RequestMethod.GET, params = "templateId")
    public ModelAndView displayMrrtReportTemplate(HttpServletRequest request,
            @RequestParam("templateId") MrrtReportTemplate mrrtReportTemplate) {
        
        final ModelAndView modelAndView = new ModelAndView(MRRT_REPORT_TEMPLATE_FORM_VIEW);
        
        try {
            modelAndView.addObject("templateBody",
                mrrtReportTemplateService.getMrrtReportTemplateHtmlBody(mrrtReportTemplate));
            modelAndView.addObject("template", mrrtReportTemplate);
        }
        catch (IOException exception) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
                        "Error occured while dispaying template => " + exception.getMessage());
            return new ModelAndView("/module/radiology/radiologyDashboardForm");
        }
        return modelAndView;
    }
}
