# openmrs-module-radiology

[![Build Status](https://secure.travis-ci.org/openmrs/openmrs-module-radiology.png?branch=master)](https://travis-ci.org/openmrs/openmrs-module-radiology) [![Coverage Status](https://coveralls.io/repos/openmrs/openmrs-module-radiology/badge.svg?branch=master&service=github)](https://coveralls.io/github/openmrs/openmrs-module-radiology?branch=master)

## Overview

OpenMRS module radiology (previously called radiologydcm4chee) is a module adding capabilities of a Radiology
Information System (RIS) onto OpenMRS. This module connects the open source
enterprise electronic medical record system [OpenMRS](http://www.openmrs.org)
with the open source clinical image and object management system
[dcm4che](http://www.dcm4che.org).

## Quickstart

### Build

Make sure you have a java jdk and [maven](https://maven.apache.org/) installed.

Clone this project:
```bash
git clone https://github.com/openmrs/openmrs-module-radiology.git
```

And execute the following command on the command line:
```bash
mvn clean package
```

### Installation

For a detailed guide on how to install and configure this module see

https://wiki.openmrs.org/display/docs/Radiology+Module

### Contributing

Contributions are very welcome, we can definitely use your help!

Check out our [contributing guidelines](CONTRIBUTING.md) to get started.

## Issues

To file new issues or help to fix existing ones please check out

https://issues.openmrs.org/browse/RAD

## Limitations

This module is not yet officially released to the [openmrs modules](https://modules.openmrs.org/#/).

The API and UI are not yet stable and subject to frequent changes.

The module currently only works with OpenMRS Version 1.11.4
