package uk.gov.ch.oauth;

import uk.gov.companieshouse.session.Session;

import javax.servlet.http.HttpServletResponse;

/**
 * Handler for the OAuth sight in flow based on the requirements of the CH version of OAuth2
 */
public interface IOauth {

    /**
     * Only referenced in tests and an unused method in {@link uk.gov.ch.developer.docs.controller.developer.SignInController}
     *
     * @param attributeName always "email"
     * @return ?
     * @see uk.gov.ch.developer.docs.controller.developer.SignInController#createAuthoriseURIWithForceAndHint(String,
     * String, String, String)
     */
    String oauth2EncodeState(final String returnUri,
            final String nonce,
            final String attributeName);

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
     * @return true if inputs are valid and relevant security information has been obtained
     */
    boolean validate(String state, String code, HttpServletResponse httpServletResponse);
}
