package uk.gov.ch.oauth.tokens;

import java.util.HashMap;
import java.util.Map;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.SignInInfo;

public class SignInInfoMap extends SignInInfo {

    public Map<String, Object> toMap() {
        HashMap<String, Object> data = new HashMap<>();
        data.put(SessionKeys.SIGNED_IN.getKey(), isSignedIn() ? 1 : 0);
        data.put(SessionKeys.ACCESS_TOKEN.getKey(),
                ((OAuthToken) getAccessToken()).getAccessTokenAsMap());
        data.put(SessionKeys.USER_PROFILE.getKey(),
                ((UserProfileResponse) getUserProfile()).getUserProfileAsMap());
        return data;
    }


}
