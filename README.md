# **docs.developer.ch.gov.uk**

Static General Documentation pages for the Companies House Developer Hub. This application is written using the [Spring Boot](http://projects.spring.io/spring-boot/) Java framework.

## Requirements

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)

## Getting Started
1. Run `make`
2. Run `./start.sh`

Service can be accessed using the following link: http://dev.chs-dev.internal:4904

## Docker
To build a Docker image run the following command:

```
mvn compile jib:dockerBuild
```

This will output a Docker imaged named: `416670754337.dkr.ecr.eu-west-2.amazonaws.com/docs.developer.ch.gov.uk`

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

### Endpoints

| Method | Path                                                                  | Description                                                 |
|--------|-----------------------------------------------------------------------|-------------------------------------------------------------|
| *      | /                                                                     | Companies House API overview                                |
| *      | /api-testing                                                          | API testing                                                 |
| GET    | /api/docs                                                             | ( redirects to '/' )                                        |
| *      | /authentication                                                       | API authentication                                          |
| *      | /create-account                                                       | How to Create a Companies House Account                     |
| *      | /developer-guidelines                                                 | Developer guidelines                                        |
| *      | /error                                                                | Sorry we are experiencing technical difficulties            |
| *      | /get-started                                                          | Get started                                                 |
| *      | /how-to-create-an-application                                         | How to create an application                                |
| *      | /manage-applications                                                  | Application Overview                                        |
| GET    | /oauth2/user/callback                                                 | ( redirects )                                               |
| *      | /overview                                                             | Companies House REST API                                    |
| GET    | /signin                                                               | Sign in to Companies House ( redirect )                     |
| GET    | /signout                                                              |                                                             |

## Related Services
For full functionality of the developer hub this also requires [applications.api.identity.ch.gov.uk](https://github.com/companieshouse/applications.api.identity.ch.gov.uk)
and [applications.developer.web.ch.gov.uk](https://github.com/companieshouse/applications.developer.web.ch.gov.uk). For more information, please see their readme documents.
