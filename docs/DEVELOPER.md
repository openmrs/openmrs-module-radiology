# DEVELOPER GUIDE

####Table of Contents

1. [Overview](#overview)
2. [Build](#build)
  * [Plugins](#plugins)
    * [License Header](#license-header)
    * [Formatter](#formatter)
    * [Code Style](#code-style)
    * [Test coverage](#test-coverage)
    * [Docker](#docker)

## Overview

This guide should give any developer a quick reference of useful commands and
guidelines which are used in this module.

This document is work in progress :construction_worker:

Contributions are welcome!

## Build

The build tool used is [Maven](https://maven.apache.org/)

### Plugins

#### License Header

We use http://code.mycila.com/license-maven-plugin/ which makes sure that every
java, xml, txt file has a correct OpenMRS license header. The license header
content is taken from license-header.txt the formatting depends on the file
type (java, xml, txt).

This plugin is automatically run on `mvn clean package/install` and fails on
travis CI if a file misses or doesnt have a correct license header.

##### Commands

_Add or update license header_

```bash
mvn license:format
```

_Check for wrong or missing license header_

```bash
mvn license:check
```

#### Formatter

We use https://github.com/velo/maven-formatter-plugin as source code formatter.

This plugin is automatically run on `mvn clean package/install` (not on travis
CI) and formats java, javascript files only. JSPs and xmls have to be formatted
by you, so please configure your IDE formatter correctly.

Refer to [formatter guide](FORMATTER.md) on how to configure your
IDE.

##### Commands

_Format source code_

```bash
mvn formatter:format
```

#### Code Style

to come

#### Test coverage

to come

#### Docker

Please read the corresponding [docker guide](DOCKER.md).

