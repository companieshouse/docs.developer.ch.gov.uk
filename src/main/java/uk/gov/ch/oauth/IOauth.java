package uk.gov.ch.oauth;

import uk.gov.companieshouse.session.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handler for the OAuth sight in flow based on the requirements of the CH version of OAuth2
 */
public interface IOauth {

    /**
     * Encodes a URI with suitable parameters to enable sign in flow during the redirect for
     * authentication
     *
     * @param returnUri URI to which the signed in user should be returned after successful sign-in
     * @param sessions current session state
     * @param attributeName which type of attribute to use. Currently only "content"
     * @return A uri string with suitably encoded parameters
     */
    String encodeSignInState(final String returnUri,
            final Session sessions,
            final String attributeName);

    /**
     * On a callback, determine whether properties such as the Nonce is consistent and that the code
     * and state contain values that can be decoded.
     *
     * @param state State string from the callback parameter
     * @param code Code string from the callback parameter
     * @param httpServletResponse for security the cookies and state of response need to be
     * manipulated
     * @return true if inputs are valid and relevant security information has been obtained
     */
    boolean validate(String state, String code, HttpServletResponse httpServletResponse);

    /**
     * Removes SignIn info and ZXS info from a signed-in session.
     *
     * @param chSession The active session.
     */
    void invalidateSession(Session chSession);

    /**
     * This method takes a HttpRequest and returns the state by getting the original request url from
     * getOriginalRequestUrl() and then calling encodeSignInState() to get the state.
     *
     * @param request HttpRequest
     * @return The state as a String
     */
    String prepareState(final HttpServletRequest request);
}
