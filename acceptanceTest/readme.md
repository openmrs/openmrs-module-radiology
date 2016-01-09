# Description
These are the AcceptanceTests for the OpenMRS Radiology Module.

# Installation
`cd plugin/mUpdate`
`gradle install`
or
run `start.bat`

# Usage
To run individual browsers, use the following commands:
- `gradle chromeTest`
- `gradle firefoxTest`
- `gradle phantomJsTest`

To run all browsers, use `gradle test`


# Individual configuration:
For an individual login, add ` -DconfigFile="FILENAME"`

Example: ` gradle chromeTest -DconfigFile="config/config_example.config" `
- For individual login: ` -DconfigFile="FILENAME"`
- For individual omod-file: ` -DinstallFile="FILENAME-DESTINATION"`
- For individual baseUrl for OpenMRS: ` -DbaseUrl="BASEURL"`

# Update module
If you want to update the radiology module use the given command and the module
will automatically updated with the default login-configuration and the default-place
for the compiled module (omod-file):
`gradle updateModule`

If you want to updateModule with using the default login-configuration in the program, use the task "gradle updateModuleDefault".

