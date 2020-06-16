package uk.gov.ch.oauth.tokens;

import java.util.Map;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.AccessToken;

public class SessionSignInModifier {

    @SuppressWarnings("unchecked")
    // This is necessary as the original data on the session is untyped, but expected to be of the correct types
    static Map<String, Object> updateSignIn(final Object sInf, final Object sio) {
        final Map<String, Object> original = (Map<String, Object>) sInf;
        final Map<String, Object> extras = (Map<String, Object>) sio;
        original.putAll(extras);
        return original;
    }

    /**
     * Accepts a User session and updates its sign in info with the provided Access Token and User
     * Profile.
     *
     * @param accessToken extended implementation of {@link AccessToken}
     * @param userProfileResponse extended implementation of {@link uk.gov.companieshouse.session.model.UserProfile}
     */
    public void alterSessionData(final Session session, final OAuthToken accessToken,
            final UserProfileResponse userProfileResponse) {
        final SignInInfoMap signInInfo = new SignInInfoMap();
        signInInfo.setUserProfile(userProfileResponse);
        signInInfo.setAccessToken(accessToken);
        signInInfo.setSignedIn(true);

        alterSessionData(session.getData(), signInInfo.toMap());
    }

    /**
     * Functional method to alter sessions underlying data map with the new signin info.
     *
     * @param currentSessionData the sessions current data map.
     * @param desiredSignInInfo which will be added, to the session data, overwriting anything that
     * conflicts.
     */
    void alterSessionData(final Map<String, Object> currentSessionData,
            final Map<String, Object> desiredSignInInfo) {

        final String signInInfoKey = SessionKeys.SIGN_IN_INFO.getKey();

        currentSessionData
                .merge(signInInfoKey, desiredSignInInfo, SessionSignInModifier::updateSignIn);
    }
}
