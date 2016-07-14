package org.openmrs.module.radiology.report.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.openmrs.web.controller.PortletController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the radiology reports tab portlet.
 */
@Controller
@RequestMapping("**/radiologyReportsTab.portlet")
public class RadiologyReportsTabPortletController extends PortletController {
    
    
    /**
     * @see org.openmrs.web.controller.PortletController#populateModel(javax.servlet.http.HttpServletRequest,
     *      java.util.Map)
     */
    @Override
    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
        
        final List<String> radiologyReportStatuses = new LinkedList<String>();
        radiologyReportStatuses.add("");
        
        for (final RadiologyReportStatus status : RadiologyReportStatus.values()) {
            radiologyReportStatuses.add(status.name());
        }
        
        model.put("radiologyReportStatuses", radiologyReportStatuses);
    }
}
