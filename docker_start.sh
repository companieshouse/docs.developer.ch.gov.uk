#!/bin/bash
#
# Start script for docs.developer.ch.gov.uk

PORT=8080

exec java -jar -Dserver.port="${PORT}" "docs.developer.ch.gov.uk.jar"
#exec java ${JAVA_MEM_ARGS} -jar -Dserver.port="${PORT}" -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 "${APP_DIR}/docs.developer.ch.gov.uk.jar"