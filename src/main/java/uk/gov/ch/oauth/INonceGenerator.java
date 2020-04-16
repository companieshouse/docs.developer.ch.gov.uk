package uk.gov.ch.oauth;

import uk.gov.companieshouse.session.Session;

public interface INonceGenerator {

    /**
     * Generates a nonce, adds it to the session and then returns the nonce.
     *
     * @param session takes a session and sets the unique key in the data. If the session is null,
     * generates a new one.
     * @return Base64 encoded unique key
     */
    String setNonceForSession(Session session);
}
