# openmrs-module-radiologydcm4chee

[![Build Status](https://secure.travis-ci.org/openmrs/openmrs-module-radiologydcm4chee.png?branch=master)](https://travis-ci.org/openmrs/openmrs-module-radiologydcm4chee)

## Overview

OpenMRS module radiologydcm4chee is a module adding capabilities of a Radiology
Information System (RIS) onto OpenMRS. This module connects the open source
enterprise electronic medical record system [OpenMRS](http://www.openmrs.org)
with the open source clinical image and object management system
[dcm4che](http://www.dcm4che.org).

## Installation

For a detailed guide on how to install and configure this module see

https://wiki.openmrs.org/display/docs/Radiology+Module+with+dcm4chee

## Development

To file new issues or help to fix existing ones please check out

https://issues.openmrs.org/browse/RAD

###Build

To ensure that your commit builds fine run
```
mvn clean package
```
before opening a new pull request.

###Coding conventions

This module adheres to the OpenMRS coding conventions, please read

https://wiki.openmrs.org/display/docs/Coding+Conventions

####Code style

Help us to keep the code consistent!
This will produce readable diffs and make merging easier and quicker!

This module uses the Eclipse formatter plugin to automatically format *.java
files. This plugin is automatically executed when you build the module.

To manually run the formatter plugin, do
```
mvn java-formatter:format
```

For xml and javascript files use **control-shift-f** in Eclipse.
You will need to configure Eclipse to use the [OpenMRSFormatter.xml](tools/src/main/resources/eclipse/OpenMRSFormatter.xml)
provided by this module.

Remove unused imports by using **control-shift-o** in Eclipse.

## Limitations

This module currently only works with OpenMRS Version 1.9.9

This limitation is due to the deprecation of OrderType and this modules
dependency on OrderType and OrderService methods for OrderType.

