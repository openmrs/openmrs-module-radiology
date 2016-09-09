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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.APIException;
import org.openmrs.module.radiology.modality.RadiologyModality;
import org.openmrs.module.radiology.modality.RadiologyModalityService;
import org.openmrs.module.radiology.modality.RadiologyModalityValidator;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.web.WebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests {@link RadiologyModalityFormController}.
 */
public class RadiologyModalityFormControllerTest extends BaseContextMockTest {
    
    
    @Mock
    private RadiologyModalityService radiologyModalityService;
    
    @Mock
    private RadiologyModalityValidator radiologyModalityValidator;
    
    @InjectMocks
    private RadiologyModalityFormController radiologyModalityFormController = new RadiologyModalityFormController();
    
    RadiologyModality radiologyModality;
    
    @Before
    public void setUp() {
        
        radiologyModality = new RadiologyModality();
        radiologyModality.setModalityId(1);
        radiologyModality.setAeTitle("CT01");
    }
    
    /**
     * @verifies populate model and view with new radiology modality
     * @see RadiologyModalityFormController#getRadiologyModality()
     */
    @Test
    public void getRadiologyModality_shouldPopulateModelAndViewWithNewRadiologyModality() throws Exception {
        
        ModelAndView modelAndView = radiologyModalityFormController.getRadiologyModality();
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyModalityFormController.RADIOLOGY_MODALITY_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyModality"));
        RadiologyModality modality = (RadiologyModality) modelAndView.getModelMap()
                .get("radiologyModality");
        assertNull(modality.getModalityId());
    }
    
    /**
     * @verifies populate model and view with given radiology modality
     * @see RadiologyModalityFormController#getRadiologyModality(org.openmrs.module.radiology.modality.RadiologyModality)
     */
    @Test
    public void getRadiologyModality_shouldPopulateModelAndViewWithGivenRadiologyModality() throws Exception {
        
        ModelAndView modelAndView = radiologyModalityFormController.getRadiologyModality(radiologyModality);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyModalityFormController.RADIOLOGY_MODALITY_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyModality"));
        RadiologyModality modality = (RadiologyModality) modelAndView.getModelMap()
                .get("radiologyModality");
        assertThat(modality, is(radiologyModality));
    }
    
    /**
     * @verifies save given radiology modality if valid and set http session attribute openmrs message to modality saved and redirect to the new radiology modality
     * @see RadiologyModalityFormController#saveRadiologyModality(javax.servlet.http.HttpServletRequest, org.openmrs.module.radiology.modality.RadiologyModality, org.springframework.validation.BindingResult)
     */
    @Test
    public void
            saveRadiologyModality_shouldSaveGivenRadiologyModalityIfValidAndSetHttpSessionAttributeOpenmrsMessageToModalitySavedAndRedirectToTheNewRadiologyModality()
                    throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("saveRadiologyModality", "saveRadiologyModality");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult modalityErrors = mock(BindingResult.class);
        when(modalityErrors.hasErrors()).thenReturn(false);
        
        ModelAndView modelAndView =
                radiologyModalityFormController.saveRadiologyModality(mockRequest, radiologyModality, modalityErrors);
        
