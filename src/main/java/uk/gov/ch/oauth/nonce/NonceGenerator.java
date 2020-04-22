package uk.gov.ch.oauth.nonce;

import java.security.SecureRandom;
import java.util.Random;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;

@Component
public class NonceGenerator implements INonceGenerator {

    private final Random random = new SecureRandom();

    @Autowired
    SessionFactory sessionFactory;

    /**
     * Generates a nonce, adds it to the session and then returns the nonce.
     *
     * @param session takes a session and sets the unique key in the data. If the session is null,
     * generates a new one.
     * @return Base64 encoded unique key
     */
    @Override
    public String setNonceForSession(Session session) {
        // Generate and store a nonce in the session
        if (session == null) {
            session = sessionFactory.createSession();
        }
        final String nonce = generateNonce();
        session.getData().put(SessionKeys.NONCE.getKey(), nonce);
        return nonce;
    }

    /**
     * Generates a secure unique key
     *
     * @return Base64 encoded unique key
     */
    String generateNonce() {
        final byte[] bytes = new byte[5];
        random.nextBytes(bytes);
        return Base64.encodeBase64URLSafeString(bytes);
    }
}