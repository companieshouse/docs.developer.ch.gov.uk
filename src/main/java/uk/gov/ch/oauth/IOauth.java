package uk.gov.ch.oauth;

import com.nimbusds.jose.Payload;
import uk.gov.ch.oauth.tokens.OAuthToken;
import uk.gov.ch.oauth.tokens.UserProfileResponse;
import uk.gov.companieshouse.session.Session;

public interface IOauth {

    String oauth2EncodeState(final String returnUri,
            final String nonce,
            final String attributeName);

    String oauth2EncodeState(final String returnUri,
            final Session sessions,
            final String attributeName);

    Payload oauth2DecodeState(final String state);

    /**
     * Verify if a supplied Nonce corresponds with a session or internal value
     *
     * @param Nonce Supplied value
     * @return true if Nonces match and are not both null
     */
    boolean oauth2VerifyNonce(final String Nonce);

    UserProfileResponse getUserProfile(Session chSession, OAuthToken oauthTokenResponse);

    OAuthToken getOAuthToken(String code);
}
