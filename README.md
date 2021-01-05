# **docs.developer.ch.gov.uk**

Static General Documentation pages for the Companies House Developer Hub.

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

## Environment Variables
The supported environmental variables have been categorised by use case and are as follows.

| Name | Description |
|------|-------------|
OAUTH2_REDIRECT_URI | The OAuth2 callback url
CHS_DEVELOPER_CLIENT_ID | OAuth2 Client ID
CHS_DEVELOPER_CLIENT_SECRET | OAuth2 Client Secret
DEVELOPER_OAUTH2_REQUEST_KEY | OAuth2 Request Key
COOKIE_SECURE_ONLY | Determines whether cookie is sent via a secure protocol
REDIRECT_URI | Redirect url post callback & signout