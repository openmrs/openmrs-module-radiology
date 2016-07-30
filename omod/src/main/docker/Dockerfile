FROM teleivo/openmrs-platform:2.0.0

# Get radiology modules dependencies
RUN curl -L \
    "https://github.com/teleivo/tmp-dependencies-openmrs-module-radiology/raw/master/legacyui-0.3.1.omod" \
    -o "${OPENMRS_MODULES}/legacyui-0.3.1.omod"
RUN curl -L \
    "https://github.com/teleivo/tmp-dependencies-openmrs-module-radiology/raw/master/webservices.rest-2.15-SNAPSHOT.36ddda.omod" \
    -o "${OPENMRS_MODULES}/webservices.rest-2.15-SNAPSHOT.36dda.omod"

COPY maven/*.omod ${OPENMRS_MODULES}/
