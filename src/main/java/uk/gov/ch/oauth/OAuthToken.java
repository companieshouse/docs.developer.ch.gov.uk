package uk.gov.ch.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

}
