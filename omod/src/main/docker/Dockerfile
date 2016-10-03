FROM teleivo/openmrs-platform:2.0.0-1

# Get radiology modules dependencies
RUN curl -L \
    "http://mavenrepo.openmrs.org/nexus/service/local/repositories/modules/content/org/openmrs/module/legacyui-omod/1.1/legacyui-omod-1.1.jar" \
    -o "${OPENMRS_MODULES}/legacyui-1.1.omod"
RUN curl -L \
    "http://mavenrepo.openmrs.org/nexus/service/local/repositories/modules/content/org/openmrs/module/webservices.rest-omod/2.16/webservices.rest-omod-2.16.jar" \
    -o "${OPENMRS_MODULES}/webservices.rest-2.16.omod"

COPY maven/*.omod ${OPENMRS_MODULES}/
