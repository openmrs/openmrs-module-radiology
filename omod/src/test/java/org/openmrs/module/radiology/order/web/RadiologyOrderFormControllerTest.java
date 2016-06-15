/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.order.web;

import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.OrderService;
import org.openmrs.module.radiology.RadiologyProperties;
import org.openmrs.module.radiology.dicom.DicomWebViewer;
import org.openmrs.module.radiology.dicom.code.PerformedProcedureStepStatus;
import org.openmrs.module.radiology.order.RadiologyOrder;
import org.openmrs.module.radiology.order.RadiologyOrderService;
import org.openmrs.module.radiology.report.RadiologyReport;
import org.openmrs.module.radiology.report.RadiologyReportService;
import org.openmrs.module.radiology.report.RadiologyReportStatus;
import org.openmrs.module.radiology.test.RadiologyTestData;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.test.Verifies;
import org.openmrs.web.WebConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * Tests {@link RadiologyOrderFormController}
 */
public class RadiologyOrderFormControllerTest extends BaseContextMockTest {
    
    
    @Mock
    private OrderService orderService;
    
    @Mock
    private RadiologyOrderService radiologyOrderService;
    
    @Mock
    private RadiologyReportService radiologyReportService;
    
    @Mock
    private RadiologyProperties radiologyProperties;
    
    @Mock
    private DicomWebViewer dicomWebViewer;
    
    @InjectMocks
    private RadiologyOrderFormController radiologyOrderFormController = new RadiologyOrderFormController();
    
    private Method radiologyReportNeedsToBeCreatedMethod = null;
    
    @Before
    public void setUp() throws Exception {
        when(radiologyProperties.getRadiologyTestOrderType()).thenReturn(RadiologyTestData.getMockRadiologyOrderType());
        
        radiologyReportNeedsToBeCreatedMethod =
                RadiologyOrderFormController.class.getDeclaredMethod("radiologyReportNeedsToBeCreated",
                    new Class[] { org.springframework.web.servlet.ModelAndView.class, org.openmrs.Order.class });
        radiologyReportNeedsToBeCreatedMethod.setAccessible(true);
    }
    
    /**
     * @see RadiologyOrderFormController#getRadiologyOrderFormWithNewRadiologyOrder()
     */
    @Test
    @Verifies(value = "should populate model and view with new radiology order",
            method = "getRadiologyOrderFormWithNewRadiologyOrder()")
    public void getRadiologyOrderFormWithNewRadiologyOrder_shouldPopulateModelAndViewWithNewRadiologyOrder()
            throws Exception {
        
        ModelAndView modelAndView = radiologyOrderFormController.getRadiologyOrderFormWithNewRadiologyOrder();
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertNull(order.getOrderId());
        
        assertNotNull(order.getStudy());
        assertNull(order.getStudy()
                .getStudyId());
        
        assertNull(order.getOrderer());
    }
    
    /**
     * @see RadiologyOrderFormController#getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(Integer)
     */
    @Test
    @Verifies(value = "should populate model and view with new radiology order prefilled with given patient",
            method = "getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(Integer)")
    public void
            getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient_shouldPopulateModelAndViewWithNewRadiologyOrderPrefilledWithGivenPatient()
                    
                    throws Exception {
        
        // given
        Patient mockPatient = RadiologyTestData.getMockPatient1();
        
        ModelAndView modelAndView =
                radiologyOrderFormController.getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(mockPatient);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertNull(order.getOrderId());
        
        assertNotNull(order.getStudy());
        assertNull(order.getStudy()
                .getStudyId());
        
        assertNotNull(order.getPatient());
        assertThat(order.getPatient(), is(mockPatient));
        
        assertThat(modelAndView.getModelMap(), hasKey("patientId"));
        Integer patientId = (Integer) modelAndView.getModelMap()
                .get("patientId");
        assertThat(patientId, is(mockPatient.getPatientId()));
    }
    
    /**
     * @see RadiologyOrderFormController#getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(Integer)
     */
    @Test
    @Verifies(
            value = "should populate model and view with new radiology order without prefilled patient if given patient is null",
            method = "getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(Integer)")
    public void
            getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient_shouldPopulateModelAndViewWithNewRadiologyOrderWithoutPrefilledPatientIfGivenPatientIsNull()
                    throws Exception {
        
        ModelAndView modelAndView =
                radiologyOrderFormController.getRadiologyOrderFormWithNewRadiologyOrderAndPrefilledPatient(null);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertNull(order.getOrderId());
        
        assertNotNull(order.getStudy());
        assertNull(order.getStudy()
                .getStudyId());
        
        assertNull(order.getPatient());
        
        assertThat(modelAndView.getModelMap(), not(hasKey("patientId")));
    }
    
