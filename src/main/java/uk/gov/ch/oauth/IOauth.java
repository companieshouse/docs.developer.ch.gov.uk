package uk.gov.ch.oauth;

import com.nimbusds.jose.Payload;
import uk.gov.companieshouse.session.Session;

public interface IOauth {

    String oauth2EncodeState(final String returnUri,
            final String nonce,
            final String attributeName);

    String encodeSignInState(final String returnUri,
            final Session sessions,
            final String attributeName);

    Payload oauth2DecodeState(final String state);

//    /**
//     * Verify if a supplied Nonce corresponds with a session or internal value
//     *
//     * @param Nonce Supplied value
//     * @return true if Nonces match and are not both null
//     */
//    boolean oauth2VerifyNonce(final String Nonce);

    //  UserProfileResponse fetchUserProfile(String code);

    boolean isValid(String state, String code);
}
