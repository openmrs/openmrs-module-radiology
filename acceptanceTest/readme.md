# Description
These are the acceptance tests for the OpenMRS Radiology Module.

# General
The acceptance tests require certain test-data which will be inserted into the MySQL database of Openmrs. 

Therefore the acceptance tests need to have access to the openmrs database.


The login credentials can be set with an additional ` -DconfigFile="FILENAME"`

(Use the `config/db/config_db_example.config`-file for an example database configuration.)

The acceptance tests also require to have the openmrs example data installed: https://wiki.openmrs.org/display/RES/Demo+Data

Another requirement is to have a fully set up configured openmrs instance with an configured radiology module.

# Database changes
Running these tests will perform a clean insert on the following tables in the openmrs database:

- encounter
- encounter_provider
- orders
- test_order
- radiology_order
- radiology_study
- radiology_report


# Installation
Open a new command line in the root folder of the acceptance tests and enter:

`cd plugin/mUpdate`

`gradle install`

or just run `start.bat` in the root folder of the acceptance tests.

# Usage
To run individual browsers, use the following commands:
- `gradle chromeTest`
- `gradle phantomJsTest`

To run all browsers, use `gradle test`
(all commands should be executed in the root folder of the acceptance tests).

# Individual configuration:
Add the following for and individual configuration:

- For individual login: ` -DconfigLoginFile="FILENAME"`
- For individual database login: ` -DconfigDBFile="FILENAME"`
- For individual omod-file: ` -DinstallFile="FILENAME-DESTINATION"`
- For individual baseUrl for OpenMRS: ` -DbaseUrl="BASEURL"`

Example: ` gradle chromeTest -DconfigLoginFile="config/login/config_login_example.config" -DconfigDBFile="config/db/config_db_example.config"`
# Update module
If you want to update the radiology module use the given command in the root folder of the acceptance tests and the module
will automatically updated with the default login-configuration and the default-place
for the compiled module (omod-file):
`gradle updateModule`

If you want to run `updateModule` with using the default login-configuration in the program, use the task `gradle updateModuleDefault`.
