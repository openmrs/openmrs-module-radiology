Installation:
  TODO

Folder structure:
  Sources, see them in eclipse, is just the folder src that contains:
    xebra sources and configuration
    dcm4che servers
    module code (org.openmrs.module.radiology)
    weasis launcher
  
  Libs:
    lib
    lib-common for openmrs libraries, needed in build time, provided by the server on runtime
      
  Web pages:
    web/module JSP, CSS, JS files
    
  Module configuration and startup:
    metadata
  
  build.xml to generate the omod file.
  
Notes
  Configure Xebra before build in
    src\sqlmapconfig\config.properties,
    metadata\xebra-server.properties and
    src\com\hxti\xebra\util\xebra-server.properties
    two lasts are the same, todo: test which one is correct