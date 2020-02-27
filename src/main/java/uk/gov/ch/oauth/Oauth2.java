package uk.gov.ch.oauth;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ch.developer.docs.session.SessionService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.SessionKeys;

@Component
public class Oauth2 implements IOauth {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");
    final IIdentityProvider identityProvider;
    @Autowired
    private SessionService sessionService;

    @Autowired
    public Oauth2(final IIdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
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

    public boolean oauth2VerifyNonce(final String nonce) {
        boolean retval = false;
        if (nonce != null) {
            retval = nonce.equals(getSessionNonce());
        }
        return retval;
    }

    /**
     * Extract the OAuth2 Nonce from the current session
     *
     * @return The Nonce String from within the session or null if not found.
     */
    private String getSessionNonce() {
        String oauth2Nonce = null;
        try {
            final Map<String, Object> data = sessionService.getSessionDataFromContext();
            oauth2Nonce = (String) data.getOrDefault(SessionKeys.NONCE, null);
        } catch (final Exception e) {
            LOGGER.error("Unable to extract OAuth2 Nonce from session", e);
        }
        return oauth2Nonce;
    }
}
