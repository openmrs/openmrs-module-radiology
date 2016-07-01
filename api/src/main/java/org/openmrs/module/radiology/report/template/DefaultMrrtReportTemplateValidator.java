package org.openmrs.module.radiology.report.template;

import java.io.File;

import org.openmrs.api.APIException;

class DefaultMrrtReportTemplateValidator implements MrrtReportTemplateValidator {
    
    
    private static final String VALID_EXTENSION = "html";
    
    @Override
    public void validate(File templateFile) throws APIException {
        
        if (!VALID_EXTENSION.equals(getFileExtension(templateFile)))
            throw new APIException("Invalid file extension. Only .html files are accepted");
    }
    
    private String getFileExtension(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
        
        return fileExtension;
    }
}
