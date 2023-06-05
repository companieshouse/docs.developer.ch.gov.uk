#!/bin/bash
#
# Start script for docs.developer.ch.gov.uk

PORT=8080

exec java -jar -Dserver.port="${PORT}" "docs.developer.ch.gov.uk.jar"