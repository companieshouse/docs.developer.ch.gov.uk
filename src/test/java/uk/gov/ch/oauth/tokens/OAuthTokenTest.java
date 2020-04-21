package uk.gov.ch.oauth.tokens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.session.SessionKeys;

class OAuthTokenTest {

    public static final int EXPIRES_IN = 1;
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String TOKEN = "TOKEN";
    public static final String TOKEN_TYPE = "TOKEN_TYPE";

    @Test
    void saveAccessToken() {
        OAuthToken token = new OAuthToken();

        token.setExpiresIn(EXPIRES_IN);
        token.setRefreshToken(REFRESH_TOKEN);
        token.setToken(TOKEN);
        token.setTokenType(TOKEN_TYPE);

        Map<String, Object> map = token.saveAccessToken();

        Map<String, Object> data = (Map<String, Object>) map.get(SessionKeys.ACCESS_TOKEN.getKey());
        assertNotNull(data);

        assertEquals(EXPIRES_IN, data.get(SessionKeys.EXPIRES_IN.getKey()));
        assertEquals(REFRESH_TOKEN, data.get(SessionKeys.REFRESH_TOKEN.getKey()));
        assertEquals(TOKEN, data.get(SessionKeys.ACCESS_TOKEN.getKey()));
        assertEquals(TOKEN_TYPE, data.get(SessionKeys.TOKEN_TYPE.getKey()));
    }
}