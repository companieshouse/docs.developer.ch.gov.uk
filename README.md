# **docs.developer.ch.gov.uk**

Static General Documentation pages for the Companies House Developer Hub. This application is written using the [Spring Boot](http://projects.spring.io/spring-boot/) Java framework.

## Requirements

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)
- [MongoDB](https://www.mongodb.com/)

## Getting Started
1. Run `make`
2. Run `./start.sh`

## Running in vagrant
If running in vagrant you can start all developer-hub services by using:
- `ubic start chs.dev-hub`

Or start developer-hub by using:
- `ubic start chs.dev-hub.docs-developer-service`

Service can be accessed using the following link: http://dev.chs-dev.internal:4904

## Docker
To build a Docker image run the following command:

```
mvn compile jib:dockerBuild
```

This will output a Docker imaged named: `169942020521.dkr.ecr.eu-west-1.amazonaws.com/local/docs.developer.ch.gov.uk`

You can specify a different image name run `mvn compile jib:dockerBuild -Dimage=<YOUR NAME HERE>`

## Environment Variables
The following is a list of mandatory environment variables for the service to run:

| Name | Description |
|------|-------------|
OAUTH2_REDIRECT_URI | The OAuth2 callback url
CHS_DEVELOPER_CLIENT_ID | OAuth2 Client ID
CHS_DEVELOPER_CLIENT_SECRET | OAuth2 Client Secret
DEVELOPER_OAUTH2_REQUEST_KEY | OAuth2 Request Key
COOKIE_SECURE_ONLY | Determines whether cookie is sent via a secure protocol
REDIRECT_URI | Redirect url post callback & signout