# Demo Data

The `demo-data.sql` contains a basic set of data used to demonstrate, test the
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

## Users

The user logins and their roles are listed here for convenience, for an up to date
list please see the `demo-data.sql`.

All users in the dataset have password: `Radio1234`

Data Clerk

* dat1

Referring Physician

* ref1
* ref2

Schedule

* sch1

Performing Physician

* per1

Reading Physician

* rad1
* rad2

## Import

### Command

To import it in your database do:

```bash
mysql -h 192.168.99.100 -uopenmrs -popenmrs openmrs < demo-data.sql
```

NOTE: please update the mysql host, port ... for your setup

### Update Search Index

In order for you to find the concepts in the legacy UIs concept dictionary
search, you need to update the search index.

This can be done in the Administration section under Maintenance click on
Search Index and then the button `Rebuild Search Index`.
