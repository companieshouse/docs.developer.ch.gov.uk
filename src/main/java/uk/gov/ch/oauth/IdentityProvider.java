package uk.gov.ch.oauth;

import java.util.Base64;
import java.util.Base64.Decoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public class IdentityProvider implements IIdentityProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");
    private static final Decoder decoder = Base64.getDecoder();
    private final byte[] requestKey;
    private final String authorizationUri;
    private final String clientId;
    private final String redirectUri;
    private final String clientSecret;
    private final String accountLocalUrl;
    private final String tokenUrl;
    private final String profileUrl;
    private final String redirectUriPage;
    private final String grantType;

    @Value("${home.url}")
    private String homeUrl;

    @Autowired
    public IdentityProvider(final EnvironmentReader reader) {
        requestKey = decoder.decode(reader.getMandatoryString("DEVELOPER_OAUTH2_REQUEST_KEY"));
        authorizationUri = reader.getMandatoryString("OAUTH2_AUTH_URI");
        clientId = reader.getMandatoryString("CHS_DEVELOPER_CLIENT_ID");
        clientSecret = reader.getMandatoryString("CHS_DEVELOPER_CLIENT_SECRET");
        redirectUri = reader.getMandatoryString("OAUTH2_REDIRECT_URI");
        redirectUriPage = reader.getMandatoryString("REDIRECT_URI");
        accountLocalUrl = reader.getMandatoryString("ACCOUNT_LOCAL_URL");
        tokenUrl = accountLocalUrl + "/oauth2/token";
        profileUrl = accountLocalUrl + "/user/profile";
        grantType = "authorization_code";
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public byte[] getRequestKey() {
        return requestKey;
    }

    @Override
    public String getRedirectUri() {
        return redirectUri;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    /**
     * Auth URL with scope added
     */
    public String getAuthorisationUrl(final String state, final String scope) {
        StringBuilder sb = new StringBuilder(getAuthorisationUrl(state));
        if (scope != null) {
            sb.append("&scope=");
            sb.append(scope);
        }
        return sb.toString();
    }


    @Override
    public String getTokenUrl() {
        return tokenUrl;
    }

    @Override
    public String getProfileUrl() {
        return profileUrl;
    }

    @Override
    public String getHomeUrl() {
        return homeUrl;
    }

    @Override
    public String getAuthorisationUrl(final String state) {

        final String url = getAuthorizationUri() + "?" + "client_id=" + getClientId()
                + "&redirect_uri=" + getRedirectUri() + "&response_type=code" + "&state=" + state;
        LOGGER.trace(url);
        return url;
    }

    @Override
    public String getPostRequestBody(String code) {
        final StringBuilder sb = new StringBuilder();
        sb.append("code=").append(code).append("&client_id=").append(getClientId())
                .append("&client_secret=").append(getClientSecret()).append("&redirect_uri=")
                .append(getRedirectUri()).append("&grant_type=").append(getGrantType());

        return sb.toString();
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    @Override
    public String getRedirectUriPage() {
        return redirectUriPage;
    }

    @Override
    public String getAccountLocalUrl() {
        return accountLocalUrl;
    }
    
    @Override
    public String getGrantType() {
        return grantType;
    }
}
