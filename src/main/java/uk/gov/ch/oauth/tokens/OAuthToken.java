package uk.gov.ch.oauth.tokens;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.AccessToken;

/**
 * Extension of {@link uk.gov.companieshouse.session.model.AccessToken} to allow addition of {@link
 * JsonProperty} annotations to enable parsing of {@link uk.gov.companieshouse.session.model.AccessToken}
 * data from the authentication server. Most methods delegate directly to the superclass. To provide
 * resilience unused or unknown properties are marked as ignored for default JSON parsing.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthToken extends AccessToken {

    @JsonProperty("expires_in")
    @Override
    public void setExpiresIn(int expiresIn) {
        super.setExpiresIn(expiresIn);
    }

    @JsonProperty("token_type")
    @Override
    public void setTokenType(String tokenType) {
        super.setTokenType(tokenType);
    }

    @JsonProperty("refresh_token")
    @Override
    public void setRefreshToken(String refreshToken) {
        super.setRefreshToken(refreshToken);
    }

    @JsonProperty("access_token")
    @Override
    public void setToken(String token) {
        super.setToken(token);
    }

    /**
     * Convert properties into the format that can be processed by the {@link
     * uk.gov.companieshouse.session.Session} for access token sign in information.
     *
     * @return members mapped to appropriate {@link SessionKeys}
     */
    public Map<String, Object> saveAccessToken() {

        Map<String, Object> accessTokenData = new HashMap<>();

        accessTokenData.put(SessionKeys.ACCESS_TOKEN.getKey(), getToken());
        accessTokenData.put(SessionKeys.EXPIRES_IN.getKey(), getExpiresIn());
        accessTokenData.put(SessionKeys.REFRESH_TOKEN.getKey(), getRefreshToken());
        accessTokenData.put(SessionKeys.TOKEN_TYPE.getKey(), getTokenType());

        Map<String, Object> signInData = new HashMap<>();
        signInData.put(SessionKeys.ACCESS_TOKEN.getKey(), accessTokenData);

        return signInData;
    }
}
