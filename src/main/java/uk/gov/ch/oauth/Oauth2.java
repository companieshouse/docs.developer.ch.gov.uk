package uk.gov.ch.oauth;

import com.nimbusds.jose.Payload;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
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
     * Encodes a URI with a nonce according to a JWE encoding algorithm
     *
     * @return JWE encoded string, comprised of the return URI and a nonce
     */
    @Override
    public String oauth2EncodeState(final String returnUri,
            final String nonce,
            final String attributeName) {
        return oAuth2StateHandler.oauth2EncodeState(returnUri, nonce, attributeName);
    }

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

    @Override
    public boolean isValid(final String state, final String code) {
        final String returnedNonce = getNonceFromState(state);
        boolean valid = oauth2VerifyNonce(returnedNonce);
        if (!valid) {
            LOGGER.error("Invalid nonce value in state");
        } else {
            valid = extractUserProfile(code);
            if (!valid) {
                LOGGER.error("No user profile returned in OAuth");
            }
        }
        return valid;
    }

    private boolean extractUserProfile(final String code) {
        final UserProfileResponse userProfileResponse = fetchUserProfile(code);
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

    private UserProfileResponse fetchUserProfile(final String code) {
        final OAuthToken oauthToken = requestOAuthToken(code);

        UserProfileResponse userProfile = requestUserProfile(oauthToken);
        if ((userProfile.getId() == null) || userProfile.getId().isEmpty()) {
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

    private UserProfileResponse requestUserProfile(final OAuthToken oauthToken) {
        LOGGER.debug("Requesting User Profile");
        final URI profileUrl = URI.create(identityProvider.getProfileUrl());

        final WebClient webClient = WebClient.create();
        final Mono<UserProfileResponse> userProfileResponse = webClient.get()
                .uri(profileUrl)
                .headers(h -> h.setBearerAuth(oauthToken.getToken()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(response -> response.bodyToMono(UserProfileResponse.class));
        return userProfileResponse.block(timeoutDuration);
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
}