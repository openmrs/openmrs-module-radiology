FROM teleivo/openmrs-platform:2.0.0

# Get radiology modules dependencies
RUN curl -L \
    "http://mavenrepo.openmrs.org/nexus/service/local/repositories/modules/content/org/openmrs/module/legacyui-omod/0.3.1/legacyui-omod-0.3.1.jar" \
    -o "${OPENMRS_MODULES}/legacyui-0.3.1.omod"
RUN curl -L \
    "http://mavenrepo.openmrs.org/nexus/service/local/repositories/modules/content/org/openmrs/module/webservices.rest-omod/2.15/webservices.rest-omod-2.15.jar" \
    -o "${OPENMRS_MODULES}/webservices.rest-2.15.omod"

COPY maven/*.omod ${OPENMRS_MODULES}/
