package uk.gov.ch.oauth.tokens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.AccessToken;
import uk.gov.companieshouse.session.model.UserProfile;

class SignInInfoMapTest {

    public static final String EMAIL = "EMAIL";
    public static final String FORENAME = "FORENAME";
    public static final String ID = "ID";
    public static final String LOCALE = "LOCALE";
    public static final String SURNAME = "SURNAME";
    public static final int EXPIRES_IN = 1;
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String TOKEN = "TOKEN";
    public static final String TOKEN_TYPE = "TOKEN_TYPE";
    private static final Map<String, Boolean> PERMISSIONS = new HashMap<>();
    private static final String SCOPE = "SCOPE";
    private UserProfileResponse userProfileResponse;
    private OAuthToken authToken;

    @BeforeEach
    void setup() {
        userProfileResponse = new UserProfileResponse();
        userProfileResponse.setEmail(EMAIL);
        userProfileResponse.setForename(FORENAME);
        userProfileResponse.setId(ID);
        userProfileResponse.setLocale(LOCALE);
        userProfileResponse.setPermissions(PERMISSIONS);
        userProfileResponse.setScope(SCOPE);
        userProfileResponse.setSurname(SURNAME);
        authToken = new OAuthToken();

        authToken.setExpiresIn(EXPIRES_IN);
        authToken.setRefreshToken(REFRESH_TOKEN);
        authToken.setToken(TOKEN);
        authToken.setTokenType(TOKEN_TYPE);
    }

    @Test
    void mapIsConstructedWithKeysAndValues() {
        SignInInfoMap signInInfoMap = new SignInInfoMap();
        signInInfoMap.setSignedIn(true);
        signInInfoMap.setUserProfile(userProfileResponse);
        signInInfoMap.setAccessToken(authToken);

        Map<String, Object> map = signInInfoMap.toMap();

        assertMapIsSignedIn(map);
        assertMapHasUserDetails(userProfileResponse, map);
        assertMapHasAccessToken(authToken, map);
    }


    private void assertMapIsSignedIn(Map<String, Object> map) {
        assertEquals(1, map.get(SessionKeys.SIGNED_IN.getKey()));
    }

    @SuppressWarnings("unchecked")
    private void assertMapHasUserDetails(UserProfileResponse userProfileResponse,
            Map<String, Object> map) {
        map = (Map<String, Object>) map.get(SessionKeys.USER_PROFILE.getKey());

        assertEquals(userProfileResponse.getEmail(),
                map.get(SessionKeys.EMAIL.getKey()));
        assertEquals(userProfileResponse.getSurname(),
                map.get(SessionKeys.SURNAME.getKey()));
        assertEquals(userProfileResponse.getForename(),
                map.get(SessionKeys.FORENAME.getKey()));
        assertEquals(userProfileResponse.getId(),
                map.get(SessionKeys.USER_ID.getKey()));
        assertEquals(userProfileResponse.getLocale(),
                map.get(SessionKeys.LOCALE.getKey()));
        assertEquals(userProfileResponse.getScope(),
                map.get(SessionKeys.SCOPE.getKey()));
        assertEquals(userProfileResponse.getPermissions(),
                map.get(SessionKeys.PERMISSIONS.getKey()));
    }

    @SuppressWarnings("unchecked")
    private void assertMapHasAccessToken(OAuthToken token, Map<String, Object> map) {
        map = (Map<String, Object>) map.get(SessionKeys.ACCESS_TOKEN.getKey());
        assertEquals(token.getRefreshToken(),
                map.get(SessionKeys.REFRESH_TOKEN.getKey()));
        assertEquals(token.getExpiresIn(),
                map.get(SessionKeys.EXPIRES_IN.getKey()));
        assertEquals(token.getToken(),
                map.get(SessionKeys.ACCESS_TOKEN.getKey()));
        assertEquals(token.getTokenType(),
                map.get(SessionKeys.TOKEN_TYPE.getKey()));
    }

    @Test
    void setAccessTokenThrowsIfNonOAuthTokenIsGiven() {
        SignInInfoMap signInInfoMap = new SignInInfoMap();
        assertThrows(IllegalArgumentException.class,
                () -> signInInfoMap.setAccessToken(new AccessToken()));
    }

    @Test
    void setUserProfileThrowsIfNonUserProfileResponseIsGiven() {
        SignInInfoMap signInInfoMap = new SignInInfoMap();
        assertThrows(IllegalArgumentException.class,
                () -> signInInfoMap.setUserProfile(new UserProfile()));
    }
}