package uk.gov.ch.oauth.tokens;

import static org.apache.naming.ResourceRef.SCOPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.session.SessionKeys;

class UserProfileResponseTest {

    public static final String EMAIL = "EMAIL";
    public static final String FORENAME = "FORENAME";
    public static final String ID = "ID";
    public static final String LOCALE = "LOCALE";
    public static final String SURNAME = "SURNAME";
    private static final Map<String, Object> USER_PROFILE = new HashMap<>();
    private static final Map<String, Boolean> PERMISSIONS = new HashMap<>();
    private UserProfileResponse userProfileResponse;

    @BeforeEach
    void setup() {
        userProfileResponse = new UserProfileResponse();
        userProfileResponse.addUserProfileToMap(USER_PROFILE);
        userProfileResponse.setEmail(EMAIL);
        userProfileResponse.setForename(FORENAME);
        userProfileResponse.setId(ID);
        userProfileResponse.setLocale(LOCALE);
        userProfileResponse.setPermissions(PERMISSIONS);
        userProfileResponse.setScope(SCOPE);
        userProfileResponse.setSurname(SURNAME);
    }

    @Test
    void addUserProfileToMap() {
        Map<String, Object> signInData = new HashMap<>();

        userProfileResponse.addUserProfileToMap(signInData);

        Map<String, Object> data = (Map<String, Object>) signInData
                .get(SessionKeys.USER_PROFILE.getKey());
        assertNotNull(data);

        assertEquals(EMAIL, data.get(SessionKeys.EMAIL.getKey()));
        assertEquals(FORENAME, data.get(SessionKeys.FORENAME.getKey()));
        assertEquals(ID, data.get(SessionKeys.USER_ID.getKey()));
        assertEquals(LOCALE, data.get(SessionKeys.LOCALE.getKey()));
        assertEquals(PERMISSIONS, data.get(SessionKeys.PERMISSIONS.getKey()));
        assertEquals(SCOPE, data.get(SessionKeys.SCOPE.getKey()));
        assertEquals(SURNAME, data.get(SessionKeys.SURNAME.getKey()));
    }
}