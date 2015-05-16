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

## Limitations

This module currently only works with OpenMRS Version 1.9.*

This limitation is due to the deprecation of OrderType and this modules
dependancy on OrderType and OrderService methods for OrderType.

