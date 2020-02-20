package uk.gov.ch.developer.docs.oauth;

import com.nimbusds.jose.Payload;

public interface IOauth {

    String oauth2EncodeState(final String returnUri,
            final String nonce,
            final String attributeName);

    Payload oauth2DecodeState(final String state);

    boolean oauth2VerifyNonce(final String Nonce);
}
