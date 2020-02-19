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
import uk.gov.ch.developer.docs.oauth.IIdentityProvider;
import uk.gov.ch.developer.docs.oauth.IOauth;
import uk.gov.ch.developer.docs.session.SessionFactory;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.handler.SessionHandler;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

@Controller
@RequestMapping("/signin")
public class SignInController {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");
    private static final String OAUTH_COMPANY_SCOPE_PREFIX =
            "https://api.companieshouse.gov.uk/company/";
    private final SecureRandom random = new SecureRandom();
    @Autowired
    IIdentityProvider identityProvider;
    @Autowired
    IOauth oauth;
    // private String base64Key;
    private String authorizationUri;
    private String clientId;
    private String redirectUri;
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    public SignInController(final EnvironmentReader reader) {
        //  this.base64Key = reader.getMandatoryString("OAUTH2_REQUEST_KEY");
        this.authorizationUri = reader.getMandatoryString("OAUTH2_AUTH_URI");
        this.clientId = reader.getMandatoryString("OAUTH2_CLIENT_ID");
        this.redirectUri = reader.getMandatoryString("OAUTH2_REDIRECT_URI");
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

    private static String getAuthorisationUrl(String scope, String state, String authorizationUri,
            String clientId, String redirectUri) {
        StringBuilder sb = new StringBuilder();
        sb.append(authorizationUri);
        sb.append("?");
        sb.append("client_id=");
        sb.append(clientId);
        sb.append("&redirect_uri=");
        sb.append(redirectUri);
        sb.append("&response_type=code");
        if (scope != null) {
            sb.append("&scope=");
            sb.append(scope);
        }

        sb.append("&state=");
        sb.append(state);

        return sb.toString();
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
        String authoriseUri;

        final String state = oauth.oauth2EncodeState(originalRequestUrl, nonce, "content");
        authoriseUri = identityProvider.getAuthorisationUrl(state);
        response.sendRedirect(authoriseUri);
    }

    private String generateSessionNonce(Session session) {
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
     * Redirects to a URI for the user to authenticate themselves
     *
     * @param session The user's session, retrieved from context
     * @param companyNumber The company number a user is viewing
     * @param force Boolean determining whether to force re-authentication
     */
    protected void redirectForAuth(final Session session, final HttpServletRequest request,
            final HttpServletResponse response, final String companyNumber, final boolean force)
            throws IOException {

        final String originalRequestUrl = getRequestURL(request);

        // Set the scope
        String scope = null;
        if (companyNumber != null) {
            scope = OAUTH_COMPANY_SCOPE_PREFIX + companyNumber;
        }

        // Generate and store a nonce in the session
        String nonce = generateSessionNonce(session);

        // Build oauth uri and redirect
        String authoriseUri;
        if (force) {
            authoriseUri = createAuthoriseURIWithForceAndHint(originalRequestUrl, scope,
                    nonce, getEmailFromSession(session));
        } else {
            authoriseUri = createAuthoriseURI(originalRequestUrl, scope, nonce);
        }
        response.sendRedirect(authoriseUri);
    }

    /**
     * Retrieves email from a session if present, else returns an empty string
     *
     * @param session User session from which to retrieve the email address
     * @return email
     */
    private String getEmailFromSession(Session session) {

        String email = "";

        SignInInfo signInInfo = new SignInInfo();
        if (session != null) {
            signInInfo = session.getSignInInfo();
        }
        UserProfile userProfile = signInInfo.getUserProfile();
        if (userProfile != null) {
            email = userProfile.getEmail();
        }
        return email;
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
            final String scope,
            final String nonce, final String email) {

        final String sb =
                createAuthoriseURI(originalRequestUri, scope, nonce) + "&reauthenticate=force"
                        + "&hint="
                        + oauth.oauth2EncodeState(email, nonce, "email");
        return sb;
    }

    /**
     * Constructs the authorisation URI
     *
     * @param originalRequestUri Original URI from which to redirect
     * @param scope Scope of the request
     * @return Authorisation URI
     */
    protected String createAuthoriseURI(String originalRequestUri, String scope, String nonce) {

        final String state = oauth.oauth2EncodeState(originalRequestUri, nonce, "content");
        return getAuthorisationUrl(scope, state, authorizationUri, clientId, redirectUri);
    }

    /**
     * Constructs the authorisation URI
     *
     * @param originalRequestUri Original URI from which to redirect
     * @return Authorisation URI
     */
    protected String createAuthoriseURI(final String originalRequestUri, final String nonce) {
        final String state = oauth.oauth2EncodeState(originalRequestUri, nonce, "content");
        return identityProvider.getAuthorisationUrl(state);
    }

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
