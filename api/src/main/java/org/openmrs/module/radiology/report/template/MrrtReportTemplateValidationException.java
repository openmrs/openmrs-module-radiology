/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.xml.sax.SAXParseException;

public class MrrtReportTemplateValidationException extends APIException {
    
    
    private static final long serialVersionUID = 1L;
    
    private List<SAXParseException> exceptions;
    
    /**
     * Default empty constructor. If at all possible, don't use this one, but use the
     * {@link #MrrtReportTemplateValidationException(String)} constructor to specify a helpful message to the end user
     */
    public MrrtReportTemplateValidationException() {
    }
    
    /**
     * General constructor to give the end user a helpful message that relates to why this error
     * occurred.
     * 
     * @param message helpful message string for the end user
     */
    public MrrtReportTemplateValidationException(String message) {
        super(message);
    }
    
    /**
     * General constructor to give the end user a helpful message and to also propagate the parent
     * error exception message.
     * 
     * @param message helpful message string for the end user
     * @param cause the parent exception cause that this APIException is wrapping around
     */
    public MrrtReportTemplateValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor used to simply chain a parent exception cause to an MrrtReportTemplateValidationException. Preference
     * should be given to the {@link #MrrtReportTemplateValidationException(String, Throwable)} constructor if at all
     * possible instead of this one.
     * 
     * @param cause the parent exception cause that this MrrtReportTemplateValidationException is wrapping around
     */
    public MrrtReportTemplateValidationException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructor to give the end user a helpful message that relates to why this error occurred.
     * 
     * @param messageKey message code to retrieve
     * @param parameters message parameters
     */
    public MrrtReportTemplateValidationException(String messageKey, Object[] parameters) {
        super(Context.getMessageSourceService()
                .getMessage(messageKey, parameters, Context.getLocale()));
    }
    
    /**
     * Constructor to give the end user a helpful message and to also propagate the parent
     * error exception message..
     *
     * @param messageKey message code to retrieve
     * @param parameters message parameters
     * @param cause the parent exception cause that this APIException is wrapping around   
     */
    public MrrtReportTemplateValidationException(String messageKey, Object[] parameters, Throwable cause) {
        super(Context.getMessageSourceService()
                .getMessage(messageKey, parameters, Context.getLocale()), cause);
    }
    
    public MrrtReportTemplateValidationException(List<SAXParseException> exceptions) {
        super(getErrorMessage(exceptions));
    }
    
    private final static String getErrorMessage(List<SAXParseException> exceptions) {
        final StringBuilder errorMessage = new StringBuilder();
        for (SAXParseException exception : exceptions) {
            errorMessage.append(exception.getMessage() + "\n");
        }
        return errorMessage.toString();
    }
}
