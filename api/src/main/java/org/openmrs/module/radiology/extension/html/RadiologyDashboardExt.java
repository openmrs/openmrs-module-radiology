package org.openmrs.module.radiology.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.PatientDashboardTabExt;

    public class RadiologyDashboardExt extends PatientDashboardTabExt {

       public Extension.MEDIA_TYPE getMediaType() {
          return Extension.MEDIA_TYPE.html;
       }
       
       @Override
       public String getPortletUrl() {
          return "RadiologyDashboardTab";
       }

       @Override
       public String getRequiredPrivilege() {
          return "Patient Dashboard - View Radiology Section";
       }

       @Override
       public String getTabId() {
          return "RadiologyTab";
       }

       @Override
       public String getTabName() {
          return "Radiology";
       }
       
    }