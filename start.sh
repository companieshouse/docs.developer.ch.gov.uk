#!/bin/bash
#
# Start script for docs.developer.ch.gov.uk

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [[ -z "${MESOS_SLAVE_PID}" ]]; then
  source ~/.chs_env/private_env
  source ~/.chs_env/global_env
  source ~/.chs_env/docs.developer.ch.gov.uk/env

  PORT="${DOC_DEVELOPER_SERVICE_PORT}"
else
  PORT="$1"
  CONFIG_URL="$2"
  ENVIRONMENT="$3"
  APP_NAME="$4"

  source /etc/profile

  echo "Downloading environment from: ${CONFIG_URL}/${ENVIRONMENT}/${APP_NAME}"
  wget -O "${APP_DIR}/private_env" "${CONFIG_URL}/${ENVIRONMENT}/private_env"
  wget -O "${APP_DIR}/global_env" "${CONFIG_URL}/${ENVIRONMENT}/global_env"
  wget -O "${APP_DIR}/app_env" "${CONFIG_URL}/${ENVIRONMENT}/${APP_NAME}/env"
  source "${APP_DIR}/private_env"
  source "${APP_DIR}/global_env"
  source "${APP_DIR}/app_env"
fi

#exec java ${JAVA_MEM_ARGS} -jar -Dserver.port="${PORT}" "${APP_DIR}/docs.developer.ch.gov.uk.jar"
exec java ${JAVA_MEM_ARGS} -jar -Dserver.port="${PORT}" -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 "${APP_DIR}/docs.developer.ch.gov.uk.jar"
