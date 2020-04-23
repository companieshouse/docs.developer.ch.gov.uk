package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Controller
@RequestMapping("${signin.url}")
public class SignInController {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");

    @Autowired
    IOauth oauth;

    @Autowired
    IIdentityProvider identityProvider;

    private static String getRequestURL(final HttpServletRequest request) {
        // Find the original requested url
        final StringBuilder originalRequestUrl = new StringBuilder(
                request.getRequestURL());
        final String queryString = request.getQueryString();
        if (queryString != null) {
            originalRequestUrl.append("?").append(queryString);
        }
        return originalRequestUrl.toString();
    }

    @GetMapping
    public void getSignIn(final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse) throws IOException {

        final Session chSession = (Session) httpServletRequest
                .getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);
        // Redirect for user authentication (no scope specified)
        redirectForAuth(chSession, httpServletRequest, httpServletResponse);
        LOGGER.debugContext("Sign In", "Sign In", chSession.getData());
    }

    /**
     * Redirects to a URI for the user to authenticate themselves
     *
     * @param session The user's session, retrieved from context
     */
    void redirectForAuth(final Session session, final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {

        final String originalRequestUrl = getRequestURL(request);

        // Build oauth uri and redirect
        final String state = oauth.oauth2EncodeState(originalRequestUrl, session, "content");
        final String authoriseUri = identityProvider.getAuthorisationUrl(state);
        response.sendRedirect(authoriseUri);
    }

    /**
     * Constructs the authorisation URI with additional force and hint
     *
     * @param originalRequestUri Original URI from which to redirect
     * @param scope Scope of the request
     * @param email Email address from the session, empty if not present
     * @return Authorisation URI
     */
    protected String createAuthoriseURIWithForceAndHint(final String originalRequestUri,
            final String scope, final String nonce, final String email) {
        if (email == null) {
            LOGGER.debug("No email supplied");
        }
        if (scope == null) {
            LOGGER.debug("No scope supplied");
        }
        final String hint = oauth.oauth2EncodeState(email, nonce, "email");
        final String authUrl = identityProvider.getAuthorisationUrl(originalRequestUri, scope);
        return authUrl + "&reauthenticate=force"
                + "&hint="
                + hint;
    }
}
