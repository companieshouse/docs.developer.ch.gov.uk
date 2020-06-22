package uk.gov.ch.oauth;

import uk.gov.ch.oauth.exceptions.UnauthorisedException;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.identity.IdentityProvider;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class OAuthCoordinator implements IOAuthCoordinator {

    private final Logger logger;
    private IOauth oAuth;
    private SessionFactory sessionFactory;
    private IIdentityProvider identityProvider;
    private EnvironmentReader environmentReader;

    public OAuthCoordinator(String loggerNameSpace) {
        this.logger = LoggerFactory.getLogger(loggerNameSpace);
    }


    @Override
    public String getPostCallbackRedirectURL(HttpServletResponse response,
                                             Map<String, String> params) throws UnauthorisedException {
        if (params.containsKey("error")) {
            return logAuthServerError(params.get("error"), response);
        } else {
            return validateResponse(params.get("state"), params.get("code"), response);
        }
    }

    @Override
    public String getSignoutUri() {
        return getIdentityProvider().getRedirectUriPage();
    }

    @Override
    public void invalidateSession(Session session) {
        getOAuth().invalidateSession(session);
    }

    String validateResponse(String state, String code, HttpServletResponse response)
            throws UnauthorisedException {

        final boolean valid = state != null && code != null &&
                getOAuth().validate(state, code, response);
        if (valid) {
            return identityProvider.getRedirectUriPage();
        } else {
            return logAuthServerError("Invalid access token", response);
        }
    }

    String logAuthServerError(String error, HttpServletResponse response)
            throws UnauthorisedException {
        logger.error("Error in OAUTH Callback journey: " + error);
        try {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, error);
        } catch (IOException e) {
            logger.error(e);
        }
        throw new UnauthorisedException(error);
    }

    public IOauth getOAuth() {
        if (this.oAuth == null) {
            this.oAuth = new Oauth2(getIdentityProvider(), getSessionFactory());
        }
        return oAuth;
    }

    private SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new SessionFactory();
        }
        return sessionFactory;
    }

    private IIdentityProvider getIdentityProvider() {
        if (identityProvider == null) {
            identityProvider = new IdentityProvider(getEnvironmentReader());
        }
        return identityProvider;
    }

    private EnvironmentReader getEnvironmentReader() {
        if (environmentReader == null) {
            environmentReader = new EnvironmentReaderImpl();
        }
        return environmentReader;
    }
}