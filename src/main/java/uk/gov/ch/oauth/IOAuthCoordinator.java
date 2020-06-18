package uk.gov.ch.oauth;

import uk.gov.ch.oauth.exceptions.UnauthorisedException;
import uk.gov.companieshouse.session.Session;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface IOAuthCoordinator {

    String getPostCallbackRedirectURL(HttpServletResponse response, Map<String, String> params)
            throws UnauthorisedException;

    /**
     * @return The redirect Uri so that it can be assigned to a parameter for the sendRedirect() method
     * of HttpServletResponse.
     */
    String getSignoutUri();

    /**
     * @param chSession The session that will be passed in so that the invalidateSession(Session session)
     *                  from the Oauth2 class can be called.
     */
    void invalidateSession(Session chSession);
}
