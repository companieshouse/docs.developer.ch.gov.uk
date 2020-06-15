package uk.gov.ch.oauth;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import java.util.Optional;
import net.minidev.json.JSONObject;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Provides JWE specific functionality as a dedicated service for CH version of OAuth2
 */
public class OAuth2StateHandler {

    private final Logger logger;

    private final IIdentityProvider identityProvider;

    public OAuth2StateHandler(final IIdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
        EnvironmentReaderImpl environmentReader = new EnvironmentReaderImpl();
        final String oauth_logging_namespace = Optional.ofNullable(environmentReader
                .getOptionalString("OAUTH_LOGGING_NAMESPACE"))
                .orElse("oauth-signin-java-library");
        logger = LoggerFactory.getLogger(oauth_logging_namespace);
    }

    public OAuth2StateHandler(final IIdentityProvider identityProvider, Logger logger) {
        this.identityProvider = identityProvider;
        this.logger = logger;
    }

    /**
     * Encodes a URI with a nonce according to a JWE encoding algorithm
     *
     * @return JWE encoded string, comprised of the return URI and a nonce
     */
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
            @SuppressWarnings("SpellCheckingInspection") final DirectEncrypter encrypter = new DirectEncrypter(
                    identityProvider.getRequestKey());
            jweObject.encrypt(encrypter);
        } catch (final JOSEException e) {
            logger.error("Could not encode OAuth state", e);
            return null;
        }
        return jweObject.serialize();
    }

    /**
     * Given a state encapsulating a JWE token, decrypt and decode it into a {@link Payload}
     * @param state as returned to a sign in controller
     * @return a {@link Payload} or {@code null} if the payload could not be found or decrypted.
     */
    public Payload oauth2DecodeState(final String state) {
        Payload payload;
        try {
            final JWEObject jweObject = JWEObject.parse(state);

            final byte[] key = identityProvider.getRequestKey();
            jweObject.decrypt(new DirectDecrypter(key));
            payload = jweObject.getPayload();
        } catch (final Exception e) {
            logger.error("Could not decode OAuth state", e);
            payload = null;
        }
        return payload;
    }
}