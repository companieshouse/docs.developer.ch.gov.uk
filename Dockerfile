FROM 416670754337.dkr.ecr.eu-west-2.amazonaws.com/ci-corretto-build-21

FROM 416670754337.dkr.ecr.eu-west-2.amazonaws.com/ci-corretto-runtime-21

VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} .
ENTRYPOINT ["java","-jar","docs.developer.ch.gov.uk.jar"]
