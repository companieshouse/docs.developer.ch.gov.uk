package uk.gov.ch.oauth.tokens;

import java.util.HashMap;
import java.util.Map;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.AccessToken;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

/**
 * {@inheritDoc} Implementation of SignInInfo used to create map structure matching session map.
 * Requires OAuthToken and UserProfileResponse implementations of AccessToken and UserProfile in
 * order to access additional methods.
 */
public class SignInInfoMap extends SignInInfo {

    /**
     * {@inheritDoc} Sets access token. Overwritten in order to check argument typing.
     *
     * @throws IllegalArgumentException if input is not {@link OAuthToken}
     */
    @Override
    public void setAccessToken(AccessToken accessToken) {
        if (!(accessToken instanceof OAuthToken)) {
            throw new IllegalArgumentException("Can only accept instance of OAuthToken.");
        }
        super.setAccessToken(accessToken);
    }

    /**
     * {@inheritDoc} Sets user profile. Overwritten in order to check argument typing.
     *
     * @throws IllegalArgumentException if input is not {@link UserProfileResponse}
     */
    @Override
    public void setUserProfile(UserProfile userProfile) {
        if (!(userProfile instanceof UserProfileResponse)) {
            throw new IllegalArgumentException("Can only accept instance of UserProfileResponse.");
        }
        super.setUserProfile(userProfile);
    }

    /**
     * Convert properties into the map format that can be processed by the {@link
     * uk.gov.companieshouse.session.Session} for access token sign in information.
     *
     * @return this as a map representation that matches session data map structure.
     */
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