        verify(radiologyModalityService, times(1)).saveRadiologyModality(radiologyModality);
        verifyNoMoreInteractions(radiologyModalityService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyModality.form?modalityId=" + radiologyModality.getModalityId()));
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR),
            is("radiology.RadiologyModality.saved"));
    }
    
    /**
     * @verifies not save given radiology modality if it is not valid and not redirect
     * @see RadiologyModalityFormController#saveRadiologyModality(javax.servlet.http.HttpServletRequest, org.openmrs.module.radiology.modality.RadiologyModality, org.springframework.validation.BindingResult)
     */
    @Test
    public void saveRadiologyModality_shouldNotSaveGivenRadiologyModalityIfItIsNotValidAndNotRedirect() throws Exception {
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("saveRadiologyModality", "saveRadiologyModality");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult modalityErrors = mock(BindingResult.class);
        when(modalityErrors.hasErrors()).thenReturn(true);
        
        ModelAndView modelAndView =
                radiologyModalityFormController.saveRadiologyModality(mockRequest, radiologyModality, modalityErrors);
        
        verifyZeroInteractions(radiologyModalityService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyModalityFormController.RADIOLOGY_MODALITY_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyModality"));
        RadiologyModality modality = (RadiologyModality) modelAndView.getModelMap()
                .get("radiologyModality");
        assertThat(modality, is(radiologyModality));
    }
    
    /**
     * @verifies not redirect and set session attribute with openmrs error if api exception is thrown by save radiology modality
     * @see RadiologyModalityFormController#saveRadiologyModality(javax.servlet.http.HttpServletRequest, org.openmrs.module.radiology.modality.RadiologyModality, org.springframework.validation.BindingResult)
     */
    @Test
    public void
            saveRadiologyModality_shouldNotRedirectAndSetSessionAttributeWithOpenmrsErrorIfApiExceptionIsThrownBySaveRadiologyModality()
                    throws Exception {
        
        when(radiologyModalityService.saveRadiologyModality(radiologyModality))
                .thenThrow(new APIException("modality related error"));
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("saveRadiologyModality", "saveRadiologyModality");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult modalityErrors = mock(BindingResult.class);
        when(modalityErrors.hasErrors()).thenReturn(false);
        
        ModelAndView modelAndView =
                radiologyModalityFormController.saveRadiologyModality(mockRequest, radiologyModality, modalityErrors);
        
        verify(radiologyModalityService, times(1)).saveRadiologyModality(radiologyModality);
        verifyNoMoreInteractions(radiologyModalityService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyModalityFormController.RADIOLOGY_MODALITY_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyModality"));
        RadiologyModality modality = (RadiologyModality) modelAndView.getModelMap()
                .get("radiologyModality");
        assertThat(modality, is(radiologyModality));
        
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR), is("modality related error"));
    }
    
    /**
     * @verifies retire given radiology modality if valid and set http session attribute openmrs message to modality retired and redirect to the radiology modality
     * @see RadiologyModalityFormController#retireRadiologyModality(javax.servlet.http.HttpServletRequest, RadiologyModality, BindingResult)
     */
    @Test
    public void
            retireRadiologyModality_shouldRetireGivenRadiologyModalityIfValidAndSetHttpSessionAttributeOpenmrsMessageToModalityRetiredAndRedirectToTheRadiologyModality()
                    throws Exception {
        
        radiologyModality.setRetireReason("out of order");
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("retireRadiologyModality", "retireRadiologyModality");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult modalityErrors = mock(BindingResult.class);
        when(modalityErrors.hasErrors()).thenReturn(false);
        
        ModelAndView modelAndView =
                radiologyModalityFormController.retireRadiologyModality(mockRequest, radiologyModality, modalityErrors);
        
        verify(radiologyModalityService, times(1)).retireRadiologyModality(radiologyModality,
            radiologyModality.getRetireReason());
        verifyNoMoreInteractions(radiologyModalityService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyModality.form?modalityId=" + radiologyModality.getModalityId()));
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR),
            is("radiology.RadiologyModality.retired"));
    }
    
    /**
     * @verifies not retire given radiology modality if it is not valid and not redirect
     * @see RadiologyModalityFormController#retireRadiologyModality(javax.servlet.http.HttpServletRequest, RadiologyModality, BindingResult)
     */
    @Test
    public void retireRadiologyModality_shouldNotRetireGivenRadiologyModalityIfItIsNotValidAndNotRedirect()
            throws Exception {
        
        radiologyModality.setRetireReason(null);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("retireRadiologyModality", "retireRadiologyModality");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult modalityErrors = mock(BindingResult.class);
        when(modalityErrors.hasErrors()).thenReturn(true);
        
        ModelAndView modelAndView =
                radiologyModalityFormController.retireRadiologyModality(mockRequest, radiologyModality, modalityErrors);
        
        verifyZeroInteractions(radiologyModalityService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyModalityFormController.RADIOLOGY_MODALITY_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyModality"));
        RadiologyModality modality = (RadiologyModality) modelAndView.getModelMap()
                .get("radiologyModality");
        assertThat(modality, is(radiologyModality));
    }
    
    /**
     * @verifies not redirect and set session attribute with openmrs error if api exception is thrown by retire radiology modality
     * @see RadiologyModalityFormController#retireRadiologyModality(javax.servlet.http.HttpServletRequest, RadiologyModality, BindingResult)
     */
    @Test
    public void
            retireRadiologyModality_shouldNotRedirectAndSetSessionAttributeWithOpenmrsErrorIfApiExceptionIsThrownByRetireRadiologyModality()
                    throws Exception {
        
        radiologyModality.setRetireReason("out of order");
        
        when(radiologyModalityService.retireRadiologyModality(radiologyModality, radiologyModality.getRetireReason()))
                .thenThrow(new APIException("modality related error"));
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("saveRadiologyModality", "saveRadiologyModality");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult modalityErrors = mock(BindingResult.class);
        when(modalityErrors.hasErrors()).thenReturn(false);
        
        ModelAndView modelAndView =
                radiologyModalityFormController.retireRadiologyModality(mockRequest, radiologyModality, modalityErrors);
        
        verify(radiologyModalityService, times(1)).retireRadiologyModality(radiologyModality,
            radiologyModality.getRetireReason());
        verifyNoMoreInteractions(radiologyModalityService);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyModalityFormController.RADIOLOGY_MODALITY_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyModality"));
        RadiologyModality modality = (RadiologyModality) modelAndView.getModelMap()
                .get("radiologyModality");
        assertThat(modality, is(radiologyModality));
        
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR), is("modality related error"));
    }
}
