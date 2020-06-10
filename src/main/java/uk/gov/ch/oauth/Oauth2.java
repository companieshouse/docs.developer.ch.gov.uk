package uk.gov.ch.oauth;

import static uk.gov.companieshouse.session.handler.SessionHandler.buildSessionCookie;

import com.nimbusds.jose.Payload;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.nonce.NonceGenerator;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.ch.oauth.tokens.OAuthToken;
import uk.gov.ch.oauth.tokens.UserProfileResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;

@Component
public class Oauth2 implements IOauth {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");
    private final IIdentityProvider identityProvider;
    private final SessionFactory sessionFactory;
    private final Duration timeoutDuration = Duration.ofSeconds(10L);
    private final NonceGenerator nonceGenerator = new NonceGenerator();
    private final OAuth2StateHandler oAuth2StateHandler;
    private static final String SIGN_IN_INFO = SessionKeys.SIGN_IN_INFO.getKey();



    @Autowired
    public Oauth2(final IIdentityProvider identityProvider, final SessionFactory sessionFactory) {
        this.identityProvider = identityProvider;
        this.sessionFactory = sessionFactory;
        oAuth2StateHandler = new OAuth2StateHandler(this.identityProvider);
    }

    @SuppressWarnings("unchecked")
    // This is necessary as the original data on the session is untyped, but expected to be of the correct types
    private static Map<String, Object> updateSignIn(final Object sInf, final Object sio) {
        final Map<String, Object> original = (Map<String, Object>) sInf;
        final Map<String, Object> extras = (Map<String, Object>) sio;
        original.putAll(extras);
        return original;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encodeSignInState(final String returnUri,
            final Session session,
            final String attributeName) {
        return oAuth2StateHandler
                .oauth2EncodeState(returnUri, nonceGenerator.setNonceForSession(session),
                        attributeName);
    }

    /**
     * Given a state encapsulating a JWE token, decode it into a {@link com.nimbusds.jose.Payload}
     */
    private Payload oauth2DecodeState(final String state) {
        return oAuth2StateHandler.oauth2DecodeState(state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(final String state, final String code,
            final HttpServletResponse httpServletResponse) {
        final String returnedNonce = getNonceFromState(state);
        boolean validNonce = oauth2VerifyNonce(returnedNonce);
        if (validNonce) {
            boolean validProfile = extractUserProfile(code, httpServletResponse);
            if (validProfile) {
                return true;
            }
            LOGGER.error("No user profile returned in OAuth");
        } else {
            LOGGER.error("Invalid nonce value in state");
        }
        return false;
    }

    private boolean extractUserProfile(final String code,
            final HttpServletResponse httpServletResponse) {
        final UserProfileResponse userProfileResponse = fetchUserProfile(code, httpServletResponse);
        return userProfileResponse != null;
    }

    private String getNonceFromState(final String state) {
        final Payload payload = oauth2DecodeState(state);
        final JSONObject jsonObject = payload.toJSONObject();
        return jsonObject.getAsString("nonce");
    }

    /**
     * Verify's Nonce against Session Nonce
     *
     * @param nonce string to verify is correct
     * @return true if Nonce values match false otherwise
     */
    private boolean oauth2VerifyNonce(final String nonce) {
        boolean retval = false;
        if (nonce != null) {
            retval = nonce.equals(getSessionNonce());
        }
        return retval;
    }

    /**
     * Extract the OAuth2 Nonce from the current session Removes Nonce value so can be validated
     * once and replay attacks are more difficult
     *
     * @return The Nonce String from within the session or null if not found.
     */
    private String getSessionNonce() {
        String oauth2Nonce = null;
        try {
            final Map<String, Object> data = sessionFactory.getSessionDataFromContext();
            oauth2Nonce = (String) data.remove(SessionKeys.NONCE.getKey());
            LOGGER.debug("Extracting nonce value");
        } catch (final Exception e) {
            LOGGER.error("Unable to extract OAuth2 Nonce from session", e);
        }
        return oauth2Nonce;
    }

    private UserProfileResponse fetchUserProfile(final String code,
            final HttpServletResponse httpServletResponse) {
        final OAuthToken oauthToken = requestOAuthToken(code);
        regenerateSessionID(httpServletResponse);
        final UserProfileResponse userProfile = requestUserProfile(oauthToken);
        if ((null == userProfile.getId()) || userProfile.getId().isEmpty()) {
            return null;
        }
        final Map<String, Object> signInData = oauthToken.saveAccessToken();
        userProfile.addUserProfileToMap(signInData);
        signInData.put(SessionKeys.SIGNED_IN.getKey(), 1);
        final String signInInfoKey = SessionKeys.SIGN_IN_INFO.getKey();
        final Map<String, Object> sData = sessionFactory.getSessionDataFromContext();

        sData.merge(signInInfoKey, signInData, Oauth2::updateSignIn);

        return userProfile;
    }

    /**
     * Use an oauth token to request user profile information from the OAuth server
     *
     * @param oauthToken Token to exchange for user date
     * @return user profile data from the account service or an empty {@link UserProfileResponse} if
     * the data was incompatible
     */
    protected UserProfileResponse requestUserProfile(final OAuthToken oauthToken) {
        LOGGER.debug("Requesting User Profile");
        final URI profileUrl = URI.create(identityProvider.getProfileUrl());

        final WebClient webClient = WebClient.create();
        final Mono<UserProfileResponse> userProfileResponse = webClient.get()
                .uri(profileUrl)
                .headers(h -> h.setBearerAuth(oauthToken.getToken()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(response -> validResponse(response));
        return userProfileResponse.block(timeoutDuration);
    }

    /**
     * Checks client response for an invalid status code
     * 
     * @param response
     * @return If code is invalid, returns a Mono of an empty {@link UserProfileResponse} else a
     *         Mono with correct response body
     */
    private Mono<UserProfileResponse> validResponse(ClientResponse response) {
        if (response.statusCode().isError()) {
            return Mono.just(new UserProfileResponse());
        }
        return response.bodyToMono(UserProfileResponse.class);
    }

    OAuthToken requestOAuthToken(String code) {
        LOGGER.debug("Getting OAuth Token");

        final WebClient webClient = WebClient.create();

        final URI tokenUrl = URI.create(identityProvider.getTokenUrl());
        final Mono<String> postRequest = Mono.just(identityProvider.getPostRequestBody(code));
        final BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInsert = BodyInserters
                .fromPublisher(postRequest, String.class);

        final Mono<OAuthToken> postReq = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(bodyInsert)
                .retrieve()
                .bodyToMono(OAuthToken.class);
        return postReq.block(timeoutDuration);
    }

    private void regenerateSessionID(HttpServletResponse httpServletResponse) {
        Session session = sessionFactory
                .regenerateSession();

        httpServletResponse.addCookie(buildSessionCookie(session));
    }

    public void invalidateSession(Session chSession) {
        final Map<String, Object> sessionData = chSession.getData();
        if (chSession.getSignInInfo().isSignedIn()) {
            removeSignInInfo(sessionData);
            removeZXSInfo(sessionData);
        }
    }

    private void removeSignInInfo(Map<String, Object> sessionData) {
        final Map<String, Object> signInInfo =
                (Map<String, Object>) sessionData.get(SIGN_IN_INFO);
        signInInfo.replace(SessionKeys.SIGNED_IN.getKey(), 1, 0);
        sessionData.remove(SIGN_IN_INFO);
    }

    private void removeZXSInfo(Map<String, Object> sessionData) {
        final String zxsKey = (String) sessionData.get(".zxs_key");// This is the id of cookie stored in redis
        if (zxsKey != null) {
            LOGGER.trace("Deleting ZXS info from cache");
            sessionFactory.getDefaultStore().delete(zxsKey);
        }
    }

    private String getOriginalRequestURL(final HttpServletRequest request) {
        final StringBuilder originalRequestUrl = new StringBuilder(
                request.getRequestURL());
        final String queryString = request.getQueryString();
        if (queryString != null) {
            originalRequestUrl.append("?").append(queryString);
        }
        return originalRequestUrl.toString();

    }

    public String prepareState(final HttpServletRequest request) {
        String originalURL = getOriginalRequestURL(request);
        return encodeSignInState(originalURL, sessionFactory.getSessionFromContext(), "content");
    }

}
