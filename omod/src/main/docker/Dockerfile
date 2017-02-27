FROM teleivo/openmrs-platform:2.0.0-1

# Get radiology modules dependencies
RUN curl -L \
    "https://openmrs.jfrog.io/openmrs/omods/omod/legacyui-1.2.omod" \
    -o "${OPENMRS_MODULES}/legacyui-1.2.omod"
RUN curl -L \
    "https://openmrs.jfrog.io/openmrs/omods/omod/webservices.rest-2.17.omod" \
    -o "${OPENMRS_MODULES}/webservices.rest-2.17.omod"

COPY maven/*.omod ${OPENMRS_MODULES}/
