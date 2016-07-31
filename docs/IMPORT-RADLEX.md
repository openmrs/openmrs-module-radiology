# Import Radlex to OpenMRS

## Overview

This guide shows how you can import the Radlex Playbook into OpenMRS concept
reference terms.

ATTENTION: I only tested this on linux!

## Background

Search the OpenMRS wiki for some backround information about concepts/concept
sources, like:

https://wiki.openmrs.org/display/docs/Standard+and+Non-standard+Terminology+Mapping

## Howto

### Get Radlex

You need to download it yourself since you need to accept their license terms

http://www.rsna.org/radlexdownloads/

### Import into OpenMRS

_Create Concept Source Radlex_

You first need to create Radlex as an OpenMRS concept source.

You can directly execute this sql statement in your database:

```sql
INSERT concept_reference_source (concept_source_id,name,description,creator,date_created,uuid) VALUES
(1,"RADLEX","RadLex Playbook is a project of the Radiological Society of North America (RSNA)",1,"2016-08-01 09:00:00","616a9691-a1bf-4426-85a6-21a60c558265");
```

_Create Concept Reference Terms for Radlex terms_

We need to generate UUIDs for every concept reference term.

I am doing this with [uuidgen command](http://man7.org/linux/man-pages/man1/uuidgen.1.html) on linux.

Execute the following to create sql insert statements for Radlex terms as
OpenMRS concept reference terms.


```bash
#!/bin/bash

[ -f import-radlex.sql ] && rm import-radlex.sql

awk -vFPAT='[^,]*|"[^"]*"'']"' '{if (NR!=1) {print "\42" sq $1 "\42," $3 sq ","sq $4 sq ");" }}' core-playbook-2_1 > radlex-columns.csv

while read; do
    echo "INSERT concept_reference_term
    (concept_source_id,version,creator,date_created,uuid,code,name,description)
    VALUES (1,\"2.1\",1,\"2016-08-01 12:00:00\",\"$(uuidgen)\",${REPLY}" >>
import-radlex.sql done < radlex-columns.csv
```

NOTE: if you use a different Radlex version adjust it in the command, if you
dont agree with the Radlex columns I use to populate the reference terms just
adjust them ;)

If you have suggestions/improvements please share!!

