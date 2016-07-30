# Docker

## Overview

We are using maven plugin https://github.com/fabric8io/docker-maven-plugin
to integrate creating and running a Docker image of the Radiology Module with
its dependencies into the build process.

If you are having trouble using the plugin or want to know more about its great
features please checkout their documentation, it is extensive!

## Background

The Radiology Module needs OpenMRS (which itself needs an application server,
we use Tomcat) and a database (we use MySQL).

The Docker image of the Radiology Module is

* built on top of a [Docker image for OpenMRS](https://hub.docker.com/r/teleivo/openmrs-platform/)
* linked to a [Docker image for the database](https://hub.docker.com/r/teleivo/openmrs-platform-mysql/)

If you want to get into the details please look at the omod/pom.xml

## Create Image

Build the Radiology Module and its Docker image:

```bash
cd openmrs-module-radiology
mvn clean package docker:build
```

Only build the Docker image (assumes you already built the module):

```bash
cd openmrs-module-radiology/omod
mvn package docker:build
```

NOTE: `package` is necessary otherwise the docker plugin wont find the created
artifact. Thats a maven limitations which you can read about at the plugins
documentation.

### Skip tests

If you want to skip tests at any stage add command line arg:

```bash
-DskipTests
```

### Build args

You can specify build arguments on the command line.

For example if you are behind a proxy add:

```bash
mvn package docker:build \
  -Ddocker.buildArg.http_proxy=http://192.168.1.100:8080 \
  -Ddocker.buildArg.https_proxy=http://192.168.1.100:8080
```

**NOTE: please use the IP and port of your proxy** :wink:

## Create and Run Container

```bash
cd openmrs-module-radiology
mvn docker:start
```

This will start a container for the database and one for the Radiology Module
using the image you built at [Create Image](#create-image).

You will see log outputs of both containers with a prefix

* DB for the database container
* TC for the tomcat container

Logs will only show until a certain string in the log is found which we defined
in the Docker plugin configuration and define as "the container is ready".

So after that maven should show that the build succeeded and the Radiology
Module will be available for login.

From then on two new containers should be running.

You can check with:

```bash
docker ps
```

### Run Container with Custom Args

Check out the `properties` secion in the `omod/pom.xml`. There you can find
maven properties that are exposed. The properties values can be overriden on
the command line. This enables you to change for example the

* MySQL database name, user, password
* Tomcat port and its `JAVA_OPTS`

So lets says you want to override all of these mentioned properties, then just
execute:

```bash
mvn docker:start -Dmysql.user=ris -Dmysql.database=ris -Dmysql.password=ris \
  -Dtomcat.port=8082 -Dtomcat.env.java_opts="-Dfile.encoding=UTF-8 -server -Xms256m -Xmx1024m"
```

## Stop and Remove Container

```bash
cd openmrs-module-radiology
mvn docker:stop
```

This will only stop and remove the containers created via the Docker plugin.

### Remove Volumes

The database image is based on the Docker MySQL image which uses Docker volumes
for storing the persistent data. You might need to remove the volume your self.

After you started the Radiology Module using the Docker plugin you can check
that there is a new volume with:

```bash
docker volume ls
```

If you want to remove the volume created by the plugin when stopping do:

```bash
cd openmrs-module-radiology
mvn docker:stop -Ddocker.removeVolumes=true
```

If you want to manually remove a volume

```bash
docker volume rm <enter hash of volume you want to delete>
```

## I am busy :bowtie:

So build the module, create the image and run it all at once!

```bash
cd openmrs-module-radiology
mvn clean package docker:build docker:start
```

