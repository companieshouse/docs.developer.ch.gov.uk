package uk.gov.ch.oauth.tokens;

import java.util.Map;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.AccessToken;
import uk.gov.companieshouse.session.model.UserProfile;

public class SessionSignInModifier {

    @SuppressWarnings("unchecked")
    // This is necessary as the original data on the session is untyped, but expected to be of the correct types
    private static Map<String, Object> updateSignIn(final Object sInf, final Object sio) {
        final Map<String, Object> original = (Map<String, Object>) sInf;
        final Map<String, Object> extras = (Map<String, Object>) sio;
        original.putAll(extras);
        return original;
    }

    public void alterSessionData(Session session, AccessToken accessToken,
            UserProfile userProfileResponse) {
        SignInInfoMap signInInfo = new SignInInfoMap();
        signInInfo.setUserProfile(userProfileResponse);
        signInInfo.setAccessToken(accessToken);
        signInInfo.setSignedIn(true);

        alterSessionData(session.getData(), signInInfo.toMap());
    }

    public void alterSessionData(Map<String, Object> currentSessionData,
            Map<String, Object> desiredSignInInfo) {

        final String signInInfoKey = SessionKeys.SIGN_IN_INFO.getKey();

        currentSessionData
                .merge(signInInfoKey, desiredSignInInfo, SessionSignInModifier::updateSignIn);
    }
}
