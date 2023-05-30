ARG IMAGE_VERSION="latest"
FROM 416670754337.dkr.ecr.eu-west-2.amazonaws.com/ci-open-jdk8-maven-build:${IMAGE_VERSION}

ARG APPLICATION_NAME=docs.developer.ch.gov.uk
ARG PORT="8080"
ARG JAVA_MEM_ARGS=""

WORKDIR /opt
COPY /app .
COPY docker_start.sh .

CMD ["./docker_start.sh"]