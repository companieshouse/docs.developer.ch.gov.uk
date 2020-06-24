package uk.gov.ch.oauth;

import uk.gov.ch.oauth.exceptions.UnauthorisedException;
import uk.gov.companieshouse.session.Session;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface IOAuthCoordinator {

    /**
     * Will return the post-callback redirect Url so long as there are no errors in the params parameter,
     * else an UnauthorisedException is thrown.
     *
     * @param response The HttpServletResponse.
     * @param params   A Map of Strings each with a String key.
     * @return A String that represents a Url
     * @throws UnauthorisedException Will be thrown if there is an 'error' String in the params Map.
     */
    String getPostCallbackRedirectURL(HttpServletResponse response, Map<String, String> params)
            throws UnauthorisedException;

    /**
     * Calls the getRedirectUriPage method from the oauth library.
     *
     * @return The redirect Uri so that it can be assigned to a parameter for the sendRedirect() method
     * of HttpServletResponse.
     */
    String getSignoutUri();

    /**
     * Calls the invalidateSession method from the oauth library.
     *
     * @param chSession The session that will be passed in so that the invalidateSession(Session session)
     *                  from the Oauth2 class can be called.
     */
    void invalidateSession(Session chSession);
}
