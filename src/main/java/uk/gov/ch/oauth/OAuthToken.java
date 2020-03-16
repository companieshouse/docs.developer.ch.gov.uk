package uk.gov.ch.oauth;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.AccessToken;

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
    
    public Map<String, Object> setAccessToken() {

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
