/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.modality.web;

import org.openmrs.api.APIException;
import org.openmrs.module.radiology.modality.RadiologyModality;
import org.openmrs.module.radiology.modality.RadiologyModalityService;
import org.openmrs.module.radiology.modality.RadiologyModalityValidator;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for the form handling entry, display, discontinuation of {@code RadiologyModality's}.
 */
@Controller
@RequestMapping(RadiologyModalityFormController.RADIOLOGY_MODALITY_FORM_REQUEST_MAPPING)
public class RadiologyModalityFormController {
    
    
    public static final String RADIOLOGY_MODALITY_FORM_REQUEST_MAPPING = "/module/radiology/radiologyModality.form";
    
    static final String RADIOLOGY_MODALITY_FORM_VIEW = "/module/radiology/modalities/radiologyModalityForm";
    
    @Autowired
    private RadiologyModalityService radiologyModalityService;
    
    @Autowired
    private RadiologyModalityValidator radiologyModalityValidator;
    
    /**
     * Handles requests for a new {@code RadiologyModality}.
     * 
     * @return model and view containing new radiology modality
     * @should populate model and view with new radiology modality
     */
    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView getRadiologyModality() {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_MODALITY_FORM_VIEW);
        modelAndView.addObject(new RadiologyModality());
        return modelAndView;
    }
    
    /**
     * Handles requests for getting existing {@code RadiologyModality's}.
     * 
     * @param radiologyModality the existing radiology modality which should be returned
     * @return model and view containing radiology modality
     * @should populate model and view with given radiology modality
     */
    @RequestMapping(method = RequestMethod.GET, params = "modalityId")
    protected ModelAndView getRadiologyModality(@RequestParam("modalityId") RadiologyModality radiologyModality) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_MODALITY_FORM_VIEW);
        modelAndView.addObject(radiologyModality);
        return modelAndView;
    }
    
    /**
     * Handles requests for saving a new {@code RadiologyModality}.
     *
     * @param request the http servlet request issued to save the radiology modality
     * @param radiologyModality the radiology modality to be saved
     * @param resultRadiologyModality the binding result for given radiology modality
     * @return the model and view for the radiology modality form containing binding result errors if given radiology modality is
     *         not valid
     * @should save given radiology modality if valid and set http session attribute openmrs message to modality saved and redirect
     *         to the new radiology modality
     * @should not save given radiology modality if it is not valid and not redirect
     * @should not redirect and set session attribute with openmrs error if api exception is thrown by save radiology modality
     */
    @RequestMapping(method = RequestMethod.POST, params = "saveRadiologyModality")
    protected ModelAndView saveRadiologyModality(HttpServletRequest request,
            @ModelAttribute RadiologyModality radiologyModality, BindingResult resultRadiologyModality) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_MODALITY_FORM_VIEW);
        
        radiologyModalityValidator.validate(radiologyModality, resultRadiologyModality);
        if (resultRadiologyModality.hasErrors()) {
            modelAndView.addObject(radiologyModality);
            return modelAndView;
        }
        
        try {
            radiologyModalityService.saveRadiologyModality(radiologyModality);
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.RadiologyModality.saved");
            modelAndView.setViewName(
                "redirect:" + RADIOLOGY_MODALITY_FORM_REQUEST_MAPPING + "?modalityId=" + radiologyModality.getModalityId());
            return modelAndView;
        }
        catch (APIException apiException) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, apiException.getMessage());
        }
        
        modelAndView.addObject(radiologyModality);
        return modelAndView;
    }
    
    /**
     * Handles requests for retiring a {@code RadiologyModality}.
     *
     * @param request the http servlet request issued to retire the radiology modality
     * @param radiologyModality the radiology modality to be retired
     * @param resultRadiologyModality the binding result for given radiology modality
     * @return the model and view for the radiology modality form containing binding result errors if given radiology modality is
     *         not valid
     * @should retire given radiology modality if valid and set http session attribute openmrs message to modality retired and redirect
     *         to the radiology modality
     * @should not retire given radiology modality if it is not valid and not redirect
     * @should not redirect and set session attribute with openmrs error if api exception is thrown by retire radiology modality
     */
    @RequestMapping(method = RequestMethod.POST, params = "retireRadiologyModality")
    protected ModelAndView retireRadiologyModality(HttpServletRequest request,
            @ModelAttribute RadiologyModality radiologyModality, BindingResult resultRadiologyModality) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_MODALITY_FORM_VIEW);
        
        radiologyModalityValidator.validate(radiologyModality, resultRadiologyModality);
        if (resultRadiologyModality.hasErrors()) {
            modelAndView.addObject(radiologyModality);
            return modelAndView;
        }
        
        try {
            radiologyModalityService.retireRadiologyModality(radiologyModality, radiologyModality.getRetireReason());
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.RadiologyModality.retired");
            modelAndView.setViewName(
                "redirect:" + RADIOLOGY_MODALITY_FORM_REQUEST_MAPPING + "?modalityId=" + radiologyModality.getModalityId());
            return modelAndView;
        }
        catch (APIException apiException) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, apiException.getMessage());
        }
        
        modelAndView.addObject(radiologyModality);
        return modelAndView;
    }
}
