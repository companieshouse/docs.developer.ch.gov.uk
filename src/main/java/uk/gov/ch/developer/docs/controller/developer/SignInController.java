package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import java.security.SecureRandom;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import net.minidev.json.JSONObject;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionImpl;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.handler.SessionHandler;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

@Controller
@RequestMapping("/signin")
public class SignInController {

    private static final String OAUTH_COMPANY_SCOPE_PREFIX =
            "https://api.companieshouse.gov.uk/company/";

    private static final EnvironmentReader reader = new EnvironmentReaderImpl();

    private static final String base64Key = reader.getMandatoryString("OAUTH2_REQUEST_KEY");
    private static final String authorizationUri = reader.getMandatoryString("OAUTH2_AUTH_URI");
    private static final String clientId = reader.getMandatoryString("OAUTH2_CLIENT_ID");
    private static final String redirectUri = reader.getMandatoryString("OAUTH2_REDIRECT_URI");
    private static final String secret = reader.getMandatoryString("COOKIE_SECRET");

    public static final String COOKIE_NAME = reader.getMandatoryString("COOKIE_NAME");
    private static final String COOKIE_DOMAIN_DEFAULT = ".companieshouse.gov.uk";
    private static final boolean COOKIE_SECURE_ONLY_DEFAULT = true;

//    private static final String COOKIE_DOMAIN = reader.getMandatoryString("COOKIE_DOMAIN");;

    private static final String COOKIE_SECURE_ONLY = System.getenv("COOKIE_SECURE_ONLY");

    protected Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");

    @GetMapping
    public void getSignIn(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

//        SessionConfig sess = new SessionConfig();
//        Session chSession = sess.getSession(httpServletRequest, httpServletResponse);
        
//        HttpSession session = httpServletRequest.getSession();
//        if (httpServletRequest.getParameter("JSESSIONID") != null) {
//            Cookie userCookie = new Cookie("JSESSIONID", httpServletRequest.getParameter("JSESSIONID"));
//            httpServletResponse.addCookie(userCookie);
//        } else {
//            String sessionId = session.getId();
//            Cookie userCookie = new Cookie("JSESSIONID", sessionId);
//            httpServletResponse.addCookie(userCookie);
//        }

        Session chSession = (Session) httpServletRequest.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);

        // Redirect for user authentication (no scope specified)
        redirectForAuth(chSession, httpServletRequest, httpServletResponse, null, false);
    }

    /**
     * Redirects to a URI for the user to authenticate themselves
     * 
     * @param session The user's session, retrieved from context
     * @param request
     * @param response
     * @param companyNumber The company number a user is viewing
     * @param force Boolean determining whether to force re-authentication
     * @throws IOException
     */
    protected void redirectForAuth(Session session, HttpServletRequest request,
            HttpServletResponse response, String companyNumber, boolean force) throws IOException {

        // Find the original requested url
        StringBuilder originalRequestUrl = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();
        if (queryString != null)
            originalRequestUrl.append("?").append(queryString);

        // Set the scope
        String scope = null;
        if (companyNumber != null)
            scope = OAUTH_COMPANY_SCOPE_PREFIX + companyNumber;

        // Generate and store a nonce in the session
        Session sessionToUpdate = session;

        if (sessionToUpdate == null) {
            sessionToUpdate = new SessionImpl();
        }

        String nonce = generateNonce();
        sessionToUpdate.getData().put(SessionKeys.NONCE.getKey(), nonce);
        
        // Build oauth uri and redirect
        String authoriseUri;
        if (force) {
            authoriseUri = createAuthoriseURIWithForceAndHint(originalRequestUrl.toString(), scope,
                    nonce, getEmailFromSession(session));
        } else {
            authoriseUri = createAuthoriseURI(originalRequestUrl.toString(), scope, nonce);
        }
//
////        //Put the CHS session into the request as chsSession attribute
//        request.setAttribute("chsSession", sessionToUpdate);
//        
//        response.addCookie(prepareCookieForSession(COOKIE_DOMAIN, COOKIE_SECURE_ONLY, sessionToUpdate));
//
//        //Store the CHS session
//        sessionToUpdate.store();
//        
//        
        response.sendRedirect(response.encodeRedirectURL(authoriseUri));
    }
    
    protected static Cookie prepareCookieForSession(String cookieDomain, String cookieSecureOnly,  Session chsSession) {
        Cookie chsSessionCookie = new Cookie(COOKIE_NAME, chsSession.getCookieId());
        chsSessionCookie.setMaxAge(chsSession.getExpirationPeriod());
        chsSessionCookie.setHttpOnly(true);
        chsSessionCookie.setPath("/");
        
        if(cookieDomain == null) {
            chsSessionCookie.setDomain(COOKIE_DOMAIN_DEFAULT);
        } else {
            chsSessionCookie.setDomain(cookieDomain);
        }
        
        if(cookieSecureOnly == null) {
            chsSessionCookie.setSecure(COOKIE_SECURE_ONLY_DEFAULT);
        } else {
            boolean secureOnly = "true".equalsIgnoreCase(cookieSecureOnly);
            chsSessionCookie.setSecure(secureOnly);
        }
               
        return chsSessionCookie;
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
        if (userProfile != null)
            email = userProfile.getEmail();

        return email;
    }

    /**
     * Constructs the authorisation URI with additional force and hint
     * 
     * @param originalRequestUri Original URI from which to redirect
     * @param scope Scope of the request
     * @param nonce
     * @param email Email address from the session, empty if not present
     * @return Authorisation URI
     */
    protected String createAuthoriseURIWithForceAndHint(String originalRequestUri, String scope,
            String nonce, String email) {

        StringBuilder sb = new StringBuilder(createAuthoriseURI(originalRequestUri, scope, nonce));
        sb.append("&reauthenticate=force");
        sb.append("&hint=");
        sb.append(jweEncodeWithNonce(email, nonce, "email"));

        return sb.toString();
    }

    /**
     * Constructs the authorisation URI
     * 
     * @param originalRequestUri Original URI from which to redirect
     * @param scope Scope of the request
     * @param nonce
     * @return Authorisation URI
     */
    protected String createAuthoriseURI(String originalRequestUri, String scope, String nonce) {

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
        sb.append(jweEncodeWithNonce(originalRequestUri, nonce, "content"));

        return sb.toString();
    }

    /**
     * Generates a secure unique key
     * 
     * @return Base64 encoded unique key
     */
    private String generateNonce() {

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[5];
        random.nextBytes(bytes);

        return Base64.encodeBase64URLSafeString(bytes);
    }

    /**
     * Encodes a URI with a nonce according to a JWE encoding algorithm
     * 
     * @param returnUri
     * @param nonce
     * @param attributeName
     * @return JWE encoded string, comprised of the return URI and a nonce
     */
    protected String jweEncodeWithNonce(String returnUri, String nonce, String attributeName) {

        JSONObject payloadJson = new JSONObject();
        payloadJson.put(attributeName, returnUri);
        payloadJson.put("nonce", nonce);

        Payload payload = new Payload(payloadJson);
        JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256);
        JWEObject jweObject = new JWEObject(header, payload);

        byte[] key = Base64.decodeBase64(base64Key);

        try {
            jweObject.encrypt(new DirectEncrypter(key));
        } catch (JOSEException e) {
            LOGGER.error(e, null);
        }

        return jweObject.serialize();
    }

    public String getSecret() {
        return secret;
    }

}