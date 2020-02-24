package uk.gov.ch.oauth;

import java.util.Base64;
import java.util.Base64.Decoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class IdentityProvider implements IIdentityProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");
    private final static Decoder decoder = Base64.getDecoder();
    final private byte[] requestKey;
    final private String authorizationUri;
    final private String clientId;
    final private String redirectUri;
    final private String clientSecret;

    @Autowired
    public IdentityProvider(final EnvironmentReader reader) {
        requestKey = decoder.decode(reader.getMandatoryString("DEVELOPER_OAUTH2_REQUEST_KEY"));
        this.authorizationUri = reader.getMandatoryString("OAUTH2_AUTH_URI");
        this.clientId = reader.getMandatoryString("CHS_DEVELOPER_CLIENT_ID");
        redirectUri = reader.getMandatoryString("OAUTH2_REDIRECT_URI");
        clientSecret = reader.getMandatoryString("CHS_DEVELOPER_CLIENT_SECRET");
       // TODO is this better? redirectUri = reader.getMandatoryString("REDIRECT_URI");
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
    public String getAuthorisationUrl(final String state) {

        final String url = getAuthorizationUri()
                + "?"
                + "client_id="
                + getClientId()
                + "&redirect_uri="
                + getRedirectUri()
                + "&response_type=code"
                + "&state="
                + state;
        LOGGER.trace(url);
        return url;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

}