    /**
     * @see RadiologyOrderFormController#getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(Order)
     * @verifies populate model and view with existing radiology order if given order id matches a
     *           radiology order and no dicomViewerUrl if order is not completed
     */
    @Test
    public void
            getRadiologyOrderFormWithExistingRadiologyOrderByOrderId_shouldPopulateModelAndViewWithExistingRadiologyOrderIfGivenOrderIdMatchesARadiologyOrderAndNoDicomViewerUrlIfOrderIsNotCompleted()
                    throws Exception {
        
        // given
        RadiologyOrder mockRadiologyOrderInProgress = RadiologyTestData.getMockRadiologyOrder1();
        mockRadiologyOrderInProgress.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        
        ModelAndView modelAndView = radiologyOrderFormController
                .getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(mockRadiologyOrderInProgress);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("order"));
        RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap()
                .get("order");
        assertThat(order, is(mockRadiologyOrderInProgress));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertThat(radiologyOrder, is(mockRadiologyOrderInProgress));
        
        assertThat(modelAndView.getModelMap(), not(hasKey("dicomViewerUrl")));
    }
    
    /**
     * @see RadiologyOrderFormController#getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(Order)
     * @verifies populate model and view with existing radiology order if given order id matches a
     *           radiology order and dicomViewerUrl if order completed
     */
    @Test
    public void
            getRadiologyOrderFormWithExistingRadiologyOrderByOrderId_shouldPopulateModelAndViewWithExistingRadiologyOrderIfGivenOrderIdMatchesARadiologyOrderAndDicomViewerUrlIfOrderCompleted()
                    throws Exception {
        
        // given
        RadiologyOrder mockCompletedRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
        mockCompletedRadiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        
        when(dicomWebViewer.getDicomViewerUrl(mockCompletedRadiologyOrder.getStudy()))
                .thenReturn("http://localhost:8081/weasis-pacs-connector/viewer?studyUID=1.2.826.0.1.3680043.8.2186.1.1");
        
        ModelAndView modelAndView = radiologyOrderFormController
                .getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(mockCompletedRadiologyOrder);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("order"));
        RadiologyOrder order = (RadiologyOrder) modelAndView.getModelMap()
                .get("order");
        assertThat(order, is(mockCompletedRadiologyOrder));
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyOrder"));
        RadiologyOrder radiologyOrder = (RadiologyOrder) modelAndView.getModelMap()
                .get("radiologyOrder");
        assertThat(radiologyOrder, is(mockCompletedRadiologyOrder));
        
        assertThat(modelAndView.getModelMap(), hasKey("dicomViewerUrl"));
        String dicomViewerUrl = (String) modelAndView.getModelMap()
                .get("dicomViewerUrl");
        assertThat(dicomViewerUrl,
            is("http://localhost:8081/weasis-pacs-connector/viewer?studyUID=1.2.826.0.1.3680043.8.2186.1.1"));
    }
    
    /**
     * @see RadiologyOrderFormController#getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(Order)
     * @verifies populate model and view with existing order if given order id only matches an order
     *           and not a radiology order
     */
    @Test
    public void
            getRadiologyOrderFormWithExistingRadiologyOrderByOrderId_shouldPopulateModelAndViewWithExistingOrderIfGivenOrderIdOnlyMatchesAnOrderAndNotARadiologyOrder()
                    throws Exception {
        
        // given
        RadiologyOrder mockRadiologyOrderToDiscontinue = RadiologyTestData.getMockRadiologyOrder1();
        String discontinueReason = "Wrong Procedure";
        
        Order mockDiscontinuationOrder = new Order();
        mockDiscontinuationOrder.setOrderId(2);
        mockDiscontinuationOrder.setAction(Order.Action.DISCONTINUE);
        mockDiscontinuationOrder.setOrderer(mockRadiologyOrderToDiscontinue.getOrderer());
        mockDiscontinuationOrder.setOrderReasonNonCoded(discontinueReason);
        mockDiscontinuationOrder.setPreviousOrder(mockRadiologyOrderToDiscontinue);
        
        ModelAndView modelAndView = radiologyOrderFormController
                .getRadiologyOrderFormWithExistingRadiologyOrderByOrderId(mockDiscontinuationOrder);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW));
        
        assertThat(modelAndView.getModelMap(), hasKey("order"));
        Order order = (Order) modelAndView.getModelMap()
                .get("order");
        assertThat(order, is(mockDiscontinuationOrder));
        
        assertThat(modelAndView.getModelMap(), not(hasKey("radiologyOrder")));
        
        assertThat(modelAndView.getModelMap(), not(hasKey("dicomViewerUrl")));
    }
    
    /**
     * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order, BindingResult)
     */
    @Test
    @Verifies(
            value = "should set http session attribute openmrs message to order saved and redirect to radiologyOrderForm when save study was successful",
            method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
    public void
            postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToOrderSavedAndRedirectToRadiologyOrderFormWhenSaveStudyWasSuccessful()
                    throws Exception {
        
        // given
        RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
        
        when(radiologyOrderService.placeRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("saveOrder", "saveOrder");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult orderErrors = mock(BindingResult.class);
        when(orderErrors.hasErrors()).thenReturn(false);
        
        ModelAndView modelAndView = radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, null,
            mockRadiologyOrder, mockRadiologyOrder, orderErrors);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyOrder.form?orderId=" + mockRadiologyOrder.getOrderId()));
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.saved"));
    }
    
    /**
     * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order, BindingResult)
     */
    @Test
    @Verifies(
            value = "should set http session attribute openmrs message to order saved and redirect to radiologyOrderForm when save study was successful and given patient id",
            method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
    public void
            postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToOrderSavedAndRedirectToRadiologyOrderFormWhenSaveStudyWasSuccessfulAndGivenPatientId()
                    throws Exception {
        
        // given
        RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
        
        when(radiologyOrderService.placeRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("saveOrder", "saveOrder");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult orderErrors = mock(BindingResult.class);
        when(orderErrors.hasErrors()).thenReturn(false);
        
        ModelAndView modelAndView =
                radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder.getPatient()
                        .getPatientId(),
                    mockRadiologyOrder, mockRadiologyOrder, orderErrors);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyOrder.form?orderId=" + mockRadiologyOrder.getOrderId()));
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.saved"));
    }
    
    /**
     * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order, BindingResult)
     */
    @Test
    @Verifies(
            value = "should set http session attribute openmrs message to study performed when study performed status is in progress and request was issued by radiology scheduler",
            method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
    public void
            postSaveRadiologyOrder_shouldSetHttpSessionAttributeOpenmrsMessageToStudyPerformedWhenStudyPerformedStatusIsInProgressAndRequestWasIssuedByRadiologyScheduler()
                    throws Exception {
        
        // given
        RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
        mockRadiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        User mockRadiologyScheduler = RadiologyTestData.getMockRadiologyScheduler();
        
        when(userContext.getAuthenticatedUser()).thenReturn(mockRadiologyScheduler);
        when(radiologyOrderService.placeRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("saveOrder", "saveOrder");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult orderErrors = mock(BindingResult.class);
        when(orderErrors.hasErrors()).thenReturn(false);
        
        ModelAndView modelAndView =
                radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder.getPatient()
                        .getPatientId(),
                    mockRadiologyOrder, mockRadiologyOrder, orderErrors);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW));
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_ERROR_ATTR), is("radiology.studyPerformed"));
    }
    
    /**
     * @see RadiologyOrderFormController#postSaveRadiologyOrder(HttpServletRequest, Integer, Order, BindingResult)
     */
    @Test
    @Verifies(value = "should not redirect if radiology order is not valid according to order validator",
            method = "postSaveRadiologyOrder(HttpServletRequest, Integer, RadiologyOrder, BindingResult)")
    public void postSaveRadiologyOrder_shouldNotRedirectIfRadiologyOrderIsNotValidAccordingToOrderValidator()
            throws Exception {
        
        // given
        RadiologyOrder mockRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
        User mockRadiologyScheduler = RadiologyTestData.getMockRadiologyScheduler();
        
        when(userContext.getAuthenticatedUser()).thenReturn(mockRadiologyScheduler);
        when(radiologyOrderService.placeRadiologyOrder(mockRadiologyOrder)).thenReturn(mockRadiologyOrder);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("saveOrder", "saveOrder");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        BindingResult orderErrors = mock(BindingResult.class);
        when(orderErrors.hasErrors()).thenReturn(true);
        
        ModelAndView modelAndView =
                radiologyOrderFormController.postSaveRadiologyOrder(mockRequest, mockRadiologyOrder.getPatient()
                        .getPatientId(),
                    mockRadiologyOrder, mockRadiologyOrder, orderErrors);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(), is(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW));
    }
    
    /**
     * @see RadiologyOrderFormController#postDiscontinueRadiologyOrder(HttpServletRequest, HttpServletResponse, Order,
     *      String, Date)
     */
    @Test
    @Verifies(value = "should discontinue non discontinued order and redirect to discontinuation order",
            method = "postDiscontinueRadiologyOrder(HttpServletRequest, HttpServletResponse, Order, String, Date)")
    public void postDiscontinueRadiologyOrder_shouldDiscontinueNonDiscontinuedOrderAndRedirectToDiscontinuationOrder()
            throws Exception {
        // given
        RadiologyOrder mockRadiologyOrderToDiscontinue = RadiologyTestData.getMockRadiologyOrder1();
        String discontinueReason = "Wrong Procedure";
        
        Order mockDiscontinuationOrder = new Order();
        mockDiscontinuationOrder.setOrderId(2);
        mockDiscontinuationOrder.setAction(Order.Action.DISCONTINUE);
        mockDiscontinuationOrder.setOrderer(mockRadiologyOrderToDiscontinue.getOrderer());
        mockDiscontinuationOrder.setOrderReasonNonCoded(discontinueReason);
        mockDiscontinuationOrder.setPreviousOrder(mockRadiologyOrderToDiscontinue);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("discontinueOrder", "discontinueOrder");
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        when(radiologyOrderService.getRadiologyOrderByOrderId(mockRadiologyOrderToDiscontinue.getOrderId()))
                .thenReturn(mockRadiologyOrderToDiscontinue);
        when(radiologyOrderService.discontinueRadiologyOrder(mockRadiologyOrderToDiscontinue,
            mockDiscontinuationOrder.getOrderer(), mockDiscontinuationOrder.getOrderReasonNonCoded()))
                    .thenReturn(mockDiscontinuationOrder);
        
        BindingResult orderErrors = mock(BindingResult.class);
        assertThat(mockRadiologyOrderToDiscontinue.getAction(), is(Order.Action.NEW));
        ModelAndView modelAndView = radiologyOrderFormController.postDiscontinueRadiologyOrder(mockRequest, null,
            mockRadiologyOrderToDiscontinue, mockDiscontinuationOrder, orderErrors);
        
        assertNotNull(modelAndView);
        assertThat(modelAndView.getViewName(),
            is("redirect:/module/radiology/radiologyOrder.form?orderId=" + mockDiscontinuationOrder.getOrderId()));
        assertThat((String) mockSession.getAttribute(WebConstants.OPENMRS_MSG_ATTR), is("Order.discontinuedSuccessfully"));
    }
    
    /**
     * @see RadiologyOrderFormController#radiologyReportNeedsToBeCreated(ModelAndView,Order)
     * @verifies return false if order is not a radiology order
     */
    @Test
    public void radiologyReportNeedsToBeCreated_shouldReturnFalseIfOrderIsNotARadiologyOrder() throws Exception {
        
        // given
        ModelAndView modelAndView = new ModelAndView(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW);
        
        RadiologyOrder mockRadiologyOrderToDiscontinue = RadiologyTestData.getMockRadiologyOrder1();
        String discontinueReason = "Wrong Procedure";
        
        Order mockDiscontinuationOrder = new Order();
        mockDiscontinuationOrder.setOrderId(2);
        mockDiscontinuationOrder.setAction(Order.Action.DISCONTINUE);
        mockDiscontinuationOrder.setOrderer(mockRadiologyOrderToDiscontinue.getOrderer());
        mockDiscontinuationOrder.setOrderReasonNonCoded(discontinueReason);
        mockDiscontinuationOrder.setPreviousOrder(mockRadiologyOrderToDiscontinue);
        
        final boolean result = (Boolean) radiologyReportNeedsToBeCreatedMethod.invoke(radiologyOrderFormController,
            new Object[] { modelAndView, mockDiscontinuationOrder });
        assertFalse(result);
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyReportNeedsToBeCreated"));
        assertFalse((Boolean) modelAndView.getModelMap()
                .get("radiologyReportNeedsToBeCreated"));
    }
    
    /**
     * @see RadiologyOrderFormController#radiologyReportNeedsToBeCreated(ModelAndView,Order)
     * @verifies return false if radiology order is not completed
     */
    @Test
    public void radiologyReportNeedsToBeCreated_shouldReturnFalseIfRadiologyOrderIsNotCompleted() throws Exception {
        
        // given
        ModelAndView modelAndView = new ModelAndView(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW);
        
        RadiologyOrder incompleteRadiologyOrder = RadiologyTestData.getMockRadiologyOrder1();
        incompleteRadiologyOrder.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.IN_PROGRESS);
        
        final boolean result = (Boolean) radiologyReportNeedsToBeCreatedMethod.invoke(radiologyOrderFormController,
            new Object[] { modelAndView, incompleteRadiologyOrder });
        assertFalse(result);
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyReportNeedsToBeCreated"));
        assertFalse((Boolean) modelAndView.getModelMap()
                .get("radiologyReportNeedsToBeCreated"));
    }
    
    /**
     * @see RadiologyOrderFormController#radiologyReportNeedsToBeCreated(ModelAndView,Order)
     * @verifies return false if radiology order is completed but has a claimed report
     */
    @Test
    public void radiologyReportNeedsToBeCreated_shouldReturnFalseIfRadiologyOrderIsCompletedButHasAClaimedReport()
            throws Exception {
        
        // given
        ModelAndView modelAndView = new ModelAndView(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW);
        
        RadiologyReport claimedReport = RadiologyTestData.getMockRadiologyReport1();
        claimedReport.setReportStatus(RadiologyReportStatus.CLAIMED);
        
        RadiologyOrder completedRadiologyOrderWithClaimedReport = claimedReport.getRadiologyOrder();
        completedRadiologyOrderWithClaimedReport.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        
        when(radiologyReportService.getActiveRadiologyReportByRadiologyOrder(completedRadiologyOrderWithClaimedReport))
                .thenReturn(claimedReport);
        
        final boolean result = (Boolean) radiologyReportNeedsToBeCreatedMethod.invoke(radiologyOrderFormController,
            new Object[] { modelAndView, completedRadiologyOrderWithClaimedReport });
        assertFalse(result);
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyReportNeedsToBeCreated"));
        assertFalse((Boolean) modelAndView.getModelMap()
                .get("radiologyReportNeedsToBeCreated"));
    }
    
    /**
     * @see RadiologyOrderFormController#radiologyReportNeedsToBeCreated(ModelAndView,Order)
     * @verifies return false if radiology order is completed but has a completed report
     */
    @Test
    public void radiologyReportNeedsToBeCreated_shouldReturnFalseIfRadiologyOrderIsCompletedButHasACompletedReport()
            throws Exception {
        
        // given
        ModelAndView modelAndView = new ModelAndView(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW);
        
        RadiologyReport completedReport = RadiologyTestData.getMockRadiologyReport1();
        completedReport.setReportStatus(RadiologyReportStatus.COMPLETED);
        
        RadiologyOrder completedRadiologyOrderWithCompletedReport = completedReport.getRadiologyOrder();
        completedRadiologyOrderWithCompletedReport.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        
        when(radiologyReportService.getActiveRadiologyReportByRadiologyOrder(completedRadiologyOrderWithCompletedReport))
                .thenReturn(completedReport);
        
        final boolean result = (Boolean) radiologyReportNeedsToBeCreatedMethod.invoke(radiologyOrderFormController,
            new Object[] { modelAndView, completedRadiologyOrderWithCompletedReport });
        assertFalse(result);
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyReportNeedsToBeCreated"));
        assertFalse((Boolean) modelAndView.getModelMap()
                .get("radiologyReportNeedsToBeCreated"));
    }
    
    /**
     * @see RadiologyOrderFormController#radiologyReportNeedsToBeCreated(ModelAndView,Order)
     * @verifies return true if radiology order is completed and has no claimed report
     */
    @Test
    public void radiologyReportNeedsToBeCreated_shouldReturnTrueIfRadiologyOrderIsCompletedAndHasNoClaimedReport()
            throws Exception {
        
        // given
        ModelAndView modelAndView = new ModelAndView(RadiologyOrderFormController.RADIOLOGY_ORDER_FORM_VIEW);
        
        RadiologyOrder completedRadiologyOrderWithNoClaimedReport = RadiologyTestData.getMockRadiologyOrder1();
        completedRadiologyOrderWithNoClaimedReport.getStudy()
                .setPerformedStatus(PerformedProcedureStepStatus.COMPLETED);
        
        when(radiologyReportService.getActiveRadiologyReportByRadiologyOrder(completedRadiologyOrderWithNoClaimedReport))
                .thenReturn(null);
        
        final boolean result = (Boolean) radiologyReportNeedsToBeCreatedMethod.invoke(radiologyOrderFormController,
            new Object[] { modelAndView, completedRadiologyOrderWithNoClaimedReport });
        assertTrue(result);
        
        assertThat(modelAndView.getModelMap(), hasKey("radiologyReportNeedsToBeCreated"));
        assertTrue((Boolean) modelAndView.getModelMap()
                .get("radiologyReportNeedsToBeCreated"));
    }
}
