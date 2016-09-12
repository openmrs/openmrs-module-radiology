# Demo Data

####Table of Contents

1. [Overview](#overview)
2. [How to Import](#how-to-import)
3. [Roles and Users](#roles-and-users)
4. [Patients and Cases](#patients-and-cases)

## Overview

The `acceptanceTest/resources/demo-data.sql` contains a basic set of data used to demonstrate, test the
Radiology Module and its legacy UI interface.

It contains:
* concepts extracted from CIEL dictionary of class "Radiology/Imaging
Procedure" regarding radiology orderables
* an update statement to set the global property used to define concept classes
which are radiology orderables to above concepts
* a subset of concepts extracted from CIEL dictionary of class "Diagnosis" to
be used as radiology order reason
* insert statements to assign necessary privileges to the radiology roles
* a set of users/providers for every radiology roles

**NOTE: users, providers, patients and their cases are purely fictional**

Person related data has been generated using the awesome [Faker.js](https://github.com/marak/Faker.js/) project.

## How to Import

### Command

To import it in your database do:

```bash
mysql -h <IP ADDRESS> -uopenmrs -popenmrs openmrs < acceptanceTest/resources/demo-data.sql
```

**NOTE: please adjust the mysql connection details (host, port ...) according to
your your setup!**

### Update Search Index

In order for you to find the concepts in the legacy UIs concept dictionary
search, you need to update the search index.

This can be done in the Administration section under Maintenance click on
Search Index and then the button `Rebuild Search Index`.

## Roles and Users

The user logins and their roles are listed here for convenience, for an up to date
list please see the `acceptanceTest/resources/demo-data.sql`.

All users in the dataset have password: `Radio1234`

### Role - Administrator

* `radmin (Nicholaus Wuckert)`

### Role - Data Clerk

* `dat1 (Makenna Reichert)`

### Role - Referring Physician

* `ref1 (Susie Lowe)`
* `ref2 (Dimitri Christiansen)`

### Role - Scheduler

* `sch1 (Juliana Kon)`

### Role - Performing Physician

* `per1 (Melody Jaskolski)`

### Role - Reading Physician

* `rad1 (Alexandra Skiles)`
* `rad2 (Arden Veum)`

## Patients and Cases

### Joyce Batz

#### Accession Number 1

_Clinical History_

This patient has hurt her wrist.

_Order_

A X-RAY scan was made to check wheter the wrist was broken.

_Images_

The images are available.

_Report_

Radiologist `Alexandra Skiles (user rad1)` has already finished the report.

### Carlos Wilderman

#### Accession Number 2

_Clinical History_

This patient has a history of strokes and felt dizzy.

_Order_

A CT san was made.

_Images_

The images are available.

_Report_

A report has to be written.

### Eldora Hegmann

#### Accession Number 3 and 4

_Clinical History_

This patient had fallen on her hip.

_Order_

`Susie Lowe (user ref1)` her referring physician unfortunately ordered the wrong procedure at first, so
there is a discontinued order. After that the correct order for X-RAYs was
placed.

_Images_

Are not yet available.

_Report_

Since there are no images, there is no report.

### Meaghan McGlynn

#### Accession Number 5

_Clinical History_

This patient had hurt her ankle while rollerblading.

_Order_

X-RAYs were ordered.

_Images_

Are available.

_Report_

Radiologist `Alexandra Skiles (user rad1)` has started writing the report but hasnt
finished it yet.

#### Accession Number 6

_Clinical History_

This patient had several evenst of sharp abdominal pain for a few weeks.

_Order_

Ultrasounds were ordered.

_Images_

Are available.

_Report_

Radiologist `Alexandra Skiles (user rad1)` has started writing the report but canceled it.
Radiologist `Arden Veum (user rad2)` took over and is currently writing it.

