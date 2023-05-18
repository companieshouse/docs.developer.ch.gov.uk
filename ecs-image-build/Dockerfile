ARG IMAGE_VERSION="latest"
FROM 416670754337.dkr.ecr.eu-west-2.amazonaws.com/ci-open-jdk8-maven-build:${IMAGE_VERSION}

ARG S3_RELEASE_BUCKET=""
ARG APPLICATION_NAME=docs.developer.ch.gov.uk
ARG APPLICATION_VERSION=""
ARG AWS_ACCESS_KEY_ID=""
ARG AWS_SECRET_ACCESS_KEY=""
ARG AWS_REGION="eu-west-2"
ARG FILE_NAME=""
ARG PORT="8080"
ARG JAVA_MEM_ARGS=""

WORKDIR /opt
COPY docker_start.sh .

RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" \
    && unzip awscliv2.zip \
    && ./aws/install \
    && aws s3 cp s3://${S3_RELEASE_BUCKET}/${APPLICATION_NAME}/${FILE_NAME} . \
    && unzip ${FILE_NAME} \
    && rm -f ${FILE_NAME} awscliv2.zip

CMD ["./docker_start.sh"]