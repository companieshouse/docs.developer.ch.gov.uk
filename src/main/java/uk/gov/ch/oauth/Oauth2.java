package uk.gov.ch.oauth;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
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

    @Autowired
    public Oauth2(final IIdentityProvider identityProvider, SessionFactory sessionFactory) {
        this.identityProvider = identityProvider;
        this.sessionFactory = sessionFactory;
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

        final JSONObject payloadJson = new JSONObject();
        payloadJson.put(attributeName, returnUri);
        payloadJson.put("nonce", nonce);

        final Payload payload = new Payload(payloadJson);
        final JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256);
        final JWEObject jweObject = new JWEObject(header, payload);

        try {
            final DirectEncrypter encrypter = new DirectEncrypter(identityProvider.getRequestKey());
            jweObject.encrypt(encrypter);
        } catch (final JOSEException e) {
            LOGGER.error("Could not encode OAuth state", e);
            return null;
        }

        return jweObject.serialize();
    }

    public String oauth2EncodeState(final String returnUri,
            final Session session,
            final String attributeName) {
        return oauth2EncodeState(returnUri, nonceGenerator.setNonceForSession(session),
                attributeName);
    }


    /**
     * Given a state encapsulating a JWE token, decode it into a {@link com.nimbusds.jose.Payload}
     */
    @Override
    public Payload oauth2DecodeState(final String state) {
        Payload payload;
        try {
            final JWEObject jweObject = JWEObject.parse(state);

            final byte[] key = identityProvider.getRequestKey();
            jweObject.decrypt(new DirectDecrypter(key));
            payload = jweObject.getPayload();
        } catch (final Exception e) {
            LOGGER.error("Could not decode OAuth state", e);
            payload = null;
        }
        return payload;
    }

    /**
     * Verify's Nonce against Session Nonce
     *
     * @param nonce string to verify is correct
     * @return true if Nonce values match false otherwise
     */
    public boolean oauth2VerifyNonce(final String nonce) {
        boolean retval = false;
        if (nonce != null) {
            retval = nonce.equals(getSessionNonce());
        }
        return retval;
    }

    /**
     * Extract the OAuth2 Nonce from the current session
     * Removes Nonce value so can be validated once and replay attacks are more difficult
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

    public UserProfileResponse getUserProfile(final String code, final Session chSession) {
        LOGGER.debug("Requesting User Profile");

        final OAuthToken oauthToken = getOAuthToken(code);
        final WebClient webClient = WebClient.create();
        final URI profileUrl = URI.create(identityProvider.getProfileUrl());

        final UserProfileResponse userProfile =
                getUserProfileResponse(oauthToken, webClient, profileUrl);

        final Map<String, Object> signInData = oauthToken.saveAccessToken();
        userProfile.addUserProfileToMap(signInData);
        signInData.put(SessionKeys.SIGNED_IN.getKey(), 1);
        final String signInInfoKey = SessionKeys.SIGN_IN_INFO.getKey();
        final Map<String, Object> sData = chSession.getData();

        sData.merge(signInInfoKey, signInData, Oauth2::updateSignIn);

        return userProfile;
    }

    private UserProfileResponse getUserProfileResponse(OAuthToken oauthToken, WebClient webClient,
            URI profileUrl) {
        final Mono<UserProfileResponse> userProfileResponse = webClient.get()
                .uri(profileUrl)
                .headers(h -> h.setBearerAuth(oauthToken.getToken()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(response -> response.bodyToMono(UserProfileResponse.class));
        return userProfileResponse.block(timeoutDuration);
    }

    OAuthToken getOAuthToken(String code) {
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