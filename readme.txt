Description : 
  OpenMRS module to perform Radiology tasks using dcm4chee medical imaging archive.

Wiki: 
  https://wiki.openmrs.org/display/docs/Radiology+Module+with+dcm4chee

Installation:
  See Install Guide in the WIki link above.
  
Usage:
  See User Guide in the WIki link above.

Folder structure:
  Sources, see them in netbeans, is just the folder src that contains:
    dcm4che servers
    module code (org.openmrs.module.radiology)
    
  Libs:
    lib
    lib-common for openmrs libraries, needed in build time, provided by the server on runtime
      
  Web pages:
    web/module JSP, CSS, JS files
    
  Module configuration and startup:
    metadata

  
Notes
  See Guide 

Important
OpenMRS Version 1.9.*
Module Dependant on OrderType , OrderService methods for OrderType
Does Not work after OrderType Deprecation in latest openmrs-core-1.10/trunk

