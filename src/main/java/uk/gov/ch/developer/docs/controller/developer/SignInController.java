package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import java.security.SecureRandom;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.session.SessionFactory;
import uk.gov.ch.oauth.IIdentityProvider;
import uk.gov.ch.oauth.IOauth;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.handler.SessionHandler;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

@Controller
@RequestMapping("${signin.url}")
public class SignInController {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");
    private final SecureRandom random = new SecureRandom();

    @Autowired
    IOauth oauth;

    @Autowired
    IIdentityProvider identityProvider;

    @Autowired
    private SessionFactory sessionFactory;

    public SignInController() {
    }

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
    }

    /**
     * Redirects to a URI for the user to authenticate themselves
     *
     * @param session The user's session, retrieved from context
     */
    protected void redirectForAuth(final Session session, final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {

        final String originalRequestUrl = getRequestURL(request);
        final String nonce = generateSessionNonce(session);

        // Build oauth uri and redirect
        final String state = oauth.oauth2EncodeState(originalRequestUrl, nonce, "content");
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
        if (email == null) {
            LOGGER.debug("No scope supplied");
        }
        final String hint = oauth.oauth2EncodeState(email, nonce, "email");
        final String authUrl = identityProvider.getAuthorisationUrl(originalRequestUri, scope);
        String authUri = authUrl + "&reauthenticate=force"
                + "&hint="
                + hint;
        return authUri;
    }

    //TODO Move this onto the OAuth instance
    private String generateSessionNonce(final Session session) {
        // Generate and store a nonce in the session
        Session sessionToUpdate = session;
        if (sessionToUpdate == null) {
            sessionToUpdate = sessionFactory.createSession();
        }
        String nonce = generateNonce();
        sessionToUpdate.getData().put(SessionKeys.NONCE.getKey(), nonce);
        return nonce;
    }

    /**
     * Retrieves email from a session if present, else returns an empty string
     *
     * @param session User session from which to retrieve the email address
     * @return email
     */
    private String getEmailFromSession(final Session session) {

        String email = "";

        SignInInfo signInInfo = new SignInInfo();
        if (session != null) {
            signInInfo = session.getSignInInfo();
        }
        final UserProfile userProfile = signInInfo.getUserProfile();
        if (userProfile != null) {
            email = userProfile.getEmail();
        }
        return email;
    }

    //TODO Move this into the OAuth object

    /**
     * Generates a secure unique key
     *
     * @return Base64 encoded unique key
     */
    private String generateNonce() {
        byte[] bytes = new byte[5];
        random.nextBytes(bytes);
        return Base64.encodeBase64URLSafeString(bytes);
    }

}
