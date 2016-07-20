# openmrs-module-radiology

[![Build Status](https://secure.travis-ci.org/openmrs/openmrs-module-radiology.png?branch=master)](https://travis-ci.org/openmrs/openmrs-module-radiology) [![Coverage Status](https://coveralls.io/repos/openmrs/openmrs-module-radiology/badge.svg?branch=master&service=github)](https://coveralls.io/github/openmrs/openmrs-module-radiology?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/grade/5e0137f0c916494eaa3ba7de43149ef7)](https://www.codacy.com/app/teleivo/openmrs-module-radiology_2)

## Overview

OpenMRS module radiology (previously called radiologydcm4chee) is a module adding capabilities of a Radiology
Information System (RIS) onto OpenMRS.

## Build

### Prerequisites

You need to have installed

* a Java JDK 8
* the build tool [Maven](https://maven.apache.org/)

You need to configure Maven to use the JAVA JDK 8

```bash
mvn -version
```

Should tell you what version Maven is using.

You need to clone this repository:

```bash
git clone https://github.com/openmrs/openmrs-module-radiology.git
```

### Command

After you have taken care of the [Prerequisites](#prerequisites)

Execute the following command:

```bash
cd openmrs-module-radiology
mvn clean package
```

This will generate the radiology module in `omod/target/radiology-{VERSION}.omod` which you will have to deploy into OpenMRS.

## Install

The easiest way to install the module is to use [Docker](https://www.docker.com/).

### Docker :whale:

This module can be baked into a Docker image so you can easily run and test it.

#### Prerequisites

Build the module as described under [Build](#build).

Make sure you have [Docker](https://docs.docker.com/) installed.

This Docker image cannot run without the correct database appropriate for the OpenMRS Platform 2.0 version used.

I suggest you install and use [Docker Compose](https://docs.docker.com/compose/install/) to install the Docker image for the Radiology Module you built together with
the Docker image [OpenMRS Platform MySQL](https://github.com/teleivo/docker-openmrs-platform-mysql) for the database.

Just follow the next sections.

#### Build

Enter the omod directory and build the Docker image for the Radiology Module
you just built:

```bash
cd omod
mvn docker:build
```

#### Run

To run an instance of the OpenMRS Platform MySQL database, the Platform itself
and the Radiology Module execute:

```
docker-compose up
```

in the root directory of this repository.

OpenMRS will be accessible at `localhost:8080/openmrs`

Look at `docker-compose.yml` which connects the required Docker images
and sets credentials and exposed ports.

## Documentation

For a detailed guide on ways to install and configure this module see

https://wiki.openmrs.org/display/docs/Radiology+Module


## Contributing

Contributions are very welcome, we can definitely use your help!

Check out our [contributing guidelines](CONTRIBUTING.md) to get started.

## Issues

To file new issues or help to fix existing ones please check out

https://issues.openmrs.org/browse/RAD

## Limitations

This module is not yet officially released to the [openmrs modules](https://modules.openmrs.org/#/).

The API and UI are not yet stable and subject to frequent changes.

The module depends on [OpenMRS Version 2.0](https://github.com/openmrs/openmrs-core) and
[OpenMRS Legacy UI](https://github.com/openmrs/openmrs-module-legacyui) which have not yet been officially released.

## Community

[![OpenMRS Talk](https://omrs-shields.psbrandt.io/custom/openmrs/talk/F26522?logo=openmrs)](http://talk.openmrs.org)
[![OpenMRS IRC](https://img.shields.io/badge/openmrs-irc-EEA616.svg?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MTIiIGhlaWdodD0iNjEyIiB2aWV3Qm94PSIwIDAgNjEyIDYxMiI%2BPHBhdGggZD0iTTE1MyAyMjkuNWMtMjEuMTMzIDAtMzguMjUgMTcuMTE3LTM4LjI1IDM4LjI1UzEzMS44NjcgMzA2IDE1MyAzMDZjMjEuMTE0IDAgMzguMjUtMTcuMTE3IDM4LjI1LTM4LjI1UzE3NC4xMzMgMjI5LjUgMTUzIDIyOS41em0xNTMgMGMtMjEuMTMzIDAtMzguMjUgMTcuMTE3LTM4LjI1IDM4LjI1UzI4NC44NjcgMzA2IDMwNiAzMDZjMjEuMTE0IDAgMzguMjUtMTcuMTE3IDM4LjI1LTM4LjI1UzMyNy4xMzMgMjI5LjUgMzA2IDIyOS41em0xNTMgMGMtMjEuMTMzIDAtMzguMjUgMTcuMTE3LTM4LjI1IDM4LjI1UzQzNy44NjcgMzA2IDQ1OSAzMDZzMzguMjUtMTcuMTE3IDM4LjI1LTM4LjI1UzQ4MC4xMzMgMjI5LjUgNDU5IDIyOS41ek0zMDYgMEMxMzcuMDEyIDAgMCAxMTkuODc1IDAgMjY3Ljc1YzAgODQuNTE0IDQ0Ljg0OCAxNTkuNzUgMTE0Ljc1IDIwOC44MjZWNjEybDEzNC4wNDctODEuMzRjMTguNTUyIDMuMDYyIDM3LjYzOCA0Ljg0IDU3LjIwMyA0Ljg0IDE2OS4wMDggMCAzMDYtMTE5Ljg3NSAzMDYtMjY3Ljc1UzQ3NS4wMDggMCAzMDYgMHptMCA0OTcuMjVjLTIyLjMzOCAwLTQzLjkxLTIuNi02NC42NDMtNy4wMmwtOTAuMDQgNTQuMTI0IDEuMjA0LTg4LjdDODMuNSA0MTQuMTMzIDM4LjI1IDM0NS41MTMgMzguMjUgMjY3Ljc1YzAtMTI2Ljc0IDExOS44NzUtMjI5LjUgMjY3Ljc1LTIyOS41czI2Ny43NSAxMDIuNzYgMjY3Ljc1IDIyOS41UzQ1My44NzUgNDk3LjI1IDMwNiA0OTcuMjV6IiBmaWxsPSIjZmZmIi8%2BPC9zdmc%2B)](http://irc.openmrs.org)
[![OpenMRS Telegram](https://img.shields.io/badge/openmrs-telegram-009384.svg?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNDAgMjQwIj48ZGVmcz48bGluZWFyR3JhZGllbnQgaWQ9ImEiIHgxPSIuNjY3IiB5MT0iLjE2NyIgeDI9Ii40MTciIHkyPSIuNzUiPjxzdG9wIHN0b3AtY29sb3I9IiMzN2FlZTIiIG9mZnNldD0iMCIvPjxzdG9wIHN0b3AtY29sb3I9IiMxZTk2YzgiIG9mZnNldD0iMSIvPjwvbGluZWFyR3JhZGllbnQ%2BPGxpbmVhckdyYWRpZW50IGlkPSJiIiB4MT0iLjY2IiB5MT0iLjQzNyIgeDI9Ii44NTEiIHkyPSIuODAyIj48c3RvcCBzdG9wLWNvbG9yPSIjZWZmN2ZjIiBvZmZzZXQ9IjAiLz48c3RvcCBzdG9wLWNvbG9yPSIjZmZmIiBvZmZzZXQ9IjEiLz48L2xpbmVhckdyYWRpZW50PjwvZGVmcz48Y2lyY2xlIGN4PSIxMjAiIGN5PSIxMjAiIHI9IjEyMCIgZmlsbD0idXJsKCNhKSIvPjxwYXRoIGZpbGw9IiNjOGRhZWEiIGQ9Ik05OCAxNzVjLTMuODg4IDAtMy4yMjctMS40NjgtNC41NjgtNS4xN0w4MiAxMzIuMjA3IDE3MCA4MCIvPjxwYXRoIGZpbGw9IiNhOWM5ZGQiIGQ9Ik05OCAxNzVjMyAwIDQuMzI1LTEuMzcyIDYtM2wxNi0xNS41NTgtMTkuOTU4LTEyLjAzNSIvPjxwYXRoIGZpbGw9InVybCgjYikiIGQ9Ik0xMDAuMDQgMTQ0LjQxbDQ4LjM2IDM1LjczYzUuNTIgMy4wNDQgOS41IDEuNDY3IDEwLjg3Ni01LjEyNGwxOS42ODUtOTIuNzYzYzIuMDE2LTguMDgtMy4wOC0xMS43NDYtOC4zNTgtOS4zNWwtMTE1LjU5IDQ0LjU3MmMtNy44OSAzLjE2NS03Ljg0NCA3LjU2Ny0xLjQ0IDkuNTI4bDI5LjY2NCA5LjI2IDY4LjY3My00My4zMjZjMy4yNC0xLjk2NiA2LjIxNy0uOTEgMy43NzUgMS4yNTgiLz48L3N2Zz4%3D)](https://telegram.me/openmrs)
[![OpenMRS Radiology Wiki](https://img.shields.io/badge/openmrs-wiki-5B57A6.svg?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNjAiIGhlaWdodD0iMTQyIiB2aWV3Qm94PSIwIDAgMTYwIDE0MiI%2BPHBhdGggY2xhc3M9InN0MCIgZD0iTTExMy42MTUgOTQuNDk0Yy0yLjAxNi0zLjk3NC00LjQwNS03Ljk5LTcuMi0xMi4wNzctMi0yLjkzLTQuMTQ1LTUuNzc4LTYuMzg3LTguNTY3LS45MS0xLjEzNi0uNTMtMi41NDguMTY3LTMuMjUuNjg4LS43MDUgMS4zOC0xLjQxIDIuMDc2LTIuMTIgOS41OC05Ljc3IDE5LjQ5LTE5Ljg3MyAyNy4wOS0zMC43ODcgOC4wOC0xMS42MSAxMi41Ni0yMi42MjQgMTMuNjktMzMuOTU0LjEyLTEuMTQtLjQtMi4zNS0xLjMyLTMuMDUtLjYtLjQ2LTEuMzMtLjctMi4wNy0uNy0uNDEgMC0uODIuMDctMS4yMS4yMi03LjM3IDIuODItMTQuODUgNC45Ni0yMS42OCA2LjU1LTEuMzkuMzItMi41MSAxLjM2LTIuOTggMi42LTQuOTggMTMuNjMtMTcuNjggMjYuNjEtMzEuMDEgNDAuMi0uNTMuNTEtMS4yOCAxLjE4LTIuNSAxLjE4cy0xLjk2LS42NS0yLjUtMS4xOGMtMTMuMzMtMTMuNTktMjYuMDMtMjYuNTItMzEtNDAuMTUtLjQ2LTEuMjQtMS41OS0yLjI4LTIuOTgtMi42QzM2Ljk0IDUuMjIgMjkuNDUgMi45IDIyLjEuMDhjLS4zOTgtLjE1LS44MS0uMjI1LTEuMjItLjIyNS0uNzQgMC0xLjQ3LjI0LTIuMDcuNy0uOTQuNzE4LTEuNDQgMS44NzItMS4zMiAzLjA0OCAxLjEzIDExLjMzMiA1LjYgMjIuNDggMTMuNjg0IDM0LjA5IDcuNiAxMC45MTUgMTcuNTEgMjEuMDE3IDI3LjA5IDMwLjc4NyAxNy42NSAxNy45OTQgMzQuMzMgMzQuOTk3IDM1Ljc5IDU0LjcxMy4xMyAxLjc4IDEuNjIgMy4xNTggMy40IDMuMTU4aDIwLjc0Yy45NCAwIDEuODMtLjM4IDIuNDctMS4wNi42NS0uNjcuOTktMS41OC45NC0yLjUyLS4xOC0zLjcxLS43Mi03LjQyLTEuNTktMTEuMTZoLjAxYy0uMDI4LS4xMS0uMDQ3LS4yMi0uMDQ3LS4zMyAwLS43NS41ODgtMS4zOCAxLjM1Ny0xLjM4LjA3IDAgLjEzLjAyLjIuMDMgMTYuOTMgMi40OCAyNy42MzYgNi40NCAyNy42NSAxMC44di4wMWMwIDQuMTEtOS42MjMgMTAuMzEtMjUuMjY2IDE0Ljg1bC0uMDA1LjAxYy0xLjM5LjQtMi40MDYgMS42Ni0yLjQwNiAzLjE1IDAgMS44MSAxLjQ5MyAzLjI4IDMuMzQgMy4yOC4yNTUgMCAuNS0uMDMuNzQtLjA4IDIxLjAyNi00Ljg2IDM0Ljk2NS0xMy4wMzQgMzQuOTY1LTIyLjI2MiAwLTEwLjk1NC0xOC44NC0yMC43NC00Ni45LTI1LjE1MnpNNTguMDEgODMuODA2Yy0uNDI1LS40NDQtMS4yNzctMS4wMzgtMi40MjItMS4wMzgtMS41NDcgMC0yLjQ2NiAxLTIuODEyIDEuNTMtMi4yNjQgMy40NDQtNC4yNCA2Ljg0My01Ljk0NiAxMC4yMDhDMTguODEgOTguOTI0IDAgMTA4LjcgMCAxMTkuNjVjMCA5LjIzNyAxMy44NCAxNy4zOTQgMzQuOTA1IDIyLjI1NS4wMDMuMDAyLjAyMyAwIC4wMyAwIC4yNS4wNTguNTA0LjA5NS43Ny4wOTUgMS44NDYgMCAzLjM0LTEuNDcgMy4zNC0zLjI4IDAtMS40ODctMS4wMTctMi43My0yLjQtMy4xM2wtLjAxLS4wMjJjLTE1LjY0NS00LjU0LTI1LjI3LTEwLjc0NC0yNS4yNy0xNC44NTJ2LS4wMWMuMDE3LTQuMzUzIDEwLjY5My04LjMwNiAyNy41OC0xMC43ODcuMDYyLS4wMS4xMi0uMDIuMTgyLS4wMi43NzUgMCAxLjM2OC42MyAxLjM2OCAxLjM5IDAgLjExLS4wMi4yMy0uMDQ2LjMzbC4wMS4wMWMtLjg3IDMuNzEtMS40IDcuNDEtMS41OCAxMS4xMS0uMDUuOTMuMjkgMS44NS45NCAyLjUzLjY0LjY3IDEuNTQgMS4wNiAyLjQ4IDEuMDZoMjAuNzRjMS43OCAwIDMuMjgtMS40IDMuNDEtMy4xNy40NS02LjA3IDIuMzUtMTIuMTUgNS43OC0xOC41NCAxLjE5LTIuMjEuMjYtNC4yOS0uNDItNS4xOC0zLjQyLTQuNDMtNy41OS05LjE2LTEzLjgxLTE1LjY1eiIgZmlsbD0iI2ZmZiIvPjxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik03Ny44NjggMzIuNTc4Yy44Mi43OTggMS43NS45NDcgMi4zOS45NDdoLjAwNmMuNjQyIDAgMS41Ny0uMTQ4IDIuMzktLjk0NiA3LjMxMy03LjExIDExLjI0Mi0xNS40IDEyLjEwMy0xNy43MS4xMjUtLjM0LjI1Mi0uNzMuMjUyLTEuMjYgMC0xLjg0LTEuNTQtMy4xNi0zLjE0LTMuMTYtMS4zMyAwLTUuMS4zOS0xMS41OS4zOWgtLjA1Yy02LjUgMC0xMC4yNy0uMzktMTEuNTktLjM5LTEuNjEgMC0zLjE0IDEuMzEtMy4xNCAzLjE1IDAgLjUzLjEzLjkyLjI1IDEuMjYuODYgMi4zIDQuNzkgMTAuNTkgMTIuMSAxNy43eiIgZmlsbD0iI2ZmZiIvPjwvc3ZnPg%3D%3D)](https://wiki.openmrs.org/display/docs/Radiology+Module)

## Support

Ask questions on [OpenMRS Talk](https://talk.openmrs.org/).

## License

[MPL 2.0 w/ HD](http://openmrs.org/license/) © [OpenMRS Inc.](http://www.openmrs.org/)
